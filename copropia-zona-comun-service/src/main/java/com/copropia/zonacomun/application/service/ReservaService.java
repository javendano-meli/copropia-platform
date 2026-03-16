package com.copropia.zonacomun.application.service;

import com.copropia.common.enums.EstadoReserva;
import com.copropia.common.enums.TipoReserva;
import com.copropia.common.exception.BusinessException;
import com.copropia.common.exception.ResourceNotFoundException;
import com.copropia.zonacomun.domain.model.Reserva;
import com.copropia.zonacomun.domain.model.ZonaComun;
import com.copropia.zonacomun.domain.port.in.ReservaUseCase;
import com.copropia.zonacomun.domain.port.out.ReservaRepository;
import com.copropia.zonacomun.domain.port.out.ZonaComunRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservaService implements ReservaUseCase {

    private final ReservaRepository reservaRepository;
    private final ZonaComunRepository zonaComunRepository;

    @Override
    @Transactional
    public Reserva create(Reserva reserva) {
        log.info("Solicitud de reserva - zona: {}, usuario: {}, fecha: {}", reserva.getZonaComunId(), reserva.getUsuarioId(), reserva.getFecha());

        ZonaComun zona = zonaComunRepository.findById(reserva.getZonaComunId())
                .orElseThrow(() -> new ResourceNotFoundException("ZonaComun", reserva.getZonaComunId()));

        if (!zona.isActiva()) {
            log.warn("Intento de reserva en zona inactiva id: {}", zona.getId());
            throw new BusinessException("La zona comun no esta activa");
        }

        if (reserva.getFecha().isBefore(LocalDate.now())) {
            log.warn("Intento de reserva en fecha pasada: {}", reserva.getFecha());
            throw new BusinessException("No se puede reservar en una fecha pasada");
        }

        if (zona.getTipoReserva() == TipoReserva.DIA_COMPLETO) {
            reserva.setHoraInicio(LocalTime.of(0, 0));
            reserva.setHoraFin(LocalTime.of(23, 59));
        } else {
            validateHorarios(reserva, zona);
        }

        if (reservaRepository.existsOverlap(reserva.getZonaComunId(), reserva.getFecha(), reserva.getHoraInicio(), reserva.getHoraFin())) {
            log.warn("Conflicto de horario - zona: {}, fecha: {}, hora: {}-{}", reserva.getZonaComunId(), reserva.getFecha(), reserva.getHoraInicio(), reserva.getHoraFin());
            throw new BusinessException("Ya existe una reserva en ese horario para esta zona comun");
        }

        reserva.setEstado(zona.isRequiereAprobacion() ? EstadoReserva.PENDIENTE : EstadoReserva.CONFIRMADA);
        reserva.setCreatedAt(LocalDateTime.now());

        Reserva saved = reservaRepository.save(reserva);
        log.info("Reserva creada id: {} - estado: {}", saved.getId(), saved.getEstado());
        return saved;
    }

    @Override
    public Reserva getById(Long id) {
        log.debug("Buscando reserva id: {}", id);
        return reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", id));
    }

    @Override
    public List<Reserva> getByZonaComunIdAndFecha(Long zonaComunId, LocalDate fecha) {
        log.debug("Consultando reservas - zona: {}, fecha: {}", zonaComunId, fecha);
        return reservaRepository.findByZonaComunIdAndFecha(zonaComunId, fecha);
    }

    @Override
    public List<Reserva> getByUsuarioId(Long usuarioId) {
        log.debug("Consultando reservas del usuario: {}", usuarioId);
        return reservaRepository.findByUsuarioId(usuarioId);
    }

    @Override
    @Transactional
    public Reserva confirm(Long id) {
        log.info("Confirmando reserva id: {}", id);
        Reserva reserva = getById(id);
        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            log.warn("Intento de confirmar reserva {} en estado {}", id, reserva.getEstado());
            throw new BusinessException("Solo se pueden confirmar reservas en estado PENDIENTE");
        }
        reserva.setEstado(EstadoReserva.CONFIRMADA);
        return reservaRepository.save(reserva);
    }

    @Override
    @Transactional
    public Reserva cancel(Long id, Long usuarioId) {
        log.info("Cancelando reserva id: {} por usuario: {}", id, usuarioId);
        Reserva reserva = getById(id);

        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new BusinessException("La reserva ya esta cancelada");
        }
        if (reserva.getEstado() == EstadoReserva.COMPLETADA) {
            throw new BusinessException("No se puede cancelar una reserva completada");
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        log.info("Reserva {} cancelada exitosamente", id);
        return reservaRepository.save(reserva);
    }

    private void validateHorarios(Reserva reserva, ZonaComun zona) {
        if (reserva.getHoraInicio() == null || reserva.getHoraFin() == null) {
            throw new BusinessException("Debe indicar hora de inicio y fin para reservas por horas");
        }
        if (!reserva.getHoraInicio().isBefore(reserva.getHoraFin())) {
            throw new BusinessException("La hora de inicio debe ser anterior a la hora de fin");
        }
        if (reserva.getHoraInicio().isBefore(zona.getHoraApertura())) {
            throw new BusinessException("La hora de inicio no puede ser antes de la apertura (" + zona.getHoraApertura() + ")");
        }
        if (reserva.getHoraFin().isAfter(zona.getHoraCierre())) {
            throw new BusinessException("La hora de fin no puede ser despues del cierre (" + zona.getHoraCierre() + ")");
        }

        long horas = ChronoUnit.HOURS.between(reserva.getHoraInicio(), reserva.getHoraFin());
        if (horas > zona.getMaxHorasReserva()) {
            log.warn("Reserva excede maximo de horas - solicitadas: {}, maximo: {}", horas, zona.getMaxHorasReserva());
            throw new BusinessException("La reserva excede el maximo de " + zona.getMaxHorasReserva() + " horas permitidas. Solicitadas: " + horas);
        }
    }
}

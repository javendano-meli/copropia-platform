package com.copropia.zonacomun.infrastructure.adapter.out.persistence;

import com.copropia.zonacomun.domain.model.Reserva;
import com.copropia.zonacomun.domain.port.out.ReservaRepository;
import com.copropia.zonacomun.infrastructure.adapter.out.persistence.jpa.ReservaJpaRepository;
import com.copropia.zonacomun.infrastructure.adapter.out.persistence.mapper.ReservaPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReservaPersistenceAdapter implements ReservaRepository {
    private final ReservaJpaRepository jpaRepository;
    private final ReservaPersistenceMapper mapper;

    @Override
    public Reserva save(Reserva reserva) {
        log.debug("Guardando reserva - zona: {}, fecha: {}", reserva.getZonaComunId(), reserva.getFecha());
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(reserva)));
    }
    @Override
    public Optional<Reserva> findById(Long id) {
        log.debug("Buscando reserva id: {}", id);
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
    @Override
    public List<Reserva> findByZonaComunIdAndFecha(Long zonaComunId, LocalDate fecha) {
        log.debug("Buscando reservas - zona: {}, fecha: {}", zonaComunId, fecha);
        return jpaRepository.findByZonaComunIdAndFechaOrderByHoraInicioAsc(zonaComunId, fecha).stream().map(mapper::toDomain).toList();
    }
    @Override
    public List<Reserva> findByUsuarioId(Long usuarioId) {
        log.debug("Buscando reservas del usuario: {}", usuarioId);
        return jpaRepository.findByUsuarioIdOrderByFechaDesc(usuarioId).stream().map(mapper::toDomain).toList();
    }
    @Override
    public boolean existsOverlap(Long zonaComunId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        log.debug("Verificando solapamiento - zona: {}, fecha: {}, {}-{}", zonaComunId, fecha, horaInicio, horaFin);
        return jpaRepository.existsOverlap(zonaComunId, fecha, horaInicio, horaFin);
    }
}

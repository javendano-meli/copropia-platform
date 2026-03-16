package com.copropia.zonacomun.application.service;

import com.copropia.common.enums.TipoReserva;
import com.copropia.common.exception.BusinessException;
import com.copropia.common.exception.ResourceNotFoundException;
import com.copropia.zonacomun.domain.model.ZonaComun;
import com.copropia.zonacomun.domain.port.in.ZonaComunUseCase;
import com.copropia.zonacomun.domain.port.out.ZonaComunRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZonaComunService implements ZonaComunUseCase {

    private final ZonaComunRepository zonaComunRepository;

    @Override
    @Transactional
    public ZonaComun create(ZonaComun zonaComun) {
        log.info("Creando zona comun '{}' para copropiedad {}", zonaComun.getNombre(), zonaComun.getCopropiedadId());

        if (zonaComunRepository.existsByCopropiedadIdAndNombre(zonaComun.getCopropiedadId(), zonaComun.getNombre())) {
            log.warn("Zona comun duplicada: '{}' en copropiedad {}", zonaComun.getNombre(), zonaComun.getCopropiedadId());
            throw new BusinessException("Ya existe una zona comun con nombre '" + zonaComun.getNombre() + "' en esta copropiedad");
        }

        if (zonaComun.getTipoReserva() == TipoReserva.POR_HORAS) {
            if (zonaComun.getMaxHorasReserva() == null || zonaComun.getMaxHorasReserva() <= 0) {
                throw new BusinessException("Para reservas por horas, debe indicar el maximo de horas permitido");
            }
            if (zonaComun.getHoraApertura() == null || zonaComun.getHoraCierre() == null) {
                throw new BusinessException("Para reservas por horas, debe indicar horario de apertura y cierre");
            }
            if (!zonaComun.getHoraApertura().isBefore(zonaComun.getHoraCierre())) {
                throw new BusinessException("La hora de apertura debe ser anterior a la hora de cierre");
            }
        }

        zonaComun.setActiva(true);
        zonaComun.setCreatedAt(LocalDateTime.now());

        ZonaComun saved = zonaComunRepository.save(zonaComun);
        log.info("Zona comun creada exitosamente id: {}", saved.getId());
        return saved;
    }

    @Override
    public ZonaComun getById(Long id) {
        log.debug("Buscando zona comun id: {}", id);
        return zonaComunRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ZonaComun", id));
    }

    @Override
    public List<ZonaComun> getByCopropiedadId(Long copropiedadId) {
        log.debug("Consultando zonas comunes de copropiedad {}", copropiedadId);
        List<ZonaComun> zonas = zonaComunRepository.findByCopropiedadId(copropiedadId);
        log.debug("Encontradas {} zonas comunes para copropiedad {}", zonas.size(), copropiedadId);
        return zonas;
    }

    @Override
    @Transactional
    public ZonaComun update(Long id, ZonaComun updated) {
        log.info("Actualizando zona comun id: {}", id);
        ZonaComun existing = getById(id);
        existing.setNombre(updated.getNombre());
        existing.setDescripcion(updated.getDescripcion());
        existing.setCapacidad(updated.getCapacidad());
        existing.setTipoReserva(updated.getTipoReserva());
        existing.setMaxHorasReserva(updated.getMaxHorasReserva());
        existing.setHoraApertura(updated.getHoraApertura());
        existing.setHoraCierre(updated.getHoraCierre());
        existing.setRequiereAprobacion(updated.isRequiereAprobacion());
        return zonaComunRepository.save(existing);
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        log.warn("Desactivando zona comun id: {}", id);
        ZonaComun zonaComun = getById(id);
        zonaComun.setActiva(false);
        zonaComunRepository.save(zonaComun);
    }
}

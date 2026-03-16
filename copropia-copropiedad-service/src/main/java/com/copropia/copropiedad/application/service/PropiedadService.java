package com.copropia.copropiedad.application.service;

import com.copropia.copropiedad.domain.model.Propiedad;
import com.copropia.copropiedad.domain.port.in.PropiedadUseCase;
import com.copropia.copropiedad.domain.port.out.PropiedadRepository;
import com.copropia.common.exception.BusinessException;
import com.copropia.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PropiedadService implements PropiedadUseCase {

    private final PropiedadRepository propiedadRepository;

    @Override
    @Transactional
    public Propiedad create(Propiedad propiedad) {
        log.info("Registrando propiedad {} en copropiedad {}", propiedad.getIdentificacion(), propiedad.getCopropiedadId());
        if (propiedadRepository.existsByCopropiedadIdAndIdentificacion(
                propiedad.getCopropiedadId(), propiedad.getIdentificacion())) {
            log.warn("Intento de registrar propiedad duplicada: {} en copropiedad {}", propiedad.getIdentificacion(), propiedad.getCopropiedadId());
            throw new BusinessException("Ya existe la propiedad " + propiedad.getIdentificacion() + " en esta copropiedad");
        }
        propiedad.setEstado("activa");
        propiedad.setCreatedAt(LocalDateTime.now());
        Propiedad saved = propiedadRepository.save(propiedad);
        log.info("Propiedad registrada exitosamente con id: {}", saved.getId());
        return saved;
    }

    @Override
    public Propiedad getById(Long id) {
        log.debug("Buscando propiedad con id: {}", id);
        return propiedadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Propiedad", id));
    }

    @Override
    public List<Propiedad> getByCopropiedadId(Long copropiedadId) {
        log.debug("Buscando propiedades de copropiedad id: {}", copropiedadId);
        List<Propiedad> result = propiedadRepository.findByCopropiedadId(copropiedadId);
        log.debug("Propiedades encontradas para copropiedad {}: {}", copropiedadId, result.size());
        return result;
    }

    @Override
    public List<Propiedad> getByPropietarioId(Long propietarioId) {
        log.debug("Buscando propiedades del propietario id: {}", propietarioId);
        return propiedadRepository.findByPropietarioId(propietarioId);
    }

    @Override
    @Transactional
    public Propiedad update(Long id, Propiedad updated) {
        log.info("Actualizando propiedad id: {}", id);
        Propiedad existing = getById(id);
        existing.setPropietarioId(updated.getPropietarioId());
        existing.setTipo(updated.getTipo());
        existing.setMetrosCuadrados(updated.getMetrosCuadrados());
        existing.setCoeficiente(updated.getCoeficiente());
        return propiedadRepository.save(existing);
    }

    @Override
    public BigDecimal getTotalCoeficiente(Long copropiedadId) {
        BigDecimal total = propiedadRepository.sumCoeficienteByCopropiedadId(copropiedadId);
        log.debug("Coeficiente total copropiedad {}: {}", copropiedadId, total);
        return total;
    }

    @Override
    public void validateCoeficientes(Long copropiedadId) {
        BigDecimal total = getTotalCoeficiente(copropiedadId);
        if (total != null && total.compareTo(new BigDecimal("100")) > 0) {
            log.warn("Coeficientes exceden 100% en copropiedad {}: {}%", copropiedadId, total);
            throw new BusinessException("Los coeficientes superan el 100%. Total actual: " + total + "%");
        }
        log.debug("Coeficientes validados para copropiedad {}: {}%", copropiedadId, total);
    }
}

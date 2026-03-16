package com.copropia.copropiedad.application.service;

import com.copropia.copropiedad.domain.model.Propiedad;
import com.copropia.copropiedad.domain.port.in.PropiedadUseCase;
import com.copropia.copropiedad.domain.port.out.PropiedadRepository;
import com.copropia.common.exception.BusinessException;
import com.copropia.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PropiedadService implements PropiedadUseCase {

    private final PropiedadRepository propiedadRepository;

    @Override
    @Transactional
    public Propiedad create(Propiedad propiedad) {
        if (propiedadRepository.existsByCopropiedadIdAndIdentificacion(
                propiedad.getCopropiedadId(), propiedad.getIdentificacion())) {
            throw new BusinessException("Ya existe la propiedad " + propiedad.getIdentificacion() + " en esta copropiedad");
        }
        propiedad.setEstado("activa");
        propiedad.setCreatedAt(LocalDateTime.now());
        return propiedadRepository.save(propiedad);
    }

    @Override
    public Propiedad getById(Long id) {
        return propiedadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Propiedad", id));
    }

    @Override
    public List<Propiedad> getByCopropiedadId(Long copropiedadId) {
        return propiedadRepository.findByCopropiedadId(copropiedadId);
    }

    @Override
    public List<Propiedad> getByPropietarioId(Long propietarioId) {
        return propiedadRepository.findByPropietarioId(propietarioId);
    }

    @Override
    @Transactional
    public Propiedad update(Long id, Propiedad updated) {
        Propiedad existing = getById(id);
        existing.setPropietarioId(updated.getPropietarioId());
        existing.setTipo(updated.getTipo());
        existing.setMetrosCuadrados(updated.getMetrosCuadrados());
        existing.setCoeficiente(updated.getCoeficiente());
        return propiedadRepository.save(existing);
    }

    @Override
    public BigDecimal getTotalCoeficiente(Long copropiedadId) {
        return propiedadRepository.sumCoeficienteByCopropiedadId(copropiedadId);
    }

    @Override
    public void validateCoeficientes(Long copropiedadId) {
        BigDecimal total = getTotalCoeficiente(copropiedadId);
        if (total != null && total.compareTo(new BigDecimal("100")) > 0) {
            throw new BusinessException("Los coeficientes superan el 100%. Total actual: " + total + "%");
        }
    }
}

package com.copropia.copropiedad.application.service;

import com.copropia.common.enums.EstadoCopropiedad;
import com.copropia.copropiedad.domain.model.Copropiedad;
import com.copropia.copropiedad.domain.port.in.CopropiedadUseCase;
import com.copropia.copropiedad.domain.port.out.CopropiedadRepository;
import com.copropia.copropiedad.domain.port.out.PlanRepository;
import com.copropia.common.exception.BusinessException;
import com.copropia.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CopropiedadService implements CopropiedadUseCase {

    private final CopropiedadRepository copropiedadRepository;
    private final PlanRepository planRepository;

    @Override
    @Transactional
    public Copropiedad create(Copropiedad copropiedad) {
        log.info("Creando copropiedad: {} NIT: {}", copropiedad.getNombre(), copropiedad.getNit());
        if (copropiedadRepository.existsByNit(copropiedad.getNit())) {
            log.warn("Intento de crear copropiedad con NIT duplicado: {}", copropiedad.getNit());
            throw new BusinessException("Ya existe una copropiedad con NIT: " + copropiedad.getNit());
        }
        planRepository.findById(copropiedad.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan", copropiedad.getPlanId()));

        copropiedad.setEstado(EstadoCopropiedad.ACTIVA);
        copropiedad.setCreatedAt(LocalDateTime.now());
        Copropiedad saved = copropiedadRepository.save(copropiedad);
        log.info("Copropiedad creada exitosamente con id: {}", saved.getId());
        return saved;
    }

    @Override
    public Copropiedad getById(Long id) {
        log.debug("Buscando copropiedad con id: {}", id);
        return copropiedadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Copropiedad", id));
    }

    @Override
    public List<Copropiedad> getAll() {
        log.debug("Consultando todas las copropiedades");
        List<Copropiedad> result = copropiedadRepository.findAll();
        log.debug("Copropiedades encontradas: {}", result.size());
        return result;
    }

    @Override
    @Transactional
    public Copropiedad update(Long id, Copropiedad updated) {
        log.info("Actualizando copropiedad id: {}", id);
        Copropiedad existing = getById(id);
        existing.setNombre(updated.getNombre());
        existing.setDireccion(updated.getDireccion());
        existing.setCiudad(updated.getCiudad());
        existing.setDepartamento(updated.getDepartamento());
        existing.setTelefono(updated.getTelefono());
        existing.setEmail(updated.getEmail());
        return copropiedadRepository.save(existing);
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        log.warn("Desactivando copropiedad id: {}", id);
        Copropiedad copropiedad = getById(id);
        copropiedad.setEstado(EstadoCopropiedad.INACTIVA);
        copropiedadRepository.save(copropiedad);
        log.info("Copropiedad id: {} desactivada exitosamente", id);
    }
}

package com.copropia.copropiedad.application.service;

import com.copropia.copropiedad.domain.model.Plan;
import com.copropia.copropiedad.domain.port.in.PlanUseCase;
import com.copropia.copropiedad.domain.port.out.PlanRepository;
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
public class PlanService implements PlanUseCase {

    private final PlanRepository planRepository;

    @Override
    @Transactional
    public Plan create(Plan plan) {
        log.info("Creando plan: {}", plan.getNombre());
        plan.setEstado("activo");
        plan.setCreatedAt(LocalDateTime.now());
        Plan saved = planRepository.save(plan);
        log.info("Plan creado exitosamente con id: {}", saved.getId());
        return saved;
    }

    @Override
    public Plan getById(Long id) {
        log.debug("Buscando plan con id: {}", id);
        return planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan", id));
    }

    @Override
    public List<Plan> getAll() {
        log.debug("Consultando todos los planes");
        return planRepository.findAll();
    }

    @Override
    @Transactional
    public Plan update(Long id, Plan updated) {
        log.info("Actualizando plan id: {}", id);
        Plan existing = getById(id);
        existing.setNombre(updated.getNombre());
        existing.setDescripcion(updated.getDescripcion());
        existing.setMaxCopropiedades(updated.getMaxCopropiedades());
        existing.setMaxUsuarios(updated.getMaxUsuarios());
        existing.setPrecio(updated.getPrecio());
        return planRepository.save(existing);
    }
}

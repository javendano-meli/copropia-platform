package com.copropia.copropiedad.application.service;

import com.copropia.copropiedad.domain.model.Plan;
import com.copropia.copropiedad.domain.port.in.PlanUseCase;
import com.copropia.copropiedad.domain.port.out.PlanRepository;
import com.copropia.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanService implements PlanUseCase {

    private final PlanRepository planRepository;

    @Override
    @Transactional
    public Plan create(Plan plan) {
        plan.setEstado("activo");
        plan.setCreatedAt(LocalDateTime.now());
        return planRepository.save(plan);
    }

    @Override
    public Plan getById(Long id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan", id));
    }

    @Override
    public List<Plan> getAll() {
        return planRepository.findAll();
    }

    @Override
    @Transactional
    public Plan update(Long id, Plan updated) {
        Plan existing = getById(id);
        existing.setNombre(updated.getNombre());
        existing.setDescripcion(updated.getDescripcion());
        existing.setMaxCopropiedades(updated.getMaxCopropiedades());
        existing.setMaxUsuarios(updated.getMaxUsuarios());
        existing.setPrecio(updated.getPrecio());
        return planRepository.save(existing);
    }
}

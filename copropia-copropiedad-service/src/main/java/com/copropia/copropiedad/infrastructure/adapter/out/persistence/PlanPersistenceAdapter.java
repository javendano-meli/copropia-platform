package com.copropia.copropiedad.infrastructure.adapter.out.persistence;

import com.copropia.copropiedad.domain.model.Plan;
import com.copropia.copropiedad.domain.port.out.PlanRepository;
import com.copropia.copropiedad.infrastructure.adapter.out.persistence.jpa.PlanJpaRepository;
import com.copropia.copropiedad.infrastructure.adapter.out.persistence.mapper.PlanPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PlanPersistenceAdapter implements PlanRepository {
    private final PlanJpaRepository jpaRepository;
    private final PlanPersistenceMapper mapper;

    @Override
    public Plan save(Plan plan) {
        log.debug("Persistiendo plan: {}", plan.getNombre());
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(plan)));
    }
    @Override
    public Optional<Plan> findById(Long id) {
        log.debug("Buscando plan en BD con id: {}", id);
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
    @Override
    public List<Plan> findAll() {
        log.debug("Consultando todos los planes en BD");
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }
}

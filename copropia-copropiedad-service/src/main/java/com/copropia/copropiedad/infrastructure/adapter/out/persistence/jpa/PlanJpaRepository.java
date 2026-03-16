package com.copropia.copropiedad.infrastructure.adapter.out.persistence.jpa;

import com.copropia.copropiedad.infrastructure.adapter.out.persistence.entity.PlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanJpaRepository extends JpaRepository<PlanEntity, Long> {
}

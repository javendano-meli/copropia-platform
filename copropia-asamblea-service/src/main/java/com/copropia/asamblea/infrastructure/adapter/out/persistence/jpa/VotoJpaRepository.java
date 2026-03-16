package com.copropia.asamblea.infrastructure.adapter.out.persistence.jpa;

import com.copropia.asamblea.infrastructure.adapter.out.persistence.entity.VotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VotoJpaRepository extends JpaRepository<VotoEntity, Long> {
    List<VotoEntity> findByVotacionId(Long votacionId);
    boolean existsByVotacionIdAndPropiedadId(Long votacionId, Long propiedadId);
}

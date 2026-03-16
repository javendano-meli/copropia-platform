package com.copropia.asamblea.infrastructure.adapter.out.persistence.jpa;

import com.copropia.asamblea.infrastructure.adapter.out.persistence.entity.OpcionVotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OpcionVotoJpaRepository extends JpaRepository<OpcionVotoEntity, Long> {
    List<OpcionVotoEntity> findByVotacionIdOrderByOrdenAsc(Long votacionId);
}

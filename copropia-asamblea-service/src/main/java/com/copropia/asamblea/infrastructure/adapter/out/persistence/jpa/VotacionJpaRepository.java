package com.copropia.asamblea.infrastructure.adapter.out.persistence.jpa;

import com.copropia.asamblea.infrastructure.adapter.out.persistence.entity.VotacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VotacionJpaRepository extends JpaRepository<VotacionEntity, Long> {
    List<VotacionEntity> findByAsambleaIdOrderByOrdenAsc(Long asambleaId);
}

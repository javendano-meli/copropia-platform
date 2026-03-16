package com.copropia.pqr.infrastructure.adapter.out.persistence.jpa;

import com.copropia.pqr.infrastructure.adapter.out.persistence.entity.ComentarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComentarioJpaRepository extends JpaRepository<ComentarioEntity, Long> {
    List<ComentarioEntity> findByPqrIdOrderByCreatedAtAsc(Long pqrId);
    int countByPqrId(Long pqrId);
}

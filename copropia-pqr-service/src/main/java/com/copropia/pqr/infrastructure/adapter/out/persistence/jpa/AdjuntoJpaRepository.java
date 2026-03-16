package com.copropia.pqr.infrastructure.adapter.out.persistence.jpa;

import com.copropia.pqr.infrastructure.adapter.out.persistence.entity.AdjuntoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AdjuntoJpaRepository extends JpaRepository<AdjuntoEntity, Long> {
    List<AdjuntoEntity> findByPqrId(Long pqrId);
}

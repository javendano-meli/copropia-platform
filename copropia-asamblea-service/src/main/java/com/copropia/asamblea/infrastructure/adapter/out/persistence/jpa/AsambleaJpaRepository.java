package com.copropia.asamblea.infrastructure.adapter.out.persistence.jpa;

import com.copropia.asamblea.infrastructure.adapter.out.persistence.entity.AsambleaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AsambleaJpaRepository extends JpaRepository<AsambleaEntity, Long> {
    List<AsambleaEntity> findByCopropiedadId(Long copropiedadId);
}

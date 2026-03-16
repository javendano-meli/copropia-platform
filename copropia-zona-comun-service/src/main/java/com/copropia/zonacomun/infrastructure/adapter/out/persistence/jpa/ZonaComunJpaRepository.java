package com.copropia.zonacomun.infrastructure.adapter.out.persistence.jpa;

import com.copropia.zonacomun.infrastructure.adapter.out.persistence.entity.ZonaComunEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ZonaComunJpaRepository extends JpaRepository<ZonaComunEntity, Long> {
    List<ZonaComunEntity> findByCopropiedadIdAndActivaTrue(Long copropiedadId);
    boolean existsByCopropiedadIdAndNombre(Long copropiedadId, String nombre);
}

package com.copropia.copropiedad.infrastructure.adapter.out.persistence.jpa;

import com.copropia.copropiedad.infrastructure.adapter.out.persistence.entity.CopropiedadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CopropiedadJpaRepository extends JpaRepository<CopropiedadEntity, Long> {
    boolean existsByNit(String nit);
}

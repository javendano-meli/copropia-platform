package com.copropia.copropiedad.infrastructure.adapter.out.persistence.jpa;

import com.copropia.copropiedad.infrastructure.adapter.out.persistence.entity.PropiedadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.util.List;

public interface PropiedadJpaRepository extends JpaRepository<PropiedadEntity, Long> {
    List<PropiedadEntity> findByCopropiedadId(Long copropiedadId);
    List<PropiedadEntity> findByPropietarioId(Long propietarioId);
    boolean existsByCopropiedadIdAndIdentificacion(Long copropiedadId, String identificacion);

    @Query("SELECT COALESCE(SUM(p.coeficiente), 0) FROM PropiedadEntity p WHERE p.copropiedadId = :copropiedadId AND p.estado = 'activa'")
    BigDecimal sumCoeficienteByCopropiedadId(Long copropiedadId);
}

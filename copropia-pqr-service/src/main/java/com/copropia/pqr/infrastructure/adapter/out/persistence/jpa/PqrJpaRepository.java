package com.copropia.pqr.infrastructure.adapter.out.persistence.jpa;

import com.copropia.pqr.infrastructure.adapter.out.persistence.entity.PqrEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PqrJpaRepository extends JpaRepository<PqrEntity, Long> {
    List<PqrEntity> findByCopropiedadIdAndEsPublicoTrueOrderByCreatedAtDesc(Long copropiedadId);
    List<PqrEntity> findByUsuarioIdOrderByCreatedAtDesc(Long usuarioId);
    List<PqrEntity> findByCopropiedadIdOrderByCreatedAtDesc(Long copropiedadId);
}

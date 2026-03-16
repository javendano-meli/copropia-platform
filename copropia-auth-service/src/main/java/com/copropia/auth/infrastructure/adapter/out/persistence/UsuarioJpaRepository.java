package com.copropia.auth.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Long> {
    Optional<UsuarioEntity> findByEmail(String email);
    List<UsuarioEntity> findByCopropiedadId(Long copropiedadId);
    boolean existsByEmail(String email);
}

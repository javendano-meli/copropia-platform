package com.copropia.auth.domain.port.out;

import com.copropia.auth.domain.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {
    Usuario save(Usuario usuario);
    Optional<Usuario> findById(Long id);
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByCopropiedadId(Long copropiedadId);
    boolean existsByEmail(String email);
}

package com.copropia.auth.domain.port.in;

import com.copropia.auth.domain.model.Usuario;
import java.util.List;

public interface UsuarioUseCase {
    Usuario getById(Long id);
    List<Usuario> getByCopropiedadId(Long copropiedadId);
    Usuario update(Long id, Usuario usuario);
    void deactivate(Long id);
}

package com.copropia.auth.application.service;

import com.copropia.auth.domain.model.Usuario;
import com.copropia.auth.domain.port.in.UsuarioUseCase;
import com.copropia.auth.domain.port.out.UsuarioRepository;
import com.copropia.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UsuarioUseCase {

    private final UsuarioRepository usuarioRepository;

    @Override
    public Usuario getById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
    }

    @Override
    public List<Usuario> getByCopropiedadId(Long copropiedadId) {
        return usuarioRepository.findByCopropiedadId(copropiedadId);
    }

    @Override
    @Transactional
    public Usuario update(Long id, Usuario updated) {
        Usuario existing = getById(id);
        existing.setNombre(updated.getNombre());
        existing.setApellido(updated.getApellido());
        existing.setTelefono(updated.getTelefono());
        return usuarioRepository.save(existing);
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        Usuario usuario = getById(id);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }
}

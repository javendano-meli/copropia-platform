package com.copropia.auth.application.service;

import com.copropia.auth.domain.model.Usuario;
import com.copropia.auth.domain.port.in.UsuarioUseCase;
import com.copropia.auth.domain.port.out.UsuarioRepository;
import com.copropia.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService implements UsuarioUseCase {

    private final UsuarioRepository usuarioRepository;

    @Override
    public Usuario getById(Long id) {
        log.debug("Buscando usuario por id={}", id);
        return usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado id={}", id);
                    return new ResourceNotFoundException("Usuario", id);
                });
    }

    @Override
    public List<Usuario> getByCopropiedadId(Long copropiedadId) {
        log.debug("Buscando usuarios por copropiedadId={}", copropiedadId);
        List<Usuario> usuarios = usuarioRepository.findByCopropiedadId(copropiedadId);
        log.debug("Encontrados {} usuarios para copropiedadId={}", usuarios.size(), copropiedadId);
        return usuarios;
    }

    @Override
    @Transactional
    public Usuario update(Long id, Usuario updated) {
        log.info("Actualizando usuario id={}", id);
        Usuario existing = getById(id);
        existing.setNombre(updated.getNombre());
        existing.setApellido(updated.getApellido());
        existing.setTelefono(updated.getTelefono());
        return usuarioRepository.save(existing);
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        log.warn("Desactivando usuario id={}", id);
        Usuario usuario = getById(id);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }
}

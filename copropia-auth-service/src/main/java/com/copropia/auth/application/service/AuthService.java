package com.copropia.auth.application.service;

import com.copropia.auth.domain.model.Usuario;
import com.copropia.auth.domain.port.in.AuthUseCase;
import com.copropia.auth.domain.port.out.UsuarioRepository;
import com.copropia.common.exception.BusinessException;
import com.copropia.common.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public String login(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Credenciales invalidas", HttpStatus.UNAUTHORIZED));

        if (!usuario.isActivo()) {
            throw new BusinessException("Usuario inactivo", HttpStatus.FORBIDDEN);
        }

        if (!passwordEncoder.matches(password, usuario.getPasswordHash())) {
            throw new BusinessException("Credenciales invalidas", HttpStatus.UNAUTHORIZED);
        }

        return jwtUtils.generateToken(usuario.getEmail(), usuario.getRol().name(), usuario.getCopropiedadId());
    }

    @Override
    @Transactional
    public Usuario register(Usuario usuario, String rawPassword) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new BusinessException("El email ya esta registrado");
        }

        usuario.setPasswordHash(passwordEncoder.encode(rawPassword));
        usuario.setActivo(true);
        usuario.setCreatedAt(LocalDateTime.now());

        return usuarioRepository.save(usuario);
    }
}

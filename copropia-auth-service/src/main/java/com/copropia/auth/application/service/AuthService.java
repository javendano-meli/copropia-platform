package com.copropia.auth.application.service;

import com.copropia.auth.domain.model.Usuario;
import com.copropia.auth.domain.port.in.AuthUseCase;
import com.copropia.auth.domain.port.out.UsuarioRepository;
import com.copropia.common.exception.BusinessException;
import com.copropia.common.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public String login(String email, String password) {
        log.info("Intento de login para email={}", email);
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Login fallido: credenciales invalidas para email={}", email);
                    return new BusinessException("Credenciales invalidas", HttpStatus.UNAUTHORIZED);
                });

        if (!usuario.isActivo()) {
            log.warn("Login fallido: usuario inactivo email={}", email);
            throw new BusinessException("Usuario inactivo", HttpStatus.FORBIDDEN);
        }

        if (!passwordEncoder.matches(password, usuario.getPasswordHash())) {
            log.warn("Login fallido: credenciales invalidas para email={}", email);
            throw new BusinessException("Credenciales invalidas", HttpStatus.UNAUTHORIZED);
        }

        log.info("Login exitoso para email={}", email);
        return jwtUtils.generateToken(usuario.getEmail(), usuario.getRol().name(), usuario.getCopropiedadId());
    }

    @Override
    @Transactional
    public Usuario register(Usuario usuario, String rawPassword) {
        log.info("Intento de registro para email={}", usuario.getEmail());
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            log.warn("Registro fallido: email duplicado email={}", usuario.getEmail());
            throw new BusinessException("El email ya esta registrado");
        }

        usuario.setPasswordHash(passwordEncoder.encode(rawPassword));
        usuario.setActivo(true);
        usuario.setCreatedAt(LocalDateTime.now());

        Usuario saved = usuarioRepository.save(usuario);
        log.info("Registro exitoso para email={}, userId={}", saved.getEmail(), saved.getId());
        return saved;
    }
}

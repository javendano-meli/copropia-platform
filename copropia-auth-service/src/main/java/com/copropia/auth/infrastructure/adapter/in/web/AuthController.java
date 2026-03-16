package com.copropia.auth.infrastructure.adapter.in.web;

import com.copropia.auth.domain.model.Usuario;
import com.copropia.auth.domain.port.in.AuthUseCase;
import com.copropia.auth.infrastructure.adapter.in.web.dto.*;
import com.copropia.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Solicitud de login recibida para email={}", request.getEmail());
        String token = authUseCase.login(request.getEmail(), request.getPassword());
        LoginResponse response = new LoginResponse(token, request.getEmail(), null);
        log.info("Login exitoso para email={}", request.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Login exitoso", response));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UsuarioResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Solicitud de registro recibida para email={}, rol={}", request.getEmail(), request.getRol());
        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .rol(request.getRol())
                .copropiedadId(request.getCopropiedadId())
                .build();

        Usuario created = authUseCase.register(usuario, request.getPassword());

        UsuarioResponse response = UsuarioResponse.builder()
                .id(created.getId())
                .nombre(created.getNombre())
                .apellido(created.getApellido())
                .email(created.getEmail())
                .telefono(created.getTelefono())
                .rol(created.getRol())
                .copropiedadId(created.getCopropiedadId())
                .activo(created.isActivo())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Usuario registrado", response));
    }
}

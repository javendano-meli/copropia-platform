package com.copropia.auth.infrastructure.adapter.in.web;

import com.copropia.auth.domain.model.Usuario;
import com.copropia.auth.domain.port.in.UsuarioUseCase;
import com.copropia.auth.infrastructure.adapter.in.web.dto.UsuarioResponse;
import com.copropia.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioUseCase usuarioUseCase;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(toResponse(usuarioUseCase.getById(id))));
    }

    @GetMapping("/copropiedad/{copropiedadId}")
    public ResponseEntity<ApiResponse<List<UsuarioResponse>>> getByCopropiedad(@PathVariable Long copropiedadId) {
        List<UsuarioResponse> list = usuarioUseCase.getByCopropiedadId(copropiedadId).stream()
                .map(this::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id) {
        usuarioUseCase.deactivate(id);
        return ResponseEntity.ok(ApiResponse.ok("Usuario desactivado", null));
    }

    private UsuarioResponse toResponse(Usuario u) {
        return UsuarioResponse.builder()
                .id(u.getId()).nombre(u.getNombre()).apellido(u.getApellido())
                .email(u.getEmail()).telefono(u.getTelefono()).rol(u.getRol())
                .copropiedadId(u.getCopropiedadId()).activo(u.isActivo())
                .build();
    }
}

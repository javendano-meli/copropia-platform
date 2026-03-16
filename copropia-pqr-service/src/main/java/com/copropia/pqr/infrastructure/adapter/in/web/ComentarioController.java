package com.copropia.pqr.infrastructure.adapter.in.web;

import com.copropia.common.dto.ApiResponse;
import com.copropia.pqr.domain.model.Comentario;
import com.copropia.pqr.domain.port.in.ComentarioUseCase;
import com.copropia.pqr.infrastructure.adapter.in.web.dto.ComentarioRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j @RestController @RequestMapping("/api/pqr-comentarios") @RequiredArgsConstructor
public class ComentarioController {
    private final ComentarioUseCase comentarioUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<Comentario>> create(@Valid @RequestBody ComentarioRequest req) {
        log.info("POST /api/pqr-comentarios - PQR: {} por {}", req.getPqrId(), req.getUsuarioNombre());
        Comentario comentario = Comentario.builder()
                .pqrId(req.getPqrId()).usuarioId(req.getUsuarioId())
                .usuarioNombre(req.getUsuarioNombre()).contenido(req.getContenido()).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Comentario creado", comentarioUseCase.create(comentario)));
    }

    @GetMapping("/pqr/{pqrId}")
    public ResponseEntity<ApiResponse<List<Comentario>>> getByPqr(@PathVariable Long pqrId) {
        return ResponseEntity.ok(ApiResponse.ok(comentarioUseCase.getByPqrId(pqrId)));
    }
}

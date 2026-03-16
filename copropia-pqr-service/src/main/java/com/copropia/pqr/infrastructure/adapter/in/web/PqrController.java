package com.copropia.pqr.infrastructure.adapter.in.web;

import com.copropia.common.dto.ApiResponse;
import com.copropia.pqr.domain.model.Adjunto;
import com.copropia.pqr.domain.model.Pqr;
import com.copropia.pqr.domain.port.in.PqrUseCase;
import com.copropia.pqr.infrastructure.adapter.in.web.dto.CambioEstadoRequest;
import com.copropia.pqr.infrastructure.adapter.in.web.dto.PqrRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j @RestController @RequestMapping("/api/pqr") @RequiredArgsConstructor
public class PqrController {
    private final PqrUseCase pqrUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<Pqr>> create(@Valid @RequestBody PqrRequest req) {
        log.info("POST /api/pqr - '{}' por {}", req.getTitulo(), req.getUsuarioNombre());
        List<Adjunto> adjuntos = req.getAdjuntos() != null
                ? req.getAdjuntos().stream().map(a -> Adjunto.builder().url(a.getUrl()).tipoArchivo(a.getTipoArchivo()).nombre(a.getNombre()).build()).toList()
                : List.of();
        Pqr pqr = Pqr.builder()
                .copropiedadId(req.getCopropiedadId()).usuarioId(req.getUsuarioId())
                .usuarioNombre(req.getUsuarioNombre()).propiedadId(req.getPropiedadId())
                .tipo(req.getTipo()).titulo(req.getTitulo()).contenido(req.getContenido())
                .esPublico(req.isEsPublico()).adjuntos(adjuntos).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("PQR creada", pqrUseCase.create(pqr)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Pqr>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(pqrUseCase.getById(id)));
    }

    @GetMapping("/feed/{copropiedadId}")
    public ResponseEntity<ApiResponse<List<Pqr>>> getFeed(@PathVariable Long copropiedadId) {
        log.info("GET /api/pqr/feed/{}", copropiedadId);
        return ResponseEntity.ok(ApiResponse.ok(pqrUseCase.getFeedPublico(copropiedadId)));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<ApiResponse<List<Pqr>>> getByUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(ApiResponse.ok(pqrUseCase.getByUsuarioId(usuarioId)));
    }

    @GetMapping("/copropiedad/{copropiedadId}")
    public ResponseEntity<ApiResponse<List<Pqr>>> getAllByCopropiedad(@PathVariable Long copropiedadId) {
        log.info("GET /api/pqr/copropiedad/{} (admin)", copropiedadId);
        return ResponseEntity.ok(ApiResponse.ok(pqrUseCase.getAllByCopropiedad(copropiedadId)));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<Pqr>> cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambioEstadoRequest req) {
        log.info("PATCH /api/pqr/{}/estado -> {}", id, req.getEstado());
        return ResponseEntity.ok(ApiResponse.ok("Estado actualizado", pqrUseCase.cambiarEstado(id, req.getEstado())));
    }
}

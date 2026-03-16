package com.copropia.asamblea.infrastructure.adapter.in.web;

import com.copropia.asamblea.domain.model.Asamblea;
import com.copropia.asamblea.domain.port.in.AsambleaUseCase;
import com.copropia.asamblea.infrastructure.adapter.in.web.dto.AsambleaRequest;
import com.copropia.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/asambleas")
@RequiredArgsConstructor
public class AsambleaController {
    private final AsambleaUseCase asambleaUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<Asamblea>> create(@Valid @RequestBody AsambleaRequest req) {
        log.info("POST /api/asambleas - {}", req.getNombre());
        Asamblea asamblea = Asamblea.builder()
                .copropiedadId(req.getCopropiedadId())
                .creadoPor(req.getCreadoPor())
                .nombre(req.getNombre())
                .descripcion(req.getDescripcion())
                .fechaProgramada(req.getFechaProgramada())
                .quorumRequerido(req.getQuorumRequerido() != null ? req.getQuorumRequerido() : new BigDecimal("51.00"))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(asambleaUseCase.create(asamblea)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Asamblea>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(asambleaUseCase.getById(id)));
    }

    @GetMapping("/copropiedad/{copropiedadId}")
    public ResponseEntity<ApiResponse<List<Asamblea>>> getByCopropiedad(@PathVariable Long copropiedadId) {
        return ResponseEntity.ok(ApiResponse.ok(asambleaUseCase.getByCopropiedadId(copropiedadId)));
    }

    @PatchMapping("/{id}/abrir")
    public ResponseEntity<ApiResponse<Asamblea>> open(@PathVariable Long id) {
        log.info("PATCH /api/asambleas/{}/abrir", id);
        return ResponseEntity.ok(ApiResponse.ok("Asamblea abierta", asambleaUseCase.open(id)));
    }

    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<ApiResponse<Asamblea>> close(@PathVariable Long id) {
        log.info("PATCH /api/asambleas/{}/cerrar", id);
        return ResponseEntity.ok(ApiResponse.ok("Asamblea cerrada", asambleaUseCase.close(id)));
    }
}

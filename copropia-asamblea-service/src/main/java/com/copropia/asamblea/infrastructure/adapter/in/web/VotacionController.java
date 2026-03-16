package com.copropia.asamblea.infrastructure.adapter.in.web;

import com.copropia.asamblea.domain.model.OpcionVoto;
import com.copropia.asamblea.domain.model.Votacion;
import com.copropia.asamblea.domain.port.in.VotacionUseCase;
import com.copropia.asamblea.infrastructure.adapter.in.web.dto.VotacionRequest;
import com.copropia.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("/api/votaciones")
@RequiredArgsConstructor
public class VotacionController {
    private final VotacionUseCase votacionUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<Votacion>> create(@Valid @RequestBody VotacionRequest req) {
        log.info("POST /api/votaciones - '{}' con {} opciones", req.getTitulo(),
                req.getOpciones() != null ? req.getOpciones().size() : 0);
        AtomicInteger counter = new AtomicInteger(1);
        List<OpcionVoto> opciones = req.getOpciones() != null
                ? req.getOpciones().stream()
                    .map(nombre -> OpcionVoto.builder().nombre(nombre).orden(counter.getAndIncrement()).build())
                    .toList()
                : List.of();

        Votacion votacion = Votacion.builder()
                .asambleaId(req.getAsambleaId())
                .titulo(req.getTitulo())
                .descripcion(req.getDescripcion())
                .tipoVotacion(req.getTipoVotacion())
                .orden(req.getOrden())
                .opciones(opciones)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(votacionUseCase.create(votacion)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Votacion>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(votacionUseCase.getById(id)));
    }

    @GetMapping("/asamblea/{asambleaId}")
    public ResponseEntity<ApiResponse<List<Votacion>>> getByAsamblea(@PathVariable Long asambleaId) {
        return ResponseEntity.ok(ApiResponse.ok(votacionUseCase.getByAsambleaId(asambleaId)));
    }

    @PatchMapping("/{id}/abrir")
    public ResponseEntity<ApiResponse<Votacion>> open(@PathVariable Long id) {
        log.info("PATCH /api/votaciones/{}/abrir", id);
        return ResponseEntity.ok(ApiResponse.ok("Votacion abierta", votacionUseCase.open(id)));
    }

    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<ApiResponse<Votacion>> close(@PathVariable Long id) {
        log.info("PATCH /api/votaciones/{}/cerrar", id);
        return ResponseEntity.ok(ApiResponse.ok("Votacion cerrada", votacionUseCase.close(id)));
    }
}

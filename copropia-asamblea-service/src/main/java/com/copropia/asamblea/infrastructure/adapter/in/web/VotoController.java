package com.copropia.asamblea.infrastructure.adapter.in.web;

import com.copropia.asamblea.domain.model.ResultadoVotacion;
import com.copropia.asamblea.domain.model.Voto;
import com.copropia.asamblea.domain.port.in.VotoUseCase;
import com.copropia.asamblea.infrastructure.adapter.in.web.dto.VotoRequest;
import com.copropia.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/votos")
@RequiredArgsConstructor
public class VotoController {
    private final VotoUseCase votoUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<Voto>> emitirVoto(@Valid @RequestBody VotoRequest req) {
        log.info("POST /api/votos - votacion: {}, propiedad: {}", req.getVotacionId(), req.getPropiedadId());
        Voto voto = Voto.builder()
                .votacionId(req.getVotacionId())
                .opcionId(req.getOpcionId())
                .usuarioId(req.getUsuarioId())
                .propiedadId(req.getPropiedadId())
                .coeficienteAplicado(req.getCoeficienteAplicado())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Voto registrado", votoUseCase.emitirVoto(voto)));
    }

    @GetMapping("/resultados/{votacionId}")
    public ResponseEntity<ApiResponse<ResultadoVotacion>> getResultados(
            @PathVariable Long votacionId,
            @RequestParam Long copropiedadId) {
        log.info("GET /api/votos/resultados/{}", votacionId);
        return ResponseEntity.ok(ApiResponse.ok(votoUseCase.getResultados(votacionId, copropiedadId)));
    }
}

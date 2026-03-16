package com.copropia.asamblea.infrastructure.adapter.in.web;

import com.copropia.asamblea.domain.model.ResultadoVotacion;
import com.copropia.asamblea.domain.model.Voto;
import com.copropia.asamblea.domain.port.in.VotoUseCase;
import com.copropia.asamblea.infrastructure.adapter.in.web.dto.VotoRequest;
import com.copropia.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votos")
@RequiredArgsConstructor
public class VotoController {
    private final VotoUseCase votoUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<Voto>> emitirVoto(@Valid @RequestBody VotoRequest req) {
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
        return ResponseEntity.ok(ApiResponse.ok(votoUseCase.getResultados(votacionId, copropiedadId)));
    }
}

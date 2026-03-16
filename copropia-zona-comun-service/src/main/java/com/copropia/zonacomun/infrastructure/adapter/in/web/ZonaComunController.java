package com.copropia.zonacomun.infrastructure.adapter.in.web;

import com.copropia.common.dto.ApiResponse;
import com.copropia.zonacomun.domain.model.ZonaComun;
import com.copropia.zonacomun.domain.port.in.ZonaComunUseCase;
import com.copropia.zonacomun.infrastructure.adapter.in.web.dto.ZonaComunRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/zonas-comunes")
@RequiredArgsConstructor
public class ZonaComunController {
    private final ZonaComunUseCase zonaComunUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<ZonaComun>> create(@Valid @RequestBody ZonaComunRequest req) {
        log.info("POST /api/zonas-comunes - '{}'", req.getNombre());
        ZonaComun zona = ZonaComun.builder()
                .copropiedadId(req.getCopropiedadId())
                .nombre(req.getNombre()).descripcion(req.getDescripcion())
                .capacidad(req.getCapacidad()).tipoReserva(req.getTipoReserva())
                .maxHorasReserva(req.getMaxHorasReserva())
                .horaApertura(req.getHoraApertura()).horaCierre(req.getHoraCierre())
                .requiereAprobacion(req.isRequiereAprobacion())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(zonaComunUseCase.create(zona)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ZonaComun>> getById(@PathVariable Long id) {
        log.debug("GET /api/zonas-comunes/{}", id);
        return ResponseEntity.ok(ApiResponse.ok(zonaComunUseCase.getById(id)));
    }

    @GetMapping("/copropiedad/{copropiedadId}")
    public ResponseEntity<ApiResponse<List<ZonaComun>>> getByCopropiedad(@PathVariable Long copropiedadId) {
        log.debug("GET /api/zonas-comunes/copropiedad/{}", copropiedadId);
        return ResponseEntity.ok(ApiResponse.ok(zonaComunUseCase.getByCopropiedadId(copropiedadId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ZonaComun>> update(@PathVariable Long id, @Valid @RequestBody ZonaComunRequest req) {
        log.info("PUT /api/zonas-comunes/{}", id);
        ZonaComun zona = ZonaComun.builder()
                .nombre(req.getNombre()).descripcion(req.getDescripcion())
                .capacidad(req.getCapacidad()).tipoReserva(req.getTipoReserva())
                .maxHorasReserva(req.getMaxHorasReserva())
                .horaApertura(req.getHoraApertura()).horaCierre(req.getHoraCierre())
                .requiereAprobacion(req.isRequiereAprobacion())
                .build();
        return ResponseEntity.ok(ApiResponse.ok(zonaComunUseCase.update(id, zona)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id) {
        log.warn("DELETE /api/zonas-comunes/{}", id);
        zonaComunUseCase.deactivate(id);
        return ResponseEntity.ok(ApiResponse.ok("Zona comun desactivada", null));
    }
}

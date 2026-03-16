package com.copropia.copropiedad.infrastructure.adapter.in.web;

import com.copropia.common.dto.ApiResponse;
import com.copropia.copropiedad.domain.model.Propiedad;
import com.copropia.copropiedad.domain.port.in.PropiedadUseCase;
import com.copropia.copropiedad.infrastructure.adapter.in.web.dto.PropiedadRequest;
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
@RequestMapping("/api/propiedades")
@RequiredArgsConstructor
public class PropiedadController {
    private final PropiedadUseCase propiedadUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<Propiedad>> create(@Valid @RequestBody PropiedadRequest req) {
        log.info("POST /api/propiedades - {} en copropiedad {}", req.getIdentificacion(), req.getCopropiedadId());
        Propiedad prop = Propiedad.builder()
                .copropiedadId(req.getCopropiedadId()).propietarioId(req.getPropietarioId())
                .identificacion(req.getIdentificacion()).tipo(req.getTipo())
                .metrosCuadrados(req.getMetrosCuadrados()).coeficiente(req.getCoeficiente())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(propiedadUseCase.create(prop)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Propiedad>> getById(@PathVariable Long id) {
        log.debug("GET /api/propiedades/{}", id);
        return ResponseEntity.ok(ApiResponse.ok(propiedadUseCase.getById(id)));
    }

    @GetMapping("/copropiedad/{copropiedadId}")
    public ResponseEntity<ApiResponse<List<Propiedad>>> getByCopropiedad(@PathVariable Long copropiedadId) {
        log.debug("GET /api/propiedades/copropiedad/{}", copropiedadId);
        return ResponseEntity.ok(ApiResponse.ok(propiedadUseCase.getByCopropiedadId(copropiedadId)));
    }

    @GetMapping("/propietario/{propietarioId}")
    public ResponseEntity<ApiResponse<List<Propiedad>>> getByPropietario(@PathVariable Long propietarioId) {
        log.debug("GET /api/propiedades/propietario/{}", propietarioId);
        return ResponseEntity.ok(ApiResponse.ok(propiedadUseCase.getByPropietarioId(propietarioId)));
    }

    @GetMapping("/copropiedad/{copropiedadId}/coeficiente-total")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalCoeficiente(@PathVariable Long copropiedadId) {
        log.debug("GET /api/propiedades/copropiedad/{}/coeficiente-total", copropiedadId);
        return ResponseEntity.ok(ApiResponse.ok(propiedadUseCase.getTotalCoeficiente(copropiedadId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Propiedad>> update(@PathVariable Long id, @Valid @RequestBody PropiedadRequest req) {
        log.debug("PUT /api/propiedades/{}", id);
        Propiedad prop = Propiedad.builder()
                .propietarioId(req.getPropietarioId()).tipo(req.getTipo())
                .metrosCuadrados(req.getMetrosCuadrados()).coeficiente(req.getCoeficiente())
                .build();
        return ResponseEntity.ok(ApiResponse.ok(propiedadUseCase.update(id, prop)));
    }
}

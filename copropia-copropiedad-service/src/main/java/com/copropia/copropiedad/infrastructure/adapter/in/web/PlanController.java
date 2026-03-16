package com.copropia.copropiedad.infrastructure.adapter.in.web;

import com.copropia.common.dto.ApiResponse;
import com.copropia.copropiedad.domain.model.Plan;
import com.copropia.copropiedad.domain.port.in.PlanUseCase;
import com.copropia.copropiedad.infrastructure.adapter.in.web.dto.PlanRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/planes")
@RequiredArgsConstructor
public class PlanController {
    private final PlanUseCase planUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<Plan>> create(@Valid @RequestBody PlanRequest req) {
        Plan plan = Plan.builder()
                .nombre(req.getNombre()).descripcion(req.getDescripcion())
                .maxCopropiedades(req.getMaxCopropiedades()).maxUsuarios(req.getMaxUsuarios())
                .precio(req.getPrecio()).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(planUseCase.create(plan)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Plan>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(planUseCase.getById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Plan>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(planUseCase.getAll()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Plan>> update(@PathVariable Long id, @Valid @RequestBody PlanRequest req) {
        Plan plan = Plan.builder()
                .nombre(req.getNombre()).descripcion(req.getDescripcion())
                .maxCopropiedades(req.getMaxCopropiedades()).maxUsuarios(req.getMaxUsuarios())
                .precio(req.getPrecio()).build();
        return ResponseEntity.ok(ApiResponse.ok(planUseCase.update(id, plan)));
    }
}

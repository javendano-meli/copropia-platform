package com.copropia.copropiedad.infrastructure.adapter.in.web;

import com.copropia.common.dto.ApiResponse;
import com.copropia.copropiedad.domain.model.Copropiedad;
import com.copropia.copropiedad.domain.port.in.CopropiedadUseCase;
import com.copropia.copropiedad.infrastructure.adapter.in.web.dto.CopropiedadRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/copropiedades")
@RequiredArgsConstructor
public class CopropiedadController {
    private final CopropiedadUseCase copropiedadUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<Copropiedad>> create(@Valid @RequestBody CopropiedadRequest req) {
        Copropiedad cop = Copropiedad.builder()
                .planId(req.getPlanId()).nombre(req.getNombre()).nit(req.getNit())
                .direccion(req.getDireccion()).ciudad(req.getCiudad())
                .departamento(req.getDepartamento()).telefono(req.getTelefono())
                .email(req.getEmail()).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(copropiedadUseCase.create(cop)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Copropiedad>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(copropiedadUseCase.getById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Copropiedad>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(copropiedadUseCase.getAll()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Copropiedad>> update(@PathVariable Long id, @Valid @RequestBody CopropiedadRequest req) {
        Copropiedad cop = Copropiedad.builder()
                .nombre(req.getNombre()).direccion(req.getDireccion())
                .ciudad(req.getCiudad()).departamento(req.getDepartamento())
                .telefono(req.getTelefono()).email(req.getEmail()).build();
        return ResponseEntity.ok(ApiResponse.ok(copropiedadUseCase.update(id, cop)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id) {
        copropiedadUseCase.deactivate(id);
        return ResponseEntity.ok(ApiResponse.ok("Copropiedad desactivada", null));
    }
}

package com.copropia.copropiedad.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PlanRequest {
    @NotBlank private String nombre;
    private String descripcion;
    @Positive private int maxCopropiedades;
    @Positive private int maxUsuarios;
    @NotNull @Positive private BigDecimal precio;
}

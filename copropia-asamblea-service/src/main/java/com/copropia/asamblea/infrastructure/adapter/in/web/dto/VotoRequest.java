package com.copropia.asamblea.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class VotoRequest {
    @NotNull private Long votacionId;
    @NotNull private Long opcionId;
    @NotNull private Long usuarioId;
    @NotNull private Long propiedadId;
    @NotNull private BigDecimal coeficienteAplicado;
}

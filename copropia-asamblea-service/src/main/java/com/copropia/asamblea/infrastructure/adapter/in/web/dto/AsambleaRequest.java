package com.copropia.asamblea.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AsambleaRequest {
    @NotNull private Long copropiedadId;
    @NotNull private Long creadoPor;
    @NotBlank private String nombre;
    private String descripcion;
    @NotNull private LocalDateTime fechaProgramada;
    private BigDecimal quorumRequerido;
}

package com.copropia.pqr.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ComentarioRequest {
    @NotNull private Long pqrId;
    @NotNull private Long usuarioId;
    @NotBlank private String usuarioNombre;
    @NotBlank private String contenido;
}

package com.copropia.copropiedad.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CopropiedadRequest {
    @NotNull private Long planId;
    @NotBlank private String nombre;
    @NotBlank private String nit;
    @NotBlank private String direccion;
    @NotBlank private String ciudad;
    private String departamento;
    private String telefono;
    private String email;
}

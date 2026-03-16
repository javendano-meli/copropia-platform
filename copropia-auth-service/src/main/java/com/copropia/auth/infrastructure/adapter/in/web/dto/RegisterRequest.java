package com.copropia.auth.infrastructure.adapter.in.web.dto;

import com.copropia.common.enums.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank private String nombre;
    @NotBlank private String apellido;
    @NotBlank @Email private String email;
    private String telefono;
    @NotBlank @Size(min = 8) private String password;
    @NotNull private Rol rol;
    private Long copropiedadId;
}

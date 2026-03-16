package com.copropia.auth.domain.model;

import com.copropia.common.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    private Long id;
    private Long copropiedadId;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String passwordHash;
    private Rol rol;
    private boolean activo;
    private LocalDateTime createdAt;
}

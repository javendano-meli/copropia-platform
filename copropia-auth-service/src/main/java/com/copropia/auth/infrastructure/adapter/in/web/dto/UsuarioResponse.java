package com.copropia.auth.infrastructure.adapter.in.web.dto;

import com.copropia.common.enums.Rol;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private Rol rol;
    private Long copropiedadId;
    private boolean activo;
}

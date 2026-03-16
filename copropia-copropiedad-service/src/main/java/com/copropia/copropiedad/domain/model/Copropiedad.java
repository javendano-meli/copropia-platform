package com.copropia.copropiedad.domain.model;

import com.copropia.common.enums.EstadoCopropiedad;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Copropiedad {
    private Long id;
    private Long planId;
    private String nombre;
    private String nit;
    private String direccion;
    private String ciudad;
    private String departamento;
    private String telefono;
    private String email;
    private EstadoCopropiedad estado;
    private LocalDateTime createdAt;
}

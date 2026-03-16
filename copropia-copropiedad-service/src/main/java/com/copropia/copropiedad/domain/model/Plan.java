package com.copropia.copropiedad.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Plan {
    private Long id;
    private String nombre;
    private String descripcion;
    private int maxCopropiedades;
    private int maxUsuarios;
    private BigDecimal precio;
    private String estado;
    private LocalDateTime createdAt;
}

package com.copropia.asamblea.domain.model;

import com.copropia.common.enums.EstadoAsamblea;
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
public class Asamblea {
    private Long id;
    private Long copropiedadId;
    private Long creadoPor;
    private String nombre;
    private String descripcion;
    private LocalDateTime fechaProgramada;
    private EstadoAsamblea estado;
    private BigDecimal quorumRequerido;
    private LocalDateTime createdAt;
}

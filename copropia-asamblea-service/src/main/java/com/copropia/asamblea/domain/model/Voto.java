package com.copropia.asamblea.domain.model;

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
public class Voto {
    private Long id;
    private Long votacionId;
    private Long opcionId;
    private Long usuarioId;
    private Long propiedadId;
    private BigDecimal coeficienteAplicado;
    private LocalDateTime timestamp;
}

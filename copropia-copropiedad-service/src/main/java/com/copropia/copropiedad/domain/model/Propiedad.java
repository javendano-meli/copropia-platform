package com.copropia.copropiedad.domain.model;

import com.copropia.common.enums.TipoPropiedad;
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
public class Propiedad {
    private Long id;
    private Long copropiedadId;
    private Long propietarioId;
    private String identificacion;
    private TipoPropiedad tipo;
    private BigDecimal metrosCuadrados;
    private BigDecimal coeficiente;
    private String estado;
    private LocalDateTime createdAt;
}

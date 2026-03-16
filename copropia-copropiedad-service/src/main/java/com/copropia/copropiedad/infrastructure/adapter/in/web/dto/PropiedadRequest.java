package com.copropia.copropiedad.infrastructure.adapter.in.web.dto;

import com.copropia.common.enums.TipoPropiedad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PropiedadRequest {
    @NotNull private Long copropiedadId;
    private Long propietarioId;
    @NotBlank private String identificacion;
    @NotNull private TipoPropiedad tipo;
    private BigDecimal metrosCuadrados;
    @NotNull @Positive private BigDecimal coeficiente;
}

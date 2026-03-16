package com.copropia.pqr.infrastructure.adapter.in.web.dto;

import com.copropia.common.enums.EstadoPQR;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CambioEstadoRequest {
    @NotNull private EstadoPQR estado;
}

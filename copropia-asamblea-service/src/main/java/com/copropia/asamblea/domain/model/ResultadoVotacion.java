package com.copropia.asamblea.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoVotacion {
    private Long votacionId;
    private String titulo;
    private int totalVotos;
    private BigDecimal coeficienteTotal;
    private BigDecimal porcentajeParticipacion;
    private List<ResultadoOpcion> resultadosPorOpcion;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultadoOpcion {
        private Long opcionId;
        private String opcion;
        private int cantidadVotos;
        private BigDecimal coeficienteTotal;
        private BigDecimal porcentaje;
    }
}

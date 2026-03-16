package com.copropia.pqr.infrastructure.adapter.in.web.dto;

import com.copropia.common.enums.TipoPQR;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class PqrRequest {
    @NotNull private Long copropiedadId;
    @NotNull private Long usuarioId;
    @NotBlank private String usuarioNombre;
    private Long propiedadId;
    @NotNull private TipoPQR tipo;
    @NotBlank private String titulo;
    @NotBlank private String contenido;
    private boolean esPublico;
    private List<AdjuntoRequest> adjuntos;

    @Data
    public static class AdjuntoRequest {
        private String url;
        private String tipoArchivo;
        private String nombre;
    }
}

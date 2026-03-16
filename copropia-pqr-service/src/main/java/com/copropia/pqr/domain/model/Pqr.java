package com.copropia.pqr.domain.model;

import com.copropia.common.enums.EstadoPQR;
import com.copropia.common.enums.TipoPQR;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Pqr {
    private Long id;
    private Long copropiedadId;
    private Long usuarioId;
    private String usuarioNombre;
    private Long propiedadId;
    private TipoPQR tipo;
    private String titulo;
    private String contenido;
    private boolean esPublico;
    private EstadoPQR estado;
    private int cantidadComentarios;
    private List<Adjunto> adjuntos;
    private LocalDateTime createdAt;
}

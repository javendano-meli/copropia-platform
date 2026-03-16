package com.copropia.pqr.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Comentario {
    private Long id;
    private Long pqrId;
    private Long usuarioId;
    private String usuarioNombre;
    private String contenido;
    private LocalDateTime createdAt;
}

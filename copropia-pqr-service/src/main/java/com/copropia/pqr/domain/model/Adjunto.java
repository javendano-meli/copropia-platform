package com.copropia.pqr.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Adjunto {
    private Long id;
    private Long pqrId;
    private String url;
    private String tipoArchivo; // IMAGEN, VIDEO
    private String nombre;
    private LocalDateTime createdAt;
}

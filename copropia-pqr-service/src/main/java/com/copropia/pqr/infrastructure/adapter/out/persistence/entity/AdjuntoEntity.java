package com.copropia.pqr.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "pqr_adjunto")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AdjuntoEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "pqr_id", nullable = false)
    private Long pqrId;
    @Column(nullable = false, length = 500)
    private String url;
    @Column(name = "tipo_archivo", nullable = false, length = 20)
    private String tipoArchivo;
    @Column(length = 200)
    private String nombre;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

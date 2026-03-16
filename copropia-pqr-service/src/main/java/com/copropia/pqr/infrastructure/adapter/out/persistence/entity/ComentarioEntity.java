package com.copropia.pqr.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "pqr_comentario")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ComentarioEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "pqr_id", nullable = false)
    private Long pqrId;
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    @Column(name = "usuario_nombre", nullable = false, length = 200)
    private String usuarioNombre;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

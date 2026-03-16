package com.copropia.pqr.infrastructure.adapter.out.persistence.entity;

import com.copropia.common.enums.EstadoPQR;
import com.copropia.common.enums.TipoPQR;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "pqr")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PqrEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "copropiedad_id", nullable = false)
    private Long copropiedadId;
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    @Column(name = "usuario_nombre", nullable = false, length = 200)
    private String usuarioNombre;
    @Column(name = "propiedad_id")
    private Long propiedadId;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private TipoPQR tipo;
    @Column(nullable = false, length = 300)
    private String titulo;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;
    @Column(name = "es_publico", nullable = false)
    private boolean esPublico;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private EstadoPQR estado;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

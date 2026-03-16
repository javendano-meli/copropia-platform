package com.copropia.asamblea.infrastructure.adapter.out.persistence.entity;

import com.copropia.common.enums.EstadoAsamblea;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "asamblea")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AsambleaEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "copropiedad_id", nullable = false)
    private Long copropiedadId;
    @Column(name = "creado_por", nullable = false)
    private Long creadoPor;
    @Column(nullable = false, length = 200)
    private String nombre;
    private String descripcion;
    @Column(name = "fecha_programada", nullable = false)
    private LocalDateTime fechaProgramada;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoAsamblea estado;
    @Column(name = "quorum_requerido", nullable = false, precision = 5, scale = 2)
    private BigDecimal quorumRequerido;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

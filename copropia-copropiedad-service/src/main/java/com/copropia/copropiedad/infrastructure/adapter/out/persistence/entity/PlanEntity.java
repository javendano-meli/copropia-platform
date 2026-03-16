package com.copropia.copropiedad.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "plan")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PlanEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String nombre;
    private String descripcion;
    @Column(name = "max_copropiedades", nullable = false)
    private int maxCopropiedades;
    @Column(name = "max_usuarios", nullable = false)
    private int maxUsuarios;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;
    @Column(nullable = false, length = 20)
    private String estado;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

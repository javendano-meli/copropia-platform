package com.copropia.copropiedad.infrastructure.adapter.out.persistence.entity;

import com.copropia.common.enums.EstadoCopropiedad;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "copropiedad")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CopropiedadEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "plan_id", nullable = false)
    private Long planId;
    @Column(nullable = false, length = 200)
    private String nombre;
    @Column(nullable = false, unique = true, length = 20)
    private String nit;
    @Column(nullable = false, length = 300)
    private String direccion;
    @Column(nullable = false, length = 100)
    private String ciudad;
    @Column(length = 100)
    private String departamento;
    @Column(length = 20)
    private String telefono;
    @Column(length = 150)
    private String email;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCopropiedad estado;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

package com.copropia.asamblea.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "opcion_voto")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class OpcionVotoEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "votacion_id", nullable = false)
    private Long votacionId;
    @Column(nullable = false, length = 100)
    private String nombre;
    @Column(nullable = false)
    private int orden;
}

package com.copropia.asamblea.infrastructure.adapter.out.persistence.entity;

import com.copropia.common.enums.EstadoVotacion;
import com.copropia.common.enums.TipoVotacion;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "votacion")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class VotacionEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "asamblea_id", nullable = false)
    private Long asambleaId;
    @Column(nullable = false, length = 300)
    private String titulo;
    private String descripcion;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_votacion", nullable = false, length = 30)
    private TipoVotacion tipoVotacion;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoVotacion estado;
    @Column(nullable = false)
    private int orden;
    @Column(name = "fecha_apertura")
    private LocalDateTime fechaApertura;
    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;
}

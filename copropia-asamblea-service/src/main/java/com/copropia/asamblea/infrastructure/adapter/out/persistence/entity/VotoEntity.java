package com.copropia.asamblea.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "voto", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"votacion_id", "propiedad_id"})
})
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class VotoEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "votacion_id", nullable = false)
    private Long votacionId;
    @Column(name = "opcion_id", nullable = false)
    private Long opcionId;
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    @Column(name = "propiedad_id", nullable = false)
    private Long propiedadId;
    @Column(name = "coeficiente_aplicado", nullable = false, precision = 6, scale = 4)
    private BigDecimal coeficienteAplicado;
    @Column(nullable = false)
    private LocalDateTime timestamp;
}

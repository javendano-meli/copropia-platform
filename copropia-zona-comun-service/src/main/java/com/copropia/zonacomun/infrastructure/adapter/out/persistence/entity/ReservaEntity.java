package com.copropia.zonacomun.infrastructure.adapter.out.persistence.entity;

import com.copropia.common.enums.EstadoReserva;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "reserva")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReservaEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "zona_comun_id", nullable = false)
    private Long zonaComunId;
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    @Column(name = "propiedad_id", nullable = false)
    private Long propiedadId;
    @Column(nullable = false)
    private LocalDate fecha;
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;
    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoReserva estado;
    private String observaciones;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

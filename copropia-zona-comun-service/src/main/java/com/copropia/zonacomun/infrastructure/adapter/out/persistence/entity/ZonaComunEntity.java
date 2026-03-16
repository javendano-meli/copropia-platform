package com.copropia.zonacomun.infrastructure.adapter.out.persistence.entity;

import com.copropia.common.enums.TipoReserva;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "zona_comun", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"copropiedad_id", "nombre"})
})
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ZonaComunEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "copropiedad_id", nullable = false)
    private Long copropiedadId;
    @Column(nullable = false, length = 100)
    private String nombre;
    private String descripcion;
    @Column(nullable = false)
    private int capacidad;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_reserva", nullable = false, length = 20)
    private TipoReserva tipoReserva;
    @Column(name = "max_horas_reserva")
    private Integer maxHorasReserva;
    @Column(name = "hora_apertura")
    private LocalTime horaApertura;
    @Column(name = "hora_cierre")
    private LocalTime horaCierre;
    @Column(name = "requiere_aprobacion", nullable = false)
    private boolean requiereAprobacion;
    @Column(nullable = false)
    private boolean activa;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

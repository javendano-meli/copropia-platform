package com.copropia.zonacomun.domain.model;

import com.copropia.common.enums.TipoReserva;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZonaComun {
    private Long id;
    private Long copropiedadId;
    private String nombre;
    private String descripcion;
    private int capacidad;
    private TipoReserva tipoReserva;
    private Integer maxHorasReserva;
    private LocalTime horaApertura;
    private LocalTime horaCierre;
    private boolean requiereAprobacion;
    private boolean activa;
    private LocalDateTime createdAt;
}

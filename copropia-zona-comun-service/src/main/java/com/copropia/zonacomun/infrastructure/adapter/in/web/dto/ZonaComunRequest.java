package com.copropia.zonacomun.infrastructure.adapter.in.web.dto;

import com.copropia.common.enums.TipoReserva;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.time.LocalTime;

@Data
public class ZonaComunRequest {
    @NotNull private Long copropiedadId;
    @NotBlank private String nombre;
    private String descripcion;
    @Positive private int capacidad;
    @NotNull private TipoReserva tipoReserva;
    private Integer maxHorasReserva;
    private LocalTime horaApertura;
    private LocalTime horaCierre;
    private boolean requiereAprobacion;
}

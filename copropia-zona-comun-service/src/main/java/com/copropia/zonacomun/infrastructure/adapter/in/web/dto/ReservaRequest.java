package com.copropia.zonacomun.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ReservaRequest {
    @NotNull private Long zonaComunId;
    @NotNull private Long usuarioId;
    @NotNull private Long propiedadId;
    @NotNull private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String observaciones;
}

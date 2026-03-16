package com.copropia.zonacomun.domain.port.out;

import com.copropia.zonacomun.domain.model.Reserva;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ReservaRepository {
    Reserva save(Reserva reserva);
    Optional<Reserva> findById(Long id);
    List<Reserva> findByZonaComunIdAndFecha(Long zonaComunId, LocalDate fecha);
    List<Reserva> findByUsuarioId(Long usuarioId);
    boolean existsOverlap(Long zonaComunId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin);
}

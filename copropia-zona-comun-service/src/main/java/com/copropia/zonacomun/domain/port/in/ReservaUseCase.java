package com.copropia.zonacomun.domain.port.in;

import com.copropia.zonacomun.domain.model.Reserva;
import java.time.LocalDate;
import java.util.List;

public interface ReservaUseCase {
    Reserva create(Reserva reserva);
    Reserva getById(Long id);
    List<Reserva> getByZonaComunIdAndFecha(Long zonaComunId, LocalDate fecha);
    List<Reserva> getByUsuarioId(Long usuarioId);
    Reserva confirm(Long id);
    Reserva cancel(Long id, Long usuarioId);
}

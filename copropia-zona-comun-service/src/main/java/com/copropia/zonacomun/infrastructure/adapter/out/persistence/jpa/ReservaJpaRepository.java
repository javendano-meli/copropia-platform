package com.copropia.zonacomun.infrastructure.adapter.out.persistence.jpa;

import com.copropia.common.enums.EstadoReserva;
import com.copropia.zonacomun.infrastructure.adapter.out.persistence.entity.ReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservaJpaRepository extends JpaRepository<ReservaEntity, Long> {
    List<ReservaEntity> findByZonaComunIdAndFechaOrderByHoraInicioAsc(Long zonaComunId, LocalDate fecha);
    List<ReservaEntity> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    @Query("SELECT COUNT(r) > 0 FROM ReservaEntity r WHERE r.zonaComunId = :zonaComunId " +
           "AND r.fecha = :fecha AND r.estado <> 'CANCELADA' " +
           "AND r.horaInicio < :horaFin AND r.horaFin > :horaInicio")
    boolean existsOverlap(Long zonaComunId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin);
}

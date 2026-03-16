package com.copropia.zonacomun.infrastructure.adapter.in.web;

import com.copropia.common.dto.ApiResponse;
import com.copropia.zonacomun.domain.model.Reserva;
import com.copropia.zonacomun.domain.port.in.ReservaUseCase;
import com.copropia.zonacomun.infrastructure.adapter.in.web.dto.ReservaRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {
    private final ReservaUseCase reservaUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<Reserva>> create(@Valid @RequestBody ReservaRequest req) {
        log.info("POST /api/reservas - zona: {}, usuario: {}, fecha: {}", req.getZonaComunId(), req.getUsuarioId(), req.getFecha());
        Reserva reserva = Reserva.builder()
                .zonaComunId(req.getZonaComunId())
                .usuarioId(req.getUsuarioId())
                .propiedadId(req.getPropiedadId())
                .fecha(req.getFecha())
                .horaInicio(req.getHoraInicio())
                .horaFin(req.getHoraFin())
                .observaciones(req.getObservaciones())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Reserva creada", reservaUseCase.create(reserva)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Reserva>> getById(@PathVariable Long id) {
        log.debug("GET /api/reservas/{}", id);
        return ResponseEntity.ok(ApiResponse.ok(reservaUseCase.getById(id)));
    }

    @GetMapping("/zona/{zonaComunId}")
    public ResponseEntity<ApiResponse<List<Reserva>>> getByZonaAndFecha(
            @PathVariable Long zonaComunId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        log.debug("GET /api/reservas/zona/{} fecha={}", zonaComunId, fecha);
        return ResponseEntity.ok(ApiResponse.ok(reservaUseCase.getByZonaComunIdAndFecha(zonaComunId, fecha)));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<ApiResponse<List<Reserva>>> getByUsuario(@PathVariable Long usuarioId) {
        log.debug("GET /api/reservas/usuario/{}", usuarioId);
        return ResponseEntity.ok(ApiResponse.ok(reservaUseCase.getByUsuarioId(usuarioId)));
    }

    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<ApiResponse<Reserva>> confirm(@PathVariable Long id) {
        log.info("PATCH /api/reservas/{}/confirmar", id);
        return ResponseEntity.ok(ApiResponse.ok("Reserva confirmada", reservaUseCase.confirm(id)));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ApiResponse<Reserva>> cancel(@PathVariable Long id, @RequestParam Long usuarioId) {
        log.info("PATCH /api/reservas/{}/cancelar por usuario: {}", id, usuarioId);
        return ResponseEntity.ok(ApiResponse.ok("Reserva cancelada", reservaUseCase.cancel(id, usuarioId)));
    }
}

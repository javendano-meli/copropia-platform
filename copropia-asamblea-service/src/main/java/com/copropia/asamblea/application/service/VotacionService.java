package com.copropia.asamblea.application.service;

import com.copropia.asamblea.domain.model.Asamblea;
import com.copropia.asamblea.domain.model.OpcionVoto;
import com.copropia.asamblea.domain.model.Votacion;
import com.copropia.asamblea.domain.port.in.VotacionUseCase;
import com.copropia.asamblea.domain.port.out.AsambleaRepository;
import com.copropia.asamblea.domain.port.out.OpcionVotoRepository;
import com.copropia.asamblea.domain.port.out.VotacionRepository;
import com.copropia.common.enums.EstadoAsamblea;
import com.copropia.common.enums.EstadoVotacion;
import com.copropia.common.exception.BusinessException;
import com.copropia.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VotacionService implements VotacionUseCase {
    private final VotacionRepository votacionRepository;
    private final AsambleaRepository asambleaRepository;
    private final OpcionVotoRepository opcionVotoRepository;

    @Override
    @Transactional
    public Votacion create(Votacion votacion) {
        log.info("Creando votacion '{}' para asamblea {}", votacion.getTitulo(), votacion.getAsambleaId());
        Asamblea asamblea = asambleaRepository.findById(votacion.getAsambleaId())
                .orElseThrow(() -> new ResourceNotFoundException("Asamblea", votacion.getAsambleaId()));

        if (asamblea.getEstado() == EstadoAsamblea.CERRADA || asamblea.getEstado() == EstadoAsamblea.CANCELADA) {
            log.warn("Intento de crear votacion en asamblea {} con estado {}", asamblea.getId(), asamblea.getEstado());
            throw new BusinessException("No se pueden crear votaciones en una asamblea cerrada o cancelada");
        }

        votacion.setEstado(EstadoVotacion.PENDIENTE);
        Votacion saved = votacionRepository.save(votacion);

        if (votacion.getOpciones() != null && !votacion.getOpciones().isEmpty()) {
            List<OpcionVoto> opciones = votacion.getOpciones().stream()
                    .peek(op -> op.setVotacionId(saved.getId()))
                    .toList();
            saved.setOpciones(opcionVotoRepository.saveAll(opciones));
        }

        int numOpciones = saved.getOpciones() != null ? saved.getOpciones().size() : 0;
        log.info("Votacion creada id: {} con {} opciones", saved.getId(), numOpciones);
        return saved;
    }

    @Override
    public Votacion getById(Long id) {
        log.debug("Buscando votacion id: {}", id);
        Votacion votacion = votacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Votacion", id));
        votacion.setOpciones(opcionVotoRepository.findByVotacionId(id));
        return votacion;
    }

    @Override
    public List<Votacion> getByAsambleaId(Long asambleaId) {
        log.debug("Buscando votaciones para asamblea id: {}", asambleaId);
        List<Votacion> votaciones = votacionRepository.findByAsambleaId(asambleaId);
        votaciones.forEach(v -> v.setOpciones(opcionVotoRepository.findByVotacionId(v.getId())));
        log.debug("Encontradas {} votaciones para asamblea {}", votaciones.size(), asambleaId);
        return votaciones;
    }

    @Override
    @Transactional
    public Votacion open(Long id) {
        log.info("Abriendo votacion id: {}", id);
        Votacion votacion = getById(id);
        if (votacion.getEstado() != EstadoVotacion.PENDIENTE) {
            log.warn("Intento de abrir votacion en estado {}", votacion.getEstado());
            throw new BusinessException("Solo se pueden abrir votaciones en estado PENDIENTE");
        }
        votacion.setEstado(EstadoVotacion.ABIERTA);
        votacion.setFechaApertura(LocalDateTime.now());
        return votacionRepository.save(votacion);
    }

    @Override
    @Transactional
    public Votacion close(Long id) {
        log.info("Cerrando votacion id: {}", id);
        Votacion votacion = getById(id);
        if (votacion.getEstado() != EstadoVotacion.ABIERTA) {
            log.warn("Intento de cerrar votacion en estado {}", votacion.getEstado());
            throw new BusinessException("Solo se pueden cerrar votaciones en estado ABIERTA");
        }
        votacion.setEstado(EstadoVotacion.CERRADA);
        votacion.setFechaCierre(LocalDateTime.now());
        return votacionRepository.save(votacion);
    }
}

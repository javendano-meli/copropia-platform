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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VotacionService implements VotacionUseCase {
    private final VotacionRepository votacionRepository;
    private final AsambleaRepository asambleaRepository;
    private final OpcionVotoRepository opcionVotoRepository;

    @Override
    @Transactional
    public Votacion create(Votacion votacion) {
        Asamblea asamblea = asambleaRepository.findById(votacion.getAsambleaId())
                .orElseThrow(() -> new ResourceNotFoundException("Asamblea", votacion.getAsambleaId()));

        if (asamblea.getEstado() == EstadoAsamblea.CERRADA || asamblea.getEstado() == EstadoAsamblea.CANCELADA) {
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

        return saved;
    }

    @Override
    public Votacion getById(Long id) {
        Votacion votacion = votacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Votacion", id));
        votacion.setOpciones(opcionVotoRepository.findByVotacionId(id));
        return votacion;
    }

    @Override
    public List<Votacion> getByAsambleaId(Long asambleaId) {
        List<Votacion> votaciones = votacionRepository.findByAsambleaId(asambleaId);
        votaciones.forEach(v -> v.setOpciones(opcionVotoRepository.findByVotacionId(v.getId())));
        return votaciones;
    }

    @Override
    @Transactional
    public Votacion open(Long id) {
        Votacion votacion = getById(id);
        if (votacion.getEstado() != EstadoVotacion.PENDIENTE) {
            throw new BusinessException("Solo se pueden abrir votaciones en estado PENDIENTE");
        }
        votacion.setEstado(EstadoVotacion.ABIERTA);
        votacion.setFechaApertura(LocalDateTime.now());
        return votacionRepository.save(votacion);
    }

    @Override
    @Transactional
    public Votacion close(Long id) {
        Votacion votacion = getById(id);
        if (votacion.getEstado() != EstadoVotacion.ABIERTA) {
            throw new BusinessException("Solo se pueden cerrar votaciones en estado ABIERTA");
        }
        votacion.setEstado(EstadoVotacion.CERRADA);
        votacion.setFechaCierre(LocalDateTime.now());
        return votacionRepository.save(votacion);
    }
}

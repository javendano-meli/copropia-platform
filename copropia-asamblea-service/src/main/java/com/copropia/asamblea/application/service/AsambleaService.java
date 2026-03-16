package com.copropia.asamblea.application.service;

import com.copropia.asamblea.domain.model.Asamblea;
import com.copropia.asamblea.domain.port.in.AsambleaUseCase;
import com.copropia.asamblea.domain.port.out.AsambleaRepository;
import com.copropia.common.enums.EstadoAsamblea;
import com.copropia.common.exception.BusinessException;
import com.copropia.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AsambleaService implements AsambleaUseCase {
    private final AsambleaRepository asambleaRepository;

    @Override
    @Transactional
    public Asamblea create(Asamblea asamblea) {
        asamblea.setEstado(EstadoAsamblea.PROGRAMADA);
        asamblea.setCreatedAt(LocalDateTime.now());
        return asambleaRepository.save(asamblea);
    }

    @Override
    public Asamblea getById(Long id) {
        return asambleaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asamblea", id));
    }

    @Override
    public List<Asamblea> getByCopropiedadId(Long copropiedadId) {
        return asambleaRepository.findByCopropiedadId(copropiedadId);
    }

    @Override
    @Transactional
    public Asamblea open(Long id) {
        Asamblea asamblea = getById(id);
        if (asamblea.getEstado() != EstadoAsamblea.PROGRAMADA) {
            throw new BusinessException("Solo se pueden abrir asambleas en estado PROGRAMADA");
        }
        asamblea.setEstado(EstadoAsamblea.ABIERTA);
        return asambleaRepository.save(asamblea);
    }

    @Override
    @Transactional
    public Asamblea close(Long id) {
        Asamblea asamblea = getById(id);
        if (asamblea.getEstado() != EstadoAsamblea.ABIERTA) {
            throw new BusinessException("Solo se pueden cerrar asambleas en estado ABIERTA");
        }
        asamblea.setEstado(EstadoAsamblea.CERRADA);
        return asambleaRepository.save(asamblea);
    }
}

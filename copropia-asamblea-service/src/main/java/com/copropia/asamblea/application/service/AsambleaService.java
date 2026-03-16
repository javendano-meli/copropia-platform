package com.copropia.asamblea.application.service;

import com.copropia.asamblea.domain.model.Asamblea;
import com.copropia.asamblea.domain.port.in.AsambleaUseCase;
import com.copropia.asamblea.domain.port.out.AsambleaRepository;
import com.copropia.common.enums.EstadoAsamblea;
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
public class AsambleaService implements AsambleaUseCase {
    private final AsambleaRepository asambleaRepository;

    @Override
    @Transactional
    public Asamblea create(Asamblea asamblea) {
        log.info("Creando asamblea '{}' para copropiedad {}", asamblea.getNombre(), asamblea.getCopropiedadId());
        asamblea.setEstado(EstadoAsamblea.PROGRAMADA);
        asamblea.setCreatedAt(LocalDateTime.now());
        Asamblea saved = asambleaRepository.save(asamblea);
        log.info("Asamblea creada exitosamente id: {}", saved.getId());
        return saved;
    }

    @Override
    public Asamblea getById(Long id) {
        log.debug("Buscando asamblea id: {}", id);
        return asambleaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asamblea", id));
    }

    @Override
    public List<Asamblea> getByCopropiedadId(Long copropiedadId) {
        log.debug("Buscando asambleas para copropiedad id: {}", copropiedadId);
        List<Asamblea> result = asambleaRepository.findByCopropiedadId(copropiedadId);
        log.debug("Encontradas {} asambleas para copropiedad {}", result.size(), copropiedadId);
        return result;
    }

    @Override
    @Transactional
    public Asamblea open(Long id) {
        log.info("Abriendo asamblea id: {}", id);
        Asamblea asamblea = getById(id);
        if (asamblea.getEstado() != EstadoAsamblea.PROGRAMADA) {
            log.warn("Intento de abrir asamblea en estado {}", asamblea.getEstado());
            throw new BusinessException("Solo se pueden abrir asambleas en estado PROGRAMADA");
        }
        asamblea.setEstado(EstadoAsamblea.ABIERTA);
        return asambleaRepository.save(asamblea);
    }

    @Override
    @Transactional
    public Asamblea close(Long id) {
        log.info("Cerrando asamblea id: {}", id);
        Asamblea asamblea = getById(id);
        if (asamblea.getEstado() != EstadoAsamblea.ABIERTA) {
            log.warn("Intento de cerrar asamblea en estado {}", asamblea.getEstado());
            throw new BusinessException("Solo se pueden cerrar asambleas en estado ABIERTA");
        }
        asamblea.setEstado(EstadoAsamblea.CERRADA);
        return asambleaRepository.save(asamblea);
    }
}

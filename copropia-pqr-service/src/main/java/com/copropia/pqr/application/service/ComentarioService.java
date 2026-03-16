package com.copropia.pqr.application.service;

import com.copropia.common.exception.BusinessException;
import com.copropia.common.exception.ResourceNotFoundException;
import com.copropia.pqr.domain.model.Comentario;
import com.copropia.pqr.domain.model.Pqr;
import com.copropia.pqr.domain.port.in.ComentarioUseCase;
import com.copropia.pqr.domain.port.out.ComentarioRepository;
import com.copropia.pqr.domain.port.out.PqrRepository;
import com.copropia.common.enums.EstadoPQR;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComentarioService implements ComentarioUseCase {
    private final ComentarioRepository comentarioRepository;
    private final PqrRepository pqrRepository;

    @Override
    @Transactional
    public Comentario create(Comentario comentario) {
        log.info("Nuevo comentario en PQR {} por usuario {}", comentario.getPqrId(), comentario.getUsuarioId());
        Pqr pqr = pqrRepository.findById(comentario.getPqrId())
                .orElseThrow(() -> new ResourceNotFoundException("PQR", comentario.getPqrId()));

        if (pqr.getEstado() == EstadoPQR.CERRADO) {
            log.warn("Intento de comentar en PQR cerrada: {}", pqr.getId());
            throw new BusinessException("No se puede comentar en una PQR cerrada");
        }

        if (!pqr.isEsPublico()) {
            log.debug("Comentario en PQR privada {} - validando acceso", pqr.getId());
        }

        comentario.setCreatedAt(LocalDateTime.now());
        Comentario saved = comentarioRepository.save(comentario);
        log.info("Comentario creado id: {} en PQR {}", saved.getId(), saved.getPqrId());
        return saved;
    }

    @Override
    public List<Comentario> getByPqrId(Long pqrId) {
        log.debug("Consultando comentarios de PQR: {}", pqrId);
        return comentarioRepository.findByPqrId(pqrId);
    }
}

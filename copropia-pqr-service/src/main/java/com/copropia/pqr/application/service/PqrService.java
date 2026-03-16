package com.copropia.pqr.application.service;

import com.copropia.common.enums.EstadoPQR;
import com.copropia.common.exception.BusinessException;
import com.copropia.common.exception.ResourceNotFoundException;
import com.copropia.pqr.domain.model.Adjunto;
import com.copropia.pqr.domain.model.Pqr;
import com.copropia.pqr.domain.port.in.PqrUseCase;
import com.copropia.pqr.domain.port.out.AdjuntoRepository;
import com.copropia.pqr.domain.port.out.ComentarioRepository;
import com.copropia.pqr.domain.port.out.PqrRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PqrService implements PqrUseCase {
    private final PqrRepository pqrRepository;
    private final AdjuntoRepository adjuntoRepository;
    private final ComentarioRepository comentarioRepository;

    @Override
    @Transactional
    public Pqr create(Pqr pqr) {
        log.info("Creando PQR tipo {} - '{}' por usuario {} en copropiedad {}", pqr.getTipo(), pqr.getTitulo(), pqr.getUsuarioId(), pqr.getCopropiedadId());
        pqr.setEstado(EstadoPQR.ABIERTO);
        pqr.setCantidadComentarios(0);
        pqr.setCreatedAt(LocalDateTime.now());
        Pqr saved = pqrRepository.save(pqr);

        if (pqr.getAdjuntos() != null && !pqr.getAdjuntos().isEmpty()) {
            List<Adjunto> adjuntos = pqr.getAdjuntos().stream()
                    .peek(a -> { a.setPqrId(saved.getId()); a.setCreatedAt(LocalDateTime.now()); })
                    .toList();
            saved.setAdjuntos(adjuntoRepository.saveAll(adjuntos));
            log.info("PQR {} creada con {} adjuntos", saved.getId(), adjuntos.size());
        }

        log.info("PQR creada id: {} - visibilidad: {}", saved.getId(), pqr.isEsPublico() ? "PUBLICA" : "PRIVADA");
        return saved;
    }

    @Override
    public Pqr getById(Long id) {
        log.debug("Buscando PQR id: {}", id);
        Pqr pqr = pqrRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PQR", id));
        pqr.setAdjuntos(adjuntoRepository.findByPqrId(id));
        pqr.setCantidadComentarios(comentarioRepository.countByPqrId(id));
        return pqr;
    }

    @Override
    public List<Pqr> getFeedPublico(Long copropiedadId) {
        log.debug("Consultando feed publico copropiedad: {}", copropiedadId);
        List<Pqr> feed = pqrRepository.findByCopropiedadIdAndEsPublicoTrue(copropiedadId);
        feed.forEach(p -> {
            p.setAdjuntos(adjuntoRepository.findByPqrId(p.getId()));
            p.setCantidadComentarios(comentarioRepository.countByPqrId(p.getId()));
        });
        return feed;
    }

    @Override
    public List<Pqr> getByUsuarioId(Long usuarioId) {
        log.debug("Consultando PQRs del usuario: {}", usuarioId);
        List<Pqr> pqrs = pqrRepository.findByUsuarioId(usuarioId);
        pqrs.forEach(p -> {
            p.setAdjuntos(adjuntoRepository.findByPqrId(p.getId()));
            p.setCantidadComentarios(comentarioRepository.countByPqrId(p.getId()));
        });
        return pqrs;
    }

    @Override
    public List<Pqr> getAllByCopropiedad(Long copropiedadId) {
        log.debug("Consultando todas las PQRs de copropiedad: {} (admin)", copropiedadId);
        List<Pqr> pqrs = pqrRepository.findByCopropiedadId(copropiedadId);
        pqrs.forEach(p -> {
            p.setAdjuntos(adjuntoRepository.findByPqrId(p.getId()));
            p.setCantidadComentarios(comentarioRepository.countByPqrId(p.getId()));
        });
        return pqrs;
    }

    @Override
    @Transactional
    public Pqr cambiarEstado(Long id, EstadoPQR nuevoEstado) {
        log.info("Cambiando estado de PQR {} a {}", id, nuevoEstado);
        Pqr pqr = getById(id);
        EstadoPQR actual = pqr.getEstado();

        if (actual == EstadoPQR.CERRADO) {
            throw new BusinessException("No se puede cambiar el estado de una PQR cerrada");
        }
        if (actual == EstadoPQR.RESUELTO && nuevoEstado != EstadoPQR.CERRADO) {
            throw new BusinessException("Una PQR resuelta solo puede pasar a CERRADO");
        }

        pqr.setEstado(nuevoEstado);
        return pqrRepository.save(pqr);
    }
}

package com.copropia.asamblea.infrastructure.adapter.out.persistence;

import com.copropia.asamblea.domain.model.Votacion;
import com.copropia.asamblea.domain.port.out.VotacionRepository;
import com.copropia.asamblea.infrastructure.adapter.out.persistence.jpa.VotacionJpaRepository;
import com.copropia.asamblea.infrastructure.adapter.out.persistence.mapper.VotacionPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class VotacionPersistenceAdapter implements VotacionRepository {
    private final VotacionJpaRepository jpaRepository;
    private final VotacionPersistenceMapper mapper;

    @Override
    public Votacion save(Votacion votacion) {
        log.debug("Guardando votacion: {}", votacion.getTitulo());
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(votacion)));
    }
    @Override
    public Optional<Votacion> findById(Long id) {
        log.debug("Buscando votacion por id: {}", id);
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
    @Override
    public List<Votacion> findByAsambleaId(Long asambleaId) {
        log.debug("Buscando votaciones por asamblea id: {}", asambleaId);
        return jpaRepository.findByAsambleaIdOrderByOrdenAsc(asambleaId).stream().map(mapper::toDomain).toList();
    }
}

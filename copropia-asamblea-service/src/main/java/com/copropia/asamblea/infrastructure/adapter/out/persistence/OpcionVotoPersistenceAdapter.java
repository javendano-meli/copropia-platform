package com.copropia.asamblea.infrastructure.adapter.out.persistence;

import com.copropia.asamblea.domain.model.OpcionVoto;
import com.copropia.asamblea.domain.port.out.OpcionVotoRepository;
import com.copropia.asamblea.infrastructure.adapter.out.persistence.jpa.OpcionVotoJpaRepository;
import com.copropia.asamblea.infrastructure.adapter.out.persistence.mapper.OpcionVotoPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OpcionVotoPersistenceAdapter implements OpcionVotoRepository {
    private final OpcionVotoJpaRepository jpaRepository;
    private final OpcionVotoPersistenceMapper mapper;

    @Override
    public OpcionVoto save(OpcionVoto opcion) {
        log.debug("Guardando opcion de voto: {}", opcion.getNombre());
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(opcion)));
    }
    @Override
    public List<OpcionVoto> saveAll(List<OpcionVoto> opciones) {
        log.debug("Guardando {} opciones de voto", opciones.size());
        return jpaRepository.saveAll(opciones.stream().map(mapper::toEntity).toList())
                .stream().map(mapper::toDomain).toList();
    }
    @Override
    public Optional<OpcionVoto> findById(Long id) {
        log.debug("Buscando opcion de voto por id: {}", id);
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
    @Override
    public List<OpcionVoto> findByVotacionId(Long votacionId) {
        log.debug("Buscando opciones de voto por votacion id: {}", votacionId);
        return jpaRepository.findByVotacionIdOrderByOrdenAsc(votacionId).stream().map(mapper::toDomain).toList();
    }
}

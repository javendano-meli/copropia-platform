package com.copropia.asamblea.infrastructure.adapter.out.persistence;

import com.copropia.asamblea.domain.model.Voto;
import com.copropia.asamblea.domain.port.out.VotoRepository;
import com.copropia.asamblea.infrastructure.adapter.out.persistence.jpa.VotoJpaRepository;
import com.copropia.asamblea.infrastructure.adapter.out.persistence.mapper.VotoPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class VotoPersistenceAdapter implements VotoRepository {
    private final VotoJpaRepository jpaRepository;
    private final VotoPersistenceMapper mapper;

    @Override
    public Voto save(Voto voto) {
        log.debug("Guardando voto para votacion: {}, propiedad: {}", voto.getVotacionId(), voto.getPropiedadId());
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(voto)));
    }
    @Override
    public List<Voto> findByVotacionId(Long votacionId) {
        log.debug("Buscando votos por votacion id: {}", votacionId);
        return jpaRepository.findByVotacionId(votacionId).stream().map(mapper::toDomain).toList();
    }
    @Override
    public boolean existsByVotacionIdAndPropiedadId(Long votacionId, Long propiedadId) {
        log.debug("Verificando existencia de voto - votacion: {}, propiedad: {}", votacionId, propiedadId);
        return jpaRepository.existsByVotacionIdAndPropiedadId(votacionId, propiedadId);
    }
}

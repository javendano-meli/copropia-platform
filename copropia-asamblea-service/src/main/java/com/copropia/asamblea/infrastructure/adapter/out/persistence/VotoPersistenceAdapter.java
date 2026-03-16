package com.copropia.asamblea.infrastructure.adapter.out.persistence;

import com.copropia.asamblea.domain.model.Voto;
import com.copropia.asamblea.domain.port.out.VotoRepository;
import com.copropia.asamblea.infrastructure.adapter.out.persistence.jpa.VotoJpaRepository;
import com.copropia.asamblea.infrastructure.adapter.out.persistence.mapper.VotoPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class VotoPersistenceAdapter implements VotoRepository {
    private final VotoJpaRepository jpaRepository;
    private final VotoPersistenceMapper mapper;

    @Override
    public Voto save(Voto voto) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(voto)));
    }
    @Override
    public List<Voto> findByVotacionId(Long votacionId) {
        return jpaRepository.findByVotacionId(votacionId).stream().map(mapper::toDomain).toList();
    }
    @Override
    public boolean existsByVotacionIdAndPropiedadId(Long votacionId, Long propiedadId) {
        return jpaRepository.existsByVotacionIdAndPropiedadId(votacionId, propiedadId);
    }
}

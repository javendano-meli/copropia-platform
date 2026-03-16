package com.copropia.pqr.infrastructure.adapter.out.persistence;

import com.copropia.pqr.domain.model.Adjunto;
import com.copropia.pqr.domain.port.out.AdjuntoRepository;
import com.copropia.pqr.infrastructure.adapter.out.persistence.jpa.AdjuntoJpaRepository;
import com.copropia.pqr.infrastructure.adapter.out.persistence.mapper.AdjuntoPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.util.List;

@Slf4j @Repository @RequiredArgsConstructor
public class AdjuntoPersistenceAdapter implements AdjuntoRepository {
    private final AdjuntoJpaRepository jpaRepository;
    private final AdjuntoPersistenceMapper mapper;

    @Override public Adjunto save(Adjunto a) { return mapper.toDomain(jpaRepository.save(mapper.toEntity(a))); }
    @Override public List<Adjunto> saveAll(List<Adjunto> adjuntos) { return jpaRepository.saveAll(adjuntos.stream().map(mapper::toEntity).toList()).stream().map(mapper::toDomain).toList(); }
    @Override public List<Adjunto> findByPqrId(Long pqrId) { return jpaRepository.findByPqrId(pqrId).stream().map(mapper::toDomain).toList(); }
}

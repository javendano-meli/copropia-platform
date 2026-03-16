package com.copropia.pqr.infrastructure.adapter.out.persistence;

import com.copropia.pqr.domain.model.Comentario;
import com.copropia.pqr.domain.port.out.ComentarioRepository;
import com.copropia.pqr.infrastructure.adapter.out.persistence.jpa.ComentarioJpaRepository;
import com.copropia.pqr.infrastructure.adapter.out.persistence.mapper.ComentarioPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.util.List;

@Slf4j @Repository @RequiredArgsConstructor
public class ComentarioPersistenceAdapter implements ComentarioRepository {
    private final ComentarioJpaRepository jpaRepository;
    private final ComentarioPersistenceMapper mapper;

    @Override public Comentario save(Comentario c) { log.debug("Guardando comentario en PQR: {}", c.getPqrId()); return mapper.toDomain(jpaRepository.save(mapper.toEntity(c))); }
    @Override public List<Comentario> findByPqrId(Long pqrId) { return jpaRepository.findByPqrIdOrderByCreatedAtAsc(pqrId).stream().map(mapper::toDomain).toList(); }
    @Override public int countByPqrId(Long pqrId) { return jpaRepository.countByPqrId(pqrId); }
}

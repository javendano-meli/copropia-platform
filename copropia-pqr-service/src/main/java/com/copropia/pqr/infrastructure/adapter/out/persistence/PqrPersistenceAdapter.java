package com.copropia.pqr.infrastructure.adapter.out.persistence;

import com.copropia.pqr.domain.model.Pqr;
import com.copropia.pqr.domain.port.out.PqrRepository;
import com.copropia.pqr.infrastructure.adapter.out.persistence.jpa.PqrJpaRepository;
import com.copropia.pqr.infrastructure.adapter.out.persistence.mapper.PqrPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Slf4j @Repository @RequiredArgsConstructor
public class PqrPersistenceAdapter implements PqrRepository {
    private final PqrJpaRepository jpaRepository;
    private final PqrPersistenceMapper mapper;

    @Override public Pqr save(Pqr pqr) { log.debug("Guardando PQR: {}", pqr.getTitulo()); return mapper.toDomain(jpaRepository.save(mapper.toEntity(pqr))); }
    @Override public Optional<Pqr> findById(Long id) { return jpaRepository.findById(id).map(mapper::toDomain); }
    @Override public List<Pqr> findByCopropiedadIdAndEsPublicoTrue(Long copropiedadId) { return jpaRepository.findByCopropiedadIdAndEsPublicoTrueOrderByCreatedAtDesc(copropiedadId).stream().map(mapper::toDomain).toList(); }
    @Override public List<Pqr> findByUsuarioId(Long usuarioId) { return jpaRepository.findByUsuarioIdOrderByCreatedAtDesc(usuarioId).stream().map(mapper::toDomain).toList(); }
    @Override public List<Pqr> findByCopropiedadId(Long copropiedadId) { return jpaRepository.findByCopropiedadIdOrderByCreatedAtDesc(copropiedadId).stream().map(mapper::toDomain).toList(); }
}

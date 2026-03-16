package com.copropia.asamblea.infrastructure.adapter.out.persistence;

import com.copropia.asamblea.domain.model.Asamblea;
import com.copropia.asamblea.domain.port.out.AsambleaRepository;
import com.copropia.asamblea.infrastructure.adapter.out.persistence.jpa.AsambleaJpaRepository;
import com.copropia.asamblea.infrastructure.adapter.out.persistence.mapper.AsambleaPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AsambleaPersistenceAdapter implements AsambleaRepository {
    private final AsambleaJpaRepository jpaRepository;
    private final AsambleaPersistenceMapper mapper;

    @Override
    public Asamblea save(Asamblea asamblea) {
        log.debug("Guardando asamblea: {}", asamblea.getNombre());
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(asamblea)));
    }
    @Override
    public Optional<Asamblea> findById(Long id) {
        log.debug("Buscando asamblea por id: {}", id);
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
    @Override
    public List<Asamblea> findByCopropiedadId(Long copropiedadId) {
        log.debug("Buscando asambleas por copropiedad id: {}", copropiedadId);
        return jpaRepository.findByCopropiedadId(copropiedadId).stream().map(mapper::toDomain).toList();
    }
}

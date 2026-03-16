package com.copropia.copropiedad.infrastructure.adapter.out.persistence;

import com.copropia.copropiedad.domain.model.Copropiedad;
import com.copropia.copropiedad.domain.port.out.CopropiedadRepository;
import com.copropia.copropiedad.infrastructure.adapter.out.persistence.jpa.CopropiedadJpaRepository;
import com.copropia.copropiedad.infrastructure.adapter.out.persistence.mapper.CopropiedadPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CopropiedadPersistenceAdapter implements CopropiedadRepository {
    private final CopropiedadJpaRepository jpaRepository;
    private final CopropiedadPersistenceMapper mapper;

    @Override
    public Copropiedad save(Copropiedad copropiedad) {
        log.debug("Persistiendo copropiedad: {}", copropiedad.getNombre());
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(copropiedad)));
    }
    @Override
    public Optional<Copropiedad> findById(Long id) {
        log.debug("Buscando copropiedad en BD con id: {}", id);
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
    @Override
    public List<Copropiedad> findAll() {
        log.debug("Consultando todas las copropiedades en BD");
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }
    @Override
    public boolean existsByNit(String nit) {
        log.debug("Verificando existencia de NIT: {}", nit);
        return jpaRepository.existsByNit(nit);
    }
}

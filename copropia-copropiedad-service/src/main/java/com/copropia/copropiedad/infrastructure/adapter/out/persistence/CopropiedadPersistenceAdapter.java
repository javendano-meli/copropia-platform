package com.copropia.copropiedad.infrastructure.adapter.out.persistence;

import com.copropia.copropiedad.domain.model.Copropiedad;
import com.copropia.copropiedad.domain.port.out.CopropiedadRepository;
import com.copropia.copropiedad.infrastructure.adapter.out.persistence.jpa.CopropiedadJpaRepository;
import com.copropia.copropiedad.infrastructure.adapter.out.persistence.mapper.CopropiedadPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CopropiedadPersistenceAdapter implements CopropiedadRepository {
    private final CopropiedadJpaRepository jpaRepository;
    private final CopropiedadPersistenceMapper mapper;

    @Override
    public Copropiedad save(Copropiedad copropiedad) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(copropiedad)));
    }
    @Override
    public Optional<Copropiedad> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
    @Override
    public List<Copropiedad> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }
    @Override
    public boolean existsByNit(String nit) {
        return jpaRepository.existsByNit(nit);
    }
}

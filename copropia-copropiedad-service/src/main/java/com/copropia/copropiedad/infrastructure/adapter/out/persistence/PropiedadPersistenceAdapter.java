package com.copropia.copropiedad.infrastructure.adapter.out.persistence;

import com.copropia.copropiedad.domain.model.Propiedad;
import com.copropia.copropiedad.domain.port.out.PropiedadRepository;
import com.copropia.copropiedad.infrastructure.adapter.out.persistence.jpa.PropiedadJpaRepository;
import com.copropia.copropiedad.infrastructure.adapter.out.persistence.mapper.PropiedadPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PropiedadPersistenceAdapter implements PropiedadRepository {
    private final PropiedadJpaRepository jpaRepository;
    private final PropiedadPersistenceMapper mapper;

    @Override
    public Propiedad save(Propiedad propiedad) {
        log.debug("Persistiendo propiedad: {}", propiedad.getIdentificacion());
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(propiedad)));
    }
    @Override
    public Optional<Propiedad> findById(Long id) {
        log.debug("Buscando propiedad en BD con id: {}", id);
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
    @Override
    public List<Propiedad> findByCopropiedadId(Long copropiedadId) {
        log.debug("Buscando propiedades en BD por copropiedad id: {}", copropiedadId);
        return jpaRepository.findByCopropiedadId(copropiedadId).stream().map(mapper::toDomain).toList();
    }
    @Override
    public List<Propiedad> findByPropietarioId(Long propietarioId) {
        log.debug("Buscando propiedades en BD por propietario id: {}", propietarioId);
        return jpaRepository.findByPropietarioId(propietarioId).stream().map(mapper::toDomain).toList();
    }
    @Override
    public boolean existsByCopropiedadIdAndIdentificacion(Long copropiedadId, String identificacion) {
        log.debug("Verificando existencia de propiedad {} en copropiedad {}", identificacion, copropiedadId);
        return jpaRepository.existsByCopropiedadIdAndIdentificacion(copropiedadId, identificacion);
    }
    @Override
    public BigDecimal sumCoeficienteByCopropiedadId(Long copropiedadId) {
        log.debug("Calculando suma de coeficientes para copropiedad id: {}", copropiedadId);
        return jpaRepository.sumCoeficienteByCopropiedadId(copropiedadId);
    }
}

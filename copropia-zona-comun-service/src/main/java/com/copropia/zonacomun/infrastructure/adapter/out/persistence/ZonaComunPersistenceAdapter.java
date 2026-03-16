package com.copropia.zonacomun.infrastructure.adapter.out.persistence;

import com.copropia.zonacomun.domain.model.ZonaComun;
import com.copropia.zonacomun.domain.port.out.ZonaComunRepository;
import com.copropia.zonacomun.infrastructure.adapter.out.persistence.jpa.ZonaComunJpaRepository;
import com.copropia.zonacomun.infrastructure.adapter.out.persistence.mapper.ZonaComunPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ZonaComunPersistenceAdapter implements ZonaComunRepository {
    private final ZonaComunJpaRepository jpaRepository;
    private final ZonaComunPersistenceMapper mapper;

    @Override
    public ZonaComun save(ZonaComun zonaComun) {
        log.debug("Guardando zona comun: {}", zonaComun.getNombre());
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(zonaComun)));
    }
    @Override
    public Optional<ZonaComun> findById(Long id) {
        log.debug("Buscando zona comun id: {}", id);
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
    @Override
    public List<ZonaComun> findByCopropiedadId(Long copropiedadId) {
        log.debug("Buscando zonas comunes de copropiedad: {}", copropiedadId);
        return jpaRepository.findByCopropiedadIdAndActivaTrue(copropiedadId).stream().map(mapper::toDomain).toList();
    }
    @Override
    public boolean existsByCopropiedadIdAndNombre(Long copropiedadId, String nombre) {
        return jpaRepository.existsByCopropiedadIdAndNombre(copropiedadId, nombre);
    }
}

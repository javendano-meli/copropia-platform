package com.copropia.copropiedad.infrastructure.adapter.out.persistence.mapper;

import com.copropia.copropiedad.domain.model.Propiedad;
import com.copropia.copropiedad.infrastructure.adapter.out.persistence.entity.PropiedadEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PropiedadPersistenceMapper {
    Propiedad toDomain(PropiedadEntity entity);
    PropiedadEntity toEntity(Propiedad domain);
}

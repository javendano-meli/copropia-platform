package com.copropia.copropiedad.infrastructure.adapter.out.persistence.mapper;

import com.copropia.copropiedad.domain.model.Copropiedad;
import com.copropia.copropiedad.infrastructure.adapter.out.persistence.entity.CopropiedadEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CopropiedadPersistenceMapper {
    Copropiedad toDomain(CopropiedadEntity entity);
    CopropiedadEntity toEntity(Copropiedad domain);
}

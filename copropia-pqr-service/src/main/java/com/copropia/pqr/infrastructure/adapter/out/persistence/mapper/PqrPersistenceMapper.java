package com.copropia.pqr.infrastructure.adapter.out.persistence.mapper;

import com.copropia.pqr.domain.model.Pqr;
import com.copropia.pqr.infrastructure.adapter.out.persistence.entity.PqrEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PqrPersistenceMapper {
    @Mapping(target = "adjuntos", ignore = true)
    @Mapping(target = "cantidadComentarios", ignore = true)
    Pqr toDomain(PqrEntity entity);
    PqrEntity toEntity(Pqr domain);
}

package com.copropia.pqr.infrastructure.adapter.out.persistence.mapper;

import com.copropia.pqr.domain.model.Comentario;
import com.copropia.pqr.infrastructure.adapter.out.persistence.entity.ComentarioEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ComentarioPersistenceMapper {
    Comentario toDomain(ComentarioEntity entity);
    ComentarioEntity toEntity(Comentario domain);
}

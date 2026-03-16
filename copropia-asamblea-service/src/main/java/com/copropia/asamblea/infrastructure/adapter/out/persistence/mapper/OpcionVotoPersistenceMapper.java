package com.copropia.asamblea.infrastructure.adapter.out.persistence.mapper;

import com.copropia.asamblea.domain.model.OpcionVoto;
import com.copropia.asamblea.infrastructure.adapter.out.persistence.entity.OpcionVotoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OpcionVotoPersistenceMapper {
    OpcionVoto toDomain(OpcionVotoEntity entity);
    OpcionVotoEntity toEntity(OpcionVoto domain);
}

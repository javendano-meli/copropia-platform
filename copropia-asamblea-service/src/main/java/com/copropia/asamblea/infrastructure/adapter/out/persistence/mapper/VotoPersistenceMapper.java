package com.copropia.asamblea.infrastructure.adapter.out.persistence.mapper;

import com.copropia.asamblea.domain.model.Voto;
import com.copropia.asamblea.infrastructure.adapter.out.persistence.entity.VotoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VotoPersistenceMapper {
    Voto toDomain(VotoEntity entity);
    VotoEntity toEntity(Voto domain);
}

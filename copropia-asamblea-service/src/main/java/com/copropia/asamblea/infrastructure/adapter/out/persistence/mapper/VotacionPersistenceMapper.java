package com.copropia.asamblea.infrastructure.adapter.out.persistence.mapper;

import com.copropia.asamblea.domain.model.Votacion;
import com.copropia.asamblea.infrastructure.adapter.out.persistence.entity.VotacionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VotacionPersistenceMapper {
    @Mapping(target = "opciones", ignore = true)
    Votacion toDomain(VotacionEntity entity);
    VotacionEntity toEntity(Votacion domain);
}

package com.copropia.asamblea.infrastructure.adapter.out.persistence.mapper;

import com.copropia.asamblea.domain.model.Asamblea;
import com.copropia.asamblea.infrastructure.adapter.out.persistence.entity.AsambleaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AsambleaPersistenceMapper {
    Asamblea toDomain(AsambleaEntity entity);
    AsambleaEntity toEntity(Asamblea domain);
}

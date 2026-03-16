package com.copropia.zonacomun.infrastructure.adapter.out.persistence.mapper;

import com.copropia.zonacomun.domain.model.ZonaComun;
import com.copropia.zonacomun.infrastructure.adapter.out.persistence.entity.ZonaComunEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ZonaComunPersistenceMapper {
    ZonaComun toDomain(ZonaComunEntity entity);
    ZonaComunEntity toEntity(ZonaComun domain);
}

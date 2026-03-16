package com.copropia.pqr.infrastructure.adapter.out.persistence.mapper;

import com.copropia.pqr.domain.model.Adjunto;
import com.copropia.pqr.infrastructure.adapter.out.persistence.entity.AdjuntoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdjuntoPersistenceMapper {
    Adjunto toDomain(AdjuntoEntity entity);
    AdjuntoEntity toEntity(Adjunto domain);
}

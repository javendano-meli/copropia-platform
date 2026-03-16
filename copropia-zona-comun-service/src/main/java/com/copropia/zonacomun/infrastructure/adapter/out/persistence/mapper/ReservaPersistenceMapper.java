package com.copropia.zonacomun.infrastructure.adapter.out.persistence.mapper;

import com.copropia.zonacomun.domain.model.Reserva;
import com.copropia.zonacomun.infrastructure.adapter.out.persistence.entity.ReservaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReservaPersistenceMapper {
    Reserva toDomain(ReservaEntity entity);
    ReservaEntity toEntity(Reserva domain);
}

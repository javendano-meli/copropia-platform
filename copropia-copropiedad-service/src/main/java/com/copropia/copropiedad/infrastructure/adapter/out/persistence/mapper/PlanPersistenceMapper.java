package com.copropia.copropiedad.infrastructure.adapter.out.persistence.mapper;

import com.copropia.copropiedad.domain.model.Plan;
import com.copropia.copropiedad.infrastructure.adapter.out.persistence.entity.PlanEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlanPersistenceMapper {
    Plan toDomain(PlanEntity entity);
    PlanEntity toEntity(Plan domain);
}

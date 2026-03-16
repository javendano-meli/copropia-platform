package com.copropia.copropiedad.domain.port.in;

import com.copropia.copropiedad.domain.model.Plan;
import java.util.List;

public interface PlanUseCase {
    Plan create(Plan plan);
    Plan getById(Long id);
    List<Plan> getAll();
    Plan update(Long id, Plan plan);
}

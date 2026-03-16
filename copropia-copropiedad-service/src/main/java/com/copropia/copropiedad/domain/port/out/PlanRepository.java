package com.copropia.copropiedad.domain.port.out;

import com.copropia.copropiedad.domain.model.Plan;
import java.util.List;
import java.util.Optional;

public interface PlanRepository {
    Plan save(Plan plan);
    Optional<Plan> findById(Long id);
    List<Plan> findAll();
}

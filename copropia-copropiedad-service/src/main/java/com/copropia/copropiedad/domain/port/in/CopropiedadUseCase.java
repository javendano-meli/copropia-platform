package com.copropia.copropiedad.domain.port.in;

import com.copropia.copropiedad.domain.model.Copropiedad;
import java.util.List;

public interface CopropiedadUseCase {
    Copropiedad create(Copropiedad copropiedad);
    Copropiedad getById(Long id);
    List<Copropiedad> getAll();
    Copropiedad update(Long id, Copropiedad copropiedad);
    void deactivate(Long id);
}

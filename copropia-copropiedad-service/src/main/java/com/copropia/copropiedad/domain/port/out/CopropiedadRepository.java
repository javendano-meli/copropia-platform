package com.copropia.copropiedad.domain.port.out;

import com.copropia.copropiedad.domain.model.Copropiedad;
import java.util.List;
import java.util.Optional;

public interface CopropiedadRepository {
    Copropiedad save(Copropiedad copropiedad);
    Optional<Copropiedad> findById(Long id);
    List<Copropiedad> findAll();
    boolean existsByNit(String nit);
}

package com.copropia.copropiedad.domain.port.out;

import com.copropia.copropiedad.domain.model.Propiedad;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PropiedadRepository {
    Propiedad save(Propiedad propiedad);
    Optional<Propiedad> findById(Long id);
    List<Propiedad> findByCopropiedadId(Long copropiedadId);
    List<Propiedad> findByPropietarioId(Long propietarioId);
    boolean existsByCopropiedadIdAndIdentificacion(Long copropiedadId, String identificacion);
    BigDecimal sumCoeficienteByCopropiedadId(Long copropiedadId);
}

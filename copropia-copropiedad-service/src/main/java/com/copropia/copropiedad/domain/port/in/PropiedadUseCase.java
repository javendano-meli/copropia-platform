package com.copropia.copropiedad.domain.port.in;

import com.copropia.copropiedad.domain.model.Propiedad;
import java.math.BigDecimal;
import java.util.List;

public interface PropiedadUseCase {
    Propiedad create(Propiedad propiedad);
    Propiedad getById(Long id);
    List<Propiedad> getByCopropiedadId(Long copropiedadId);
    List<Propiedad> getByPropietarioId(Long propietarioId);
    Propiedad update(Long id, Propiedad propiedad);
    BigDecimal getTotalCoeficiente(Long copropiedadId);
    void validateCoeficientes(Long copropiedadId);
}

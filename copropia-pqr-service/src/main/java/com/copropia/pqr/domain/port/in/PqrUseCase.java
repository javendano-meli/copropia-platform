package com.copropia.pqr.domain.port.in;

import com.copropia.common.enums.EstadoPQR;
import com.copropia.pqr.domain.model.Pqr;
import java.util.List;

public interface PqrUseCase {
    Pqr create(Pqr pqr);
    Pqr getById(Long id);
    List<Pqr> getFeedPublico(Long copropiedadId);
    List<Pqr> getByUsuarioId(Long usuarioId);
    List<Pqr> getAllByCopropiedad(Long copropiedadId);
    Pqr cambiarEstado(Long id, EstadoPQR nuevoEstado);
}

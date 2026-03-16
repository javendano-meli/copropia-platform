package com.copropia.pqr.domain.port.out;

import com.copropia.pqr.domain.model.Pqr;
import java.util.List;
import java.util.Optional;

public interface PqrRepository {
    Pqr save(Pqr pqr);
    Optional<Pqr> findById(Long id);
    List<Pqr> findByCopropiedadIdAndEsPublicoTrue(Long copropiedadId);
    List<Pqr> findByUsuarioId(Long usuarioId);
    List<Pqr> findByCopropiedadId(Long copropiedadId);
}

package com.copropia.asamblea.domain.port.out;

import com.copropia.asamblea.domain.model.Asamblea;
import java.util.List;
import java.util.Optional;

public interface AsambleaRepository {
    Asamblea save(Asamblea asamblea);
    Optional<Asamblea> findById(Long id);
    List<Asamblea> findByCopropiedadId(Long copropiedadId);
}

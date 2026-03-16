package com.copropia.asamblea.domain.port.in;

import com.copropia.asamblea.domain.model.Asamblea;
import java.util.List;

public interface AsambleaUseCase {
    Asamblea create(Asamblea asamblea);
    Asamblea getById(Long id);
    List<Asamblea> getByCopropiedadId(Long copropiedadId);
    Asamblea open(Long id);
    Asamblea close(Long id);
}

package com.copropia.asamblea.domain.port.in;

import com.copropia.asamblea.domain.model.Votacion;
import java.util.List;

public interface VotacionUseCase {
    Votacion create(Votacion votacion);
    Votacion getById(Long id);
    List<Votacion> getByAsambleaId(Long asambleaId);
    Votacion open(Long id);
    Votacion close(Long id);
}

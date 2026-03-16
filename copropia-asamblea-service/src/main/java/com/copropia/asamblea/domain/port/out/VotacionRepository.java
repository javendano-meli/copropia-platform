package com.copropia.asamblea.domain.port.out;

import com.copropia.asamblea.domain.model.Votacion;
import java.util.List;
import java.util.Optional;

public interface VotacionRepository {
    Votacion save(Votacion votacion);
    Optional<Votacion> findById(Long id);
    List<Votacion> findByAsambleaId(Long asambleaId);
}

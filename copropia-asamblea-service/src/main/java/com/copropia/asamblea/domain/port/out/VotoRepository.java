package com.copropia.asamblea.domain.port.out;

import com.copropia.asamblea.domain.model.Voto;
import java.util.List;

public interface VotoRepository {
    Voto save(Voto voto);
    List<Voto> findByVotacionId(Long votacionId);
    boolean existsByVotacionIdAndPropiedadId(Long votacionId, Long propiedadId);
}

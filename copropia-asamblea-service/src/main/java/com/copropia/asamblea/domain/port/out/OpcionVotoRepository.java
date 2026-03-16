package com.copropia.asamblea.domain.port.out;

import com.copropia.asamblea.domain.model.OpcionVoto;
import java.util.List;
import java.util.Optional;

public interface OpcionVotoRepository {
    OpcionVoto save(OpcionVoto opcion);
    List<OpcionVoto> saveAll(List<OpcionVoto> opciones);
    Optional<OpcionVoto> findById(Long id);
    List<OpcionVoto> findByVotacionId(Long votacionId);
}

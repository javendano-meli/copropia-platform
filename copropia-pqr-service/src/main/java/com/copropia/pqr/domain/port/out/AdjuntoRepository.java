package com.copropia.pqr.domain.port.out;

import com.copropia.pqr.domain.model.Adjunto;
import java.util.List;

public interface AdjuntoRepository {
    Adjunto save(Adjunto adjunto);
    List<Adjunto> saveAll(List<Adjunto> adjuntos);
    List<Adjunto> findByPqrId(Long pqrId);
}

package com.copropia.asamblea.domain.port.in;

import com.copropia.asamblea.domain.model.ResultadoVotacion;
import com.copropia.asamblea.domain.model.Voto;

public interface VotoUseCase {
    Voto emitirVoto(Voto voto);
    ResultadoVotacion getResultados(Long votacionId, Long copropiedadId);
}

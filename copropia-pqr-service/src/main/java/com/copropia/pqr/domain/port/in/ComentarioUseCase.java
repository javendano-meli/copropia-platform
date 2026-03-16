package com.copropia.pqr.domain.port.in;

import com.copropia.pqr.domain.model.Comentario;
import java.util.List;

public interface ComentarioUseCase {
    Comentario create(Comentario comentario);
    List<Comentario> getByPqrId(Long pqrId);
}

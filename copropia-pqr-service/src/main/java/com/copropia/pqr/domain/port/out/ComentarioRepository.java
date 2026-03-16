package com.copropia.pqr.domain.port.out;

import com.copropia.pqr.domain.model.Comentario;
import java.util.List;

public interface ComentarioRepository {
    Comentario save(Comentario comentario);
    List<Comentario> findByPqrId(Long pqrId);
    int countByPqrId(Long pqrId);
}

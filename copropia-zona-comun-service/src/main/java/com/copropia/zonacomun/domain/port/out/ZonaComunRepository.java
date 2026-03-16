package com.copropia.zonacomun.domain.port.out;

import com.copropia.zonacomun.domain.model.ZonaComun;
import java.util.List;
import java.util.Optional;

public interface ZonaComunRepository {
    ZonaComun save(ZonaComun zonaComun);
    Optional<ZonaComun> findById(Long id);
    List<ZonaComun> findByCopropiedadId(Long copropiedadId);
    boolean existsByCopropiedadIdAndNombre(Long copropiedadId, String nombre);
}

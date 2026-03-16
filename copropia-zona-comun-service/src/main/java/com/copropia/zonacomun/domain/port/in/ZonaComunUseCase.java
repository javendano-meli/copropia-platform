package com.copropia.zonacomun.domain.port.in;

import com.copropia.zonacomun.domain.model.ZonaComun;
import java.util.List;

public interface ZonaComunUseCase {
    ZonaComun create(ZonaComun zonaComun);
    ZonaComun getById(Long id);
    List<ZonaComun> getByCopropiedadId(Long copropiedadId);
    ZonaComun update(Long id, ZonaComun zonaComun);
    void deactivate(Long id);
}

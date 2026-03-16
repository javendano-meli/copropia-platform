package com.copropia.copropiedad.application.service;

import com.copropia.common.enums.EstadoCopropiedad;
import com.copropia.copropiedad.domain.model.Copropiedad;
import com.copropia.copropiedad.domain.port.in.CopropiedadUseCase;
import com.copropia.copropiedad.domain.port.out.CopropiedadRepository;
import com.copropia.copropiedad.domain.port.out.PlanRepository;
import com.copropia.common.exception.BusinessException;
import com.copropia.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CopropiedadService implements CopropiedadUseCase {

    private final CopropiedadRepository copropiedadRepository;
    private final PlanRepository planRepository;

    @Override
    @Transactional
    public Copropiedad create(Copropiedad copropiedad) {
        if (copropiedadRepository.existsByNit(copropiedad.getNit())) {
            throw new BusinessException("Ya existe una copropiedad con NIT: " + copropiedad.getNit());
        }
        planRepository.findById(copropiedad.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan", copropiedad.getPlanId()));

        copropiedad.setEstado(EstadoCopropiedad.ACTIVA);
        copropiedad.setCreatedAt(LocalDateTime.now());
        return copropiedadRepository.save(copropiedad);
    }

    @Override
    public Copropiedad getById(Long id) {
        return copropiedadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Copropiedad", id));
    }

    @Override
    public List<Copropiedad> getAll() {
        return copropiedadRepository.findAll();
    }

    @Override
    @Transactional
    public Copropiedad update(Long id, Copropiedad updated) {
        Copropiedad existing = getById(id);
        existing.setNombre(updated.getNombre());
        existing.setDireccion(updated.getDireccion());
        existing.setCiudad(updated.getCiudad());
        existing.setDepartamento(updated.getDepartamento());
        existing.setTelefono(updated.getTelefono());
        existing.setEmail(updated.getEmail());
        return copropiedadRepository.save(existing);
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        Copropiedad copropiedad = getById(id);
        copropiedad.setEstado(EstadoCopropiedad.INACTIVA);
        copropiedadRepository.save(copropiedad);
    }
}

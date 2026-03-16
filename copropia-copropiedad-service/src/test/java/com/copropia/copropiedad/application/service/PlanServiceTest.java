package com.copropia.copropiedad.application.service;

import com.copropia.copropiedad.domain.model.Plan;
import com.copropia.copropiedad.domain.port.out.PlanRepository;
import com.copropia.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanServiceTest {

    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private PlanService planService;

    @Test
    @DisplayName("Debe crear plan con estado activo")
    void create_planValido_creaConEstadoActivo() {
        Plan plan = Plan.builder().nombre("Basico").precio(new BigDecimal("50000")).maxCopropiedades(1).maxUsuarios(50).build();
        when(planRepository.save(any(Plan.class))).thenAnswer(inv -> {
            Plan p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        Plan result = planService.create(plan);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEstado()).isEqualTo("activo");
        assertThat(result.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Debe obtener plan por ID")
    void getById_existente_retornaPlan() {
        Plan plan = Plan.builder().id(1L).nombre("Basico").build();
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));

        Plan result = planService.getById(1L);

        assertThat(result.getNombre()).isEqualTo("Basico");
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando plan no existe")
    void getById_noExiste_lanzaExcepcion() {
        when(planRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> planService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Debe retornar todos los planes")
    void getAll_retornaLista() {
        when(planRepository.findAll()).thenReturn(List.of(
                Plan.builder().id(1L).nombre("Basico").build(),
                Plan.builder().id(2L).nombre("Premium").build()
        ));

        List<Plan> result = planService.getAll();

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Debe actualizar plan existente")
    void update_planExistente_actualizaDatos() {
        Plan existing = Plan.builder().id(1L).nombre("Basico").descripcion("Plan basico")
                .maxCopropiedades(1).maxUsuarios(50).precio(new BigDecimal("50000")).estado("activo").build();
        Plan updated = Plan.builder().nombre("Basico Plus").descripcion("Plan mejorado")
                .maxCopropiedades(3).maxUsuarios(100).precio(new BigDecimal("75000")).build();

        when(planRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(planRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Plan result = planService.update(1L, updated);

        assertThat(result.getNombre()).isEqualTo("Basico Plus");
        assertThat(result.getMaxCopropiedades()).isEqualTo(3);
        assertThat(result.getPrecio()).isEqualByComparingTo(new BigDecimal("75000"));
    }
}

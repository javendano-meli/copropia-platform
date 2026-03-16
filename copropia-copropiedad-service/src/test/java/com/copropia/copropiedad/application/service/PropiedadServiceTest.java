package com.copropia.copropiedad.application.service;

import com.copropia.common.enums.TipoPropiedad;
import com.copropia.copropiedad.domain.model.Propiedad;
import com.copropia.copropiedad.domain.port.out.PropiedadRepository;
import com.copropia.common.exception.BusinessException;
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
class PropiedadServiceTest {

    @Mock
    private PropiedadRepository propiedadRepository;

    @InjectMocks
    private PropiedadService propiedadService;

    @Test
    @DisplayName("Debe crear propiedad exitosamente")
    void create_datosValidos_creaPropiedad() {
        Propiedad nueva = Propiedad.builder()
                .copropiedadId(1L).identificacion("Apto 101")
                .tipo(TipoPropiedad.APARTAMENTO).coeficiente(new BigDecimal("2.5000"))
                .metrosCuadrados(new BigDecimal("75.00")).build();

        when(propiedadRepository.existsByCopropiedadIdAndIdentificacion(1L, "Apto 101")).thenReturn(false);
        when(propiedadRepository.save(any())).thenAnswer(inv -> {
            Propiedad p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        Propiedad result = propiedadService.create(nueva);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEstado()).isEqualTo("activa");
    }

    @Test
    @DisplayName("Debe rechazar propiedad duplicada en misma copropiedad")
    void create_identificacionDuplicada_lanzaExcepcion() {
        Propiedad nueva = Propiedad.builder().copropiedadId(1L).identificacion("Apto 101").build();
        when(propiedadRepository.existsByCopropiedadIdAndIdentificacion(1L, "Apto 101")).thenReturn(true);

        assertThatThrownBy(() -> propiedadService.create(nueva))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Ya existe");
    }

    @Test
    @DisplayName("Debe obtener propiedades por copropiedad")
    void getByCopropiedadId_retornaLista() {
        when(propiedadRepository.findByCopropiedadId(1L)).thenReturn(List.of(
                Propiedad.builder().id(1L).identificacion("Apto 101").build(),
                Propiedad.builder().id(2L).identificacion("Apto 102").build()
        ));

        assertThat(propiedadService.getByCopropiedadId(1L)).hasSize(2);
    }

    @Test
    @DisplayName("Debe calcular total coeficiente")
    void getTotalCoeficiente_retornaSuma() {
        when(propiedadRepository.sumCoeficienteByCopropiedadId(1L)).thenReturn(new BigDecimal("85.5000"));

        BigDecimal total = propiedadService.getTotalCoeficiente(1L);

        assertThat(total).isEqualByComparingTo(new BigDecimal("85.5000"));
    }

    @Test
    @DisplayName("Debe lanzar excepcion si coeficientes superan 100%")
    void validateCoeficientes_superan100_lanzaExcepcion() {
        when(propiedadRepository.sumCoeficienteByCopropiedadId(1L)).thenReturn(new BigDecimal("105.0000"));

        assertThatThrownBy(() -> propiedadService.validateCoeficientes(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("superan el 100%");
    }

    @Test
    @DisplayName("No debe lanzar excepcion si coeficientes son validos")
    void validateCoeficientes_validos_noLanzaExcepcion() {
        when(propiedadRepository.sumCoeficienteByCopropiedadId(1L)).thenReturn(new BigDecimal("95.0000"));

        propiedadService.validateCoeficientes(1L);
    }

    @Test
    @DisplayName("Debe obtener propiedad por ID")
    void getById_existente_retornaPropiedad() {
        Propiedad prop = Propiedad.builder().id(1L).identificacion("Apto 101").build();
        when(propiedadRepository.findById(1L)).thenReturn(Optional.of(prop));

        assertThat(propiedadService.getById(1L).getIdentificacion()).isEqualTo("Apto 101");
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando propiedad no existe")
    void getById_noExiste_lanzaExcepcion() {
        when(propiedadRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> propiedadService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}

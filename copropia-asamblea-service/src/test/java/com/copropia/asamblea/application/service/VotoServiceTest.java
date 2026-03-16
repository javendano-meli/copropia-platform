package com.copropia.asamblea.application.service;

import com.copropia.asamblea.domain.model.*;
import com.copropia.asamblea.domain.port.out.OpcionVotoRepository;
import com.copropia.asamblea.domain.port.out.VotacionRepository;
import com.copropia.asamblea.domain.port.out.VotoRepository;
import com.copropia.common.enums.EstadoVotacion;
import com.copropia.common.enums.TipoVotacion;
import com.copropia.common.exception.BusinessException;
import com.copropia.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotoServiceTest {

    @Mock
    private VotoRepository votoRepository;
    @Mock
    private VotacionRepository votacionRepository;
    @Mock
    private OpcionVotoRepository opcionVotoRepository;

    @InjectMocks
    private VotoService votoService;

    private Votacion testVotacion;

    @BeforeEach
    void setUp() {
        testVotacion = Votacion.builder()
                .id(1L).asambleaId(1L).titulo("Aprobacion presupuesto")
                .tipoVotacion(TipoVotacion.APROBACION_SIMPLE)
                .estado(EstadoVotacion.ABIERTA).build();
    }

    @Nested
    @DisplayName("Emitir Voto Tests")
    class EmitirVotoTests {

        @Test
        @DisplayName("Debe emitir voto exitosamente")
        void emitirVoto_datosValidos_registraVoto() {
            Voto voto = Voto.builder()
                    .votacionId(1L).opcionId(1L).usuarioId(1L)
                    .propiedadId(1L).coeficienteAplicado(new BigDecimal("2.5000")).build();

            when(votacionRepository.findById(1L)).thenReturn(Optional.of(testVotacion));
            when(votoRepository.existsByVotacionIdAndPropiedadId(1L, 1L)).thenReturn(false);
            when(opcionVotoRepository.findById(1L)).thenReturn(Optional.of(OpcionVoto.builder().id(1L).nombre("Si").build()));
            when(votoRepository.save(any())).thenAnswer(inv -> {
                Voto v = inv.getArgument(0);
                v.setId(1L);
                return v;
            });

            Voto result = votoService.emitirVoto(voto);

            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getTimestamp()).isNotNull();
        }

        @Test
        @DisplayName("No debe votar en votacion cerrada")
        void emitirVoto_votacionCerrada_lanzaExcepcion() {
            testVotacion.setEstado(EstadoVotacion.CERRADA);
            Voto voto = Voto.builder().votacionId(1L).build();

            when(votacionRepository.findById(1L)).thenReturn(Optional.of(testVotacion));

            assertThatThrownBy(() -> votoService.emitirVoto(voto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("no esta abierta");
        }

        @Test
        @DisplayName("No debe permitir voto duplicado por propiedad")
        void emitirVoto_propiedadYaVoto_lanzaExcepcion() {
            Voto voto = Voto.builder().votacionId(1L).propiedadId(1L).build();

            when(votacionRepository.findById(1L)).thenReturn(Optional.of(testVotacion));
            when(votoRepository.existsByVotacionIdAndPropiedadId(1L, 1L)).thenReturn(true);

            assertThatThrownBy(() -> votoService.emitirVoto(voto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("ya emitio un voto");
        }
    }

    @Nested
    @DisplayName("Resultados Tests")
    class ResultadosTests {

        @Test
        @DisplayName("Debe calcular resultados ponderados correctamente")
        void getResultados_votosExistentes_calculaCorrectamente() {
            List<OpcionVoto> opciones = List.of(
                    OpcionVoto.builder().id(1L).nombre("Si").build(),
                    OpcionVoto.builder().id(2L).nombre("No").build(),
                    OpcionVoto.builder().id(3L).nombre("Abstencion").build()
            );

            List<Voto> votos = List.of(
                    Voto.builder().id(1L).opcionId(1L).coeficienteAplicado(new BigDecimal("2.5000")).build(),
                    Voto.builder().id(2L).opcionId(1L).coeficienteAplicado(new BigDecimal("3.0000")).build(),
                    Voto.builder().id(3L).opcionId(2L).coeficienteAplicado(new BigDecimal("1.2000")).build()
            );

            when(votacionRepository.findById(1L)).thenReturn(Optional.of(testVotacion));
            when(opcionVotoRepository.findByVotacionId(1L)).thenReturn(opciones);
            when(votoRepository.findByVotacionId(1L)).thenReturn(votos);

            ResultadoVotacion resultado = votoService.getResultados(1L, 1L);

            assertThat(resultado.getTotalVotos()).isEqualTo(3);
            assertThat(resultado.getCoeficienteTotal()).isEqualByComparingTo(new BigDecimal("6.7000"));

            ResultadoVotacion.ResultadoOpcion opcionSi = resultado.getResultadosPorOpcion().stream()
                    .filter(r -> r.getOpcion().equals("Si")).findFirst().orElseThrow();
            assertThat(opcionSi.getCantidadVotos()).isEqualTo(2);
            assertThat(opcionSi.getCoeficienteTotal()).isEqualByComparingTo(new BigDecimal("5.5000"));

            ResultadoVotacion.ResultadoOpcion opcionNo = resultado.getResultadosPorOpcion().stream()
                    .filter(r -> r.getOpcion().equals("No")).findFirst().orElseThrow();
            assertThat(opcionNo.getCantidadVotos()).isEqualTo(1);
            assertThat(opcionNo.getCoeficienteTotal()).isEqualByComparingTo(new BigDecimal("1.2000"));

            ResultadoVotacion.ResultadoOpcion opcionAbst = resultado.getResultadosPorOpcion().stream()
                    .filter(r -> r.getOpcion().equals("Abstencion")).findFirst().orElseThrow();
            assertThat(opcionAbst.getCantidadVotos()).isEqualTo(0);
        }

        @Test
        @DisplayName("Debe manejar votacion sin votos")
        void getResultados_sinVotos_retornaVacios() {
            List<OpcionVoto> opciones = List.of(
                    OpcionVoto.builder().id(1L).nombre("Si").build(),
                    OpcionVoto.builder().id(2L).nombre("No").build()
            );

            when(votacionRepository.findById(1L)).thenReturn(Optional.of(testVotacion));
            when(opcionVotoRepository.findByVotacionId(1L)).thenReturn(opciones);
            when(votoRepository.findByVotacionId(1L)).thenReturn(List.of());

            ResultadoVotacion resultado = votoService.getResultados(1L, 1L);

            assertThat(resultado.getTotalVotos()).isEqualTo(0);
            assertThat(resultado.getCoeficienteTotal()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(resultado.getResultadosPorOpcion()).hasSize(2);
            resultado.getResultadosPorOpcion().forEach(r ->
                    assertThat(r.getCantidadVotos()).isEqualTo(0));
        }
    }
}

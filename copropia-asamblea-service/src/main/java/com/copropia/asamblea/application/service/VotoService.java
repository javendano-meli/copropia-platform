package com.copropia.asamblea.application.service;

import com.copropia.asamblea.domain.model.*;
import com.copropia.asamblea.domain.port.in.VotoUseCase;
import com.copropia.asamblea.domain.port.out.OpcionVotoRepository;
import com.copropia.asamblea.domain.port.out.VotacionRepository;
import com.copropia.asamblea.domain.port.out.VotoRepository;
import com.copropia.common.enums.EstadoVotacion;
import com.copropia.common.exception.BusinessException;
import com.copropia.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VotoService implements VotoUseCase {
    private final VotoRepository votoRepository;
    private final VotacionRepository votacionRepository;
    private final OpcionVotoRepository opcionVotoRepository;

    @Override
    @Transactional
    public Voto emitirVoto(Voto voto) {
        log.info("Voto recibido - votacion: {}, propiedad: {}, opcion: {}, coeficiente: {}",
                voto.getVotacionId(), voto.getPropiedadId(), voto.getOpcionId(), voto.getCoeficienteAplicado());
        Votacion votacion = votacionRepository.findById(voto.getVotacionId())
                .orElseThrow(() -> new ResourceNotFoundException("Votacion", voto.getVotacionId()));

        if (votacion.getEstado() != EstadoVotacion.ABIERTA) {
            log.warn("Intento de votar en votacion {} con estado {}", votacion.getId(), votacion.getEstado());
            throw new BusinessException("La votacion no esta abierta");
        }

        if (votoRepository.existsByVotacionIdAndPropiedadId(voto.getVotacionId(), voto.getPropiedadId())) {
            log.warn("Voto duplicado - propiedad {} ya voto en votacion {}", voto.getPropiedadId(), voto.getVotacionId());
            throw new BusinessException("Esta propiedad ya emitio un voto en esta votacion");
        }

        opcionVotoRepository.findById(voto.getOpcionId())
                .orElseThrow(() -> new ResourceNotFoundException("OpcionVoto", voto.getOpcionId()));

        voto.setTimestamp(LocalDateTime.now());
        Voto saved = votoRepository.save(voto);
        log.info("Voto registrado exitosamente id: {}", saved.getId());
        return saved;
    }

    @Override
    public ResultadoVotacion getResultados(Long votacionId, Long copropiedadId) {
        log.info("Consultando resultados votacion {}", votacionId);
        Votacion votacion = votacionRepository.findById(votacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Votacion", votacionId));

        List<OpcionVoto> opciones = opcionVotoRepository.findByVotacionId(votacionId);
        List<Voto> votos = votoRepository.findByVotacionId(votacionId);

        BigDecimal coeficienteVotado = votos.stream()
                .map(Voto::getCoeficienteAplicado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<Long, List<Voto>> votosPorOpcion = votos.stream()
                .collect(Collectors.groupingBy(Voto::getOpcionId));

        List<ResultadoVotacion.ResultadoOpcion> resultados = opciones.stream()
                .map(opcion -> {
                    List<Voto> votosOpcion = votosPorOpcion.getOrDefault(opcion.getId(), List.of());
                    BigDecimal coefOpcion = votosOpcion.stream()
                            .map(Voto::getCoeficienteAplicado)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal porcentaje = coeficienteVotado.compareTo(BigDecimal.ZERO) > 0
                            ? coefOpcion.multiply(new BigDecimal("100")).divide(coeficienteVotado, 2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;

                    return ResultadoVotacion.ResultadoOpcion.builder()
                            .opcionId(opcion.getId())
                            .opcion(opcion.getNombre())
                            .cantidadVotos(votosOpcion.size())
                            .coeficienteTotal(coefOpcion)
                            .porcentaje(porcentaje)
                            .build();
                }).toList();

        ResultadoVotacion resultado = ResultadoVotacion.builder()
                .votacionId(votacionId)
                .titulo(votacion.getTitulo())
                .totalVotos(votos.size())
                .coeficienteTotal(coeficienteVotado)
                .porcentajeParticipacion(coeficienteVotado)
                .resultadosPorOpcion(resultados)
                .build();
        log.info("Resultados votacion {}: {} votos, coeficiente total: {}", votacionId, votos.size(), coeficienteVotado);
        return resultado;
    }
}

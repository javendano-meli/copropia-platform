package com.copropia.asamblea.domain.model;

import com.copropia.common.enums.EstadoVotacion;
import com.copropia.common.enums.TipoVotacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Votacion {
    private Long id;
    private Long asambleaId;
    private String titulo;
    private String descripcion;
    private TipoVotacion tipoVotacion;
    private EstadoVotacion estado;
    private int orden;
    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;
    private List<OpcionVoto> opciones;
}

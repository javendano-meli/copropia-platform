package com.copropia.asamblea.infrastructure.adapter.in.web.dto;

import com.copropia.common.enums.TipoVotacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class VotacionRequest {
    @NotNull private Long asambleaId;
    @NotBlank private String titulo;
    private String descripcion;
    @NotNull private TipoVotacion tipoVotacion;
    private int orden;
    private List<String> opciones;
}

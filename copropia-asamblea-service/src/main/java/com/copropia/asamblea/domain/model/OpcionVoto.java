package com.copropia.asamblea.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpcionVoto {
    private Long id;
    private Long votacionId;
    private String nombre;
    private int orden;
}

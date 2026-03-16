package com.copropia.copropiedad.infrastructure.adapter.out.persistence.entity;

import com.copropia.common.enums.TipoPropiedad;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "propiedad", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"copropiedad_id", "identificacion"})
})
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PropiedadEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "copropiedad_id", nullable = false)
    private Long copropiedadId;
    @Column(name = "propietario_id")
    private Long propietarioId;
    @Column(nullable = false, length = 50)
    private String identificacion;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoPropiedad tipo;
    @Column(name = "metros_cuadrados", precision = 10, scale = 2)
    private BigDecimal metrosCuadrados;
    @Column(nullable = false, precision = 6, scale = 4)
    private BigDecimal coeficiente;
    @Column(nullable = false, length = 20)
    private String estado;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

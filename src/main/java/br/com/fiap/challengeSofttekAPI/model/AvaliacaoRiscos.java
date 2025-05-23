package br.com.fiap.challengeSofttekAPI.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "avaliacao_riscos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AvaliacaoRiscos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_avaliacao", nullable = false)
    private LocalDateTime dataAvaliacao;

    @Column(name = "media_percentual", nullable = false)
    private Double mediaPercentual;

    @Column(name = "categoria_final", nullable = false)
    private String categoriaFinal;

    public AvaliacaoRiscos(Double mediaPercentual, String categoriaFinal) {
        this.dataAvaliacao = LocalDateTime.now();
        this.mediaPercentual = mediaPercentual;
        this.categoriaFinal = categoriaFinal;
    }
}
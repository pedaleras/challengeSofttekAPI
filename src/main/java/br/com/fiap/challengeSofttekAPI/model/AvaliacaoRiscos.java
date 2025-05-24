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
    private LocalDateTime dataAvaliacao = LocalDateTime.now();

    @Column(name = "media_percentual", nullable = false)
    private Double mediaPercentual;

    @Column(name = "categoria_final", nullable = false)
    private String categoriaFinal;

    public AvaliacaoRiscos(Double mediaPercentual) {
        this.dataAvaliacao = LocalDateTime.now();
        this.setMediaPercentual(mediaPercentual);
    }

    public void setMediaPercentual(Double mediaPercentual) {
        this.mediaPercentual = mediaPercentual;
        this.categoriaFinal = calcularCategoriaFinal(mediaPercentual);
    }

    private String calcularCategoriaFinal(Double mediaPercentual) {
        if (mediaPercentual <= 25) return "Neutro";
        if (mediaPercentual <= 50) return "Leve";
        if (mediaPercentual <= 75) return "Moderado";
        return "Agudo";
    }
}

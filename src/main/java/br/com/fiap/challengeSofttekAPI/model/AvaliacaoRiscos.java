package br.com.fiap.challengeSofttekAPI.model;

import org.springframework.data.annotation.Id; // Importar do Spring Data
import org.springframework.data.mongodb.core.mapping.Document; // Importar do Spring Data MongoDB
import org.springframework.data.mongodb.core.mapping.Field; // Opcional: para customizar nome do campo no Mongo
import lombok.*;

import java.time.LocalDateTime;

@Document(collection = "avaliacoes_riscos") // Mapeia para a coleção "avaliacoes_riscos" no MongoDB
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AvaliacaoRiscos {

    @Id
    private String id;

    @Field("data_avaliacao")
    private LocalDateTime dataAvaliacao = LocalDateTime.now();

    @Field("media_percentual")
    private Double mediaPercentual;

    @Field("categoria_final")
    private String categoriaFinal;

    public AvaliacaoRiscos(Double mediaPercentual) {
        this.dataAvaliacao = LocalDateTime.now();
        this.setMediaPercentual(mediaPercentual);
    }

    public void setMediaPercentual(Double mediaPercentual) {
        this.mediaPercentual = mediaPercentual;
        this.categoriaFinal = calcularCategoriaFinal(mediaPercentual);
    }

    // Lógica de negócio para calcular a categoria final
    private String calcularCategoriaFinal(Double mediaPercentual) {
        if (mediaPercentual <= 25) return "Neutro";
        if (mediaPercentual <= 50) return "Leve";
        if (mediaPercentual <= 75) return "Moderado";
        return "Agudo";
    }
}
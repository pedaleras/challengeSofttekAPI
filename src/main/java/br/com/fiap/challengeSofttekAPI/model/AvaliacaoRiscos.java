package br.com.fiap.challengeSofttekAPI.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.*;

import java.time.LocalDateTime;

@Document(collection = "avaliacoes_riscos")
@Getter
@Setter
@NoArgsConstructor // Mantenha este construtor vazio para o Spring Data
@AllArgsConstructor
@EqualsAndHashCode
public class AvaliacaoRiscos {

    @Id
    private String id;

    @Field("colaborador_id")
    private String colaboradorId;

    @Field("data_avaliacao")
    private LocalDateTime dataAvaliacao;

    @Field("media_percentual")
    private Double mediaPercentual;

    @Field("categoria_final")
    private String categoriaFinal;

    // Construtor para conveniência, pode ser útil em testes ou inicializações específicas
    public AvaliacaoRiscos(String colaboradorId, Double mediaPercentual) {
        this.colaboradorId = colaboradorId;
        this.dataAvaliacao = LocalDateTime.now();
        this.setMediaPercentual(mediaPercentual); // Usa o setter para definir mediaPercentual e categoriaFinal
    }

    // Setter personalizado para mediaPercentual que também calcula a categoriaFinal
    public void setMediaPercentual(Double mediaPercentual) {
        this.mediaPercentual = mediaPercentual;
        this.categoriaFinal = calcularCategoriaFinal(mediaPercentual);
    }

    // Lógica de negócio para calcular a categoria final
    private String calcularCategoriaFinal(Double mediaPercentual) {
        if (mediaPercentual == null) return "Não Avaliado"; // Tratar caso de media nula
        if (mediaPercentual <= 25) return "Neutro";
        if (mediaPercentual <= 50) return "Leve";
        if (mediaPercentual <= 75) return "Moderado";
        return "Agudo";
    }
}
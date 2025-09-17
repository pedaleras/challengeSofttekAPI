package br.com.fiap.challengeSofttekAPI.dto;

import br.com.fiap.challengeSofttekAPI.model.AvaliacaoRiscos; // Importa a classe de modelo
import java.time.LocalDateTime; // Certifique-se de que LocalDateTime está importado

public record AvaliacaoRiscosResponseDTO(
        String id,
        Double mediaPercentual,
        String categoriaFinal,
        LocalDateTime dataAvaliacao
) {
    public AvaliacaoRiscosResponseDTO(AvaliacaoRiscos avaliacaoRiscos) {
        this(
                avaliacaoRiscos.getId(),
                avaliacaoRiscos.getMediaPercentual(),
                avaliacaoRiscos.getCategoriaFinal(),
                avaliacaoRiscos.getDataAvaliacao()
        );
    }
}
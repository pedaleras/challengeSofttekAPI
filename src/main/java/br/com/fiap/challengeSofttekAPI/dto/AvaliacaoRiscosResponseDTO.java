package br.com.fiap.challengeSofttekAPI.dto;

import br.com.fiap.challengeSofttekAPI.model.AvaliacaoRiscos;

import java.time.LocalDateTime;

public record AvaliacaoRiscosResponseDTO(
        Long id,
        Double mediaPercentual,
        String categoriaFinal,
        LocalDateTime dataCriacao
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

package br.com.fiap.challengeSofttekAPI.dto;

import br.com.fiap.challengeSofttekAPI.model.Humor;

import java.time.LocalDateTime;

public record HumorResponseDTO(
        Long id,
        Integer nivel,
        String descricao,
        LocalDateTime dataRegistro
) {
    public HumorResponseDTO(Humor humor){
        this(
                humor.getId(),
                humor.getNivelHumor(),
                humor.getDescricaoHumor().getDescricao(),
                humor.getDataRegistro()
        );
    }
}

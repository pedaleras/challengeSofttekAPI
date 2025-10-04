package br.com.fiap.challengeSofttekAPI.dto;

import br.com.fiap.challengeSofttekAPI.model.Humor;
import br.com.fiap.challengeSofttekAPI.model.NivelHumor;

import java.time.LocalDateTime;

public record HumorResponseDTO(
        String id,
        Integer nivel,
        String descricao,
        LocalDateTime dataRegistro
) {
    public HumorResponseDTO(Humor humor) {
        this(
                humor.getId(),
                humor.getNivelHumor(),
                (humor.getDescricaoHumor() != null)
                        ? humor.getDescricaoHumor().getDescricao()
                        : (humor.getNivelHumor() != 0
                        ? NivelHumor.fromNivel(humor.getNivelHumor()).getDescricao()
                        : "Não informado"),
                humor.getDataRegistro()
        );
    }
}

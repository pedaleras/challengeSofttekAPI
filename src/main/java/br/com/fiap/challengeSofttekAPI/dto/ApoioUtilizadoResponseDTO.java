package br.com.fiap.challengeSofttekAPI.dto;

import br.com.fiap.challengeSofttekAPI.model.ApoioUtilizado;

import java.time.LocalDateTime;

public record ApoioUtilizadoResponseDTO(
        Long id,
        String tipo,
        String descricao,
        LocalDateTime dataRegistro
) {
    public ApoioUtilizadoResponseDTO(ApoioUtilizado apoio) {
        this(
                apoio.getId(),
                apoio.getTipoApoio(),
                apoio.getDescricao(),
                apoio.getDataRegistro()
        );
    }
}


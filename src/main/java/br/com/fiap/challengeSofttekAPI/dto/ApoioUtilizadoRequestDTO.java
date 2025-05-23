package br.com.fiap.challengeSofttekAPI.dto;

import jakarta.validation.constraints.NotBlank;

public record ApoioUtilizadoRequestDTO(
        @NotBlank
        String tipo,

        @NotBlank
        String descricao
) {}

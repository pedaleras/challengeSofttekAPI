package br.com.fiap.challengeSofttekAPI.dto;

import jakarta.validation.constraints.*;

public record AvaliacaoRiscosRequestDTO(
        @NotNull
        @DecimalMin("0.0")
        @DecimalMax("100.0")
        Double mediaPercentual,

        @NotBlank
        String categoriaFinal
) {
}


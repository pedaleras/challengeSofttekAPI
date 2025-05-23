package br.com.fiap.challengeSofttekAPI.dto;

import jakarta.validation.constraints.*;

public record HumorRequestDTO(
        @NotNull
        @Min(1)
        @Max(5)
        Integer nivel
) {
}


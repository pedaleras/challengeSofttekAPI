package br.com.fiap.challengeSofttekAPI.dto;

import jakarta.validation.constraints.*;

public record HumorRequestDTO(
        @NotNull(message = "O Nível não pode ser nula!")
        @Min(value = 1, message = "O Nível deve estar entre 1 e 5!")
        @Max(value = 5, message = "O Nível deve estar entre 1 e 5!")
        Integer nivel
) {
}


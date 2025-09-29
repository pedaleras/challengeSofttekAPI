package br.com.fiap.challengeSofttekAPI.dto;

import jakarta.validation.constraints.*;

public record AvaliacaoRiscosRequestDTO(
        @NotNull(message = "O nível de estresse não pode ser nulo!")
        @Min(value = 1, message = "O nível de estresse deve ser no mínimo 1.")
        @Max(value = 5, message = "O nível de estresse deve ser no máximo 5.")
        Integer estresse,

        @NotNull(message = "O nível de ansiedade não pode ser nulo!")
        @Min(value = 1, message = "O nível de ansiedade deve ser no mínimo 1.")
        @Max(value = 5, message = "O nível de ansiedade deve ser no máximo 5.")
        Integer ansiedade,

        @NotNull(message = "O nível de depressão não pode ser nulo!")
        @Min(value = 1, message = "O nível de depressão deve ser no mínimo 1.")
        @Max(value = 5, message = "O nível de depressão deve ser no máximo 5.")
        Integer depressao
) {
}
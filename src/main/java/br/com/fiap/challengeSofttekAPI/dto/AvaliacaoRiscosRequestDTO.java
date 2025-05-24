package br.com.fiap.challengeSofttekAPI.dto;

import jakarta.validation.constraints.*;

public record AvaliacaoRiscosRequestDTO(
        @NotNull(message = "A média percentual não pode ser nula!")
        @DecimalMin(value = "0.0",message = "A média percentual deve ser maior ou igual a zero!")
        @DecimalMax(value = "100.0", message = "A média percentual deve ser menor ou igual a cem!")
        Double mediaPercentual
) {
}


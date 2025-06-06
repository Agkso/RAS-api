package com.example.RAS_API.dto.denuncia;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL; // Opcional, se quiser validar a URL

public record DenunciaCriacaoDto(
        @NotBlank String descricao,
        @NotNull String localizacao,
        @URL String fotoUrl // Opcional
) {}
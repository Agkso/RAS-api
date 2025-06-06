package com.example.RAS_API.dto.denuncia;

import com.example.RAS_API.entity.EnumStatusDenuncia;
import jakarta.validation.constraints.NotNull;

public record DenunciaStatusUpdateDto(
        @NotNull EnumStatusDenuncia status,
        String feedbackAutoridade
) {}
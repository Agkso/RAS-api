package com.example.RAS_api.dto.denuncia;

import com.example.RAS_api.entity.EnumStatusDenuncia;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor

public class DenunciaStatusUpdateDto {
    @NotNull
    private EnumStatusDenuncia status;
    private String feedbackAutoridade;
}

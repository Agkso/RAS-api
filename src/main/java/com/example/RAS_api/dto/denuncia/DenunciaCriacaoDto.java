package com.example.RAS_api.dto.denuncia;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class DenunciaCriacaoDto {
    @NotBlank
    private String descricao;
    @NotNull
    private String localizacao;
    private Double latitude;
    private Double longitude;
    private String fotoUrl; // Removido @NotBlank para tornar opcional
}

package com.example.RAS_api.dto.denuncia;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DenunciaDto {
    private Long id;
    private String descricao;
    private String localizacao;
    private Double latitude;
    private Double longitude;
    private String fotoUrl;
    private LocalDateTime dataCriacao;
    private String status;
    private String feedbackAutoridade;
    private Long usuarioId;
    private String usuarioNome;
}
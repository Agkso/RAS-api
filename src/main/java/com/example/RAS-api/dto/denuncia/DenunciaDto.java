package com.example.RAS_API.dto.denuncia;

import java.time.LocalDateTime;

public record DenunciaDto(
        Long id,
        String descricao,
        String localizacao,
        String fotoUrl,
        LocalDateTime dataCriacao,
        String status,
        String feedbackAutoridade,
        Long usuarioId,
        String nomeUsuario // Para exibir o nome do usuário que fez a denúncia
) {}
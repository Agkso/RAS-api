package com.example.RAS_api.dto.usuario;

public record UsuarioDto(
        Long id,
        String nome,
        String email,
        String role
) {}
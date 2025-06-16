package com.example.RAS_API.dto.usuario;

public record UsuarioDto(
        Long id,
        String nome,
        String email,
        String role
) {}
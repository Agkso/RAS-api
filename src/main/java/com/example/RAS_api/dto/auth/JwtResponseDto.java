package com.example.RAS_api.dto.auth;

public record JwtResponseDto(
        String token,
        Long id,
        String nome,
        String email,
        String role
) {}
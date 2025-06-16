package com.example.RAS_api.dto.auth;

import java.util.List;

public record JwtResponseDto(
        String token,
        Long id,
        String nome,
        String email,
        List<String> roles
) {}
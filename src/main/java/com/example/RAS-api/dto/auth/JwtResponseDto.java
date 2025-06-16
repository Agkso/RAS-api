package com.example.RAS_API.dto.auth;

import java.util.List;

public record JwtResponseDto(
        String token,
        Long id,
        String email,
        List<String> roles
) {}
package com.example.RAS_API.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroUsuarioDto(
        @NotBlank @Size(min = 3, max = 100) String nome,
        @NotBlank @Email @Size(max = 100) String email,
        @NotBlank @Size(min = 6, max = 40) String senha
) {}
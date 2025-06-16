package com.example.RAS_api.dto.auth;

import com.example.RAS_api.entity.EnumRole;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistroUsuarioDto {
    @NotBlank @Size(min = 3, max = 100)
    private String nome;

    @NotBlank @Email @Size(max = 100) 
    private String email;

    @NotBlank @Size(min = 6, max = 40)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EnumRole role;

}

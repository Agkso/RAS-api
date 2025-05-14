package com.example.RAS_API.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.RAS_API.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}

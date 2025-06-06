package com.example.RAS_API.repository;

import com.example.RAS_API.entity.Denuncia;
import com.example.RAS_API.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DenunciaRepository extends JpaRepository<Denuncia, Long> {
    List<Denuncia> findByUsuario(Usuario usuario);
}

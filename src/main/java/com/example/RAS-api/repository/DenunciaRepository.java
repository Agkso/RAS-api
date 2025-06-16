package com.example.RAS_API.repository;

import com.example.RAS_API.entity.Denuncia;
import com.example.RAS_API.entity.EnumStatusDenuncia;
import com.example.RAS_API.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DenunciaRepository extends JpaRepository<Denuncia, Long> {
    List<Denuncia> findByUsuario(Usuario usuario);
    
    Page<Denuncia> findByUsuarioId(Long usuarioId, Pageable pageable);
    
    Page<Denuncia> findByStatus(EnumStatusDenuncia status, Pageable pageable);
    
    Page<Denuncia> findByLocalizacaoContainingIgnoreCase(String localizacao, Pageable pageable);
    
    Page<Denuncia> findByStatusAndLocalizacaoContainingIgnoreCase(EnumStatusDenuncia status, String localizacao, Pageable pageable);
}

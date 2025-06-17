package com.example.RAS_api.repository;

import com.example.RAS_api.entity.Denuncia;
import com.example.RAS_api.entity.EnumStatusDenuncia;
import com.example.RAS_api.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DenunciaRepository extends JpaRepository<Denuncia, Long> {
    List<Denuncia> findByUsuario(Usuario usuario);

    Page<Denuncia> findByUsuarioId(Long usuarioId, Pageable pageable);

    Page<Denuncia> findByStatus(EnumStatusDenuncia status, Pageable pageable);

    Page<Denuncia> findByLocalizacaoContainingIgnoreCase(String localizacao, Pageable pageable);

    Page<Denuncia> findByStatusAndLocalizacaoContainingIgnoreCase(EnumStatusDenuncia status, String localizacao, Pageable pageable);

    // Novos métodos para filtros avançados
    Page<Denuncia> findByUsuarioIdAndStatus(Long usuarioId, EnumStatusDenuncia status, Pageable pageable);

    Page<Denuncia> findByUsuarioIdAndLocalizacaoContainingIgnoreCase(Long usuarioId, String localizacao, Pageable pageable);

    Page<Denuncia> findByUsuarioIdAndStatusAndLocalizacaoContainingIgnoreCase(Long usuarioId, EnumStatusDenuncia status, String localizacao, Pageable pageable);

    Page<Denuncia> findByUsuarioIdAndDataCriacaoBetween(Long usuarioId, LocalDateTime dataInicio, LocalDateTime dataFim, Pageable pageable);


    @Query("""
        SELECT d
        FROM Denuncia d
        WHERE d.usuario.id = :usuarioId
          AND (:status IS NULL OR d.status = :status)
          AND (:localizacao IS NULL
               OR LOWER(COALESCE(d.localizacao, ''))
                   LIKE CONCAT('%', LOWER(:localizacao), '%'))
          AND (:dataInicio IS NULL OR d.dataCriacao >= :dataInicio)
          AND (:dataFim    IS NULL OR d.dataCriacao <= :dataFim)
        """)
    Page<Denuncia> findByUsuarioIdWithFilters(
            @Param("usuarioId") Long usuarioId,
            @Param("status")     EnumStatusDenuncia status,
            @Param("localizacao") String localizacao,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim")    LocalDateTime dataFim,
            Pageable pageable
    );
}


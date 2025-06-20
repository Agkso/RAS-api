package com.example.RAS_api.controller;

import com.example.RAS_api.dto.denuncia.DenunciaCriacaoDto;
import com.example.RAS_api.dto.denuncia.DenunciaDto;
import com.example.RAS_api.dto.denuncia.DenunciaStatusUpdateDto;
import com.example.RAS_api.entity.Denuncia;
import com.example.RAS_api.entity.EnumStatusDenuncia;
import com.example.RAS_api.entity.Usuario;
import com.example.RAS_api.service.DenunciaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/denuncias")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DenunciaController {

    @Autowired
    private DenunciaService denunciaService;

    @PostMapping
    @PreAuthorize("hasRole('MORADOR') or hasRole('ADMIN') or hasRole('AGENTE')")
    public ResponseEntity<?> criarDenuncia(@Valid @RequestBody DenunciaCriacaoDto denunciaDto,
                                           Authentication authentication) {
        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            Denuncia denuncia = denunciaService.criarDenuncia(denunciaDto, usuario);

            // Cria resposta simples para evitar problemas de serialização
            Map<String, Object> response = new HashMap<>();
            response.put("id", denuncia.getId());
            response.put("descricao", denuncia.getDescricao());
            response.put("localizacao", denuncia.getLocalizacao());
            response.put("fotoUrl", denuncia.getFotoUrl());
            response.put("status", denuncia.getStatus().name());
            response.put("dataCriacao", denuncia.getDataCriacao());
            response.put("usuarioId", usuario.getId());
            response.put("usuarioNome", usuario.getNome());
            response.put("message", "Denúncia criada com sucesso!");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao criar denúncia: " + e.getMessage());
        }
    }

    @GetMapping("/minhas")
    @PreAuthorize("hasRole('MORADOR')")
    public ResponseEntity<?> listarMinhasDenuncias(Authentication authentication,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam(defaultValue = "dataCriacao") String sortBy,
                                                   @RequestParam(defaultValue = "desc") String sortDir,
                                                   @RequestParam(required = false) EnumStatusDenuncia status,
                                                   @RequestParam(required = false) String localizacao,
                                                   @RequestParam(required = false) String dataInicio,
                                                   @RequestParam(required = false) String dataFim) {
        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();

            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            Page<DenunciaDto> denuncias = denunciaService.listarMinhasDenunciasComFiltros(
                    usuario.getId(), status, localizacao, dataInicio, dataFim, pageable);

            return ResponseEntity.ok(denuncias);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao listar denúncias: " + e.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE')")
    public ResponseEntity<?> listarTodasDenuncias(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(required = false) EnumStatusDenuncia status,
                                                  @RequestParam(required = false) String localizacao) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("dataCriacao").descending());
            Page<DenunciaDto> denuncias = denunciaService.listarDenuncias(status, localizacao, pageable);
            return ResponseEntity.ok(denuncias);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao listar denúncias: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE')")
    public ResponseEntity<?> obterDenuncia(@PathVariable Long id) {
        try {
            DenunciaDto denuncia = denunciaService.obterDenunciaPorId(id);
            return ResponseEntity.ok(denuncia);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Denúncia não encontrada");
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE')")
    public ResponseEntity<?> atualizarStatusDenuncia(@PathVariable Long id,
                                                     @Valid @RequestBody DenunciaStatusUpdateDto statusUpdate,
                                                     Authentication authentication) {
        try {
            Usuario agente = (Usuario) authentication.getPrincipal();
            DenunciaDto denunciaAtualizada = denunciaService.atualizarStatusDenuncia(id, statusUpdate, agente);
            return ResponseEntity.ok(denunciaAtualizada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar status da denúncia: " + e.getMessage());
        }
    }

    @GetMapping("/regiao")
    public ResponseEntity<?> listarDenunciasPorRegiao(@RequestParam String localizacao,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("dataCriacao").descending());
            Page<DenunciaDto> denuncias = denunciaService.listarDenunciasPorRegiao(localizacao, pageable);
            return ResponseEntity.ok(denuncias);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao listar denúncias por região: " + e.getMessage());
        }
    }
}

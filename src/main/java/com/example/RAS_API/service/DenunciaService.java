package com.example.RAS_API.service;

import com.example.RAS_API.dto.denuncia.DenunciaCriacaoDto;
import com.example.RAS_API.dto.denuncia.DenunciaDto;
import com.example.RAS_API.dto.denuncia.DenunciaStatusUpdateDto;
import com.example.RAS_API.entity.Denuncia;
import com.example.RAS_API.entity.EnumStatusDenuncia;
import com.example.RAS_API.entity.Usuario;
import com.example.RAS_API.exception.ResourceNotFoundException;
import com.example.RAS_API.repository.DenunciaRepository;
import com.example.RAS_API.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DenunciaService {

    @Autowired
    private DenunciaRepository denunciaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository; // Para buscar o usuário ao criar a denúncia

    @Autowired
    private UsuarioService usuarioService; // Para pegar o usuário logado

    @Transactional
    public DenunciaDto criarDenuncia(DenunciaCriacaoDto denunciaCriacaoDto) {
        Usuario usuarioLogado = usuarioService.getUsuarioLogado();
        if (usuarioLogado == null) {
            throw new IllegalStateException("Usuário precisa estar logado para criar uma denúncia.");
        }

        Denuncia denuncia = new Denuncia();
        denuncia.setDescricao(denunciaCriacaoDto.descricao());
        denuncia.setLatitude(denunciaCriacaoDto.latitude());
        denuncia.setLongitude(denunciaCriacaoDto.longitude());
        denuncia.setFotoUrl(denunciaCriacaoDto.fotoUrl());
        denuncia.setUsuario(usuarioLogado);
        denuncia.setStatus(EnumStatusDenuncia.PENDENTE); // Status inicial
        denuncia.setDataCriacao(LocalDateTime.now());

        Denuncia novaDenuncia = denunciaRepository.save(denuncia);
        return mapToDto(novaDenuncia);
    }

    @Transactional(readOnly = true)
    public List<DenunciaDto> buscarDenunciasPorUsuarioLogado() {
        Usuario usuarioLogado = usuarioService.getUsuarioLogado();
        if (usuarioLogado == null) {
            throw new IllegalStateException("Usuário precisa estar logado para ver suas denúncias.");
        }
        List<Denuncia> denuncias = denunciaRepository.findByUsuario(usuarioLogado);
        return denuncias.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DenunciaDto> buscarTodasDenuncias() {
        // A verificação de role (ADMIN/VIGILANTE) será feita no controller com @PreAuthorize
        List<Denuncia> denuncias = denunciaRepository.findAll();
        return denuncias.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DenunciaDto buscarDenunciaPorId(Long id) {
        Denuncia denuncia = denunciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Denúncia", "id", id));

        Usuario usuarioLogado = usuarioService.getUsuarioLogado();
        if (usuarioLogado.getRole() == com.seuprojeto.denunciasambientais.entity.EnumRole.ROLE_USUARIO &&
                !denuncia.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Você não tem permissão para acessar esta denúncia.");
        }
        return mapToDto(denuncia);
    }

    @Transactional
    public DenunciaDto atualizarStatusDenuncia(Long id, DenunciaStatusUpdateDto statusUpdateDto) {
        // A verificação de role (ADMIN/VIGILANTE) será feita no controller com @PreAuthorize
        Denuncia denuncia = denunciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Denúncia", "id", id));

        denuncia.setStatus(statusUpdateDto.status());
        if (statusUpdateDto.feedbackAutoridade() != null && !statusUpdateDto.feedbackAutoridade().isBlank()) {
            denuncia.setFeedbackAutoridade(statusUpdateDto.feedbackAutoridade());
        }

        Denuncia denunciaAtualizada = denunciaRepository.save(denuncia);
        return mapToDto(denunciaAtualizada);
    }


    private DenunciaDto mapToDto(Denuncia denuncia) {
        return new DenunciaDto(
                denuncia.getId(),
                denuncia.getDescricao(),
                denuncia.getLatitude(),
                denuncia.getLongitude(),
                denuncia.getFotoUrl(),
                denuncia.getDataCriacao(),
                denuncia.getStatus().name(),
                denuncia.getFeedbackAutoridade(),
                denuncia.getUsuario().getId(),
                denuncia.getUsuario().getNome()
        );
    }
}
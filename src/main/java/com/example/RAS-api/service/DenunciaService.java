package com.example.RAS_API.service;

import com.example.RAS_API.dto.denuncia.DenunciaCriacaoDto;
import com.example.RAS_API.dto.denuncia.DenunciaDto;
import com.example.RAS_API.dto.denuncia.DenunciaStatusUpdateDto;
import com.example.RAS_API.entity.Denuncia;
import com.example.RAS_API.entity.EnumStatusDenuncia;
import com.example.RAS_API.entity.Usuario;
import com.example.RAS_API.exception.ResourceNotFoundException;
import com.example.RAS_API.repository.DenunciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class DenunciaService {

    @Autowired
    private DenunciaRepository denunciaRepository;

    @Transactional
    public Denuncia criarDenuncia(DenunciaCriacaoDto denunciaDto, Usuario usuario) {
        Denuncia denuncia = new Denuncia();
        denuncia.setDescricao(denunciaDto.getDescricao());
        denuncia.setLocalizacao(denunciaDto.getLocalizacao());
        denuncia.setFotoUrl(denunciaDto.getFotoUrl());
        denuncia.setUsuario(usuario);
        denuncia.setStatus(EnumStatusDenuncia.PENDENTE);
        denuncia.setDataCriacao(LocalDateTime.now());

        return denunciaRepository.save(denuncia);
    }

    public Page<DenunciaDto> listarDenunciasPorUsuario(Long usuarioId, Pageable pageable) {
        Page<Denuncia> denuncias = denunciaRepository.findByUsuarioId(usuarioId, pageable);
        return denuncias.map(this::convertToDto);
    }

    public Page<DenunciaDto> listarDenuncias(EnumStatusDenuncia status, String localizacao, Pageable pageable) {
        Page<Denuncia> denuncias;
        
        if (status != null && localizacao != null) {
            denuncias = denunciaRepository.findByStatusAndLocalizacaoContainingIgnoreCase(status, localizacao, pageable);
        } else if (status != null) {
            denuncias = denunciaRepository.findByStatus(status, pageable);
        } else if (localizacao != null) {
            denuncias = denunciaRepository.findByLocalizacaoContainingIgnoreCase(localizacao, pageable);
        } else {
            denuncias = denunciaRepository.findAll(pageable);
        }
        
        return denuncias.map(this::convertToDto);
    }

    public Page<DenunciaDto> listarDenunciasPorRegiao(String localizacao, Pageable pageable) {
        Page<Denuncia> denuncias = denunciaRepository.findByLocalizacaoContainingIgnoreCase(localizacao, pageable);
        return denuncias.map(this::convertToDto);
    }

    public DenunciaDto obterDenunciaPorId(Long id) {
        Denuncia denuncia = denunciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Denúncia", "id", id));
        return convertToDto(denuncia);
    }

    @Transactional
    public DenunciaDto atualizarStatusDenuncia(Long id, DenunciaStatusUpdateDto statusUpdate, Usuario agente) {
        Denuncia denuncia = denunciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Denúncia", "id", id));

        denuncia.setStatus(statusUpdate.getStatus());
        if (statusUpdate.getFeedbackAutoridade() != null) {
            denuncia.setFeedbackAutoridade(statusUpdate.getFeedbackAutoridade());
        }

        Denuncia denunciaAtualizada = denunciaRepository.save(denuncia);
        return convertToDto(denunciaAtualizada);
    }

    private DenunciaDto convertToDto(Denuncia denuncia) {
        DenunciaDto dto = new DenunciaDto();
        dto.setId(denuncia.getId());
        dto.setDescricao(denuncia.getDescricao());
        dto.setLocalizacao(denuncia.getLocalizacao());
        dto.setFotoUrl(denuncia.getFotoUrl());
        dto.setDataCriacao(denuncia.getDataCriacao());
        dto.setStatus(denuncia.getStatus());
        dto.setFeedbackAutoridade(denuncia.getFeedbackAutoridade());
        dto.setUsuarioId(denuncia.getUsuario().getId());
        dto.setUsuarioNome(denuncia.getUsuario().getNome());
        return dto;
    }
}


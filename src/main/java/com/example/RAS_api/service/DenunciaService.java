package com.example.RAS_api.service;

import com.example.RAS_api.dto.denuncia.DenunciaCriacaoDto;
import com.example.RAS_api.dto.denuncia.DenunciaDto;
import com.example.RAS_api.dto.denuncia.DenunciaStatusUpdateDto;
import com.example.RAS_api.entity.Denuncia;
import com.example.RAS_api.entity.EnumStatusDenuncia;
import com.example.RAS_api.entity.Usuario;
import com.example.RAS_api.exception.ResourceNotFoundException;
import com.example.RAS_api.repository.DenunciaRepository;
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
        // Cria uma nova instância de usuário apenas com os dados necessários
        Usuario usuarioSimples = new Usuario();
        usuarioSimples.setId(usuario.getId());
        usuarioSimples.setNome(usuario.getNome());
        usuarioSimples.setEmail(usuario.getEmail());
        usuarioSimples.setRole(usuario.getRole());
        
        Denuncia denuncia = new Denuncia();
        denuncia.setDescricao(denunciaDto.getDescricao());
        denuncia.setLocalizacao(denunciaDto.getLocalizacao());
        denuncia.setLatitude(denunciaDto.getLatitude());
        denuncia.setLongitude(denunciaDto.getLongitude());
        // Só define fotoUrl se não for null ou vazio
        if (denunciaDto.getFotoUrl() != null && !denunciaDto.getFotoUrl().trim().isEmpty()) {
            denuncia.setFotoUrl(denunciaDto.getFotoUrl());
        }
        denuncia.setUsuario(usuarioSimples);
        denuncia.setStatus(EnumStatusDenuncia.PENDENTE);
        denuncia.setDataCriacao(LocalDateTime.now());

        Denuncia denunciaSalva = denunciaRepository.save(denuncia);
        
        // Limpa a referência do usuário para evitar problemas de serialização
        denunciaSalva.getUsuario().setDenuncias(null);
        
        return denunciaSalva;
    }

    public Page<DenunciaDto> listarMinhasDenunciasComFiltros(Long usuarioId, EnumStatusDenuncia status, 
                                                            String localizacao, String dataInicio, 
                                                            String dataFim, Pageable pageable) {
        LocalDateTime inicio = null;
        LocalDateTime fim = null;
        
        try {
            if (dataInicio != null && !dataInicio.isEmpty()) {
                inicio = LocalDateTime.parse(dataInicio + "T00:00:00");
            }
            if (dataFim != null && !dataFim.isEmpty()) {
                fim = LocalDateTime.parse(dataFim + "T23:59:59");
            }
        } catch (Exception e) {
            // Se houver erro no parsing das datas, ignora os filtros de data
        }
        
        Page<Denuncia> denuncias = denunciaRepository.findByUsuarioIdWithFilters(
            usuarioId, status, localizacao, inicio, fim, pageable);
        
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
        dto.setLatitude(denuncia.getLatitude());
        dto.setLongitude(denuncia.getLongitude());
        dto.setFotoUrl(denuncia.getFotoUrl());
        dto.setDataCriacao(denuncia.getDataCriacao());
        dto.setStatus(denuncia.getStatus().name());
        dto.setFeedbackAutoridade(denuncia.getFeedbackAutoridade());
        dto.setUsuarioId(denuncia.getUsuario().getId());
        dto.setUsuarioNome(denuncia.getUsuario().getNome());
        return dto;
    }
}


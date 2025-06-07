package com.example.RAS_API.service;

import com.example.RAS_API.dto.auth.JwtResponseDto;
import com.example.RAS_API.dto.auth.LoginRequestDto;
import com.example.RAS_API.dto.auth.RegistroUsuarioDto;
import com.example.RAS_API.dto.usuario.UsuarioDto;
import com.example.RAS_API.entity.EnumRole;
import com.example.RAS_API.entity.Usuario;
import com.example.RAS_API.exception.ResourceNotFoundException;
import com.example.RAS_API.repository.UsuarioRepository;
import com.example.RAS_API.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Transactional
    public UsuarioDto registrarUsuario(RegistroUsuarioDto registroDto) {
        if (usuarioRepository.existsByEmail(registroDto.email())) {
            throw new IllegalArgumentException("Erro: Email já está em uso!");
        }

        Usuario usuario = new Usuario(
                registroDto.nome(),
                registroDto.email(),
                passwordEncoder.encode(registroDto.senha()),
                EnumRole.USUARIO // Novo usuário sempre será ROLE_USUARIO por padrão
        );

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return new UsuarioDto(usuarioSalvo.getId(), usuarioSalvo.getNome(), usuarioSalvo.getEmail(), usuarioSalvo.getRole().name());
    }

    public JwtResponseDto autenticarUsuario(LoginRequestDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.senha()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);

        Usuario userDetails = (Usuario) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtResponseDto(jwt, userDetails.getId(), userDetails.getEmail(), roles);
    }

    public Usuario getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null; // ou lançar exceção
        }
        String emailUsuario = ((Usuario) authentication.getPrincipal()).getEmail();
        return usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "email", emailUsuario));
    }

    public UsuarioDto getUsuarioDtoById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));
        return new UsuarioDto(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getRole().name());
    }

    // Método para criar um usuário Admin/Vigilante (pode ser usado por um script inicial ou um endpoint protegido)
    @Transactional
    public UsuarioDto criarUsuarioComRole(RegistroUsuarioDto registroDto, EnumRole role) {
        if (usuarioRepository.existsByEmail(registroDto.email())) {
            throw new IllegalArgumentException("Erro: Email já está em uso!");
        }
        Usuario usuario = new Usuario(
                registroDto.nome(),
                registroDto.email(),
                passwordEncoder.encode(registroDto.senha()),
                role
        );
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return new UsuarioDto(usuarioSalvo.getId(), usuarioSalvo.getNome(), usuarioSalvo.getEmail(), usuarioSalvo.getRole().name());
    }
}

package com.example.RAS_api.controller;
import java.util.List;
import com.example.RAS_api.dto.auth.JwtResponseDto;
import com.example.RAS_api.dto.auth.LoginRequestDto;
import com.example.RAS_api.dto.auth.RegistroUsuarioDto;
import com.example.RAS_api.entity.EnumRole;
import com.example.RAS_api.entity.Usuario;
import com.example.RAS_api.security.JwtTokenProvider;
import com.example.RAS_api.service.UsuarioService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDto loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getSenha()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenProvider.generateToken(authentication);

            Usuario usuario = (Usuario) authentication.getPrincipal();

            List<String> roles = List.of(usuario.getRole().name());

            return ResponseEntity.ok(new JwtResponseDto(
                    jwt,
                    usuario.getId(),
                    usuario.getNome(),
                    usuario.getEmail(),
                    roles
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciais inválidas");
        }
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistroUsuarioDto registroRequest) {
        try {
            if (usuarioService.existsByEmail(registroRequest.getEmail())) {
                return ResponseEntity.badRequest()
                        .body("Erro: Email já está em uso!");
            }

            // Criar novo usuário
            Usuario usuario = new Usuario(
                    registroRequest.getNome(),
                    registroRequest.getEmail(),
                    passwordEncoder.encode(registroRequest.getSenha()),
                    registroRequest.getRole() != null ? registroRequest.getRole() : EnumRole.MORADOR
            );

            Usuario usuarioSalvo = usuarioService.save(usuario);

            return ResponseEntity.ok("Usuário registrado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno do servidor");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado");
        }

        Usuario usuario = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(usuario);
    }
}


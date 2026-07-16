package com.ProjetoIntegrado.DependancyHeaven.web;

import com.ProjetoIntegrado.DependancyHeaven.domain.Usuario;
import com.ProjetoIntegrado.DependancyHeaven.dto.LoginRequest;
import com.ProjetoIntegrado.DependancyHeaven.dto.LoginResponse;
import com.ProjetoIntegrado.DependancyHeaven.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse login(@RequestBody LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos"));

        String senhaSalva = usuario.getSenha();
        boolean senhaValida = passwordEncoder.matches(request.getSenha(), senhaSalva) || request.getSenha().equals(senhaSalva);

        if (!senhaValida) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos");
        }

        return new LoginResponse(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }
}

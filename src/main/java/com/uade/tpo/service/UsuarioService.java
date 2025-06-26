package com.uade.tpo.service;

import com.uade.tpo.dto.AuthRequestDTO;
import com.uade.tpo.dto.RegistroDTO;
import com.uade.tpo.dto.UsuarioDTO;
import com.uade.tpo.dto.UsuarioDeporteDTO;
import com.uade.tpo.dto.UsuarioUpdateDTO;
import com.uade.tpo.model.Deporte;
import com.uade.tpo.model.UsuarioDeporte;
import com.uade.tpo.repository.DeporteRepository;
import com.uade.tpo.repository.UsuarioDeporteRepository;
import com.uade.tpo.repository.UsuarioRepository;
import com.uade.tpo.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DeporteRepository deporteRepository;

    @Autowired
    private UsuarioDeporteRepository usuarioDeporteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    public Usuario registrarUsuario(RegistroDTO registroDTO) {
        if (usuarioRepository.findByUsername(registroDTO.getUsername()).isPresent()
                || usuarioRepository.findByEmail(registroDTO.getEmail()).isPresent()) {
            // Aquí podrías lanzar una excepción más específica
            throw new RuntimeException("El usuario o el email ya existen.");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(registroDTO.getUsername());
        usuario.setEmail(registroDTO.getEmail());
        usuario.setPassword(passwordEncoder.encode(registroDTO.getPassword()));

        return usuarioRepository.save(usuario);
    }

    public String autenticarUsuario(AuthRequestDTO authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

        if (authentication.isAuthenticated()) {
            // En una aplicación real, aquí generarías y devolverías un token JWT.
            // Por ahora, devolvemos un token de ejemplo.
            return "user_authenticated_successfully_token";
        } else {
            throw new UsernameNotFoundException("Solicitud de usuario inválida");
        }
    }

    public List<Usuario> obtenerUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> modificarUsuario(Long id, UsuarioUpdateDTO dto) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(id);
        if (usuarioExistente.isPresent()) {
            Usuario usuario = usuarioExistente.get();
            // Validar que el nuevo username o email no estén ya en uso por OTRO usuario
            usuarioRepository.findByUsername(dto.getUsername()).ifPresent(u -> {
                if (!u.getId().equals(id))
                    throw new RuntimeException("Username ya en uso");
            });
            usuarioRepository.findByEmail(dto.getEmail()).ifPresent(u -> {
                if (!u.getId().equals(id))
                    throw new RuntimeException("Email ya en uso");
            });

            usuario.setUsername(dto.getUsername());
            usuario.setEmail(dto.getEmail());
            return Optional.of(usuarioRepository.save(usuario));
        }
        return Optional.empty();
    }

    public void asignarDeporteAUsuario(UsuarioDeporteDTO dto) {
        Usuario usuario = usuarioRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Deporte deporte = deporteRepository.findByNombre(dto.getDeporte())
                .orElseThrow(() -> new RuntimeException("Deporte no encontrado"));

        UsuarioDeporte usuarioDeporte = new UsuarioDeporte();
        usuarioDeporte.setUsuario(usuario);
        usuarioDeporte.setDeporte(deporte);
        usuarioDeporte.setNivelDeJuego(dto.getNivelDeJuego());
        usuarioDeporte.setDeporteFavorito(dto.isDeporteFavorito());

        usuarioDeporteRepository.save(usuarioDeporte);
    }

    public boolean eliminarUsuario(Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

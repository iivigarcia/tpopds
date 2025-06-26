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
import java.util.stream.Collectors;

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

    public List<UsuarioDTO> obtenerUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<UsuarioDTO> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id).map(this::convertToDto);
    }

    public Optional<UsuarioDTO> registrarUsuario(RegistroDTO registroDTO) {
        if (usuarioRepository.findByEmail(registroDTO.getEmail()).isPresent()) {
            return Optional.empty();
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(registroDTO.getUsername());
        usuario.setEmail(registroDTO.getEmail());
        usuario.setPassword(passwordEncoder.encode(registroDTO.getPassword()));

        Usuario savedUsuario = usuarioRepository.save(usuario);
        return Optional.of(convertToDto(savedUsuario));
    }

    public Optional<String> autenticarUsuario(AuthRequestDTO authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

            if (authentication.isAuthenticated()) {
                return Optional.of("token-ejemplo-jwt");
            }
        } catch (Exception e) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    public Optional<UsuarioDTO> modificarUsuario(Long id, UsuarioUpdateDTO updateDTO) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) {
            return Optional.empty();
        }

        Usuario usuario = usuarioOpt.get();

        if (updateDTO.getUsername() != null && !updateDTO.getUsername().equals(usuario.getUsername())) {
            if (usuarioRepository.findByUsername(updateDTO.getUsername()).isPresent()) {
                return Optional.empty();
            }
            usuario.setUsername(updateDTO.getUsername());
        }

        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(usuario.getEmail())) {
            if (usuarioRepository.findByEmail(updateDTO.getEmail()).isPresent()) {
                return Optional.empty();
            }
            usuario.setEmail(updateDTO.getEmail());
        }

        Usuario savedUsuario = usuarioRepository.save(usuario);
        return Optional.of(convertToDto(savedUsuario));
    }

    public boolean eliminarUsuario(Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private UsuarioDTO convertToDto(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setUsername(usuario.getUsername());
        dto.setEmail(usuario.getEmail());
        return dto;
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
}

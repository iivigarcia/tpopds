package com.uade.tpo.service;

import com.uade.tpo.dto.EquipoCreateDTO;
import com.uade.tpo.model.Usuario;
import com.uade.tpo.repository.EquipoRepository;
import com.uade.tpo.model.Equipo;
import com.uade.tpo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EquipoService {

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Equipo crearEquipo(EquipoCreateDTO equipoCreateDTO) {
        Equipo equipo = new Equipo();
        equipo.setNombre(equipoCreateDTO.getNombre());
        return equipoRepository.save(equipo);
    }

    public List<Equipo> obtenerEquipos() {
        return equipoRepository.findAll();
    }

    public Optional<Equipo> obtenerEquipoPorId(Long id) {
        return equipoRepository.findById(id);
    }

    public Equipo modificarEquipo(Long id, EquipoCreateDTO equipoCreateDTO) {
        Optional<Equipo> equipoExistente = equipoRepository.findById(id);
        if (equipoExistente.isPresent()) {
            Equipo equipo = equipoExistente.get();
            equipo.setNombre(equipoCreateDTO.getNombre());
            return equipoRepository.save(equipo);
        }
        return null;
    }

    public void eliminarEquipo(Long id) {
        equipoRepository.deleteById(id);
    }

    public Equipo agregarJugador(Long equipoId, Long usuarioId) {
        Optional<Equipo> optionalEquipo = equipoRepository.findById(equipoId);
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(usuarioId);

        if (optionalEquipo.isPresent() && optionalUsuario.isPresent()) {
            Equipo equipo = optionalEquipo.get();
            Usuario usuario = optionalUsuario.get();
            equipo.getJugadores().add(usuario);
            return equipoRepository.save(equipo);
        }
        return null;
    }

    public Equipo eliminarJugador(Long equipoId, Long usuarioId) {
        Optional<Equipo> optionalEquipo = equipoRepository.findById(equipoId);
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(usuarioId);

        if (optionalEquipo.isPresent() && optionalUsuario.isPresent()) {
            Equipo equipo = optionalEquipo.get();
            Usuario usuario = optionalUsuario.get();
            equipo.getJugadores().remove(usuario);
            return equipoRepository.save(equipo);
        }
        return null;
    }
}

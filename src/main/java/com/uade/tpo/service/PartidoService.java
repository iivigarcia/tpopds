package com.uade.tpo.service;

import com.uade.tpo.dto.PartidoCreateDTO;
import com.uade.tpo.model.Equipo;
import com.uade.tpo.model.Partido;
import com.uade.tpo.model.emparejamientoStrategy.EmparejamientoStrategy;
import com.uade.tpo.repository.EquipoRepository;
import com.uade.tpo.repository.PartidoRepository;
import com.uade.tpo.repository.UsuarioDeporteRepository;
import com.uade.tpo.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PartidoService {

    @Autowired
    private PartidoRepository partidoRepository;
    @Autowired
    private EquipoRepository equipoRepository;

    public Optional<Partido> crearPartido(PartidoCreateDTO dto) {
        if (dto.getEquipoIds() == null || dto.getEquipoIds().isEmpty()) {
            return Optional.empty();
        }

        List<Equipo> equipos = dto.getEquipoIds().stream()
                .map(equipoRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        if (equipos.size() != dto.getEquipoIds().size()) {
            return Optional.empty();
        }

        Partido partido = new Partido();
        partido.setEquipos(equipos);
        partido.setFecha(dto.getFecha());
        return Optional.of(partidoRepository.save(partido));
    }

    public List<Partido> obtenerPartidos() {
        return partidoRepository.findAll();
    }

    public Optional<Partido> obtenerPartidoPorId(Long id) {
        return partidoRepository.findById(id);
    }

    public void eliminarPartido(Long id) {
        partidoRepository.deleteById(id);
    }
}

package com.uade.tpo.service;

import com.uade.tpo.dto.PartidoCreateDTO;
import com.uade.tpo.model.Equipo;
import com.uade.tpo.model.Partido;
import com.uade.tpo.repository.EquipoRepository;
import com.uade.tpo.repository.PartidoRepository;
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
        Optional<Equipo> equipoLocal = equipoRepository.findById(dto.getEquipoLocalId());
        Optional<Equipo> equipoVisitante = equipoRepository.findById(dto.getEquipoVisitanteId());

        if (equipoLocal.isPresent() && equipoVisitante.isPresent()) {
            Partido partido = new Partido();
            partido.setEquipoLocal(equipoLocal.get());
            partido.setEquipoVisitante(equipoVisitante.get());
            partido.setFecha(dto.getFecha());
            return Optional.of(partidoRepository.save(partido));
        }
        return Optional.empty();
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

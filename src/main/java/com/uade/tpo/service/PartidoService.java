package com.uade.tpo.service;

import com.uade.tpo.dto.PartidoCreateDTO;
import com.uade.tpo.model.Equipo;
import com.uade.tpo.model.Partido;
import com.uade.tpo.model.Usuario;
import com.uade.tpo.repository.EquipoRepository;
import com.uade.tpo.repository.PartidoRepository;
import com.uade.tpo.repository.UsuarioDeporteRepository;
import com.uade.tpo.repository.UsuarioRepository;
import com.uade.tpo.strategy.EmparejamientoResultado;
import com.uade.tpo.strategy.EmparejamientoStrategy;
import com.uade.tpo.strategy.impl.EmparejarPorHistorial;
import com.uade.tpo.strategy.impl.EmparejarPorNivel;
import com.uade.tpo.strategy.impl.EmparejarPorUbicacion;
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

    // Estrategia de emparejamiento
    private EmparejamientoStrategy estrategiaSeleccionada;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioDeporteRepository usuarioDeporteRepository;

    public boolean seleccionarEstrategia(String tipo) {
        return switch (tipo.toLowerCase()) {
            case "nivel" -> {
                this.estrategiaSeleccionada = new EmparejarPorNivel(partidoRepository, usuarioDeporteRepository);
                yield true;
            }
            case "historial" -> {
                this.estrategiaSeleccionada = new EmparejarPorHistorial(partidoRepository);
                yield true;
            }
            default -> false;
        };
    }

    public List<EmparejamientoResultado> emparejar() {
        if (this.estrategiaSeleccionada == null)
            throw new IllegalStateException("No se ha seleccionado una estrategia de emparejamiento");

        List<Usuario> usuarios = usuarioRepository.findAll();
        return estrategiaSeleccionada.emparejar(usuarios);
    }
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

package com.uade.tpo.service;

import com.uade.tpo.dto.EstadisticaCreateDTO;
import com.uade.tpo.dto.EstadisticaUpdateDTO;
import com.uade.tpo.model.Estadistica;
import com.uade.tpo.model.Partido;
import com.uade.tpo.model.Usuario;
import com.uade.tpo.model.Equipo;
import com.uade.tpo.repository.EstadisticaRepository;
import com.uade.tpo.repository.PartidoRepository;
import com.uade.tpo.repository.UsuarioRepository;
import com.uade.tpo.repository.EquipoJugadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@Service
public class EstadisticaService {

    @Autowired
    private EstadisticaRepository estadisticaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PartidoRepository partidoRepository;
    @Autowired
    private EquipoJugadorRepository equipoJugadorRepository;

    public Optional<Estadistica> crearEstadistica(EstadisticaCreateDTO dto) {
        Partido partido = partidoRepository.findById(dto.getPartidoId())
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));

        if (partido.getEstado() == null ||
                !partido.getEstado().getClass().getSimpleName().equals("Finalizado")) {
            throw new RuntimeException("Solo se pueden agregar estadísticas a partidos finalizados");
        }

        Usuario usuario = usuarioRepository.findById(dto.getJugadorId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean participoEnPartido = partido.getEquipos().stream()
                .anyMatch(equipo -> equipoJugadorRepository.findByEquipoAndUsuario(equipo, usuario).isPresent());

        if (!participoEnPartido) {
            throw new RuntimeException("Solo los jugadores que participaron del partido pueden agregar estadísticas");
        }

        if (estadisticaRepository.findByPartidoAndJugador(partido, usuario).isPresent()) {
            throw new RuntimeException("Ya existe una estadística para este jugador en este partido");
        }

        Estadistica estadistica = new Estadistica();
        estadistica.setJugador(usuario);
        estadistica.setPartido(partido);
        estadistica.setAnotaciones(dto.getAnotaciones());
        estadistica.setAsistencias(dto.getAsistencias());
        estadistica.setAmonestaciones(dto.getAmonestaciones());
        estadistica.setMejorJugador(dto.isMejorJugador());

        Estadistica estadisticaGuardada = estadisticaRepository.save(estadistica);

        calcularYActualizarEquipoGanador(partido);

        return Optional.of(estadisticaGuardada);
    }

    public Optional<Estadistica> modificarEstadistica(Long estadisticaId, EstadisticaUpdateDTO dto) {
        Estadistica estadistica = estadisticaRepository.findById(estadisticaId)
                .orElseThrow(() -> new RuntimeException("Estadística no encontrada"));

        Partido partido = estadistica.getPartido();
        if (partido.getEstado() == null ||
                !partido.getEstado().getClass().getSimpleName().equals("Finalizado")) {
            throw new RuntimeException("Solo se pueden modificar estadísticas de partidos finalizados");
        }

        estadistica.setAnotaciones(dto.getAnotaciones());
        estadistica.setAsistencias(dto.getAsistencias());
        estadistica.setAmonestaciones(dto.getAmonestaciones());
        estadistica.setMejorJugador(dto.isMejorJugador());

        Estadistica estadisticaActualizada = estadisticaRepository.save(estadistica);

        calcularYActualizarEquipoGanador(partido);

        return Optional.of(estadisticaActualizada);
    }

    private void calcularYActualizarEquipoGanador(Partido partido) {
        List<Estadistica> estadisticas = estadisticaRepository.findByPartidoId(partido.getId());

        Map<Equipo, Integer> anotacionesPorEquipo = new HashMap<>();

        for (Equipo equipo : partido.getEquipos()) {
            anotacionesPorEquipo.put(equipo, 0);
        }

        for (Estadistica estadistica : estadisticas) {
            Usuario jugador = estadistica.getJugador();

            for (Equipo equipo : partido.getEquipos()) {
                if (equipoJugadorRepository.findByEquipoAndUsuario(equipo, jugador).isPresent()) {
                    int anotacionesActuales = anotacionesPorEquipo.get(equipo);
                    anotacionesPorEquipo.put(equipo, anotacionesActuales + estadistica.getAnotaciones());
                    break;
                }
            }
        }

        Equipo equipoGanador = null;
        int maxAnotaciones = -1;

        for (Map.Entry<Equipo, Integer> entry : anotacionesPorEquipo.entrySet()) {
            if (entry.getValue() > maxAnotaciones) {
                maxAnotaciones = entry.getValue();
                equipoGanador = entry.getKey();
            }
        }

        boolean hayEmpate = false;
        for (Map.Entry<Equipo, Integer> entry : anotacionesPorEquipo.entrySet()) {
            if (entry.getValue() == maxAnotaciones && !entry.getKey().equals(equipoGanador)) {
                hayEmpate = true;
                break;
            }
        }

        if (hayEmpate) {
            partido.setGanador(null);
        } else {
            partido.setGanador(equipoGanador);
        }

        partidoRepository.save(partido);
    }

    public List<Estadistica> obtenerEstadisticasPorPartido(Long partidoId) {
        if (!partidoRepository.existsById(partidoId)) {
            throw new RuntimeException("Partido no encontrado");
        }

        return estadisticaRepository.findByPartidoId(partidoId);
    }

    public void eliminarEstadistica(Long id) {
        estadisticaRepository.deleteById(id);
    }
}

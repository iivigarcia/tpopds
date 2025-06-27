package com.uade.tpo.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.dto.ComentarioCreateDTO;
import com.uade.tpo.dto.ComentarioDTO;
import com.uade.tpo.dto.EquipoDTO;
import com.uade.tpo.dto.ErrorResponseDTO;
import com.uade.tpo.dto.EstadisticaCreateDTO;
import com.uade.tpo.dto.EstadisticaDTO;
import com.uade.tpo.dto.EstadisticaUpdateDTO;
import com.uade.tpo.dto.JugadorSimpleDTO;
import com.uade.tpo.dto.ParticipacionSimpleDTO;
import com.uade.tpo.dto.PartidoCreateDTO;
import com.uade.tpo.dto.PartidoDTO;
import com.uade.tpo.dto.UsuarioDTO;
import com.uade.tpo.model.Comentario;
import com.uade.tpo.model.EquipoJugador;
import com.uade.tpo.model.Estadistica;
import com.uade.tpo.model.Geolocalization;
import com.uade.tpo.model.Partido;
import com.uade.tpo.model.Usuario;
import com.uade.tpo.model.notification.NotificationManager;
import com.uade.tpo.repository.EquipoJugadorRepository;
import com.uade.tpo.repository.GeolocalizationRepository;
import com.uade.tpo.service.ComentarioService;
import com.uade.tpo.service.EstadisticaService;
import com.uade.tpo.service.PartidoCronService;
import com.uade.tpo.service.PartidoService;

@RestController
@RequestMapping("/api/partidos")
public class PartidoController {

    @Autowired
    private PartidoService partidoService;

    @Autowired
    private GeolocalizationRepository geolocalizationRepository;

    @Autowired
    private EquipoJugadorRepository equipoJugadorRepository;

    @Autowired
    private PartidoCronService partidoCronService;

    @Autowired
    private ComentarioService comentarioService;

    @Autowired
    private NotificationManager notificationManager;
    private EstadisticaService estadisticaService;

    private UsuarioDTO convertUsuarioToDto(Usuario usuario) {
        if (usuario == null)
            return null;
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setUsername(usuario.getUsername());
        dto.setEmail(usuario.getEmail());

        Optional<Geolocalization> ubicacion = geolocalizationRepository.findById(usuario.getGeolocalizationId());
        dto.setUbicacion(ubicacion.orElse(null));

        return dto;
    }

    private JugadorSimpleDTO convertJugadorToSimpleDto(Usuario usuario) {
        JugadorSimpleDTO dto = new JugadorSimpleDTO();
        dto.setId(usuario.getId());
        dto.setUsername(usuario.getUsername());
        return dto;
    }

    private EquipoDTO convertEquipoToDto(com.uade.tpo.model.Equipo equipo) {
        if (equipo == null)
            return null;
        EquipoDTO dto = new EquipoDTO();
        dto.setId(equipo.getId());
        dto.setNombre(equipo.getNombre());

        if (equipo.getJugadores() != null) {
            dto.setJugadores(equipo.getJugadores().stream()
                    .map(this::convertJugadorToSimpleDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private ComentarioDTO convertComentarioToDto(Comentario comentario) {
        ComentarioDTO dto = new ComentarioDTO();
        dto.setId(comentario.getId());
        dto.setMensaje(comentario.getMensaje());
        dto.setFecha(comentario.getFecha());
        dto.setPartidoId(comentario.getPartido().getId());
        dto.setJugador(convertUsuarioToDto(comentario.getJugador()));
        return dto;
    }

    private EstadisticaDTO convertEstadisticaToDto(Estadistica estadistica) {
        EstadisticaDTO dto = new EstadisticaDTO();
        dto.setId(estadistica.getId());
        dto.setPartidoId(estadistica.getPartido().getId());
        dto.setJugador(convertUsuarioToDto(estadistica.getJugador()));
        dto.setAnotaciones(estadistica.getAnotaciones());
        dto.setAsistencias(estadistica.getAsistencias());
        dto.setAmonestaciones(estadistica.getAmonestaciones());
        dto.setMejorJugador(estadistica.isMejorJugador());
        return dto;
    }

    private ParticipacionSimpleDTO convertEquipoJugadorToSimpleDto(EquipoJugador equipoJugador) {
        ParticipacionSimpleDTO dto = new ParticipacionSimpleDTO();
        dto.setEquipoId(equipoJugador.getEquipo().getId());
        dto.setUsuarioId(equipoJugador.getUsuario().getId());
        dto.setInscrito(equipoJugador.isInscrito());
        dto.setConfirmado(equipoJugador.isConfirmado());
        return dto;
    }

    private PartidoDTO convertToDto(Partido partido) {
        PartidoDTO dto = new PartidoDTO();
        dto.setId(partido.getId());
        dto.setDeporte(partido.getDeporte());
        dto.setFecha(partido.getFecha());
        dto.setHora(partido.getHora());
        dto.setDuracionMinutos(partido.getDuracionMinutos());
        dto.setOrganizador(convertUsuarioToDto(partido.getOrganizador()));
        dto.setUbicacion(partido.getGeolocalizationId());
        dto.setEstadoPartido(partido.getEstado() != null ? partido.getEstado().getClass().getSimpleName() : null);
        dto.setEstrategiaEmparejamiento(partido.getEstrategiaEmparejamiento() != null
                ? partido.getEstrategiaEmparejamiento().getClass().getSimpleName()
                : null);

        if (partido.getEquipos() != null) {
            dto.setEquipos(
                    partido.getEquipos().stream().map(this::convertEquipoToDto).collect(Collectors.toList()));
        }

        List<EquipoJugador> participaciones = partido.getEquipos().stream()
                .flatMap(equipo -> equipoJugadorRepository.findByEquipo(equipo).stream())
                .collect(Collectors.toList());
        if (participaciones != null && !participaciones.isEmpty()) {
            dto.setParticipaciones(
                    participaciones.stream().map(this::convertEquipoJugadorToSimpleDto).collect(Collectors.toList()));
        }

        if (partido.getComentarios() != null) {
            dto.setComentarios(
                    partido.getComentarios().stream().map(this::convertComentarioToDto).collect(Collectors.toList()));
        }
        if (partido.getEstadisticas() != null) {
            dto.setEstadisticas(
                    partido.getEstadisticas().stream().map(this::convertEstadisticaToDto).collect(Collectors.toList()));
        }

        if (partido.getGanador() != null) {
            dto.setEquipoGanador(partido.getGanador().getNombre());
        }

        return dto;
    }

    @PostMapping
    public ResponseEntity<PartidoDTO> crearPartido(@RequestBody PartidoCreateDTO createDTO) {
        return partidoService.crearPartido(createDTO)
                .map(partido -> new ResponseEntity<>(convertToDto(partido), HttpStatus.CREATED))
                .orElse(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @GetMapping
    public ResponseEntity<List<PartidoDTO>> obtenerPartidos() {
        List<Partido> partidos = partidoService.obtenerPartidos();
        List<PartidoDTO> dtos = partidos.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartidoDTO> obtenerPartidoPorId(@PathVariable Long id) {
        Optional<Partido> partido = partidoService.obtenerPartidoPorId(id);
        return partido.map(p -> ResponseEntity.ok(convertToDto(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/ubicacion/{ubicacionId}/incompletos")
    public ResponseEntity<List<PartidoDTO>> obtenerPartidosIncompletosPorUbicacion(@PathVariable Integer ubicacionId) {
        List<Partido> partidos = partidoService.obtenerPartidosIncompletosPorUbicacion(ubicacionId);
        List<PartidoDTO> dtos = partidos.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPartido(@PathVariable Long id) {
        partidoService.eliminarPartido(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/estrategia")
    public ResponseEntity<String> setEstrategia(@PathVariable Long id, @RequestParam String estrategia) {
        try {
            partidoService.setEstrategiaEmparejamiento(id, estrategia);
            return ResponseEntity.ok("Estrategia de emparejamiento actualizada correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al actualizar la estrategia: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/emparejar")
    public ResponseEntity<String> emparejarPartido(@PathVariable Long id) {
        try {
            Optional<Partido> partidoOpt = partidoService.obtenerPartidoPorId(id);
            if (partidoOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Partido no encontrado");
            }

            Partido partido = partidoOpt.get();
            if (partido.getEstado() == null ||
                    !partido.getEstado().getClass().getSimpleName().equals("NecesitamosJugadores")) {
                return ResponseEntity.badRequest()
                        .body("El partido debe estar en estado 'NecesitamosJugadores' para poder emparejar");
            }

            partidoService.emparejarPartido(id);
            return ResponseEntity.ok("Emparejamiento ejecutado correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al emparejar el partido: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<String> cancelarPartido(@PathVariable Long id) {
        try {
            partidoService.cancelarPartido(id);
            return ResponseEntity.ok("Partido cancelado correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al cancelar el partido: " + e.getMessage());
        }
    }

    @PostMapping("/{partidoId}/inscribirse")
    public ResponseEntity<String> inscribirse(@PathVariable Long partidoId, @RequestParam Long usuarioId) {
        try {
            partidoService.inscribirUsuario(partidoId, usuarioId);
            return ResponseEntity.ok("Usuario inscrito correctamente al partido");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al inscribir usuario: " + e.getMessage());
        }
    }

    @PutMapping("/{partidoId}/confirmarParticipacion")
    public ResponseEntity<String> confirmarParticipacion(@PathVariable Long partidoId, @RequestParam Long usuarioId) {
        try {
            partidoService.confirmarParticipacion(partidoId, usuarioId);

            Optional<Partido> partidoOpt = partidoService.obtenerPartidoPorId(partidoId);
            if (partidoOpt.isPresent()) {
                Partido partido = partidoOpt.get();
                if (partido.getEstado() != null &&
                        partido.getEstado().getClass().getSimpleName().equals("Confirmado")) {
                    return ResponseEntity.ok("Participación confirmada correctamente. ¡El partido ha sido confirmado!");
                }
            }

            return ResponseEntity.ok("Participación confirmada correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al confirmar participación: " + e.getMessage());
        }
    }

    @PutMapping("/{partidoId}/confirmarTodosLosJugadores")
    public ResponseEntity<String> confirmarTodosLosJugadores(@PathVariable Long partidoId) {
        try {
            partidoService.confirmarTodosLosJugadores(partidoId);

            Optional<Partido> partidoOpt = partidoService.obtenerPartidoPorId(partidoId);
            if (partidoOpt.isPresent()) {
                Partido partido = partidoOpt.get();
                if (partido.getEstado() != null &&
                        partido.getEstado().getClass().getSimpleName().equals("Confirmado")) {
                    return ResponseEntity
                            .ok("Todos los jugadores han sido confirmados. ¡El partido ha sido confirmado!");
                }
            }

            return ResponseEntity.ok("Todos los jugadores han sido confirmados correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error al confirmar todos los jugadores: " + e.getMessage());
        }
    }

    @PutMapping("/{partidoId}/comenzarPartido")
    public ResponseEntity<String> comenzarPartido(@PathVariable Long partidoId) {
        try {
            partidoService.comenzarPartido(partidoId);
            return ResponseEntity.ok("El partido ha comenzado correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al comenzar el partido: " + e.getMessage());
        }
    }

    @PutMapping("/{partidoId}/finalizarPartido")
    public ResponseEntity<String> finalizarPartido(@PathVariable Long partidoId) {
        try {
            partidoService.finalizarPartido(partidoId);
            return ResponseEntity.ok("Partido finalizado correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al finalizar el partido: " + e.getMessage());
        }
    }

    @PostMapping("/cron/ejecutar")
    public ResponseEntity<String> ejecutarCronManual() {
        try {
            partidoCronService.procesarEstadosPartidos();
            return ResponseEntity.ok("Cron job ejecutado manualmente correctamente");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al ejecutar el cron job: " + e.getMessage());
        }
    }

    @PostMapping("/{partidoId}/comentarios")
    public ResponseEntity<?> agregarComentario(@PathVariable Long partidoId,
            @RequestBody ComentarioCreateDTO createDTO) {
        try {
            createDTO.setPartidoId(partidoId);

            Optional<Comentario> comentarioOpt = comentarioService.crearComentario(createDTO);
            if (comentarioOpt.isPresent()) {
                ComentarioDTO comentarioDTO = convertComentarioToDto(comentarioOpt.get());
                return ResponseEntity.status(HttpStatus.CREATED).body(comentarioDTO);
            } else {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponseDTO("BAD_REQUEST", "No se pudo crear el comentario"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("VALIDATION_ERROR", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponseDTO("INTERNAL_ERROR", "Error interno del servidor"));
        }
    }

    @GetMapping("/{partidoId}/comentarios")
    public ResponseEntity<?> obtenerComentarios(@PathVariable Long partidoId) {
        try {
            List<Comentario> comentarios = comentarioService.obtenerComentariosPorPartido(partidoId);
            List<ComentarioDTO> comentariosDTO = comentarios.stream()
                    .map(this::convertComentarioToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(comentariosDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("VALIDATION_ERROR", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponseDTO("INTERNAL_ERROR", "Error interno del servidor"));
        }
    }

    @PutMapping("/notificaciones/estrategia")
    public ResponseEntity<String> cambiarEstrategiaNotificacion(@RequestParam String tipo) {

        notificationManager.setNotificationType(tipo);
        return ResponseEntity.ok("Estrategia de notificación cambiada a: " + tipo.toUpperCase());

    }

    @GetMapping("/notificaciones/estrategia")
    public ResponseEntity<String> obtenerEstrategiaNotificacion() {
        try {
            String currentType = notificationManager.getCurrentNotificationType();
            return ResponseEntity.ok("Estrategia de notificación actual: " + currentType);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error al obtener la estrategia de notificación: " + e.getMessage());
        }
    }

    @PostMapping("/{partidoId}/estadisticas")
    public ResponseEntity<?> agregarEstadistica(@PathVariable Long partidoId,
            @RequestBody EstadisticaCreateDTO createDTO) {
        try {
            createDTO.setPartidoId(partidoId);

            Optional<Estadistica> estadisticaOpt = estadisticaService.crearEstadistica(createDTO);
            if (estadisticaOpt.isPresent()) {
                EstadisticaDTO estadisticaDTO = convertEstadisticaToDto(estadisticaOpt.get());
                return ResponseEntity.status(HttpStatus.CREATED).body(estadisticaDTO);
            } else {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponseDTO("BAD_REQUEST", "No se pudo crear la estadística"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("VALIDATION_ERROR", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponseDTO("INTERNAL_ERROR", "Error interno del servidor"));
        }
    }

    @GetMapping("/{partidoId}/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas(@PathVariable Long partidoId) {
        try {
            List<Estadistica> estadisticas = estadisticaService.obtenerEstadisticasPorPartido(partidoId);
            List<EstadisticaDTO> estadisticasDTO = estadisticas.stream()
                    .map(this::convertEstadisticaToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(estadisticasDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("VALIDATION_ERROR", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponseDTO("INTERNAL_ERROR", "Error interno del servidor"));
        }
    }

    @PutMapping("/estadisticas/{estadisticaId}")
    public ResponseEntity<?> modificarEstadistica(@PathVariable Long estadisticaId,
            @RequestBody EstadisticaUpdateDTO updateDTO) {
        try {
            Optional<Estadistica> estadisticaOpt = estadisticaService.modificarEstadistica(estadisticaId, updateDTO);
            if (estadisticaOpt.isPresent()) {
                EstadisticaDTO estadisticaDTO = convertEstadisticaToDto(estadisticaOpt.get());
                return ResponseEntity.ok(estadisticaDTO);
            } else {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponseDTO("BAD_REQUEST", "No se pudo modificar la estadística"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("VALIDATION_ERROR", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponseDTO("INTERNAL_ERROR", "Error interno del servidor"));

        }
    }

}

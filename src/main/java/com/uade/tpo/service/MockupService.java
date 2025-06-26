package com.uade.tpo.service;

import com.uade.tpo.model.*;
import com.uade.tpo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MockupService {

  @Autowired
  private GeolocalizationRepository geolocalizationRepository;

  @Autowired
  private DeporteRepository deporteRepository;

  @Autowired
  private UsuarioRepository usuarioRepository;

  @Autowired
  private UsuarioDeporteRepository usuarioDeporteRepository;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private PasswordEncoder passwordEncoder;

  public void inicializarDB() {
    crearUbicaciones();
    crearDeportes();
    crearUsuarios();
  }

  public void limpiarDB() {
    usuarioDeporteRepository.deleteAll();
    usuarioRepository.deleteAll();
    deporteRepository.deleteAll();
    geolocalizationRepository.deleteAll();

    resetAutoIncrement();
  }

  private void resetAutoIncrement() {
    jdbcTemplate.execute("ALTER TABLE estadisticas AUTO_INCREMENT = 1");
    jdbcTemplate.execute("ALTER TABLE comentarios AUTO_INCREMENT = 1");
    jdbcTemplate.execute("ALTER TABLE partidos AUTO_INCREMENT = 1");
    jdbcTemplate.execute("ALTER TABLE equipos AUTO_INCREMENT = 1");
    jdbcTemplate.execute("ALTER TABLE usuarios AUTO_INCREMENT = 1");
    jdbcTemplate.execute("ALTER TABLE deportes AUTO_INCREMENT = 1");
    jdbcTemplate.execute("ALTER TABLE geolocalizations AUTO_INCREMENT = 1");
  }

  private void crearUbicaciones() {
    List<Geolocalization> ubicaciones = Arrays.asList(
        createGeolocalization("Palermo", "-34.5889", "-58.4107"),
        createGeolocalization("Recoleta", "-34.5895", "-58.3924"),
        createGeolocalization("San Telmo", "-34.6205", "-58.3736"),
        createGeolocalization("La Boca", "-34.6345", "-58.3631"),
        createGeolocalization("Puerto Madero", "-34.6084", "-58.3731"),
        createGeolocalization("Belgrano", "-34.5627", "-58.4563"),
        createGeolocalization("Caballito", "-34.6215", "-58.4410"),
        createGeolocalization("Villa Crespo", "-34.5957", "-58.4434"),
        createGeolocalization("Almagro", "-34.6089", "-58.4156"),
        createGeolocalization("Boedo", "-34.6333", "-58.4167"));

    for (Geolocalization ubicacion : ubicaciones) {
      geolocalizationRepository.save(ubicacion);
    }
  }

  private Geolocalization createGeolocalization(String nombre, String lat, String lng) {
    Geolocalization ubicacion = new Geolocalization();
    ubicacion.setNombre(nombre);
    ubicacion.setLat(lat);
    ubicacion.setLng(lng);
    return ubicacion;
  }

  private void crearDeportes() {
    List<Deporte> deportes = Arrays.asList(
        createDeporte("Basquet"),
        createDeporte("Football"),
        createDeporte("Padel"),
        createDeporte("Tenis"),
        createDeporte("Voley"));

    for (Deporte deporte : deportes) {
      deporteRepository.save(deporte);
    }
  }

  private Deporte createDeporte(String nombre) {
    Deporte deporte = new Deporte();
    deporte.setNombre(nombre);
    return deporte;
  }

  private void crearUsuarios() {
    List<Geolocalization> ubicaciones = geolocalizationRepository.findAll();
    List<Deporte> deportes = deporteRepository.findAll();
    NivelJuego[] niveles = NivelJuego.values();
    Random random = new Random();

    String[] nombres = {
        "Juan", "María", "Carlos", "Ana", "Luis", "Carmen", "Miguel", "Isabel", "Pedro", "Rosa",
        "Jorge", "Lucía", "Fernando", "Elena", "Roberto", "Patricia", "Diego", "Silvia", "Andrés", "Claudia",
        "Ricardo", "Mónica", "Francisco", "Verónica", "Alejandro", "Natalia", "Daniel", "Valeria", "Eduardo", "Gabriela"
    };

    Geolocalization ubicacionComun = ubicaciones.get(0);
    Deporte deporteComun = deportes.get(1);

    for (int i = 1; i <= 30; i++) {
      String nombreReal = nombres[i - 1];
      int numeroAleatorio = random.nextInt(900) + 100;
      String username = nombreReal + numeroAleatorio;

      Usuario usuario = new Usuario();
      usuario.setUsername(username);
      usuario.setEmail(username.toLowerCase() + "@email.com");
      usuario.setPassword(passwordEncoder.encode("password123"));

      if (i <= 20) {
        usuario.setGeolocalizationId(ubicacionComun.getId());
      } else {
        usuario.setGeolocalizationId(ubicaciones.get(random.nextInt(ubicaciones.size())).getId());
      }

      usuarioRepository.save(usuario);

      UsuarioDeporte usuarioDeporteFavorito = new UsuarioDeporte();
      usuarioDeporteFavorito.setUsuario(usuario);

      if (i <= 20) {
        usuarioDeporteFavorito.setDeporte(deporteComun);
      } else {
        usuarioDeporteFavorito.setDeporte(deportes.get(random.nextInt(deportes.size())));
      }

      usuarioDeporteFavorito.setNivelDeJuego(niveles[random.nextInt(niveles.length)]);
      usuarioDeporteFavorito.setDeporteFavorito(true);
      usuarioDeporteRepository.save(usuarioDeporteFavorito);

      List<Deporte> deportesDisponibles = new ArrayList<>(deportes);
      deportesDisponibles.remove(usuarioDeporteFavorito.getDeporte());

      int deportesAdicionales = random.nextInt(3) + 1;

      for (int j = 0; j < deportesAdicionales && j < deportesDisponibles.size(); j++) {
        Deporte deporteAdicional = deportesDisponibles.get(random.nextInt(deportesDisponibles.size()));
        deportesDisponibles.remove(deporteAdicional);

        UsuarioDeporte usuarioDeporte = new UsuarioDeporte();
        usuarioDeporte.setUsuario(usuario);
        usuarioDeporte.setDeporte(deporteAdicional);
        usuarioDeporte.setNivelDeJuego(niveles[random.nextInt(niveles.length)]);
        usuarioDeporte.setDeporteFavorito(false);
        usuarioDeporteRepository.save(usuarioDeporte);
      }
    }
  }
}
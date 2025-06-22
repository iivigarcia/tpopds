package com.uade.tpo.repository;

import com.uade.tpo.model.Deporte;
import com.uade.tpo.model.Usuario;
import com.uade.tpo.model.UsuarioDeporte;
import com.uade.tpo.model.UsuarioDeporteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioDeporteRepository extends JpaRepository<UsuarioDeporte, UsuarioDeporteId> {

  List<UsuarioDeporte> findByUsuario(Usuario usuario);

  List<UsuarioDeporte> findByDeporte(Deporte deporte);

  Optional<UsuarioDeporte> findByUsuarioAndDeporteFavorito(Usuario usuario, boolean deporteFavorito);

  List<UsuarioDeporte> findByUsuarioId(Long usuarioId);
}
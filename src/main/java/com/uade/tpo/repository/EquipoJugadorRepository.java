package com.uade.tpo.repository;

import com.uade.tpo.model.EquipoJugador;
import com.uade.tpo.model.EquipoJugadorId;
import com.uade.tpo.model.Equipo;
import com.uade.tpo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipoJugadorRepository extends JpaRepository<EquipoJugador, EquipoJugadorId> {

  List<EquipoJugador> findByEquipo(Equipo equipo);

  Optional<EquipoJugador> findByEquipoAndUsuario(Equipo equipo, Usuario usuario);

  List<EquipoJugador> findByConfirmado(boolean confirmado);

  List<EquipoJugador> findByInscrito(boolean inscrito);
}
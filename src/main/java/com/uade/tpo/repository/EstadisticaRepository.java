package com.uade.tpo.repository;

import com.uade.tpo.model.Estadistica;
import com.uade.tpo.model.Partido;
import com.uade.tpo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstadisticaRepository extends JpaRepository<Estadistica, Long> {
  List<Estadistica> findByPartidoId(Long partidoId);

  Optional<Estadistica> findByPartidoAndJugador(Partido partido, Usuario jugador);
}

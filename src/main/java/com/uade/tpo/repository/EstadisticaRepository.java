package com.uade.tpo.repository;

import com.uade.tpo.model.Estadistica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstadisticaRepository extends JpaRepository<Estadistica, Long> {
  List<Estadistica> findByPartidoId(Long partidoId);
}

package com.uade.tpo.repository;

import com.uade.tpo.model.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
  List<Comentario> findByPartidoId(Long partidoId);
}

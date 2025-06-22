package com.uade.tpo.service;

import com.uade.tpo.repository.EstadisticaRepository;
import com.uade.tpo.model.Estadistica;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstadisticaService {

    @Autowired
    private EstadisticaRepository estadisticaRepository;

    public List<Estadistica> findAll() {
        return estadisticaRepository.findAll();
    }

    public Optional<Estadistica> findById(Long id) {
        return estadisticaRepository.findById(id);
    }

    public Estadistica save(Estadistica estadistica) {
        return estadisticaRepository.save(estadistica);
    }

    public void delete(Long id) {
        estadisticaRepository.deleteById(id);
    }
}

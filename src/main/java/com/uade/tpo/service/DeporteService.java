package com.uade.tpo.service;

import com.uade.tpo.repository.DeporteRepository;
import com.uade.tpo.model.Deporte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeporteService {

    @Autowired
    private DeporteRepository deporteRepository;

    public List<Deporte> findAll() {
        return deporteRepository.findAll();
    }

    public Optional<Deporte> findById(Long id) {
        return deporteRepository.findById(id);
    }

    public Deporte save(Deporte deporte) {
        return deporteRepository.save(deporte);
    }

    public void delete(Long id) {
        deporteRepository.deleteById(id);
    }
}

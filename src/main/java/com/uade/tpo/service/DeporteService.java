package com.uade.tpo.service;

import com.uade.tpo.dto.DeporteCreateDTO;
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

    public Deporte crearDeporte(DeporteCreateDTO deporteCreateDTO) {
        Deporte deporte = new Deporte();
        deporte.setNombre(deporteCreateDTO.getNombre());
        return deporteRepository.save(deporte);
    }

    public List<Deporte> obtenerDeportes() {
        return deporteRepository.findAll();
    }

    public Optional<Deporte> obtenerDeportePorId(Long id) {
        return deporteRepository.findById(id);
    }

    public Deporte modificarDeporte(Long id, DeporteCreateDTO deporteCreateDTO) {
        Optional<Deporte> deporteExistente = deporteRepository.findById(id);
        if (deporteExistente.isPresent()) {
            Deporte deporte = deporteExistente.get();
            deporte.setNombre(deporteCreateDTO.getNombre());
            return deporteRepository.save(deporte);
        }
        return null;
    }

    public void eliminarDeporte(Long id) {
        deporteRepository.deleteById(id);
    }

}

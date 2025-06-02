package com.uade.tpo.controller;

import com.uade.tpo.model.Equipo;
import com.uade.tpo.service.EquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/equipos")
public class EquipoController {

    @Autowired
    private EquipoService equipoService;

    @GetMapping
    public List<Equipo> findAll() {
        return equipoService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Equipo> findById(@PathVariable Long id) {
        return equipoService.findById(id);
    }

    @PostMapping
    public Equipo create(@RequestBody Equipo equipo) {
        return equipoService.save(equipo);
    }

    @PutMapping("/{id}")
    public Equipo update(@PathVariable Long id, @RequestBody Equipo equipo) {
        equipo.setId(id);
        return equipoService.save(equipo);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        equipoService.delete(id);
    }
}

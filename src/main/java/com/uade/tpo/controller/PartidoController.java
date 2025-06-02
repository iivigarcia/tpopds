package com.uade.tpo.controller;

import com.uade.tpo.model.Partido;
import com.uade.tpo.service.PartidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/partidos")
public class PartidoController {

    @Autowired
    private PartidoService partidoService;

    @GetMapping
    public List<Partido> findAll() {
        return partidoService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Partido> findById(@PathVariable Long id) {
        return partidoService.findById(id);
    }

    @PostMapping
    public Partido create(@RequestBody Partido partido) {
        return partidoService.save(partido);
    }

    @PutMapping("/{id}")
    public Partido update(@PathVariable Long id, @RequestBody Partido partido) {
        partido.setId(id);
        return partidoService.save(partido);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        partidoService.delete(id);
    }
}

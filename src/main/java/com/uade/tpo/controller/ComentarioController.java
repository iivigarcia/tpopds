package com.uade.tpo.controller;

import com.uade.tpo.model.Comentario;
import com.uade.tpo.service.ComentarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comentarios")
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService;

    @GetMapping
    public List<Comentario> findAll() {
        return comentarioService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Comentario> findById(@PathVariable Long id) {
        return comentarioService.findById(id);
    }

    @PostMapping
    public Comentario create(@RequestBody Comentario comentario) {
        return comentarioService.save(comentario);
    }

    @PutMapping("/{id}")
    public Comentario update(@PathVariable Long id, @RequestBody Comentario comentario) {
        comentario.setId(id);
        return comentarioService.save(comentario);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        comentarioService.delete(id);
    }
}

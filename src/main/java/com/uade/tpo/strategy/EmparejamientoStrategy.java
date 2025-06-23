package com.uade.tpo.strategy;

import com.uade.tpo.model.Usuario;

import java.util.List;

public interface EmparejamientoStrategy {
    List<EmparejamientoResultado> emparejar(List<Usuario> usuarios);
}
package com.uade.tpo.dto;

import com.uade.tpo.model.NivelJuego;
import lombok.Data;

@Data
public class UsuarioDeporteDTO {
    private String username;
    private String deporte;
    private NivelJuego nivelDeJuego;
    private boolean deporteFavorito;
}

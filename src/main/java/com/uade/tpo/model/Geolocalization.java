package com.uade.tpo.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "geolocalizations")
public class Geolocalization {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "nombre", nullable = false)
  private String nombre;

  @Column(name = "lat", nullable = false)
  private String lat;

  @Column(name = "lng", nullable = false)
  private String lng;
}
package com.uade.tpo.repository;

import com.uade.tpo.model.Geolocalization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeolocalizationRepository extends JpaRepository<Geolocalization, Integer> {
}
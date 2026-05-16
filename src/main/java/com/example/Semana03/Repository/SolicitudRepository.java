package com.example.Semana03.Repository;

import com.example.Semana03.Model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    // JpaRepository ya trae el método findAll() incluido por defecto
}
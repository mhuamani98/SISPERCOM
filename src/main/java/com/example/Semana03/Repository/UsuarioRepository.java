package com.example.Semana03.Repository;

import com.example.Semana03.Model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {
    // Este método es vital para que el Service pueda buscar por nombre
    UsuarioModel findByUsername(String username);
}
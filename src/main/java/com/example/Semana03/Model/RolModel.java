package com.example.Semana03.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rol") 
public class RolModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre; // Ejemplo: 'ROLE_ADMIN', 'ROLE_EVALUADOR', 'ROLE_USER'

    // Usamos @JsonIgnore para evitar errores de recursividad
    // Usamos @ToString.Exclude para que Lombok no cause errores al imprimir el objeto
    @OneToMany(mappedBy = "rol")
    @JsonIgnore
    @ToString.Exclude
    private List<UsuarioModel> usuarios;
}
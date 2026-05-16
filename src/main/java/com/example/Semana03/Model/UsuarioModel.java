package com.example.Semana03.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
public class UsuarioModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password; // Aquí guardaremos el hash de BCrypt

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id", nullable = false)
    private RolModel rol; 

    // MODO EXPERTO: Método para obtener el nombre del rol directamente
    // Ayuda a que el UsuarioService no tenga que buscar dentro del objeto Rol
    public String getRoleName() {
        return rol != null ? rol.getNombre() : "ROLE_USER";
    }
}
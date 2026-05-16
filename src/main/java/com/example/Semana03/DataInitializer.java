package com.example.Semana03;

import com.example.Semana03.Model.RolModel;
import com.example.Semana03.Model.UsuarioModel;
import com.example.Semana03.Repository.UsuarioRepository;
import com.example.Semana03.Repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RolRepository rolRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    // Quitamos el PasswordEncoder porque ya no usamos Spring Security

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepo.count() == 0) {
            // 1. Creamos los roles
            RolModel adminRol = new RolModel();
            adminRol.setNombre("ADMIN");
            rolRepo.save(adminRol);

            RolModel evalRol = new RolModel();
            evalRol.setNombre("EVALUADOR");
            rolRepo.save(evalRol);

            RolModel abogadoRol = new RolModel();
            abogadoRol.setNombre("ABOGADO");
            rolRepo.save(abogadoRol);

            RolModel usuarioRol = new RolModel();
            usuarioRol.setNombre("USUARIO");
            rolRepo.save(usuarioRol);

            // 2. Creamos el Admin (Texto plano)
            UsuarioModel admin = new UsuarioModel();
            admin.setUsername("admin");
            admin.setPassword("admin123"); 
            admin.setRol(adminRol); 
            usuarioRepo.save(admin);

            // 3. Creamos el Evaluador (Texto plano)
            UsuarioModel evaluador = new UsuarioModel();
            evaluador.setUsername("evaluador");
            evaluador.setPassword("eval123");
            evaluador.setRol(evalRol); 
            usuarioRepo.save(evaluador);
            
            // 4. Creamos el Abogado (Texto plano)
            UsuarioModel abogado = new UsuarioModel();
            abogado.setUsername("abogado");
            abogado.setPassword("abogado123");
            abogado.setRol(abogadoRol);
            usuarioRepo.save(abogado);
            
            System.out.println("✅ Usuarios de prueba (SIN ENCRIPTAR) listos.");
        }
    }
}
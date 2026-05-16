package com.example.Semana03.Controller;

import com.example.Semana03.Repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UsuarioRepository usuarioRepo;
    
    /*@GetMapping("/dashboard")
    public String dashboard() {
        return "admin_dashboard"; 
    }*/
    

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {

        model.addAttribute(
                "usuarios",
                usuarioRepo.findAll()
        );

        return "admin_usuarios";
    }
    
    
}
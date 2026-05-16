package com.example.Semana03.Controller;

import com.example.Semana03.Repository.SolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private SolicitudRepository solicitudRepo;

    @GetMapping("/usuario/home")
    public String homeUsuario(Model model) {
        // Carga todas las solicitudes para el historial del usuario
        model.addAttribute("solicitudes", solicitudRepo.findAll());
        return "usuario_home"; 
    }
}
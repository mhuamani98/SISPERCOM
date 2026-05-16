package com.example.Semana03.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

@Controller 
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login"; //
    }

    @GetMapping("/home-redirect")
    public String homeRedirect(Authentication auth) {
        // Extraemos los roles (Asegúrate de que en MySQL digan ROLE_...)
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
        boolean esAbogado = auth.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ABOGADO"));
        boolean esEvaluador = auth.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_EVALUADOR"));
        boolean esUsuario = auth.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_USUARIO"));

        // Redirecciones basadas en tus nuevos Mapping del SolicitudController
        if (esAdmin || esUsuario) {
    return "redirect:/solicitud/usuario_home";
}
        if (esAbogado) {
            return "redirect:/solicitud/home-abogado"; // Ruta corregida según SolicitudController
        }
        if (esEvaluador) {
            return "redirect:/solicitud/home-evaluador"; // Ruta corregida según SolicitudController
        }
        return "redirect:/usuario_home"; //
    }
}
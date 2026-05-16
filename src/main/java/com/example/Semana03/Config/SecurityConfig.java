package com.example.Semana03.Config;

import com.example.Semana03.Service.UsuarioService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth
                // 1. Recursos públicos (Sin esto, el login se ve feo o falla)
                .requestMatchers("/login", "/css/**", "/js/**", "/images/**").permitAll()
                    // 2. Permiso para ver el certificado (CLAVE PARA QUITAR EL ERROR 403)
            .requestMatchers("/solicitud/ver-certificado/**").permitAll()
                
                // Cambia .hasRole por .hasAuthority
.requestMatchers("/admin/**").hasRole("ADMIN")
.requestMatchers("/abogado/**").hasRole("ABOGADO")
.requestMatchers("/evaluador/**").hasRole("EVALUADOR")
.requestMatchers("/usuario**", "/usuario_home").hasAnyRole("ADMIN", "USUARIO")
                // 4. Todo lo demás requiere autenticación
                .anyRequest().authenticated() 
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login") 
                .defaultSuccessUrl("/home-redirect", true) 
                .failureUrl("/login?error") 
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .deleteCookies("JSESSIONID") // Limpia la sesión por completo
                .permitAll()
            );
        
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UsuarioService usuarioService) {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(usuarioService); 
        auth.setPasswordEncoder(passwordEncoder()); 
        return auth;
    }
}
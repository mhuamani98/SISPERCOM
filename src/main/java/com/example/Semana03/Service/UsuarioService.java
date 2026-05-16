package com.example.Semana03.Service;

import com.example.Semana03.Model.UsuarioModel;
import com.example.Semana03.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class UsuarioService implements UserDetailsService {
    @Autowired
    private UsuarioRepository repo;

    @Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UsuarioModel user = repo.findByUsername(username);
    if (user == null) throw new UsernameNotFoundException("No existe");

    String rolNom = user.getRol().getNombre();
    if (!rolNom.startsWith("ROLE_")) rolNom = "ROLE_" + rolNom;

    return User.withUsername(user.getUsername())
               .password(user.getPassword())
               .authorities(rolNom)
               .build();

    }
}
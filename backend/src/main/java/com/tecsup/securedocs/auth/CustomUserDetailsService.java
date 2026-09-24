package com.tecsup.securedocs.auth;

import com.tecsup.securedocs.user.User;
import com.tecsup.securedocs.user.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo)
            throws UsernameNotFoundException {

        User user = userRepository.findByCorreo(correo)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuario no encontrado"
                        ));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getCorreo())
                .password(user.getPassword())
                .roles(user.getRol().getNombre())
                .disabled(!"ACTIVO".equalsIgnoreCase(user.getEstado()))
                .build();
    }
}
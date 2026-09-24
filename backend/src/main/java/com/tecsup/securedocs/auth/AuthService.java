package com.tecsup.securedocs.auth;

import com.tecsup.securedocs.user.User;
import com.tecsup.securedocs.user.UserRepository;
import org.springframework.security.authentication.*;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.correo(),
                        request.password()
                )
        );

        User user = userRepository.findByCorreo(request.correo())
                .orElseThrow();

        String token = jwtService.generateToken(
                user.getCorreo(),
                user.getRol().getNombre()
        );

        return new LoginResponse(
                token,
                "Bearer",
                user.getCorreo(),
                user.getRol().getNombre()
        );
    }
}
package com.tecsup.securedocs.user;

import com.tecsup.securedocs.authorization.Action;
import com.tecsup.securedocs.authorization.AuthorizationResult;
import com.tecsup.securedocs.authorization.AuthorizationService;
import com.tecsup.securedocs.authorization.abac.Environment;
import com.tecsup.securedocs.authorization.abac.EnvironmentFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UserController {

    private final UserService userService;
    private final AuthorizationService authorizationService;
    private final EnvironmentFactory environmentFactory;

    public UserController(
            UserService userService,
            AuthorizationService authorizationService,
            EnvironmentFactory environmentFactory
    ) {
        this.userService = userService;
        this.authorizationService = authorizationService;
        this.environmentFactory = environmentFactory;
    }

    private User currentUser() {

        String correo =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        return userService.findAll()
                .stream()
                .filter(user ->
                        user.getCorreo().equals(correo))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Usuario autenticado no encontrado"
                        ));
    }

    @GetMapping
    public ResponseEntity<?> getAll(
            HttpServletRequest request
    ) {

        User currentUser = currentUser();

        Environment environment =
                environmentFactory.fromRequest(request);

        AuthorizationResult authorization =
                authorizationService.authorize(
                        currentUser,
                        Action.MANAGE_USERS,
                        environment
                );

        if (!authorization.allowed()) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(authorization.reason());
        }

        List<UserResponse> users =
                userService.findAll()
                        .stream()
                        .map(UserResponse::from)
                        .toList();

        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody UserRequest requestBody,
            HttpServletRequest request
    ) {

        User currentUser = currentUser();

        Environment environment =
                environmentFactory.fromRequest(request);

        AuthorizationResult authorization =
                authorizationService.authorize(
                        currentUser,
                        Action.MANAGE_USERS,
                        environment
                );

        if (!authorization.allowed()) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(authorization.reason());
        }

        User user = userService.create(requestBody);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(UserResponse.from(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody UserRequest requestBody,
            HttpServletRequest request
    ) {

        User currentUser = currentUser();

        Environment environment =
                environmentFactory.fromRequest(request);

        AuthorizationResult manageUsers =
                authorizationService.authorize(
                        currentUser,
                        Action.MANAGE_USERS,
                        environment
                );

        if (!manageUsers.allowed()) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(manageUsers.reason());
        }

        AuthorizationResult assignRoles =
                authorizationService.authorize(
                        currentUser,
                        Action.ASSIGN_ROLES,
                        environment
                );

        if (!assignRoles.allowed()) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(assignRoles.reason());
        }

        User user =
                userService.update(
                        id,
                        requestBody
                );

        return ResponseEntity.ok(
                UserResponse.from(user)
        );
    }
}
package com.tecsup.securedocs.audit;

import com.tecsup.securedocs.authorization.Action;
import com.tecsup.securedocs.authorization.AuthorizationResult;
import com.tecsup.securedocs.authorization.AuthorizationService;
import com.tecsup.securedocs.authorization.abac.Environment;
import com.tecsup.securedocs.authorization.abac.EnvironmentFactory;
import com.tecsup.securedocs.user.User;
import com.tecsup.securedocs.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auditoria")
public class AuditController {

    private final AuditRepository auditRepository;
    private final UserService userService;
    private final AuthorizationService authorizationService;
    private final EnvironmentFactory environmentFactory;

    public AuditController(
            AuditRepository auditRepository,
            UserService userService,
            AuthorizationService authorizationService,
            EnvironmentFactory environmentFactory
    ) {
        this.auditRepository = auditRepository;
        this.userService = userService;
        this.authorizationService = authorizationService;
        this.environmentFactory = environmentFactory;
    }

    @GetMapping
    public ResponseEntity<?> getAll(
            HttpServletRequest request
    ) {

        String correo =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        User user =
                userService.findAll()
                        .stream()
                        .filter(
                                current ->
                                        current.getCorreo()
                                                .equals(correo)
                        )
                        .findFirst()
                        .orElseThrow();

        Environment environment =
                environmentFactory.fromRequest(request);

        AuthorizationResult authorization =
                authorizationService.authorize(
                        user,
                        Action.VIEW_AUDIT,
                        environment
                );

        if (!authorization.allowed()) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(authorization.reason());
        }

        List<AuditResponse> response =
                auditRepository.findAll()
                        .stream()
                        .map(AuditResponse::from)
                        .toList();

        return ResponseEntity.ok(response);
    }
}
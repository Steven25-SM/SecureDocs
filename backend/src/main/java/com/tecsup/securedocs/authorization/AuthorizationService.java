package com.tecsup.securedocs.authorization;

import com.tecsup.securedocs.authorization.abac.AbacContext;
import com.tecsup.securedocs.authorization.abac.AbacService;
import com.tecsup.securedocs.authorization.abac.Environment;
import com.tecsup.securedocs.authorization.abac.PolicyDecision;
import com.tecsup.securedocs.document.Document;
import com.tecsup.securedocs.user.User;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    private final RbacService rbacService;
    private final AbacService abacService;

    public AuthorizationService(
            RbacService rbacService,
            AbacService abacService
    ) {
        this.rbacService = rbacService;
        this.abacService = abacService;
    }

    public AuthorizationResult authorize(
            User user,
            Document document,
            Action action,
            Environment environment
    ) {

        // 1. RBAC
        AuthorizationResult rbacResult =
                rbacService.check(user, action);

        if (!rbacResult.allowed()) {
            return rbacResult;
        }

        // 2. ABAC
        AbacContext context = new AbacContext(
                user,
                document,
                action,
                environment
        );

        PolicyDecision abacResult =
                abacService.check(context);

        if (!abacResult.permitido()) {
            return new AuthorizationResult(
                    false,
                    abacResult.motivo()
            );
        }

        return new AuthorizationResult(
                true,
                "RBAC y ABAC autorizados"
        );
    }

    /*
     * Autorización para operaciones que no trabajan
     * directamente con un documento, por ejemplo:
     * - GET /usuarios
     * - POST /usuarios
     * - PUT /usuarios/{id}
     * - GET /auditoria
     */
    public AuthorizationResult authorize(
            User user,
            Action action,
            Environment environment
    ) {

        // 1. RBAC
        AuthorizationResult rbacResult =
                rbacService.check(user, action);

        if (!rbacResult.allowed()) {
            return rbacResult;
        }

        // 2. ABAC
        AbacContext context = new AbacContext(
                user,
                null,
                action,
                environment
        );

        PolicyDecision abacResult =
                abacService.check(context);

        if (!abacResult.permitido()) {
            return new AuthorizationResult(
                    false,
                    abacResult.motivo()
            );
        }

        return new AuthorizationResult(
                true,
                "RBAC y ABAC autorizados"
        );
    }
}
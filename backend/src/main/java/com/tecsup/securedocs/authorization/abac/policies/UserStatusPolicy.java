package com.tecsup.securedocs.authorization.abac.policies;

import com.tecsup.securedocs.authorization.abac.*;
import org.springframework.stereotype.Component;

@Component
public class UserStatusPolicy implements AbacPolicyRule {

    @Override
    public PolicyDecision evaluate(AbacContext context) {

        if (!"ACTIVO".equalsIgnoreCase(
                context.usuario().getEstado())) {

            return PolicyDecision.denegar(
                    "El usuario no está activo"
            );
        }

        return PolicyDecision.permitir(
                "Usuario activo"
        );
    }
}
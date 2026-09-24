package com.tecsup.securedocs.authorization.abac.policies;

import com.tecsup.securedocs.authorization.abac.*;
import org.springframework.stereotype.Component;

@Component
public class SecurityLevelPolicy implements AbacPolicyRule {

    @Override
    public PolicyDecision evaluate(AbacContext context) {

        int userLevel = context.usuario().getNivelSeguridad();
        int documentLevel = context.documento().getNivelConfidencialidad();

        if (userLevel < documentLevel) {
            return PolicyDecision.denegar(
                    "Nivel de seguridad insuficiente"
            );
        }

        return PolicyDecision.permitir(
                "Nivel de seguridad suficiente"
        );
    }
}
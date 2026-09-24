package com.tecsup.securedocs.authorization.abac.policies;

import com.tecsup.securedocs.authorization.Action;
import com.tecsup.securedocs.authorization.abac.*;
import org.springframework.stereotype.Component;

@Component
public class DevicePolicy implements AbacPolicyRule {

    @Override
    public PolicyDecision evaluate(AbacContext context) {

        if (context.accion() != Action.READ_DOCUMENT
                || context.documento().getNivelConfidencialidad() < 4) {
            return PolicyDecision.noAplica();
        }

        if (!"CORPORATIVO".equalsIgnoreCase(
                context.entorno().dispositivo())) {

            return PolicyDecision.denegar(
                    "El documento requiere un dispositivo corporativo"
            );
        }

        return PolicyDecision.permitir(
                "Dispositivo corporativo autorizado"
        );
    }
}
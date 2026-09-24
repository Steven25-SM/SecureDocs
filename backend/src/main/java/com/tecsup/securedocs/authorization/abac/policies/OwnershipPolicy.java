package com.tecsup.securedocs.authorization.abac.policies;

import com.tecsup.securedocs.authorization.Action;
import com.tecsup.securedocs.authorization.abac.*;
import org.springframework.stereotype.Component;

@Component
public class OwnershipPolicy implements AbacPolicyRule {

    @Override
    public PolicyDecision evaluate(AbacContext context) {

        String role = context.usuario().getRol().getNombre();

        if (!"EMPLEADO".equals(role)
                || context.accion() != Action.UPDATE_DOCUMENT) {
            return PolicyDecision.noAplica();
        }

        boolean owner =
                context.usuario().getId()
                        .equals(context.documento().getPropietario().getId());

        if (!owner) {
            return PolicyDecision.denegar(
                    "El empleado no es propietario del documento"
            );
        }

        return PolicyDecision.permitir(
                "El empleado es propietario del documento"
        );
    }
}
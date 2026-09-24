package com.tecsup.securedocs.authorization.abac.policies;

import com.tecsup.securedocs.authorization.abac.*;
import org.springframework.stereotype.Component;

@Component
public class GuestPolicy implements AbacPolicyRule {

    @Override
    public PolicyDecision evaluate(AbacContext context) {

        if (!"INVITADO".equals(
                context.usuario().getRol().getNombre())) {
            return PolicyDecision.noAplica();
        }

        boolean external =
                "EXTERNO".equalsIgnoreCase(
                        context.usuario().getTipoContrato());

        boolean lowConfidentiality =
                context.documento().getNivelConfidencialidad() <= 1;

        boolean published =
                "PUBLICADO".equalsIgnoreCase(
                        context.documento().getEstado());

        if (!external || !lowConfidentiality || !published) {
            return PolicyDecision.denegar(
                    "El invitado no cumple las condiciones de acceso"
            );
        }

        return PolicyDecision.permitir(
                "Invitado autorizado para documento público"
        );
    }
}
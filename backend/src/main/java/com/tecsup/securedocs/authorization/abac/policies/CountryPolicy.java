package com.tecsup.securedocs.authorization.abac.policies;

import com.tecsup.securedocs.authorization.abac.*;
import org.springframework.stereotype.Component;

@Component
public class CountryPolicy implements AbacPolicyRule {

    @Override
    public PolicyDecision evaluate(AbacContext context) {

        boolean sameCountry =
                context.usuario().getPais()
                        .equalsIgnoreCase(context.documento().getPais());

        if (!sameCountry) {
            return PolicyDecision.denegar(
                    "El país del usuario no coincide con el país del documento"
            );
        }

        return PolicyDecision.permitir(
                "País autorizado"
        );
    }
}
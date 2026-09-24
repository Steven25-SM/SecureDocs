package com.tecsup.securedocs.authorization.abac;

import com.tecsup.securedocs.authorization.abac.policies.UserStatusPolicy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AbacService {

    private final List<AbacPolicyRule> policies;

    public AbacService(List<AbacPolicyRule> policies) {
        this.policies = policies;
    }

    public PolicyDecision check(AbacContext context) {

        for (AbacPolicyRule policy : policies) {

            /*
             * Algunas operaciones, como consultar auditoría,
             * no tienen un documento asociado.
             *
             * En ese caso solo evaluamos políticas aplicables
             * al usuario, principalmente USER_STATUS.
             */
            if (context.documento() == null
                    && !(policy instanceof UserStatusPolicy)) {
                continue;
            }

            PolicyDecision decision =
                    policy.evaluate(context);

            if (decision.aplica() && !decision.permitido()) {
                return decision;
            }
        }

        return PolicyDecision.permitir(
                "Todas las políticas ABAC aplicables fueron satisfechas"
        );
    }
}
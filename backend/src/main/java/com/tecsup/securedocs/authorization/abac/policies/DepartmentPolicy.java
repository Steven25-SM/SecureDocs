package com.tecsup.securedocs.authorization.abac.policies;

import com.tecsup.securedocs.authorization.Action;
import com.tecsup.securedocs.authorization.abac.AbacContext;
import com.tecsup.securedocs.authorization.abac.AbacPolicyRule;
import com.tecsup.securedocs.authorization.abac.PolicyDecision;
import org.springframework.stereotype.Component;

@Component
public class DepartmentPolicy implements AbacPolicyRule {

    @Override
    public PolicyDecision evaluate(AbacContext context) {

        if (context.documento() == null) {
            return PolicyDecision.noAplica();
        }

        if (!"EMPLEADO".equals(
                context.usuario().getRol().getNombre())) {
            return PolicyDecision.noAplica();
        }

        if (context.accion() != Action.READ_DOCUMENT) {
            return PolicyDecision.noAplica();
        }

        boolean sameDepartment =
                context.usuario()
                        .getDepartamento()
                        .getId()
                        .equals(
                                context.documento()
                                        .getDepartamento()
                                        .getId()
                        );

        if (!sameDepartment) {
            return PolicyDecision.denegar(
                    "El empleado pertenece a un departamento diferente al documento"
            );
        }

        return PolicyDecision.permitir(
                "El documento pertenece al departamento del empleado"
        );
    }
}
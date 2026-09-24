package com.tecsup.securedocs.authorization.abac.policies;

import com.tecsup.securedocs.authorization.Action;
import com.tecsup.securedocs.authorization.abac.*;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class SchedulePolicy implements AbacPolicyRule {

    private static final LocalTime START = LocalTime.of(8, 0);
    private static final LocalTime END = LocalTime.of(18, 0);

    @Override
    public PolicyDecision evaluate(AbacContext context) {

        if (context.accion() != Action.READ_DOCUMENT
                || context.documento().getNivelConfidencialidad() < 4) {
            return PolicyDecision.noAplica();
        }

        LocalTime hora = context.entorno().hora();

        if (hora.isBefore(START) || hora.isAfter(END)) {
            return PolicyDecision.denegar(
                    "Documento altamente confidencial fuera del horario autorizado"
            );
        }

        return PolicyDecision.permitir(
                "Acceso dentro del horario autorizado"
        );
    }
}
package com.tecsup.securedocs.authorization.abac;

public record PolicyDecision(
        boolean aplica,
        boolean permitido,
        String motivo
) {

    public static PolicyDecision noAplica() {
        return new PolicyDecision(false, true, "");
    }

    public static PolicyDecision permitir(String motivo) {
        return new PolicyDecision(true, true, motivo);
    }

    public static PolicyDecision denegar(String motivo) {
        return new PolicyDecision(true, false, motivo);
    }
}
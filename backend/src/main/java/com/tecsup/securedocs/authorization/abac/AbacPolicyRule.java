package com.tecsup.securedocs.authorization.abac;

public interface AbacPolicyRule {

    PolicyDecision evaluate(AbacContext context);

}
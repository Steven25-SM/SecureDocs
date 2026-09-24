package com.tecsup.securedocs.authorization;

public record AuthorizationResult(
        boolean allowed,
        String reason
) {
}
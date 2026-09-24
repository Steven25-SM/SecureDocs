package com.tecsup.securedocs.auth;

public record LoginRequest(
        String correo,
        String password
) {
}
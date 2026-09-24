package com.tecsup.securedocs.auth;

public record LoginResponse(
        String token,
        String tipo,
        String correo,
        String rol
) {
}
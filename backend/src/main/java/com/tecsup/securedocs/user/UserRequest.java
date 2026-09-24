package com.tecsup.securedocs.user;

public record UserRequest(
        String nombre,
        String correo,
        String password,
        Long rolId,
        Long departamentoId,
        Integer nivelSeguridad,
        String pais,
        String tipoContrato,
        String estado
) {
}
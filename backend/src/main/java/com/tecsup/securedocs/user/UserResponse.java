package com.tecsup.securedocs.user;

public record UserResponse(
        Long id,
        String nombre,
        String correo,
        String rol,
        String departamento,
        Integer nivelSeguridad,
        String pais,
        String tipoContrato,
        String estado
) {

    public static UserResponse from(User user) {

        return new UserResponse(
                user.getId(),
                user.getNombre(),
                user.getCorreo(),
                user.getRol().getNombre(),
                user.getDepartamento().getNombre(),
                user.getNivelSeguridad(),
                user.getPais(),
                user.getTipoContrato(),
                user.getEstado()
        );
    }
}
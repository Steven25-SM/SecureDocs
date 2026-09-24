package com.tecsup.securedocs.audit;

public record AuditResponse(
        Long id,
        Long usuarioId,
        String usuario,
        String recurso,
        String accion,
        String fecha,
        String resultado,
        String motivo,
        String direccionIp,
        String dispositivo,
        String ubicacion
) {

    public static AuditResponse from(Audit audit) {

        return new AuditResponse(
                audit.getId(),
                audit.getUsuario() != null
                        ? audit.getUsuario().getId()
                        : null,
                audit.getUsuario() != null
                        ? audit.getUsuario().getCorreo()
                        : null,
                audit.getRecurso(),
                audit.getAccion(),
                audit.getFecha().toString(),
                audit.getResultado(),
                audit.getMotivo(),
                audit.getDireccionIp(),
                audit.getDispositivo(),
                audit.getUbicacion()
        );
    }
}
package com.tecsup.securedocs.document;

public record DocumentResponse(
        Long id,
        String titulo,
        String descripcion,
        Long propietarioId,
        String propietario,
        Long departamentoId,
        String departamento,
        Integer nivelConfidencialidad,
        String estado,
        String pais
) {

    public static DocumentResponse from(Document document) {

        return new DocumentResponse(
                document.getId(),
                document.getTitulo(),
                document.getDescripcion(),
                document.getPropietario().getId(),
                document.getPropietario().getNombre(),
                document.getDepartamento().getId(),
                document.getDepartamento().getNombre(),
                document.getNivelConfidencialidad(),
                document.getEstado(),
                document.getPais()
        );
    }
}
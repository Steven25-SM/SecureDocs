package com.tecsup.securedocs.document;

public record DocumentRequest(
        String titulo,
        String descripcion,
        Long departamentoId,
        Integer nivelConfidencialidad,
        String pais
) {
}
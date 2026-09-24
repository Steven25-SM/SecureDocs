package com.tecsup.securedocs.authorization.abac;

import java.time.LocalDate;
import java.time.LocalTime;

public record Environment(
        LocalDate fecha,
        LocalTime hora,
        String direccionIp,
        String ubicacion,
        String dispositivo
) {
}
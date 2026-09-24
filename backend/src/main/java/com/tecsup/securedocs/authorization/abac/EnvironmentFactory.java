package com.tecsup.securedocs.authorization.abac;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class EnvironmentFactory {

    public Environment fromRequest(HttpServletRequest request) {

        String timeHeader =
                request.getHeader("X-Time");

        String dateHeader =
                request.getHeader("X-Date");

        String device =
                request.getHeader("X-Device");

        String location =
                request.getHeader("X-Location");

        LocalTime hora =
                timeHeader != null
                        ? LocalTime.parse(timeHeader)
                        : LocalTime.now();

        LocalDate fecha =
                dateHeader != null
                        ? LocalDate.parse(dateHeader)
                        : LocalDate.now();

        if (device == null || device.isBlank()) {
            device = "PERSONAL";
        }

        if (location == null || location.isBlank()) {
            location = "PERU";
        }

        return new Environment(
                fecha,
                hora,
                request.getRemoteAddr(),
                location,
                device
        );
    }
}
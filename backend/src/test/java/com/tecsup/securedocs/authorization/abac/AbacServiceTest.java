package com.tecsup.securedocs.authorization.abac;

import com.tecsup.securedocs.authorization.Action;
import com.tecsup.securedocs.authorization.abac.policies.SecurityLevelPolicy;
import com.tecsup.securedocs.department.Department;
import com.tecsup.securedocs.document.Document;
import com.tecsup.securedocs.role.Role;
import com.tecsup.securedocs.user.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbacServiceTest {

    @Test
    void deniesWhenUserSecurityLevelIsInsufficient() {

        Role empleadoRole = new Role("EMPLEADO");
        Department finanzas = new Department("FINANZAS");

        User user = new User();
        user.setRol(empleadoRole);
        user.setDepartamento(finanzas);
        user.setNivelSeguridad(2);
        user.setPais("PERU");
        user.setEstado("ACTIVO");

        Document document = new Document();
        document.setDepartamento(finanzas);
        document.setNivelConfidencialidad(3);
        document.setPais("PERU");

        Environment environment = new Environment(
                LocalDate.of(2026, 9, 23),
                LocalTime.of(11, 30),
                "192.168.10.20",
                "PERU",
                "CORPORATIVO"
        );

        AbacContext context = new AbacContext(
                user,
                document,
                Action.READ_DOCUMENT,
                environment
        );

        SecurityLevelPolicy policy =
                new SecurityLevelPolicy();

        PolicyDecision result =
                policy.evaluate(context);

        assertFalse(result.permitido());
    }

    @Test
    void allowsWhenUserSecurityLevelIsEnough() {

        Role empleadoRole = new Role("EMPLEADO");
        Department finanzas = new Department("FINANZAS");

        User user = new User();
        user.setRol(empleadoRole);
        user.setDepartamento(finanzas);
        user.setNivelSeguridad(3);
        user.setPais("PERU");
        user.setEstado("ACTIVO");

        Document document = new Document();
        document.setDepartamento(finanzas);
        document.setNivelConfidencialidad(3);
        document.setPais("PERU");

        Environment environment = new Environment(
                LocalDate.of(2026, 9, 23),
                LocalTime.of(11, 30),
                "192.168.10.20",
                "PERU",
                "CORPORATIVO"
        );

        AbacContext context = new AbacContext(
                user,
                document,
                Action.READ_DOCUMENT,
                environment
        );

        SecurityLevelPolicy policy =
                new SecurityLevelPolicy();

        PolicyDecision result =
                policy.evaluate(context);

        assertTrue(result.permitido());
    }
}
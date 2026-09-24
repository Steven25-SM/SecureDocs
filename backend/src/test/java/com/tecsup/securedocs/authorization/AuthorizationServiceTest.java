package com.tecsup.securedocs.authorization;

import com.tecsup.securedocs.authorization.abac.AbacContext;
import com.tecsup.securedocs.authorization.abac.AbacPolicyRule;
import com.tecsup.securedocs.authorization.abac.AbacService;
import com.tecsup.securedocs.authorization.abac.Environment;
import com.tecsup.securedocs.authorization.abac.policies.CountryPolicy;
import com.tecsup.securedocs.authorization.abac.policies.DepartmentPolicy;
import com.tecsup.securedocs.authorization.abac.policies.DevicePolicy;
import com.tecsup.securedocs.authorization.abac.policies.GuestPolicy;
import com.tecsup.securedocs.authorization.abac.policies.OwnershipPolicy;
import com.tecsup.securedocs.authorization.abac.policies.SchedulePolicy;
import com.tecsup.securedocs.authorization.abac.policies.SecurityLevelPolicy;
import com.tecsup.securedocs.authorization.abac.policies.UserStatusPolicy;
import com.tecsup.securedocs.department.Department;
import com.tecsup.securedocs.document.Document;
import com.tecsup.securedocs.role.Role;
import com.tecsup.securedocs.user.User;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class AuthorizationServiceTest {

    @Test
    void supervisorCanApproveDocumentFromSameDepartment() {

        Role supervisorRole = new Role("SUPERVISOR");

        Department finanzas =
                Mockito.mock(Department.class);

        when(finanzas.getId()).thenReturn(1L);
        when(finanzas.getNombre()).thenReturn("FINANZAS");

        User user = new User();
        user.setRol(supervisorRole);
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

        RolePermissionRepository repository =
                Mockito.mock(RolePermissionRepository.class);

        when(repository.existsByRol_IdAndPermiso_Nombre(
                null,
                "APPROVE_DOCUMENT"
        )).thenReturn(true);

        RbacService rbacService =
                new RbacService(repository);

        List<AbacPolicyRule> policies = List.of(
                new DepartmentPolicy(),
                new SecurityLevelPolicy(),
                new OwnershipPolicy(),
                new SchedulePolicy(),
                new CountryPolicy(),
                new DevicePolicy(),
                new UserStatusPolicy(),
                new GuestPolicy()
        );

        AbacService abacService =
                new AbacService(policies);

        AuthorizationService authorizationService =
                new AuthorizationService(
                        rbacService,
                        abacService
                );

        AuthorizationResult result =
                authorizationService.authorize(
                        user,
                        document,
                        Action.APPROVE_DOCUMENT,
                        environment
                );

        assertTrue(result.allowed());
    }
}
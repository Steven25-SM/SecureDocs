package com.tecsup.securedocs.authorization;

import com.tecsup.securedocs.role.Role;
import com.tecsup.securedocs.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RbacServiceTest {

    @Mock
    private RolePermissionRepository rolePermissionRepository;

    @Test
    void supervisorCanApproveDocument() {

        Role supervisor = new Role("SUPERVISOR");

        User user = new User();
        user.setRol(supervisor);

        when(rolePermissionRepository
                .existsByRol_IdAndPermiso_Nombre(
                        null,
                        "APPROVE_DOCUMENT"
                ))
                .thenReturn(true);

        RbacService service =
                new RbacService(rolePermissionRepository);

        AuthorizationResult result =
                service.check(user, Action.APPROVE_DOCUMENT);

        assertTrue(result.allowed());
    }

    @Test
    void supervisorCannotDeleteDocument() {

        Role supervisor = new Role("SUPERVISOR");

        User user = new User();
        user.setRol(supervisor);

        when(rolePermissionRepository
                .existsByRol_IdAndPermiso_Nombre(
                        null,
                        "DELETE_DOCUMENT"
                ))
                .thenReturn(false);

        RbacService service =
                new RbacService(rolePermissionRepository);

        AuthorizationResult result =
                service.check(user, Action.DELETE_DOCUMENT);

        assertFalse(result.allowed());
    }
}
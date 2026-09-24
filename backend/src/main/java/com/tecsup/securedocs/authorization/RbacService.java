package com.tecsup.securedocs.authorization;

import com.tecsup.securedocs.user.User;
import org.springframework.stereotype.Service;

@Service
public class RbacService {

    private final RolePermissionRepository rolePermissionRepository;

    public RbacService(
            RolePermissionRepository rolePermissionRepository
    ) {
        this.rolePermissionRepository = rolePermissionRepository;
    }

    public AuthorizationResult check(
            User user,
            Action action
    ) {

        if (user == null || user.getRol() == null) {
            return new AuthorizationResult(
                    false,
                    "Usuario sin rol asignado"
            );
        }

        boolean hasPermission =
                rolePermissionRepository.existsByRol_IdAndPermiso_Nombre(
                        user.getRol().getId(),
                        action.name()
                );

        if (!hasPermission) {
            return new AuthorizationResult(
                    false,
                    "El rol " + user.getRol().getNombre()
                            + " no tiene el permiso "
                            + action.name()
            );
        }

        return new AuthorizationResult(
                true,
                "Permiso RBAC autorizado"
        );
    }
}
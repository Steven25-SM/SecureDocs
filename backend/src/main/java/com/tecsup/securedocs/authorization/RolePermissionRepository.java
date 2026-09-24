package com.tecsup.securedocs.authorization;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RolePermissionRepository
        extends JpaRepository<RolePermission, Long> {

    boolean existsByRol_IdAndPermiso_Id(
            Long rolId,
            Long permisoId
    );

    boolean existsByRol_IdAndPermiso_Nombre(
            Long rolId,
            String permisoNombre
    );
}
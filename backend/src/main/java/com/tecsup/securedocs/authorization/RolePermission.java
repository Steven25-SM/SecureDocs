package com.tecsup.securedocs.authorization;

import com.tecsup.securedocs.permission.Permission;
import com.tecsup.securedocs.role.Role;
import jakarta.persistence.*;

@Entity
@Table(
        name = "roles_permisos",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"rol_id", "permiso_id"})
        }
)
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "rol_id")
    private Role rol;

    @ManyToOne(optional = false)
    @JoinColumn(name = "permiso_id")
    private Permission permiso;

    public RolePermission() {
    }

    public RolePermission(Role rol, Permission permiso) {
        this.rol = rol;
        this.permiso = permiso;
    }

    public Long getId() {
        return id;
    }

    public Role getRol() {
        return rol;
    }

    public void setRol(Role rol) {
        this.rol = rol;
    }

    public Permission getPermiso() {
        return permiso;
    }

    public void setPermiso(Permission permiso) {
        this.permiso = permiso;
    }
}
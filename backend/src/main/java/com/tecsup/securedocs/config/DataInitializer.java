package com.tecsup.securedocs.config;

import com.tecsup.securedocs.authorization.RolePermission;
import com.tecsup.securedocs.authorization.RolePermissionRepository;
import com.tecsup.securedocs.department.Department;
import com.tecsup.securedocs.department.DepartmentRepository;
import com.tecsup.securedocs.permission.Permission;
import com.tecsup.securedocs.permission.PermissionRepository;
import com.tecsup.securedocs.policy.Policy;
import com.tecsup.securedocs.policy.PolicyRepository;
import com.tecsup.securedocs.role.Role;
import com.tecsup.securedocs.role.RoleRepository;
import com.tecsup.securedocs.user.User;
import com.tecsup.securedocs.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.Order;

import java.util.Map;

@Component
@Order(1)
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final DepartmentRepository departmentRepository;
    private final PolicyRepository policyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            RolePermissionRepository rolePermissionRepository,
            DepartmentRepository departmentRepository,
            PolicyRepository policyRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.departmentRepository = departmentRepository;
        this.policyRepository = policyRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        Map<String, Role> roles = Map.of(
                "ADMINISTRADOR", createRole("ADMINISTRADOR"),
                "GERENTE", createRole("GERENTE"),
                "SUPERVISOR", createRole("SUPERVISOR"),
                "EMPLEADO", createRole("EMPLEADO"),
                "AUDITOR", createRole("AUDITOR"),
                "INVITADO", createRole("INVITADO")
        );

        Map<String, Permission> permissions = Map.of(
                "CREATE_DOCUMENT", createPermission("CREATE_DOCUMENT"),
                "READ_DOCUMENT", createPermission("READ_DOCUMENT"),
                "UPDATE_DOCUMENT", createPermission("UPDATE_DOCUMENT"),
                "DELETE_DOCUMENT", createPermission("DELETE_DOCUMENT"),
                "APPROVE_DOCUMENT", createPermission("APPROVE_DOCUMENT"),
                "VIEW_AUDIT", createPermission("VIEW_AUDIT"),
                "MANAGE_USERS", createPermission("MANAGE_USERS"),
                "ASSIGN_ROLES", createPermission("ASSIGN_ROLES")
        );

        createRbacMatrix(roles, permissions);
        createDepartments();
        createPolicies();
        createUsers(roles);
    }

    // =========================
    // ROLES
    // =========================

    private Role createRole(String nombre) {

        return roleRepository.findByNombre(nombre)
                .orElseGet(() ->
                        roleRepository.save(
                                new Role(nombre)
                        )
                );
    }

    // =========================
    // PERMISOS
    // =========================

    private Permission createPermission(String nombre) {

        return permissionRepository.findByNombre(nombre)
                .orElseGet(() ->
                        permissionRepository.save(
                                new Permission(nombre)
                        )
                );
    }

    // =========================
    // RBAC
    // =========================

    private void assign(
            String roleName,
            String permissionName,
            Map<String, Role> roles,
            Map<String, Permission> permissions
    ) {

        Role role = roles.get(roleName);
        Permission permission = permissions.get(permissionName);

        if (!rolePermissionRepository
                .existsByRol_IdAndPermiso_Id(
                        role.getId(),
                        permission.getId()
                )) {

            rolePermissionRepository.save(
                    new RolePermission(
                            role,
                            permission
                    )
            );
        }
    }

    private void createRbacMatrix(
            Map<String, Role> roles,
            Map<String, Permission> permissions
    ) {

        // ADMINISTRADOR
        for (Permission permission : permissions.values()) {

            assign(
                    "ADMINISTRADOR",
                    permission.getNombre(),
                    roles,
                    permissions
            );
        }

        // GERENTE
        assign("GERENTE", "CREATE_DOCUMENT", roles, permissions);
        assign("GERENTE", "READ_DOCUMENT", roles, permissions);
        assign("GERENTE", "UPDATE_DOCUMENT", roles, permissions);
        assign("GERENTE", "DELETE_DOCUMENT", roles, permissions);
        assign("GERENTE", "APPROVE_DOCUMENT", roles, permissions);

        // SUPERVISOR
        assign("SUPERVISOR", "CREATE_DOCUMENT", roles, permissions);
        assign("SUPERVISOR", "READ_DOCUMENT", roles, permissions);
        assign("SUPERVISOR", "UPDATE_DOCUMENT", roles, permissions);
        assign("SUPERVISOR", "APPROVE_DOCUMENT", roles, permissions);

        // EMPLEADO
        assign("EMPLEADO", "CREATE_DOCUMENT", roles, permissions);
        assign("EMPLEADO", "READ_DOCUMENT", roles, permissions);
        assign("EMPLEADO", "UPDATE_DOCUMENT", roles, permissions);

        // AUDITOR
        assign("AUDITOR", "READ_DOCUMENT", roles, permissions);
        assign("AUDITOR", "VIEW_AUDIT", roles, permissions);

        // INVITADO
        assign("INVITADO", "READ_DOCUMENT", roles, permissions);
    }

    // =========================
    // DEPARTAMENTOS
    // =========================

    private void createDepartments() {

        createDepartment("FINANZAS");
        createDepartment("RRHH");
        createDepartment("TI");
        createDepartment("OPERACIONES");
    }

    private void createDepartment(String nombre) {

        boolean exists =
                departmentRepository.findAll()
                        .stream()
                        .anyMatch(
                                department ->
                                        department
                                                .getNombre()
                                                .equals(nombre)
                        );

        if (!exists) {

            departmentRepository.save(
                    new Department(nombre)
            );
        }
    }

    // =========================
    // POLÍTICAS
    // =========================

    private void createPolicies() {

        createPolicy(
                "DEPARTMENT",
                "El usuario solo puede consultar documentos de su departamento.",
                true
        );

        createPolicy(
                "SECURITY_LEVEL",
                "El nivel de seguridad del usuario debe ser igual o superior al nivel de confidencialidad del documento.",
                true
        );

        createPolicy(
                "OWNERSHIP",
                "El empleado solamente puede modificar documentos de los que sea propietario.",
                true
        );

        createPolicy(
                "SCHEDULE",
                "Los documentos con confidencialidad 4 o superior solo pueden consultarse de 08:00 a 18:00.",
                true
        );

        createPolicy(
                "COUNTRY",
                "El país del usuario debe coincidir con el país del documento.",
                true
        );

        createPolicy(
                "DEVICE",
                "Los documentos de confidencialidad 4 o superior requieren dispositivo corporativo.",
                true
        );

        createPolicy(
                "USER_STATUS",
                "Solo los usuarios con estado ACTIVO pueden acceder al sistema.",
                true
        );

        createPolicy(
                "GUEST",
                "Los invitados deben ser EXTERNOS y solo pueden acceder a documentos publicados de confidencialidad 1 o inferior.",
                true
        );
    }

    private void createPolicy(
            String nombre,
            String descripcion,
            boolean activa
    ) {

        if (!policyRepository.existsByNombre(nombre)) {

            policyRepository.save(
                    new Policy(
                            nombre,
                            descripcion,
                            activa
                    )
            );
        }
    }

    // =========================
    // USUARIOS
    // =========================

    private void createUsers(Map<String, Role> roles) {

        Department finanzas =
                findDepartment("FINANZAS");

        Department rrhh =
                findDepartment("RRHH");

        Department ti =
                findDepartment("TI");

        createUser(
                "Administrador",
                "admin@securedocs.com",
                "123456",
                roles.get("ADMINISTRADOR"),
                ti,
                5,
                "PERU",
                "INTERNO",
                "ACTIVO"
        );

        createUser(
                "Gerente Finanzas",
                "gerente@securedocs.com",
                "123456",
                roles.get("GERENTE"),
                finanzas,
                5,
                "PERU",
                "INTERNO",
                "ACTIVO"
        );

        createUser(
                "Carlos Ruiz",
                "carlos@securedocs.com",
                "123456",
                roles.get("SUPERVISOR"),
                finanzas,
                3,
                "PERU",
                "INTERNO",
                "ACTIVO"
        );

        createUser(
                "Ana Torres",
                "ana@securedocs.com",
                "123456",
                roles.get("EMPLEADO"),
                finanzas,
                3,
                "PERU",
                "INTERNO",
                "ACTIVO"
        );

        createUser(
                "Rosa Medina",
                "rosa@securedocs.com",
                "123456",
                roles.get("EMPLEADO"),
                rrhh,
                2,
                "PERU",
                "INTERNO",
                "ACTIVO"
        );

        createUser(
                "Invitado Demo",
                "invitado@securedocs.com",
                "123456",
                roles.get("INVITADO"),
                ti,
                1,
                "PERU",
                "EXTERNO",
                "ACTIVO"
        );

        createUser(
                "Auditor Demo",
                "auditor@securedocs.com",
                "123456",
                roles.get("AUDITOR"),
                ti,
                5,
                "PERU",
                "INTERNO",
                "ACTIVO"
        );

        createUser(
                "Usuario Inactivo",
                "inactivo@securedocs.com",
                "123456",
                roles.get("EMPLEADO"),
                finanzas,
                3,
                "PERU",
                "INTERNO",
                "INACTIVO"
        );
    }

    private Department findDepartment(String nombre) {

        return departmentRepository.findAll()
                .stream()
                .filter(
                        department ->
                                department
                                        .getNombre()
                                        .equals(nombre)
                )
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException(
                                "Departamento no encontrado: "
                                        + nombre
                        )
                );
    }

    private void createUser(
            String nombre,
            String correo,
            String password,
            Role rol,
            Department departamento,
            int nivelSeguridad,
            String pais,
            String tipoContrato,
            String estado
    ) {

        if (userRepository
                .findByCorreo(correo)
                .isPresent()) {

            return;
        }

        User user = new User();

        user.setNombre(nombre);
        user.setCorreo(correo);

        user.setPassword(
                passwordEncoder.encode(password)
        );

        user.setRol(rol);
        user.setDepartamento(departamento);
        user.setNivelSeguridad(nivelSeguridad);
        user.setPais(pais);
        user.setTipoContrato(tipoContrato);
        user.setEstado(estado);

        userRepository.save(user);
    }
}
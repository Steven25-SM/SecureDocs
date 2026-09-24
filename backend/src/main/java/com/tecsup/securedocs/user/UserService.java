package com.tecsup.securedocs.user;

import com.tecsup.securedocs.department.Department;
import com.tecsup.securedocs.department.DepartmentRepository;
import com.tecsup.securedocs.role.Role;
import com.tecsup.securedocs.role.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            DepartmentRepository departmentRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Usuario no encontrado"
                        ));
    }

    public User create(UserRequest request) {

        if (userRepository.findByCorreo(request.correo()).isPresent()) {
            throw new IllegalArgumentException(
                    "El correo ya está registrado"
            );
        }

        Role role =
                roleRepository.findById(request.rolId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Rol no encontrado"
                                ));

        Department department =
                departmentRepository.findById(
                                request.departamentoId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Departamento no encontrado"
                                ));

        User user = new User();

        user.setNombre(request.nombre());
        user.setCorreo(request.correo());
        user.setPassword(
                passwordEncoder.encode(
                        request.password()
                )
        );
        user.setRol(role);
        user.setDepartamento(department);
        user.setNivelSeguridad(
                request.nivelSeguridad()
        );
        user.setPais(request.pais());
        user.setTipoContrato(
                request.tipoContrato()
        );
        user.setEstado(request.estado());

        return userRepository.save(user);
    }

    public User update(
            Long id,
            UserRequest request
    ) {

        User user = findById(id);

        Role role =
                roleRepository.findById(request.rolId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Rol no encontrado"
                                ));

        Department department =
                departmentRepository.findById(
                                request.departamentoId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Departamento no encontrado"
                                ));

        user.setNombre(request.nombre());
        user.setCorreo(request.correo());
        user.setRol(role);
        user.setDepartamento(department);
        user.setNivelSeguridad(
                request.nivelSeguridad()
        );
        user.setPais(request.pais());
        user.setTipoContrato(
                request.tipoContrato()
        );
        user.setEstado(request.estado());

        if (request.password() != null
                && !request.password().isBlank()) {

            user.setPassword(
                    passwordEncoder.encode(
                            request.password()
                    )
            );
        }

        return userRepository.save(user);
    }
}
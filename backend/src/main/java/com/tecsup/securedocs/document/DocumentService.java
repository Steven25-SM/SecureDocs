package com.tecsup.securedocs.document;

import com.tecsup.securedocs.audit.AuditService;
import com.tecsup.securedocs.authorization.*;
import com.tecsup.securedocs.authorization.abac.Environment;
import com.tecsup.securedocs.department.Department;
import com.tecsup.securedocs.department.DepartmentRepository;
import com.tecsup.securedocs.user.User;
import com.tecsup.securedocs.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final AuthorizationService authorizationService;
    private final AuditService auditService;

    public DocumentService(
            DocumentRepository documentRepository,
            DepartmentRepository departmentRepository,
            UserRepository userRepository,
            AuthorizationService authorizationService,
            AuditService auditService
    ) {
        this.documentRepository = documentRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.authorizationService = authorizationService;
        this.auditService = auditService;
    }

    public User findUserByCorreo(String correo) {

        return userRepository.findByCorreo(correo)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Usuario autenticado no encontrado"
                        ));
    }

    public List<Document> findAll(
            User user,
            Environment environment
    ) {

        List<Document> allowedDocuments =
                new ArrayList<>();

        for (Document document :
                documentRepository.findAll()) {

            AuthorizationResult result =
                    authorizationService.authorize(
                            user,
                            document,
                            Action.READ_DOCUMENT,
                            environment
                    );

            auditService.record(
                    user,
                    "documento-" + document.getId(),
                    Action.READ_DOCUMENT,
                    result,
                    environment
            );

            if (result.allowed()) {
                allowedDocuments.add(document);
            }
        }

        return allowedDocuments;
    }

    public OperationResult<Document> findById(
            Long id,
            User user,
            Environment environment
    ) {

        Document document =
                documentRepository.findById(id)
                        .orElse(null);

        if (document == null) {
            return new OperationResult<>(
                    false,
                    "Documento no encontrado",
                    null
            );
        }

        AuthorizationResult result =
                authorizationService.authorize(
                        user,
                        document,
                        Action.READ_DOCUMENT,
                        environment
                );

        auditService.record(
                user,
                "documento-" + id,
                Action.READ_DOCUMENT,
                result,
                environment
        );

        return new OperationResult<>(
                result.allowed(),
                result.reason(),
                result.allowed()
                        ? document
                        : null
        );
    }

    @Transactional
    public OperationResult<Document> create(
            DocumentRequest request,
            User user,
            Environment environment
    ) {

        Department department =
                departmentRepository.findById(
                        request.departamentoId()
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "Departamento no encontrado"
                        ));

        Document document =
                new Document();

        document.setTitulo(request.titulo());
        document.setDescripcion(request.descripcion());
        document.setPropietario(user);
        document.setDepartamento(department);
        document.setNivelConfidencialidad(
                request.nivelConfidencialidad()
        );
        document.setEstado("PENDIENTE");
        document.setPais(request.pais());
        document.setFechaCreacion(
                LocalDateTime.now()
        );

        AuthorizationResult result =
                authorizationService.authorize(
                        user,
                        document,
                        Action.CREATE_DOCUMENT,
                        environment
                );

        auditService.record(
                user,
                "documento-NUEVO",
                Action.CREATE_DOCUMENT,
                result,
                environment
        );

        if (!result.allowed()) {
            return new OperationResult<>(
                    false,
                    result.reason(),
                    null
            );
        }

        Document saved =
                documentRepository.save(document);

        return new OperationResult<>(
                true,
                result.reason(),
                saved
        );
    }

    @Transactional
    public OperationResult<Document> update(
            Long id,
            DocumentRequest request,
            User user,
            Environment environment
    ) {

        Document document =
                documentRepository.findById(id)
                        .orElse(null);

        if (document == null) {
            return new OperationResult<>(
                    false,
                    "Documento no encontrado",
                    null
            );
        }

        AuthorizationResult result =
                authorizationService.authorize(
                        user,
                        document,
                        Action.UPDATE_DOCUMENT,
                        environment
                );

        auditService.record(
                user,
                "documento-" + id,
                Action.UPDATE_DOCUMENT,
                result,
                environment
        );

        if (!result.allowed()) {
            return new OperationResult<>(
                    false,
                    result.reason(),
                    null
            );
        }

        Department department =
                departmentRepository.findById(
                        request.departamentoId()
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "Departamento no encontrado"
                        ));

        document.setTitulo(request.titulo());
        document.setDescripcion(request.descripcion());
        document.setDepartamento(department);
        document.setNivelConfidencialidad(
                request.nivelConfidencialidad()
        );
        document.setPais(request.pais());

        return new OperationResult<>(
                true,
                result.reason(),
                documentRepository.save(document)
        );
    }

    @Transactional
    public OperationResult<Void> delete(
            Long id,
            User user,
            Environment environment
    ) {

        Document document =
                documentRepository.findById(id)
                        .orElse(null);

        if (document == null) {
            return new OperationResult<>(
                    false,
                    "Documento no encontrado",
                    null
            );
        }

        AuthorizationResult result =
                authorizationService.authorize(
                        user,
                        document,
                        Action.DELETE_DOCUMENT,
                        environment
                );

        auditService.record(
                user,
                "documento-" + id,
                Action.DELETE_DOCUMENT,
                result,
                environment
        );

        if (!result.allowed()) {
            return new OperationResult<>(
                    false,
                    result.reason(),
                    null
            );
        }

        documentRepository.delete(document);

        return new OperationResult<>(
                true,
                result.reason(),
                null
        );
    }

    @Transactional
    public OperationResult<Document> approve(
            Long id,
            User user,
            Environment environment
    ) {

        Document document =
                documentRepository.findById(id)
                        .orElse(null);

        if (document == null) {
            return new OperationResult<>(
                    false,
                    "Documento no encontrado",
                    null
            );
        }

        AuthorizationResult result =
                authorizationService.authorize(
                        user,
                        document,
                        Action.APPROVE_DOCUMENT,
                        environment
                );

        auditService.record(
                user,
                "documento-" + id,
                Action.APPROVE_DOCUMENT,
                result,
                environment
        );

        if (!result.allowed()) {
            return new OperationResult<>(
                    false,
                    result.reason(),
                    null
            );
        }

        document.setEstado("APROBADO");

        return new OperationResult<>(
                true,
                "Documento aprobado correctamente",
                documentRepository.save(document)
        );
    }

    public record OperationResult<T>(
            boolean allowed,
            String reason,
            T data
    ) {
    }
}
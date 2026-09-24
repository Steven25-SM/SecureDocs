package com.tecsup.securedocs.document;

import com.tecsup.securedocs.authorization.abac.Environment;
import com.tecsup.securedocs.authorization.abac.EnvironmentFactory;
import com.tecsup.securedocs.user.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/documentos")
public class DocumentController {

    private final DocumentService documentService;
    private final EnvironmentFactory environmentFactory;

    public DocumentController(
            DocumentService documentService,
            EnvironmentFactory environmentFactory
    ) {
        this.documentService = documentService;
        this.environmentFactory = environmentFactory;
    }

    private User currentUser() {

        String correo =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        return documentService.findUserByCorreo(correo);
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getAll(
            HttpServletRequest request
    ) {

        User user = currentUser();

        Environment environment =
                environmentFactory.fromRequest(request);

        List<DocumentResponse> response =
                documentService.findAll(
                                user,
                                environment
                        )
                        .stream()
                        .map(DocumentResponse::from)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable Long id,
            HttpServletRequest request
    ) {

        User user = currentUser();

        Environment environment =
                environmentFactory.fromRequest(request);

        DocumentService.OperationResult<Document> result =
                documentService.findById(
                        id,
                        user,
                        environment
                );

        if (!result.allowed()) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(result.reason());
        }

        return ResponseEntity.ok(
                DocumentResponse.from(
                        result.data()
                )
        );
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody DocumentRequest requestBody,
            HttpServletRequest request
    ) {

        User user = currentUser();

        Environment environment =
                environmentFactory.fromRequest(request);

        DocumentService.OperationResult<Document> result =
                documentService.create(
                        requestBody,
                        user,
                        environment
                );

        if (!result.allowed()) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(result.reason());
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        DocumentResponse.from(
                                result.data()
                        )
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody DocumentRequest requestBody,
            HttpServletRequest request
    ) {

        User user = currentUser();

        Environment environment =
                environmentFactory.fromRequest(request);

        DocumentService.OperationResult<Document> result =
                documentService.update(
                        id,
                        requestBody,
                        user,
                        environment
                );

        if (!result.allowed()) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(result.reason());
        }

        return ResponseEntity.ok(
                DocumentResponse.from(
                        result.data()
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            HttpServletRequest request
    ) {

        User user = currentUser();

        Environment environment =
                environmentFactory.fromRequest(request);

        DocumentService.OperationResult<Void> result =
                documentService.delete(
                        id,
                        user,
                        environment
                );

        if (!result.allowed()) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(result.reason());
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/aprobar")
    public ResponseEntity<?> approve(
            @PathVariable Long id,
            HttpServletRequest request
    ) {

        User user = currentUser();

        Environment environment =
                environmentFactory.fromRequest(request);

        DocumentService.OperationResult<Document> result =
                documentService.approve(
                        id,
                        user,
                        environment
                );

        if (!result.allowed()) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(result.reason());
        }

        return ResponseEntity.ok(
                DocumentResponse.from(
                        result.data()
                )
        );
    }
}
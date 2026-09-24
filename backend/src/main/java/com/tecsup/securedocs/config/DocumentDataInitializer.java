package com.tecsup.securedocs.config;

import com.tecsup.securedocs.department.Department;
import com.tecsup.securedocs.department.DepartmentRepository;
import com.tecsup.securedocs.document.Document;
import com.tecsup.securedocs.document.DocumentRepository;
import com.tecsup.securedocs.user.User;
import com.tecsup.securedocs.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Order(2)
public class DocumentDataInitializer
        implements CommandLineRunner {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    public DocumentDataInitializer(
            DocumentRepository documentRepository,
            UserRepository userRepository,
            DepartmentRepository departmentRepository
    ) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public void run(String... args) {

        if (documentRepository.count() > 0) {
            return;
        }

        User ana =
                userRepository.findByCorreo(
                        "ana@securedocs.com"
                ).orElseThrow();

        User rosa =
                userRepository.findByCorreo(
                        "rosa@securedocs.com"
                ).orElseThrow();

        Department finanzas =
                departmentRepository.findAll()
                        .stream()
                        .filter(d ->
                                d.getNombre()
                                        .equals("FINANZAS"))
                        .findFirst()
                        .orElseThrow();

        Department rrhh =
                departmentRepository.findAll()
                        .stream()
                        .filter(d ->
                                d.getNombre()
                                        .equals("RRHH"))
                        .findFirst()
                        .orElseThrow();

        createDocument(
                "Manual de procedimientos financieros",
                "Documento público del área de finanzas.",
                ana,
                finanzas,
                1,
                "PUBLICADO",
                "PERU"
        );

        createDocument(
                "Reporte de Recursos Humanos",
                "Documento público del área de RRHH.",
                rosa,
                rrhh,
                1,
                "PUBLICADO",
                "PERU"
        );

        createDocument(
                "Presupuesto corporativo",
                "Documento altamente confidencial.",
                ana,
                finanzas,
                4,
                "PUBLICADO",
                "PERU"
        );

        createDocument(
                "Presupuesto anual",
                "Documento para aprobación del supervisor.",
                ana,
                finanzas,
                3,
                "PENDIENTE",
                "PERU"
        );

        createDocument(
                "Plan estratégico 2027",
                "Documento de máxima confidencialidad.",
                ana,
                finanzas,
                5,
                "PUBLICADO",
                "PERU"
        );
    }

    private void createDocument(
            String titulo,
            String descripcion,
            User propietario,
            Department departamento,
            int nivel,
            String estado,
            String pais
    ) {

        Document document = new Document();

        document.setTitulo(titulo);
        document.setDescripcion(descripcion);
        document.setPropietario(propietario);
        document.setDepartamento(departamento);
        document.setNivelConfidencialidad(nivel);
        document.setEstado(estado);
        document.setPais(pais);
        document.setFechaCreacion(
                LocalDateTime.now()
        );

        documentRepository.save(document);
    }
}
package com.tecsup.securedocs.audit;

import com.tecsup.securedocs.authorization.Action;
import com.tecsup.securedocs.authorization.AuthorizationResult;
import com.tecsup.securedocs.authorization.abac.Environment;
import com.tecsup.securedocs.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuditService {

    private final AuditRepository auditRepository;

    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(
            User user,
            String recurso,
            Action action,
            AuthorizationResult result,
            Environment environment
    ) {

        Audit audit = new Audit();

        audit.setUsuario(user);
        audit.setRecurso(recurso);
        audit.setAccion(action.name());

        audit.setFecha(
                LocalDateTime.of(
                        environment.fecha(),
                        environment.hora()
                )
        );

        audit.setResultado(
                result.allowed()
                        ? "PERMITIDO"
                        : "DENEGADO"
        );

        audit.setMotivo(result.reason());

        audit.setDireccionIp(
                environment.direccionIp()
        );

        audit.setDispositivo(
                environment.dispositivo()
        );

        audit.setUbicacion(
                environment.ubicacion()
        );

        auditRepository.save(audit);
    }
}
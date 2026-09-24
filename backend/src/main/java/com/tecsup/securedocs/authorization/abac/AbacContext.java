package com.tecsup.securedocs.authorization.abac;

import com.tecsup.securedocs.authorization.Action;
import com.tecsup.securedocs.document.Document;
import com.tecsup.securedocs.user.User;

public record AbacContext(
        User usuario,
        Document documento,
        Action accion,
        Environment entorno
) {
}
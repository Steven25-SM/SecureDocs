# SecureDocs — RBAC + ABAC

Sistema web de gestión de documentos y expedientes empresariales desarrollado para demostrar un modelo de control de acceso combinado mediante **RBAC (Role-Based Access Control)** y **ABAC (Attribute-Based Access Control)**.

La autorización se realiza en dos etapas:

```text
Autenticación
      ↓
     RBAC
      ↓
     ABAC
      ↓
PERMITIR / DENEGAR
```

Todas las solicitudes de acceso relevantes son registradas mediante un sistema de auditoría.

---

## Tecnologías

### Backend

* Java
* Spring Boot
* Spring Data JPA / Hibernate
* Spring Security
* JWT
* Maven
* MySQL

### Frontend

* React
* Vite
* Axios
* React Router DOM
* CSS

### Base de datos

* MySQL / MariaDB mediante XAMPP
* phpMyAdmin para administración

---

## Arquitectura

```text
┌──────────────────────┐
│   React + Vite       │
│     Frontend         │
└──────────┬───────────┘
           │ HTTP / REST
           ▼
┌──────────────────────────────┐
│       Spring Boot API        │
├──────────────────────────────┤
│ Authentication / JWT         │
│ Authorization                │
│   ├── RBAC                   │
│   └── ABAC                   │
│ User Service                 │
│ Document Service             │
│ Audit Service                │
└────────────┬─────────────────┘
             │
             ▼
      ┌───────────────┐
      │     MySQL     │
      └───────────────┘
```

---

# Requisitos previos

Antes de ejecutar el proyecto se necesita instalar:

* **JDK 25 o superior**
* **Maven Wrapper incluido en el proyecto**
* **Node.js + npm**
* **XAMPP**
* Navegador web moderno
* Git

---

# 1. Clonar el repositorio

```bash
git clone <URL_DEL_REPOSITORIO>
cd securedocs-rbac-abac
```

---

# 2. Configurar MySQL

Abrir **XAMPP Control Panel** y encender:

```text
MySQL
```

Luego abrir:

```text
http://localhost/phpmyadmin
```

Crear una base de datos llamada:

```text
securedocs
```

Importar el script SQL incluido en el proyecto, si está disponible, o ejecutar el script de creación de tablas y datos proporcionado junto con la entrega.

Las principales tablas utilizadas son:

```text
usuarios
roles
permisos
roles_permisos
departamentos
documentos
politicas
auditorias
```

---

# 3. Configurar el Backend

Entrar a la carpeta del backend:

```bash
cd backend
```

Revisar la configuración de conexión a MySQL en:

```text
src/main/resources/application.properties
```

Ejemplo:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/securedocs
spring.datasource.username=root
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

> Los valores de usuario y contraseña deben adaptarse a la configuración local de MySQL/XAMPP.

---

# 4. Ejecutar el Backend

Desde la carpeta `backend`:

### Windows

```bash
mvnw.cmd spring-boot:run
```

El backend se ejecutará en:

```text
http://localhost:8080
```

---

# 5. Ejecutar pruebas del Backend

Desde la carpeta `backend`:

```bash
mvnw.cmd test
```

El proyecto debe finalizar correctamente con:

```text
BUILD SUCCESS
```

---

# 6. Configurar el Frontend

Abrir otra terminal y entrar a:

```bash
cd frontend
```

Instalar las dependencias:

```bash
npm install
```

---

# 7. Ejecutar el Frontend

```bash
npm run dev
```

Vite mostrará una URL similar a:

```text
http://localhost:5173
```

Abrir esa dirección en el navegador.

---

# 8. Flujo de funcionamiento

El sistema sigue el siguiente proceso:

```text
Usuario
   │
   ▼
Login
   │
   ▼
JWT
   │
   ▼
Solicitud de operación
   │
   ▼
Validación RBAC
   │
   ├── DENEGADO → Auditoría
   │
   ▼
Validación ABAC
   │
   ├── DENEGADO → Auditoría
   │
   ▼
Operación permitida
   │
   ▼
Auditoría
```

---

# 9. Roles disponibles

El sistema considera los siguientes roles:

| Rol           | Descripción                                                       |
| ------------- | ----------------------------------------------------------------- |
| ADMINISTRADOR | Administra usuarios, roles y operaciones administrativas          |
| GERENTE       | Supervisa y gestiona documentos de su área                        |
| SUPERVISOR    | Revisa, modifica y aprueba documentos                             |
| EMPLEADO      | Crea, consulta y modifica documentos de acuerdo con las políticas |
| AUDITOR       | Consulta documentos y registros de auditoría                      |
| INVITADO      | Acceso limitado a documentos públicos                             |

---

# 10. Permisos RBAC

| Operación           | Admin | Gerente | Supervisor | Empleado | Auditor | Invitado |
| ------------------- | :---: | :-----: | :--------: | :------: | :-----: | :------: |
| Crear documento     |   ✓   |    ✓    |      ✓     |     ✓    |    ✗    |     ✗    |
| Consultar documento |   ✓   |    ✓    |      ✓     |     ✓    |    ✓    |     ✓    |
| Modificar documento |   ✓   |    ✓    |      ✓     |     ✓    |    ✗    |     ✗    |
| Eliminar documento  |   ✓   |    ✓    |      ✗     |     ✗    |    ✗    |     ✗    |
| Aprobar documento   |   ✓   |    ✓    |      ✓     |     ✗    |    ✗    |     ✗    |
| Ver auditoría       |   ✓   |    ✓    |      ✗     |     ✗    |    ✓    |     ✗    |
| Gestionar usuarios  |   ✓   |    ✗    |      ✗     |     ✗    |    ✗    |     ✗    |
| Asignar roles       |   ✓   |    ✗    |      ✗     |     ✗    |    ✗    |     ✗    |

---

# 11. Políticas ABAC

El sistema utiliza atributos del usuario, recurso y entorno.

### Políticas implementadas

1. **Departamento**

```text
usuario.departamento == documento.departamento
```

2. **Nivel de seguridad**

```text
usuario.nivel_seguridad >= documento.nivel_confidencialidad
```

3. **Propiedad**

Los empleados solamente pueden modificar documentos propios.

```text
usuario.id == documento.propietario
```

Gerente y Administrador están exceptuados de esta condición.

4. **Horario**

Los documentos con confidencialidad alta solamente pueden consultarse entre:

```text
08:00 - 18:00
```

5. **País**

```text
usuario.pais == documento.pais
```

6. **Dispositivo**

Los documentos de nivel 4 o 5 requieren:

```text
dispositivo == CORPORATIVO
```

7. **Estado del usuario**

```text
usuario.estado == ACTIVO
```

8. **Invitados**

Los invitados deben cumplir:

```text
tipo_contrato == EXTERNO
AND
nivel_confidencialidad <= 1
AND
documento.estado == PUBLICADO
```

---

# 12. API principal

## Autenticación

**POST**

```text
http://localhost:8080/auth/login
```

## Usuarios

**GET**

```text
http://localhost:8080/usuarios
```

**POST**

```text
http://localhost:8080/usuarios
```

**PUT**

```text
http://localhost:8080/usuarios/{id}
```

## Documentos

**GET**

```text
http://localhost:8080/documentos
```

**GET**

```text
http://localhost:8080/documentos/{id}
```

**POST**

```text
http://localhost:8080/documentos
```

**PUT**

```text
http://localhost:8080/documentos/{id}
```

**DELETE**

```text
http://localhost:8080/documentos/{id}
```

## Aprobación

**POST**

```text
http://localhost:8080/documentos/{id}/aprobar
```

## Auditoría

**GET**

```text
http://localhost:8080/auditoria
```

Los endpoints protegidos utilizan autenticación mediante JWT y pasan por la capa de autorización correspondiente.

---

# 13. Auditoría

El sistema registra los intentos de acceso con información como:

```text
usuario
recurso
accion
fecha
resultado
motivo
direccion_ip
dispositivo
ubicacion
```

Ejemplo:

```json
{
  "usuario": "usuario@securedocs.com",
  "recurso": "documento-3",
  "accion": "READ_DOCUMENT",
  "resultado": "DENEGADO",
  "motivo": "Nivel de seguridad insuficiente"
}
```

Los registros pueden consultarse mediante:

```text
GET /auditoria
```

y directamente en la tabla:

```text
auditorias
```

de la base de datos.

---

# 14. Pruebas

La solución fue validada mediante **17 casos de prueba**:

* 12 casos obligatorios definidos en el laboratorio.
* 5 casos adicionales diseñados para validar escenarios complementarios.

Las pruebas cubren:

* Autorización RBAC.
* Autorización ABAC.
* Diferencias de departamento.
* Niveles de seguridad.
* Propiedad de documentos.
* Horarios.
* País.
* Dispositivo.
* Estado del usuario.
* Acceso de invitados.
* Auditoría.
* Operaciones CRUD.

Las evidencias se encuentran en:

```text
docs/evidencias/
```

---

# 15. Estructura del proyecto

```text
securedocs-rbac-abac/
│
├── backend/
│   ├── src/
│   ├── pom.xml
│   ├── mvnw
│   └── mvnw.cmd
│
├── frontend/
│   ├── src/
│   ├── public/
│   ├── package.json
│   └── vite.config.js
│
├── docs/
│   ├── arquitectura.png
│   ├── modelo-bd.png
│   ├── matriz-rbac.png
│   ├── matriz-abac.png
│   └── evidencias/
│
└── README.md
```

---

# 16. Puertos utilizados

| Servicio            | Puerto |
| ------------------- | -----: |
| Backend Spring Boot | `8080` |
| Frontend Vite       | `5173` |
| MySQL/XAMPP         | `3306` |
| phpMyAdmin          |   `80` |

---

# 17. Usuarios de prueba

Para las demostraciones del laboratorio se utilizan usuarios con diferentes roles, departamentos, niveles de seguridad y estados.

Las credenciales de prueba utilizadas durante la demostración pueden encontrarse en la documentación/evidencias del proyecto.

> Para una instalación real, las credenciales deben modificarse y no deben almacenarse en el repositorio.

---

# 18. Evidencias

La carpeta:

```text
docs/evidencias/
```

contiene las capturas correspondientes a los casos de prueba obligatorios y adicionales.

También se incluyen:

```text
arquitectura.png
modelo-bd.png
matriz-rbac.png
matriz-abac.png
```

---

# 19. Ejecución rápida

### Terminal 1 — Backend

```bash
cd backend
mvnw.cmd spring-boot:run
```

### Terminal 2 — Frontend

```bash
cd frontend
npm install
npm run dev
```

### Base de datos

```text
XAMPP → MySQL → ON
```

Base:

```text
securedocs
```

Frontend:

```text
http://localhost:5173
```

Backend:

```text
http://localhost:8080
```

---

# 20. Estado del proyecto

SecureDocs implementa:

```text
✓ Autenticación JWT
✓ RBAC
✓ ABAC
✓ Gestión de usuarios
✓ Gestión de documentos
✓ Aprobación de documentos
✓ Auditoría
✓ Frontend React
✓ API REST
✓ Persistencia MySQL
✓ Pruebas automatizadas
✓ Pruebas funcionales mediante Postman
```

---

## Autor

**Jaime Farfán**

**Curso:** Cloud Security
**Sección:** 5 - C24

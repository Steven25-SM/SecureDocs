import { useEffect, useMemo, useState } from "react";
import axios from "axios";

const API_URL = "http://localhost:8080";

const EMPTY_DOCUMENT_FORM = {
  titulo: "",
  descripcion: "",
  departamentoId: "1",
  nivelConfidencialidad: "1",
  pais: "PERU",
};

const DEPARTMENTS = [
  { id: 1, nombre: "FINANZAS" },
  { id: 2, nombre: "RRHH" },
  { id: 3, nombre: "TI" },
  { id: 4, nombre: "OPERACIONES" },
];

const ROLE_RULES = {
  ADMINISTRADOR: {
    create: true,
    update: true,
    delete: true,
    approve: true,
  },
  GERENTE: {
    create: true,
    update: true,
    delete: true,
    approve: true,
  },
  SUPERVISOR: {
    create: true,
    update: true,
    delete: false,
    approve: true,
  },
  EMPLEADO: {
    create: true,
    update: true,
    delete: false,
    approve: false,
  },
  AUDITOR: {
    create: false,
    update: false,
    delete: false,
    approve: false,
  },
  INVITADO: {
    create: false,
    update: false,
    delete: false,
    approve: false,
  },
};

function getApiError(err, fallback) {
  const data = err.response?.data;

  if (typeof data === "string") {
    return data;
  }

  if (data?.message) {
    return data.message;
  }

  if (data?.error) {
    return data.error;
  }

  return fallback;
}

function App() {
  const [token, setToken] = useState(
    localStorage.getItem("token")
  );

  const [user, setUser] = useState(() => {
    const savedUser = localStorage.getItem("user");

    try {
      return savedUser ? JSON.parse(savedUser) : null;
    } catch {
      return null;
    }
  });

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [activePage, setActivePage] = useState("Dashboard");

  const [documents, setDocuments] = useState([]);
  const [users, setUsers] = useState([]);
  const [audits, setAudits] = useState([]);

  const [auditLoaded, setAuditLoaded] = useState(false);
  const [usersLoaded, setUsersLoaded] = useState(false);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  // Documents CRUD
  const [showDocumentForm, setShowDocumentForm] = useState(false);
  const [editingDocument, setEditingDocument] = useState(null);
  const [documentForm, setDocumentForm] = useState(
    EMPTY_DOCUMENT_FORM
  );
  const [savingDocument, setSavingDocument] = useState(false);

  const authConfig = useMemo(
    () => ({
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }),
    [token]
  );

  const currentRole = user?.rol || "";

  const roleRules =
    ROLE_RULES[currentRole] || {
      create: false,
      update: false,
      delete: false,
      approve: false,
    };

  const login = async (event) => {
    event.preventDefault();

    setError("");
    setSuccessMessage("");
    setLoading(true);

    try {
      const response = await axios.post(
        `${API_URL}/auth/login`,
        {
          correo: email,
          password,
        }
      );

      const data = response.data;

      const loggedUser = {
        correo: data.correo,
        rol: data.rol,
      };

      localStorage.setItem("token", data.token);
      localStorage.setItem(
        "user",
        JSON.stringify(loggedUser)
      );

      setToken(data.token);
      setUser(loggedUser);

      setActivePage("Dashboard");

      setDocuments([]);
      setUsers([]);
      setAudits([]);

      setUsersLoaded(false);
      setAuditLoaded(false);

      setShowDocumentForm(false);
      setEditingDocument(null);
      setDocumentForm(EMPTY_DOCUMENT_FORM);
    } catch (err) {
      setError(
        getApiError(
          err,
          "No se pudo iniciar sesión."
        )
      );
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");

    setToken(null);
    setUser(null);

    setDocuments([]);
    setUsers([]);
    setAudits([]);

    setUsersLoaded(false);
    setAuditLoaded(false);

    setShowDocumentForm(false);
    setEditingDocument(null);
    setDocumentForm(EMPTY_DOCUMENT_FORM);

    setActivePage("Dashboard");

    setEmail("");
    setPassword("");
    setError("");
    setSuccessMessage("");
  };

  /*
   * ============================================================
   * DOCUMENTOS
   * ============================================================
   */

  const loadDocuments = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await axios.get(
        `${API_URL}/documentos`,
        authConfig
      );

      setDocuments(response.data);
    } catch (err) {
      setError(
        getApiError(
          err,
          "No se pudieron cargar los documentos."
        )
      );
    } finally {
      setLoading(false);
    }
  };

  const openCreateDocumentForm = () => {
    setError("");
    setSuccessMessage("");

    setEditingDocument(null);

    setDocumentForm({
      ...EMPTY_DOCUMENT_FORM,
      departamentoId: "1",
    });

    setShowDocumentForm(true);
  };

  const openEditDocumentForm = (document) => {
    setError("");
    setSuccessMessage("");

    setEditingDocument(document);

    const department = DEPARTMENTS.find(
      (item) =>
        item.nombre === document.departamento
    );

    setDocumentForm({
      titulo: document.titulo || "",
      descripcion: document.descripcion || "",
      departamentoId: department
        ? String(department.id)
        : "1",
      nivelConfidencialidad: String(
        document.nivelConfidencialidad || 1
      ),
      pais: document.pais || "PERU",
    });

    setShowDocumentForm(true);
  };

  const closeDocumentForm = () => {
    if (savingDocument) {
      return;
    }

    setShowDocumentForm(false);
    setEditingDocument(null);
    setDocumentForm(EMPTY_DOCUMENT_FORM);
  };

  const handleDocumentFormChange = (event) => {
    const { name, value } = event.target;

    setDocumentForm((current) => ({
      ...current,
      [name]: value,
    }));
  };

  const saveDocument = async (event) => {
    event.preventDefault();

    setError("");
    setSuccessMessage("");
    setSavingDocument(true);

    const payload = {
      titulo: documentForm.titulo.trim(),
      descripcion: documentForm.descripcion.trim(),
      departamentoId: Number(
        documentForm.departamentoId
      ),
      nivelConfidencialidad: Number(
        documentForm.nivelConfidencialidad
      ),
      pais: documentForm.pais,
    };

    try {
      if (editingDocument) {
        await axios.put(
          `${API_URL}/documentos/${editingDocument.id}`,
          payload,
          authConfig
        );

        setSuccessMessage(
          "Documento actualizado correctamente."
        );
      } else {
        await axios.post(
          `${API_URL}/documentos`,
          payload,
          authConfig
        );

        setSuccessMessage(
          "Documento creado correctamente."
        );
      }

      setShowDocumentForm(false);
      setEditingDocument(null);
      setDocumentForm(EMPTY_DOCUMENT_FORM);

      await loadDocuments();
    } catch (err) {
      setError(
        getApiError(
          err,
          editingDocument
            ? "No se pudo actualizar el documento."
            : "No se pudo crear el documento."
        )
      );
    } finally {
      setSavingDocument(false);
    }
  };

  const deleteDocument = async (document) => {
    const confirmed = window.confirm(
      `¿Deseas eliminar el documento "${document.titulo}"?`
    );

    if (!confirmed) {
      return;
    }

    setError("");
    setSuccessMessage("");
    setLoading(true);

    try {
      await axios.delete(
        `${API_URL}/documentos/${document.id}`,
        authConfig
      );

      setSuccessMessage(
        "Documento eliminado correctamente."
      );

      await loadDocuments();
    } catch (err) {
      setError(
        getApiError(
          err,
          "No se pudo eliminar el documento."
        )
      );
    } finally {
      setLoading(false);
    }
  };

  const approveDocument = async (document) => {
    const confirmed = window.confirm(
      `¿Deseas aprobar el documento "${document.titulo}"?`
    );

    if (!confirmed) {
      return;
    }

    setError("");
    setSuccessMessage("");
    setLoading(true);

    try {
      await axios.post(
        `${API_URL}/documentos/${document.id}/aprobar`,
        {},
        authConfig
      );

      setSuccessMessage(
        "Documento aprobado correctamente."
      );

      await loadDocuments();
    } catch (err) {
      setError(
        getApiError(
          err,
          "No se pudo aprobar el documento."
        )
      );
    } finally {
      setLoading(false);
    }
  };

  /*
   * ============================================================
   * USUARIOS
   * ============================================================
   */

  const loadUsers = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await axios.get(
        `${API_URL}/usuarios`,
        authConfig
      );

      setUsers(response.data);
      setUsersLoaded(true);
    } catch (err) {
      setError(
        getApiError(
          err,
          "No tienes permiso para consultar los usuarios."
        )
      );
    } finally {
      setLoading(false);
    }
  };

  /*
   * ============================================================
   * AUDITORÍA
   * ============================================================
   */

  const loadAudits = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await axios.get(
        `${API_URL}/auditoria`,
        authConfig
      );

      setAudits(response.data);
      setAuditLoaded(true);
    } catch (err) {
      setError(
        getApiError(
          err,
          "No tienes permiso para consultar la auditoría."
        )
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (token) {
      loadDocuments();
    }
  }, [token]);

  const permittedCount = auditLoaded
    ? audits.filter(
        (audit) =>
          audit.resultado === "PERMITIDO"
      ).length
    : null;

  const deniedCount = auditLoaded
    ? audits.filter(
        (audit) =>
          audit.resultado === "DENEGADO"
      ).length
    : null;

  const totalDocuments = documents.length;

  const publishedDocuments =
    documents.filter(
      (document) =>
        document.estado === "PUBLICADO"
    ).length;

  const maxSecurityLevel =
    documents.length > 0
      ? Math.max(
          ...documents.map(
            (document) =>
              document.nivelConfidencialidad
          )
        )
      : 0;

  const pageTitle = activePage;

  /*
   * ============================================================
   * LOGIN
   * ============================================================
   */

  if (!token) {
    return (
      <div className="login-shell">
        <div className="login-glow login-glow-one" />
        <div className="login-glow login-glow-two" />

        <div className="login-layout">
          <div className="login-brand-side">
            <div className="brand-mark">
              SD
            </div>

            <span className="eyebrow">
              TECHCORP • SECURITY PLATFORM
            </span>

            <h1>
              SecureDocs
              <span>.</span>
            </h1>

            <p>
              Gestión documental con autorización
              dinámica mediante RBAC + ABAC.
            </p>

            <div className="security-flow">
              <div className="security-flow-item">
                <span>01</span>
                Authentication
              </div>

              <div className="security-line" />

              <div className="security-flow-item">
                <span>02</span>
                RBAC
              </div>

              <div className="security-line" />

              <div className="security-flow-item">
                <span>03</span>
                ABAC
              </div>
            </div>
          </div>

          <div className="login-card">
            <div className="login-card-top">
              <div>
                <span className="muted-label">
                  SECURE ACCESS
                </span>

                <h2>
                  Bienvenido
                </h2>

                <p>
                  Ingresa con tu cuenta corporativa.
                </p>
              </div>

              <div className="login-shield">
                ◈
              </div>
            </div>

            <form onSubmit={login}>
              <label>
                Correo electrónico

                <input
                  type="email"
                  placeholder="nombre@securedocs.com"
                  value={email}
                  onChange={(event) =>
                    setEmail(event.target.value)
                  }
                  required
                />
              </label>

              <label>
                Contraseña

                <input
                  type="password"
                  placeholder="••••••••"
                  value={password}
                  onChange={(event) =>
                    setPassword(
                      event.target.value
                    )
                  }
                  required
                />
              </label>

              <button
                className="primary-button login-button"
                type="submit"
                disabled={loading}
              >
                {loading
                  ? "Validando..."
                  : "Iniciar sesión"}
              </button>
            </form>

            {error && (
              <div className="error-box">
                <strong>
                  Acceso rechazado
                </strong>

                <span>{error}</span>
              </div>
            )}

            <div className="login-footer">
              <span>JWT Authentication</span>
              <span>•</span>
              <span>BCrypt</span>
              <span>•</span>
              <span>RBAC + ABAC</span>
            </div>
          </div>
        </div>
      </div>
    );
  }

  /*
   * ============================================================
   * PANEL PRINCIPAL
   * ============================================================
   */

  return (
    <div className="dashboard-shell">
      <aside className="sidebar">

        {/* BRAND */}
        <div className="sidebar-brand">
          <div className="brand-mark small">
            SD
          </div>

          <div>
            <strong>SecureDocs</strong>
            <span>Security Cloud</span>
          </div>
        </div>

        {/* NAVEGACIÓN */}
        <div className="sidebar-section">
          <span className="sidebar-label">
            PAGES
          </span>

          <button
            className={`nav-item ${
              activePage === "Dashboard"
                ? "active"
                : ""
            }`}
            onClick={() => {
              setActivePage("Dashboard");
              setError("");
              setSuccessMessage("");
            }}
          >
            <span className="nav-icon">⌂</span>
            Dashboard
          </button>

          <button
            className={`nav-item ${
              activePage === "Documents"
                ? "active"
                : ""
            }`}
            onClick={() => {
              setActivePage("Documents");
              setError("");
              setSuccessMessage("");
              loadDocuments();
            }}
          >
            <span className="nav-icon">▦</span>
            Documents

            <span className="nav-count">
              {documents.length}
            </span>
          </button>

          <button
            className={`nav-item ${
              activePage === "Users"
                ? "active"
                : ""
            }`}
            onClick={() => {
              setActivePage("Users");

              if (!usersLoaded) {
                loadUsers();
              }
            }}
          >
            <span className="nav-icon">◉</span>
            Users
          </button>

          <button
            className={`nav-item ${
              activePage === "Audit"
                ? "active"
                : ""
            }`}
            onClick={() => {
              setActivePage("Audit");

              if (!auditLoaded) {
                loadAudits();
              }
            }}
          >
            <span className="nav-icon">◌</span>
            Audit
          </button>

          <button
            className="nav-item"
            onClick={() =>
              setError(
                "Los ajustes avanzados estarán disponibles en una siguiente versión."
              )
            }
          >
            <span className="nav-icon">⚙</span>
            Settings
          </button>
        </div>

        {/* SEGURIDAD */}
        <div className="sidebar-section">
          <span className="sidebar-label">
            SECURITY
          </span>

          <div className="security-card">
            <div className="security-card-head">
              <span className="status-dot" />
              System protected
            </div>

            <p>
              JWT, RBAC, ABAC y auditoría activa.
            </p>
          </div>
        </div>

        
      </aside>

      {/* ========================================================
          CONTENIDO
         ======================================================== */}

      <main className="main-content">
        <header className="topbar">
          <div>
            <span className="breadcrumb">
              SecureDocs / {pageTitle}
            </span>

            <h2>{pageTitle}</h2>
          </div>

          <div className="topbar-right">
            <div className="live-status">
              <span className="status-dot" />
              API online
            </div>

            <div className="profile-chip">
              <div className="mini-avatar">
                {user?.correo
                  ?.charAt(0)
                  .toUpperCase()}
              </div>

              <div>
                <strong>
                  {user?.correo}
                </strong>

                <span>
                  {user?.rol}
                </span>
              </div>
            </div>

            {/* ÚNICO BOTÓN DE CERRAR SESIÓN */}
            <button
              className="topbar-logout-button"
              onClick={logout}
              title="Cerrar sesión"
            >
              ↪
              <span>
                Cerrar sesión
              </span>
            </button>
          </div>
        </header>

        {/* MENSAJE DE ÉXITO */}
        {successMessage && (
          <div className="global-success">
            <span>✓</span>
            <span>{successMessage}</span>

            <button
              onClick={() =>
                setSuccessMessage("")
              }
            >
              ×
            </button>
          </div>
        )}

        {/* ERROR GLOBAL */}
        {error && (
          <div className="global-error">
            <span>{error}</span>

            <button
              onClick={() =>
                setError("")
              }
            >
              ×
            </button>
          </div>
        )}

        {/* ======================================================
            DASHBOARD
           ====================================================== */}

        {activePage === "Dashboard" && (
          <>
            <section className="hero-card">
              <div>
                <span className="eyebrow">
                  SECURITY COMMAND CENTER
                </span>

                <h1>
                  Buenas tardes,{" "}
                  <strong>
                    {user?.rol}
                  </strong>{" "}
                  👋
                </h1>

                <p>
                  Aquí tienes un resumen del
                  estado de tus documentos y
                  controles de acceso.
                </p>
              </div>

              <div className="hero-orb">
                <div className="hero-orb-inner">
                  <span>SECURE</span>
                  <strong>24/7</strong>
                </div>
              </div>
            </section>

            <section className="metric-grid">
              <MetricCard
                title="Accessible documents"
                value={totalDocuments}
                change={`${publishedDocuments} publicados`}
                positive
                icon="▦"
              />

              <MetricCard
                title="Allowed requests"
                value={
                  permittedCount ?? "—"
                }
                change={
                  auditLoaded
                    ? "registradas"
                    : "Solo roles autorizados"
                }
                positive
                icon="✓"
              />

              <MetricCard
                title="Denied requests"
                value={
                  deniedCount ?? "—"
                }
                change={
                  auditLoaded
                    ? "registradas"
                    : "Auditoría restringida"
                }
                icon="!"
              />

              <MetricCard
                title="Max classification"
                value={
                  maxSecurityLevel
                    ? `L${maxSecurityLevel}`
                    : "—"
                }
                change="nivel visible"
                positive
                icon="◈"
              />
            </section>

            <section className="content-grid">
              <div className="panel wide-panel">
                <div className="panel-header">
                  <div>
                    <span className="muted-label">
                      DOCUMENT SECURITY
                    </span>

                    <h3>
                      Access overview
                    </h3>
                  </div>

                  <button
                    className="ghost-button"
                    onClick={
                      loadDocuments
                    }
                  >
                    Actualizar
                  </button>
                </div>

                <div className="access-chart">
                  <div className="chart-y">
                    <span>5</span>
                    <span>4</span>
                    <span>3</span>
                    <span>2</span>
                    <span>1</span>
                  </div>

                  <div className="chart-area">
                    <div className="chart-grid-lines">
                      <span />
                      <span />
                      <span />
                      <span />
                      <span />
                    </div>

                    <div className="bars">
                      {documents.map(
                        (document) => (
                          <div
                            className="bar-column"
                            key={document.id}
                          >
                            <div
                              className="bar"
                              style={{
                                height: `${
                                  document.nivelConfidencialidad *
                                  16
                                }px`,
                              }}
                            />

                            <small>
                              D
                              {
                                document.id
                              }
                            </small>
                          </div>
                        )
                      )}
                    </div>
                  </div>
                </div>

                <div className="chart-legend">
                  <span>
                    <i className="legend-dot purple" />
                    Confidentiality level
                  </span>

                  <span>
                    <i className="legend-dot cyan" />
                    Protected resources
                  </span>
                </div>
              </div>

              <div className="panel">
                <div className="panel-header">
                  <div>
                    <span className="muted-label">
                      POLICY ENGINE
                    </span>

                    <h3>
                      Authorization flow
                    </h3>
                  </div>
                </div>

                <div className="policy-flow">
                  <PolicyStep
                    number="01"
                    title="Authentication"
                    description="JWT validated"
                    status="success"
                  />

                  <div className="policy-connector" />

                  <PolicyStep
                    number="02"
                    title="RBAC"
                    description={`Role: ${user?.rol}`}
                    status="success"
                  />

                  <div className="policy-connector" />

                  <PolicyStep
                    number="03"
                    title="ABAC"
                    description="Context evaluated"
                    status="success"
                  />
                </div>

                <div className="security-summary">
                  <span>
                    Current policy state
                  </span>

                  <strong>
                    PROTECTED
                  </strong>
                </div>
              </div>
            </section>

            <section className="content-grid">
              <div className="panel wide-panel">
                <div className="panel-header">
                  <div>
                    <span className="muted-label">
                      RECENT RESOURCES
                    </span>

                    <h3>
                      Documents
                    </h3>
                  </div>

                  <button
                    className="ghost-button"
                    onClick={() =>
                      setActivePage(
                        "Documents"
                      )
                    }
                  >
                    Ver todos
                  </button>
                </div>

                <div className="document-list">
                  {documents
                    .slice(0, 5)
                    .map((document) => (
                      <DocumentRow
                        key={document.id}
                        document={document}
                      />
                    ))}

                  {documents.length ===
                    0 && (
                    <div className="empty-state">
                      No hay documentos
                      disponibles.
                    </div>
                  )}
                </div>
              </div>

              <div className="panel">
                <div className="panel-header">
                  <div>
                    <span className="muted-label">
                      SYSTEM HEALTH
                    </span>

                    <h3>
                      Security controls
                    </h3>
                  </div>
                </div>

                <div className="health-list">
                  <HealthItem
                    label="JWT Authentication"
                    value="ACTIVE"
                  />

                  <HealthItem
                    label="RBAC Engine"
                    value="ACTIVE"
                  />

                  <HealthItem
                    label="ABAC Policies"
                    value="8 / 8"
                  />

                  <HealthItem
                    label="Audit Logging"
                    value="ACTIVE"
                  />
                </div>
              </div>
            </section>
          </>
        )}

        {/* ======================================================
            DOCUMENTS
           ====================================================== */}

        {activePage === "Documents" && (
          <PagePanel
            eyebrow="DOCUMENT MANAGEMENT"
            title="Secure documents"
            action={
              <div className="document-page-actions">
                {roleRules.create && (
                  <button
                    className="primary-button"
                    onClick={
                      openCreateDocumentForm
                    }
                  >
                    + Nuevo documento
                  </button>
                )}

                <button
                  className="ghost-button"
                  onClick={
                    loadDocuments
                  }
                >
                  Actualizar
                </button>
              </div>
            }
          >
            {loading && (
              <div className="loading-state">
                Cargando documentos...
              </div>
            )}

            <div className="document-list">
              {documents.map(
                (document) => (
                  <DocumentRow
                    key={document.id}
                    document={document}
                    detailed
                    showActions
                    roleRules={roleRules}
                    onEdit={
                      openEditDocumentForm
                    }
                    onDelete={
                      deleteDocument
                    }
                    onApprove={
                      approveDocument
                    }
                  />
                )
              )}

              {!loading &&
                documents.length === 0 && (
                  <div className="empty-state">
                    No hay documentos
                    disponibles.
                  </div>
                )}
            </div>
          </PagePanel>
        )}

        {/* ======================================================
            USERS
           ====================================================== */}

        {activePage === "Users" && (
          <PagePanel
            eyebrow="IDENTITY MANAGEMENT"
            title="Users"
            action={
              <button
                className="primary-button"
                onClick={loadUsers}
              >
                Refresh
              </button>
            }
          >
            {users.length > 0 ? (
              <div className="table-wrap">
                <table>
                  <thead>
                    <tr>
                      <th>User</th>
                      <th>Role</th>
                      <th>Department</th>
                      <th>Security</th>
                      <th>Status</th>
                    </tr>
                  </thead>

                  <tbody>
                    {users.map((item) => (
                      <tr key={item.id}>
                        <td>
                          <div className="table-user">
                            <div className="mini-avatar">
                              {item.nombre
                                ?.charAt(0)
                                .toUpperCase()}
                            </div>

                            <div>
                              <strong>
                                {item.nombre}
                              </strong>

                              <span>
                                {item.correo}
                              </span>
                            </div>
                          </div>
                        </td>

                        <td>
                          <span className="tag purple-tag">
                            {item.rol}
                          </span>
                        </td>

                        <td>
                          {item.departamento}
                        </td>

                        <td>
                          L
                          {
                            item.nivelSeguridad
                          }
                        </td>

                        <td>
                          <span className="status-pill">
                            {item.estado}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <div className="empty-state">
                Este módulo requiere el
                permiso MANAGE_USERS.
              </div>
            )}
          </PagePanel>
        )}

        {/* ======================================================
            AUDIT
           ====================================================== */}

        {activePage === "Audit" && (
          <PagePanel
            eyebrow="AUDIT TRAIL"
            title="Authorization events"
            action={
              <button
                className="primary-button"
                onClick={loadAudits}
              >
                Refresh
              </button>
            }
          >
            {audits.length > 0 ? (
              <div className="audit-list">
                {audits.map((audit) => (
                  <div
                    className="audit-item"
                    key={audit.id}
                  >
                    <div className="audit-badge">
                      {audit.resultado ===
                      "PERMITIDO"
                        ? "✓"
                        : "!"}
                    </div>

                    <div className="audit-main">
                      <div className="audit-top">
                        <strong>
                          {audit.accion}
                        </strong>

                        <span
                          className={
                            audit.resultado ===
                            "PERMITIDO"
                              ? "allowed"
                              : "denied"
                          }
                        >
                          {audit.resultado}
                        </span>
                      </div>

                      <span>
                        {audit.usuario} ·{" "}
                        {audit.recurso}
                      </span>

                      <small>
                        {audit.motivo}
                      </small>
                    </div>

                    <time>
                      {audit.fecha}
                    </time>
                  </div>
                ))}
              </div>
            ) : (
              <div className="restricted-state">
                <div className="restricted-icon">
                  ◈
                </div>

                <h3>
                  Auditoría protegida
                </h3>

                <p>
                  Este módulo requiere
                  VIEW_AUDIT.
                </p>
              </div>
            )}
          </PagePanel>
        )}
      </main>

      {/* ========================================================
          MODAL CREAR / EDITAR DOCUMENTO
         ======================================================== */}

      {showDocumentForm && (
        <div
          className="modal-overlay"
          onMouseDown={(event) => {
            if (
              event.target ===
              event.currentTarget
            ) {
              closeDocumentForm();
            }
          }}
        >
          <div className="modal-card">
            <div className="modal-header">
              <div>
                <span className="muted-label">
                  DOCUMENT MANAGEMENT
                </span>

                <h2>
                  {editingDocument
                    ? "Editar documento"
                    : "Nuevo documento"}
                </h2>

                <p>
                  {editingDocument
                    ? "Actualiza la información del documento."
                    : "Registra un nuevo documento en SecureDocs."}
                </p>
              </div>

              <button
                className="modal-close"
                onClick={
                  closeDocumentForm
                }
                disabled={savingDocument}
              >
                ×
              </button>
            </div>

            <form
              className="document-form"
              onSubmit={
                saveDocument
              }
            >
              <label>
                Título

                <input
                  type="text"
                  name="titulo"
                  value={
                    documentForm.titulo
                  }
                  onChange={
                    handleDocumentFormChange
                  }
                  placeholder="Ej. Informe financiero"
                  required
                />
              </label>

              <label>
                Descripción

                <textarea
                  name="descripcion"
                  value={
                    documentForm.descripcion
                  }
                  onChange={
                    handleDocumentFormChange
                  }
                  placeholder="Describe brevemente el documento..."
                  rows="4"
                  required
                />
              </label>

              <div className="form-grid">
                <label>
                  Departamento

                  <select
                    name="departamentoId"
                    value={
                      documentForm.departamentoId
                    }
                    onChange={
                      handleDocumentFormChange
                    }
                    required
                  >
                    {DEPARTMENTS.map(
                      (department) => (
                        <option
                          key={
                            department.id
                          }
                          value={
                            department.id
                          }
                        >
                          {department.nombre}
                        </option>
                      )
                    )}
                  </select>
                </label>

                <label>
                  Nivel de confidencialidad

                  <select
                    name="nivelConfidencialidad"
                    value={
                      documentForm.nivelConfidencialidad
                    }
                    onChange={
                      handleDocumentFormChange
                    }
                    required
                  >
                    <option value="1">
                      Nivel 1 — Público
                    </option>

                    <option value="2">
                      Nivel 2 — Interno
                    </option>

                    <option value="3">
                      Nivel 3 — Restringido
                    </option>

                    <option value="4">
                      Nivel 4 — Confidencial
                    </option>

                    <option value="5">
                      Nivel 5 — Alto secreto
                    </option>
                  </select>
                </label>
              </div>

              <label>
                País

                <select
                  name="pais"
                  value={
                    documentForm.pais
                  }
                  onChange={
                    handleDocumentFormChange
                  }
                  required
                >
                  <option value="PERU">
                    PERU
                  </option>

                  <option value="CHILE">
                    CHILE
                  </option>

                  <option value="COLOMBIA">
                    COLOMBIA
                  </option>

                  <option value="MEXICO">
                    MEXICO
                  </option>
                </select>
              </label>

              {!editingDocument && (
                <div className="form-info">
                  El documento se registrará
                  inicialmente como{" "}
                  <strong>
                    PENDIENTE
                  </strong>.
                </div>
              )}

              <div className="modal-actions">
                <button
                  type="button"
                  className="ghost-button"
                  onClick={
                    closeDocumentForm
                  }
                  disabled={savingDocument}
                >
                  Cancelar
                </button>

                <button
                  type="submit"
                  className="primary-button"
                  disabled={savingDocument}
                >
                  {savingDocument
                    ? "Guardando..."
                    : editingDocument
                    ? "Guardar cambios"
                    : "Crear documento"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

/*
 * ============================================================
 * COMPONENTES
 * ============================================================
 */

function MetricCard({
  title,
  value,
  change,
  positive = false,
  icon,
}) {
  return (
    <div className="metric-card">
      <div className="metric-top">
        <span>{title}</span>

        <div className="metric-icon">
          {icon}
        </div>
      </div>

      <div className="metric-value">
        {value}
      </div>

      <div
        className={`metric-change ${
          positive ? "positive" : ""
        }`}
      >
        {change}
      </div>

      <div className="mini-sparkline">
        <span />
        <span />
        <span />
        <span />
        <span />
        <span />
        <span />
      </div>
    </div>
  );
}

function PolicyStep({
  number,
  title,
  description,
  status,
}) {
  return (
    <div className="policy-step">
      <div
        className={`policy-number ${status}`}
      >
        {number}
      </div>

      <div>
        <strong>{title}</strong>
        <span>{description}</span>
      </div>
    </div>
  );
}

function DocumentRow({
  document,
  detailed = false,
  showActions = false,
  roleRules,
  onEdit,
  onDelete,
  onApprove,
}) {
  return (
    <div
      className={`document-row ${
        detailed ? "detailed" : ""
      }`}
    >
      <div className="document-icon">
        ▤
      </div>

      <div className="document-main">
        <strong>
          {document.titulo}
        </strong>

        <span>
          {document.departamento} ·{" "}
          {document.pais}
        </span>

        {detailed && (
          <small>
            {document.descripcion}
          </small>
        )}
      </div>

      <div className="document-classification">
        <span>NIVEL</span>

        <strong>
          {
            document.nivelConfidencialidad
          }
        </strong>
      </div>

      <span className="status-pill">
        {document.estado}
      </span>

      {showActions && (
        <div className="document-actions">

          {roleRules.update && (
            <button
              className="document-action edit"
              onClick={() =>
                onEdit(document)
              }
            >
              Editar
            </button>
          )}

          {roleRules.approve && (
            <button
              className="document-action approve"
              onClick={() =>
                onApprove(document)
              }
            >
              Aprobar
            </button>
          )}

          {roleRules.delete && (
            <button
              className="document-action delete"
              onClick={() =>
                onDelete(document)
              }
            >
              Eliminar
            </button>
          )}
        </div>
      )}
    </div>
  );
}

function HealthItem({
  label,
  value,
}) {
  return (
    <div className="health-item">
      <div>
        <span className="status-dot" />
        {label}
      </div>

      <strong>{value}</strong>
    </div>
  );
}

function PagePanel({
  eyebrow,
  title,
  action,
  children,
}) {
  return (
    <section className="page-panel">
      <div className="page-panel-header">
        <div>
          <span className="muted-label">
            {eyebrow}
          </span>

          <h1>{title}</h1>
        </div>

        {action}
      </div>

      {children}
    </section>
  );
}

export default App;
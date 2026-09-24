package com.tecsup.securedocs.document;

import com.tecsup.securedocs.department.Department;
import com.tecsup.securedocs.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "documentos")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String descripcion;

    @ManyToOne(optional = false)
    @JoinColumn(name = "propietario_id")
    private User propietario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "departamento_id")
    private Department departamento;

    @Column(name = "nivel_confidencialidad", nullable = false)
    private Integer nivelConfidencialidad;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private String pais;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    public Document() {
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public User getPropietario() {
        return propietario;
    }

    public void setPropietario(User propietario) {
        this.propietario = propietario;
    }

    public Department getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Department departamento) {
        this.departamento = departamento;
    }

    public Integer getNivelConfidencialidad() {
        return nivelConfidencialidad;
    }

    public void setNivelConfidencialidad(Integer nivelConfidencialidad) {
        this.nivelConfidencialidad = nivelConfidencialidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
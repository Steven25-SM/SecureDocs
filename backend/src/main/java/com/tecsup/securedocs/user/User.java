package com.tecsup.securedocs.user;

import com.tecsup.securedocs.department.Department;
import com.tecsup.securedocs.role.Role;
import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false)
    private String password;

    @ManyToOne(optional = false)
    @JoinColumn(name = "rol_id")
    private Role rol;

    @ManyToOne(optional = false)
    @JoinColumn(name = "departamento_id")
    private Department departamento;

    @Column(name = "nivel_seguridad", nullable = false)
    private Integer nivelSeguridad;

    @Column(nullable = false)
    private String pais;

    @Column(name = "tipo_contrato", nullable = false)
    private String tipoContrato;

    @Column(nullable = false)
    private String estado;

    public User() {
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRol() {
        return rol;
    }

    public void setRol(Role rol) {
        this.rol = rol;
    }

    public Department getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Department departamento) {
        this.departamento = departamento;
    }

    public Integer getNivelSeguridad() {
        return nivelSeguridad;
    }

    public void setNivelSeguridad(Integer nivelSeguridad) {
        this.nivelSeguridad = nivelSeguridad;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
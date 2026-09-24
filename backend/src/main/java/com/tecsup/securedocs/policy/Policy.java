package com.tecsup.securedocs.policy;

import jakarta.persistence.*;

@Entity
@Table(name = "politicas")
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(nullable = false, length = 1000)
    private String descripcion;

    @Column(nullable = false)
    private boolean activa;

    public Policy() {
    }

    public Policy(String nombre, String descripcion, boolean activa) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activa = activa;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }
}
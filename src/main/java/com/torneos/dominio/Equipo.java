package com.torneos.dominio;

/**
 * Entidad que representa un equipo participante en torneos.
 */
public class Equipo {

    private int    id;
    private String nombre;

    // -------------------------------------------------------------------------
    // Constructores
    // -------------------------------------------------------------------------

    public Equipo() {}

    public Equipo(int id, String nombre) {
        this.id     = id;
        this.nombre = nombre;
    }

    // -------------------------------------------------------------------------
    // Getters y Setters
    // -------------------------------------------------------------------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Equipo{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}

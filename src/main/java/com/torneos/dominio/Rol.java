package com.torneos.dominio;

/**
 * Entidad que representa un rol de usuario en el sistema.
 * Valores posibles: ADMIN, JUGADOR.
 */
public class Rol {

    private int    id;
    private String nombre;

    // -------------------------------------------------------------------------
    // Constructores
    // -------------------------------------------------------------------------

    public Rol() {}

    public Rol(int id, String nombre) {
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
        return "Rol{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}

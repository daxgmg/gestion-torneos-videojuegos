package com.torneos.dominio;

/**
 * Entidad que representa un usuario registrado en el sistema.
 */
public class User {

    private int    id;
    private String nombre;
    private String email;
    private String password;
    private Rol    rol;

    // -------------------------------------------------------------------------
    // Constructores
    // -------------------------------------------------------------------------

    public User() {}

    public User(int id, String nombre, String email, String password, Rol rol) {
        this.id       = id;
        this.nombre   = nombre;
        this.email    = email;
        this.password = password;
        this.rol      = rol;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", rol=" + rol +
                '}';
    }
}

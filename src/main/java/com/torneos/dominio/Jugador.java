package com.torneos.dominio;

/**
 * Entidad que representa a un jugador, miembro de un equipo
 * y asociado a una cuenta de usuario.
 */
public class Jugador {

    private int    id;
    private String nombre;
    private String alias;
    private Equipo equipo;
    private User   user;

    // -------------------------------------------------------------------------
    // Constructores
    // -------------------------------------------------------------------------

    public Jugador() {}

    public Jugador(int id, String nombre, String alias, Equipo equipo, User user) {
        this.id     = id;
        this.nombre = nombre;
        this.alias  = alias;
        this.equipo = equipo;
        this.user   = user;
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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Jugador{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", alias='" + alias + '\'' +
                ", equipo=" + equipo +
                ", user=" + user +
                '}';
    }
}

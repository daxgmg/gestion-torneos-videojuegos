package com.torneos.dominio;

/**
 * Entidad que representa la inscripción de un equipo en un torneo.
 */
public class Inscripcion {

    private int    id;
    private Torneo torneo;
    private Equipo equipo;

    // -------------------------------------------------------------------------
    // Constructores
    // -------------------------------------------------------------------------

    public Inscripcion() {}

    public Inscripcion(int id, Torneo torneo, Equipo equipo) {
        this.id     = id;
        this.torneo = torneo;
        this.equipo = equipo;
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

    public Torneo getTorneo() {
        return torneo;
    }

    public void setTorneo(Torneo torneo) {
        this.torneo = torneo;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Inscripcion{" +
                "id=" + id +
                ", torneo=" + torneo +
                ", equipo=" + equipo +
                '}';
    }
}

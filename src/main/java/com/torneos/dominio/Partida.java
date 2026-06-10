package com.torneos.dominio;

/**
 * Entidad que representa una partida disputada dentro de un torneo.
 */
public class Partida {

    private int    id;
    private Torneo torneo;
    private Equipo equipo1;
    private Equipo equipo2;
    private String resultado;
    private String fecha;

    // -------------------------------------------------------------------------
    // Constructores
    // -------------------------------------------------------------------------

    public Partida() {}

    public Partida(int id, Torneo torneo, Equipo equipo1, Equipo equipo2,
                   String resultado, String fecha) {
        this.id        = id;
        this.torneo    = torneo;
        this.equipo1   = equipo1;
        this.equipo2   = equipo2;
        this.resultado = resultado;
        this.fecha     = fecha;
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

    public Equipo getEquipo1() {
        return equipo1;
    }

    public void setEquipo1(Equipo equipo1) {
        this.equipo1 = equipo1;
    }

    public Equipo getEquipo2() {
        return equipo2;
    }

    public void setEquipo2(Equipo equipo2) {
        this.equipo2 = equipo2;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Partida{" +
                "id=" + id +
                ", torneo=" + torneo +
                ", equipo1=" + equipo1 +
                ", equipo2=" + equipo2 +
                ", resultado='" + resultado + '\'' +
                ", fecha='" + fecha + '\'' +
                '}';
    }
}

package com.torneos.dominio;

/**
 * Entidad que representa un torneo de videojuegos.
 * Estado posible: ACTIVO, FINALIZADO.
 */
public class Torneo {

    private int    id;
    private String nombre;
    private String fechaInicio;
    private String fechaFin;
    private String estado;
    private String recompensa;

    // -------------------------------------------------------------------------
    // Constructores
    // -------------------------------------------------------------------------

    public Torneo() {}

    public Torneo(int id, String nombre, String fechaInicio, String fechaFin, String estado) {
        this.id          = id;
        this.nombre      = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin    = fechaFin;
        this.estado      = estado;
    }

    public Torneo(int id, String nombre, String fechaInicio, String fechaFin, String estado, String recompensa) {
        this.id          = id;
        this.nombre      = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin    = fechaFin;
        this.estado      = estado;
        this.recompensa  = recompensa;
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

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getRecompensa() {
        return recompensa;
    }

    public void setRecompensa(String recompensa) {
        this.recompensa = recompensa;
    }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Torneo{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", fechaInicio='" + fechaInicio + '\'' +
                ", fechaFin='" + fechaFin + '\'' +
                ", estado='" + estado + '\'' +
                ", recompensa='" + recompensa + '\'' +
                '}';
    }
}

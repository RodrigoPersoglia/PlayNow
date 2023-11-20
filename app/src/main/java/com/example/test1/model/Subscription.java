package com.example.test1.model;

import java.io.Serializable;
import java.util.Date;

public class Subscription implements Serializable {
    private String Usuario;
    private String Evento;
    private Date Fecha;
    private Date Hora;
    private String Nombre;
    private String Status;
    private String Deporte;

    private Double Latitud;
    private Double Longitud;
    public Subscription() {
    }

    public Subscription(String usuario, String evento, Date fecha, Date hora, String nombre, String status, String deporte, Double latitud, Double longitud) {
        this.Usuario = usuario;
        this.Evento = evento;
        this.Fecha = fecha;
        this.Hora = hora;
        this.Nombre = nombre;
        this.Status = status;
        this.Deporte = deporte;
        this.Latitud = latitud;
        this.Longitud = longitud;
    }

    public String getUsuario() { return Usuario;}
    public void setUsuario(String usuario) { Usuario = usuario;}

    public String getEvento() { return Evento;}
    public void setEvento(String evento) { Evento = evento;}

    public Date getFecha() { return Fecha;}
    public void setFecha(Date fecha) { Fecha = fecha;}

    public Date getHora() { return Hora;}
    public void setHora(Date hora) {Hora = hora;}

    public String getNombre() { return Nombre;}
    public void setNombre(String nombre) { Nombre = nombre;}

    public String getStatus() { return Status;}
    public void setStatus(String status) { Status = status;}

    public String getDeporte() { return Deporte;}
    public void setDeporte(String deporte) { Deporte = deporte;}

    public Double getLatitud() {
        return Latitud;
    }

    public void setLatitud(Double latitud) {
        Latitud = latitud;
    }

    public Double getLongitud() {
        return Longitud;
    }

    public void setLongitud(Double longitud) {
        Longitud = longitud;
    }
    @Override
    public String toString() {return Nombre;}
}
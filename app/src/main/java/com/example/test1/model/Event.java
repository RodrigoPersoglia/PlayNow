package com.example.test1.model;

import java.util.Date;

public class Event {
    private String Uid;
    private String Nombre;
    private String Publicador;
    private String Status;
    private String Sports;
    private Date Fecha;
    private Date Hora;
    private String Localizacion;

    public Event() {
    }

    public Event(String nombre, String publicador, String status, String sports, Date fecha, Date hora, String localizacion) {
        this.Nombre = nombre;
        this.Publicador = publicador;
        this.Status = status;
        this.Sports = sports;
        this.Fecha = fecha;
        this.Hora = hora;
        this.Localizacion = localizacion;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getPublicador() {
        return Publicador;
    }

    public void setPublicador(String publicador) {
        Publicador = publicador;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getSports() {
        return Sports;
    }

    public void setSports(String sports) {
        Sports = sports;
    }

    public Date getFecha() {
        return Fecha;
    }
    public void setFecha(Date fecha) {
        Fecha = fecha;
    }

    public Date getHora() {
        return Hora;
    }

    public void setHora(Date hora) {
        Hora = hora;
    }

    public String getLocalizacion() {
        return Localizacion;
    }

    public void setLocalizacion(String localizacion) {
        Localizacion = localizacion;
    }

    @Override
    public String toString() {
        return Nombre;
    }
}
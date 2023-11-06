package com.example.test1.model;

public class Event {
    private String Uid;
    private String Nombre;
    private String Publicador;
    private String Status;
    private String Localizacion;

    public Event() {
        // Constructor vacío necesario para Firebase, asegúrate de agregarlo
    }

    public Event(String nombre, String publicador, String status, String localizacion) {
        this.Nombre = nombre;
        this.Publicador = publicador;
        this.Status = status;
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
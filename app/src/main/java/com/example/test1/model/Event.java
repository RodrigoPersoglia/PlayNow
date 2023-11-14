package com.example.test1.model;

import java.util.Date;

public class Event {
    private String Id;
    private String Nombre;
    private String Publicador;
    private String Status;
    private String Deporte;
    private Date Fecha;
    private Date Hora;
    private String Localizacion;
    private Double Latitud;
    private Double Longitud;
    private Integer Cantidad;

    public Event() {
    }

    public Event(String nombre, String publicador, String status, String deporte, Date fecha, Date hora, String localizacion, String id, Double latitud, Double longitud, Integer cantidad) {
        this.Nombre = nombre;
        this.Publicador = publicador;
        this.Status = status;
        this.Deporte = deporte;
        this.Fecha = fecha;
        this.Hora = hora;
        this.Localizacion = localizacion;
        this.Id = id;
        this.Latitud = latitud;
        this.Longitud = longitud;
        this.Cantidad = cantidad;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
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

    public String getDeporte() {
        return Deporte;
    }

    public void setDeporte(String sports) {
        Deporte = sports;
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

    public Integer getCantidad() {
        return Cantidad;
    }

    public void setCantidad(Integer cantidad) {
        Cantidad = cantidad;
    }

    @Override
    public String toString() {
        return Nombre;
    }
}
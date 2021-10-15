package com.example.firebase_citas.Modelo;

public class Especialidad {
    private String id;
    private String nombre;

    public Especialidad(String idAreaid, String nombre){
        this.id = id;
        this.nombre = nombre;
    }

    public Especialidad() {
        this.id = "";
        this.nombre = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}

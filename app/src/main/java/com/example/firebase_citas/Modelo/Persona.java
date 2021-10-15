package com.example.firebase_citas.Modelo;

public class Persona {

    protected String cedula;
    protected String nombre;
    protected String apellido;
    protected String edad;
    protected String correo;
    protected String telefono;

    public Persona(String cedula, String nombre, String apellido, String edad, String correo, String telefono) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.correo = correo;
        this.telefono = telefono;
    }

    public Persona() {
        this.cedula = "";
        this.nombre = "";
        this.apellido = "";
        this.edad = "";
        this.correo = "";
        this.telefono = "";
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return cedula + " | " +
                nombre  + " " +
                apellido + " | " +
                edad;
    }
}

package com.example.firebase_citas.Modelo;

public class Admin extends Persona{

    private String contra, pregSeguridad, respuesta;

    public Admin(String cedula, String nombre, String apellido, String edad, String correo, String telefono, String contra, String pregSeguridad, String respuesta) {
        super(cedula, nombre, apellido, edad, correo, telefono);
        this.contra = contra;
        this.pregSeguridad = pregSeguridad;
        this.respuesta = respuesta;
    }

    public Admin() {
        super();
        this.contra = "";
        this.pregSeguridad = "";
        this.respuesta = "";
    }

    public String getContra() {
        return contra;
    }

    public void setContra(String contra) {
        this.contra = contra;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public String getPregSeguridad() {
        return pregSeguridad;
    }

    public void setPregSeguridad(String pregSeguridad) {
        this.pregSeguridad = pregSeguridad;
    }

}

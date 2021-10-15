package com.example.firebase_citas.Modelo;

public class Medico extends Persona {
    private String contra, pregSeguridad, respuesta;
    private Especialidad especialidad;

    public Medico(String cedula, String nombre, String apellido, String edad, String correo, String telefono, String contra, String pregSeguridad, String respuesta, Especialidad especialidad) {
        super(cedula, nombre, apellido, edad, correo, telefono);
        this.contra = contra;
        this.pregSeguridad = pregSeguridad;
        this.respuesta = respuesta;
        this.especialidad = especialidad;
    }

    public Medico() {
        super();
        this.contra = "";
        this.pregSeguridad = "";
        this.respuesta = "";
        this.especialidad = new Especialidad();
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

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    @Override
    public String toString() {
        return  cedula;
    }
}

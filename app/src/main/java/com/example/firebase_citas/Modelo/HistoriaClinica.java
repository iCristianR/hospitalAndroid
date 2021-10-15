package com.example.firebase_citas.Modelo;

public class HistoriaClinica {
    private String id;
    private String fecha_apertura;
    private Paciente paciente;
    private String estatura;
    private String peso;
    private String rh;
    private String enfermedades;

    public HistoriaClinica(String id, String fecha_apertura, Paciente paciente, String estatura, String peso, String rh, String enfermedades) {
        this.id = id;
        this.fecha_apertura = fecha_apertura;
        this.paciente = paciente;
        this.estatura = estatura;
        this.peso = peso;
        this.rh = rh;
        this.enfermedades = enfermedades;
    }

    public HistoriaClinica() {
        this.id = "";
        this.fecha_apertura = "";
        this.paciente = new Paciente();
        this.estatura = "";
        this.peso = "";
        this.rh = "";
        this.enfermedades = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFecha_apertura() {
        return fecha_apertura;
    }

    public void setFecha_apertura(String fecha_apertura) {
        this.fecha_apertura = fecha_apertura;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public String getEstatura() {
        return estatura;
    }

    public void setEstatura(String estatura) {
        this.estatura = estatura;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public String getRh() {
        return rh;
    }

    public void setRh(String rh) {
        this.rh = rh;
    }

    public String getEnfermedades() {
        return enfermedades;
    }

    public void setEnfermedades(String enfermedades) {
        this.enfermedades = enfermedades;
    }

}

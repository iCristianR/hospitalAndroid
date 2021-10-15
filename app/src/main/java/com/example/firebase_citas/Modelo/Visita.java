package com.example.firebase_citas.Modelo;

public class Visita {
    private String id;
    private Paciente paciente;
    private Medico medico;
    private String fecha;
    private String hora;
    private String estado;

    public Visita(String id, Paciente paciente, Medico medico, String fecha, String hora, String estado) {
        this.id = id;
        this.paciente = paciente;
        this.medico = medico;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
    }

    public Visita() {
        this.id = "";
        this.paciente = new Paciente();
        this.medico = new Medico();
        this.fecha = "";
        this.hora = "";
        this.estado = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return id;
    }
}

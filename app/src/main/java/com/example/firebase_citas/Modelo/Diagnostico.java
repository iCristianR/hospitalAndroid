package com.example.firebase_citas.Modelo;

public class Diagnostico {
    private String id;
    private Visita visita;
    private HistoriaClinica hc;
    private String observaciones;
    private String medicamentos;

    public Diagnostico(String id, Visita visita, HistoriaClinica hc, String observaciones, String medicamentos) {
        this.id = id;
        this.visita = visita;
        this.hc = hc;
        this.observaciones = observaciones;
        this.medicamentos = medicamentos;
    }

    public Diagnostico() {
        this.id = "";
        this.visita = new Visita();
        this.hc = new HistoriaClinica();
        this.observaciones = "";
        this.medicamentos = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Visita getVisita() {
        return visita;
    }

    public void setVisita(Visita visita) {
        this.visita = visita;
    }

    public HistoriaClinica getHc() {
        return hc;
    }

    public void setHc(HistoriaClinica hc) {
        this.hc = hc;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(String medicamentos) {
        this.medicamentos = medicamentos;
    }
}

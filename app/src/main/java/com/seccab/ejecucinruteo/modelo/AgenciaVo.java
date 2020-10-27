package com.seccab.ejecucinruteo.modelo;

public class AgenciaVo {
    private int cod_agencia;
    private String nom_agencia;

    public AgenciaVo(int cod_agencia, String nom_agencia) {
        this.cod_agencia = cod_agencia;
        this.nom_agencia = nom_agencia;
    }

    public AgenciaVo() {

    }

    public int getCod_agencia() {
        return cod_agencia;
    }

    public void setCod_agencia(int cod_agencia) {
        this.cod_agencia = cod_agencia;
    }

    public String getNom_agencia() {
        return nom_agencia;
    }

    public void setNom_agencia(String nom_agencia) {
        this.nom_agencia = nom_agencia;
    }
}

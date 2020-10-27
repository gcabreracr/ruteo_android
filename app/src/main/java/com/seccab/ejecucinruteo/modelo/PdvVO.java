package com.seccab.ejecucinruteo.modelo;

public class PdvVO {

    private int cod_agencia;
    private int cod_pdv;
    private String nom_pdv;

    public PdvVO(int cod_agencia, int cod_pdv, String nom_pdv) {
        this.cod_agencia = cod_agencia;
        this.cod_pdv = cod_pdv;
        this.nom_pdv = nom_pdv;
    }

    public PdvVO() {

    }

    public int getCod_agencia() {
        return cod_agencia;
    }

    public void setCod_agencia(int cod_agencia) {
        this.cod_agencia = cod_agencia;
    }

    public int getCod_pdv() {
        return cod_pdv;
    }

    public void setCod_pdv(int cod_pdv) {
        this.cod_pdv = cod_pdv;
    }

    public String getNom_pdv() {
        return nom_pdv;
    }

    public void setNom_pdv(String nom_pdv) {
        this.nom_pdv = nom_pdv;
    }
}

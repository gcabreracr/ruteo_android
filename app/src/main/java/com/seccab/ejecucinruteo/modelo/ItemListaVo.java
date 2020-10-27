package com.seccab.ejecucinruteo.modelo;

public class ItemListaVo {
    private int codArt;
    private String nomCortoArt;
    private String medida;
    private double precioArt;
    private int porDescto;
    private int exento;
    private int porImpto;

    public ItemListaVo(){
    }


    public int getPorDescto() {
        return porDescto;
    }

    public void setPorDescto(int porDescto) {
        this.porDescto = porDescto;
    }

    public int getExento() {
        return exento;
    }

    public void setExento(int exento) {
        this.exento = exento;
    }

    public int getPorImpto() {
        return porImpto;
    }

    public void setPorImpto(int porImpto) {
        this.porImpto = porImpto;
    }

    public int getCodArt() {
        return codArt;
    }

    public void setCodArt(int codArt) {
        this.codArt = codArt;
    }

    public String getNomCortoArt() {
        return nomCortoArt;
    }

    public void setNomCortoArt(String nomCortoArt) {
        this.nomCortoArt = nomCortoArt;
    }

    public String getMedida() {
        return medida;
    }

    public void setMedida(String medida) {
        this.medida = medida;
    }

    public double getPrecioArt() {
        return precioArt;
    }

    public void setPrecioArt(double precioArt) {
        this.precioArt = precioArt;
    }
}

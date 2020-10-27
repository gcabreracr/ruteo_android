package com.seccab.ejecucinruteo.modelo;

public class ItemVo {
    private int coditem;
    private String nomLargo;
    private String nomCorto;
    private double precio;
    private String medida;
    private int desctoArt;
    private int imptoArt;
    private int exento;



    public ItemVo() {

    }

    public int getCoditem() {
        return coditem;
    }

    public void setCoditem(int coditem) {
        this.coditem = coditem;
    }

    public String getNomLargo() {
        return nomLargo;
    }

    public void setNomLargo(String nomLargo) {
        this.nomLargo = nomLargo;
    }

    public String getNomCorto() {
        return nomCorto;
    }

    public void setNomCorto(String nomCorto) {
        this.nomCorto = nomCorto;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getMedida() {
        return medida;
    }

    public void setMedida(String medida) {
        this.medida = medida;
    }

    public int getDesctoArt() {
        return desctoArt;
    }

    public void setDesctoArt(int desctoArt) {
        this.desctoArt = desctoArt;
    }

    public int getImptoArt() {
        return imptoArt;
    }

    public void setImptoArt(int imptoArt) {
        this.imptoArt = imptoArt;
    }

    public int getExento() {
        return exento;
    }

    public void setExento(int exento) {
        this.exento = exento;
    }
}

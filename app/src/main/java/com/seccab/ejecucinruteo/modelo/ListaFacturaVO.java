package com.seccab.ejecucinruteo.modelo;

public class ListaFacturaVO {
    private int numFactura;
    private double monFactura;
    private String nomNegocio;

    public ListaFacturaVO() {

    }

    public int getNumFactura() {
        return numFactura;
    }

    public void setNumFactura(int numFactura) {
        this.numFactura = numFactura;
    }

    public double getMonFactura() {
        return monFactura;
    }

    public void setMonFactura(double monFactura) {
        this.monFactura = monFactura;
    }

    public String getNomNegocio() {
        return nomNegocio;
    }

    public void setNomNegocio(String nomNegocio) {
        this.nomNegocio = nomNegocio;
    }
}

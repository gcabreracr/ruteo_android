package com.seccab.ejecucinruteo.modelo;

public class VentaDiariaVO {

    private String nomArticulo;
    private int cant_venta;
    private double monto_liq;
    private int carga;


    public VentaDiariaVO(String nomArticulo, int cant_venta, double monto_liq, int carga, int sobrante) {
        this.nomArticulo = nomArticulo;
        this.cant_venta = cant_venta;
        this.monto_liq = monto_liq;
        this.carga = carga;
    }

    public VentaDiariaVO() {

    }

    public String getNomArticulo() {
        return nomArticulo;
    }

    public void setNomArticulo(String nomArticulo) {
        this.nomArticulo = nomArticulo;
    }

    public int getCant_venta() {
        return cant_venta;
    }

    public void setCant_venta(int cant_venta) {
        this.cant_venta = cant_venta;
    }

    public double getMonto_liq() {
        return monto_liq;
    }

    public void setMonto_liq(double monto_liq) {
        this.monto_liq = monto_liq;
    }

    public int getCarga() {
        return carga;
    }

    public void setCarga(int carga) {
        this.carga = carga;
    }


}

package com.seccab.ejecucinruteo.modelo;

public class ItemListaFacVo {

    private int codItem;
    private String nomItem;
    private int cantidad;
    private double monto;
    private int pordescto;
    private double descuento;
    private int porimpto;
    private double impuesto;
    private double precio;
    private int exento;

    public ItemListaFacVo() {

    }

    public int getCodItem() {
        return codItem;
    }

    public int getPordescto() {
        return pordescto;
    }

    public int getPorimpto() {
        return porimpto;
    }

    public double getPrecio() {
        return precio;
    }

    public int getExento() {
        return exento;
    }

    public void setPordescto(int pordescto) {
        this.pordescto = pordescto;
    }



    public void setPorimpto(int porimpto) {
        this.porimpto = porimpto;
    }



    public void setExento(int exento) {
        this.exento = exento;
    }



    public void setCodItem(int codItem) {
        this.codItem = codItem;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public double getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(double impuesto) {
        this.impuesto = impuesto;
    }



    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getNomItem() {
        return nomItem;
    }

    public void setNomItem(String nomItem) {
        this.nomItem = nomItem;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(int monto) {
        this.monto = monto;
    }
}

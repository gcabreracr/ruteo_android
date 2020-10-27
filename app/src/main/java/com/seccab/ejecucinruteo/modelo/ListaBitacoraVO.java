package com.seccab.ejecucinruteo.modelo;

public class ListaBitacoraVO {

    private String hora_bit;
    private String nom_cliente;
    private double mon_compra;
    private String mot_nocompra;

    public ListaBitacoraVO(String hora_bit, String nom_cliente, double mon_compra, String mot_nocompra) {
        this.hora_bit = hora_bit;
        this.nom_cliente = nom_cliente;
        this.mon_compra = mon_compra;
        this.mot_nocompra = mot_nocompra;
    }

    public ListaBitacoraVO() {

    }

    public String getHora_bit() {
        return hora_bit;
    }

    public void setHora_bit(String hora_bit) {
        this.hora_bit = hora_bit;
    }

    public String getNom_cliente() {
        return nom_cliente;
    }

    public void setNom_cliente(String nom_cliente) {
        this.nom_cliente = nom_cliente;
    }

    public double getMon_compra() {
        return mon_compra;
    }

    public void setMon_compra(double mon_compra) {
        this.mon_compra = mon_compra;
    }

    public String getMot_nocompra() {
        return mot_nocompra;
    }

    public void setMot_nocompra(String mot_nocompra) {
        this.mot_nocompra = mot_nocompra;
    }
}

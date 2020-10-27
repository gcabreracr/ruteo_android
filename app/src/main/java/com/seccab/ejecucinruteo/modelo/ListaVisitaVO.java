package com.seccab.ejecucinruteo.modelo;

public class ListaVisitaVO {

    private double secuencia;
    private String nom_cliente;
    private String direccion;

    public ListaVisitaVO(double secuencia, String nom_cliente, String direccion) {
        this.secuencia = secuencia;
        this.nom_cliente = nom_cliente;
        this.direccion = direccion;
    }

    public ListaVisitaVO() {

    }

    public double getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(double secuencia) {
        this.secuencia = secuencia;
    }

    public String getNom_cliente() {
        return nom_cliente;
    }

    public void setNom_cliente(String nom_cliente) {
        this.nom_cliente = nom_cliente;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}

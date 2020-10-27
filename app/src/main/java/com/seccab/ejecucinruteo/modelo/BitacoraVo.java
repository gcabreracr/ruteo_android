package com.seccab.ejecucinruteo.modelo;

import java.sql.Date;
import java.sql.Time;

public class BitacoraVo {

    private int cod_cliente;
    private int cod_agencia;
    private int cod_pdv;
    private String cod_vendedor;
    private double mon_compra;
    private String mot_nocompra;
    private int sharekolbi;
    private int sharemovistar;
    private String notas;



    public BitacoraVo() {

    }


    public int getCod_cliente() {
        return cod_cliente;
    }

    public void setCod_cliente(int cod_cliente) {
        this.cod_cliente = cod_cliente;
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

    public String getCod_vendedor() {
        return cod_vendedor;
    }

    public void setCod_vendedor(String cod_vendedor) {
        this.cod_vendedor = cod_vendedor;
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

    public void setMot_nocompra(String motivo_compra) {
        this.mot_nocompra = motivo_compra;
    }

    public int getSharekolbi() {
        return sharekolbi;
    }

    public void setSharekolbi(int sharekolbi) {
        this.sharekolbi = sharekolbi;
    }

    public int getSharemovistar() {
        return sharemovistar;
    }

    public void setSharemovistar(int sharemovistar) {
        this.sharemovistar = sharemovistar;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}

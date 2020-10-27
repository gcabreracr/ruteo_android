package com.seccab.ejecucinruteo.modelo;

public class ClienteListaVo {
    private int codcli;
    private String nomcli;
    private String direccion;

    public ClienteListaVo(int codcli, String nomcli, String direccion) {
        this.codcli = codcli;
        this.nomcli = nomcli;
        this.direccion = direccion;
    }

    public ClienteListaVo() {

    }

    public int getCodcli() {
        return codcli;
    }

    public void setCodcli(int codcli) {
        this.codcli = codcli;
    }

    public String getNomcli() {
        return nomcli;
    }

    public void setNomcli(String nomcli) {
        this.nomcli = nomcli;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}

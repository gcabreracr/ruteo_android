package com.seccab.ejecucinruteo.modelo;

public class UsuarioVO {
    private String CodUsu;
    private String NomUsu;
    private String passUsu;
    private int tipoUsu;
    private int codAgencia;
    private String nomAgencia;
    private int codPdv;
    private String nompdv;
    private String codBodega;
    private int statuUsu;

    public void UsuarioVo(){

    }

    public String getNomAgencia() {
        return nomAgencia;
    }

    public String getNompdv() {
        return nompdv;
    }

    public void setNompdv(String nompdv) {
        this.nompdv = nompdv;
    }

    public void setNomAgencia(String nomAgencia) {
        this.nomAgencia = nomAgencia;
    }

    public String getCodUsu() {
        return CodUsu;
    }

    public void setCodUsu(String mCodUsu) {
        this.CodUsu = mCodUsu;
    }

    public String getNomUsu() {
        return NomUsu;
    }

    public void setNomUsu(String mNomUsu) {
        this.NomUsu = mNomUsu;
    }

    public String getPassUsu() {
        return passUsu;
    }

    public void setPassUsu(String passUsu) {
        this.passUsu = passUsu;
    }

    public int getTipoUsu() {
        return tipoUsu;
    }

    public void setTipoUsu(int tipoUsu) {
        this.tipoUsu = tipoUsu;
    }

    public int getCodAgencia() {
        return codAgencia;
    }

    public void setCodAgencia(int codAgencia) {
        this.codAgencia = codAgencia;
    }

    public int getCodPdv() {
        return codPdv;
    }

    public void setCodPdv(int codPdv) {
        this.codPdv = codPdv;
    }

    public String getCodBodega() {
        return codBodega;
    }

    public void setCodBodega(String codBodega) {
        this.codBodega = codBodega;
    }

    public int getStatuUsu() {
        return statuUsu;
    }

    public void setStatuUsu(int statuUsu) {
        this.statuUsu = statuUsu;
    }
}

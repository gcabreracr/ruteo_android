package com.seccab.ejecucinruteo.modelo;

public class Variables {

    public static String NOMBRE_USUARIO;
    public static String CODIGO_USUARIO;
    public static String DB_USUARIO;
    public static int COD_PDV;
    public static String NOMBRE_PDV;
    public static int COD_AGENCIA;
    public static String NOMBRE_AGENCIA;
    public static String COD_BODEGA;
    public static int CAT_PDV;
    public static int TIPO_USU;
    public static int DESCTO_AUT_USUARIO;

    public static double AVISO_MONTO_VENTA;



    //Variables para base datos temporal de facturas temporal
    public static final String TABLA_FACTURA="factura";
    public static final String TF_CODIGO="codigo";
    public static final String TF_NOMBRE="nombre";
    public static final String TF_CANTIDAD="cantidad";
    public static final String TF_MONTO="monto";
    public static final String TF_PRECIO="precio";
    public static final String TF_POR_DESCTO="pordescto";
    public static final String TF_MON_DESCTO="mondescto";
    public static final String TF_POR_IMPTO="porimpto";
    public static final String TF_MON_IMPTO="monimpto";
    public static final String TF_EXENTO="exento";

    public static final String CREAR_TABLA_FACTURA="CREATE TABLE "+TABLA_FACTURA+" ("+TF_CODIGO+" INTEGER,"
                        +TF_NOMBRE+" TEXT,"+TF_CANTIDAD+" INTEGER,"+TF_PRECIO+" DOUBLE,"+TF_MONTO+" DOUBLE,"+TF_POR_DESCTO+" INTEGER,"
                        +TF_MON_DESCTO+" DOUBLE,"+TF_MON_IMPTO+" DOUBLE,"+TF_POR_IMPTO+" INTEGER,"+TF_EXENTO+" INTEGER)";



}

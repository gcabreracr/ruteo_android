package com.seccab.ejecucinruteo.metodos;

public class Redondear {

    public static double decimal (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}

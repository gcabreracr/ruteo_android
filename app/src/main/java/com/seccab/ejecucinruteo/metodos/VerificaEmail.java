package com.seccab.ejecucinruteo.metodos;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerificaEmail {

    public static boolean verificar(String email) {
        Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher mather = pattern.matcher(email);

        return mather.find();
    }
}

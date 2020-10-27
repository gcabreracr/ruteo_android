package com.seccab.ejecucinruteo.metodos;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Internet {

    /**
     * Función para comprobar si hay conexión a Internet
     * @param context
     * @return boolean
     */

    public static boolean compruebaConexion(Context context) {

        boolean connected = false;

        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Recupera todas las redes (tanto móviles como wifi)
        NetworkInfo networkInfo = connec.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            connected=true;
        } else {
            connected=false;
        }

        return connected;
    }

}

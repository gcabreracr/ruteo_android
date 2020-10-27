package com.seccab.ejecucinruteo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.seccab.ejecucinruteo.metodos.VolleySingleton;
import com.seccab.ejecucinruteo.modelo.Variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CambiaPin extends AppCompatActivity {

    Button btnCambiaPin;
    EditText edPinActual, edPinNuevo, edPinNuevo2;
    TextView tvNomUsuario;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambia_pin);

        btnCambiaPin = findViewById(R.id.cp_btnCambiar);
        edPinActual = findViewById(R.id.cp_PinActual);
        edPinNuevo = findViewById(R.id.cp_PinNuevo);
        edPinNuevo2 = findViewById(R.id.cp_PinNuevo2);
        tvNomUsuario = findViewById(R.id.cp_nomUsuario);
        tvNomUsuario.setText(Variables.NOMBRE_USUARIO);

    }

    public void cp_btnCambiar(View view) {
        String pinNuevo = edPinNuevo.getText().toString();
        String pinNuevo2 = edPinNuevo2.getText().toString();


        if (pinNuevo.equals(pinNuevo2)) {

            String ip = getString(R.string.ip);
            String url = ip + "/ejecucionpdv/wsLoginUsuario.php?id_usuario='" + Variables.CODIGO_USUARIO + "'";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    JSONArray json = response.optJSONArray("usuario");
                    JSONObject jsonObject = null;
                    String jsonPass = null;
                    try {
                        jsonObject = json.getJSONObject(0);
                        jsonPass = jsonObject.optString("pass_usu").trim();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (edPinActual.getText().toString().trim().equals(jsonPass)) {

                       cambiarpin();

                    } else {
                        Toast.makeText(CambiaPin.this, "PIN actual es incorrecto", Toast.LENGTH_SHORT).show();
                        edPinActual.requestFocus();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(CambiaPin.this, "Error en respuesta del servidor", Toast.LENGTH_SHORT).show();
                    Log.d("Error: ", error.toString());
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
        } else {
            Toast.makeText(this, "Los campos del nuevo PIN no coinciden", Toast.LENGTH_SHORT).show();
        }

    }

    private void cambiarpin() {

        String ip = getString(R.string.ip);
        String url = ip + "/ejecucionpdv/wsCambiarPin.php";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id_usuario", Variables.CODIGO_USUARIO);
            jsonObject.put("nuevopin", edPinNuevo.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


               String resultado = response.optString("resultado");

                Toast.makeText(CambiaPin.this, "Pin " + resultado, Toast.LENGTH_SHORT).show();
                edPinActual.setText("");
                edPinNuevo.setText("");
                edPinNuevo2.setText("");
                edPinActual.requestFocus();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(CambiaPin.this, "Error en respuesta del servidor", Toast.LENGTH_SHORT).show();
                Log.d("Error: ", error.toString());

            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
}

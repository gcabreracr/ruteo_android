package com.seccab.ejecucinruteo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.seccab.ejecucinruteo.adaptadores.AdapterConsultaBitacora;
import com.seccab.ejecucinruteo.metodos.VolleySingleton;
import com.seccab.ejecucinruteo.modelo.ListaBitacoraVO;
import com.seccab.ejecucinruteo.modelo.Variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ConsultaBitacoraDiaria extends AppCompatActivity {

    AdapterConsultaBitacora adapterConsultaBitacora;
    ArrayList<ListaBitacoraVO> arrayListBitacora;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    ListaBitacoraVO listaBitacoraVO;
    TextView txtFechaBit,txtEfectividad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_bitacora);

        txtFechaBit=findViewById(R.id.cbd_fecha);
        txtEfectividad=findViewById(R.id.cbd_efectividad);
        progressBar = findViewById(R.id.cbd_progressBar);
        recyclerView = findViewById(R.id.cbd_RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        llenarArrayBitacora();

    }

    private void llenarArrayBitacora() {
        progressBar.setVisibility(View.VISIBLE);
        arrayListBitacora = new ArrayList<ListaBitacoraVO>();

        String ip = getString(R.string.ip);
        String url = ip + "/ejecucionpdv/wsListarBitacoraDiaria.php";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cod_agencia", Variables.COD_AGENCIA);
            jsonObject.put("cod_ruta", Variables.COD_PDV);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                SimpleDateFormat formatoDMY=new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat formatoYMD=new SimpleDateFormat("yyyy-MM-dd");
                Date fechaJson=null;
                try {
                    fechaJson=formatoYMD.parse(response.optString("fecha_bit"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                txtFechaBit.setText("Fecha: "+formatoDMY.format(fechaJson));
                String porcentaje=response.optString("porcentaje");
                String marcados=response.optString("marcados");
                String visita_dia=response.optString("visita_dia");
                txtEfectividad.setText("Efectividad: "+porcentaje+"% ("+marcados+" de "+visita_dia+")");

                JSONArray jsonBit = response.optJSONArray("bit_dia");
                JSONObject jsonObjectBit;
                try {
                    for (int i = 0; i < jsonBit.length(); i++) {
                        listaBitacoraVO = new ListaBitacoraVO();
                        jsonObjectBit = jsonBit.optJSONObject(i);
                        listaBitacoraVO.setHora_bit(jsonObjectBit.optString("hora_bit"));
                        listaBitacoraVO.setNom_cliente(jsonObjectBit.optString("nom_cliente"));
                        listaBitacoraVO.setMon_compra(jsonObjectBit.optDouble("mon_compra"));
                        listaBitacoraVO.setMot_nocompra(jsonObjectBit.optString("mot_nocompra"));
                        arrayListBitacora.add(listaBitacoraVO);
                    }

                    construirRecycler();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ConsultaBitacoraDiaria.this, "No hay registros", Toast.LENGTH_SHORT).show();
                }

                //construiRecyclerView();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);

                Toast.makeText(ConsultaBitacoraDiaria.this, "No hay registros en ese dia", Toast.LENGTH_SHORT).show();
                Log.d("ERROR:", error.toString());

            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

    }

    private void construirRecycler() {

        adapterConsultaBitacora = new AdapterConsultaBitacora(arrayListBitacora);
        recyclerView.setAdapter(adapterConsultaBitacora);

    }
}

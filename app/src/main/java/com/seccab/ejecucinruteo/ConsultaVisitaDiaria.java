package com.seccab.ejecucinruteo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.seccab.ejecucinruteo.adaptadores.AdapterListaFacturas;
import com.seccab.ejecucinruteo.adaptadores.AdapterListaVisita;
import com.seccab.ejecucinruteo.metodos.VolleySingleton;
import com.seccab.ejecucinruteo.modelo.ListaFacturaVO;
import com.seccab.ejecucinruteo.modelo.ListaVisitaVO;
import com.seccab.ejecucinruteo.modelo.Variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ConsultaVisitaDiaria extends AppCompatActivity {

    Spinner spDias;
    RecyclerView recyclerView;
    String dia_vis="vis_lun";
    AdapterListaVisita adapterListaVisita;
    ArrayList<ListaVisitaVO> arrayListVisita;
    ProgressBar progressBar;
    ListaVisitaVO listaVisitaVO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_visita);

        progressBar=findViewById(R.id.cvd_progressBar);
        spDias=findViewById(R.id.cvd_SpinnerDias);
        recyclerView=findViewById(R.id.cvd_RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.diasvisita, R.layout.texto_spinner);
        spDias.setAdapter(arrayAdapter);
        spDias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position){
                    case 0:
                        dia_vis="vis_lun";
                        break;
                    case 1:
                        dia_vis="vis_mar";
                        break;
                    case 2:
                        dia_vis="vis_mie";
                        break;
                    case 3:
                        dia_vis="vis_jue";
                        break;
                    case 4:
                        dia_vis="vis_vie";
                        break;
                    case 5:
                        dia_vis="vis_sab";
                        break;
                    case 6:
                        dia_vis="vis_dom";
                        break;
                }

                llenarArrayConsulta();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

       // llenarArrayConsulta();

    }

    private void llenarArrayConsulta() {
        progressBar.setVisibility(View.VISIBLE);

        arrayListVisita=new ArrayList<ListaVisitaVO>();

        String ip = getString(R.string.ip);
        String url = ip + "/ejecucionpdv/wsListarClientesVisita.php";
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("cod_agencia",Variables.COD_AGENCIA);
            jsonObject.put("cod_ruta",Variables.COD_PDV);
            jsonObject.put("dia_vis",dia_vis);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);

                    JSONObject jsonObject = null;

                    JSONArray jsonResp = response.optJSONArray("cli_vis");

                    try {
                        for (int i = 0; i < jsonResp.length(); i++) {
                            listaVisitaVO = new ListaVisitaVO();
                            jsonObject = jsonResp.optJSONObject(i);
                            listaVisitaVO.setSecuencia(jsonObject.optDouble("secuencia"));
                            listaVisitaVO.setNom_cliente(jsonObject.optString("nom_cliente"));
                            listaVisitaVO.setDireccion(jsonObject.optString("direccion"));
                            arrayListVisita.add(listaVisitaVO);
                        }

                        // construiRecyclerView();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ConsultaVisitaDiaria.this, "No hay registros", Toast.LENGTH_SHORT).show();
                    }

                //construiRecyclerView();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);

                Toast.makeText(ConsultaVisitaDiaria.this, "No hay registros en ese dia", Toast.LENGTH_SHORT).show();
                Log.d("ERROR:", error.toString());
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

       construiRecyclerView();
    }

    private void construiRecyclerView() {

       adapterListaVisita=new AdapterListaVisita(arrayListVisita);
       recyclerView.setAdapter(adapterListaVisita);

    }
}

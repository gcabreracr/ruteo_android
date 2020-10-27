package com.seccab.ejecucinruteo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.seccab.ejecucinruteo.metodos.VolleySingleton;
import com.seccab.ejecucinruteo.modelo.AgenciaVo;
import com.seccab.ejecucinruteo.modelo.PdvVO;
import com.seccab.ejecucinruteo.modelo.Variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CambiaPDV extends AppCompatActivity {

    TextView tvNom_Usuario;
    Spinner spAgencias, spPDVs;
    ProgressBar progressBar;
    ArrayList<String> listaAgencias;
    ArrayList<AgenciaVo> arrayListAgenciasVO;
    ArrayList<String> listaPdvs;
    ArrayList<PdvVO> arrayListPdvVO;
    AgenciaVo agenciaVo;
    PdvVO pdvVO;

    int codAgencia, codPDV;
    String nomAgencia, nomPDV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambia_pdv);

        progressBar = findViewById(R.id.cpdv_progressBar);

        tvNom_Usuario = findViewById(R.id.cpdv_usuario);
        tvNom_Usuario.setText("Usuario: " + Variables.NOMBRE_USUARIO);
        spAgencias = findViewById(R.id.cpdv_spAgencias);
        spPDVs = findViewById(R.id.cpdv_spPDV);
        spAgencias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                codAgencia = arrayListAgenciasVO.get(position).getCod_agencia();
                nomAgencia = arrayListAgenciasVO.get(position).getNom_agencia();
                llenarArrayPDV();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spPDVs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                codPDV = arrayListPdvVO.get(position).getCod_pdv();
                nomPDV = arrayListPdvVO.get(position).getNom_pdv();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        llenarArrayAgencias();

    }

    private void llenarArrayPDV() {
        progressBar.setVisibility(View.VISIBLE);
        listaPdvs = new ArrayList<String>();
        arrayListPdvVO = new ArrayList<PdvVO>();


        String ip = getString(R.string.ip);
        String url = ip + "/ejecucionpdv/wsListarPdvxAgencia.php?cod_agencia=" + codAgencia;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);

                JSONObject jsonObject = null;
                JSONArray jsonResp = response.optJSONArray("pdv_agencia");

                try {
                    for (int i = 0; i < jsonResp.length(); i++) {
                        pdvVO = new PdvVO();
                        jsonObject = jsonResp.optJSONObject(i);
                        // pdvVO.setCod_agencia(jsonObject.optInt("cod_agencia"));
                        pdvVO.setCod_pdv(jsonObject.optInt("cod_pdv"));
                        pdvVO.setNom_pdv(jsonObject.optString("nom_pdv"));
                        arrayListPdvVO.add(pdvVO);
                    }


                    for (int i = 0; i < arrayListPdvVO.size(); i++) {
                        listaPdvs.add(arrayListPdvVO.get(i).getNom_pdv());
                    }
                    //construirSpinerPDV();


                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(CambiaPDV.this, "No hay Puntos de Venta en esta Agencia", Toast.LENGTH_SHORT).show();
                }
                construirSpinerPDV();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);

                Toast.makeText(CambiaPDV.this, "Error respuesta servidor", Toast.LENGTH_SHORT).show();
                Log.d("ERROR:", error.toString());

            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

        // construirSpinerPDV();

    }

    private void construirSpinerPDV() {

        ArrayAdapter<CharSequence> arrayAdapterPdv = new ArrayAdapter(this, R.layout.texto_spinner, listaPdvs);
        spPDVs.setAdapter(arrayAdapterPdv);

    }

    private void llenarArrayAgencias() {
        progressBar.setVisibility(View.VISIBLE);
        listaAgencias = new ArrayList<String>();
        arrayListAgenciasVO = new ArrayList<AgenciaVo>();

        String ip = getString(R.string.ip);
        String url = ip + "/ejecucionpdv/wsListarAgencias.php";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);

                JSONObject jsonObject = null;
                JSONArray jsonResp = response.optJSONArray("agencias");

                try {
                    for (int i = 0; i < jsonResp.length(); i++) {
                        agenciaVo = new AgenciaVo();
                        jsonObject = jsonResp.optJSONObject(i);
                        agenciaVo.setCod_agencia(jsonObject.optInt("cod_agencia"));
                        agenciaVo.setNom_agencia(jsonObject.optString("nom_agencia"));
                        arrayListAgenciasVO.add(agenciaVo);
                    }


                    for (int i = 0; i < arrayListAgenciasVO.size(); i++) {
                        listaAgencias.add(arrayListAgenciasVO.get(i).getNom_agencia());
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(CambiaPDV.this, "No hay registros", Toast.LENGTH_SHORT).show();
                }

                construirSpinerAgencias();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);

                Toast.makeText(CambiaPDV.this, "No hay registros", Toast.LENGTH_SHORT).show();
                Log.d("ERROR:", error.toString());

            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

    }

    private void construirSpinerAgencias() {

        ArrayAdapter<CharSequence> arrayAdapterAgencias = new ArrayAdapter(this, R.layout.texto_spinner, listaAgencias);
        spAgencias.setAdapter(arrayAdapterAgencias);

    }


    public void cpdv_btnCambiar(View view) {

        if (Variables.TIPO_USU > 1) {

           if (Variables.TIPO_USU==4){
               cambiapdv();
           }else if(Variables.TIPO_USU<4 && Variables.COD_AGENCIA==codAgencia){
               cambiapdv();
           }else {
               Toast.makeText(this, "Usuario NO autorizado en esta Agencia", Toast.LENGTH_SHORT).show();
           }

        } else {
            String ip = getString(R.string.ip);
            String url = ip + "/ejecucionpdv/wsConsultaPdvUsuario.php";
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cod_usuario", Variables.CODIGO_USUARIO);
                jsonObject.put("cod_agencia", codAgencia);
                jsonObject.put("cod_pdv", codPDV);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        int resp = response.getInt("resultado");
                        if (resp == 1) {
                            cambiapdv();
                        } else {
                            Toast.makeText(CambiaPDV.this, "Usuario NO autorizado en este PDV", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(CambiaPDV.this, "Error en respuesta del servidor", Toast.LENGTH_SHORT).show();
                    Log.d("Error: ", error.toString());
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

        }

    }

    private void cambiapdv() {

        SharedPreferences sharedPreferences = getSharedPreferences("userpref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user", Variables.CODIGO_USUARIO);
        editor.putString("nomuser", Variables.NOMBRE_USUARIO);
        editor.putInt("codagencia", codAgencia);
        editor.putString("nomagencia", nomAgencia);
        editor.putInt("codpdv", codPDV);
        editor.putString("nompdv", nomPDV);
        editor.putString("bodega", Variables.COD_BODEGA);
        editor.putInt("catpdv", Variables.CAT_PDV);
        editor.putInt("tipousu", Variables.TIPO_USU);
        editor.commit();
        Toast.makeText(this, "Cambio PDV satisfactorio", Toast.LENGTH_SHORT).show();
        finish();

    }
}

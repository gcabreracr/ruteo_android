package com.seccab.ejecucinruteo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.seccab.ejecucinruteo.adaptadores.AdapterVentaDiaria;
import com.seccab.ejecucinruteo.metodos.VolleySingleton;
import com.seccab.ejecucinruteo.modelo.Variables;
import com.seccab.ejecucinruteo.modelo.VentaDiariaVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class StatuLiquida extends AppCompatActivity {

    Button btnImprimir, btnVerFacturas;
    AdapterVentaDiaria adapterVentaDiaria;
    VentaDiariaVO ventaDiariaVO;
    ArrayList<VentaDiariaVO> arrayListVentaDiaria;
    RecyclerView recyclerVentaDiaria;
    ProgressBar progressBar;
    DecimalFormat formatoDecimal= new DecimalFormat("######,###.00");

    TextView tvTotLiq;
    double mTotalLiq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statu_liquida);

        mTotalLiq = 0;
        progressBar = findViewById(R.id.sl_progressBar);
        tvTotLiq=findViewById(R.id.sl_TotStaLiq);
        btnImprimir=findViewById(R.id.sl_btnImprimir);
        btnVerFacturas =findViewById(R.id.sl_btnVerFacturas);
        recyclerVentaDiaria = findViewById(R.id.sl_RecyclerView);
        recyclerVentaDiaria.setLayoutManager(new LinearLayoutManager(this));

        llenarArrayVentaDiaria();
    }

    private void llenarArrayVentaDiaria() {
        progressBar.setVisibility(View.VISIBLE);

        arrayListVentaDiaria = new ArrayList<>();
        mTotalLiq = 0;

        String ip = getString(R.string.ip);
        String url = ip + "/ejecucionpdv/wsVentaDiaria.php?cod_agencia=" + Variables.COD_AGENCIA + "&cod_pdv=" + Variables.COD_PDV;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);

                arrayListVentaDiaria = new ArrayList<>();
                mTotalLiq=0;
                JSONArray json = response.optJSONArray("ventadiaria");

                if (json != null) {
                    JSONObject jsonObject;
                    try {
                        for (int i = 0; i < json.length(); i++) {
                            ventaDiariaVO = new VentaDiariaVO();
                            jsonObject = json.getJSONObject(i);
                            ventaDiariaVO.setNomArticulo(jsonObject.optString("nom_cor_art"));
                            ventaDiariaVO.setCant_venta(jsonObject.optInt("cant_venta"));
                            ventaDiariaVO.setCarga(jsonObject.optInt("carga"));
                            ventaDiariaVO.setMonto_liq(jsonObject.optDouble("monto_liq"));
                            arrayListVentaDiaria.add(ventaDiariaVO);
                            mTotalLiq = mTotalLiq + ventaDiariaVO.getMonto_liq();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(getApplicationContext(), "No hay registros de venta del día", Toast.LENGTH_SHORT).show();
                }
                tvTotLiq.setText("Total: "+formatoDecimal.format(mTotalLiq));
                construirRecyclerView();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Log.d("ERROR: ", error.toString());
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);


    }

    private void construirRecyclerView() {

        adapterVentaDiaria = new AdapterVentaDiaria(arrayListVentaDiaria);
        recyclerVentaDiaria.setAdapter(adapterVentaDiaria);
    }


    public void sl_btnImprmirOnClik(View view) {

        Toast.makeText(getApplicationContext(),"Opcion NO disponible",Toast.LENGTH_SHORT).show();
    }

    public void sl_btnVerFacturasOnClik(View view) {

        Intent intent=new Intent(StatuLiquida.this,ListadoFacturas.class);
        startActivity(intent);
    }
}

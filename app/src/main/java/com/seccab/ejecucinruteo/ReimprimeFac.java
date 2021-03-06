package com.seccab.ejecucinruteo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.seccab.ejecucinruteo.adaptadores.AdapterListaFacturas;
import com.seccab.ejecucinruteo.metodos.VolleySingleton;
import com.seccab.ejecucinruteo.modelo.ListaFacturaVO;
import com.seccab.ejecucinruteo.modelo.Variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReimprimeFac extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<ListaFacturaVO> arrayList;
    ProgressBar progressBar;
    ListaFacturaVO listaFacturaVO;
    AdapterListaFacturas adapterListaFacturas;
    private static final int IMP_FACTURA=3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reimprime_fac);

        progressBar = findViewById(R.id.rf_progressBar);
        recyclerView = findViewById(R.id.rf_RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        llenarListaFacturas();
        adapterListaFacturas = new AdapterListaFacturas(arrayList);
        adapterListaFacturas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numFac=arrayList.get(recyclerView.getChildAdapterPosition(v)).getNumFactura();

                Intent intent = new Intent(ReimprimeFac.this, ImprimeFactura.class);
                intent.putExtra("numfactura", numFac);
                startActivityForResult(intent, IMP_FACTURA);

            }
        });
        recyclerView.setAdapter(adapterListaFacturas);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void llenarListaFacturas() {
        progressBar.setVisibility(View.VISIBLE);
        arrayList = new ArrayList<>();

        String ip = getString(R.string.ip);
        String url = ip + "/ejecucionpdv/wsListarFacturas.php?cod_agencia=" + Variables.COD_AGENCIA + "&cod_pdv=" + Variables.COD_PDV;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);

                JSONObject jsonObject = null;
                JSONArray jsonResp = null;
                jsonResp = response.optJSONArray("facturas");

                try {
                    for (int i = 0; i < jsonResp.length(); i++) {
                        listaFacturaVO = new ListaFacturaVO();
                        jsonObject = jsonResp.optJSONObject(i);
                        listaFacturaVO.setNumFactura(jsonObject.optInt("num_factura"));
                        listaFacturaVO.setNomNegocio(jsonObject.optString("nom_cliente"));
                        listaFacturaVO.setMonFactura(jsonObject.optDouble("netofactura"));
                        arrayList.add(listaFacturaVO);
                    }
                   // adapterListaFacturas = new AdapterListaFacturas(arrayList);
                   // recyclerView.setAdapter(adapterListaFacturas);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ReimprimeFac.this, "No hay facturas registradas", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ReimprimeFac.this, "Error Volley " + error.toString(), Toast.LENGTH_SHORT).show();
                Log.d("ERROR:", error.toString());

            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
}

package com.seccab.ejecucinruteo;

import android.content.Intent;
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
import com.seccab.ejecucinruteo.adaptadores.AdapterListaFacturas;
import com.seccab.ejecucinruteo.metodos.VolleySingleton;
import com.seccab.ejecucinruteo.modelo.ListaFacturaVO;
import com.seccab.ejecucinruteo.modelo.Variables;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class ListadoFacturas extends AppCompatActivity {

    RecyclerView recyclerViewListaFac;
    AdapterListaFacturas adapterListaFacturas;
    ArrayList<ListaFacturaVO> arrayListFacturas;
    DecimalFormat formatoInt3=new DecimalFormat("###");
    ListaFacturaVO listaFacturaVO;
    ProgressBar progressBar;
    TextView tvCantFac;
    private static final int IMP_FACTURA=3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_facturas);


        tvCantFac=findViewById(R.id.rf_tvCantFac);
        tvCantFac.setText(formatoInt3.format(0));
        progressBar = findViewById(R.id.lf_progressBar);
        recyclerViewListaFac = findViewById(R.id.lf_RecyclerView);
        recyclerViewListaFac.setLayoutManager(new LinearLayoutManager(this));
        construirRecycler();
        //llenarListaFacturas();
        //adapterListaFacturas=new AdapterListaFacturas(arrayListFacturas);
        //recyclerViewListaFac.setAdapter(adapterListaFacturas);

    }

    private void construirRecycler() {

        llenarListaFacturas();
        adapterListaFacturas=new AdapterListaFacturas(arrayListFacturas);
        adapterListaFacturas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numFac=arrayListFacturas.get(recyclerViewListaFac.getChildAdapterPosition(v)).getNumFactura();

                Intent intent = new Intent(ListadoFacturas.this, ImprimeFactura.class);
                intent.putExtra("numfactura", numFac);
                startActivityForResult(intent, IMP_FACTURA);
            }
        });
        recyclerViewListaFac.setAdapter(adapterListaFacturas);
    }


    private void llenarListaFacturas() {
        progressBar.setVisibility(View.VISIBLE);
        arrayListFacturas = new ArrayList<>();

        String ip = getString(R.string.ip);
        String url = ip + "/ejecucionpdv/wsListarFacturas.php?cod_agencia=" + Variables.COD_AGENCIA + "&cod_pdv=" + Variables.COD_PDV;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);

                int cantfacturas=response.optInt("cantfac");
                tvCantFac.setText("Cantidad documentos: "+cantfacturas);

                JSONArray jArrayResp = response.optJSONArray("facturas");
                JSONObject jsonObj = null;
                try {
                    for (int i = 0; i < jArrayResp.length(); i++) {
                        listaFacturaVO = new ListaFacturaVO();
                        jsonObj = jArrayResp.optJSONObject(i);
                        listaFacturaVO.setNumFactura(jsonObj.optInt("num_factura"));
                        listaFacturaVO.setNomNegocio(jsonObj.optString("nom_cliente"));
                        listaFacturaVO.setMonFactura(jsonObj.optDouble("netofactura"));
                        arrayListFacturas.add(listaFacturaVO);
                    }
                 //   adapterListaFacturas = new AdapterListaFacturas(arrayListFacturas);
                  //  recyclerViewListaFac.setAdapter(adapterListaFacturas);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ListadoFacturas.this, "No hay facturas registradas", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ListadoFacturas.this, "Error Volley " + error.toString(), Toast.LENGTH_SHORT).show();
                Log.d("ERROR:", error.toString());

            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

    }
}

package com.seccab.ejecucinruteo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.seccab.ejecucinruteo.adaptadores.AdapterClientes;
import com.seccab.ejecucinruteo.metodos.VolleySingleton;
import com.seccab.ejecucinruteo.modelo.ClienteListaVo;
import com.seccab.ejecucinruteo.modelo.Variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SeleccionaCliente extends AppCompatActivity
        implements Response.Listener<JSONObject>, Response.ErrorListener {

    AdapterClientes adapterClientes;
    ArrayList<ClienteListaVo> arraylistaClientes;
    RecyclerView recyclerViewClientes;
    JsonObjectRequest jsonObjectRequest;
    StringRequest stringRequest;
    ProgressBar progressBar;

    Button btnSelecciona, btnCancela;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecciona_cliente);

        progressBar = findViewById(R.id.sc_progressBar);

        recyclerViewClientes = findViewById(R.id.sc_recyclerClientes);
        recyclerViewClientes.setLayoutManager(new LinearLayoutManager(this));

        llenarArrayClientes();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_seleciona_cliente, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapterClientes.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }


    private void construirRecycler() {

        adapterClientes = new AdapterClientes(arraylistaClientes);
        adapterClientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int codcliente = arraylistaClientes.get(recyclerViewClientes.getChildAdapterPosition(v)).getCodcli();
                String nomcliente = arraylistaClientes.get(recyclerViewClientes.getChildAdapterPosition(v)).getNomcli();
                Intent i = getIntent();
                i.putExtra("CODCLI", codcliente);
                i.putExtra("NOMCLI", nomcliente);
                setResult(RESULT_OK, i);
                finish();

            }
        });
        recyclerViewClientes.setAdapter(adapterClientes);

    }

    private void llenarArrayClientes() {


        progressBar.setVisibility(View.VISIBLE);

        String ip = getString(R.string.ip);
        String url = ip + "/ejecucionpdv/wsListarCLientes.php?cod_agencia=" + Variables.COD_AGENCIA + "&cod_ruta=" + Variables.COD_PDV;

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        progressBar.setVisibility(View.GONE);

        Toast.makeText(getApplicationContext(), "Error de conexion con servidor " + error.toString(), Toast.LENGTH_LONG).show();
        Log.d("ERROR: ", error.toString());
    }

    @Override
    public void onResponse(JSONObject response) {
        progressBar.setVisibility(View.GONE);


        //JSONArray json = response.optJSONArray("cliente");
        JSONArray json = null;
        try {
            json = response.getJSONArray("cliente");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        arraylistaClientes = new ArrayList<>();
        try {
            ClienteListaVo clienteListaVo = null;

            for (int i = 0; i < json.length(); i++) {
                clienteListaVo = new ClienteListaVo();
                JSONObject jsonObject = null;
                jsonObject = json.getJSONObject(i);
                clienteListaVo.setCodcli(jsonObject.getInt("cod_cliente"));
                clienteListaVo.setNomcli(jsonObject.getString("nom_cliente"));
                clienteListaVo.setDireccion(jsonObject.getString("direccion"));
                arraylistaClientes.add(clienteListaVo);

            }

            construirRecycler();

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "PDV " + Variables.NOMBRE_PDV + " no tiene registros", Toast.LENGTH_SHORT).show();

        }

    }
}


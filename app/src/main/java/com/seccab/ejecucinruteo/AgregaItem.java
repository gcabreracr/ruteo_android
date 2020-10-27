package com.seccab.ejecucinruteo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import com.seccab.ejecucinruteo.adaptadores.AdapterAgregaItems;
import com.seccab.ejecucinruteo.dialogFragment.DialogoConfirmacion;
import com.seccab.ejecucinruteo.metodos.Redondear;
import com.seccab.ejecucinruteo.metodos.VolleySingleton;
import com.seccab.ejecucinruteo.modelo.ConexionSQLite;
import com.seccab.ejecucinruteo.modelo.ItemListaVo;
import com.seccab.ejecucinruteo.modelo.ItemVo;
import com.seccab.ejecucinruteo.modelo.Variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AgregaItem extends AppCompatActivity implements DialogoConfirmacion.DialogoConfirmacionListener {

    AdapterAgregaItems adapterAgregaItems;
    ArrayList<ItemListaVo> arrayListItemVo;
    ItemVo itemVo;
    ItemListaVo itemListaVo;
    TextView tvPorDescto, tvPorImpto;
    TextView tvNomItem, tvMonDescto, tvMonImpto, tvSubTotal, tvMonTotal;
    EditText txtCantidad, txtPorDescto;
    Button btnAgregar, btnCancelar;
    ProgressBar progressBar;

    DecimalFormat decimalFormat;
    int mPorDesctoCli, mPorDesctoArt, mPorImpto, mPorDescto;
    double iSubtotal, iMonDescto, iMonImpto, iMonTotal, iPrecio, iCantidad;

    ConexionSQLite conn;
    RecyclerView recyclerViewItems;
    JsonObjectRequest jsonObjectRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agrega_item);

        progressBar = findViewById(R.id.ai_progressBar);

        decimalFormat = new DecimalFormat("#####,###.00");

        mPorDesctoArt = 0;
        mPorImpto = 0;
        iPrecio = 0;
        iSubtotal = 0;
        iMonDescto = 0;
        iMonImpto = 0;

        //  searchViewItems = findViewById(R.id.ai_SearchView);

        tvNomItem = findViewById(R.id.ai_nomItem);
        txtPorDescto = findViewById(R.id.ai_edPorDescto);
        tvPorImpto = findViewById(R.id.ai_tvPorImpto);
        tvSubTotal = findViewById(R.id.ai_tvSubTotal);
        tvMonDescto = findViewById(R.id.ai_tvDescto);
        tvMonImpto = findViewById(R.id.ai_tvImpto);
        tvMonTotal = findViewById(R.id.ai_tvTotal);
        txtCantidad = findViewById(R.id.ai_edCantidad);
        txtCantidad.setEnabled(false);

        btnAgregar = findViewById(R.id.ai_btnAgregar);
        btnAgregar.setEnabled(false);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculaMontos();
                if (iCantidad == 0) {
                    Toast.makeText(AgregaItem.this, "No se puede facturar una cantidad en CERO", Toast.LENGTH_SHORT).show();
                    txtCantidad.requestFocus();
                    return;

                }

                if (iSubtotal > Variables.AVISO_MONTO_VENTA) {

                    DecimalFormat decimalFormat = new DecimalFormat("###,###,###.00");
                    String msgDialogo = "Esta registrando una venta por un monto de " + decimalFormat.format(iSubtotal) + ". Esta correcto el monto?";
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    DialogoConfirmacion dialogoConfirmacion = new DialogoConfirmacion();
                    dialogoConfirmacion.setMsg(msgDialogo);
                    dialogoConfirmacion.setTitulo("ATENCION");
                    dialogoConfirmacion.show(fragmentManager, "confirma");
                } else {
                    incluyeItem();
                }


            }
        });

        btnCancelar = findViewById(R.id.ai_btnCancelar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recibirExtras(); //Recibe datos pasados en el Intent
        calculaMontos(); //Calcula montos (subtotal, descto, impto y total

        //Definimos el recycler
        recyclerViewItems = findViewById(R.id.ai_RecyclerView);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));

        //Definir el evento Enter del teclado (action_done)
        txtCantidad.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    calculaMontos();
                    return true;
                }
                return false;
            }
        });
        txtPorDescto.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    calculaMontos();
                    return true;
                }

                return false;
            }
        });


        llenarArrayItems();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_busca_articulo, menu);

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
                adapterAgregaItems.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    private void incluyeItem() {

        conn = new ConexionSQLite(this, "bd_ruteo", null, 1);
        SQLiteDatabase db = conn.getWritableDatabase();
        ContentValues datosItem = new ContentValues();
        datosItem.put(Variables.TF_CODIGO, itemVo.getCoditem());
        datosItem.put(Variables.TF_NOMBRE, itemVo.getNomCorto());
        datosItem.put(Variables.TF_CANTIDAD, iCantidad);
        datosItem.put(Variables.TF_PRECIO, iPrecio);
        datosItem.put(Variables.TF_MONTO, iSubtotal);
        datosItem.put(Variables.TF_POR_DESCTO, mPorDescto);
        datosItem.put(Variables.TF_MON_DESCTO, iMonDescto);
        datosItem.put(Variables.TF_POR_IMPTO, mPorImpto);
        datosItem.put(Variables.TF_MON_IMPTO, iMonImpto);
        datosItem.put(Variables.TF_EXENTO, itemVo.getExento());

        db.insert(Variables.TABLA_FACTURA, null, datosItem);
        db.close();
        Intent i = getIntent();
        setResult(RESULT_OK, i);
        finish();

    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        incluyeItem();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        txtCantidad.requestFocus();
    }

    private void calculaMontos() {

        // Calcula montos

        try {
            iCantidad = Integer.parseInt(txtCantidad.getText().toString());
            // iCantidad = Double.parseDouble(txtCantidad.getText().toString());
            mPorDescto = Integer.parseInt(txtPorDescto.getText().toString());
        } catch (Exception e) {
            iCantidad = 0;
            mPorDescto = 0;
        }

        if (mPorDesctoArt > 0) {
            if (mPorDesctoCli > mPorDesctoArt) {
                if (mPorDescto > mPorDesctoCli) {
                    Toast.makeText(this, "NO se permite un descuento mayor al autorizado para el cliente", Toast.LENGTH_SHORT).show();
                    mPorDescto = mPorDesctoCli;
                    txtPorDescto.setText(String.valueOf(mPorDesctoCli));
                }

            } else {
                if (mPorDescto > mPorDesctoArt) {
                    Toast.makeText(this, "NO se permite un descuento mayor al auotirzado para el artículo", Toast.LENGTH_SHORT).show();
                    mPorDescto = mPorDesctoArt;
                    txtPorDescto.setText(String.valueOf(mPorDesctoArt));
                }
            }
        } else {
            if (mPorDescto > 0) {
                Toast.makeText(this, "El articulo NO permite descuentos", Toast.LENGTH_SHORT).show();
                mPorDescto = 0;
                txtPorDescto.setText(String.valueOf(0));
            }

        }


        // iSubtotal = iCantidad * iPrecio;
        // iMonDescto = iSubtotal * mPorDescto / 100;
        //iMonImpto = (iSubtotal - iMonDescto) * mPorImpto / 100;
        iSubtotal = Redondear.decimal((iCantidad * iPrecio), 0);
        iMonDescto = Redondear.decimal((iSubtotal * mPorDescto / 100), 0);
        iMonImpto = Redondear.decimal(((iSubtotal - iMonDescto) * mPorImpto / 100), 0);
        iMonTotal = iSubtotal - iMonDescto + iMonImpto;

        // Muestra los datos en los textview
        tvSubTotal.setText(decimalFormat.format(iSubtotal));
        String porImpto = String.valueOf(mPorImpto) + "%";
        // String porDescto = String.valueOf(mPorDescto) + "%";
        // tvPorDescto.setText(porDescto);
        tvMonDescto.setText(decimalFormat.format(iMonDescto));
        tvPorImpto.setText(porImpto);
        tvMonImpto.setText(decimalFormat.format(iMonImpto));
        tvMonTotal.setText(decimalFormat.format(iMonTotal));

    }

    private void recibirExtras() {
        mPorDesctoCli = getIntent().getIntExtra("desctocli", 0);
    }

    private void construirRecycler() {

        adapterAgregaItems = new AdapterAgregaItems(arrayListItemVo);
        recyclerViewItems.setAdapter(adapterAgregaItems);
        adapterAgregaItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemVo = new ItemVo();
                itemVo.setCoditem(arrayListItemVo.get(recyclerViewItems.getChildAdapterPosition(v)).getCodArt());
                itemVo.setNomCorto(arrayListItemVo.get(recyclerViewItems.getChildAdapterPosition(v)).getNomCortoArt());
                itemVo.setPrecio(arrayListItemVo.get(recyclerViewItems.getChildAdapterPosition(v)).getPrecioArt());
                itemVo.setMedida(arrayListItemVo.get(recyclerViewItems.getChildAdapterPosition(v)).getMedida());
                itemVo.setDesctoArt(arrayListItemVo.get(recyclerViewItems.getChildAdapterPosition(v)).getPorDescto());
                itemVo.setImptoArt(arrayListItemVo.get(recyclerViewItems.getChildAdapterPosition(v)).getPorImpto());
                itemVo.setExento(arrayListItemVo.get(recyclerViewItems.getChildAdapterPosition(v)).getExento());

                mPorDesctoArt = itemVo.getDesctoArt();
                mPorImpto = itemVo.getImptoArt();
                iPrecio = itemVo.getPrecio();
                tvNomItem.setText(itemVo.getNomCorto());

                if (mPorDesctoArt > 0) {
                    if (mPorDesctoCli > mPorDesctoArt) {
                        // mPorDescto = mPorDesctoCli;
                        txtPorDescto.setText(String.valueOf(mPorDesctoCli));
                    } else {
                        // mPorDescto = mPorDesctoArt;
                        txtPorDescto.setText(String.valueOf(mPorDesctoArt));
                    }
                } else {
                    mPorDescto = 0;
                    txtPorDescto.setText(String.valueOf(0));
                }

                calculaMontos();
                txtCantidad.setEnabled(true);
                txtPorDescto.setEnabled(true);
                btnAgregar.setEnabled(true);
                txtCantidad.requestFocus();

            }
        });

    }

    private void llenarArrayItems() {

        progressBar.setVisibility(View.VISIBLE);
        arrayListItemVo = new ArrayList<>();

        String ip = getString(R.string.ip);
        String url = ip + "/ejecucionpdv/wsListarItems.php?cat_pdv=" + Variables.CAT_PDV;

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                progressBar.setVisibility(View.GONE);
                arrayListItemVo = new ArrayList<>();
                JSONArray json = response.optJSONArray("items");
                JSONObject jsonObject = null;
                try {
                    for (int i = 0; i < json.length(); i++) {
                        itemListaVo = new ItemListaVo();
                        jsonObject = json.getJSONObject(i);
                        itemListaVo.setCodArt(jsonObject.optInt("codigo_art"));
                        itemListaVo.setNomCortoArt(jsonObject.optString("nom_cor_art"));
                        itemListaVo.setMedida(jsonObject.optString("uni_med"));
                        itemListaVo.setPrecioArt(jsonObject.optDouble("precio_art"));
                        itemListaVo.setPorDescto(jsonObject.optInt("descto_art"));
                        itemListaVo.setPorImpto(jsonObject.optInt("impto_art"));
                        itemListaVo.setExento(jsonObject.optInt("exento_art"));
                        arrayListItemVo.add(itemListaVo);

                    }

                    construirRecycler();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressBar.setVisibility(View.GONE);
                //Toast.makeText(getApplicationContext(), " No hay articulo de la categoria del Punto de Ventas", Toast.LENGTH_LONG).show();
                //System.out.println();
                Log.d("ERROR: ", error.toString());
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }


}

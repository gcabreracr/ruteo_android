package com.seccab.ejecucinruteo;

import android.Manifest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.seccab.ejecucinruteo.adaptadores.AdapterItemFac;
import com.seccab.ejecucinruteo.metodos.GeoLocalizacion;
import com.seccab.ejecucinruteo.metodos.Internet;
import com.seccab.ejecucinruteo.metodos.VerificaEmail;
import com.seccab.ejecucinruteo.metodos.VolleySingleton;
import com.seccab.ejecucinruteo.modelo.BitacoraVo;
import com.seccab.ejecucinruteo.modelo.ClienteVO;
import com.seccab.ejecucinruteo.modelo.ConexionSQLite;
import com.seccab.ejecucinruteo.modelo.ItemListaFacVo;
import com.seccab.ejecucinruteo.modelo.Variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class EmiteFactura extends AppCompatActivity {

    private static final int CLIENTES = 0;
    private static final int CONDICIONES = 1;
    private static final int ITEMS = 2;
    private static final int IMP_FAC = 3;


    Spinner spTipoId;
    EditText txtNotas, txtNomFactura, txtIdFactura, txtEmailFactura;
    TextView tvSubTotal, tvMonDescto, tvMonImpto, tvTotFactura, tvNomNegocio;
    Button btnBusCli, btnCondiciones, btnEnviaFac, btnCancelaFac, btnImprimeFac, btnAgregaItem;
    DecimalFormat formatodecimal;
    ProgressBar progressBar;

    ArrayList<ItemListaFacVo> arrayListItemFac;
    RecyclerView recyclerItemFac;
    ItemListaFacVo itemListaFacVo;
    ClienteVO clienteVO = new ClienteVO();
    BitacoraVo bitacoraVo;

    ConexionSQLite conn;
    JsonObjectRequest jsonObjectRequest;
    StringRequest stringRequest;
    LocationListener locationListener;
    LocationManager locationManager;
    GeoLocalizacion geoLocalizacion;


    // Variables de la actividad
    String mNumFactura, mTipoId;
    int numFacNueva;
    double mSubTotFac = 0; //Monto del subtotal
    double mDesctoFac = 0; //Monto del descuento
    double mImptoFac = 0; // Monto del IVA
    double mTotFac = 0; // Monto total factura
    String tipoDoc = "01"; // Tipo documento (01=FE, 04=TE)
    String tipoFac = "01"; // Tipo factura (01=Contado, 02=Credito)

    double mlatitud;
    double mlongitud;
    int mOpcionId, plazoFac;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emite_factura);

        tipoDoc = "01";
        tipoFac = "01";
        plazoFac = 0;

        formatodecimal = new DecimalFormat("####,###.00");
        progressBar = findViewById(R.id.ef_progressBar);

        txtNomFactura = findViewById(R.id.ef_edNomFac);
        txtNomFactura.setEnabled(false);
        txtIdFactura = findViewById(R.id.ef_edIdFac);
        txtIdFactura.setEnabled(false);
        spTipoId = findViewById(R.id.ef_spTipoId);
        spTipoId.setEnabled(false);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.tipoid, R.layout.texto_spinner);
        spTipoId.setAdapter(arrayAdapter);
        spTipoId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mTipoId = parent.getItemAtPosition(position).toString();
                mOpcionId = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        txtNotas = findViewById(R.id.ef_etNotas);
        txtNotas.setEnabled(false);
        txtEmailFactura = findViewById(R.id.ef_edEmailFac);
        txtEmailFactura.setEnabled(false);

        tvNomNegocio = findViewById(R.id.ef_tvNomNegocio);
        tvNomNegocio.setText("");

        tvSubTotal = findViewById(R.id.ef_tvSubFactura);
        tvMonDescto = findViewById(R.id.ef_tvDsctoFactura);
        tvMonImpto = findViewById(R.id.ef_tvImptoFactura);
        tvTotFactura = findViewById(R.id.ef_tvMontoFactura);
        btnBusCli = findViewById(R.id.ef_btnBusCli);
        btnCondiciones = findViewById(R.id.ef_btnCondiciones);
        btnCondiciones.setEnabled(false);
        btnEnviaFac = findViewById(R.id.ef_btnEnviaFac);
        btnEnviaFac.setEnabled(false);
        btnCancelaFac = findViewById(R.id.ef_btnCancelar);
        btnCancelaFac.setEnabled(false);
        btnAgregaItem = findViewById(R.id.ef_btnAgregaItem);
        btnAgregaItem.setEnabled(false);
        recyclerItemFac = findViewById(R.id.recyclerItemFac);
        recyclerItemFac.setLayoutManager(new LinearLayoutManager(this));


        borrarRegistrosTablaFactura();
        limpiarCampos();
        construirRecyclerItemFac();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {

            geoLocalizacion = new GeoLocalizacion(getApplicationContext());
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    geoLocalizacion = new GeoLocalizacion(getApplicationContext());

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (geoLocalizacion != null) {
            geoLocalizacion.detieneLocalizacion();
        }


    }

    private void borrarRegistrosTablaFactura() {

        conn = new ConexionSQLite(this, "bd_ruteo", null, 1);
        SQLiteDatabase db = conn.getWritableDatabase();
        db.delete(Variables.TABLA_FACTURA, null, null); //Elimina todos los registros tabla factura
        db.close();
    }

    private void construirRecyclerItemFac() {
        // arrayListItemFac = new ArrayList<>();

        llenarArrayItemFac();
        AdapterItemFac adapterItemFac = new AdapterItemFac(arrayListItemFac);
        recyclerItemFac.setAdapter(adapterItemFac);
    }

    private void llenarArrayItemFac() {

        arrayListItemFac = new ArrayList<>();

        mSubTotFac = 0.00;
        mDesctoFac = 0.00;
        mImptoFac = 0.00;
        mTotFac = 0.00;

        String sql = "SELECT * FROM " + Variables.TABLA_FACTURA;

        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            itemListaFacVo = new ItemListaFacVo();
            itemListaFacVo.setCodItem(cursor.getInt(0));
            itemListaFacVo.setNomItem(cursor.getString(1));
            itemListaFacVo.setCantidad(cursor.getInt(2));
            itemListaFacVo.setPrecio(cursor.getDouble(3));
            itemListaFacVo.setMonto(cursor.getDouble(4));
            itemListaFacVo.setDescuento(cursor.getDouble(6));
            itemListaFacVo.setImpuesto(cursor.getDouble(7));
            mSubTotFac = mSubTotFac + itemListaFacVo.getMonto();
            mDesctoFac = mDesctoFac + itemListaFacVo.getDescuento();
            mImptoFac = mImptoFac + itemListaFacVo.getImpuesto();
            arrayListItemFac.add(itemListaFacVo);
        }
        db.close();

        mTotFac = mSubTotFac - mDesctoFac + mImptoFac;
        tvSubTotal.setText(formatodecimal.format(mSubTotFac));
        tvMonDescto.setText(formatodecimal.format(mDesctoFac));
        tvMonImpto.setText(formatodecimal.format(mImptoFac));
        tvTotFactura.setText(formatodecimal.format(mTotFac));

    }

    public void btnBuscaClientes(View view) {
        if (!Internet.compruebaConexion(this)) {
            Toast.makeText(this, "Compruebe la coneccion a internet e intente de nuevo", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, SeleccionaCliente.class);
        startActivityForResult(intent, CLIENTES);
    }

    public void btnCondiciones(View view) {

        //Guardar en un bundle las condiciones recibidas
        Intent intent = new Intent(getApplicationContext(), CondicionesFactura.class);
        startActivityForResult(intent, CONDICIONES);

    }

    public void btnAgregaItem(View view) {
        if (!Internet.compruebaConexion(this)) {
            Toast.makeText(this, "Compruebe la coneccion a internet e intente de nuevo", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(getApplicationContext(), AgregaItem.class);
        intent.putExtra("desctocli", clienteVO.getPordescto());
        startActivityForResult(intent, ITEMS);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CLIENTES:
                if (resultCode == RESULT_OK) {
                    int resulCodCliente = data.getExtras().getInt("CODCLI");
                    tvNomNegocio.setText(data.getExtras().getString("NOMCLI"));

                    buscarCliente(resulCodCliente); //Buscar cliente en base de datos y actualizar datos

                } else {
                    // Se inactivan botones
                    btnBusCli.setEnabled(true);
                    btnCondiciones.setEnabled(false);
                    btnAgregaItem.setEnabled(false);
                    btnEnviaFac.setEnabled(false);
                    btnCancelaFac.setEnabled(false);
                    txtNomFactura.setEnabled(false);
                    txtIdFactura.setEnabled(false);
                    txtEmailFactura.setEnabled(false);
                    txtNotas.setEnabled(false);
                    limpiarCampos();
                }
                break;
            case CONDICIONES:
                if (resultCode == RESULT_OK) {
                    tipoFac = data.getExtras().getString("TIPOFAC");
                    tipoDoc = data.getExtras().getString("TIPODOC");
                    plazoFac = data.getExtras().getInt("PLAZO");

                    if (tipoDoc.equals("04")) {
                        txtNomFactura.setEnabled(false);
                        txtNomFactura.setText("Cliente Contado");
                        txtIdFactura.setEnabled(false);
                        txtIdFactura.setText("999999999");
                        spTipoId.setEnabled(false);
                        spTipoId.setSelection(0);
                        txtEmailFactura.setEnabled(false);
                        txtEmailFactura.setText("");

                    } else {
                        txtNomFactura.setEnabled(true);
                        txtIdFactura.setEnabled(true);
                        spTipoId.setEnabled(true);
                        txtEmailFactura.setEnabled(true);

                    }

                } else {
                    tipoDoc = "01";
                    tipoFac = "01";
                    plazoFac = 0;
                }

                break;
            case ITEMS:

                construirRecyclerItemFac();

                break;
            case IMP_FAC:

                finish();

                break;
        }
    }

    private void buscarCliente(int resulCodCliente) {

        String ip = getString(R.string.ip);
        String url = ip + "/ejecucionpdv/wsConsultaCliente.php?cod_cliente=" + resulCodCliente;
        jsonObjectRequest = new JsonObjectRequest(Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray json = response.optJSONArray("cliente");
                JSONObject jsonObject = null;
                int jsonCliente;

                try {
                    jsonObject = json.getJSONObject(0);
                    jsonCliente = jsonObject.getInt("cod_cliente");

                    if (jsonCliente != 0) {
                        //ClienteVO clienteVO=new ClienteVO();
                        clienteVO.setCodCliente(jsonObject.getInt("cod_cliente"));
                        clienteVO.setNomCliente(jsonObject.getString("nom_cliente"));
                        clienteVO.setNomTributa(jsonObject.optString("nom_tributa", ""));
                        clienteVO.setIdTributa(jsonObject.optString("id_tributa", ""));
                        clienteVO.setTipoId(jsonObject.optString("tipo_id", ""));
                        clienteVO.setEmail(jsonObject.optString("email_cliente", ""));
                        clienteVO.setPordescto(jsonObject.optInt("por_descto", 0));
                        clienteVO.setShareKolbi(jsonObject.optInt("sharekolbi", 0));
                        clienteVO.setShareMovistar(jsonObject.optInt("sharemovistar", 0));
                        mTipoId = jsonObject.optString("tipo_id", "");
                        spTipoId.setSelection(Integer.parseInt(mTipoId.substring(0, 1)) - 1);

                        //Se activan o inactivan botones y campos de texto
                        btnBusCli.setEnabled(false); //Inactiva boton buscar clientes
                        btnCondiciones.setEnabled(true);
                        btnAgregaItem.setEnabled(true);
                        btnEnviaFac.setEnabled(true);
                        btnCancelaFac.setEnabled(true);
                        txtNomFactura.setEnabled(true);
                        txtNomFactura.requestFocus();
                        txtNomFactura.setText(clienteVO.getNomTributa());
                        txtIdFactura.setText(clienteVO.getIdTributa());
                        txtEmailFactura.setText(clienteVO.getEmail());


                        txtIdFactura.setEnabled(true);
                        spTipoId.setEnabled(true);
                        txtEmailFactura.setEnabled(true);
                        txtNotas.setEnabled(true);

                    } else {
                        Toast.makeText(getApplicationContext(), "Cliente NO existe", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //  Toast.makeText(getApplicationContext(), "Error en respuesta del servidor" + error.toString(), Toast.LENGTH_SHORT).show();
                Log.i("ERROR", error.toString());
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

    }

    private void limpiarCampos() {

        txtNomFactura.setText("");
        txtIdFactura.setText("");
        txtEmailFactura.setText("");
        mSubTotFac = 0;
        mDesctoFac = 0;
        mImptoFac = 0;
        mTotFac = 0;
        tvSubTotal.setText(formatodecimal.format(mSubTotFac));
        tvMonDescto.setText(formatodecimal.format(mDesctoFac));
        tvMonImpto.setText(formatodecimal.format(mImptoFac));
        tvTotFactura.setText(formatodecimal.format(mTotFac));
        txtNotas.setText("");

    } // Fin de limpiar campos


    public void onClickCancelaFac(View view) {
        // Se inactivan botones
        btnBusCli.setEnabled(true);
        btnBusCli.requestFocus();
        btnCondiciones.setEnabled(false);
        btnAgregaItem.setEnabled(false);
        btnEnviaFac.setEnabled(false);
        btnCancelaFac.setEnabled(false);
        txtNomFactura.setEnabled(false);
        txtIdFactura.setEnabled(false);
        txtEmailFactura.setEnabled(false);
        txtNotas.setEnabled(false);
        tvNomNegocio.setText("");
        limpiarCampos();
        borrarRegistrosTablaFactura();
        construirRecyclerItemFac();

    } // Fin del onClick Cancelar


    public void onClickEnviarFac(View view) {
        if (!Internet.compruebaConexion(this)) {
            Toast.makeText(this, "Compruebe la coneccion a INTERNET e intente de nuevo", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mTotFac == 0) { //Verifica que no se incluya una factura en CERO
            Toast.makeText(getApplicationContext(), "No se puede registrar una factura con valor CERO", Toast.LENGTH_SHORT).show();
            return;
        }

        if (txtEmailFactura.getText().length() > 0) {
            if (!VerificaEmail.verificar(txtEmailFactura.getText().toString())) {
                Toast.makeText(EmiteFactura.this, "Correo Electronico Incorrecto", Toast.LENGTH_SHORT).show();
                txtEmailFactura.requestFocus();
                return;
            }
        }

        if (tipoDoc.equals("01")) {
            if (txtNomFactura.length() > 0) {

                // if(txtIdFactura.)


                if (txtIdFactura.length() == 0) {
                    Toast.makeText(EmiteFactura.this, "Debe introducir un número de ID", Toast.LENGTH_SHORT).show();
                    txtIdFactura.requestFocus();
                    return;
                } else {
                    switch (mOpcionId) {
                        case 0:
                            if (txtIdFactura.length() != 9) {
                                Toast.makeText(EmiteFactura.this, "Largo de ID incorrecto", Toast.LENGTH_SHORT).show();
                                txtIdFactura.requestFocus();
                                return;
                            }
                            break;
                        case 1:
                        case 3:
                            if (txtIdFactura.length() != 10) {
                                Toast.makeText(EmiteFactura.this, "Largo de ID incorrecto", Toast.LENGTH_SHORT).show();
                                txtIdFactura.requestFocus();
                                return;
                            }
                            break;
                        case 2:
                            if (txtIdFactura.length() < 11 || txtIdFactura.length() > 12) {
                                Toast.makeText(EmiteFactura.this, "Largo de ID incorrecto", Toast.LENGTH_SHORT).show();
                                txtIdFactura.requestFocus();
                                return;
                            }
                            break;
                    }
                }
            } else {
                Toast.makeText(EmiteFactura.this, "Debe digitar un nombre a facturar", Toast.LENGTH_SHORT).show();
                txtNomFactura.requestFocus();
                return;
            }
        }


        progressBar.setVisibility(View.VISIBLE);


        agregarFacturaJson();
        borrarRegistrosTablaFactura();
        construirRecyclerItemFac();

        // Inactiva campos y botones y limpia
        btnBusCli.setEnabled(true);
        btnBusCli.requestFocus();
        btnCondiciones.setEnabled(false);
        btnAgregaItem.setEnabled(false);
        btnEnviaFac.setEnabled(false);
        btnCancelaFac.setEnabled(false);

        txtNomFactura.setEnabled(false);
        txtIdFactura.setEnabled(false);
        txtEmailFactura.setEnabled(false);
        txtNotas.setEnabled(false);
        tvNomNegocio.setText("");
        limpiarCampos();

    }// Fin del onClik de Enviar Factura

    private void agregarFacturaJson() {

        Location loc = geoLocalizacion.obtenerLocalizacion();

        if (loc != null) {
            mlatitud = loc.getLatitude();
            mlongitud = loc.getLongitude();
        } else {
            mlatitud = 0;
            mlongitud = 0;
        }

        clienteVO.setNomTributa(txtNomFactura.getText().toString());
        clienteVO.setIdTributa(txtIdFactura.getText().toString());
        clienteVO.setEmail(txtEmailFactura.getText().toString());
        clienteVO.setTipoId(mTipoId);

        // Crear array con Items de facturas
        String sql = "SELECT * FROM " + Variables.TABLA_FACTURA;
        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        JSONArray jsonArray = new JSONArray();

        while (cursor.moveToNext()) try {
            JSONObject jsonItemFac = new JSONObject();
            //jsonItemFac.put("codigo_art", cursor.getInt(0));
            jsonItemFac.put("codigo_art", cursor.getInt(0));
            jsonItemFac.put("cantidad", cursor.getInt(2));
            jsonItemFac.put("precio", cursor.getDouble(3));
            jsonItemFac.put("mon_subtotal", cursor.getDouble(4));
            jsonItemFac.put("por_descto", cursor.getInt(5));
            jsonItemFac.put("mon_descto", cursor.getDouble(6));
            jsonItemFac.put("mon_impto", cursor.getDouble(7));
            jsonItemFac.put("por_impto", cursor.getInt(8));
            jsonItemFac.put("exento", cursor.getInt(9));
            jsonArray.put(jsonItemFac);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        db.close();

        JSONObject jsonFactura = new JSONObject();

        try {
            jsonFactura.put("cod_agencia", Variables.COD_AGENCIA);
            jsonFactura.put("cod_pdv", Variables.COD_PDV);
            jsonFactura.put("cod_vendedor", Variables.CODIGO_USUARIO);
            jsonFactura.put("cod_cliente", clienteVO.getCodCliente());
            jsonFactura.put("tipo_doc", tipoDoc);
            jsonFactura.put("tipo_fac", tipoFac);
            jsonFactura.put("plazo_fac", plazoFac);
            jsonFactura.put("nom_tributa", clienteVO.getNomTributa());
            jsonFactura.put("id_tributa", clienteVO.getIdTributa());
            jsonFactura.put("tipo_id", clienteVO.getTipoId());
            jsonFactura.put("email", clienteVO.getEmail());
            jsonFactura.put("notas_fact", txtNotas.getText().toString());
            jsonFactura.put("sharekolbi", clienteVO.getShareKolbi());
            jsonFactura.put("sharemovistar", clienteVO.getShareMovistar());
            jsonFactura.put("latitud_bit", mlatitud);
            jsonFactura.put("longitud_bit", mlongitud);
            jsonFactura.put("articulos", jsonArray);

            String ip = getString(R.string.ip);
            String url = ip + "/ejecucionpdv/wsAgregaFacturaJson.php?";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonFactura, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressBar.setVisibility(View.GONE);
                    numFacNueva = response.optInt("factura");

                    Toast.makeText(getApplicationContext(), "Venta registrada. Numero Control " + numFacNueva, Toast.LENGTH_SHORT).show();
                    enviaDocEle();


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Error en respuesta del servidor" + error.toString(), Toast.LENGTH_SHORT).show();
                    Log.i("ERROR", error.toString());
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    } // Fin de agregarFacturaJson

    private void enviaDocEle() {
        progressBar.setVisibility(View.VISIBLE);
        String ip = getString(R.string.ip);
        String url = ip + "/ejecucionpdv/wsApifecrEnvioDocumento.php?num_factura=" + numFacNueva;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);

                int codigoRespuesta = response.optInt("CodigoRespuesta");
                if (codigoRespuesta == 1) {
                    Toast.makeText(EmiteFactura.this, "Documento enviado a Hacienda", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EmiteFactura.this, "ERROR en el envío a Hacienda\nDocumento será enviado posteriormente", Toast.LENGTH_LONG).show();
                }
                imprimeFactura();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Error de conexion con Hacienda \nDocumento será enviado posteriormente", Toast.LENGTH_LONG).show();
                Log.i("ERROR", error.toString());

                imprimeFactura();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

    }


    public void imprimeFactura() {

        Intent intent = new Intent(EmiteFactura.this, ImprimeFactura.class);
        intent.putExtra("numfactura", numFacNueva);
        startActivityForResult(intent, IMP_FAC);

    }


}

package com.seccab.ejecucinruteo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.seccab.ejecucinruteo.metodos.GeoLocalizacion;
import com.seccab.ejecucinruteo.metodos.Internet;
import com.seccab.ejecucinruteo.metodos.VolleySingleton;
import com.seccab.ejecucinruteo.modelo.BitacoraVo;
import com.seccab.ejecucinruteo.modelo.ClienteVO;
import com.seccab.ejecucinruteo.modelo.Variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;

public class Bitacora extends AppCompatActivity {

    Spinner spNoCompra;
    String mMotNOCompra = "";
    TextView tvNomNegocio, tvMonCompra;
    Button btnBuscaCliente, btnGuardar;
    EditText txtNotas;
    ProgressBar progressBar;
    LocationManager locationManager;
    LocationListener locationListener;
    GeoLocalizacion geoLocalizacion;

    ClienteVO clienteVO;
    BitacoraVo bitacoraVo;

    DecimalFormat formatoDecimal = new DecimalFormat("#####,###.00");

    DateFormat formatoCompleto;
    DateFormat formatoFecha;

    double mMOnCompra;
    double mlatitud=0.00;
    double mlongitud=0.00;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitacora);

       // geoLocalizacion= new GeoLocalizacion(this.getApplicationContext(),this.getParent());
        clienteVO = new ClienteVO();
        bitacoraVo = new BitacoraVo();

        progressBar = findViewById(R.id.bit_progressBar);

        spNoCompra = findViewById(R.id.bit_spNoCompra);
        spNoCompra.setEnabled(false);
        tvNomNegocio = findViewById(R.id.bit_tvNomNegocio);
        tvMonCompra = findViewById(R.id.bit_tvCompra);
        txtNotas = findViewById(R.id.bit_tvNotaBita);
        txtNotas.setEnabled(false);
        btnBuscaCliente = findViewById(R.id.bit_btnBuscaCliente);
        btnGuardar = findViewById(R.id.bit_btnGuardar);
        btnGuardar.setEnabled(false);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.motivonoventa, R.layout.texto_spinner);
        spNoCompra.setAdapter(arrayAdapter);
        spNoCompra.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mMotNOCompra = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        limpiarCampos();

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
        if(geoLocalizacion!=null){
            geoLocalizacion.detieneLocalizacion();
        }


    }

    public void bit_btnBuscaCliente(View view) {
        if (!Internet.compruebaConexion(this)) {
            Toast.makeText(this, "Compruebe la coneccion a internet e intente de nuevo", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, SeleccionaCliente.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            int resulCodCliente;
            resulCodCliente = data.getIntExtra("CODCLI", 0);
            //resulCodCliente = data.getExtras().getInt("CODCLI");
            tvNomNegocio.setText(data.getStringExtra("NOMCLI"));
            //tvNomNegocio.setText(data.getExtras().getString("NOMCLI"));

            buscarCliente(resulCodCliente); //Buscar cliente en base de datos y actualizar datos
            buscarBitacora(resulCodCliente);
            txtNotas.setEnabled(true);
            btnGuardar.setEnabled(true);

        } else {
            // Se inactivan botones
            limpiarCampos();
            spNoCompra.setEnabled(false);
            txtNotas.setEnabled(false);
        }
    } // Fin de onActivityResult

    public void bit_btnGuardar(View view) {

        if (!Internet.compruebaConexion(this)) {
            Toast.makeText(this, "Compruebe la coneccion a internet e intente de nuevo", Toast.LENGTH_SHORT).show();
            return;
        }
               
        // Actualiza campos de motivo observaciones, motivo de no compra
        bitacoraVo.setNotas(txtNotas.getText().toString());

        if (bitacoraVo.getMon_compra() == 0) {
            bitacoraVo.setMot_nocompra(mMotNOCompra);
            bitacoraVo.setSharemovistar(0);
            bitacoraVo.setSharekolbi(0);
        } else {
            bitacoraVo.setMot_nocompra("");
        }

        guardarBitacora();
        limpiarCampos();

        spNoCompra.setEnabled(false);
        btnGuardar.setEnabled(false);
        txtNotas.setEnabled(false);


    } // Fin de boton guardar

    private void guardarBitacora() {

        progressBar.setVisibility(View.VISIBLE);

        Location loc = geoLocalizacion.obtenerLocalizacion();

        if (loc != null) {
            mlatitud = loc.getLatitude();
            mlongitud = loc.getLongitude();
        } else {
            mlatitud = 0;
            mlongitud = 0;
        }

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cod_cliente", clienteVO.getCodCliente());
            jsonObject.put("cod_agencia", Variables.COD_AGENCIA);
            jsonObject.put("cod_pdv", Variables.COD_PDV);
            jsonObject.put("cod_vendedor", Variables.CODIGO_USUARIO);
            jsonObject.put("mon_compra", 0);
            jsonObject.put("mot_nocompra", bitacoraVo.getMot_nocompra());
            jsonObject.put("sharekolbi", bitacoraVo.getSharekolbi());
            jsonObject.put("sharemovistar", bitacoraVo.getSharemovistar());
            jsonObject.put("latitud_bit", mlatitud);
            jsonObject.put("longitud_bit", mlongitud);
            if (txtNotas.getText() != null) {
                jsonObject.put("observaciones", txtNotas.getText());
            } else {
                jsonObject.put("observaciones", "");
            }


            String ip = getString(R.string.ip);

            String url = ip + "/ejecucionpdv/wsActualizaBitacora.php?";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressBar.setVisibility(View.GONE);

                    String respuesta = response.optString("resultado");

                    //Toast.makeText(getApplicationContext(), respuesta, Toast.LENGTH_SHORT).show();


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Error en respuesta del servidor" + error.toString(), Toast.LENGTH_SHORT).show();
                    Log.i("ERROR", error.toString());
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void limpiarCampos() {
        mMOnCompra = 0;
        txtNotas.setText("");
        tvNomNegocio.setText("");

    } // End limpiarCampos

    private void buscarCliente(int resulCodCliente) {

        String ip = getString(R.string.ip);
        String url = ip + "/ejecucionpdv/wsConsultaCliente.php?cod_cliente=" + resulCodCliente;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray json = response.optJSONArray("cliente");
                //   JSONObject jsonObject = null;
                try {
                    JSONObject jsonObject = json.getJSONObject(0);
                    clienteVO.setCodCliente(jsonObject.optInt("cod_cliente"));
                    clienteVO.setNomCliente(jsonObject.optString("nom_cliente"));
                    clienteVO.setCodAgencia(jsonObject.optInt("cod_agencia"));
                    clienteVO.setCodPDV(jsonObject.optInt("cod_ruta"));
                    clienteVO.setShareKolbi(jsonObject.optInt("sharekolbi", 0));
                    clienteVO.setShareMovistar(jsonObject.optInt("sharemovistar", 0));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(getApplicationContext(), "Error en respuesta del servidor" + error.toString(), Toast.LENGTH_SHORT).show();
                Log.i("ERROR", error.toString());
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

    }  // End buscarCliente


    // Busca si existe registro de bitacora
    private void buscarBitacora(int resulCodCliente) {

        progressBar.setVisibility(View.VISIBLE);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cod_cliente", resulCodCliente);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String ip = getString(R.string.ip);
        String url = ip + "/ejecucionpdv/wsConsultaBitacora.php?";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);

                JSONArray jsonArray = response.optJSONArray("bitacora");

                bitacoraVo = new BitacoraVo();

                if (jsonArray != null) {
                    try {
                        JSONObject jsonresp = jsonArray.getJSONObject(0);
                        bitacoraVo.setMon_compra(jsonresp.optDouble("mon_compra"));
                        bitacoraVo.setMot_nocompra(jsonresp.optString("mot_nocompra"));
                        mMotNOCompra = jsonresp.optString("mot_nocompra");
                        bitacoraVo.setSharekolbi(jsonresp.optInt("sharekolbi"));
                        bitacoraVo.setSharemovistar(jsonresp.optInt("sharemovistar"));
                        bitacoraVo.setNotas(jsonresp.optString("observaciones"));
                        bitacoraVo.setCod_cliente(clienteVO.getCodCliente());
                        bitacoraVo.setCod_agencia(clienteVO.getCodAgencia());
                        bitacoraVo.setCod_pdv(clienteVO.getCodPDV());
                        bitacoraVo.setCod_vendedor(Variables.CODIGO_USUARIO);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    bitacoraVo.setMon_compra(0);
                    bitacoraVo.setMot_nocompra("");
                    bitacoraVo.setNotas("");
                    bitacoraVo.setCod_cliente(clienteVO.getCodCliente());
                    bitacoraVo.setCod_agencia(clienteVO.getCodAgencia());
                    bitacoraVo.setCod_pdv(clienteVO.getCodPDV());
                    bitacoraVo.setCod_vendedor(Variables.CODIGO_USUARIO);
                    bitacoraVo.setSharekolbi(clienteVO.getShareKolbi());
                    bitacoraVo.setSharemovistar(clienteVO.getShareMovistar());
                }
                if (bitacoraVo.getMon_compra() > 0) {
                    spNoCompra.setEnabled(false);
                } else {
                    spNoCompra.setEnabled(true);
                }
                tvMonCompra.setText(formatoDecimal.format(bitacoraVo.getMon_compra()));
                txtNotas.setText(bitacoraVo.getNotas());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressBar.setVisibility(View.GONE);


                Log.i("ERROR", error.toString());
                // Toast.makeText(getApplicationContext(), "Error de conexion con el servidor" + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

    } // fin de buscarBitacora


} // End class Bitacora

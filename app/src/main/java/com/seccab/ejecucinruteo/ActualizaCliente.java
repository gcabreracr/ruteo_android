package com.seccab.ejecucinruteo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.seccab.ejecucinruteo.metodos.GeoLocalizacion;
import com.seccab.ejecucinruteo.metodos.Internet;
import com.seccab.ejecucinruteo.metodos.VerificaEmail;
import com.seccab.ejecucinruteo.metodos.VolleySingleton;
import com.seccab.ejecucinruteo.modelo.ClienteVO;
import com.seccab.ejecucinruteo.modelo.Variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ActualizaCliente extends AppCompatActivity {

    GeoLocalizacion geoLocalizacion;
    Spinner spTipoId, spProvincia, spCanton, spDistrito;
    String mTipoId;
    ImageButton btnUbicacion;
    Button btnGuardar, btnNuevoCliente, btnBuscarCliente;
    EditText txtNomNegocio, txtnomTributa, txtIdTributa, txtNomContacto, txtTelNegocio, txtTelContacto, txtEmail, txtReferencia;
    EditText txtProvincia,txtCanton,txtDistrito,txtShareKolbi, txtShareMovistar, txtLatitud, txtLongitud;
    JsonObjectRequest jsonObjectRequest;
    StringRequest stringRequest;
    ClienteVO clienteVO;
    String nuevoCliente;
    ProgressBar progressBar;

    DecimalFormat formatoGeo = new DecimalFormat("###0.000000");

    double mlatitud = 0;
    double mlongitud = 0;
    int mOpcionId;


    private static final int CLIENTES = 0;
    private static final int PERMISO_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualiza_cliente);

        progressBar = findViewById(R.id.ac_progressBar);
        btnUbicacion = findViewById(R.id.ac_btnUbicacion);
        txtLatitud = findViewById(R.id.ac_tvLatitud);
        txtLongitud = findViewById(R.id.ac_tvLongitud);
        btnBuscarCliente = findViewById(R.id.ac_btnBuscarCliente);
        btnNuevoCliente = findViewById(R.id.ac_btnNuevo);
        btnGuardar = findViewById(R.id.ac_btnGuardar);
        txtNomNegocio = findViewById(R.id.ac_edNomNegocio);
        txtnomTributa = findViewById(R.id.ac_edNomFactura);
        txtIdTributa = findViewById(R.id.ac_edIdTributa);
        spTipoId = findViewById(R.id.ac_spTipoId);
        txtNomContacto = findViewById(R.id.ac_nomContacto);
        txtTelNegocio = findViewById(R.id.ac_edTelNeg);
        txtTelContacto = findViewById(R.id.ac_edTelCon);
        txtEmail = findViewById(R.id.ac_email);
        txtReferencia = findViewById(R.id.ac_referencia);
        txtProvincia = findViewById(R.id.ac_Provincia);
        txtCanton = findViewById(R.id.ac_Canton);
        txtDistrito = findViewById(R.id.ac_Distrito);
        txtShareKolbi = findViewById(R.id.ac_Kolbi);
        txtShareMovistar = findViewById(R.id.ac_Movistar);

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

        inactivaCampos();

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtNomNegocio.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Nombre cliente invalido", Toast.LENGTH_LONG).show();
                    txtNomNegocio.requestFocus();
                    return;
                }
                if (txtReferencia.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Direccion invalida", Toast.LENGTH_LONG).show();
                    txtReferencia.requestFocus();
                    return;
                }
                if (txtEmail.getText().length() > 0) {
                    if (!VerificaEmail.verificar(txtEmail.getText().toString())) {
                        Toast.makeText(ActualizaCliente.this, "Correo Electronico Incorrecto", Toast.LENGTH_SHORT).show();

                        txtEmail.requestFocus();
                        return;
                    }
                }

                if (txtnomTributa.length() > 0) {
                    if (txtIdTributa.length() == 0) {
                        Toast.makeText(ActualizaCliente.this, "Debe introducir un número de ID", Toast.LENGTH_SHORT).show();
                        txtIdTributa.requestFocus();
                        return;
                    } else {
                        switch (mOpcionId) {
                            case 0:
                                if (txtIdTributa.length() != 9) {
                                    Toast.makeText(ActualizaCliente.this, "Largo de ID incorrecto", Toast.LENGTH_SHORT).show();
                                    txtIdTributa.requestFocus();
                                    return;
                                }
                                break;
                            case 1:
                            case 3:
                                if (txtIdTributa.length() != 10) {
                                    Toast.makeText(ActualizaCliente.this, "Largo de ID incorrecto", Toast.LENGTH_SHORT).show();
                                    txtIdTributa.requestFocus();
                                    return;
                                }
                                break;
                            case 2:
                                if (txtIdTributa.length() < 11 || txtIdTributa.length() > 12) {
                                    Toast.makeText(ActualizaCliente.this, "Largo de ID incorrecto", Toast.LENGTH_SHORT).show();
                                    txtIdTributa.requestFocus();
                                    return;
                                }
                                break;
                        }
                    }
                } else {
                    txtIdTributa.setText("");
                    txtEmail.setText("");
                    mTipoId = "1-Fisica";
                }

                guardarCliente();

            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISO_FINE_LOCATION);
        } else {
            geoLocalizacion = new GeoLocalizacion(getApplicationContext());
        }


    } // Fin onCreate()

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISO_FINE_LOCATION: {
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
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (geoLocalizacion != null) {
            geoLocalizacion.detieneLocalizacion();
        }


    }

    private void activaCampos() {
        txtNomNegocio.setEnabled(true);
        txtEmail.setEnabled(true);
        txtTelNegocio.setEnabled(true);
        txtnomTributa.setEnabled(true);
        txtIdTributa.setEnabled(true);
        spTipoId.setEnabled(true);
        btnUbicacion.setEnabled(true);
        txtNomContacto.setEnabled(true);
        txtTelContacto.setEnabled(true);
        txtReferencia.setEnabled(true);
        txtShareKolbi.setEnabled(true);
        txtShareMovistar.setEnabled(true);
        txtProvincia.setEnabled(true);
        txtCanton.setEnabled(true);
        txtDistrito.setEnabled(true);
    }

    private void inactivaCampos() {

        txtNomNegocio.setEnabled(false);
        txtEmail.setEnabled(false);
        txtTelNegocio.setEnabled(false);
        txtnomTributa.setEnabled(false);
        txtIdTributa.setEnabled(false);
        spTipoId.setEnabled(false);
        btnUbicacion.setEnabled(false);
        txtNomContacto.setEnabled(false);
        txtTelContacto.setEnabled(false);
        txtReferencia.setEnabled(false);
        txtShareKolbi.setEnabled(false);
        txtShareMovistar.setEnabled(false);
        txtProvincia.setEnabled(false);
        txtCanton.setEnabled(false);
        txtDistrito.setEnabled(false);
    }

    public void ac_btnNuevo(View view) {
        clienteVO = new ClienteVO();
        nuevoCliente = "S";
        limpiarCampos();
        activaCampos();
        txtNomNegocio.requestFocus();
    }

    public void ac_btnBuscarCliente(View view) {

        if (!Internet.compruebaConexion(this)) {
            Toast.makeText(this, "Compruebe la coneccion a internet e intente de nuevo", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, SeleccionaCliente.class);
        startActivityForResult(intent, CLIENTES);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            nuevoCliente = "N";

            int resulCodCliente = (Objects.requireNonNull(data.getExtras())).getInt("CODCLI");
            buscarClienteSeleccionado(resulCodCliente); //Buscar cliente en base de datos y actualizar datos
            activaCampos();

        } else {

            limpiarCampos();
            inactivaCampos();
        }
    }

    private void limpiarCampos() {

        txtNomNegocio.setText("");
        txtEmail.setText("");
        txtTelNegocio.setText("");
        txtnomTributa.setText("");
        txtIdTributa.setText("");
        txtNomContacto.setText("");
        txtTelContacto.setText("");
        txtReferencia.setText("");
        txtShareKolbi.setText("0");
        txtShareMovistar.setText("0");
        txtProvincia.setText("");
        txtCanton.setText("");
        txtDistrito.setText("");
        txtLatitud.setText("0.000000");
        txtLongitud.setText("0.000000");
    }

    private void buscarClienteSeleccionado(int codigocliente) {

        String ip = getString(R.string.ip);
        String url = ip + "/ejecucionpdv/wsConsultaCliente.php?cod_cliente=" + codigocliente;
        //+"&db_usuario="+Variables.DB_USUARIO;


        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray json = response.optJSONArray("cliente");
                        JSONObject jsonObject = null;

                        int jsonCliente;

                        try {
                            jsonObject = json.getJSONObject(0);
                            jsonCliente = jsonObject.getInt("cod_cliente");

                            if (jsonCliente != 0) {
                                clienteVO = new ClienteVO();
                                clienteVO.setCodCliente(jsonObject.getInt("cod_cliente"));
                                clienteVO.setNomCliente(jsonObject.getString("nom_cliente"));
                                txtNomNegocio.setText(jsonObject.getString("nom_cliente"));
                                clienteVO.setNomTributa(jsonObject.optString("nom_tributa", ""));
                                txtnomTributa.setText(jsonObject.optString("nom_tributa", ""));
                                clienteVO.setIdTributa(jsonObject.optString("id_tributa", ""));
                                txtIdTributa.setText(jsonObject.optString("id_tributa", ""));
                                clienteVO.setTipoId(jsonObject.optString("tipo_id", ""));
                                mTipoId = jsonObject.optString("tipo_id", "");
                                spTipoId.setSelection(Integer.parseInt(mTipoId.substring(0, 1)) - 1);
                                clienteVO.setEmail(jsonObject.optString("email_cliente", ""));
                                txtEmail.setText(jsonObject.optString("email_cliente", ""));
                                clienteVO.setNomContacto(jsonObject.optString("nom_contacto", ""));
                                txtNomContacto.setText(jsonObject.optString("nom_contacto", ""));
                                clienteVO.setTelContacto(jsonObject.optString("tel_contacto", ""));
                                txtTelContacto.setText(jsonObject.optString("tel_contacto", ""));
                                clienteVO.setTelNegocio(jsonObject.optString("tel_negocio", ""));
                                txtTelNegocio.setText(jsonObject.optString("tel_negocio", ""));
                                clienteVO.setProvincia(jsonObject.optString("provincia", ""));
                                txtProvincia.setText(jsonObject.optString("provincia", ""));
                                clienteVO.setCanton(jsonObject.optString("canton", ""));
                                txtCanton.setText(jsonObject.optString("canton", ""));
                                clienteVO.setDistrito(jsonObject.optString("distrito", ""));
                                txtDistrito.setText(jsonObject.optString("distrito", ""));
                                clienteVO.setReferencia(jsonObject.optString("referencia", ""));
                                txtReferencia.setText(jsonObject.optString("referencia", ""));
                                clienteVO.setPordescto(jsonObject.optInt("por_descto", 0));
                                clienteVO.setShareKolbi(jsonObject.optInt("sharekolbi", 0));
                                txtShareKolbi.setText(String.valueOf(jsonObject.optInt("sharekolbi", 0)));
                                clienteVO.setShareMovistar(jsonObject.optInt("sharemovistar", 0));
                                txtShareMovistar.setText(String.valueOf(jsonObject.optInt("sharemovistar", 0)));
                                clienteVO.setLatitud(jsonObject.optDouble("latitud", 0));
                                Double tLati = jsonObject.optDouble("latitud");
                                txtLatitud.setText(formatoGeo.format(tLati));
                                clienteVO.setLongitud(jsonObject.optDouble("longitud", 0));
                                Double tLong = jsonObject.optDouble("longitud");
                                txtLongitud.setText(formatoGeo.format(tLong));

                                activaCampos();
                                txtNomNegocio.requestFocus();

                            } else {
                                Toast.makeText(getApplicationContext(), "Cliente NO existe", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error en respuesta del servidor" + error.toString(), Toast.LENGTH_LONG).show();
                Log.i("ERROR", error.toString());
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

    }


    private void guardarCliente() {
        if (!Internet.compruebaConexion(this)) {
            Toast.makeText(this, "Compruebe la coneccion a internet e intente de nuevo", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        clienteVO.setNomCliente(txtNomNegocio.getText().toString());
        clienteVO.setNomTributa(txtnomTributa.getText().toString());
        clienteVO.setIdTributa(txtIdTributa.getText().toString());
        clienteVO.setTipoId(mTipoId);
        clienteVO.setNomContacto(txtNomContacto.getText().toString());
        clienteVO.setTelNegocio(txtTelNegocio.getText().toString());
        clienteVO.setTelContacto(txtTelContacto.getText().toString());
        clienteVO.setProvincia(txtProvincia.getText().toString());
        clienteVO.setCanton(txtCanton.getText().toString());
        clienteVO.setDistrito(txtDistrito.getText().toString());
        clienteVO.setReferencia(txtReferencia.getText().toString());

        clienteVO.setEmail(txtEmail.getText().toString());
        if (txtShareKolbi != null) {
            clienteVO.setShareKolbi(Integer.valueOf(txtShareKolbi.getText().toString()));
        } else {
            clienteVO.setShareKolbi(0);
        }
        if (txtShareMovistar != null) {
            clienteVO.setShareMovistar(Integer.valueOf(txtShareMovistar.getText().toString()));
        } else {
            clienteVO.setShareMovistar(0);
        }
        if (txtLatitud != null) {
            clienteVO.setLatitud(Double.valueOf(txtLatitud.getText().toString()));
        } else {
            clienteVO.setLatitud(0.00);
        }
        if (txtLongitud != null) {
            clienteVO.setLongitud(Double.valueOf(txtLongitud.getText().toString()));
        } else {
            clienteVO.setLongitud(0.00);
        }


        if (nuevoCliente.equals("S")) {
            clienteVO.setCodAgencia(Variables.COD_AGENCIA);
            clienteVO.setCodPDV(Variables.COD_PDV);
            clienteVO.setPordescto(Variables.DESCTO_AUT_USUARIO);

            //Calcular el dia de visita
            Calendar calendar = Calendar.getInstance();
            int numdia = calendar.get(Calendar.DAY_OF_WEEK);


            switch (numdia) {
                case 1:
                    clienteVO.setVisDom(1);
                    break;
                case 2:
                    clienteVO.setVisLun(1);
                    break;
                case 3:
                    clienteVO.setVisMar(1);
                    break;
                case 4:
                    clienteVO.setVisMie(1);
                    break;
                case 5:
                    clienteVO.setVisJue(1);
                    break;
                case 6:
                    clienteVO.setVisVie(1);
                    break;
                case 7:
                    clienteVO.setVisSab(1);
                    break;
            }

        }


        String ip = getString(R.string.ip);
        String url = ip + "/ejecucionpdv/wsActualizaCliente.php?";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);

                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                limpiarCampos();
                inactivaCampos();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);

                Toast.makeText(getApplicationContext(), "Error en la conexion con la Web Service", Toast.LENGTH_LONG).show();
                txtNomNegocio.requestFocus();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> parametros = new HashMap<>();
                parametros.put("nuevo", nuevoCliente);
                parametros.put("cod_cliente", String.valueOf(clienteVO.getCodCliente()));
                // parametros.put("db_usuario", Variables.DB_USUARIO);
                parametros.put("nom_cliente", clienteVO.getNomCliente());
                parametros.put("nom_tributa", clienteVO.getNomTributa());
                parametros.put("id_tributa", clienteVO.getIdTributa());
                parametros.put("tipo_id", clienteVO.getTipoId());
                parametros.put("email_cliente", clienteVO.getEmail());
                parametros.put("tel_negocio", clienteVO.getTelNegocio());
                parametros.put("tel_contacto", clienteVO.getTelContacto());
                parametros.put("nom_contacto", clienteVO.getNomContacto());

                parametros.put("provincia", clienteVO.getProvincia());

                parametros.put("canton", clienteVO.getCanton());

                parametros.put("distrito", clienteVO.getDistrito());
                parametros.put("referencia", clienteVO.getReferencia());
                parametros.put("por_descto", String.valueOf(clienteVO.getPordescto()));
                parametros.put("vis_lun", String.valueOf(clienteVO.getVisLun()));
                parametros.put("vis_mar", String.valueOf(clienteVO.getVisMar()));
                parametros.put("vis_mie", String.valueOf(clienteVO.getVisMie()));
                parametros.put("vis_jue", String.valueOf(clienteVO.getVisJue()));
                parametros.put("vis_vie", String.valueOf(clienteVO.getVisVie()));
                parametros.put("vis_sab", String.valueOf(clienteVO.getVisSab()));
                parametros.put("vis_dom", String.valueOf(clienteVO.getVisDom()));
                parametros.put("sharekolbi", String.valueOf(clienteVO.getShareKolbi()));
                parametros.put("sharemovistar", String.valueOf(clienteVO.getShareMovistar()));
                parametros.put("cod_agencia", String.valueOf(clienteVO.getCodAgencia()));
                parametros.put("cod_ruta", String.valueOf(clienteVO.getCodPDV()));
                parametros.put("latitud", String.valueOf(clienteVO.getLatitud()));
                parametros.put("longitud", String.valueOf(clienteVO.getLongitud()));
                return parametros;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(stringRequest);

    }


    public void ac_onClickUbicacion(View view) {

        Location loc = geoLocalizacion.obtenerLocalizacion();

        if (loc != null) {
            mlatitud = loc.getLatitude();
            mlongitud = loc.getLongitude();
        } else {
            mlatitud = clienteVO.getLatitud();
            mlongitud = clienteVO.getLongitud();
        }

        clienteVO.setLatitud(mlatitud);
        clienteVO.setLongitud(mlongitud);
        txtLatitud.setText(formatoGeo.format(mlatitud));
        txtLongitud.setText(formatoGeo.format(mlongitud));

    }


}


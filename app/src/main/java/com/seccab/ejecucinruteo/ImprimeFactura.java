package com.seccab.ejecucinruteo;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.seccab.ejecucinruteo.adaptadores.AdapterItemFac;
import com.seccab.ejecucinruteo.metodos.VolleySingleton;
import com.seccab.ejecucinruteo.modelo.ItemListaFacVo;
import com.seccab.ejecucinruteo.modelo.Variables;
import com.seccab.ejecucinruteo.printer_bt.BluetoothService;
import com.seccab.ejecucinruteo.printer_bt.DeviceListActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ImprimeFactura extends AppCompatActivity implements View.OnClickListener {

    // Accesar contactos y hacer llamada
    private static final int PICK_CONTACT_REQUEST = 15;
    private static final int PERMISO_HACER_LLAMADAS = 16;
    private static final int ACCESO_CONTACTOS = 17;
    private String numeroTel;
    private String nombreUsuario = null;

    // Debugging
    private static final String TAG = "Main_Activity";
    private static final boolean DEBUG = true;
    /******************************************************************************************************/
    // Message types sent from the BluetoothService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_CONNECTION_LOST = 6;
    public static final int MESSAGE_UNABLE_CONNECT = 7;
    /*******************************************************************************************************/
    // Key names received from the BluetoothService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_CHOSE_BMP = 3;
    private static final int REQUEST_CAMER = 4;

    //QRcode
    private static final int QR_WIDTH = 350;
    private static final int QR_HEIGHT = 350;

    private static boolean is58mm = true;

    /******************************************************************************************************/
    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the services
    private BluetoothService mService = null;

    private TextView tvNomImpresora;
    private String printerDefault;


    private TextView tvTipoDoc, tvNumDE, tvFechaDoc, tvHoraDoc, tvNomCliente, tvNomFactura, tvTip;
    private TextView tvIdFactura, tvTipoVenta, tvPlazo, tvTipoMoneda, tvConInterno;
    private TextView tvMonSubtotal, tvMonDescto, tvMonImpto, tvMonFactura;
    private RecyclerView rvImpFactura;
    private Button btnConectar, btnImprimir;
    private ArrayList<ItemListaFacVo> arrayListItemFac;
    private ItemListaFacVo itemListaFacVo;
    private AdapterItemFac adapterItemFac;
    private FloatingActionButton fab;

    private DecimalFormat formatoDecimal = new DecimalFormat("#####,##0.00");
    private DecimalFormat formatoEntero = new DecimalFormat("####,##0");

    private Double mSubTotFac, mImptoFac, mDesctoFac, mTotFac, mTotExe, mTotGra;
    private int numFactura, plazodias;
    private String tipoFac, tipoDoc, mClave_FE, mConse_FE, mNotasFac, mNomFactura;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imprime_factura);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


    }

    @Override
    public void onStart() {
        super.onStart();

        // If Bluetooth is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the session
        } else {
            if (mService == null)
                KeyListenerInit();// Inicializa los campos
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (ActivityCompat.checkSelfPermission(ImprimeFactura.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ImprimeFactura.this, new String[]{Manifest.permission.READ_CONTACTS}, ACCESO_CONTACTOS);
                    } else {
                        consultaContactos();
                    }


                }
            });
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();

        if (mService != null) {

            if (mService.getState() == BluetoothService.STATE_NONE) {
                // Start the Bluetooth services
                mService.start();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth services
        if (mService != null)
            mService.stop();
    }

    private void consultaContactos() {

        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(i, PICK_CONTACT_REQUEST);

    }

    private void KeyListenerInit() {

        fab = findViewById(R.id.if_fab);
        tvTipoDoc = findViewById(R.id.if_tvTipoDoc);
        tvNumDE = findViewById(R.id.if_tvNumDocEle);
        tvConInterno = findViewById(R.id.if_tvNumInterno);
        tvFechaDoc = findViewById(R.id.if_tvFechaDoc);
        tvHoraDoc = findViewById(R.id.if_tvHoraDoc);
        tvIdFactura = findViewById(R.id.if_IdFactura);
        tvNomFactura = findViewById(R.id.if_NomFactura);
        tvNomCliente = findViewById(R.id.if_NomCliente);
        tvTipoVenta = findViewById(R.id.if_tvTipoVen);
        tvPlazo = findViewById(R.id.if_tvPlazo);
        tvMonSubtotal = findViewById(R.id.if_tvSubFactura);
        tvMonImpto = findViewById(R.id.if_tvImptoFactura);
        tvMonDescto = findViewById(R.id.if_tvDsctoFactura);
        tvMonImpto = findViewById(R.id.if_tvImptoFactura);
        tvMonFactura = findViewById(R.id.if_tvMontoFactura);
        tvNomImpresora = findViewById(R.id.tvNombreImpresora);
        tvNomImpresora.setText("Impresora: NO conectada");
        btnConectar = findViewById(R.id.if_btnConectar);
        btnImprimir = findViewById(R.id.if_btnImprimir);

        btnConectar.setOnClickListener(this);

        btnImprimir.setOnClickListener(this);


        rvImpFactura = findViewById(R.id.if_RecyclerView);
        rvImpFactura.setLayoutManager(new LinearLayoutManager(this));

        // Captura datos del intent
        numFactura = getIntent().getIntExtra("numfactura", 0);

        //construirRecycler();
        llenarArrayItemFac();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth No esta disponible",
                    Toast.LENGTH_LONG).show();

            btnConectar.setEnabled(false);
            btnImprimir.setEnabled(false);

            //finish();
        } else {

            btnConectar.setEnabled(true);
            btnImprimir.setEnabled(true);

            mService = new BluetoothService(this);

            //Busca la impresora predeterminada en el SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("printer", Context.MODE_PRIVATE);
            printerDefault = sharedPreferences.getString("nombre", "");
            if (printerDefault.length() > 0) {
                tvNomImpresora.setText("Impresora: " + printerDefault);
                BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                BluetoothDevice device = bluetoothManager.getAdapter().getRemoteDevice(printerDefault);
                mService.connect(device);
            } else {
                tvNomImpresora.setText("Impresora: NO conectada");
            }

        }
    }


    private void construirRecycler() {

        // llenarArrayItemFac();

        adapterItemFac = new AdapterItemFac(arrayListItemFac);
        rvImpFactura.setAdapter(adapterItemFac);
    }


    private void SendDataString(String data) {

        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (data.length() > 0) {
            mService.write(data.getBytes(StandardCharsets.ISO_8859_1));
        }
    }

    /*
     *SendDataByte
     */
    private void SendDataByte(byte[] data) {

        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        mService.write(data);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case PICK_CONTACT_REQUEST: {
                if (resultCode == RESULT_OK) {
                    Uri contactUri = data.getData();
                    obtenerContacto(contactUri);
                }
                break;
            }

            case REQUEST_CONNECT_DEVICE: {
                // When DeviceListActivity returns with a device to connect
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(
                            DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    if (BluetoothAdapter.checkBluetoothAddress(address)) {
                        BluetoothDevice device = mBluetoothAdapter
                                .getRemoteDevice(address);

                        tvNomImpresora.setText("Impresora: " + device);
                        // Guardar la impresora en el SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("printer", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("nombre", device.toString());
                        editor.apply();
                        printerDefault = device.toString();

                        // Attempt to connect to the device
                        mService.connect(device);
                    }

                }
                break;
            }
            case REQUEST_ENABLE_BT: {
                // When the request to enable Bluetooth returns
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a session
                    KeyListenerInit();
                } else {
                    // User did not enable Bluetooth or an error occured

                    Toast.makeText(this, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            }

        }
    }


    private void llenarArrayItemFac() {

        arrayListItemFac = new ArrayList<>();

        mSubTotFac = 0.00;
        mDesctoFac = 0.00;
        mImptoFac = 0.00;
        mTotFac = 0.00;


        String ip = getString(R.string.ip);
        String url = ip + "/ejecucionpdv/wsConsultaFactura.php?num_factura=" + numFactura;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                plazodias = response.optInt("plazo_fac");
                tipoFac = response.optString("tipo_fac");
                if (tipoFac.equals("01")) {
                    tvTipoVenta.setText("Tipo venta: Contado");
                    tvPlazo.setText("");
                } else {
                    tvTipoVenta.setText("Tipo venta: Credito");
                    tvPlazo.setText("Plazo: " + String.format("%2d", plazodias));
                }
                tipoDoc = response.optString("tipo_doc");
                if (tipoDoc.equals("01")) {
                    tvTipoDoc.setText("FACTURA ELECTRONICA");
                } else {
                    tvTipoDoc.setText("TIQUETE ELECTRONICO");
                }
                tvNumDE.setText(response.optString("consecutivo_fe"));
                mConse_FE = response.optString("consecutivo_fe");
                mClave_FE = response.optString("clave_fe");
                mNotasFac = response.optString("notas");


                SimpleDateFormat formatoDMY = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat formatoYMD = new SimpleDateFormat("yyyy-MM-dd");
                Date fechaJson = null;
                try {
                    fechaJson = formatoYMD.parse(response.optString("fec_factura"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                tvFechaDoc.setText("Fecha: " + formatoDMY.format(fechaJson));
                tvHoraDoc.setText("Hora: " + response.optString("hora_factura"));
                tvNomFactura.setText("Cliente: " + response.optString("nom_factura"));
                mNomFactura = response.optString("nom_factura");
                tvConInterno.setText("Consecutivo Interno: " + numFactura);
                tvIdFactura.setText("ID: " + response.optString("id_factura"));
                tvNomCliente.setText(response.optString("nom_cliente"));
                mTotExe = response.optDouble("mon_exento");
                mTotGra = response.optDouble("mon_gravado");
                mSubTotFac = response.optDouble("mon_subtotal");
                tvMonSubtotal.setText(formatoDecimal.format(mSubTotFac));
                mDesctoFac = response.optDouble("mon_descto");
                tvMonDescto.setText(formatoDecimal.format(mDesctoFac));
                mImptoFac = response.optDouble("mon_impto");
                tvMonImpto.setText(formatoDecimal.format(mImptoFac));
                mTotFac = mSubTotFac - mDesctoFac + mImptoFac;
                tvMonFactura.setText(formatoDecimal.format(mTotFac));


                JSONArray jsonItems = response.optJSONArray("items");
                JSONObject jsonObjectItem;
                try {
                    for (int i = 0; i < jsonItems.length(); i++) {
                        itemListaFacVo = new ItemListaFacVo();
                        jsonObjectItem = jsonItems.optJSONObject(i);
                        itemListaFacVo.setCodItem(jsonObjectItem.optInt("codigo_art"));
                        itemListaFacVo.setNomItem(jsonObjectItem.optString("nom_cor_art"));
                        itemListaFacVo.setCantidad(jsonObjectItem.optInt("cantidad"));
                        itemListaFacVo.setPrecio(jsonObjectItem.optDouble("precio"));
                        itemListaFacVo.setPordescto(jsonObjectItem.optInt("por_descto"));
                        itemListaFacVo.setDescuento(jsonObjectItem.optDouble("mon_descto"));
                        itemListaFacVo.setPorimpto(jsonObjectItem.optInt("por_impto"));
                        itemListaFacVo.setImpuesto(jsonObjectItem.optInt("mon_impto"));
                        itemListaFacVo.setMonto(jsonObjectItem.optDouble("mon_subtotal"));
                        itemListaFacVo.setExento(jsonObjectItem.optInt("exento"));
                        arrayListItemFac.add(itemListaFacVo);
                    }

                    construirRecycler();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }


    private void obtenerContacto(Uri contactUri) {

        //nombreUsuario = getName(contactUri);
        numeroTel = getPhone(contactUri);
        if (numeroTel == null) {
            Toast.makeText(this, "Contacto NO tiene programado un teléfono de trabajo", Toast.LENGTH_LONG).show();
            return;
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, PERMISO_HACER_LLAMADAS);
        } else {
            //startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel: " + numeroTel)));
            hacerLlamada(numeroTel);
        }


    }

    private void hacerLlamada(String numeroTel) {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Uri.encode(numeroTel))));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISO_HACER_LLAMADAS:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    hacerLlamada(numeroTel);

                } else {
                    Toast.makeText(this, "No esta activado el permiso para hacer llamadas", Toast.LENGTH_SHORT).show();
                }
                return;

            case ACCESO_CONTACTOS:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    consultaContactos();

                } else {
                    Toast.makeText(this, "No esta activado el permiso para acceso a contactos", Toast.LENGTH_SHORT).show();
                }
        }

    }


    private String getName(Uri uri) {

        // Valor a retornar
        String name = null;

        //  Obtener una instancia del Content Resolver


        Cursor c = getContentResolver().query(
                uri, new String[]{ContactsContract.Contacts.DISPLAY_NAME}, null, null, null);

        /*
        Consultando el primer y único resultado elegido
         */
        if (c.moveToFirst()) {
            name = c.getString(0);
        }

        /*
        Cerramos el cursor
         */
        c.close();

        return name;
    }

    private String getPhone(Uri uri) {
        /*
        Variables temporales para el id y el teléfono
         */
        //String id = null;
        String phone = null;
        String id = uri.getLastPathSegment();

        /*
        Sentencia WHERE para especificar que solo deseamos
        números de telefonía móvil
         */
        String selectionArgs =
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + "= " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_WORK;

          /*
        Obtener el número telefónico
         */
        Cursor phoneCursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                selectionArgs
                ,
                new String[]{id},
                null
        );


        if (phoneCursor.moveToFirst()) {
            phone = phoneCursor.getString(0);
        }
        phoneCursor.close();

        return phone;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.if_btnConectar: {
                Intent serverIntent = new Intent(ImprimeFactura.this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                break;
            }
            case R.id.if_btnImprimir: {

                // BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                // BluetoothDevice device = bluetoothManager.getAdapter().getRemoteDevice(printerDefault);
                // mService.connect(device);


                String data = crearDocEle();
                SendDataString(data);

                break;
            }
        }

    }

    private String crearDocEle() {

        String sCan, sMon, sDescto;


        DecimalFormat formatoDecimal = new DecimalFormat("#####,##0.00");
        DecimalFormat formatoEntero = new DecimalFormat("####,##0");
        StringBuilder builder = new StringBuilder();
        builder.append(new char[]{27, '@'});

        builder.append(new char[]{27, 'd', 3}); // Avance de 3 lineas
        builder.append(new char[]{27, '!', 24}); // doble altura
        builder.append(new char[]{27, 'a', 1}); // Alineacion centrada
        builder.append("RECARGAS VIRTUALES").append("\r\n");
        builder.append("COSTA RICA S.A.").append("\r\n");
        builder.append(new char[]{27, '!', 8}); // Negrita, 10cpp
        builder.append("Ced.Jur. 3-101-658573").append("\r\n");
        builder.append("San José, Escazú, 175 mts al").append("\r\n");
        builder.append("este del Parque Central, ").append("\r\n");
        builder.append("contiguo al Ebais").append("\r\n");

        builder.append(new char[]{27, 'd', 2}); // Avance de lineas

        builder.append(tvTipoDoc.getText()).append("\r\n");
        builder.append(tvNumDE.getText()).append("\r\n");
        builder.append(tvConInterno.getText()).append("\r\n");
        builder.append("\r\n");
        builder.append("\r\n");
        builder.append(new char[]{27, '!', 0}); // 10cpp
        builder.append(new char[]{27, 'a', 0}); // Alineacion izquierda
        builder.append(tvFechaDoc.getText()).append(String.format("%15s", tvHoraDoc.getText())).append("\r\n").append("\r\n");


        builder.append("Cliente:").append("\r\n");
        builder.append(new char[]{27, '!', 8}); // 10cpp, negrita
        builder.append(mNomFactura).append("\r\n");
        builder.append(new char[]{27, '!', 0}); // 10cpp
        builder.append(tvIdFactura.getText()).append("\r\n");
        builder.append(tvNomCliente.getText()).append("\r\n");
        if (!tipoFac.equals("01")) {
            builder.append("Venta: Crédito").append("\r\n");
            builder.append("Plazo: 15 dias");
        } else {
            builder.append("Venta: Contado");
        }

        builder.append("\r\n");
        builder.append("Moneda: CRC colon").append("\r\n");
        builder.append("Vendedor: ").append(Variables.NOMBRE_USUARIO).append("\r\n");
        builder.append("\r\n");
        builder.append("===============================\r\n");
        builder.append("Artículo\r\n");
        builder.append(String.format("%-10s", "Cantidad"))
                .append(String.format("%8s", "Descto"))
                .append(String.format("%12s", "Monto")).append("\r\n");
        builder.append("===============================\r\n");

        for (int i = 0; i < arrayListItemFac.size(); i++) {

            builder.append(arrayListItemFac.get(i).getNomItem()).append("\r\n");
            sCan = formatoEntero.format(arrayListItemFac.get(i).getCantidad());
            sDescto = String.valueOf(arrayListItemFac.get(i).getPordescto()).trim() + "%";
            sMon = formatoDecimal.format(arrayListItemFac.get(i).getMonto());
            builder.append(String.format("%-10s", sCan))
                    .append(String.format("%6s", sDescto))
                    .append(String.format("%14s", sMon));
            if (arrayListItemFac.get(i).getExento() > 0) {
                builder.append(" E").append("\r\n");
            } else {
                builder.append("\r\n");
            }

        }

        builder.append("===============================\r\n");
        builder.append(new char[]{27, 'a', 2}); // Alineacion derecha
        sMon = formatoDecimal.format(mTotExe);
        builder.append("Monto Exento").append(String.format("%15s", sMon)).append("\r\n");
        sMon = formatoDecimal.format(mTotGra);
        builder.append("Monto Gravado").append(String.format("%15s", sMon)).append("\r\n");
        sMon = formatoDecimal.format(mSubTotFac);
        builder.append("Sub-total").append(String.format("%15s", sMon)).append("\r\n");
        sMon = formatoDecimal.format(mDesctoFac);
        builder.append("Descuento").append(String.format("%15s", sMon)).append("\r\n");
        sMon = formatoDecimal.format(mImptoFac);
        builder.append("IVA").append(String.format("%15s", sMon)).append("\r\n");
        sMon = formatoDecimal.format(mTotFac);
        builder.append(new char[]{27, '!', 8}); // 10cpp, negrita
        builder.append("Total Facturado").append(String.format("%15s", sMon)).append("\r\n").append("\r\n");
        builder.append(new char[]{27, '!', 0}); // 10cpp
        builder.append(new char[]{27, 'a', 1}); // Alineacion centrada
        builder.append("Clave numérica").append("\r\n");
        builder.append(mClave_FE).append("\r\n");

        if (mNotasFac.length() > 0) {
            builder.append(new char[]{27, 'a', 0}); // Alineacion Izquierda
            builder.append("Observaciones: ").append("\r\n").append(mNotasFac).append("\r\n");
        }

        builder.append(new char[]{27, 'd', 2}); // Avance de lineas
        builder.append(new char[]{27, 'a', 1}); // Alineacion centrada
        builder.append(new char[]{27, '!', 1}); // 12cpp
        builder.append("Emitida segun resolucion DGT-R-33-2019 del 27 junio 2019").append("\r\n");
        builder.append("Version 4.3").append("\r\n").append("\r\n");
        builder.append(new char[]{27, '!', 16}); // doble altura
        builder.append("GRACIAS POR SU COMPRA");

        builder.append(new char[]{27, '!', 0}); // 10 CPP
        builder.append(new char[]{27, 'd', 5}); // Avance de lineas
        return builder.toString();


    }
}

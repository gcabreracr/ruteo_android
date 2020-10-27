package com.seccab.ejecucinruteo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.seccab.ejecucinruteo.metodos.Internet;
import com.seccab.ejecucinruteo.metodos.VolleySingleton;
import com.seccab.ejecucinruteo.modelo.UsuarioVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginUsuario extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {

    EditText txtCodUsuario, txtPassUsuario;
    Button btnAceptar;
    TextView tvNomUsuario, tvNomAgencia, tvNomPDV;
    ProgressBar progressBar;


    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_usuario);

        progressBar=findViewById(R.id.login_progressBar);
        txtCodUsuario = findViewById(R.id.login_edUsuario);
        txtPassUsuario = findViewById(R.id.login_edPass);
        btnAceptar = findViewById(R.id.login_btnAceptar);


    }

    public void btnAceptar(View view) {
        if (!Internet.compruebaConexion(this)) {
            Toast.makeText(this, "Compruebe la conección a internet e intente de nuevo", Toast.LENGTH_SHORT).show();
            return;
        }


        progressBar.setVisibility(View.VISIBLE);

        String ip = getString(R.string.ip);
        String url = ip + "/ejecucionpdv/wsLoginUsuario.php?id_usuario='"+ txtCodUsuario.getText().toString().trim() + "'";

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

    }

    @Override

    public void onErrorResponse(VolleyError error) {
        progressBar.setVisibility(View.GONE);

        Toast.makeText(this, "No se pudo Consultar " + error.toString(), Toast.LENGTH_SHORT).show();
        Log.i("ERROR", error.toString());

    }

    @Override
    public void onResponse(JSONObject response) {
        progressBar.setVisibility(View.GONE);

        JSONArray json = response.optJSONArray("usuario");
        JSONObject jsonObject = null;

        String jsonUsuario = null;
        String jsonPass = null;

        try {
            jsonObject = json.getJSONObject(0);
            jsonUsuario = jsonObject.optString("codigo_usu").trim();
            jsonPass = jsonObject.optString("pass_usu").trim();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (txtCodUsuario.getText().toString().trim().equals(jsonUsuario)) {

            if (txtPassUsuario.getText().toString().trim().equals(jsonPass)) {

                SharedPreferences sharedPreferences = getSharedPreferences("userpref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user", jsonObject.optString("codigo_usu"));
                editor.putString("nomuser", jsonObject.optString("nombre_usu"));
               // editor.putString("dbuser", jsonObject.optString("db_user"));
                editor.putInt("desctoaut", jsonObject.optInt("descto_aut"));
                editor.putInt("codagencia", jsonObject.optInt("cod_agencia"));
                editor.putString("nomagencia", jsonObject.optString("nom_agencia"));
                editor.putInt("codpdv", jsonObject.optInt("cod_pdv"));
                editor.putString("nompdv", jsonObject.optString("nom_pdv"));
                editor.putString("bodega",jsonObject.optString("cod_bodega"));
                editor.putInt("catpdv",jsonObject.optInt("cat_pdv"));
                editor.putInt("tipousu",jsonObject.optInt("tipo_usu"));
                editor.apply();


                Toast.makeText(this, "Usuario Logueado correctamente", Toast.LENGTH_SHORT).show();
                finish();

            } else {
                Toast.makeText(getApplicationContext(), "Password Incorrecto", Toast.LENGTH_SHORT).show();
                txtPassUsuario.requestFocus();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Usuario NO existe", Toast.LENGTH_SHORT).show();
            txtPassUsuario.requestFocus();

        }


    }
}

package com.seccab.ejecucinruteo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.seccab.ejecucinruteo.modelo.Variables;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView tvNomUsuario, tvNomAgencia, tvNomPDV;

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvNomUsuario = findViewById(R.id.main_nomusuario);
        tvNomAgencia = findViewById(R.id.main_nomagencia);
        tvNomPDV = findViewById(R.id.main_nompdv);

        cargarpreferencias();

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



    }

    @Override
    protected void onResume() {

        super.onResume();
        cargarpreferencias();
    }


    private void cargarpreferencias() {
        // Busca los parametros del usuario almacenados en el movil
        SharedPreferences sharedPreferences = getSharedPreferences("userpref", Context.MODE_PRIVATE);
        Variables.CODIGO_USUARIO = sharedPreferences.getString("user", "");
        Variables.NOMBRE_USUARIO = sharedPreferences.getString("nomuser", "Invalido");
       // Variables.DB_USUARIO=sharedPreferences.getString("dbuser","sicca");
        Variables.DESCTO_AUT_USUARIO = sharedPreferences.getInt("desctoaut", 0);
        Variables.COD_AGENCIA = sharedPreferences.getInt("codagencia", 0);
        Variables.NOMBRE_AGENCIA = sharedPreferences.getString("nomagencia", "Invalida");
        Variables.COD_PDV = sharedPreferences.getInt("codpdv", 0);
        Variables.NOMBRE_PDV = sharedPreferences.getString("nompdv", "Invalido");
        Variables.CAT_PDV = sharedPreferences.getInt("catpdv", 0);
        Variables.COD_BODEGA = sharedPreferences.getString("bodega", "");
        Variables.TIPO_USU = sharedPreferences.getInt("tipousu", 0);
        Variables.AVISO_MONTO_VENTA = 500000;
        tvNomUsuario.setText("Usuario: " + Variables.NOMBRE_USUARIO);
        tvNomAgencia.setText("Agencia: " + Variables.NOMBRE_AGENCIA);
        tvNomPDV.setText("PDV: " + Variables.NOMBRE_PDV);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_login) {
            Intent intent = new Intent(MainActivity.this, LoginUsuario.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.cambia_pin) {

            if (!Variables.CODIGO_USUARIO.equals("")) {
                Intent intent = new Intent(MainActivity.this, CambiaPin.class);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "NO existe un usuario seleccionado", Toast.LENGTH_SHORT).show();
            }
            return true;

        } else if (id == R.id.cambia_pdv) {
            if (!Variables.CODIGO_USUARIO.equals("")) {
                Intent intent = new Intent(MainActivity.this, CambiaPDV.class);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "NO existe un usuario seleccionado", Toast.LENGTH_SHORT).show();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        if (Variables.CODIGO_USUARIO != "") {


            Intent intent = null;

            int id = item.getItemId();

            if (id == R.id.nav_clientes) {

                intent = new Intent(MainActivity.this, ActualizaCliente.class);


            } else if (id == R.id.nav_emiteFac) {
                intent = new Intent(MainActivity.this, EmiteFactura.class);


            } else if (id == R.id.nav_reimprimeFac) {
                intent = new Intent(MainActivity.this, ReimprimeFac.class);

            } else if (id == R.id.nav_statusLiq) {
                intent = new Intent(MainActivity.this, StatuLiquida.class);

            } else if (id == R.id.nav_bitacora) {
                intent = new Intent(MainActivity.this, Bitacora.class);

            } else if (id == R.id.nav_ConVisita) {
                intent = new Intent(MainActivity.this, ConsultaVisitaDiaria.class);

            } else if (id == R.id.nav_ConBitacora) {
                intent = new Intent(MainActivity.this, ConsultaBitacoraDiaria.class);
            }

            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Proceso NO disponible", Toast.LENGTH_SHORT).show();

            }


            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else {
            Toast.makeText(this, "Debe ingresar un Usuario", Toast.LENGTH_LONG).show();
            return false;
        }
    }
}

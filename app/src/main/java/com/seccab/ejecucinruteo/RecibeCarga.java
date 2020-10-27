package com.seccab.ejecucinruteo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class RecibeCarga extends AppCompatActivity {

    ListView listViewCarga;
    int monCarIni,monRecarga,monTotRec;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recibe_carga);




    }

    public void eventoRecibir(View view) {

    }

    public void eventoCancelar(View view) {
        finish();
    }
}

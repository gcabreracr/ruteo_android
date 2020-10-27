package com.seccab.ejecucinruteo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class CondicionesFactura extends AppCompatActivity {

    RadioButton rbContado,rbCredito,rbFE,rbTE;
    EditText edPlazo;
    Button btnGuardar,btnCancelar;
    String tipodoc,tipofactura;
    int plazo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_condiciones_factura);

        plazo=0;
        tipodoc="01";
        tipofactura="01";
        rbContado=findViewById(R.id.cf_rdContado);
        rbContado.setChecked(true);
        rbCredito=findViewById(R.id.cf_rbCredito);
        rbTE=findViewById(R.id.cf_rbTE);
        rbFE=findViewById(R.id.cf_rdFE);
        rbFE.setChecked(true);
        edPlazo=findViewById(R.id.cf_etPlazo);
        edPlazo.setEnabled(false);
        edPlazo.setText("0");
        btnGuardar=findViewById(R.id.cf_btnGuardar);
        btnCancelar=findViewById(R.id.cf_btnCancelar);


    }

    public void cf_btnGuardar(View view) {

        plazo=Integer.valueOf(edPlazo.getText().toString());
        Intent i = getIntent();
        i.putExtra("TIPODOC", tipodoc);
        i.putExtra("TIPOFAC", tipofactura);
        i.putExtra("PLAZO",plazo);
        setResult(RESULT_OK, i);
        finish();
    }

    public void cf_btnCancelar(View view) {

        finish();

    }

    public void cf_rbFE(View view) {
        tipodoc="01";
        tipofactura="01";
        rbContado.setChecked(true);
        rbContado.setEnabled(true);
        rbCredito.setEnabled(true);
    }

    public void cf_rbTE(View view) {
        tipodoc="04";
        tipofactura="01";
        edPlazo.setText("0");
        edPlazo.setEnabled(false);
        rbContado.setChecked(true);
        rbContado.setEnabled(false);
        rbCredito.setEnabled(false);
    }

    public void cf_rbContado(View view) {
        tipofactura="01";
        edPlazo.setText("0");
        edPlazo.setEnabled(false);
    }

    public void cf_rbCredito(View view) {
        tipofactura="02";
        edPlazo.setEnabled(true);
    }
}

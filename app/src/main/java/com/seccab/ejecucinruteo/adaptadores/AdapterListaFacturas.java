package com.seccab.ejecucinruteo.adaptadores;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seccab.ejecucinruteo.R;
import com.seccab.ejecucinruteo.modelo.ListaFacturaVO;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AdapterListaFacturas extends RecyclerView.Adapter<AdapterListaFacturas.ViewHolderListaFacturas> implements View.OnClickListener {

    private View.OnClickListener listener;
    private ArrayList<ListaFacturaVO> arrayListaFacturas;

    public AdapterListaFacturas(ArrayList<ListaFacturaVO> listaFacturasArray) {
        this.arrayListaFacturas = listaFacturasArray;
    }

    @NonNull
    @Override
    public ViewHolderListaFacturas onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista =LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_factura,null,false);
        vista.setOnClickListener(this);
        return new ViewHolderListaFacturas(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderListaFacturas holder, int position) {
        DecimalFormat formatoDecimal=new DecimalFormat("####,###.00");
       // String.format("%010d",1234567);
        String sMontoFactura=formatoDecimal.format(arrayListaFacturas.get(position).getMonFactura());
        int numfactura= arrayListaFacturas.get(position).getNumFactura();
        @SuppressLint("DefaultLocale") String sNumFactura=String.format("%012d",numfactura);

        holder.tvNumFac.setText(sNumFactura);
        holder.tvMontoFac.setText(sMontoFactura);
        holder.tvNegocio.setText(arrayListaFacturas.get(position).getNomNegocio());

    }

    @Override
    public int getItemCount() {
        return arrayListaFacturas.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener=listener;
    }

    @Override
    public void onClick(View v) {
        if(listener!=null){
            listener.onClick(v);
        }

    }


    public class ViewHolderListaFacturas extends RecyclerView.ViewHolder {
        TextView tvNumFac, tvMontoFac, tvNegocio;

        public ViewHolderListaFacturas(View itemView) {
            super(itemView);
            tvNumFac = itemView.findViewById(R.id.lf_numFact);
            tvMontoFac = itemView.findViewById(R.id.lf_montoFact);
            tvNegocio = itemView.findViewById(R.id.lf_nombreNegocio);
        }
    }
}






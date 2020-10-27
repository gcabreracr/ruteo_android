package com.seccab.ejecucinruteo.adaptadores;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seccab.ejecucinruteo.R;
import com.seccab.ejecucinruteo.modelo.VentaDiariaVO;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AdapterVentaDiaria extends RecyclerView.Adapter<AdapterVentaDiaria.ViewHolderVentaDiaria>{

    private ArrayList<VentaDiariaVO> ventaDiariaArrayList;

    public AdapterVentaDiaria(ArrayList<VentaDiariaVO> ventaDiariaVOArrayList) {
        this.ventaDiariaArrayList = ventaDiariaVOArrayList;
    }

    @NonNull
    @Override
    public ViewHolderVentaDiaria onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista =LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_status_liq,null,false);

        return new ViewHolderVentaDiaria(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderVentaDiaria holder, int position) {
        DecimalFormat formatoDecimal=new DecimalFormat("####,###.00");
        DecimalFormat formatoEntero=new DecimalFormat("####,##0");

        String sCargaTotal=formatoEntero.format(ventaDiariaArrayList.get(position).getCarga());
        String sCantVenta=formatoEntero.format(ventaDiariaArrayList.get(position).getCant_venta());
        String sMontoLiq=formatoDecimal.format(ventaDiariaArrayList.get(position).getMonto_liq());

        holder.tvNomArt.setText(ventaDiariaArrayList.get(position).getNomArticulo());
        holder.tvCargaTotal.setText(sCargaTotal);
        holder.tvCantVenta.setText(sCantVenta);
        holder.tvMonLiq.setText(sMontoLiq);

    }

    @Override
    public int getItemCount() {
        return ventaDiariaArrayList.size();
    }


    public class ViewHolderVentaDiaria extends RecyclerView.ViewHolder {
        TextView tvNomArt,tvCargaTotal,tvCantVenta,tvCantSobrante,tvMonLiq;

        public ViewHolderVentaDiaria(View itemView) {
            super(itemView);
            tvNomArt=itemView.findViewById(R.id.lsl_nom_art);
            tvCargaTotal=itemView.findViewById(R.id.lsl_Cant_carga);
            tvCantVenta=itemView.findViewById(R.id.lsl_Cant_venta);
            tvMonLiq=itemView.findViewById(R.id.lsl_MontoLiq);

        }
    }
}

package com.seccab.ejecucinruteo.adaptadores;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seccab.ejecucinruteo.R;
import com.seccab.ejecucinruteo.modelo.ListaBitacoraVO;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AdapterConsultaBitacora extends RecyclerView.Adapter<AdapterConsultaBitacora.ViewHolderBitacora> {

    private ArrayList<ListaBitacoraVO> arrayListBitacora;

    public AdapterConsultaBitacora(ArrayList<ListaBitacoraVO> arrayListBitacora) {
        this.arrayListBitacora = arrayListBitacora;
    }

    @NonNull
    @Override
    public ViewHolderBitacora onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista =LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_consulta_bitacora,null,false);
        return new ViewHolderBitacora(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderBitacora holder, int position) {
        DecimalFormat formatoDecimal=new DecimalFormat("#####,###.00");
        String sMontoCompra=formatoDecimal.format(arrayListBitacora.get(position).getMon_compra());

        holder.tvMonCompra.setText("Monto Compra: "+sMontoCompra);
        holder.tvHoraBit.setText(arrayListBitacora.get(position).getHora_bit());
        holder.tvMotNOCompra.setText(arrayListBitacora.get(position).getMot_nocompra());
        holder.tvNomCliente.setText(arrayListBitacora.get(position).getNom_cliente());

    }

    @Override
    public int getItemCount() {
        return arrayListBitacora.size();
    }

    public class ViewHolderBitacora extends RecyclerView.ViewHolder {
        TextView tvNomCliente,tvHoraBit,tvMonCompra,tvMotNOCompra;

        public ViewHolderBitacora(View itemView) {
            super(itemView);
            tvNomCliente=itemView.findViewById(R.id.lcb_nomcli);
            tvHoraBit=itemView.findViewById(R.id.lcb_horabit);
            tvMonCompra=itemView.findViewById(R.id.lcb_monCompra);
            tvMotNOCompra=itemView.findViewById(R.id.lcb_motNOcompra);
        }
    }
}

package com.seccab.ejecucinruteo.adaptadores;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seccab.ejecucinruteo.R;
import com.seccab.ejecucinruteo.modelo.ListaVisitaVO;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AdapterListaVisita extends RecyclerView.Adapter<AdapterListaVisita.ViewHolderListaVisita> {

    private ArrayList<ListaVisitaVO> arrayListVisita;

    public AdapterListaVisita(ArrayList<ListaVisitaVO> arrayListVisita) {
        this.arrayListVisita=arrayListVisita;
    }


    @NonNull
    @Override
    public ViewHolderListaVisita onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista =LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_consulta_visita,null,false);
        return new ViewHolderListaVisita(vista);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderListaVisita holder, int position) {
        DecimalFormat formatoDecimal=new DecimalFormat("###.00");
        String sNumSecuencia=formatoDecimal.format(arrayListVisita.get(position).getSecuencia());
        holder.tvSecuencia.setText(sNumSecuencia);
        holder.tvNombre.setText(arrayListVisita.get(position).getNom_cliente());
        holder.tvDireccion.setText(arrayListVisita.get(position).getDireccion());
    }

    @Override
    public int getItemCount() {
        return arrayListVisita.size();
    }


    public class ViewHolderListaVisita extends RecyclerView.ViewHolder {
        TextView tvSecuencia,tvNombre,tvDireccion;

        public ViewHolderListaVisita(View itemView) {
            super(itemView);
            tvSecuencia=itemView.findViewById(R.id.lcv_secuencia);
            tvNombre=itemView.findViewById(R.id.lcv_nomcli);
            tvDireccion=itemView.findViewById(R.id.lcv_direccion);

        }
    }
}


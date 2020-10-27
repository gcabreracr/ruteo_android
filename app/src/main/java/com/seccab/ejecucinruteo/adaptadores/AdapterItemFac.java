package com.seccab.ejecucinruteo.adaptadores;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seccab.ejecucinruteo.R;
import com.seccab.ejecucinruteo.modelo.ItemListaFacVo;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AdapterItemFac extends RecyclerView.Adapter<AdapterItemFac.ViewHolderItemFac> {

    ArrayList<ItemListaFacVo> itemListaFacVo;

    public AdapterItemFac(ArrayList<ItemListaFacVo> itemListaFacVo) {
        this.itemListaFacVo = itemListaFacVo;
    }

    @NonNull
    @Override
    public ViewHolderItemFac onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_items_fac,null,false);
        return new ViewHolderItemFac(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderItemFac holder, int position) {
        DecimalFormat fdecimal=new DecimalFormat("####,###,###.00");
        DecimalFormat fentero=new DecimalFormat("####,###");
        holder.tvNomItem.setText(itemListaFacVo.get(position).getNomItem());
        holder.tvCantidad.setText(fentero.format(itemListaFacVo.get(position).getCantidad()));
        holder.tvMonto.setText(fdecimal.format(itemListaFacVo.get(position).getMonto()));

    }

    @Override
    public int getItemCount() {
        return itemListaFacVo.size();
    }

    public class ViewHolderItemFac extends RecyclerView.ViewHolder {

        TextView tvNomItem,tvCantidad,tvMonto;

        public ViewHolderItemFac(View itemView) {
            super(itemView);
            tvNomItem=itemView.findViewById(R.id.nomitem_lista);
            tvCantidad=itemView.findViewById(R.id.cantidad_lista);
            tvMonto=itemView.findViewById(R.id.monto_lista);
        }
    }
}

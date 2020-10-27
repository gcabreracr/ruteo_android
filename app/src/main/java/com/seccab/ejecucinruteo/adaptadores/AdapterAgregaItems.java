package com.seccab.ejecucinruteo.adaptadores;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.seccab.ejecucinruteo.R;
import com.seccab.ejecucinruteo.modelo.ItemListaVo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AdapterAgregaItems extends RecyclerView.Adapter<AdapterAgregaItems.ViewHolderListaItems>
                                implements View.OnClickListener, Filterable {

    private View.OnClickListener listener;
    private ArrayList<ItemListaVo> itemListaVos;
    private ArrayList<ItemListaVo> itemListaFull;

    public AdapterAgregaItems(ArrayList<ItemListaVo> itemListaVos) {
        this.itemListaVos = itemListaVos;
        itemListaFull=new ArrayList<>(itemListaVos);
    }

    @NonNull
    @Override
    public ViewHolderListaItems onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista =LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_agregar_items,null,false);
        vista.setOnClickListener(this);
        return new ViewHolderListaItems(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderListaItems holder, int position) {
        DecimalFormat formato=new DecimalFormat("####,###.00");
        int mExento= itemListaVos.get(position).getExento();
        double mPrecio= itemListaVos.get(position).getPrecioArt();
        double precioventa;
        String sPrecio="";

        if (mExento>0){
            precioventa= mPrecio;
            sPrecio="Precio: "+formato.format(precioventa)+"/ "+itemListaVos.get(position).getMedida();
        }else{

            int mPorImpto= itemListaVos.get(position).getPorImpto();
            double mMonImpto=mPrecio*mPorImpto/100;
            precioventa= mPrecio+mMonImpto;
            sPrecio="Precio: "+formato.format(precioventa)+"/ "+itemListaVos.get(position).getMedida()+" IVI";

        }
        holder.precioArt.setText(sPrecio);
        holder.nomArt.setText(itemListaVos.get(position).getNomCortoArt());
    }

    @Override
    public int getItemCount() {
        return itemListaVos.size();
    }

   public void setOnClickListener(View.OnClickListener listener){
        this.listener=listener;
   };

    @Override
    public void onClick(View v) {
        if (listener!=null){
            listener.onClick(v);
        }

    }

    @Override
    public Filter getFilter() {
        return itemListaFilter;
    }

    private Filter itemListaFilter= new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ItemListaVo> filteredList = new ArrayList<>();
            if (constraint==null || constraint.length()==0){
                filteredList.addAll(itemListaFull);
            }else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ItemListaVo item:itemListaFull) {
                    if(item.getNomCortoArt().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results =new FilterResults();
            results.values=filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            itemListaVos.clear();
            itemListaVos.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


    public class ViewHolderListaItems extends RecyclerView.ViewHolder {

        TextView nomArt,precioArt;

        public ViewHolderListaItems(View itemView) {
            super(itemView);
            nomArt=itemView.findViewById(R.id.ai_tvNomArt);
            precioArt=itemView.findViewById(R.id.ai_tvPrecio);
        }
    }
}

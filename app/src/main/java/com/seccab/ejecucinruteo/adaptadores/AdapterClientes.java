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
import com.seccab.ejecucinruteo.modelo.ClienteListaVo;
import com.seccab.ejecucinruteo.modelo.ItemListaVo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AdapterClientes extends RecyclerView.Adapter<AdapterClientes.ViewHolderListaClientes>
            implements View.OnClickListener, Filterable {

    private ArrayList<ClienteListaVo> clienteListaVo;
    private ArrayList<ClienteListaVo> clienteListaFull;
    private View.OnClickListener listener;

    public AdapterClientes(ArrayList<ClienteListaVo> clienteListaVo){
        this.clienteListaVo = clienteListaVo;
        clienteListaFull= new ArrayList<>(clienteListaVo);
    }


    @NonNull
    @Override
    public ViewHolderListaClientes onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista =LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_clientes,null,false);

        vista.setOnClickListener(this);

        return new ViewHolderListaClientes(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderListaClientes holder, int position) {
        holder.tvNomNegocio.setText(clienteListaVo.get(position).getNomcli());
        holder.tvDireccion.setText(clienteListaVo.get(position).getDireccion());
    }

    @Override
    public int getItemCount() {
        return clienteListaVo.size();
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

    @Override
    public Filter getFilter() {
        return clienteListaFilter;
    }
    private Filter clienteListaFilter= new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ClienteListaVo> filteredList = new ArrayList<>();
            if (constraint==null || constraint.length()==0){
                filteredList.addAll(clienteListaFull);
            }else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ClienteListaVo cliente:clienteListaFull) {
                    if(cliente.getNomcli().toLowerCase().contains(filterPattern)){
                        filteredList.add(cliente);
                    }
                }
            }
            FilterResults results =new FilterResults();
            results.values=filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clienteListaVo.clear();
            clienteListaVo.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };




    public class ViewHolderListaClientes extends RecyclerView.ViewHolder {

        TextView tvNomNegocio,tvDireccion;


        public ViewHolderListaClientes(View itemView) {
            super(itemView);

            tvNomNegocio=itemView.findViewById(R.id.tv_liscli_nomcli);
            tvDireccion=itemView.findViewById(R.id.tv_liscli_direccion);

        }
    }
}

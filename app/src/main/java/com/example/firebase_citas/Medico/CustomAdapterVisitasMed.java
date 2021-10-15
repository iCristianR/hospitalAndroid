package com.example.firebase_citas.Medico;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.firebase_citas.Modelo.Visita;
import com.example.firebase_citas.R;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapterVisitasMed extends BaseAdapter implements Filterable {
    private Context context;
    private List<Visita> listaVisita;
    private List<Visita> listaVisitaFiltrada;
    private CustomFilter customFilter;

    public CustomAdapterVisitasMed(Context context, List<Visita> listaVisita) {
        this.context = context;
        this.listaVisita = listaVisita;
        this.listaVisitaFiltrada = listaVisita;
    }

    @Override
    public int getCount() {
        return listaVisita.size();
    }

    @Override
    public Object getItem(int i) {
        return listaVisita.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView tvId;
        TextView tvPaciente;
        TextView tvFecha;
        TextView tvHora;
        TextView tvEstado;
        ImageView imageView;
        Visita objV = listaVisita.get(i);

        if(view==null){
            view = LayoutInflater.from(context).inflate(R.layout.listview,null);
        }

        tvId=view.findViewById(R.id.LV_tv1);
        tvPaciente=view.findViewById(R.id.LV_tv2);
        tvFecha=view.findViewById(R.id.LV_tv3);
        tvHora=view.findViewById(R.id.LV_tv4);
        tvEstado=view.findViewById(R.id.LV_tv5);
        imageView=view.findViewById(R.id.LV_ImagenView);

        tvId.setText(objV.getId());
        tvPaciente.setText(objV.getPaciente().getNombre()+" "+objV.getPaciente().getApellido());
        tvFecha.setText(objV.getFecha());
        tvHora.setText(objV.getHora());
        tvEstado.setText(objV.getEstado());
        imageView.setImageResource(R.drawable.visita);

        return view;
    }

    @Override
    public Filter getFilter() {
        if (customFilter == null) {
            customFilter = new CustomFilter();
        }
        return customFilter;
    }

    class CustomFilter extends Filter{
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults resultadosFiltrados = new FilterResults();
            if(constraint!=null && constraint.length() > 0){
                constraint = constraint.toString().toUpperCase();
                List<Visita> filtros = new ArrayList<Visita>();

                for(int i = 0; i< listaVisitaFiltrada.size(); i++){
                    if(listaVisitaFiltrada.get(i).getId().toUpperCase().contains(constraint) ||
                            listaVisitaFiltrada.get(i).getPaciente().getNombre().toUpperCase().contains(constraint) ||
                            listaVisitaFiltrada.get(i).getPaciente().getApellido().toUpperCase().contains(constraint) ||
                            listaVisitaFiltrada.get(i).getFecha().toUpperCase().contains(constraint) ||
                            listaVisitaFiltrada.get(i).getHora().toUpperCase().contains(constraint) ||
                            listaVisitaFiltrada.get(i).getEstado().toUpperCase().contains(constraint)
                    ){
                        Visita objV = new Visita(
                                listaVisitaFiltrada.get(i).getId(),
                                listaVisitaFiltrada.get(i).getPaciente(),
                                listaVisitaFiltrada.get(i).getMedico(),
                                listaVisitaFiltrada.get(i).getFecha(),
                                listaVisitaFiltrada.get(i).getHora(),
                                listaVisitaFiltrada.get(i).getEstado()
                        );
                        filtros.add(objV);
                    }
                }
                resultadosFiltrados.count = filtros.size();
                resultadosFiltrados.values = filtros;
            }else{
                resultadosFiltrados.count = listaVisitaFiltrada.size();
                resultadosFiltrados.values = listaVisitaFiltrada;
            }
            return resultadosFiltrados;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listaVisita = (List<Visita>) results.values;
            notifyDataSetChanged();
        }
    }

}

package com.example.firebase_citas.Admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.firebase_citas.Modelo.Especialidad;
import com.example.firebase_citas.R;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapterEspecialidad extends BaseAdapter implements Filterable {
    private Context context;
    private List<Especialidad> listaEspecialidad;
    private List<Especialidad> listaEspecialidadFiltrada;
    private CustomFilter customFilter;


    public CustomAdapterEspecialidad(Context context, List<Especialidad> listaEspecialidad) {
        this.context = context;
        this.listaEspecialidad = listaEspecialidad;
        this.listaEspecialidadFiltrada = listaEspecialidad;
    }

    @Override
    public int getCount() {
        return listaEspecialidad.size();
    }

    @Override
    public Object getItem(int i) {
        return listaEspecialidad.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView tvId;
        TextView tvNombre;
        TextView tvOtro;
        TextView tvOtro1;
        TextView tvOtro2;
        ImageView imageView;
        Especialidad objE = listaEspecialidad.get(i);

        if(view==null){
            view = LayoutInflater.from(context).inflate(R.layout.listview,null);
        }

        tvId=view.findViewById(R.id.LV_tv1);
        tvNombre=view.findViewById(R.id.LV_tv2);
        tvOtro=view.findViewById(R.id.LV_tv3);
        tvOtro1=view.findViewById(R.id.LV_tv4);
        tvOtro2=view.findViewById(R.id.LV_tv5);
        imageView=view.findViewById(R.id.LV_ImagenView);

        tvId.setText(objE.getId());
        tvNombre.setText(objE.getNombre());
        tvOtro.setVisibility(View.GONE);
        tvOtro1.setVisibility(View.GONE);
        tvOtro2.setVisibility(View.GONE);
        imageView.setImageResource(R.drawable.especialidades);

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
                List<Especialidad> filtros = new ArrayList<Especialidad>();

                for(int i=0;i<listaEspecialidadFiltrada.size();i++){
                    if(listaEspecialidadFiltrada.get(i).getId().toUpperCase().contains(constraint) ||
                            listaEspecialidadFiltrada.get(i).getNombre().toUpperCase().contains(constraint)
                    ){
                        Especialidad objE = new Especialidad(
                                listaEspecialidadFiltrada.get(i).getId(),
                                listaEspecialidadFiltrada.get(i).getNombre()
                        );
                        filtros.add(objE);
                    }
                }
                resultadosFiltrados.count = filtros.size();
                resultadosFiltrados.values = filtros;
            }else{
                resultadosFiltrados.count = listaEspecialidadFiltrada.size();
                resultadosFiltrados.values = listaEspecialidadFiltrada;
            }
            return resultadosFiltrados;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listaEspecialidad = (List<Especialidad>) results.values;
            notifyDataSetChanged();
        }
    }

}

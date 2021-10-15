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

import com.example.firebase_citas.Modelo.Diagnostico;
import com.example.firebase_citas.R;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapterDiagnostico extends BaseAdapter implements Filterable {
    private Context context;
    private List<Diagnostico> listaDiagnostico;
    private List<Diagnostico> listaDiagnosticoFiltrada;
    private CustomFilter customFilter;

    public CustomAdapterDiagnostico(Context context, List<Diagnostico> listaDiagnostico) {
        this.context = context;
        this.listaDiagnostico = listaDiagnostico;
        this.listaDiagnosticoFiltrada = listaDiagnostico;
    }

    @Override
    public int getCount() {
        return listaDiagnostico.size();
    }

    @Override
    public Object getItem(int i) {
        return listaDiagnostico.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView tvId;
        TextView tvFecha;
        TextView tvCedula;
        TextView tvPaciente;
        TextView tvEspecialidad;
        ImageView imageView;
        Diagnostico objA = listaDiagnostico.get(i);

        if(view==null){
            view = LayoutInflater.from(context).inflate(R.layout.listview,null);
        }

        tvId=view.findViewById(R.id.LV_tv1);
        tvFecha=view.findViewById(R.id.LV_tv2);
        tvCedula=view.findViewById(R.id.LV_tv3);
        tvPaciente=view.findViewById(R.id.LV_tv4);
        tvEspecialidad=view.findViewById(R.id.LV_tv5);
        imageView=view.findViewById(R.id.LV_ImagenView);

        tvId.setText(objA.getId());
        tvFecha.setText(objA.getVisita().getFecha());
        tvCedula.setText(objA.getVisita().getPaciente().getCedula());
        tvPaciente.setText(objA.getVisita().getPaciente().getNombre()+" "+objA.getVisita().getPaciente().getApellido());
        tvEspecialidad.setText(objA.getVisita().getMedico().getEspecialidad().getNombre());
        imageView.setImageResource(R.drawable.historiaclinica);

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
                List<Diagnostico> filtros = new ArrayList<Diagnostico>();

                for(int i = 0; i< listaDiagnosticoFiltrada.size(); i++){
                    if(listaDiagnosticoFiltrada.get(i).getId().toUpperCase().contains(constraint) ||
                            listaDiagnosticoFiltrada.get(i).getVisita().getPaciente().getCedula().toUpperCase().contains(constraint) ||
                            listaDiagnosticoFiltrada.get(i).getVisita().getPaciente().getNombre().toUpperCase().contains(constraint) ||
                            listaDiagnosticoFiltrada.get(i).getVisita().getPaciente().getApellido().toUpperCase().contains(constraint) ||
                            listaDiagnosticoFiltrada.get(i).getVisita().getMedico().getEspecialidad().getNombre().toUpperCase().contains(constraint)
                    ){
                        Diagnostico objA = new Diagnostico(
                                listaDiagnosticoFiltrada.get(i).getId(),
                                listaDiagnosticoFiltrada.get(i).getVisita(),
                                listaDiagnosticoFiltrada.get(i).getHc(),
                                listaDiagnosticoFiltrada.get(i).getMedicamentos(),
                                listaDiagnosticoFiltrada.get(i).getObservaciones()
                        );
                        filtros.add(objA);
                    }
                }
                resultadosFiltrados.count = filtros.size();
                resultadosFiltrados.values = filtros;
            }else{
                resultadosFiltrados.count = listaDiagnosticoFiltrada.size();
                resultadosFiltrados.values = listaDiagnosticoFiltrada;
            }
            return resultadosFiltrados;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listaDiagnostico = (List<Diagnostico>) results.values;
            notifyDataSetChanged();
        }
    }


}

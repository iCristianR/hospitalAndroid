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

import com.example.firebase_citas.Modelo.HistoriaClinica;
import com.example.firebase_citas.R;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapterHC extends BaseAdapter implements Filterable {
    private Context context;
    private List<HistoriaClinica> listaHC;
    private List<HistoriaClinica> listaHCFiltrada;
    private CustomFilter customFilter;

    public CustomAdapterHC(Context context, List<HistoriaClinica> listaHC) {
        this.context = context;
        this.listaHC = listaHC;
        this.listaHCFiltrada = listaHC;
    }

    @Override
    public int getCount() {
        return listaHC.size();
    }

    @Override
    public Object getItem(int i) {
        return listaHC.get(i);
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
        TextView tvNombreApellido;
        TextView tvOtro;
        ImageView imageView;
        HistoriaClinica objHC = listaHC.get(i);

        if(view==null){
            view = LayoutInflater.from(context).inflate(R.layout.listview,null);
        }

        tvId=view.findViewById(R.id.LV_tv1);
        tvFecha=view.findViewById(R.id.LV_tv2);
        tvCedula=view.findViewById(R.id.LV_tv3);
        tvNombreApellido=view.findViewById(R.id.LV_tv4);
        tvOtro=view.findViewById(R.id.LV_tv5);
        imageView=view.findViewById(R.id.LV_ImagenView);

        tvId.setText(objHC.getId());
        tvFecha.setText(objHC.getFecha_apertura());
        tvCedula.setText(objHC.getPaciente().getCedula());
        tvNombreApellido.setText(objHC.getPaciente().getNombre()+" "+objHC.getPaciente().getApellido());
        tvOtro.setVisibility(View.GONE);
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
                List<HistoriaClinica> filtros = new ArrayList<HistoriaClinica>();

                for(int i=0;i<listaHCFiltrada.size();i++){
                    if(listaHCFiltrada.get(i).getId().toUpperCase().contains(constraint) ||
                            listaHCFiltrada.get(i).getFecha_apertura().toUpperCase().contains(constraint) ||
                            listaHCFiltrada.get(i).getPaciente().getCedula().toUpperCase().contains(constraint) ||
                            listaHCFiltrada.get(i).getPaciente().getNombre().toUpperCase().contains(constraint) ||
                            listaHCFiltrada.get(i).getPaciente().getApellido().toUpperCase().contains(constraint)
                    ){
                        HistoriaClinica objHC = new HistoriaClinica(
                                listaHCFiltrada.get(i).getId(),
                                listaHCFiltrada.get(i).getFecha_apertura(),
                                listaHCFiltrada.get(i).getPaciente(),
                                listaHCFiltrada.get(i).getEstatura(),
                                listaHCFiltrada.get(i).getPeso(),
                                listaHCFiltrada.get(i).getRh(),
                                listaHCFiltrada.get(i).getEnfermedades()
                        );
                        filtros.add(objHC);
                    }
                }
                resultadosFiltrados.count = filtros.size();
                resultadosFiltrados.values = filtros;
            }else{
                resultadosFiltrados.count = listaHCFiltrada.size();
                resultadosFiltrados.values = listaHCFiltrada;
            }
            return resultadosFiltrados;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listaHC = (List<HistoriaClinica>) results.values;
            notifyDataSetChanged();
        }
    }
}

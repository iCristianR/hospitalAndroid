package com.example.firebase_citas.Paciente;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.firebase_citas.Admin.CustomAdapterMedico;
import com.example.firebase_citas.Modelo.Medico;
import com.example.firebase_citas.R;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapterEspecialidades extends BaseAdapter implements Filterable {
    private Context context;
    private List<Medico> listaMedico;
    private List<Medico> listaMedicoFiltrada;
    private CustomFilter customFilter;

    public CustomAdapterEspecialidades(Context context, List<Medico> listaMedico) {
        this.context = context;
        this.listaMedico = listaMedico;
        this.listaMedicoFiltrada = listaMedico;
    }

    @Override
    public int getCount() {
        return listaMedico.size();
    }

    @Override
    public Object getItem(int i) {
        return listaMedico.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView tvEspecialidad;
        TextView tvNombreApellido;
        TextView tvOtro;
        TextView tvOtro1;
        TextView tvOtro2;
        ImageView imageView;
        Medico objM = listaMedico.get(i);

        if(view==null){
            view = LayoutInflater.from(context).inflate(R.layout.listview,null);
        }

        tvEspecialidad=view.findViewById(R.id.LV_tv1);
        tvNombreApellido=view.findViewById(R.id.LV_tv2);
        tvOtro=view.findViewById(R.id.LV_tv3);
        tvOtro1=view.findViewById(R.id.LV_tv4);
        tvOtro2=view.findViewById(R.id.LV_tv5);
        imageView=view.findViewById(R.id.LV_ImagenView);

        tvEspecialidad.setText(objM.getEspecialidad().getNombre());
        tvNombreApellido.setText(objM.getNombre()+" "+objM.getApellido());
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
                List<Medico> filtros = new ArrayList<Medico>();

                for(int i=0;i<listaMedicoFiltrada.size();i++){
                    if(listaMedicoFiltrada.get(i).getNombre().toUpperCase().contains(constraint) ||
                            listaMedicoFiltrada.get(i).getApellido().toUpperCase().contains(constraint) ||
                            listaMedicoFiltrada.get(i).getEspecialidad().getNombre().toUpperCase().contains(constraint)
                    ){
                        Medico objM = new Medico(
                                listaMedicoFiltrada.get(i).getCedula(),
                                listaMedicoFiltrada.get(i).getNombre(),
                                listaMedicoFiltrada.get(i).getApellido(),
                                listaMedicoFiltrada.get(i).getEdad(),
                                listaMedicoFiltrada.get(i).getCorreo(),
                                listaMedicoFiltrada.get(i).getTelefono(),
                                listaMedicoFiltrada.get(i).getContra(),
                                listaMedicoFiltrada.get(i).getPregSeguridad(),
                                listaMedicoFiltrada.get(i).getRespuesta(),
                                listaMedicoFiltrada.get(i).getEspecialidad()
                        );
                        filtros.add(objM);
                    }
                }
                resultadosFiltrados.count = filtros.size();
                resultadosFiltrados.values = filtros;
            }else{
                resultadosFiltrados.count = listaMedicoFiltrada.size();
                resultadosFiltrados.values = listaMedicoFiltrada;
            }
            return resultadosFiltrados;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listaMedico = (List<Medico>) results.values;
            notifyDataSetChanged();
        }
    }


}

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

import com.example.firebase_citas.Modelo.Paciente;
import com.example.firebase_citas.R;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapterPaciente extends BaseAdapter implements Filterable {
    private Context context;
    private List<Paciente> listaPaciente;
    private List<Paciente> listaPacienteFiltrada;
    private CustomFilter customFilter;


    public CustomAdapterPaciente(Context context, List<Paciente> listaPaciente) {
        this.context = context;
        this.listaPaciente = listaPaciente;
        this.listaPacienteFiltrada = listaPaciente;
    }

    @Override
    public int getCount() {
        return listaPaciente.size();
    }

    @Override
    public Object getItem(int i) {
        return listaPaciente.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView tvCedula;
        TextView tvNombreApellido;
        TextView tvEdad;
        TextView tvOtro;
        TextView tvOtro1;
        ImageView imageView;
        Paciente objP = listaPaciente.get(i);

        if(view==null){
            view = LayoutInflater.from(context).inflate(R.layout.listview,null);
        }

        tvCedula=view.findViewById(R.id.LV_tv1);
        tvNombreApellido=view.findViewById(R.id.LV_tv2);
        tvEdad=view.findViewById(R.id.LV_tv3);
        tvOtro=view.findViewById(R.id.LV_tv4);
        tvOtro1=view.findViewById(R.id.LV_tv5);
        imageView=view.findViewById(R.id.LV_ImagenView);

        tvCedula.setText(objP.getCedula());
        tvNombreApellido.setText(objP.getNombre()+" "+objP.getApellido());
        tvEdad.setText(objP.getEdad());
        tvOtro.setVisibility(View.GONE);
        tvOtro1.setVisibility(View.GONE);
        imageView.setImageResource(R.drawable.perfil);

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
                List<Paciente> filtros = new ArrayList<Paciente>();

                for(int i=0;i<listaPacienteFiltrada.size();i++){
                    if(listaPacienteFiltrada.get(i).getCedula().toUpperCase().contains(constraint) ||
                            listaPacienteFiltrada.get(i).getNombre().toUpperCase().contains(constraint) ||
                            listaPacienteFiltrada.get(i).getApellido().toUpperCase().contains(constraint) ||
                            listaPacienteFiltrada.get(i).getEdad().toUpperCase().contains(constraint)
                    ){
                        Paciente objP = new Paciente(
                                listaPacienteFiltrada.get(i).getCedula(),
                                listaPacienteFiltrada.get(i).getNombre(),
                                listaPacienteFiltrada.get(i).getApellido(),
                                listaPacienteFiltrada.get(i).getEdad(),
                                listaPacienteFiltrada.get(i).getCorreo(),
                                listaPacienteFiltrada.get(i).getTelefono(),
                                listaPacienteFiltrada.get(i).getContra(),
                                listaPacienteFiltrada.get(i).getPregSeguridad(),
                                listaPacienteFiltrada.get(i).getRespuesta()
                        );
                        filtros.add(objP);
                    }
                }
                resultadosFiltrados.count = filtros.size();
                resultadosFiltrados.values = filtros;
            }else{
                resultadosFiltrados.count = listaPacienteFiltrada.size();
                resultadosFiltrados.values = listaPacienteFiltrada;
            }
            return resultadosFiltrados;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listaPaciente = (List<Paciente>) results.values;
            notifyDataSetChanged();
        }
    }

}

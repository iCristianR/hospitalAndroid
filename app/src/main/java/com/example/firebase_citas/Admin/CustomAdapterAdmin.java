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

import com.example.firebase_citas.Modelo.Admin;
import com.example.firebase_citas.R;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapterAdmin extends BaseAdapter implements Filterable {
    private Context context;
    private List<Admin> listaAdmin;
    private List<Admin> listaAdminFiltrada;
    private CustomFilter customFilter;

    public CustomAdapterAdmin(Context context, List<Admin> listaAdmin) {
        this.context = context;
        this.listaAdmin = listaAdmin;
        this.listaAdminFiltrada = listaAdmin;
    }

    @Override
    public int getCount() {
        return listaAdmin.size();
    }

    @Override
    public Object getItem(int i) {
        return listaAdmin.get(i);
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
        Admin objA = listaAdmin.get(i);

        if(view==null){
            view = LayoutInflater.from(context).inflate(R.layout.listview,null);
        }

        tvCedula=view.findViewById(R.id.LV_tv1);
        tvNombreApellido=view.findViewById(R.id.LV_tv2);
        tvEdad=view.findViewById(R.id.LV_tv3);
        tvOtro=view.findViewById(R.id.LV_tv4);
        tvOtro1=view.findViewById(R.id.LV_tv5);
        imageView=view.findViewById(R.id.LV_ImagenView);

        tvCedula.setText(objA.getCedula());
        tvNombreApellido.setText(objA.getNombre()+" "+objA.getApellido());
        tvEdad.setText(objA.getEdad());
        tvOtro.setVisibility(View.GONE);
        tvOtro1.setVisibility(View.GONE);
        imageView.setImageResource(R.drawable.admin);

        return view;
    }

    @Override
    public Filter getFilter() {
        if (customFilter == null) {
            customFilter = new CustomAdapterAdmin.CustomFilter();
        }
        return customFilter;
    }

    class CustomFilter extends Filter{
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults resultadosFiltrados = new FilterResults();
            if(constraint!=null && constraint.length() > 0){
                constraint = constraint.toString().toUpperCase();
                List<Admin> filtros = new ArrayList<Admin>();

                for(int i=0;i<listaAdminFiltrada.size();i++){
                    if(listaAdminFiltrada.get(i).getCedula().toUpperCase().contains(constraint) ||
                            listaAdminFiltrada.get(i).getNombre().toUpperCase().contains(constraint) ||
                            listaAdminFiltrada.get(i).getApellido().toUpperCase().contains(constraint) ||
                            listaAdminFiltrada.get(i).getEdad().toUpperCase().contains(constraint)
                    ){
                        Admin objA = new Admin(
                                listaAdminFiltrada.get(i).getCedula(),
                                listaAdminFiltrada.get(i).getNombre(),
                                listaAdminFiltrada.get(i).getApellido(),
                                listaAdminFiltrada.get(i).getEdad(),
                                listaAdminFiltrada.get(i).getCorreo(),
                                listaAdminFiltrada.get(i).getTelefono(),
                                listaAdminFiltrada.get(i).getContra(),
                                listaAdminFiltrada.get(i).getPregSeguridad(),
                                listaAdminFiltrada.get(i).getRespuesta()
                        );
                        filtros.add(objA);
                    }
                }
                resultadosFiltrados.count = filtros.size();
                resultadosFiltrados.values = filtros;
            }else{
                resultadosFiltrados.count = listaAdminFiltrada.size();
                resultadosFiltrados.values = listaAdminFiltrada;
            }
            return resultadosFiltrados;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listaAdmin = (List<Admin>) results.values;
            notifyDataSetChanged();
        }
    }


}

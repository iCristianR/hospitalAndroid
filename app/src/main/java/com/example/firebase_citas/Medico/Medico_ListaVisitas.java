package com.example.firebase_citas.Medico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.firebase_citas.Modelo.HistoriaClinica;
import com.example.firebase_citas.Modelo.Medico;
import com.example.firebase_citas.Modelo.Visita;
import com.example.firebase_citas.Paciente.Paciente_Principal;
import com.example.firebase_citas.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Medico_ListaVisitas extends AppCompatActivity {
    private ListView listaVisitas;
    private SearchView searchView;
    private List<Visita> listaVisita = new ArrayList<Visita>();
    private List<HistoriaClinica> listaHC = new ArrayList<HistoriaClinica>();
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private CustomAdapterVisitasMed adapterVisitas;
    private Visita visitaSelected;
    private String cedulaMed,opcion,opcionAux;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medico_lista_visitas);

        cedulaMed = getIntent().getStringExtra("cedulaMed");
        opcion = getIntent().getStringExtra("opcion");
        listaVisitas = (ListView)findViewById(R.id.listViewMedVisita);
        searchView = (SearchView)findViewById(R.id.searchViewMedVisita);

        Calendar calendar = Calendar.getInstance();
        int anio = 2021;
        int mes = calendar.get(Calendar.MONTH)+1;
        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        String fecha = anio+"/"+mes+"/"+dia;

        inicializarFireBase();
        listarDatosVisita(fecha);
        listarDatosHistoriaClinica();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapterVisitas.getFilter().filter(newText);
                return false;
            }
        });

        listaVisitas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                visitaSelected = listaVisita.get(position);
                String hora = visitaSelected.getHora();
                Intent intent;
                switch (opcion){
                    case "1":
                        if(verificarHCPaciente(visitaSelected.getPaciente().getCedula())==false){
                            intent = new Intent(getApplicationContext(), Medico_HC.class);
                        }else{
                            intent = new Intent(getApplicationContext(), Medico_Diagnostico.class);
                        }
                        intent.putExtra("cedulaPac", visitaSelected.getPaciente().getCedula());
                        intent.putExtra("idVisita", visitaSelected.getId());
                        intent.putExtra("cedulaMed", cedulaMed);
                        intent.putExtra("opcion", opcionAux="1");
                        intent.putExtra("hora", hora);
                        startActivity(intent);
                        finish();
                        break;
                    case "2":
                        intent = new Intent(getApplicationContext(), Medico_Visitas.class);
                        intent.putExtra("idVisita", visitaSelected.getId());
                        intent.putExtra("cedulaMed", cedulaMed);
                        intent.putExtra("opcion", opcionAux="2");
                        startActivity(intent);
                        finish();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    //Inicializar Fire Base
    public void inicializarFireBase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    //LLena la lista de visitas con los datos de la BD
    public void listarDatosVisita(String fecha){
        databaseReference.child("Visita").orderByChild("hora").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaVisita.clear();
                if (snapshot.exists()) {
                    for(DataSnapshot objDataSnapshot: snapshot.getChildren()){
                        Visita objV = objDataSnapshot.getValue(Visita.class);
                        if(cedulaMed.equals(objV.getMedico().getCedula())){
                            if(opcion.equals("2")){
                                if(objV.getEstado().equals("Efectuado")){
                                    listaVisita.add(objV);
                                }
                            }else{
                                if(objV.getFecha().equals(fecha)){
                                    if(objV.getEstado().equals("Pendiente")){
                                        listaVisita.add(objV);
                                    }
                                }
                            }
                        }
                    }
                    adapterVisitas = new CustomAdapterVisitasMed(getApplicationContext(), listaVisita);
                    listaVisitas.setAdapter(adapterVisitas);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //LLena la lista de historia clinica con los datos de la BD
    public void listarDatosHistoriaClinica(){
        databaseReference.child("HistoriaClinica").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaHC.clear();
                if (snapshot.exists()) {
                    for(DataSnapshot objDataSnapshot: snapshot.getChildren()){
                        HistoriaClinica objHC = objDataSnapshot.getValue(HistoriaClinica.class);
                        listaHC.add(objHC);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public boolean verificarHCPaciente(String cedPaciente){
        boolean band = false;
        for(int i=0;i<listaHC.size();i++){
            if(listaHC.get(i).getPaciente().getCedula().equals(cedPaciente)){
                band = true;
            }
        }
        return band;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            Intent intent = new Intent(getApplicationContext(), Medico_Principal.class);
            intent.putExtra("cedulaMed", cedulaMed);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
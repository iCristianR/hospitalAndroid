package com.example.firebase_citas.Medico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.firebase_citas.Admin.Admin_CrudDiagnostico;
import com.example.firebase_citas.Admin.CustomAdapterHC;
import com.example.firebase_citas.Modelo.Diagnostico;
import com.example.firebase_citas.Modelo.HistoriaClinica;
import com.example.firebase_citas.Modelo.Medico;
import com.example.firebase_citas.Modelo.Paciente;
import com.example.firebase_citas.Modelo.Visita;
import com.example.firebase_citas.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Medico_listaHC extends AppCompatActivity {
    private SearchView searchView;
    private ListView listView;
    private List<HistoriaClinica> listaHC = new ArrayList<HistoriaClinica>();
    private List<Diagnostico> listaDiag = new ArrayList<Diagnostico>();
    private CustomAdapterHC adapterHC;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Intent intent;
    private HistoriaClinica hcSeletec;
    private String cedulaMed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medico_lista_hc);

        cedulaMed = getIntent().getStringExtra("cedulaMed");
        searchView = (SearchView)findViewById(R.id.searchViewHCMed);
        listView = (ListView)findViewById(R.id.listViewHCMed);

        inicializarFireBase();
        listarDatosDiagnostico();
        listarDatosHistoriaClinica();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapterHC.getFilter().filter(newText);
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hcSeletec = listaHC.get(position);
                intent = new Intent(getApplicationContext(), Medico_HCPDF.class);
                intent.putExtra("idHC", hcSeletec.getId());
                intent.putExtra("cedulaMed", cedulaMed);
                startActivity(intent);
                finish();
            }
        });

    }

    //Inicializar Fire Base
    public void inicializarFireBase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    //LLena la lista de diagnostico con los datos de la BD
    public void listarDatosDiagnostico(){
        databaseReference.child("Diagnostico").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaDiag.clear();
                if (snapshot.exists()) {
                    for(DataSnapshot objDataSnapshot: snapshot.getChildren()){
                        Diagnostico objD = objDataSnapshot.getValue(Diagnostico.class);
                        if(objD.getVisita().getMedico().getCedula().equals(cedulaMed)){
                            listaDiag.add(objD);
                        }
                    }
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
                        if(validar(objHC.getId())==true){
                            listaHC.add(objHC);
                        }
                    }
                    adapterHC = new CustomAdapterHC(getApplicationContext(), listaHC);
                    listView.setAdapter(adapterHC);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public boolean validar(String id){
        boolean band = false;
        for(int i=0;i<listaDiag.size();i++){
            if (listaDiag.get(i).getHc().getId().equals(id)) {
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
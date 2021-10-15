package com.example.firebase_citas.Paciente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.firebase_citas.Admin.Admin_Perfil;
import com.example.firebase_citas.Admin.CustomAdapterEspecialidad;
import com.example.firebase_citas.Modelo.*;
import com.example.firebase_citas.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Paciente_ListaEspecialidades extends AppCompatActivity{
    private ListView listaEspecialidades;
    private SearchView searchView;
    private List<Medico> list = new ArrayList<Medico>();
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private CustomAdapterEspecialidades adapterEspecialidad;
    private Medico medicoSelected;
    private String cedulaPac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente_lista_especialidades);

        cedulaPac = getIntent().getStringExtra("cedulaPac");
        listaEspecialidades = (ListView)findViewById(R.id.listViewPEspe);
        searchView = (SearchView)findViewById(R.id.searchViewPEspe);

        inicializarFireBase();
        listarDatos();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapterEspecialidad.getFilter().filter(newText);
                return false;
            }
        });

        listaEspecialidades.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                medicoSelected = list.get(position);
                Intent intent = new Intent(getApplicationContext(), Paciente_AgendarVisita.class);
                intent.putExtra("cedulaMed", medicoSelected.getCedula());
                intent.putExtra("cedulaPac", cedulaPac);
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

    public void listarDatos(){
        databaseReference.child("Medico").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                if (snapshot.exists()) {
                    for(DataSnapshot objDataSnapshot: snapshot.getChildren()){
                        Medico objM = objDataSnapshot.getValue(Medico.class);
                        list.add(objM);

                        adapterEspecialidad = new CustomAdapterEspecialidades(getApplicationContext(), list);
                        listaEspecialidades.setAdapter(adapterEspecialidad);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            Intent intent = new Intent(getApplicationContext(), Paciente_Principal.class);
            intent.putExtra("cedulaPac", cedulaPac);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
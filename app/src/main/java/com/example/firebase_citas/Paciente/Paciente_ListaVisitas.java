package com.example.firebase_citas.Paciente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

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

public class Paciente_ListaVisitas extends AppCompatActivity {
    private ListView listaVisitas;
    private SearchView searchView;
    private List<Visita> list = new ArrayList<Visita>();
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private CustomAdapterVisitasPac adapterVisitas;
    private Visita visitaSelected;
    private String cedulaPac,opcion,opcionAux;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente_lista_visitas);

        cedulaPac = getIntent().getStringExtra("cedulaPac");
        opcion = getIntent().getStringExtra("opcion");
        listaVisitas = (ListView)findViewById(R.id.listViewPVisita);
        searchView = (SearchView)findViewById(R.id.searchViewPVisita);

        inicializarFireBase();
        listarDatos();

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
                visitaSelected = list.get(position);
                Intent intent;
                switch (opcion){
                    case "1":
                        intent = new Intent(getApplicationContext(), Paciente_CancelarVisita.class);
                        intent.putExtra("idVisita", visitaSelected.getId());
                        intent.putExtra("cedulaPac", cedulaPac);
                        intent.putExtra("opcion", opcionAux="1");
                        startActivity(intent);
                        finish();
                        break;
                    case "2":
                        intent = new Intent(getApplicationContext(), Paciente_Visita.class);
                        intent.putExtra("idVisita", visitaSelected.getId());
                        intent.putExtra("cedulaPac", cedulaPac);
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

    public void listarDatos(){
        databaseReference.child("Visita").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                if (snapshot.exists()) {
                    for(DataSnapshot objDataSnapshot: snapshot.getChildren()){
                        Visita objV = objDataSnapshot.getValue(Visita.class);
                        if(cedulaPac.equals(objV.getPaciente().getCedula())){
                            if(opcion.equals("2")){
                                list.add(objV);
                            }else{
                                if(objV.getEstado().equals("Pendiente")){
                                    list.add(objV);
                                }
                            }
                        }
                    }
                    adapterVisitas = new CustomAdapterVisitasPac(getApplicationContext(), list);
                    listaVisitas.setAdapter(adapterVisitas);
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
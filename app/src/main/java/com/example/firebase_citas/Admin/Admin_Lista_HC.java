package com.example.firebase_citas.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.firebase_citas.Modelo.HistoriaClinica;
import com.example.firebase_citas.Modelo.Medico;
import com.example.firebase_citas.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Admin_Lista_HC extends AppCompatActivity {
    private SearchView searchView;
    private ListView listViewHC;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private CustomAdapterHC adapterHC;
    private List<HistoriaClinica> listaHC = new ArrayList<HistoriaClinica>();
    private HistoriaClinica hcSelected;
    private Intent intent;
    private String opc;
    private String cedulaAdm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_lista_h_c);

        cedulaAdm = getIntent().getStringExtra("cedulaAdm");
        searchView = (SearchView)findViewById(R.id.searchViewCHC);
        listViewHC = (ListView)findViewById(R.id.listViewCHC);

        inicializarFireBase();
        listarDatosHC();

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

        listViewHC.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hcSelected = listaHC.get(position);
                intent = new Intent(getApplicationContext(), Admin_CrudHC.class);
                intent.putExtra("idHc", hcSelected.getId());
                intent.putExtra("cedulaAdm", cedulaAdm);
                intent.putExtra("opcion", opc="1");
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

    //LLena la lista de medicos con los datos de la BD
    public void listarDatosHC(){
        databaseReference.child("HistoriaClinica").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaHC.clear();
                if (snapshot.exists()) {
                    for(DataSnapshot objDataSnapshot: snapshot.getChildren()){
                        HistoriaClinica objHC = objDataSnapshot.getValue(HistoriaClinica.class);
                        listaHC.add(objHC);

                        adapterHC = new CustomAdapterHC(getApplicationContext(),listaHC);
                        listViewHC.setAdapter(adapterHC);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_add,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        startActivity(new Intent(getApplicationContext(), Admin_CrudHC.class));
        finish();
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            Intent intent = new Intent(getApplicationContext(), Admin_Principal.class);
            intent.putExtra("cedulaAdm", cedulaAdm);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


}
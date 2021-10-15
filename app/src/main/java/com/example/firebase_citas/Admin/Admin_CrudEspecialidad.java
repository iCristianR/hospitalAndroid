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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.firebase_citas.Modelo.Especialidad;
import com.example.firebase_citas.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Admin_CrudEspecialidad extends AppCompatActivity {
    private EditText txtId, txtNombre;
    private SearchView searchView;
    private ListView listViewEspecialidades;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private List<Especialidad> listaEspecialidades = new ArrayList<Especialidad>();
    private Especialidad especialidadSelected;
    private CustomAdapterEspecialidad adapterEspecialidad;
    private String cedulaAdm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_crud_especialidad);

        cedulaAdm = getIntent().getStringExtra("cedulaAdm");
        txtId = (EditText)findViewById(R.id.txtCEId);
        txtNombre = (EditText)findViewById(R.id.txtCENombre);
        listViewEspecialidades = (ListView)findViewById(R.id.listViewCE);
        searchView = (SearchView)findViewById(R.id.searchViewCE);

        inicializarFireBase();
        listarDatosEspecialidad();

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

        listViewEspecialidades.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                especialidadSelected = listaEspecialidades.get(position);
                txtId.setText(especialidadSelected.getId());
                txtNombre.setText(especialidadSelected.getNombre());
                txtId.setEnabled(false);
            }
        });
    }

    //Inicializar Fire Base
    public void inicializarFireBase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    //LLena la lista de pacientes con los datos de la BD
    public void listarDatosEspecialidad(){
        databaseReference.child("Especialidad").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaEspecialidades.clear();
                if (snapshot.exists()) {
                    for(DataSnapshot objDataSnapshot: snapshot.getChildren()){
                        Especialidad objM = objDataSnapshot.getValue(Especialidad.class);
                        listaEspecialidades.add(objM);

                        adapterEspecialidad = new CustomAdapterEspecialidad(getApplicationContext(),listaEspecialidades);
                        listViewEspecialidades.setAdapter(adapterEspecialidad);
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
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        String id = txtId.getText().toString();
        String nom = txtNombre.getText().toString();

        switch (menuItem.getItemId()){
            case R.id.ic_add:
                if(id.isEmpty() || nom.isEmpty()){
                    Toast.makeText(this, "Algunos campos estan vacios", Toast.LENGTH_SHORT).show();
                }else{
                    if(validarExiste(id)==true){
                        Toast.makeText(getApplicationContext(), "Este id ya ha sido registrada", Toast.LENGTH_SHORT).show();
                    }else{
                        registrar_actualizar(id, nom);
                        Toast.makeText(this, "Registro Completado", Toast.LENGTH_SHORT).show();
                        limpiarCajas();
                    }
                }
                break;
            case R.id.ic_save:
                if(id.isEmpty() || nom.isEmpty()){
                    Toast.makeText(this, "Algunos campos estan vacios", Toast.LENGTH_SHORT).show();
                }else{
                    registrar_actualizar(id, nom);
                    Toast.makeText(this, "Informacion Actualizada", Toast.LENGTH_SHORT).show();
                    limpiarCajas();
                }
                txtId.setEnabled(true);
                break;
            default:
                break;
        }
        return true;
    }

    //Registro en RealTimeDatabase
    private void registrar_actualizar(String id, String nombre){
        Especialidad objM = new Especialidad();
        objM.setId(id);
        objM.setNombre(nombre);
        databaseReference.child("Especialidad").child(id).setValue(objM);
    }

    public boolean validarExiste(String id){
        boolean band = false;
        for(int i=0;i<listaEspecialidades.size();i++){
            if(id.equals(listaEspecialidades.get(i).getId())){
                band = true;
            }
        }
        return band;
    }

    public void limpiarCajas(){
        txtId.setText("");
        txtNombre.setText("");
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
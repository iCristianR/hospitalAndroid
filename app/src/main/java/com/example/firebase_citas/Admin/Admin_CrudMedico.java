package com.example.firebase_citas.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.firebase_citas.Modelo.Especialidad;
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
import java.util.regex.Pattern;

public class Admin_CrudMedico extends AppCompatActivity {
    private EditText txtCedula, txtNombre, txtApellido, txtEdad, txtCorreo, txtTelefono, txtContra, txtRespuesta;
    private Spinner spnPreguntas,spnEspecialidad;
    private ListView listViewMedicos;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private CustomAdapterMedico adapterMedico;
    private List<Medico> listaMedico = new ArrayList<Medico>();
    private List<Especialidad> listaEspecialidad = new ArrayList<Especialidad>();
    private ArrayAdapter<Especialidad> adapterEspecialidades;
    private Medico medicoSelected;
    private SearchView searchView;
    private String cedulaAdm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_crud_medico);

        cedulaAdm = getIntent().getStringExtra("cedulaAdm");
        txtCedula = (EditText)findViewById(R.id.txtCMCedula);
        txtNombre = (EditText)findViewById(R.id.txtCMNombre);
        txtApellido = (EditText)findViewById(R.id.txtCMApellido);
        txtEdad = (EditText)findViewById(R.id.txtCMEdad);
        txtCorreo = (EditText)findViewById(R.id.txtCMCorreo);
        txtTelefono = (EditText)findViewById(R.id.txtCMTelefono);
        txtContra = (EditText)findViewById(R.id.txtCMContra);
        txtRespuesta = (EditText)findViewById(R.id.txtCMRespuesta);
        spnPreguntas = (Spinner)findViewById(R.id.spinnerCMPreguntas);
        spnEspecialidad = (Spinner)findViewById(R.id.spinnerCMEspecialidad);
        listViewMedicos = (ListView)findViewById(R.id.listViewCM);
        searchView = (SearchView)findViewById(R.id.searchViewCM);

        inicializarFireBase();
        listarDatosMedico();
        listarDatosEspecialidad();

        String[] preguntas = {"Primer apellido de su padre","Primer apellido de su madre","Nombre de su primera mascota","Ciudad donde nacio","Colegio en el que curso primaria"};
        ArrayAdapter<String> adapterPreguntas = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,preguntas);
        spnPreguntas.setAdapter(adapterPreguntas);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapterMedico.getFilter().filter(newText);
                return false;
            }
        });

        listViewMedicos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                medicoSelected = listaMedico.get(position);
                txtCedula.setText(medicoSelected.getCedula());
                txtNombre.setText(medicoSelected.getNombre());
                txtApellido.setText(medicoSelected.getApellido());
                txtEdad.setText(medicoSelected.getEdad());
                txtCorreo.setText(medicoSelected.getCorreo());
                txtTelefono.setText(medicoSelected.getTelefono());
                txtContra.setText(medicoSelected.getContra());
                spnPreguntas.setSelection(obtenerPosicionItem(spnPreguntas, medicoSelected.getPregSeguridad()));
                spnEspecialidad.setSelection(obtenerPosicionItem(spnEspecialidad, medicoSelected.getEspecialidad().getNombre()));
                txtRespuesta.setText(medicoSelected.getRespuesta());
                txtCedula.setEnabled(false);
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
    public void listarDatosMedico(){
        databaseReference.child("Medico").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaMedico.clear();
                if (snapshot.exists()) {
                    for(DataSnapshot objDataSnapshot: snapshot.getChildren()){
                        Medico objM = objDataSnapshot.getValue(Medico.class);
                        listaMedico.add(objM);

                        adapterMedico = new CustomAdapterMedico(getApplicationContext(),listaMedico);
                        listViewMedicos.setAdapter(adapterMedico);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //LLena la lista de Especialidaes con los datos de la BD
    public void listarDatosEspecialidad(){
        databaseReference.child("Especialidad").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaEspecialidad.clear();
                if (snapshot.exists()) {
                    for(DataSnapshot objDataSnapshot: snapshot.getChildren()){
                        Especialidad objE = objDataSnapshot.getValue(Especialidad.class);
                        listaEspecialidad.add(objE);

                        adapterEspecialidades = new ArrayAdapter<Especialidad>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,listaEspecialidad);
                        spnEspecialidad.setAdapter(adapterEspecialidades);
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
        String ced = txtCedula.getText().toString();
        String nom = txtNombre.getText().toString();
        String apell = txtApellido.getText().toString();
        String ed = txtEdad.getText().toString();
        String corr = txtCorreo.getText().toString();
        String tel = txtTelefono.getText().toString();
        String con = txtContra.getText().toString();
        String preg = spnPreguntas.getSelectedItem().toString();
        String resp = txtRespuesta.getText().toString();
        Especialidad espe = retornarEspecialidad(spnEspecialidad.getSelectedItem().toString());

        switch (menuItem.getItemId()){
            case R.id.ic_add:
                if(ced.isEmpty() || nom.isEmpty() || apell.isEmpty() || ed.isEmpty()|| corr.isEmpty()|| tel.isEmpty()|| con.isEmpty() || resp.isEmpty()){
                    Toast.makeText(this, "Algunos campos estan vacios", Toast.LENGTH_SHORT).show();
                }else{
                    if(listaMedico.size()<10){
                        if (!validarEmail(corr)){
                            Toast.makeText(getApplicationContext(), "Ingrese un correo valido", Toast.LENGTH_SHORT).show();
                        }else {
                            if(validarEdad(ed)==false){
                                Toast.makeText(getApplicationContext(), "Ingrese una edad valida", Toast.LENGTH_SHORT).show();
                            }else {
                                registrar(ced, nom, apell, ed, corr, tel, con, preg, resp, espe);
                                Toast.makeText(this, "Registro Completado", Toast.LENGTH_SHORT).show();
                                limpiarCajas();
                            }
                        }
                    }else{
                        Toast.makeText(this, "Registro maximo de doctores", Toast.LENGTH_SHORT).show();
                        limpiarCajas();
                    }
                }
                break;
            case R.id.ic_save:
                if(ced.isEmpty() || nom.isEmpty() || apell.isEmpty() || ed.isEmpty() || corr.isEmpty() || tel.isEmpty() || resp.isEmpty()){
                    Toast.makeText(this, "Algunos campos estan vacios", Toast.LENGTH_SHORT).show();
                }else{
                    if (!validarEmail(corr)){
                        Toast.makeText(getApplicationContext(), "Ingrese un correo valido", Toast.LENGTH_SHORT).show();
                    }else {
                        if(validarEdad(ed)==false){
                            Toast.makeText(getApplicationContext(), "Ingrese una edad valida", Toast.LENGTH_SHORT).show();
                        }else {
                                actualizar(ced, nom, apell, ed, corr, tel, con, preg, resp, espe);
                                Toast.makeText(this, "Informacion Actualizada", Toast.LENGTH_SHORT).show();
                                limpiarCajas();
                                txtCedula.setEnabled(true);
                        }
                    }
                }
                txtCedula.setEnabled(true);
                break;
            default:
                break;
        }
        return true;
    }

    //Registro en RealTimeDatabase
    private void registrar(String cedula, String nombre, String apellido, String edad, String correo, String telefono, String contra, String pregSeguridad, String resp, Especialidad especialidad){
        Medico objM = new Medico();
        objM.setCedula(cedula);
        objM.setNombre(nombre);
        objM.setApellido(apellido);
        objM.setEdad(edad);
        objM.setCorreo(correo);
        objM.setTelefono(telefono);
        objM.setContra(contra);
        objM.setPregSeguridad(pregSeguridad);
        objM.setRespuesta(resp);
        objM.setEspecialidad(especialidad);
        databaseReference.child("Medico").child(cedula).setValue(objM);
    }

    //Actualizar en RealTimeDatabase
    private void actualizar(String cedula, String nombre, String apellido, String edad, String correo, String telefono, String contra, String pregSeguridad, String resp, Especialidad especialidad){
        Medico objM = new Medico();
        objM.setCedula(cedula);
        objM.setNombre(nombre);
        objM.setApellido(apellido);
        objM.setEdad(edad);
        objM.setCorreo(correo);
        objM.setTelefono(telefono);
        objM.setContra(contra);
        objM.setPregSeguridad(pregSeguridad);
        objM.setRespuesta(resp);
        objM.setEspecialidad(especialidad);
        databaseReference.child("Medico").child(cedula).setValue(objM);
    }

    public Especialidad retornarEspecialidad(String nombre){
        Especialidad aux = new Especialidad();
        for(int i=0;i<listaEspecialidad.size();i++){
            if(listaEspecialidad.get(i).getNombre().equals(nombre)){
                aux.setId(listaEspecialidad.get(i).getId());
                aux.setNombre(listaEspecialidad.get(i).getNombre());
            }
        }
        return  aux;
    }

    public int obtenerPosicionItem(Spinner spinner, String preg) {
        int posicion = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(preg)) {
                posicion = i;
            }
        }
        return posicion;
    }

    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    public boolean validarEdad(String edad){
        int ed = Integer.parseInt(edad);
        boolean band = false;
        if (ed>0) {
            if(ed<120){
                band = true;
            }
        }
        return band;
    }

    public boolean validarExiste(String id){
        boolean band = false;
        for(int i=0;i<listaMedico.size();i++){
            if(id.equals(listaMedico.get(i).getCedula())){
                band = true;
            }
        }
        return band;
    }

    public void limpiarCajas(){
        txtCedula.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtEdad.setText("");
        txtCorreo.setText("");
        txtTelefono.setText("");
        txtContra.setText("");
        txtRespuesta.setText("");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            Intent intent = new Intent(getApplicationContext(), Admin_Principal1.class);
            intent.putExtra("cedulaAdm", cedulaAdm);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


}
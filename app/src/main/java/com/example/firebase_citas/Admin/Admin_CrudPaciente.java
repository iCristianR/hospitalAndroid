package com.example.firebase_citas.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

import com.example.firebase_citas.Modelo.Paciente;
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

public class Admin_CrudPaciente extends AppCompatActivity {
    private EditText txtCedula, txtNombre, txtApellido, txtEdad, txtCorreo, txtTelefono, txtContra, txtRespuesta;
    private Spinner spnPreguntas;
    private ListView listViewPacientes;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private CustomAdapterPaciente adapterPaciente;
    private List<Paciente> listaPaciente = new ArrayList<Paciente>();
    private Paciente pacienteSelected;
    private SearchView searchView;
    private String cedulaAdm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_crud_paciente);

        cedulaAdm = getIntent().getStringExtra("cedulaAdm");
        txtCedula = (EditText)findViewById(R.id.txtCPCedula);
        txtNombre = (EditText)findViewById(R.id.txtCPNombre);
        txtApellido = (EditText)findViewById(R.id.txtCPApellido);
        txtEdad = (EditText)findViewById(R.id.txtCPEdad);
        txtCorreo = (EditText)findViewById(R.id.txtCPCorreo);
        txtTelefono = (EditText)findViewById(R.id.txtCPTelefono);
        txtContra = (EditText)findViewById(R.id.txtCPContra);
        txtRespuesta = (EditText)findViewById(R.id.txtCPRespuesta);
        spnPreguntas = (Spinner)findViewById(R.id.spinnerCPPreguntas);
        listViewPacientes = (ListView)findViewById(R.id.listViewCP);
        searchView = (SearchView)findViewById(R.id.searchViewCP);

        inicializarFireBase();
        listarDatosPaciente();

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
                adapterPaciente.getFilter().filter(newText);
                return false;
            }
        });

        listViewPacientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pacienteSelected = listaPaciente.get(position);
                txtCedula.setText(pacienteSelected.getCedula());
                txtNombre.setText(pacienteSelected.getNombre());
                txtApellido.setText(pacienteSelected.getApellido());
                txtEdad.setText(pacienteSelected.getEdad());
                txtCorreo.setText(pacienteSelected.getCorreo());
                txtTelefono.setText(pacienteSelected.getTelefono());
                txtContra.setText(pacienteSelected.getContra());
                spnPreguntas.setSelection(obtenerPosicionItem(spnPreguntas, pacienteSelected.getPregSeguridad()));
                txtRespuesta.setText(pacienteSelected.getRespuesta());
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

    //LLena la lista de pacientes con los datos de la BD
    public void listarDatosPaciente(){
        databaseReference.child("Paciente").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaPaciente.clear();
                if (snapshot.exists()) {
                    for(DataSnapshot objDataSnapshot: snapshot.getChildren()){
                        Paciente objP = objDataSnapshot.getValue(Paciente.class);
                        listaPaciente.add(objP);

                        adapterPaciente = new CustomAdapterPaciente(getApplicationContext(),listaPaciente);
                        listViewPacientes.setAdapter(adapterPaciente);
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

        switch (menuItem.getItemId()){
            case R.id.ic_add:
                if(ced.isEmpty() || nom.isEmpty() || apell.isEmpty() || ed.isEmpty()|| corr.isEmpty()|| tel.isEmpty()|| con.isEmpty() || resp.isEmpty()){
                    Toast.makeText(this, "Algunos campos estan vacios", Toast.LENGTH_SHORT).show();
                }else{
                    if (!validarEmail(corr)){
                        Toast.makeText(getApplicationContext(), "Ingrese un correo valido", Toast.LENGTH_SHORT).show();
                    }else {
                        if(validarEdad(ed)==false){
                            Toast.makeText(getApplicationContext(), "Ingrese una edad valida", Toast.LENGTH_SHORT).show();
                        }else {
                            if(validarExiste(ced)==true){
                                Toast.makeText(getApplicationContext(), "Esta cedula ya ha sido registrada", Toast.LENGTH_SHORT).show();
                            }else {
                                registrar(ced, nom, apell, ed, corr, tel, con, preg, resp);
                                Toast.makeText(this, "Registro Completado", Toast.LENGTH_SHORT).show();
                                limpiarCajas();
                            }
                        }
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
                            actualizar(ced, nom, apell, ed, corr, tel, con, preg, resp);
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
    private void registrar(String cedula, String nombre, String apellido, String edad, String correo, String telefono, String contra, String pregSeguridad, String resp){
        Paciente objP = new Paciente();
        objP.setCedula(cedula);
        objP.setNombre(nombre);
        objP.setApellido(apellido);
        objP.setEdad(edad);
        objP.setCorreo(correo);
        objP.setTelefono(telefono);
        objP.setContra(contra);
        objP.setPregSeguridad(pregSeguridad);
        objP.setRespuesta(resp);
        databaseReference.child("Paciente").child(cedula).setValue(objP);
    }

    //Actualizar en RealTimeDatabase
    private void actualizar(String cedula, String nombre, String apellido, String edad, String correo, String telefono, String contra, String pregSeguridad, String resp){
        Paciente objP = new Paciente();
        objP.setCedula(cedula);
        objP.setNombre(nombre);
        objP.setApellido(apellido);
        objP.setEdad(edad);
        objP.setCorreo(correo);
        objP.setTelefono(telefono);
        objP.setContra(contra);
        objP.setPregSeguridad(pregSeguridad);
        objP.setRespuesta(resp);
        databaseReference.child("Paciente").child(cedula).setValue(objP);
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
        for(int i=0;i<listaPaciente.size();i++){
            if(id.equals(listaPaciente.get(i).getCedula())){
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
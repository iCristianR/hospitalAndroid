package com.example.firebase_citas.Paciente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.firebase_citas.Admin.CustomAdapterPaciente;
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

public class Paciente_Registro extends AppCompatActivity{

    private EditText cedula, nombre, apellido, edad, correo, telefono, contra, confircontra, respuesta;
    private Spinner preguntas;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private List<Paciente> listaPaciente = new ArrayList<Paciente>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente_registro);

        cedula = (EditText)findViewById(R.id.txtCedulaPac);
        nombre = (EditText)findViewById(R.id.txtNombrePac);
        apellido = (EditText)findViewById(R.id.txtApellidoPac);
        edad = (EditText)findViewById(R.id.txtEdadPac);
        correo = (EditText)findViewById(R.id.txtCorreoPac);
        telefono = (EditText)findViewById(R.id.txtTelefonoPac);
        contra = (EditText)findViewById(R.id.txtContraPac);
        confircontra = (EditText)findViewById(R.id.txtContraPacConf);
        respuesta = (EditText)findViewById(R.id.txtRespuestaPac);
        preguntas = (Spinner)findViewById(R.id.spinnerPreguntasPac);

        inicializarFireBase();
        listarDatosPaciente();

        String[] opciones = {"Primer apellido de su padre","Primer apellido de su madre","Nombre de su primera mascota","Ciudad donde nacio","Colegio en el que curso primaria"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,opciones);
        preguntas.setAdapter(adapter);
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
        String ced = cedula.getText().toString();
        String nom = nombre.getText().toString();
        String apell = apellido.getText().toString();
        String ed = edad.getText().toString();
        String corr = correo.getText().toString();
        String tel = telefono.getText().toString();
        String con = contra.getText().toString();
        String conf = confircontra.getText().toString();
        String preg = preguntas.getSelectedItem().toString();
        String resp = respuesta.getText().toString();

        if(ced.isEmpty() || nom.isEmpty() || apell.isEmpty() || ed.isEmpty()|| corr.isEmpty()|| tel.isEmpty()|| con.isEmpty() || conf.isEmpty() || resp.isEmpty()){
            Toast.makeText(this, "Algunos campos estan vacios", Toast.LENGTH_SHORT).show();
        }else{
            if(con.equals(conf)){
                if (!validarEmail(corr)){
                    Toast.makeText(getApplicationContext(), "Ingrese un correo valido", Toast.LENGTH_SHORT).show();
                }else {
                    if(validarEdad(ed)==false){
                        Toast.makeText(getApplicationContext(), "Ingrese una edad valida", Toast.LENGTH_SHORT).show();
                    }else{
                        if(validarExiste(ced)==true){
                            Toast.makeText(getApplicationContext(), "Esta cedula ya ha sido registrada", Toast.LENGTH_SHORT).show();
                        }else{
                            registrar(ced, nom, apell, ed, corr, tel, conf, preg, resp);
                            Toast.makeText(this, "Registro Completado", Toast.LENGTH_SHORT).show();
                            limpiarCajas();
                            startActivity(new Intent(getApplicationContext(), Paciente_Login.class));
                            finish();
                        }
                    }
                }
            }else{
                Toast.makeText(getApplicationContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                contra.setText("");
                confircontra.setText("");
            }
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
        cedula.setText("");
        nombre.setText("");
        apellido.setText("");
        edad.setText("");
        correo.setText("");
        telefono.setText("");
        contra.setText("");
        confircontra.setText("");
        respuesta.setText("");
    }

}
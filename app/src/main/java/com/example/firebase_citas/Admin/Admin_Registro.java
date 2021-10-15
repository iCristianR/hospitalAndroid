package com.example.firebase_citas.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.firebase_citas.Modelo.Admin;
import com.example.firebase_citas.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;
import java.util.regex.Pattern;

public class Admin_Registro extends AppCompatActivity{

    private EditText cedula, nombre, apellido, edad, correo, telefono, contra, confircontra, respuesta;
    private Spinner preguntas;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_registro);

        cedula = (EditText)findViewById(R.id.txtCedulaAdm);
        nombre = (EditText)findViewById(R.id.txtNombreAdm);
        apellido = (EditText)findViewById(R.id.txtApellidoAdm);
        edad = (EditText)findViewById(R.id.txtEdadAdm);
        correo = (EditText)findViewById(R.id.txtCorreoAdm);
        telefono = (EditText)findViewById(R.id.txtTelefonoAdm);
        contra = (EditText)findViewById(R.id.txtContraAdm);
        confircontra = (EditText)findViewById(R.id.txtContraAdmConf);
        preguntas = (Spinner)findViewById(R.id.spinnerPreguntasAdm);
        respuesta = (EditText)findViewById(R.id.txtSeguridadAdm);

        inicializarFireBase();

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

        if(ced.isEmpty() || nom.isEmpty() || apell.isEmpty() || ed.isEmpty() || corr.isEmpty()|| tel.isEmpty() || con.isEmpty() || conf.isEmpty() || resp.isEmpty()){
            Toast.makeText(this, "Algunos campos estan vacios", Toast.LENGTH_SHORT).show();
        }else{
            if(con.equals(conf)){
                if (!validarEmail(corr)){
                    Toast.makeText(getApplicationContext(), "Ingrese un correo valido", Toast.LENGTH_SHORT).show();
                }else {
                    if(validarEdad(ed)==false){
                        Toast.makeText(getApplicationContext(), "Ingrese una edad valida", Toast.LENGTH_SHORT).show();
                    }else {
                        registrar(ced, nom, apell, ed, corr, tel, conf, preg, resp);
                        Toast.makeText(this, "Registro Completado", Toast.LENGTH_SHORT).show();
                        limpiarCajas();
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
    private void registrar(String cedula, String nombre, String apellido, String edad, String correo, String telefono, String contra, String pregunta, String respuesta){
        Admin objA = new Admin();
        objA.setCedula(cedula);
        objA.setNombre(nombre);
        objA.setApellido(apellido);
        objA.setEdad(edad);
        objA.setCorreo(correo);
        objA.setTelefono(telefono);
        objA.setContra(contra);
        objA.setPregSeguridad(pregunta);
        objA.setRespuesta(respuesta);
        databaseReference.child("Admin").child(cedula).setValue(objA);
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
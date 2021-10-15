package com.example.firebase_citas.Paciente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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

public class Paciente_Perfil extends AppCompatActivity{
    private EditText cedula, nombre, apellido, edad, correo, telefono, contra, respuesta;
    private Spinner preguntas;
    private List<Paciente> listaPaciente = new ArrayList<Paciente>();
    private String cedulaPac;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente_perfil);

        cedula = (EditText)findViewById(R.id.txtPCedulaPac);
        nombre = (EditText)findViewById(R.id.txtPNombrePac);
        apellido = (EditText)findViewById(R.id.txtPApellidoPac);
        edad = (EditText)findViewById(R.id.txtPEdadPac);
        correo = (EditText)findViewById(R.id.txtPCorreoPac);
        telefono = (EditText)findViewById(R.id.txtPTelefonoPac);
        contra = (EditText)findViewById(R.id.txtPContraPac);
        respuesta = (EditText)findViewById(R.id.txtPRespuestaPac);
        preguntas = (Spinner)findViewById(R.id.spinnerPPreguntasPac);
        cedulaPac = getIntent().getStringExtra("cedulaPac");

        inicializarFireBase();
        listarDatos(cedulaPac);

        cedula.setEnabled(false);

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
        getMenuInflater().inflate(R.menu.menu_save,menu);
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
        String preg = preguntas.getSelectedItem().toString();
        String resp = respuesta.getText().toString();

        if(ced.isEmpty() || nom.isEmpty() || apell.isEmpty() || ed.isEmpty() || corr.isEmpty() || tel.isEmpty() || resp.isEmpty()){
            Toast.makeText(this, "Algunos campos estan vacios", Toast.LENGTH_SHORT).show();
        }else{
            Paciente objP = new Paciente();
            objP.setCedula(ced);
            objP.setNombre(nom);
            objP.setApellido(apell);
            objP.setEdad(ed);
            objP.setCorreo(corr);
            objP.setTelefono(tel);
            if(con.isEmpty()){
                objP.setContra(listaPaciente.get(0).getContra());
            }else{
                objP.setContra(con);
            }
            objP.setPregSeguridad(preg);
            objP.setRespuesta(resp);
            databaseReference.child("Paciente").child(ced).setValue(objP);
            Toast.makeText(this, "Informacion Actualizada", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    public void listarDatos(String ced){
        databaseReference.child("Paciente").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaPaciente.clear();
                if (snapshot.exists()) {
                    for(DataSnapshot objDataSnapshot: snapshot.getChildren()){
                        Paciente objP = objDataSnapshot.getValue(Paciente.class);
                        if (objP.getCedula().equals(ced)) {
                            cedula.setText(objP.getCedula());
                            nombre.setText(objP.getNombre());
                            apellido.setText(objP.getApellido());
                            edad.setText(objP.getEdad());
                            correo.setText(objP.getCorreo());
                            telefono.setText(objP.getTelefono());
                            respuesta.setText(objP.getRespuesta());
                            preguntas.setSelection(obtenerPosicionItem(preguntas, objP.getPregSeguridad()));
                            listaPaciente.add(objP);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
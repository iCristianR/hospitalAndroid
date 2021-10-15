package com.example.firebase_citas.Medico;

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

import com.example.firebase_citas.Modelo.Especialidad;
import com.example.firebase_citas.Modelo.Medico;
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

public class Medico_Perfil extends AppCompatActivity {
    private EditText txtCedula, txtNombre, txtApellido, txtEdad, txtCorreo, txtTelefono, txtEspecialidad, txtContra, txtRespuesta;
    private Spinner spnPreguntas;
    private List<Medico> listaMedico = new ArrayList<Medico>();
    private List<Especialidad> listaEspecialidad = new ArrayList<Especialidad>();
    private String cedulaMed;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medico_perfil);

        txtCedula = (EditText)findViewById(R.id.txtPCedulaMed);
        txtNombre = (EditText)findViewById(R.id.txtPNombreMed);
        txtApellido = (EditText)findViewById(R.id.txtPApellidoMed);
        txtEdad = (EditText)findViewById(R.id.txtPEdadMed);
        txtCorreo = (EditText)findViewById(R.id.txtPCorreoMed);
        txtTelefono = (EditText)findViewById(R.id.txtPTelefonoMed);
        txtEspecialidad = (EditText)findViewById(R.id.txtPEspecialidadMed);
        txtContra = (EditText)findViewById(R.id.txtPContraMed);
        txtRespuesta = (EditText)findViewById(R.id.txtPRespuestaMed);
        spnPreguntas = (Spinner)findViewById(R.id.spinnerPPreguntasMed);
        cedulaMed = getIntent().getStringExtra("cedulaMed");

        inicializarFireBase();
        listarDatos(cedulaMed);
        listarDatosEspecialidad();

        txtCedula.setEnabled(false);
        txtEspecialidad.setEnabled(false);

        String[] opciones = {"Primer apellido de su padre","Primer apellido de su madre","Nombre de su primera mascota","Ciudad donde nacio","Colegio en el que curso primaria"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,opciones);
        spnPreguntas.setAdapter(adapter);
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
        String ced = txtCedula.getText().toString();
        String nom = txtNombre.getText().toString();
        String apell = txtApellido.getText().toString();
        String ed = txtEdad.getText().toString();
        String corr = txtCorreo.getText().toString();
        String tel = txtTelefono.getText().toString();
        String con = txtContra.getText().toString();
        String preg = spnPreguntas.getSelectedItem().toString();
        String resp = txtRespuesta.getText().toString();
        String espe = txtEspecialidad.getText().toString();

        if(ced.isEmpty() || nom.isEmpty() || apell.isEmpty() || ed.isEmpty() || corr.isEmpty() || tel.isEmpty() || resp.isEmpty() || espe.isEmpty()){
            Toast.makeText(this, "Algunos campos estan vacios", Toast.LENGTH_SHORT).show();
        }else{
            Medico objM = new Medico();
            objM.setCedula(ced);
            objM.setNombre(nom);
            objM.setApellido(apell);
            objM.setEdad(ed);
            objM.setCorreo(corr);
            objM.setTelefono(tel);
            if(con.isEmpty()){
                objM.setContra(listaMedico.get(0).getContra());
            }else{
                objM.setContra(con);
            }
            objM.setPregSeguridad(preg);
            objM.setRespuesta(resp);
            objM.setEspecialidad(retornarEspecialidad(espe));
            databaseReference.child("Medico").child(ced).setValue(objM);
            Toast.makeText(this, "Informacion Actualizada", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    public void listarDatos(String ced){
        databaseReference.child("Medico").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaMedico.clear();
                if (snapshot.exists()) {
                    for(DataSnapshot objDataSnapshot: snapshot.getChildren()){
                        Medico objM = objDataSnapshot.getValue(Medico.class);
                        if (objM.getCedula().equals(ced)) {
                            txtCedula.setText(objM.getCedula());
                            txtNombre.setText(objM.getNombre());
                            txtApellido.setText(objM.getApellido());
                            txtEdad.setText(objM.getEdad());
                            txtCorreo.setText(objM.getCorreo());
                            txtTelefono.setText(objM.getTelefono());
                            txtRespuesta.setText(objM.getRespuesta());
                            spnPreguntas.setSelection(obtenerPosicionItem(spnPreguntas, objM.getPregSeguridad()));
                            txtEspecialidad.setText(objM.getEspecialidad().getNombre());
                            listaMedico.add(objM);
                        }
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
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
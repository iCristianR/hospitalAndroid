package com.example.firebase_citas.Admin;

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

import com.example.firebase_citas.Modelo.Admin;
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

public class Admin_Perfil extends AppCompatActivity {
    private EditText txtCedula, txtNombre, txtApellido, txtEdad, txtCorreo, txtTelefono, txtContra, txtRespuesta;
    private Spinner spnPreguntas;
    private List<Admin> listaAdmin = new ArrayList<Admin>();
    private String cedulaAdm;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_perfil);

        txtCedula = (EditText)findViewById(R.id.txtPCedulaAdm);
        txtNombre = (EditText)findViewById(R.id.txtPNombreAdm);
        txtApellido = (EditText)findViewById(R.id.txtPApellidoAdm);
        txtEdad = (EditText)findViewById(R.id.txtPEdadAdm);
        txtCorreo = (EditText)findViewById(R.id.txtPCorreoAdm);
        txtTelefono = (EditText)findViewById(R.id.txtPTelefonoAdm);
        txtContra = (EditText)findViewById(R.id.txtPContraAdm);
        txtRespuesta = (EditText)findViewById(R.id.txtPRespuestaAdm);
        spnPreguntas = (Spinner)findViewById(R.id.spinnerPPreguntasAdm);
        cedulaAdm = getIntent().getStringExtra("cedulaAdm");

        inicializarFireBase();
        listarDatos(cedulaAdm);

        txtCedula.setEnabled(false);

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

        if(ced.isEmpty() || nom.isEmpty() || apell.isEmpty() || ed.isEmpty() || corr.isEmpty() || tel.isEmpty() || resp.isEmpty()){
            Toast.makeText(this, "Algunos campos estan vacios", Toast.LENGTH_SHORT).show();
        }else{
            Admin objA = new Admin();
            objA.setCedula(ced);
            objA.setNombre(nom);
            objA.setApellido(apell);
            objA.setEdad(ed);
            objA.setCorreo(corr);
            objA.setTelefono(tel);
            if(con.isEmpty()){
                objA.setContra(listaAdmin.get(0).getContra());
            }else{
                objA.setContra(con);
            }
            objA.setPregSeguridad(preg);
            objA.setRespuesta(resp);
            databaseReference.child("Admin").child(ced).setValue(objA);
            Toast.makeText(this, "Informacion Actualizada", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    public void listarDatos(String ced){
        databaseReference.child("Admin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaAdmin.clear();
                if (snapshot.exists()) {
                    for(DataSnapshot objDataSnapshot: snapshot.getChildren()){
                        Admin objA = objDataSnapshot.getValue(Admin.class);
                        if (objA.getCedula().equals(ced)) {
                            txtCedula.setText(objA.getCedula());
                            txtNombre.setText(objA.getNombre());
                            txtApellido.setText(objA.getApellido());
                            txtEdad.setText(objA.getEdad());
                            txtCorreo.setText(objA.getCorreo());
                            txtTelefono.setText(objA.getTelefono());
                            txtRespuesta.setText(objA.getRespuesta());
                            spnPreguntas.setSelection(obtenerPosicionItem(spnPreguntas, objA.getPregSeguridad()));
                            listaAdmin.add(objA);
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
            Intent intent = new Intent(getApplicationContext(), Admin_Principal.class);
            intent.putExtra("cedulaAdm", cedulaAdm);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
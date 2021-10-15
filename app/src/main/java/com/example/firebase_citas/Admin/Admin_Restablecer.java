package com.example.firebase_citas.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebase_citas.Modelo.Admin;
import com.example.firebase_citas.Modelo.Paciente;
import com.example.firebase_citas.Paciente.Paciente_Login;
import com.example.firebase_citas.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Admin_Restablecer extends AppCompatActivity implements View.OnClickListener {
    private EditText txtCorreo, txtRespuesta, txtContra, txtContraConf;
    private TextView lblTitulo;
    private Button btnRecuperar,btnGuardar;
    private Spinner spnPreguntas;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private List<Admin> listaAdmin = new ArrayList<Admin>();
    private String cedula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_restablecer);

        txtCorreo = (EditText)findViewById(R.id.txtRCorreoAdm);
        spnPreguntas = (Spinner)findViewById(R.id.spinnerRPreguntasAdm);
        txtRespuesta = (EditText)findViewById(R.id.txtRRespuestaAdm);
        txtContra = (EditText)findViewById(R.id.txtRContraAdm);
        txtContraConf = (EditText)findViewById(R.id.txtRContraConfAdm);
        lblTitulo = (TextView)findViewById(R.id.lblRContraAdm);
        btnRecuperar = (Button)findViewById(R.id.btnRContraAdm);
        btnGuardar = (Button)findViewById(R.id.btnRGuardarAdm);

        btnRecuperar.setOnClickListener(this);
        btnGuardar.setOnClickListener(this);

        txtContra.setVisibility(View.GONE);
        txtContraConf.setVisibility(View.GONE);
        lblTitulo.setVisibility(View.GONE);
        btnGuardar.setVisibility(View.GONE);

        inicializarFireBase();
        listarDatos();

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

    public void listarDatos(){
        databaseReference.child("Admin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaAdmin.clear();
                if (snapshot.exists()) {
                    for(DataSnapshot objDataSnapshot: snapshot.getChildren()){
                        Admin objA = objDataSnapshot.getValue(Admin.class);
                        listaAdmin.add(objA);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        opcionBoton(v);
    }

    public void opcionBoton(View v){
        String email = txtCorreo.getText().toString();
        String preg = spnPreguntas.getSelectedItem().toString();
        String resp = txtRespuesta.getText().toString();
        String con = txtContraConf.getText().toString();

        switch(v.getId()){
            case R.id.btnRContraPac:
                if (verificarDatos(email,preg,resp)==true) {
                    txtContra.setVisibility(View.VISIBLE);
                    txtContraConf.setVisibility(View.VISIBLE);
                    lblTitulo.setVisibility(View.VISIBLE);
                    btnGuardar.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(getApplicationContext(),"Verifique los datos ingresados",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnRGuardarPac:
                if (txtContra.getText().toString().equals(txtContraConf.getText().toString())) {
                    databaseReference.child("Admin").child(cedula).child("contra").setValue(con);
                    Toast.makeText(getApplicationContext(),"Contraseña actualizada correctamente",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), Admin_Login.class));
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"Las contraseñas no coinciden",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    public boolean verificarDatos(String email, String preg, String resp){
        boolean band = false;
        for(int i=0;i<listaAdmin.size();i++){
            if (listaAdmin.get(i).getCorreo().equals(email) && listaAdmin.get(i).getPregSeguridad().equals(preg) && listaAdmin.get(i).getRespuesta().equals(resp)) {
                band = true;
                cedula = listaAdmin.get(i).getCedula();
            }
        }
        return band;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            startActivity(new Intent(getApplicationContext(), Admin_Login.class));
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


}
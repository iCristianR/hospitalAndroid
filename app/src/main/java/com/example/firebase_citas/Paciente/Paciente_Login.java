package com.example.firebase_citas.Paciente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.firebase_citas.MainActivity;
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

public class Paciente_Login extends AppCompatActivity implements View.OnClickListener{
    private EditText txtCorreo, txtContra;
    private Button btnIngresar, btnRegistrarPac, btnOlvidoContra;
    private List<Paciente> listaPaciente = new ArrayList<Paciente>();
    private String cedulaPac;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente_login);

        txtCorreo = (EditText)findViewById(R.id.txtCorreoLoginPac);
        txtContra = (EditText)findViewById(R.id.txtContraLoginPac);
        btnIngresar = (Button)findViewById(R.id.btnIngresoPac);
        btnRegistrarPac = (Button)findViewById(R.id.btnRegistroPac);
        btnOlvidoContra = (Button)findViewById(R.id.btnOlvidePac);

        btnIngresar.setOnClickListener(this);
        btnRegistrarPac.setOnClickListener(this);
        btnOlvidoContra.setOnClickListener(this);

        inicializarFireBase();
        listarDatosPaciente();
    }

    //Inicializar Fire base
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
    public void onClick(View v) {
        opcionBoton(v);
    }

    public void opcionBoton(View v){
        String corr = txtCorreo.getText().toString();
        String con = txtContra.getText().toString();
        switch (v.getId()){
            case R.id.btnIngresoPac:
                if(!corr.isEmpty() && !con.isEmpty()){
                    if(login(corr,con)==true){
                        Intent intent = new Intent(getApplicationContext(), Paciente_Principal.class);
                        intent.putExtra("cedulaPac", cedulaPac);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(),"No se pudo iniciar Sesion, compruebe los datos",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Complete los campos",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnRegistroPac:
                startActivity(new Intent(getApplicationContext(), Paciente_Registro.class));
                break;
            case R.id.btnOlvidePac:
                startActivity(new Intent(getApplicationContext(), Paciente_Restablecer.class));
                break;
            default:
                break;
        }
    }

    public boolean login(String correo, String contra){
        boolean band = false;
        for(int i=0;i<listaPaciente.size();i++){
            //Inicio de sesion correcto
            if(listaPaciente.get(i).getCorreo().equals(correo) && listaPaciente.get(i).getContra().equals(contra)){
                cedulaPac = listaPaciente.get(i).getCedula();
                band = true;
            }
        }
        return band;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
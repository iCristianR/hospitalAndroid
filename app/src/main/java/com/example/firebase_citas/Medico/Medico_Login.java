package com.example.firebase_citas.Medico;

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
import com.example.firebase_citas.Modelo.Medico;
import com.example.firebase_citas.Modelo.Paciente;
import com.example.firebase_citas.Paciente.Paciente_Principal;
import com.example.firebase_citas.Paciente.Paciente_Registro;
import com.example.firebase_citas.Paciente.Paciente_Restablecer;
import com.example.firebase_citas.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Medico_Login extends AppCompatActivity implements View.OnClickListener{
    private EditText txtCorreo, txtContra;
    private Button btnIngresar, btnOlvidoContra;
    private List<Medico> listaMedico = new ArrayList<Medico>();
    private String cedulaMed;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medico_login);

        txtCorreo = (EditText)findViewById(R.id.txtCorreoLoginMed);
        txtContra = (EditText)findViewById(R.id.txtContraLoginMed);
        btnIngresar = (Button)findViewById(R.id.btnIngresoMed);
        btnOlvidoContra = (Button)findViewById(R.id.btnOlvideMed);

        btnIngresar.setOnClickListener(this);
        btnOlvidoContra.setOnClickListener(this);

        inicializarFireBase();
        listarDatosMedico();
    }

    //Inicializar Fire base
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
            case R.id.btnIngresoMed:
                if(!corr.isEmpty() && !con.isEmpty()){
                    if(login(corr,con)==true){
                        Intent intent = new Intent(getApplicationContext(), Medico_Principal.class);
                        intent.putExtra("cedulaMed", cedulaMed);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(),"No se pudo iniciar Sesion, compruebe los datos",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Complete los campos",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnOlvideMed:
                startActivity(new Intent(getApplicationContext(), Medico_Restablecer.class));
                break;
            default:
                break;
        }
    }

    public boolean login(String correo, String contra){
        boolean band = false;
        for(int i=0;i<listaMedico.size();i++){
            //Inicio de sesion correcto
            if(listaMedico.get(i).getCorreo().equals(correo) && listaMedico.get(i).getContra().equals(contra)){
                cedulaMed = listaMedico.get(i).getCedula();
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
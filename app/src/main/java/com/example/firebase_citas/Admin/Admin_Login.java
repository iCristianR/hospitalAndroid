package com.example.firebase_citas.Admin;

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
import com.example.firebase_citas.Modelo.Admin;

import com.example.firebase_citas.Paciente.Paciente_Principal;
import com.example.firebase_citas.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Admin_Login extends AppCompatActivity implements View.OnClickListener {
    private EditText txtCorreo, txtContra;
    private Button btnIngresar, btnOlvide, btnRegistrarse;
    private List<Admin> listaAdmin = new ArrayList<Admin>();
    private String cedulaAdm;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        txtCorreo = (EditText) findViewById(R.id.txtCorreoLoginAdm);
        txtContra = (EditText) findViewById(R.id.txtContraLoginAdm);
        btnIngresar = (Button) findViewById(R.id.btnIngresoAdm);
        btnOlvide = (Button) findViewById(R.id.btnOlvideAdm);
        btnRegistrarse = (Button) findViewById(R.id.btnRegistroAdm);

        btnIngresar.setOnClickListener(this);
        btnOlvide.setOnClickListener(this);
        btnRegistrarse.setOnClickListener(this);
        btnRegistrarse.setVisibility(View.GONE);

        inicializarFireBase();
        listarDatosAdmin();

    }

    //Inicializar Fire base
    public void inicializarFireBase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    //LLena la lista de admins con los datos de la BD
    public void listarDatosAdmin() {
        databaseReference.child("Admin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    listaAdmin.clear();
                    for (DataSnapshot objDataSnapshot : snapshot.getChildren()) {
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

    public void opcionBoton(View v) {
        String corr = txtCorreo.getText().toString();
        String con = txtContra.getText().toString();
        switch (v.getId()) {
            case R.id.btnIngresoAdm:
                if (!corr.isEmpty() && !con.isEmpty()) {
                    if (login(corr, con) == true) {
                        Intent intent = new Intent(getApplicationContext(), Admin_Principal.class);
                        intent.putExtra("cedulaAdm", cedulaAdm);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Complete los campos", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnOlvideAdm:
                startActivity(new Intent(getApplicationContext(), Admin_Restablecer.class));
                break;
            case R.id.btnRegistroAdm:
                startActivity(new Intent(getApplicationContext(), Admin_Registro.class));
                break;
            default:
                break;
        }
    }

    public boolean login(String correo, String contra) {
        boolean band = false;
        for (int i = 0; i < listaAdmin.size(); i++) {
            //Inicio de sesion correcto
            if (listaAdmin.get(i).getCorreo().equals(correo) && listaAdmin.get(i).getContra().equals(contra)) {
                band = true;
                cedulaAdm = listaAdmin.get(i).getCedula();
            } else {
                Toast.makeText(getApplicationContext(), "No se pudo iniciar Sesion, compruebe los datos", Toast.LENGTH_SHORT).show();
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
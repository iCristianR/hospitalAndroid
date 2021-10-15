package com.example.firebase_citas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.firebase_citas.Admin.Admin_Login;
import com.example.firebase_citas.Medico.Medico_Login;
import com.example.firebase_citas.Paciente.Paciente_Login;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnPaciente,btnMedico,btnAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPaciente = (Button)findViewById(R.id.btnPaciente);
        btnMedico = (Button)findViewById(R.id.btnMedico);
        btnAdmin = (Button)findViewById(R.id.btnAdmin);

        btnPaciente.setOnClickListener(this);
        btnMedico.setOnClickListener(this);
        btnAdmin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        opcionBoton(v);
    }

    public void opcionBoton(View v){
        switch (v.getId()){
            case R.id.btnPaciente:
                startActivity(new Intent(getApplicationContext(), Paciente_Login.class));
                break;
            case R.id.btnMedico:
                startActivity(new Intent(getApplicationContext(), Medico_Login.class));
                break;
            case R.id.btnAdmin:
                startActivity(new Intent(getApplicationContext(), Admin_Login.class));
                break;
            default:
                break;
        }
    }



}
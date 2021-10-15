package com.example.firebase_citas.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

import com.example.firebase_citas.MainActivity;
import com.example.firebase_citas.R;

public class Admin_Principal1 extends AppCompatActivity implements View.OnClickListener{
    private ImageButton btnAdmins,btnMedicos,btnPacientes;
    private String cedulaAdm;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_principal1);

        cedulaAdm = getIntent().getStringExtra("cedulaAdm");
        btnAdmins = (ImageButton)findViewById(R.id.btnCrudAdmin);
        btnMedicos = (ImageButton)findViewById(R.id.btnCrudMedico);
        btnPacientes = (ImageButton)findViewById(R.id.btnCrudPaciente);

        btnAdmins.setOnClickListener(this);
        btnMedicos.setOnClickListener(this);
        btnPacientes.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        opcionBoton(v);
    }

    public void opcionBoton(View v){
        switch (v.getId()){
            case R.id.btnCrudAdmin:
                intent = new Intent(getApplicationContext(), Admin_CrudAdmin.class);
                intent.putExtra("cedulaAdm", cedulaAdm);
                startActivity(intent);
                finish();
                break;
            case R.id.btnCrudMedico:
                intent = new Intent(getApplicationContext(), Admin_CrudMedico.class);
                intent.putExtra("cedulaAdm", cedulaAdm);
                startActivity(intent);
                finish();
                break;
            case R.id.btnCrudPaciente:
                intent = new Intent(getApplicationContext(), Admin_CrudPaciente.class);
                intent.putExtra("cedulaAdm", cedulaAdm);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            intent = new Intent(getApplicationContext(), Admin_Principal.class);
            intent.putExtra("cedulaAdm", cedulaAdm);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


}
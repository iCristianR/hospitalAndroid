package com.example.firebase_citas.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.firebase_citas.MainActivity;
import com.example.firebase_citas.R;

public class Admin_Principal extends AppCompatActivity implements View.OnClickListener{
    private ImageButton btnPerfil,btnUsuarios,btnVisita,btnHC,btnEspecialidades,btnReportes;
    private Button btnSalir;
    private String cedulaAdm;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_principal);

        btnPerfil = (ImageButton)findViewById(R.id.btnPerfilAdmin);
        btnUsuarios = (ImageButton)findViewById(R.id.btnCrudUsuarios);
        btnVisita = (ImageButton)findViewById(R.id.btnCrudVisita);
        btnHC = (ImageButton)findViewById(R.id.btnCrudHC);
        btnEspecialidades = (ImageButton)findViewById(R.id.btnCrudEspecialidad);
        btnReportes = (ImageButton)findViewById(R.id.btnReportesAdmin);
        btnSalir = (Button)findViewById(R.id.btnSalirAdm);
        cedulaAdm = getIntent().getStringExtra("cedulaAdm");

        btnPerfil.setOnClickListener(this);
        btnUsuarios.setOnClickListener(this);
        btnVisita.setOnClickListener(this);
        btnHC.setOnClickListener(this);
        btnEspecialidades.setOnClickListener(this);
        btnReportes.setOnClickListener(this);
        btnSalir.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        opcionBoton(v);
    }

    public void opcionBoton(View v){
        switch (v.getId()){
            case R.id.btnPerfilAdmin:
                intent = new Intent(getApplicationContext(), Admin_Perfil.class);
                intent.putExtra("cedulaAdm", cedulaAdm);
                startActivity(intent);
                finish();
                break;
            case R.id.btnCrudUsuarios:
                intent = new Intent(getApplicationContext(), Admin_Principal1.class);
                intent.putExtra("cedulaAdm", cedulaAdm);
                startActivity(intent);
                finish();
                break;
            case R.id.btnCrudVisita:
                intent = new Intent(getApplicationContext(), Admin_CrudVisita.class);
                intent.putExtra("cedulaAdm", cedulaAdm);
                startActivity(intent);
                finish();
                break;
            case R.id.btnCrudHC:
                intent = new Intent(getApplicationContext(), Admin_Lista_HC.class);
                intent.putExtra("cedulaAdm", cedulaAdm);
                startActivity(intent);
                finish();
                break;
            case R.id.btnCrudEspecialidad:
                intent = new Intent(getApplicationContext(), Admin_CrudEspecialidad.class);
                intent.putExtra("cedulaAdm", cedulaAdm);
                startActivity(intent);
                finish();
                break;
            case R.id.btnReportesAdmin:
                intent = new Intent(getApplicationContext(), Admin_Reporte.class);
                intent.putExtra("cedulaAdm", cedulaAdm);
                startActivity(intent);
                finish();
                break;
            case R.id.btnSalirAdm:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
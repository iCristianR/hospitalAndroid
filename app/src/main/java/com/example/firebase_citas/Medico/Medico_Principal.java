package com.example.firebase_citas.Medico;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.firebase_citas.Admin.Admin_Principal;
import com.example.firebase_citas.MainActivity;
import com.example.firebase_citas.Modelo.Medico;
import com.example.firebase_citas.R;

public class Medico_Principal extends AppCompatActivity implements View.OnClickListener{
    private ImageButton btnAtender,btnHistorial,btnReportes,btnPerfil;
    private Button btnSalir;
    private String cedulaMed;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medico_principal);

        btnAtender = (ImageButton)findViewById(R.id.btnVAtenderVisita);
        btnHistorial = (ImageButton)findViewById(R.id.btnVHistorialVisitasMed);
        btnReportes = (ImageButton)findViewById(R.id.btnVReportesMed);
        btnPerfil = (ImageButton)findViewById(R.id.btnVPerfilMed);
        btnSalir = (Button)findViewById(R.id.btnSalirMedico);
        cedulaMed = getIntent().getStringExtra("cedulaMed");

        btnAtender.setOnClickListener(this);
        btnHistorial.setOnClickListener(this);
        btnReportes.setOnClickListener(this);
        btnPerfil.setOnClickListener(this);
        btnSalir.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        opcionBoton(v);
    }

    public void opcionBoton(View v){
        String opcion="";
        switch (v.getId()){
            case R.id.btnVAtenderVisita:
                intent = new Intent(getApplicationContext(), Medico_ListaVisitas.class);
                intent.putExtra("cedulaMed", cedulaMed);
                intent.putExtra("opcion", opcion="1");
                startActivity(intent);
                finish();
                break;
            case R.id.btnVHistorialVisitasMed:
                intent = new Intent(getApplicationContext(), Medico_ListaVisitas.class);
                intent.putExtra("cedulaMed", cedulaMed);
                intent.putExtra("opcion", opcion="2");
                startActivity(intent);
                finish();
                break;
            case R.id.btnVReportesMed:
                intent = new Intent(getApplicationContext(), Medico_listaHC.class);
                intent.putExtra("cedulaMed", cedulaMed);
                startActivity(intent);
                finish();
                break;
            case R.id.btnVPerfilMed:
                intent = new Intent(getApplicationContext(), Medico_Perfil.class);
                intent.putExtra("cedulaMed", cedulaMed);
                startActivity(intent);
                finish();
                break;
            case R.id.btnSalirMedico:
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
            intent = new Intent(getApplicationContext(), Medico_Principal.class);
            intent.putExtra("cedulaMed", cedulaMed);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
package com.example.firebase_citas.Paciente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.firebase_citas.Admin.Admin_Principal;
import com.example.firebase_citas.MainActivity;
import com.example.firebase_citas.Modelo.Paciente;
import com.example.firebase_citas.R;

public class Paciente_Principal extends AppCompatActivity implements View.OnClickListener{

    private ImageButton btnAgendar,btnCancelar,btnPerfil,btnVisitas;
    private Button btnSalir;
    private String cedulaPac;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente_principal);

        btnAgendar = (ImageButton)findViewById(R.id.btnVAgendarVisita);
        btnCancelar = (ImageButton)findViewById(R.id.btnVCancelarVisita);
        btnPerfil = (ImageButton)findViewById(R.id.btnVPerfilPaciente);
        btnVisitas = (ImageButton)findViewById(R.id.btnVVisitasPaciente);
        btnSalir = (Button)findViewById(R.id.btnSalirPaciente);
        cedulaPac = getIntent().getStringExtra("cedulaPac");

        btnAgendar.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);
        btnPerfil.setOnClickListener(this);
        btnVisitas.setOnClickListener(this);
        btnSalir.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        opcionBoton(v);
    }

    public void opcionBoton(View v){
        String opcion = "";
        switch (v.getId()){
            case R.id.btnVAgendarVisita:
                intent = new Intent(getApplicationContext(), Paciente_ListaEspecialidades.class);
                intent.putExtra("cedulaPac", cedulaPac);
                startActivity(intent);
                finish();
                break;
            case R.id.btnVCancelarVisita:
                intent = new Intent(getApplicationContext(), Paciente_ListaVisitas.class);
                intent.putExtra("cedulaPac", cedulaPac);
                intent.putExtra("opcion", opcion="1");
                startActivity(intent);
                finish();
                break;
            case R.id.btnVPerfilPaciente:
                intent = new Intent(getApplicationContext(), Paciente_Perfil.class);
                intent.putExtra("cedulaPac", cedulaPac);
                startActivity(intent);
                finish();
                break;
            case R.id.btnVVisitasPaciente:
                intent = new Intent(getApplicationContext(), Paciente_ListaVisitas.class);
                intent.putExtra("cedulaPac", cedulaPac);
                intent.putExtra("opcion", opcion="2");
                startActivity(intent);
                finish();
                break;
            case R.id.btnSalirPaciente:
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
            intent = new Intent(getApplicationContext(), Paciente_Principal.class);
            intent.putExtra("cedulaPac", cedulaPac);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
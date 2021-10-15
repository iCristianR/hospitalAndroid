package com.example.firebase_citas.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.firebase_citas.Modelo.Diagnostico;
import com.example.firebase_citas.Modelo.HistoriaClinica;
import com.example.firebase_citas.Modelo.Medico;
import com.example.firebase_citas.Modelo.Paciente;
import com.example.firebase_citas.Modelo.Visita;
import com.example.firebase_citas.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Admin_CrudDiagnostico extends AppCompatActivity {
    private TextView txtID,txtVisita,txtPaciente,txtMedico,txtEspecialidad,txtFecha, txtHora, txtEstado, txtObservaciones, txtMedicamentos;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String idDiag="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_crud_diagnostico);

        idDiag = getIntent().getStringExtra("idDiagnostico");
        txtID = (EditText)findViewById(R.id.txtCDId);
        txtVisita = (EditText)findViewById(R.id.txtCDVisita);
        txtPaciente = (EditText)findViewById(R.id.txtCDPaciente);
        txtMedico = (EditText)findViewById(R.id.txtCDMedico);
        txtEspecialidad = (EditText)findViewById(R.id.txtCDEspecialidad);
        txtFecha = (EditText)findViewById(R.id.txtCDFecha);
        txtHora = (EditText)findViewById(R.id.txtCDHora);
        txtEstado = (EditText)findViewById(R.id.txtCDEstado);
        txtObservaciones = (EditText)findViewById(R.id.txtCDObservaciones);
        txtMedicamentos = (EditText)findViewById(R.id.txtCDMedicamentos);

        inicializarFireBase();
        listarDatosDiagnostico();

    }

    //Inicializar Fire Base
    public void inicializarFireBase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    //LLena la lista de Diagnosticos con los datos de la BD
    public void listarDatosDiagnostico(){
        databaseReference.child("Diagnostico").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for(DataSnapshot objDataSnapshot: snapshot.getChildren()){
                        Diagnostico objD = objDataSnapshot.getValue(Diagnostico.class);
                        if(objD.getId().equals(idDiag)){
                            txtID.setText(objD.getId());
                            txtVisita.setText(objD.getVisita().getId());
                            txtPaciente.setText(objD.getVisita().getPaciente().getCedula());
                            txtMedico.setText(objD.getVisita().getMedico().getCedula());
                            txtEspecialidad.setText(objD.getVisita().getMedico().getEspecialidad().getNombre());
                            txtFecha.setText(objD.getVisita().getFecha());
                            txtHora.setText(objD.getVisita().getHora());
                            txtEstado.setText(objD.getVisita().getEstado());
                            txtObservaciones.setText(objD.getObservaciones());
                            txtMedicamentos.setText(objD.getMedicamentos());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }





}
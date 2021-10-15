package com.example.firebase_citas.Medico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.firebase_citas.Modelo.Diagnostico;
import com.example.firebase_citas.Modelo.HistoriaClinica;
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

public class Medico_Visitas extends AppCompatActivity{
    private EditText txtIdVisita,txtFecha,txtCedulaPac,txtPaciente,txtHora,txtEstado;
    private EditText txtIdDiag,txtIdHC,txtObservaciones,txtMedicamentos;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String idVisita,cedulaMed,opcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medico_visitas);

        idVisita = getIntent().getStringExtra("idVisita");
        cedulaMed = getIntent().getStringExtra("cedulaMed");
        opcion = getIntent().getStringExtra("opcion");
        txtIdVisita = (EditText)findViewById(R.id.txtVIdVisitaMed);
        txtFecha = (EditText)findViewById(R.id.txtVFechaMed);
        txtCedulaPac = (EditText)findViewById(R.id.txtVCedulapacMed);
        txtPaciente = (EditText)findViewById(R.id.txtVPacienteMed);
        txtHora = (EditText)findViewById(R.id.txtVHoraMed);
        txtEstado = (EditText)findViewById(R.id.txtVEstadoMed);
        txtIdDiag = (EditText)findViewById(R.id.txtVIdDiagMed);
        txtIdHC = (EditText)findViewById(R.id.txtVIdHCMed);
        txtObservaciones = (EditText)findViewById(R.id.txtVObservacionesMed);
        txtMedicamentos = (EditText)findViewById(R.id.txtVEnfermedadesMed);

        inicializarFireBase();
        listarDatosDiagnostico();
        listarDatosVisita();

    }

    //Inicializar Fire Base
    public void inicializarFireBase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    //LLena la lista de diagnostico con los datos de la BD
    public void listarDatosDiagnostico(){
        databaseReference.child("Diagnostico").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for(DataSnapshot objDataSnapshot: snapshot.getChildren()){
                        Diagnostico objD = objDataSnapshot.getValue(Diagnostico.class);
                        if(objD.getVisita().getId().equals(idVisita)){
                            txtIdDiag.setText(objD.getId());
                            txtIdHC.setText(objD.getHc().getId());
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

    //LLena la lista de visita con los datos de la BD
    public void listarDatosVisita(){
        databaseReference.child("Visita").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for(DataSnapshot objDataSnapshot: snapshot.getChildren()){
                        Visita objV = objDataSnapshot.getValue(Visita.class);
                        if(objV.getId().equals(idVisita)){
                            txtIdVisita.setText(objV.getId());
                            txtFecha.setText(objV.getFecha());
                            txtCedulaPac.setText(objV.getPaciente().getCedula());
                            txtPaciente.setText(objV.getPaciente().getNombre()+" "+objV.getPaciente().getApellido());
                            txtHora.setText(objV.getHora());
                            txtEstado.setText(objV.getEstado());
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
            Intent intent = new Intent(getApplicationContext(), Medico_ListaVisitas.class);
            intent.putExtra("cedulaMed", cedulaMed);
            intent.putExtra("opcion", opcion);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
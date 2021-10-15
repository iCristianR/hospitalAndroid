package com.example.firebase_citas.Medico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.firebase_citas.Modelo.HistoriaClinica;
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

public class Medico_VerHC extends AppCompatActivity {
    private EditText txtId,txtFecha,txtPaciente, txtEstatura,txtPeso,txtRh,txtEnfermedades;
    private String idHc,idVisita,cedulaPac,cedulaMed,opcion;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private List<HistoriaClinica> listaHC = new ArrayList<HistoriaClinica>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medico_ver_hc);

        idHc = getIntent().getStringExtra("hc");
        txtId = (EditText)findViewById(R.id.txtVerHCIdMed);
        txtPaciente = (EditText)findViewById(R.id.txtVerHCPacienteMed);
        txtPaciente = (EditText)findViewById(R.id.txtVerHCPacienteMed);
        txtFecha = (EditText)findViewById(R.id.txtVerHCFechaMed);
        txtEstatura = (EditText)findViewById(R.id.txtVerHCEstaturaMed);
        txtPeso = (EditText)findViewById(R.id.txtVerHCPesoMed);
        txtEnfermedades = (EditText)findViewById(R.id.txtVerCHCEnfermedadesMed);
        txtRh = (EditText)findViewById(R.id.txtVerHCRhMed);

        inicializarFireBase();
        listarDatosHistoriaClinica();

    }

    //Inicializar Fire Base
    public void inicializarFireBase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    //LLena la lista de historia clinica con los datos de la BD
    public void listarDatosHistoriaClinica(){
        databaseReference.child("HistoriaClinica").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaHC.clear();
                if (snapshot.exists()) {
                    for(DataSnapshot objDataSnapshot: snapshot.getChildren()){
                        HistoriaClinica objHC = objDataSnapshot.getValue(HistoriaClinica.class);
                        if(objHC.getId().equals(idHc)){
                            txtId.setText(idHc);
                            txtFecha.setText(objHC.getFecha_apertura());
                            txtPaciente.setText(objHC.getPaciente().getNombre()+" "+objHC.getPaciente().getApellido());
                            txtEstatura.setText(objHC.getEstatura());
                            txtPeso.setText(objHC.getPeso());
                            txtRh.setText(objHC.getRh());
                            txtEnfermedades.setText(objHC.getEnfermedades());
                            listaHC.add(objHC);
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
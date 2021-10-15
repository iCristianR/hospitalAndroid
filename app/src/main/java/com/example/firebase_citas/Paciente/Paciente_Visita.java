package com.example.firebase_citas.Paciente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.example.firebase_citas.Admin.CustomAdapterDiagnostico;
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

public class Paciente_Visita extends AppCompatActivity {
    private EditText txtId,txtFecha,txtMedico,txtEspecialidad,txtHora,txtEstado;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String idVisita,cedulaPac,opcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente_visita);

        idVisita = getIntent().getStringExtra("idVisita");
        cedulaPac = getIntent().getStringExtra("cedulaPac");
        opcion = getIntent().getStringExtra("opcion");
        txtId = (EditText)findViewById(R.id.txtVIdPac);
        txtFecha = (EditText)findViewById(R.id.txtVFechaPac);
        txtMedico = (EditText)findViewById(R.id.txtVMedicoPac);
        txtEspecialidad = (EditText)findViewById(R.id.txtVEspecialidadPac);
        txtHora = (EditText)findViewById(R.id.txtVHoraPac);
        txtEstado = (EditText)findViewById(R.id.txtVEstadoPac);

        inicializarFireBase();
        listarDatosVisita();
    }

    //Inicializar Fire Base
    public void inicializarFireBase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    //LLena la lista de Visita con los datos de la BD
    public void listarDatosVisita(){
        databaseReference.child("Visita").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot objDataSnapshot : snapshot.getChildren()) {
                        Visita objV = objDataSnapshot.getValue(Visita.class);
                        if(idVisita.equals(objV.getId())){
                            txtId.setText(objV.getId());
                            txtFecha.setText(objV.getFecha());
                            txtMedico.setText(objV.getMedico().getNombre()+" "+objV.getMedico().getApellido());
                            txtEspecialidad.setText(objV.getMedico().getEspecialidad().getNombre());
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
            Intent intent = new Intent(getApplicationContext(), Paciente_ListaVisitas.class);
            intent.putExtra("cedulaPac", cedulaPac);
            intent.putExtra("opcion", opcion);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }



}
package com.example.firebase_citas.Paciente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.firebase_citas.Medico.Medico_Principal;
import com.example.firebase_citas.Modelo.Paciente;
import com.example.firebase_citas.Modelo.Visita;
import com.example.firebase_citas.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Paciente_CancelarVisita extends AppCompatActivity implements View.OnClickListener {
    private EditText txtId,txtFecha,txtMedico,txtEspecialidad,txtHora,txtEstado;
    private Button btnCancelar;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String idVisita,cedulaPac,opcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente_cancelar_visita);

        idVisita = getIntent().getStringExtra("idVisita");
        cedulaPac = getIntent().getStringExtra("cedulaPac");
        opcion = getIntent().getStringExtra("opcion");
        txtId = (EditText)findViewById(R.id.txtCIdPac);
        txtFecha = (EditText)findViewById(R.id.txtCFechaPac);
        txtMedico = (EditText)findViewById(R.id.txtCMedicoPac);
        txtEspecialidad = (EditText)findViewById(R.id.txtCEspecialidadPac);
        txtHora = (EditText)findViewById(R.id.txtCHoraPac);
        txtEstado = (EditText)findViewById(R.id.txtCEstadoPac);
        btnCancelar = (Button)findViewById(R.id.btnCCancelarPac);
        btnCancelar.setOnClickListener(this);

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
    public void onClick(View v) {
        String id = txtId.getText().toString();
        String estado = "Cancelado";

        databaseReference.child("Visita").child(id).child("estado").setValue(estado);
        Toast.makeText(getApplicationContext(),"Se ha cancelado la cita correctamente!",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), Paciente_ListaVisitas.class);
        intent.putExtra("cedulaPac", cedulaPac);
        intent.putExtra("opcion", opcion);
        startActivity(intent);
        finish();
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
package com.example.firebase_citas.Medico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebase_citas.Modelo.Diagnostico;
import com.example.firebase_citas.Modelo.HistoriaClinica;
import com.example.firebase_citas.Modelo.Visita;
import com.example.firebase_citas.Paciente.Paciente_ListaVisitas;
import com.example.firebase_citas.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

public class Medico_Diagnostico extends AppCompatActivity implements View.OnClickListener{
    private TextView txtId,txtVisita,txtCedPaciente,txtPaciente,txtHC,txtObservaciones, txtMedicamentos;
    private Button btnVer,btnCancelar;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private List<Diagnostico> listaDiagnostico = new ArrayList<Diagnostico>();
    private List<HistoriaClinica> listaHC = new ArrayList<HistoriaClinica>();
    private List<Visita> listaVisita = new ArrayList<Visita>();
    private String idVisita,cedulaPac,cedulaMed,opcion,hora;
    private Intent intent;
    private Random random = new Random();
    private boolean band;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medico_diagnostico);

        idVisita = getIntent().getStringExtra("idVisita");
        cedulaPac = getIntent().getStringExtra("cedulaPac");
        cedulaMed = getIntent().getStringExtra("cedulaMed");
        opcion = getIntent().getStringExtra("opcion");
        hora = getIntent().getStringExtra("hora");
        txtId = (EditText)findViewById(R.id.txtDiagId);
        txtVisita = (EditText)findViewById(R.id.txtDiagVisita);
        txtPaciente = (EditText)findViewById(R.id.txtDiagPaciente);
        txtCedPaciente = (EditText)findViewById(R.id.txtDiagCedulaPac);
        txtHC = (EditText)findViewById(R.id.txtDiagHC);
        txtObservaciones = (EditText)findViewById(R.id.txtDiagObservaciones);
        txtMedicamentos = (EditText)findViewById(R.id.txtDiagMedicamentos);
        btnVer = (Button)findViewById(R.id.btnDiagHCVer);
        btnCancelar = (Button)findViewById(R.id.btnDiagCancelar);
        btnVer.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);
        btnCancelar.setVisibility(View.GONE);

        TimeZone tz = TimeZone.getTimeZone("GMT-5");
        Calendar c = Calendar.getInstance(tz);
        String horaActual = Integer.toString(c.get(Calendar.HOUR_OF_DAY))+":"+Integer.toString(c.get(Calendar.MINUTE));
        //true->ya paso la cita
        //false->no ha pasado la cita
        band = validarHora(hora,horaActual);
        if(band==true){
            btnCancelar.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(),"La hora de consulta ya ha pasado",Toast.LENGTH_SHORT).show();
        }

        inicializarFireBase();
        listarDatosDiagnostico();
        listarDatosVisita();
        listarDatosHistoriaClinica();

        txtId.setText("diag-"+cedulaPac+random.nextInt(9999));
        txtVisita.setText(idVisita);
        txtCedPaciente.setText(cedulaPac);

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
                listaDiagnostico.clear();
                if (snapshot.exists()) {
                    for(DataSnapshot objDataSnapshot: snapshot.getChildren()){
                        Diagnostico objD = objDataSnapshot.getValue(Diagnostico.class);
                        listaDiagnostico.add(objD);
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
                listaVisita.clear();
                if (snapshot.exists()) {
                    for(DataSnapshot objDataSnapshot: snapshot.getChildren()){
                        Visita objV = objDataSnapshot.getValue(Visita.class);
                        listaVisita.add(objV);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                        if(objHC.getPaciente().getCedula().equals(cedulaPac)){
                            txtPaciente.setText(objHC.getPaciente().getNombre()+" "+objHC.getPaciente().getApellido());
                            txtHC.setText(objHC.getId());
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
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_add,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        String id = txtId.getText().toString();
        Visita visita = retornarVisita(txtVisita.getText().toString());
        HistoriaClinica hc = retornarhistoriaClinica(txtHC.getText().toString());
        String observaciones = txtObservaciones.getText().toString();
        String medicamentos = txtMedicamentos.getText().toString();

        switch (menuItem.getItemId()){
            case R.id.ic_add:
                if(id.isEmpty() || observaciones.isEmpty()|| medicamentos.isEmpty()){
                    Toast.makeText(this, "Algunos campos estan vacios", Toast.LENGTH_SHORT).show();
                }else{
                    registrar(id,visita,hc,observaciones,medicamentos);
                    Toast.makeText(this, "Diagnostico registrado", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), Medico_ListaVisitas.class);
                    intent.putExtra("cedulaMed", cedulaMed);
                    intent.putExtra("opcion", opcion);
                    startActivity(intent);
                    finish();
                }
                break;
            default:
                break;
        }

        return true;
    }

    //Registro en RealTimeDatabase
    private void registrar(String id, Visita visita, HistoriaClinica hc, String observaciones, String medicamentos){
        Diagnostico objD = new Diagnostico();
        objD.setId(id);
        objD.setVisita(visita);
        objD.setHc(hc);
        objD.setObservaciones(observaciones);
        objD.setMedicamentos(medicamentos);
        databaseReference.child("Diagnostico").child(id).setValue(objD);
        databaseReference.child("Visita").child(visita.getId()).child("estado").setValue("Efectuado");
    }

    public Visita retornarVisita(String id){
        Visita aux = new Visita();
        for(int i=0;i<listaVisita.size();i++){
            if(listaVisita.get(i).getId().equals(id)){
                aux.setId(listaVisita.get(i).getId());
                aux.setPaciente(listaVisita.get(i).getPaciente());
                aux.setMedico(listaVisita.get(i).getMedico());
                aux.setFecha(listaVisita.get(i).getFecha());
                aux.setHora(listaVisita.get(i).getHora());
                aux.setEstado(listaVisita.get(i).getEstado());
            }
        }
        return aux;
    }

    public HistoriaClinica retornarhistoriaClinica(String id){
        HistoriaClinica aux = new HistoriaClinica();
        for(int i=0;i<listaHC.size();i++){
            if(listaHC.get(i).getId().equals(id)){
                aux.setId(listaHC.get(i).getId());
                aux.setFecha_apertura(listaHC.get(i).getFecha_apertura());
                aux.setPaciente(listaHC.get(i).getPaciente());
                aux.setEstatura(listaHC.get(i).getEstatura());
                aux.setPeso(listaHC.get(i).getPeso());
                aux.setRh(listaHC.get(i).getRh());
                aux.setEnfermedades(listaHC.get(i).getEnfermedades());
            }
        }
        return aux;
    }

    public boolean validarHora(String hora,String horaActual){
        boolean band = false;
        String[] horaAux = hora.split(":");
        String[] horaActualAux = horaActual.split(":");

        if(Integer.parseInt(horaAux[0])<=Integer.parseInt(horaActualAux[0])){
            band = true;
        }else if(Integer.parseInt(horaAux[0])==Integer.parseInt(horaActualAux[0])){
            if(Integer.parseInt(horaAux[1])<=Integer.parseInt(horaActualAux[1])){
                band = true;
            }
        }

        return band;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnDiagHCVer:
                intent = new Intent(getApplicationContext(), Medico_VerHC.class);
                intent.putExtra("hc", txtHC.getText().toString());
                startActivity(intent);
                break;
            case R.id.btnDiagCancelar:
                String id = txtVisita.getText().toString();
                String estado = "Cancelado";

                databaseReference.child("Visita").child(id).child("estado").setValue(estado);
                Toast.makeText(getApplicationContext(),"Se ha cancelado la cita correctamente!",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Medico_ListaVisitas.class);
                intent.putExtra("cedulaMed", cedulaMed);
                intent.putExtra("opcion", opcion);
                startActivity(intent);
                finish();
                break;
        }
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
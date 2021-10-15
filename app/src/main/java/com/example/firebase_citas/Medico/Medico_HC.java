package com.example.firebase_citas.Medico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class Medico_HC extends AppCompatActivity{
    private EditText txtId,txtFecha,txtPaciente, txtEstatura,txtPeso,txtEnfermedades;
    private Spinner spnRh;
    private String idVisita,cedulaPac,cedulaMed,opcion,hora;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private List<HistoriaClinica> listaHC = new ArrayList<HistoriaClinica>();
    private List<Paciente> listaPaciente = new ArrayList<Paciente>();
    private Intent intent;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medico_hc);

        idVisita = getIntent().getStringExtra("idVisita");
        cedulaPac = getIntent().getStringExtra("cedulaPac");
        cedulaMed = getIntent().getStringExtra("cedulaMed");
        opcion = getIntent().getStringExtra("opcion");
        hora = getIntent().getStringExtra("hora");
        txtId = (EditText)findViewById(R.id.txtHCIdMed);
        txtPaciente = (EditText)findViewById(R.id.txtHCPacienteMed);
        txtFecha = (EditText)findViewById(R.id.txtHCFechaMed);
        txtEstatura = (EditText)findViewById(R.id.txtHCEstaturaMed);
        txtPeso = (EditText)findViewById(R.id.txtHCPesoMed);
        txtEnfermedades = (EditText)findViewById(R.id.txtHCEnfermedadesMed);
        spnRh = (Spinner)findViewById(R.id.spinnerHCRhMed);

        inicializarFireBase();
        listarDatosHistoriaClinica();
        listarDatosPaciente();

        txtId.setText("hc-"+cedulaPac+random.nextInt(9999));
        Calendar calendar = Calendar.getInstance();
        int anio = 2021;
        int mes = calendar.get(Calendar.MONTH)+1;
        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        txtFecha.setText(anio+"/"+mes+"/"+dia);
        txtPaciente.setText(cedulaPac);

        txtId.setEnabled(false);
        txtFecha.setEnabled(false);
        txtPaciente.setEnabled(false);

        String[] rh = {"A+","A-","O+","O-","B+","B-","AB+","AB-"};
        ArrayAdapter<String> adapterRh = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,rh);
        spnRh.setAdapter(adapterRh);
    }

    //Inicializar Fire Base
    public void inicializarFireBase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    //LLena la lista de pacientes con los datos de la BD
    public void listarDatosPaciente(){
        databaseReference.child("Paciente").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaPaciente.clear();
                if (snapshot.exists()) {
                    for(DataSnapshot objDataSnapshot: snapshot.getChildren()){
                        Paciente objP = objDataSnapshot.getValue(Paciente.class);
                        listaPaciente.add(objP);
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
                        listaHC.add(objHC);
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
        String fecha = txtFecha.getText().toString();
        Paciente paciente = retornarPaciente(txtPaciente.getText().toString());
        String estatura = txtEstatura.getText().toString();
        String peso = txtPeso.getText().toString();
        String rh = spnRh.getSelectedItem().toString();
        String enfer = txtEnfermedades.getText().toString();

        switch (menuItem.getItemId()){
            case R.id.ic_add:
                if(id.isEmpty() || fecha.isEmpty() || estatura.isEmpty() || peso.isEmpty()|| enfer.isEmpty()){
                    Toast.makeText(this, "Algunos campos estan vacios", Toast.LENGTH_SHORT).show();
                }else{
                    registrar(id,fecha,paciente,estatura,peso,rh,enfer);
                    Toast.makeText(this, "Historia clinica registrada", Toast.LENGTH_SHORT).show();
                    intent = new Intent(getApplicationContext(), Medico_Diagnostico.class);
                    intent.putExtra("cedulaPac", cedulaPac);
                    intent.putExtra("idVisita", idVisita);
                    intent.putExtra("cedulaMed", cedulaMed);
                    intent.putExtra("opcion", opcion);
                    intent.putExtra("hora", hora);
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
    private void registrar(String id, String fecha, Paciente paciente, String estatura, String peso, String rh, String enfer){
        HistoriaClinica objHC = new HistoriaClinica();
        objHC.setId(id);
        objHC.setFecha_apertura(fecha);
        objHC.setPaciente(paciente);
        objHC.setEstatura(estatura);
        objHC.setPeso(peso);
        objHC.setRh(rh);
        objHC.setEnfermedades(enfer);
        databaseReference.child("HistoriaClinica").child(id).setValue(objHC);
    }

    public Paciente retornarPaciente(String id){
        Paciente aux = new Paciente();
        for(int i=0;i<listaPaciente.size();i++){
            if(listaPaciente.get(i).getCedula().equals(id)){
                aux.setCedula(listaPaciente.get(i).getCedula());
                aux.setNombre(listaPaciente.get(i).getNombre());
                aux.setApellido(listaPaciente.get(i).getApellido());
                aux.setEdad(listaPaciente.get(i).getEdad());
                aux.setCorreo(listaPaciente.get(i).getCorreo());
                aux.setTelefono(listaPaciente.get(i).getTelefono());
                aux.setContra(listaPaciente.get(i).getContra());
                aux.setPregSeguridad(listaPaciente.get(i).getPregSeguridad());
                aux.setRespuesta(listaPaciente.get(i).getRespuesta());
            }
        }
        return  aux;
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
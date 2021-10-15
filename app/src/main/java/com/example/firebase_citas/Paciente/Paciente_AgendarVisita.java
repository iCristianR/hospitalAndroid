package com.example.firebase_citas.Paciente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

public class Paciente_AgendarVisita extends AppCompatActivity implements View.OnClickListener{
    private EditText txtCedula,txtNombre, txtNomMedico,txtEspecialidad,txtId,txtFecha;
    private Spinner spnHora;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private List<Visita> listaVisita = new ArrayList<Visita>();
    private List<Paciente> listaPaciente = new ArrayList<Paciente>();
    private List<Medico> listaMedico = new ArrayList<Medico>();
    private String cedulaPac,cedulaMed;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente_agendar_visita);

        cedulaPac = getIntent().getStringExtra("cedulaPac");
        cedulaMed = getIntent().getStringExtra("cedulaMed");
        txtCedula = (EditText)findViewById(R.id.txtPAgenCedula);
        txtNombre = (EditText)findViewById(R.id.txtPAgenNombre);
        txtNomMedico = (EditText)findViewById(R.id.txtPAgenMedico);
        txtEspecialidad = (EditText)findViewById(R.id.txtPAgenEspe);
        txtId = (EditText)findViewById(R.id.txtPAgenId);
        txtFecha = (EditText)findViewById(R.id.txtPAgenFecha);
        spnHora = (Spinner)findViewById(R.id.spinnerPAgenHora);

        txtFecha.setOnClickListener(this);
        txtCedula.setEnabled(false);
        txtNombre.setEnabled(false);
        txtNomMedico.setEnabled(false);
        txtEspecialidad.setEnabled(false);
        txtId.setEnabled(false);
        txtId.setText("vis-"+cedulaPac+random.nextInt(9999));

        inicializarFireBase();
        listarDatosVisita();
        listarDatosPaciente();
        listarDatosMedico();

        String[] horas = {"8:00","8:45","9:30","10:15","11:30","12:15","13:00","13:45","14:30","15:15","16:00","16:45"};
        ArrayAdapter<String> adapterHoras= new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,horas);
        spnHora.setAdapter(adapterHoras);

    }

    //Inicializar Fire Base
    public void inicializarFireBase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    //LLena la lista de visitas con los datos de la BD
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

    //LLena la lista de pacientes con los datos de la BD
    public void listarDatosPaciente(){
        databaseReference.child("Paciente").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaPaciente.clear();
                if (snapshot.exists()) {
                    for(DataSnapshot objDataSnapshot: snapshot.getChildren()){
                        Paciente objP = objDataSnapshot.getValue(Paciente.class);
                        if(cedulaPac.equals(objP.getCedula())){
                            txtCedula.setText(objP.getCedula());
                            txtNombre.setText(objP.getNombre()+" "+objP.getApellido());
                            listaPaciente.add(objP);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //LLena la lista de medicos con los datos de la BD
    public void listarDatosMedico(){
        databaseReference.child("Medico").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaMedico.clear();
                if (snapshot.exists()) {
                    for(DataSnapshot objDataSnapshot: snapshot.getChildren()){
                        Medico objM = objDataSnapshot.getValue(Medico.class);
                        if(cedulaMed.equals(objM.getCedula())){
                            txtNomMedico.setText(objM.getNombre()+" "+objM.getApellido());
                            txtEspecialidad.setText(objM.getEspecialidad().getNombre());
                            listaMedico.add(objM);
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
        Paciente pac = retornarPaciente(cedulaPac);
        Medico med = retornarMedico(cedulaMed);
        String fecha = txtFecha.getText().toString();
        String hora = spnHora.getSelectedItem().toString();
        String estado = "Pendiente";

        switch (menuItem.getItemId()){
            case R.id.ic_add:
                if(fecha.isEmpty()){
                    Toast.makeText(this, "Algunos campos estan vacios", Toast.LENGTH_SHORT).show();
                }else{
                    if(!diaSemana().equals("Sabado") && !diaSemana().equals("Domingo")){
                        if(validarFechaHoraRepetida(fecha,hora)==false){
                            if(validarMaxCitas(cedulaMed,fecha)<10){
                                registrar(id, pac, med, fecha, hora, estado);
                                Toast.makeText(this, "Cita Agendada Correctamente", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), Paciente_ListaEspecialidades.class);
                                intent.putExtra("cedulaPac", cedulaPac);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(this, "Maximo de citas registradas para este medico", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(this, "La Hora no esta disponible", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(this, "Seleccione un dia de la semana diferente a: Sabado y domingo", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

    //Registro en RealTimeDatabase
    private void registrar(String id, Paciente pac, Medico med, String fecha, String hora,String estado){
        Visita objV = new Visita();
        objV.setId(id);
        objV.setPaciente(pac);
        objV.setMedico(med);
        objV.setFecha(fecha);
        objV.setHora(hora);
        objV.setEstado(estado);
        databaseReference.child("Visita").child(id).setValue(objV);
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

    public Medico retornarMedico(String id){
        Medico aux = new Medico();
        for(int i=0;i<listaMedico.size();i++){
            if(listaMedico.get(i).getCedula().equals(id)){
                aux.setCedula(listaMedico.get(i).getCedula());
                aux.setNombre(listaMedico.get(i).getNombre());
                aux.setApellido(listaMedico.get(i).getApellido());
                aux.setEdad(listaMedico.get(i).getEdad());
                aux.setCorreo(listaMedico.get(i).getCorreo());
                aux.setTelefono(listaMedico.get(i).getTelefono());
                aux.setContra(listaMedico.get(i).getContra());
                aux.setPregSeguridad(listaMedico.get(i).getPregSeguridad());
                aux.setRespuesta(listaMedico.get(i).getRespuesta());
                aux.setEspecialidad(listaMedico.get(i).getEspecialidad());
            }
        }
        return  aux;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txtPAgenFecha:
                final Calendar c = Calendar.getInstance();
                int mes = c.get(Calendar.MONTH);
                int dia = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        txtFecha.setText(year+"/"+(month+1)+"/"+dayOfMonth);
                    }
                }
                        ,2021,mes,dia);
                datePickerDialog.show();
                break;
            default:
                break;
        }
    }

    public int validarMaxCitas(String cedula, String fecha){
        int cont = 0;
        for(int i=0;i<listaVisita.size();i++){
            if(listaVisita.get(i).getMedico().getCedula().equals(cedula) && listaVisita.get(i).getFecha().equals(fecha)){
                cont+=1;
            }
        }
        return cont;
    }

    public boolean validarFechaHoraRepetida(String fecha, String hora){
        boolean band = false;
        for(int i=0;i<listaVisita.size();i++){
            if(listaVisita.get(i).getFecha().equals(fecha) && listaVisita.get(i).getHora().equals(hora)){
                band = true;
            }
        }
        return band;
    }

    public String diaSemana(){
        TimeZone tz = TimeZone.getTimeZone("GMT-5");
        Calendar c = new GregorianCalendar(tz);
        String diaSem = "";
        int numD = 0;
        int nDia = Integer.parseInt(recuperarNumDia());
        int nMes = Integer.parseInt(recuperarNumMes());
        int nAnio = Integer.parseInt(recuperarNumAnio());
        c.set(nAnio,nMes-1,nDia);

        numD=c.get(Calendar.DAY_OF_WEEK);
        if(numD == Calendar.SUNDAY) {
            diaSem = "Domingo";
        }else if(numD == Calendar.MONDAY) {
            diaSem = "Lunes";
        }else if(numD == Calendar.TUESDAY) {
            diaSem = "Martes";
        }else if(numD == Calendar.WEDNESDAY) {
            diaSem = "Miercoles";
        }else if(numD == Calendar.THURSDAY) {
            diaSem = "Jueves";
        }else if(numD == Calendar.FRIDAY) {
            diaSem = "Viernes";
        }else if(numD == Calendar.SATURDAY) {
            diaSem = "Sabado";
        }
        return diaSem;
    }

    public String recuperarNumDia(){
        String[] vectorFecha;
        String nDia = "";
        String fecha = txtFecha.getText().toString();
        vectorFecha  = fecha.split("/");
        nDia = vectorFecha[2];
        return nDia;
    }

    public String recuperarNumMes(){
        String[] vectorFecha;
        String nMes = "";
        String fecha = txtFecha.getText().toString();
        vectorFecha  = fecha.split("/");
        nMes = vectorFecha[1];
        return nMes;
    }

    public String recuperarNumAnio(){
        String[] vectorFecha;
        String nAnio = "";
        String fecha = txtFecha.getText().toString();
        vectorFecha  = fecha.split("/");
        nAnio = vectorFecha[0];
        return nAnio;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            Intent intent = new Intent(getApplicationContext(), Paciente_ListaEspecialidades.class);
            intent.putExtra("cedulaPac", cedulaPac);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


}
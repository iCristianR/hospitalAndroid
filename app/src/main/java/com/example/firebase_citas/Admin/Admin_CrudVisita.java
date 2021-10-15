package com.example.firebase_citas.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.firebase_citas.Modelo.Visita;
import com.example.firebase_citas.Modelo.Medico;
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
import java.util.TimeZone;

public class Admin_CrudVisita extends AppCompatActivity implements View.OnClickListener{
    private EditText txtId, txtFecha, txtEspecialidad;
    private Spinner spnHora,spnPacientes,spnMedicos,spnEstado;
    private ListView listViewAgenda;
    private SearchView searchView;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private CustomAdapterVisita adapterVisita;
    private List<Visita> listaVisita = new ArrayList<Visita>();
    private List<Paciente> listaPaciente = new ArrayList<Paciente>();
    private List<Medico> listaMedico = new ArrayList<Medico>();
    private ArrayAdapter<Paciente> adapterPacientes;
    private ArrayAdapter<Medico> adapterMedicos;
    private Visita visitaSelected;
    private String cedulaAdm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_crud_visita);

        cedulaAdm = getIntent().getStringExtra("cedulaAdm");
        txtId = (EditText)findViewById(R.id.txtCAgenId);
        txtFecha = (EditText)findViewById(R.id.txtCAgenFecha);
        spnHora = (Spinner)findViewById(R.id.spinnerCAgenHora);
        spnPacientes = (Spinner)findViewById(R.id.spinnerCAgenPaciente);
        spnMedicos = (Spinner)findViewById(R.id.spinnerCAgenMedico);
        txtEspecialidad = (EditText)findViewById(R.id.txtCAgenEspecialidad);
        spnEstado = (Spinner)findViewById(R.id.spinnerCAgenEstado);
        listViewAgenda = (ListView)findViewById(R.id.listViewCAgen);
        searchView = (SearchView)findViewById(R.id.searchViewCAgen);

        txtFecha.setOnClickListener(this);

        inicializarFireBase();
        listarDatosVisita();
        listarDatosPaciente();
        listarDatosMedico();

        txtEspecialidad.setEnabled(false);

        String[] estados = {"Pendiente","Efectuado","Cancelado"};
        ArrayAdapter<String> adapterEstados = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,estados);
        spnEstado.setAdapter(adapterEstados);

        String[] horas = {"8:00","8:45","9:30","10:15","11:30","12:15","13:00","13:45","14:30","15:15","16:00","16:45"};
        ArrayAdapter<String> adapterHoras= new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,horas);
        spnHora.setAdapter(adapterHoras);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapterVisita.getFilter().filter(newText);
                return false;
            }
        });

        listViewAgenda.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                visitaSelected = listaVisita.get(position);
                txtId.setText(visitaSelected.getId());
                txtFecha.setText(visitaSelected.getFecha());
                spnHora.setSelection(obtenerPosicionItem(spnHora, visitaSelected.getHora()));
                spnPacientes.setSelection(obtenerPosicionItem(spnPacientes, visitaSelected.getPaciente().getNombre()));
                spnMedicos.setSelection(obtenerPosicionItem(spnMedicos, visitaSelected.getMedico().getNombre()));
                txtEspecialidad.setText(visitaSelected.getMedico().getEspecialidad().getNombre());
                spnEstado.setSelection(obtenerPosicionItem(spnEstado,visitaSelected.getEstado()));
                txtId.setEnabled(false);
            }
        });

        spnMedicos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                txtEspecialidad.setText(listaMedico.get(position).getEspecialidad().getNombre());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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
                        Visita objA = objDataSnapshot.getValue(Visita.class);
                        listaVisita.add(objA);

                        adapterVisita = new CustomAdapterVisita(getApplicationContext(), listaVisita);
                        listViewAgenda.setAdapter(adapterVisita);
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
                        listaPaciente.add(objP);

                        adapterPacientes = new ArrayAdapter<Paciente>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,listaPaciente);
                        spnPacientes.setAdapter(adapterPacientes);
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
                        listaMedico.add(objM);

                        adapterMedicos = new ArrayAdapter<Medico>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,listaMedico);
                        spnMedicos.setAdapter(adapterMedicos);
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
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        String id = txtId.getText().toString();
        String fecha = txtFecha.getText().toString();
        String hora = spnHora.getSelectedItem().toString();
        Paciente pac = retornarPaciente(spnPacientes.getSelectedItem().toString());
        Medico med = retornarMedico(spnMedicos.getSelectedItem().toString());
        String estado = spnEstado.getSelectedItem().toString();

        switch (menuItem.getItemId()){
            case R.id.ic_add:
                if(id.isEmpty() || fecha.isEmpty()){
                    Toast.makeText(this, "Algunos campos estan vacios", Toast.LENGTH_SHORT).show();
                }else{
                    if(!diaSemana().equals("Sabado") && !diaSemana().equals("Domingo")){
                        registrar_actualizar(id, pac, med, fecha, hora, estado);
                        Toast.makeText(this, "Registro Completado", Toast.LENGTH_SHORT).show();
                        limpiarCajas();
                    }else{
                        Toast.makeText(this, "Seleccione un dia de la semana diferente a: Sabado y domingo", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.ic_save:
                if(id.isEmpty() || fecha.isEmpty()){
                    Toast.makeText(this, "Algunos campos estan vacios", Toast.LENGTH_SHORT).show();
                }else{
                    if(!diaSemana().equals("Sabado") && !diaSemana().equals("Domingo")){
                        registrar_actualizar(id, pac, med, fecha, hora, estado);
                        Toast.makeText(this, "Informacion Actualizada", Toast.LENGTH_SHORT).show();
                        limpiarCajas();
                    }else{
                        Toast.makeText(this, "Seleccione un dia de la semana diferente a: Sabado y domingo", Toast.LENGTH_SHORT).show();
                    }
                }
                txtId.setEnabled(true);
                break;
            default:
                break;
        }
        return true;
    }

    //Registro en RealTimeDatabase
    private void registrar_actualizar(String id, Paciente pac, Medico med, String fecha, String hora,String estado){
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

    public int obtenerPosicionItem(Spinner spinner, String preg) {
        int posicion = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(preg)) {
                posicion = i;
            }
        }
        return posicion;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txtCAgenFecha:
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

    public String diaSemana(){
        TimeZone tz = TimeZone.getTimeZone("GMT-5");
        Calendar c = Calendar.getInstance(tz);
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

    public void limpiarCajas(){
        txtId.setText("");
        txtFecha.setText("");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            Intent intent = new Intent(getApplicationContext(), Admin_Principal.class);
            intent.putExtra("cedulaAdm", cedulaAdm);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


}
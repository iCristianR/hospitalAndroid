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

import com.example.firebase_citas.Modelo.Diagnostico;
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

public class Admin_CrudHC extends AppCompatActivity implements View.OnClickListener{
    private EditText txtId,txtFecha,txtEstatura,txtPeso,txtEnfermedades;
    private Spinner spnPaciente,spnRh;
    private ListView listViewDiagnostico;
    private SearchView searchView;
    private String idHC,opc,cedulaAdm;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private List<HistoriaClinica> listaHC = new ArrayList<HistoriaClinica>();
    private List<Diagnostico> listaDiagnostico = new ArrayList<Diagnostico>();
    private List<Paciente> listaPaciente = new ArrayList<Paciente>();
    private ArrayAdapter<Paciente> adapterPacientes;
    private CustomAdapterDiagnostico adapterDiagnostico;
    private Diagnostico diagnosticoSelected;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_crud_h_c);

        idHC = getIntent().getStringExtra("idHc");
        opc = getIntent().getStringExtra("opcion");
        cedulaAdm = getIntent().getStringExtra("cedulaAdm");
        txtId = (EditText)findViewById(R.id.txtCHCId);
        txtFecha = (EditText)findViewById(R.id.txtCHCFecha);
        txtEstatura = (EditText)findViewById(R.id.txtCHCEstatura);
        txtPeso = (EditText)findViewById(R.id.txtCHCPeso);
        txtEnfermedades = (EditText)findViewById(R.id.txtCHCEnfermedades);
        spnPaciente = (Spinner)findViewById(R.id.spinnerCHCPaciente);
        spnRh = (Spinner)findViewById(R.id.spinnerCHCRh);
        listViewDiagnostico = (ListView)findViewById(R.id.listViewCHC);
        searchView = (SearchView)findViewById(R.id.searchViewCHC);

        txtFecha.setOnClickListener(this);

        inicializarFireBase();
        listarDatosHistoriaClinica();
        listarDatosPaciente();
        listarDatosDiagnostico();

        String[] rh = {"A+","A-","O+","O-","B+","B-","AB+","AB-"};
        ArrayAdapter<String> adapterRh = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,rh);
        spnRh.setAdapter(adapterRh);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapterDiagnostico.getFilter().filter(newText);
                return false;
            }
        });

        listViewDiagnostico.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                diagnosticoSelected = listaDiagnostico.get(position);
                intent = new Intent(getApplicationContext(), Admin_CrudDiagnostico.class);
                intent.putExtra("idDiagnostico", diagnosticoSelected.getId());
                startActivity(intent);
            }
        });
    }

    //Inicializar Fire Base
    public void inicializarFireBase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    //LLena la lista de Diagnostico con los datos de la BD
    public void listarDatosDiagnostico(){
        databaseReference.child("Diagnostico").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaHC.clear();
                if (snapshot.exists()) {
                    for(DataSnapshot objDataSnapshot: snapshot.getChildren()){
                        Diagnostico objD = objDataSnapshot.getValue(Diagnostico.class);
                        if(objD.getHc().getId().equals(idHC)){
                            listaDiagnostico.add(objD);
                        }
                    }
                    adapterDiagnostico = new CustomAdapterDiagnostico(getApplicationContext(), listaDiagnostico);
                    listViewDiagnostico.setAdapter(adapterDiagnostico);
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
                        if(objHC.getId().equals(idHC) && opc.equals("1")){
                            txtId.setText(objHC.getId());
                            txtFecha.setText(objHC.getFecha_apertura());
                            spnPaciente.setSelection(obtenerPosicionItem(spnPaciente, objHC.getPaciente().getCedula()));
                            txtEstatura.setText(objHC.getEstatura());
                            txtPeso.setText(objHC.getPeso());
                            spnRh.setSelection(obtenerPosicionItem(spnRh,objHC.getRh()));
                            txtEnfermedades.setText(objHC.getEnfermedades());
                        }
                        listaHC.add(objHC);
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
                        spnPaciente.setAdapter(adapterPacientes);
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
        Paciente paciente = retornarPaciente(spnPaciente.getSelectedItem().toString());
        String estatura = txtEstatura.getText().toString();
        String peso = txtPeso.getText().toString();
        String rh = spnRh.getSelectedItem().toString();
        String enfer = txtEnfermedades.getText().toString();

        switch (menuItem.getItemId()){
            case R.id.ic_add:
                if(id.isEmpty() || fecha.isEmpty() || estatura.isEmpty() || peso.isEmpty()|| enfer.isEmpty()){
                    Toast.makeText(this, "Algunos campos estan vacios", Toast.LENGTH_SHORT).show();
                }else{
                    if(!diaSemana().equals("Sabado") && !diaSemana().equals("Domingo")){
                        registrar_actualizar(id,fecha,paciente,estatura,peso,rh,enfer);
                        Toast.makeText(this, "Registro Completado", Toast.LENGTH_SHORT).show();
                        limpiarCajas();
                    }else{
                        Toast.makeText(this, "Seleccione un dia de la semana diferente a: Sabado y domingo", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.ic_save:
                if(id.isEmpty() || fecha.isEmpty() || estatura.isEmpty() || peso.isEmpty()|| enfer.isEmpty()){
                    Toast.makeText(this, "Algunos campos estan vacios", Toast.LENGTH_SHORT).show();
                }else{
                    if(!diaSemana().equals("Sabado") && !diaSemana().equals("Domingo")){
                        registrar_actualizar(id,fecha,paciente,estatura,peso,rh,enfer);
                        Toast.makeText(this, "Informacion Actualizada", Toast.LENGTH_SHORT).show();
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
    private void registrar_actualizar(String id, String fecha, Paciente paciente, String estatura, String peso, String rh, String enfer){
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

    public HistoriaClinica retornarHistoriaClinica(String id){
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txtCHCFecha:
                final Calendar c = Calendar.getInstance();
                int mes = c.get(Calendar.MONTH);
                int dia = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        txtFecha.setText(year+"/"+(month+1)+"/"+dayOfMonth);
                    }
                },2021,mes,dia);
                datePickerDialog.show();
                break;
            default:
                break;
        }
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

    public String diaSemana(){
        Calendar c = Calendar.getInstance();
        String diaSem = "";
        int numD = 0;
        int nDia = Integer.parseInt(recuperarNumDia());
        int nMes = Integer.parseInt(recuperarNumMes());
        int nAnio = Integer.parseInt(recuperarNumAnio());
        c.set(nAnio,nMes,nDia);

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
        txtEstatura.setText("");
        txtPeso.setText("");
        txtEnfermedades.setText("");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            Intent intent = new Intent(getApplicationContext(), Admin_Lista_HC.class);
            intent.putExtra("cedulaAdm", cedulaAdm);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


}
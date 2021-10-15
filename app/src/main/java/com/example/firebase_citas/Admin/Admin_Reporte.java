package com.example.firebase_citas.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebase_citas.Modelo.Diagnostico;
import com.example.firebase_citas.Modelo.Medico;
import com.example.firebase_citas.Modelo.Paciente;
import com.example.firebase_citas.Modelo.Visita;
import com.example.firebase_citas.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
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
import java.util.TimeZone;

public class Admin_Reporte extends AppCompatActivity implements View.OnClickListener{
    private String cedulaAdm;
    private BarChart barChart;
    private Spinner spnTipoUsuario,spnUsuarios,spnUsuarios2,spnTiempo;
    private EditText txtFechaInicio,txtFechaFinal;
    private TextView lblFechaInicio,lblFechaFinal;
    private Button btnAplicar;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private List<Visita> listaVisita = new ArrayList<Visita>();
    private List<Paciente> listaPaciente = new ArrayList<Paciente>();
    private List<Medico> listaMedico = new ArrayList<Medico>();
    private ArrayAdapter<Paciente> adapterPaciente;
    private ArrayAdapter<Medico> adapterMedico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_reporte);

        cedulaAdm = getIntent().getStringExtra("cedulaAdm");
        barChart = (BarChart)findViewById(R.id.barChartReportes);
        spnTipoUsuario = (Spinner)findViewById(R.id.spnRepTipoUsuario);
        spnUsuarios = (Spinner)findViewById(R.id.spnRepFiltroUsuarios);
        spnUsuarios2 = (Spinner)findViewById(R.id.spnRepFiltroUsuarios2);
        spnTiempo = (Spinner)findViewById(R.id.spnRepFiltroTiempo);
        txtFechaInicio = (EditText)findViewById(R.id.txtRepFechaInicio);
        txtFechaFinal = (EditText)findViewById(R.id.txtRepFechaFinal);
        lblFechaInicio = (TextView) findViewById(R.id.lblRepFechaInicio);
        lblFechaFinal = (TextView)findViewById(R.id.lblRepFechaFinal);
        btnAplicar = (Button)findViewById(R.id.btnRepAplicar);

        btnAplicar.setOnClickListener(this);
        txtFechaInicio.setOnClickListener(this);
        txtFechaFinal.setOnClickListener(this);

        String[] tipoUsuario = {"Paciente","Medico"};
        ArrayAdapter<String> adapterTipoUsuario= new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,tipoUsuario);
        spnTipoUsuario.setAdapter(adapterTipoUsuario);

        String[] tiempo = {"Hoy","Rango"};
        ArrayAdapter<String> adapterTiempo= new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,tiempo);
        spnTiempo.setAdapter(adapterTiempo);

        inicializarFireBase();
        listarDatosPaciente();
        listarDatosVisita();
        listarDatosMedico();

        spnTipoUsuario.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spnTipoUsuario.getSelectedItem().toString().equals("Paciente")){
                    spnUsuarios.setVisibility(View.VISIBLE);
                    spnUsuarios2.setVisibility(View.GONE);
                }else if(spnTipoUsuario.getSelectedItem().toString().equals("Medico")){
                    spnUsuarios.setVisibility(View.GONE);
                    spnUsuarios2.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spnTiempo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spnTiempo.getSelectedItem().toString().equals("Hoy")){
                    txtFechaInicio.setVisibility(View.GONE);
                    txtFechaFinal.setVisibility(View.GONE);
                    lblFechaInicio.setVisibility(View.GONE);
                    lblFechaFinal.setVisibility(View.GONE);
                }else if(spnTiempo.getSelectedItem().toString().equals("Rango")){
                    txtFechaInicio.setVisibility(View.VISIBLE);
                    txtFechaFinal.setVisibility(View.VISIBLE);
                    lblFechaInicio.setVisibility(View.VISIBLE);
                    lblFechaFinal.setVisibility(View.VISIBLE);
                }
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
                    }
                }
                adapterPaciente = new ArrayAdapter<Paciente>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,listaPaciente);
                spnUsuarios.setAdapter(adapterPaciente);
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
                    }
                }
                adapterMedico = new ArrayAdapter<Medico>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,listaMedico);
                spnUsuarios2.setAdapter(adapterMedico);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txtRepFechaInicio:
                final Calendar c1 = Calendar.getInstance();
                int mes1 = c1.get(Calendar.MONTH);
                int dia1 = c1.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog1 = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        txtFechaInicio.setText(year+"/"+(month+1)+"/"+dayOfMonth);
                    }
                },2021,mes1,dia1);
                datePickerDialog1.show();
                break;
            case R.id.txtRepFechaFinal:
                 final Calendar c2 = Calendar.getInstance();
                 int mes2 = c2.get(Calendar.MONTH);
                 int dia2 = c2.get(Calendar.DAY_OF_MONTH);
                 DatePickerDialog datePickerDialog2 = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        txtFechaFinal.setText(year+"/"+(month+1)+"/"+dayOfMonth);
                    }
                },2021,mes2,dia2);
                datePickerDialog2.show();
                break;
            case R.id.btnRepAplicar:
                ArrayList<BarEntry> barras = new ArrayList<BarEntry>();
                String fInicio = txtFechaInicio.getText().toString();
                String fFinal = txtFechaFinal.getText().toString();

                if(spnTiempo.getSelectedItem().toString().equals("Hoy")){
                    Calendar calendar = Calendar.getInstance();
                    int anio = 2021;
                    int mes = calendar.get(Calendar.MONTH)+1;
                    int dia = calendar.get(Calendar.DAY_OF_MONTH);
                    String fecha = anio+"/"+mes+"/"+dia;
                    int cont=0;

                    if(spnTipoUsuario.getSelectedItem().equals("Paciente")){
                        for(int i=0;i<listaVisita.size();i++){
                            if(listaVisita.get(i).getPaciente().getCedula().equals(spnUsuarios.getSelectedItem().toString())
                                    && listaVisita.get(i).getFecha().equals(fecha)){
                                cont+=1;
                            }
                        }
                        barras.add(new BarEntry(0,cont));
                    }else if (spnTipoUsuario.getSelectedItem().equals("Medico")) {
                        for(int i=0;i<listaVisita.size();i++){
                            if(listaVisita.get(i).getMedico().getCedula().equals(spnUsuarios2.getSelectedItem().toString())
                                    && listaVisita.get(i).getFecha().equals(fecha)){
                                cont+=1;
                            }
                        }
                        barras.add(new BarEntry(0,cont));
                    }
                }else{
                    if(!fInicio.isEmpty() || !fFinal.isEmpty()) {
                        int diaInicio = Integer.parseInt(recuperarNumDia(fInicio));
                        int mesInicio = Integer.parseInt(recuperarNumMes(fInicio));
                        int anioInicio = Integer.parseInt(recuperarNumAnio(fInicio));
                        int diaFinal = Integer.parseInt(recuperarNumDia(fFinal));
                        int mesFinal = Integer.parseInt(recuperarNumMes(fFinal));
                        int anioFinal = Integer.parseInt(recuperarNumAnio(fFinal));

                        if(anioInicio==anioFinal){
                            if(mesInicio==mesFinal){
                                if (diaInicio<diaFinal) {
                                    List<Visita> listAux = consultaRangoFechas(diaInicio,mesInicio,anioInicio,diaFinal,mesFinal,anioFinal);
                                    if (spnTipoUsuario.getSelectedItem().equals("Paciente")) {
                                        int diaAux=Integer.parseInt(recuperarNumDia(listaVisita.get(0).getFecha())),dia=0;
                                        int contTam=0,contBarras=0;
                                        for(int i=0;i<listAux.size();i++){
                                            dia = Integer.parseInt(recuperarNumDia(listAux.get(i).getFecha()));
                                            if(dia==diaAux){
                                                contTam++;
                                            }else{
                                                diaAux = Integer.parseInt(recuperarNumDia(listaVisita.get(i).getFecha()));
                                                contTam=1;
                                                contBarras++;
                                            }
                                            if(listAux.get(i).getPaciente().getCedula().equals(spnUsuarios.getSelectedItem().toString())){
                                                barras.add(new BarEntry(contBarras,contTam));
                                            }
                                        }
                                    } else if (spnTipoUsuario.getSelectedItem().equals("Medico")) {
                                        int diaAux=Integer.parseInt(recuperarNumDia(listaVisita.get(0).getFecha())),dia=0;
                                        int contTam=0,contBarras=0;
                                        for(int i=0;i<listAux.size();i++){
                                            dia = Integer.parseInt(recuperarNumDia(listAux.get(i).getFecha()));
                                            if(dia==diaAux){
                                                contTam++;
                                            }else{
                                                diaAux = Integer.parseInt(recuperarNumDia(listaVisita.get(i).getFecha()));
                                                contTam=1;
                                                contBarras++;
                                            }
                                            if(listAux.get(i).getMedico().getCedula().equals(spnUsuarios2.getSelectedItem().toString())){
                                                barras.add(new BarEntry(contBarras,contTam));
                                            }
                                        }
                                    }
                                }else{
                                    Toast.makeText(getApplicationContext(),"El dia inicial es superior al dia final",Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(getApplicationContext(),"Debe ingresar el mismo mes",Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(getApplicationContext(),"Debe ingresar el mismo año",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"Los campos de fecha estan vacios",Toast.LENGTH_SHORT).show();
                    }
                }

                BarDataSet barDataSet = new BarDataSet(barras,"Visitas");
                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                barDataSet.setValueTextColor(Color.BLACK);
                barDataSet.setValueTextSize(16f);

                BarData barData = new BarData(barDataSet);
                barChart.setFitBars(true);
                barChart.setData(barData);
                barChart.animateY(2000);
                break;
        }

    }

    public String recuperarNumDia(String fecha){
        String[] vectorFecha;
        String nDia = "";
        vectorFecha  = fecha.split("/");
        nDia = vectorFecha[2];
        return nDia;
    }

    public String recuperarNumMes(String fecha){
        String[] vectorFecha;
        String nMes = "";
        vectorFecha  = fecha.split("/");
        nMes = vectorFecha[1];
        return nMes;
    }

    public String recuperarNumAnio(String fecha){
        String[] vectorFecha;
        String nAnio = "";
        vectorFecha  = fecha.split("/");
        nAnio = vectorFecha[0];
        return nAnio;
    }

    public List<Visita> consultaRangoFechas(int diaI, int mesI, int anioI, int diaF, int mesF, int anioF){
        List<Visita> listAux = new ArrayList<Visita>();
        int dia=0,mes=0,anio=0;
        String fechaSig = "";
        String fechaAnt = "";
        for(int i=0;i<listaVisita.size();i++){
            dia = Integer.parseInt(recuperarNumDia(listaVisita.get(i).getFecha()));
            mes = Integer.parseInt(recuperarNumMes(listaVisita.get(i).getFecha()));
            anio = Integer.parseInt(recuperarNumAnio(listaVisita.get(i).getFecha()));
            if (dia>=diaI && mes==mesI && anio==anioI) {
                if(dia<=diaF && mes==mesF && anio==anioF){
                    listAux.add(listaVisita.get(i));
                }
            }
        }
        return listAux;
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
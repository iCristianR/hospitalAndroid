package com.example.firebase_citas.Medico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.CaseMap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.firebase_citas.Admin.Admin_CrudDiagnostico;
import com.example.firebase_citas.Admin.CustomAdapterDiagnostico;
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
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Medico_HCPDF extends AppCompatActivity {
    private EditText txtId,txtFecha,txtPaciente,txtEstatura,txtPeso,txtRh,txtEnfermedades;
    private ListView listViewDiagnostico;
    private SearchView searchView;
    private String idHC,cedulaMed;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private List<HistoriaClinica> listaHC = new ArrayList<HistoriaClinica>();
    private List<Diagnostico> listaDiagnostico = new ArrayList<Diagnostico>();
    private List<Paciente> listaPaciente = new ArrayList<Paciente>();
    private CustomAdapterDiagnostico adapterDiagnostico;
    private Diagnostico diagnosticoSelected;
    private Intent intent;
    //PDF
    private String NOMBRE_DIRECTORIO="MisPDFs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medico_hc_pdf);

        cedulaMed = getIntent().getStringExtra("cedulaMed");
        idHC = getIntent().getStringExtra("idHC");
        txtId = (EditText)findViewById(R.id.txtPDFHCId);
        txtFecha = (EditText)findViewById(R.id.txtPDFHCFecha);
        txtPaciente = (EditText)findViewById(R.id.txtPDFHCPaciente);
        txtEstatura = (EditText)findViewById(R.id.txtPDFHCEstatura);
        txtPeso = (EditText)findViewById(R.id.txtPDFHCPeso);
        txtRh = (EditText)findViewById(R.id.txtPDFHCRh);
        txtEnfermedades = (EditText)findViewById(R.id.txtPDFHCEnfermedades);
        listViewDiagnostico = (ListView)findViewById(R.id.listViewPDF);
        searchView = (SearchView)findViewById(R.id.searchViewPDF);

        inicializarFireBase();
        listarDatosHistoriaClinica();
        listarDatosDiagnostico();
        listarDatosPaciente();

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

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,},1000);
        }
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
                        if(objHC.getId().equals(idHC)){
                            txtId.setText(objHC.getId());
                            txtFecha.setText(objHC.getFecha_apertura());
                            txtPaciente.setText(objHC.getPaciente().getCedula());
                            txtEstatura.setText(objHC.getEstatura());
                            txtPeso.setText(objHC.getPeso());
                            txtRh.setText(objHC.getRh());
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
        getMenuInflater().inflate(R.menu.menu_pdf,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        String id = txtId.getText().toString();
        String fecha = txtFecha.getText().toString();
        Paciente paciente = retornarPaciente(txtPaciente.getText().toString());
        String estatura = txtEstatura.getText().toString();
        String peso = txtPeso.getText().toString();
        String rh = txtRh.getText().toString();
        String enfer = txtEnfermedades.getText().toString();

        switch (menuItem.getItemId()){
            case R.id.ic_pdf:
                crearPDF(id,fecha,paciente,estatura,peso,rh,enfer);
                Toast.makeText(getApplicationContext(),"El PDF ha sido creado",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }

    public void crearPDF(String id, String fecha, Paciente paciente, String estatura, String peso, String rh, String enfermedades){
        Document documento = new Document();
        String NOMBRE_DOCUMENTO = id+".pdf";
        try{
            File file = crearFichero(NOMBRE_DOCUMENTO);
            FileOutputStream ficheroPDF = new FileOutputStream(file.getAbsolutePath());

            PdfWriter writer = PdfWriter.getInstance(documento,ficheroPDF);

            documento.open();

            Font ti = new Font(Font.TIMES_ROMAN,24,Font.BOLD);
            Paragraph titulo = new Paragraph("HISTORIA CLINICA",ti);
            titulo.setAlignment(Paragraph.ALIGN_CENTER);
            documento.add(titulo);

            documento.add(Chunk.NEWLINE);

            documento.add(new Paragraph("ID Historia: "+id));
            documento.add(new Paragraph("Fecha Apertura: "+fecha));
            documento.add(new Paragraph("Cedula: " +paciente.getCedula()));
            documento.add(new Paragraph("Nombres y Apellidos: "+paciente.getNombre()+" "+paciente.getApellido()));
            documento.add(new Paragraph("Estatura: "+estatura +" cm"));
            documento.add(new Paragraph("Peso: "+peso +" kg"));
            documento.add(new Paragraph("RH: "+rh));
            documento.add(new Paragraph("Enfermedades: "+enfermedades));

            documento.add(Chunk.NEWLINE);

            Font ti1 = new Font(Font.TIMES_ROMAN,18,Font.BOLD);
            Paragraph titulo1 = new Paragraph("DIAGNOSTICOS",ti1);
            titulo1.setAlignment(Paragraph.ALIGN_CENTER);
            documento.add(titulo1);

            documento.add(Chunk.NEWLINE);

            PdfPTable tbl = new PdfPTable(6);
            tbl.setWidthPercentage(100f);
            tbl.setHorizontalAlignment(Element.ALIGN_LEFT);
            tbl.addCell("ID");
            tbl.addCell("Fecha");
            tbl.addCell("Medico");
            tbl.addCell("Especialidad");
            tbl.addCell("Observaciones");
            tbl.addCell("Medicamentos");

            for(int i=0;i<listaDiagnostico.size();i++){
                String idDiag = listaDiagnostico.get(i).getId();
                String Fecha = listaDiagnostico.get(i).getVisita().getFecha();
                String Medico = listaDiagnostico.get(i).getVisita().getMedico().getNombre()+" "+listaDiagnostico.get(i).getVisita().getMedico().getApellido();
                String Especialidad = listaDiagnostico.get(i).getVisita().getMedico().getEspecialidad().getNombre();
                String Observaciones = listaDiagnostico.get(i).getObservaciones();
                String Medicamentos = listaDiagnostico.get(i).getMedicamentos();

                tbl.addCell(idDiag);
                tbl.addCell(Fecha);
                tbl.addCell(Medico);
                tbl.addCell(Especialidad);
                tbl.addCell(Observaciones);
                tbl.addCell(Medicamentos);
            }
            documento.add(tbl);

        }catch (DocumentException e){
        }catch (IOException e){
        }finally {
            documento.close();
        }
    }

    public File crearFichero(String nombre){
        File ruta = getRuta();
        File fichero = null;
        if(ruta != null){
            fichero = new File(ruta,nombre);
        }
        return fichero;
    }

    public File getRuta(){
        File ruta = null;
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            ruta = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),NOMBRE_DIRECTORIO);
            if(ruta!=null){
                if(!ruta.mkdirs()){
                    if(!ruta.exists()){
                        return null;
                    }
                }
            }
        }
        return ruta;
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
            Intent intent = new Intent(getApplicationContext(), Medico_listaHC.class);
            intent.putExtra("cedulaMed", cedulaMed);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


}
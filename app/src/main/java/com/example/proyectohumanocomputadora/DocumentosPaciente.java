package com.example.proyectohumanocomputadora;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DocumentosPaciente extends AppCompatActivity {

    private static final int PICK_INE_FILE = 101;

    private EditText etAlergias, etEnfermedades;
    private Button btnSubirINE, btnGuardar, btnRegresar;
    private LinearLayout layoutDocumentosSubidos;
    private DB db;
    private int idPaciente;
    private Uri uriINE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documetospaciente); // tu XML

        db = new DB(this);

        // Obtener idPaciente de SharedPreferences
        idPaciente = getSharedPreferences("mis_preferencias", MODE_PRIVATE)
                .getInt("idUsuario", -1);
        if (idPaciente == -1) {
            Toast.makeText(this, "Error al identificar usuario", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inicializar vistas
        etAlergias = findViewById(R.id.etAlergias);
        etEnfermedades = findViewById(R.id.etEnfermedades);
        btnSubirINE = findViewById(R.id.btnSubirINE);
        btnGuardar = findViewById(R.id.btnGuardarDocumentos);
        btnRegresar = findViewById(R.id.btnRegresarDashboard); // ahora corresponde al XML
        layoutDocumentosSubidos = findViewById(R.id.layoutDocumentosSubidos);

        // Mostrar documentos subidos previamente
        mostrarDocumentosSubidos();

        // Listeners
        btnSubirINE.setOnClickListener(v -> seleccionarArchivo());
        btnGuardar.setOnClickListener(v -> guardarDocumentos());
        btnRegresar.setOnClickListener(v -> finish());
    }

    private void seleccionarArchivo() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // permitir cualquier tipo de archivo
        startActivityForResult(intent, PICK_INE_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_INE_FILE && resultCode == Activity.RESULT_OK && data != null) {
            uriINE = data.getData();
            btnSubirINE.setText("Archivo seleccionado");
        }
    }

    private void guardarDocumentos() {
        String alergias = etAlergias.getText().toString().trim();
        String enfermedades = etEnfermedades.getText().toString().trim();

        if (alergias.isEmpty()) etAlergias.setError("Responde esta pregunta");
        if (enfermedades.isEmpty()) etEnfermedades.setError("Responde esta pregunta");
        if (alergias.isEmpty() || enfermedades.isEmpty()) return;

        // Guardar respuestas como expediente
        String descripcion = "Alergias: " + alergias + "\nEnfermedades crónicas: " + enfermedades;
        boolean expedienteOk = db.guardarExpediente(idPaciente, descripcion, String.valueOf(System.currentTimeMillis()));

        // Guardar archivo INE
        if (uriINE != null) {
            db.guardarDocumento(idPaciente, "INE", uriINE.toString());
        }

        if (expedienteOk) {
            Toast.makeText(this, "Documentos guardados correctamente", Toast.LENGTH_SHORT).show();
            finish(); // Regresar al dashboard
        } else {
            Toast.makeText(this, "Error al guardar los documentos", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDocumentosSubidos() {
        layoutDocumentosSubidos.removeAllViews();
        Cursor cursor = db.obtenerDocumentos(idPaciente);
        while (cursor.moveToNext()) {
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombreArchivo"));
            TextView tv = new TextView(this);
            tv.setText("- " + nombre);
            layoutDocumentosSubidos.addView(tv);
        }
        cursor.close();
    }
}

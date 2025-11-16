package com.example.proyectohumanocomputadora;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class dashboards extends AppCompatActivity {

    private TextView txtNombrePaciente;
    private Button btnVerCitas, btnAgendar, btnDocumentos, btnCerrarSesion;
    private DB db; // tu clase SQLite

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboards);

        db = new DB(this);

        txtNombrePaciente = findViewById(R.id.textViewNombrePaciente);
        btnVerCitas = findViewById(R.id.buttonVerCitas);
        btnAgendar = findViewById(R.id.buttonAgendar);
        btnDocumentos = findViewById(R.id.buttonDocumentos);
        btnCerrarSesion = findViewById(R.id.buttonCerrarSesion);

        int idPaciente = getIntent().getIntExtra("idUsuario", -1);


        Cursor cursor = db.getReadableDatabase().rawQuery(
                "SELECT nombre, apellido FROM usuarios WHERE id=?",
                new String[]{String.valueOf(idPaciente)}
        );

        if (cursor.moveToFirst()) {
            String nombreCompleto = cursor.getString(0) + " " + cursor.getString(1);
            txtNombrePaciente.setText(nombreCompleto);
        }
        cursor.close();

        // Listener para Ver mis citas
        btnVerCitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(dashboards.this, VerCitasActivity.class);
                intent.putExtra("idPaciente", idPaciente);
                startActivity(intent);
            }
        });
        // Listener para Agendar nueva cita
        btnAgendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(dashboards.this, AgendarCitaActivity.class);
                intent.putExtra("idPaciente", idPaciente);
                startActivity(intent);
            }
        });

/*
        // Listener para Documentos
        btnDocumentos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(dashboards.this, DocumentosActivity.class);
                intent.putExtra("idPaciente", idPaciente);
                startActivity(intent);
            }
        });

        // Listener para Cerrar sesión
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Cierra la activity y vuelve al login
            }
        });*/
    }
}

package com.example.proyectohumanocomputadora;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardsMedico extends AppCompatActivity {

    private TextView textViewNombreMedico;
    private Button buttonGestionCitas, buttonAgendar, buttonVerExpedientes, buttonCerrarSesion;
    private DB db;
    private int idMedico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboards_medico);

        db = new DB(this);

        // Obtener ID del médico desde SharedPreferences
        SharedPreferences prefs = getSharedPreferences("mis_preferencias", MODE_PRIVATE);
        idMedico = prefs.getInt("idUsuario", -1);

        // Inicializar vistas
        textViewNombreMedico = findViewById(R.id.textViewNombreMedico);
        buttonGestionCitas = findViewById(R.id.buttonGestionCitas);
        buttonAgendar = findViewById(R.id.buttonAgendar);
        buttonVerExpedientes = findViewById(R.id.buttonVerExpedientes);
        buttonCerrarSesion = findViewById(R.id.buttonCerrarSesion);

        // Cargar nombre del médico
        cargarNombreMedico();

        // Configurar listeners de los botones
        configurarBotones();
    }

    private void cargarNombreMedico() {
        Cursor cursor = db.getReadableDatabase().rawQuery(
                "SELECT nombre, apellido FROM usuarios WHERE id=?",
                new String[]{String.valueOf(idMedico)}
        );

        if (cursor.moveToFirst()) {
            String nombreCompleto = cursor.getString(0) + " " + cursor.getString(1);
            textViewNombreMedico.setText(nombreCompleto);
        } else {
            textViewNombreMedico.setText("Médico");
        }
        cursor.close();
    }

    private void configurarBotones() {
        // Botón Gestión de Citas
        buttonGestionCitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardsMedico.this, CitasMedicoActivity.class);
                startActivity(intent);
            }
        });

        // Botón agendar
        buttonAgendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardsMedico.this, AgendarCitaMedicoActivity.class);
                startActivity(intent);
            }
        });

        // Botón ver Expediente
        buttonVerExpedientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardsMedico.this, ExpedientesDePacientes.class);
                startActivity(intent);
            }
        });

        // Botón Cerrar Sesión
        buttonCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });
    }

    private void cerrarSesion() {
        // Limpiar SharedPreferences
        SharedPreferences prefs = getSharedPreferences("mis_preferencias", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        // Volver al Login
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar datos si es necesario
        cargarNombreMedico();
    }
}
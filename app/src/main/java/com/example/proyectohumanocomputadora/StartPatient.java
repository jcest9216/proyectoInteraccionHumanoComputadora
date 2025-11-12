package com.example.proyectohumanocomputadora;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class StartPatient extends AppCompatActivity {

    private RecyclerView rvProximasCitas;
    private Button btnAgendar, btnMisCitas, btnDocumentos;
    private TextView tvBienvenida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_patient);

        tvBienvenida = findViewById(R.id.tvBienvenida);
        rvProximasCitas = findViewById(R.id.rvProximasCitas);
        btnAgendar = findViewById(R.id.btnAgendarCita);
        btnMisCitas = findViewById(R.id.btnMisCitas);
        btnDocumentos = findViewById(R.id.btnDocumentos);

        // Obtener nombre del paciente desde intent o base de datos
        String nombrePaciente = getIntent().getStringExtra("nombrePaciente");
        tvBienvenida.setText("¡Hola, " + nombrePaciente + "!");

        // Configurar RecyclerView con próximas citas
        rvProximasCitas.setLayoutManager(new LinearLayoutManager(this));
        // Adapter con datos de SQLite (a implementar)

        // Botones de navegación
        
    }
}

package com.example.proyectohumanocomputadora;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VerCitasActivity extends AppCompatActivity {

    private ListView listViewCitas;
    private TextView tvNoCitas;
    private Button btnVolver;
    private DB db;
    private int idPaciente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_citas);

        db = new DB(this);

        // Obtener ID del paciente desde SharedPreferences
        SharedPreferences prefs = getSharedPreferences("mis_preferencias", MODE_PRIVATE);
        idPaciente = prefs.getInt("idUsuario", -1);

        // Inicializar vistas
        listViewCitas = findViewById(R.id.listViewCitas);
        tvNoCitas = findViewById(R.id.tvNoCitas);
        btnVolver = findViewById(R.id.btnVolver);

        // Cargar citas
        cargarCitas();

        // Configurar botón volver
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Regresar al dashboard
            }
        });
    }

    private void cargarCitas() {
        // Obtener citas del paciente
        Cursor cursorCitas = db.obtenerCitasPaciente(idPaciente);

        if (cursorCitas == null || cursorCitas.getCount() == 0) {
            // No hay citas
            tvNoCitas.setVisibility(View.VISIBLE);
            listViewCitas.setVisibility(View.GONE);
            if (cursorCitas != null) cursorCitas.close();
            return;
        }

        // Crear lista para el adapter
        List<Map<String, String>> citasList = new ArrayList<>();

        while (cursorCitas.moveToNext()) {
            int idMedico = cursorCitas.getInt(cursorCitas.getColumnIndexOrThrow("idMedico"));
            String fecha = cursorCitas.getString(cursorCitas.getColumnIndexOrThrow("fecha"));
            String hora = cursorCitas.getString(cursorCitas.getColumnIndexOrThrow("hora"));
            String motivo = cursorCitas.getString(cursorCitas.getColumnIndexOrThrow("motivo"));
            String estado = cursorCitas.getString(cursorCitas.getColumnIndexOrThrow("estado"));

            // Obtener nombre del médico
            String nombreMedico = obtenerNombreMedico(idMedico);

            // Crear item para la lista
            Map<String, String> citaMap = new HashMap<>();
            citaMap.put("medico", nombreMedico);
            citaMap.put("fecha", fecha);
            citaMap.put("hora", hora);
            citaMap.put("motivo", motivo);
            citaMap.put("estado", estado);

            citasList.add(citaMap);
        }
        cursorCitas.close();

        // Configurar el adapter
        String[] from = {"medico", "fecha", "hora", "motivo", "estado"};
        int[] to = {
                R.id.tvMedico,
                R.id.tvFecha,
                R.id.tvHora,
                R.id.tvMotivo,
        };

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                citasList,
                R.layout.item_cita,
                from,
                to
        );

        listViewCitas.setAdapter(adapter);

        // Mostrar lista y ocultar mensaje de no citas
        listViewCitas.setVisibility(View.VISIBLE);
        tvNoCitas.setVisibility(View.GONE);
    }

    private String obtenerNombreMedico(int idMedico) {
        Cursor cursor = db.getReadableDatabase().rawQuery(
                "SELECT nombre, apellido FROM usuarios WHERE id=?",
                new String[]{String.valueOf(idMedico)}
        );

        String nombreMedico = "Médico no encontrado";
        if (cursor.moveToFirst()) {
            nombreMedico = "Dr. " + cursor.getString(0) + " " + cursor.getString(1);
        }
        cursor.close();

        return nombreMedico;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar citas por si se agendó una nueva
        cargarCitas();
    }
}
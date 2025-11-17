package com.example.proyectohumanocomputadora;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ExpedientesDePacientes extends AppCompatActivity {

    private LinearLayout layoutExpedientes;
    private Button btnVolver;
    private DB db;
    private int idMedico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expedientes_de_pacientes);

        db = new DB(this);

        SharedPreferences prefs = getSharedPreferences("mis_preferencias", MODE_PRIVATE);
        idMedico = prefs.getInt("idUsuario", -1);

        layoutExpedientes = findViewById(R.id.layoutExpedientes);
        btnVolver = findViewById(R.id.btnVolver);

        cargarExpedientes();

        btnVolver.setOnClickListener(v -> finish());
    }

    private void cargarExpedientes() {
        layoutExpedientes.removeAllViews();

        // Obtener pacientes con expedientes que tienen citas con este médico
        Cursor cursor = db.getReadableDatabase().rawQuery(
                "SELECT DISTINCT u.id, u.nombre, u.apellido, e.descripcion, e.fecha " +
                        "FROM usuarios u " +
                        "INNER JOIN citas c ON u.id = c.idPaciente " +
                        "LEFT JOIN expedientes e ON u.id = e.idPaciente " +
                        "WHERE c.idMedico = ? AND u.tipo = 'paciente'",
                new String[]{String.valueOf(idMedico)}
        );

        if (cursor.getCount() == 0) {
            TextView tv = new TextView(this);
            tv.setText("No hay pacientes con expedientes");
            tv.setTextSize(16);
            tv.setPadding(0, 20, 0, 20);
            layoutExpedientes.addView(tv);
            cursor.close();
            return;
        }

        while (cursor.moveToNext()) {
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
            String apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido"));
            String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
            String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));

            // Crear tarjeta para cada paciente
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(16, 16, 16, 16);
            card.setBackgroundResource(android.R.drawable.btn_default);
            card.setElevation(4);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 16);
            card.setLayoutParams(params);

            // Nombre del paciente
            TextView tvNombre = new TextView(this);
            tvNombre.setText("Paciente: " + nombre + " " + apellido);
            tvNombre.setTextColor(0xFF6A0DAD);
            tvNombre.setTextSize(18);
            tvNombre.setPadding(0, 0, 0, 8);

            // Fecha
            TextView tvFecha = new TextView(this);
            tvFecha.setText("Fecha: " + (fecha != null ? fecha : "No especificada"));
            tvFecha.setTextColor(0xFF666666);
            tvFecha.setTextSize(14);
            tvFecha.setPadding(0, 0, 0, 8);

            // Descripción
            TextView tvDescripcion = new TextView(this);
            tvDescripcion.setText("Expediente: " + (descripcion != null ? descripcion : "No tiene expediente"));
            tvDescripcion.setTextColor(0xFF333333);
            tvDescripcion.setTextSize(14);

            card.addView(tvNombre);
            card.addView(tvFecha);
            card.addView(tvDescripcion);

            layoutExpedientes.addView(card);
        }
        cursor.close();
    }
}
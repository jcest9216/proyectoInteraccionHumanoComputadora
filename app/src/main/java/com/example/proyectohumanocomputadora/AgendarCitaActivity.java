package com.example.proyectohumanocomputadora;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AgendarCitaActivity extends AppCompatActivity {

    private Spinner spinnerMedicos;
    private EditText etFecha, etHora, etMotivo;
    private Button btnAgendarCita;
    private DB db;
    private int idPaciente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendar_cita);

        db = new DB(this);

        SharedPreferences prefs = getSharedPreferences("mis_preferencias", MODE_PRIVATE);
        idPaciente = prefs.getInt("idUsuario", -1);

        // Verificar el id es válido
        if (idPaciente == -1) {
            Toast.makeText(this, "Error: No se pudo identificar al usuario", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inicializar vistas
        spinnerMedicos = findViewById(R.id.spinnerMedicos);
        etFecha = findViewById(R.id.etFecha);
        etHora = findViewById(R.id.etHora);
        etMotivo = findViewById(R.id.etMotivo);
        btnAgendarCita = findViewById(R.id.btnAgendarCita);

        // Cargar médicos en el Spinner
        cargarMedicos();

        // Configurar botón
        btnAgendarCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agendarCita();
            }
        });
    }

    private void cargarMedicos() {
        // Obtener todos los usuarios que son médicos
        Cursor cursor = db.getReadableDatabase().rawQuery(
                "SELECT id, nombre, apellido FROM usuarios WHERE tipo='medico'",
                null
        );

        // Verificar si hay médicos disponibles
        if (cursor.getCount() == 0) {
            String[] noMedicos = {"No hay médicos disponibles"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    noMedicos
            );
            spinnerMedicos.setAdapter(adapter);
            spinnerMedicos.setEnabled(false);
            cursor.close();
            return;
        }

        // Crear array para mostrar en el spinner
        String[] medicos = new String[cursor.getCount()];
        final int[] idsMedicos = new int[cursor.getCount()];

        int index = 0;
        while (cursor.moveToNext()) {
            String nombreCompleto = cursor.getString(1) + " " + cursor.getString(2);
            medicos[index] = nombreCompleto;
            idsMedicos[index] = cursor.getInt(0);
            index++;
        }
        cursor.close();

        // Configurar el spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                medicos
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMedicos.setAdapter(adapter);

        // Guardar los IDs en el tag del spinner para acceder después
        spinnerMedicos.setTag(idsMedicos);
    }

    private void agendarCita() {
        // Validar que hay médicos disponibles
        if (spinnerMedicos.getSelectedItem() == null || !spinnerMedicos.isEnabled()) {
            Toast.makeText(this, "No hay médicos disponibles", Toast.LENGTH_SHORT).show();
            return;
        }

        String fecha = etFecha.getText().toString().trim();
        String hora = etHora.getText().toString().trim();
        String motivo = etMotivo.getText().toString().trim();

        if (fecha.isEmpty()) {
            etFecha.setError("Ingresa la fecha");
            return;
        }

        if (hora.isEmpty()) {
            etHora.setError("Ingresa la hora");
            return;
        }

        if (motivo.isEmpty()) {
            etMotivo.setError("Describe el motivo de la cita");
            return;
        }

        // Obtener ID del médico seleccionado
        int selectedPosition = spinnerMedicos.getSelectedItemPosition();
        int[] idsMedicos = (int[]) spinnerMedicos.getTag();
        int idMedico = idsMedicos[selectedPosition];

        // Agendar cita en la base de datos
        boolean exito = db.agendarCita(idPaciente, idMedico, fecha, hora, motivo);

        if (exito) {
            Toast.makeText(this, "Cita agendada exitosamente", Toast.LENGTH_SHORT).show();
            finish(); // Regresar al dashboard
        } else {
            Toast.makeText(this, "Error al agendar la cita", Toast.LENGTH_SHORT).show();
        }
    }
}
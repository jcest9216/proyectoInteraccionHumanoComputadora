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

public class AgendarCitaMedicoActivity extends AppCompatActivity {

    private Spinner spinnerPacientes;
    private EditText etFecha, etHora, etMotivo;
    private Button btnAgendarCita;
    private DB db;
    private int idMedico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendar_cita_medico);

        db = new DB(this);

        // Obtener ID del médico
        SharedPreferences prefs = getSharedPreferences("mis_preferencias", MODE_PRIVATE);
        idMedico = prefs.getInt("idUsuario", -1);

        // Verificar que tenemos un ID válido
        if (idMedico == -1) {
            Toast.makeText(this, "Error: No se pudo identificar al médico", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inicializar vistas
        spinnerPacientes = findViewById(R.id.spinnerPacientes);
        etFecha = findViewById(R.id.etFecha);
        etHora = findViewById(R.id.etHora);
        etMotivo = findViewById(R.id.etMotivo);
        btnAgendarCita = findViewById(R.id.btnAgendarCita);

        // Cargar pacientes en el Spinner
        cargarPacientes();

        // Configurar botón
        btnAgendarCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agendarCita();
            }
        });
    }

    private void cargarPacientes() {
        // Obtener todos los usuarios que son pacientes
        Cursor cursor = db.getReadableDatabase().rawQuery(
                "SELECT id, nombre, apellido FROM usuarios WHERE tipo='paciente'",
                null
        );

        // Verificar si hay pacientes disponibles
        if (cursor.getCount() == 0) {
            String[] noPacientes = {"No hay pacientes disponibles"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    noPacientes
            );
            spinnerPacientes.setAdapter(adapter);
            spinnerPacientes.setEnabled(false);
            cursor.close();
            return;
        }

        // Crear array para mostrar en el spinner
        String[] pacientes = new String[cursor.getCount()];
        final int[] idsPacientes = new int[cursor.getCount()];

        int index = 0;
        while (cursor.moveToNext()) {
            String nombreCompleto = cursor.getString(1) + " " + cursor.getString(2);
            pacientes[index] = nombreCompleto;
            idsPacientes[index] = cursor.getInt(0);
            index++;
        }
        cursor.close();

        // Configurar el spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                pacientes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPacientes.setAdapter(adapter);

        // Guardar los IDs en el tag del spinner para acceder después
        spinnerPacientes.setTag(idsPacientes);
    }

    private void agendarCita() {
        // Validar que hay pacientes disponibles
        if (spinnerPacientes.getSelectedItem() == null || !spinnerPacientes.isEnabled()) {
            Toast.makeText(this, "No hay pacientes disponibles", Toast.LENGTH_SHORT).show();
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

        // Obtener ID del paciente seleccionado
        int selectedPosition = spinnerPacientes.getSelectedItemPosition();
        int[] idsPacientes = (int[]) spinnerPacientes.getTag();
        int idPaciente = idsPacientes[selectedPosition];

        // Agendar cita en la base de datos (el médico es el actual)
        boolean exito = db.agendarCita(idPaciente, idMedico, fecha, hora, motivo);

        if (exito) {
            Toast.makeText(this, "Cita agendada exitosamente", Toast.LENGTH_SHORT).show();
            finish(); // Regresar al dashboard
        } else {
            Toast.makeText(this, "Error al agendar la cita", Toast.LENGTH_SHORT).show();
        }
    }
}
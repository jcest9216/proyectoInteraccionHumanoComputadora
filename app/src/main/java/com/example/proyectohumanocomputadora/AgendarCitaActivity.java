package com.example.proyectohumanocomputadora;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

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

        if (idPaciente == -1) {
            Toast.makeText(this, "Error: No se pudo identificar al usuario", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        spinnerMedicos = findViewById(R.id.spinnerMedicos);
        etFecha = findViewById(R.id.etFecha);
        etHora = findViewById(R.id.etHora);
        etMotivo = findViewById(R.id.etMotivo);
        btnAgendarCita = findViewById(R.id.btnAgendarCita);

        cargarMedicos();

        // 📅 ABRIR CALENDARIO AL TOCAR FECHA
        etFecha.setFocusable(false);
        etFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarCalendario();
            }
        });

        // ⏰ ABRIR RELOJ AL TOCAR HORA
        etHora.setFocusable(false);
        etHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarReloj();
            }
        });

        btnAgendarCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agendarCita();
            }
        });
    }

    private void mostrarCalendario() {
        final Calendar calendario = Calendar.getInstance();

        int año = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int año, int mes, int dia) {
                        etFecha.setText(dia + "/" + (mes + 1) + "/" + año);
                    }
                }, año, mes, dia
        );

        dialog.show();
    }

    private void mostrarReloj() {
        final Calendar calendario = Calendar.getInstance();

        int hora = calendario.get(Calendar.HOUR_OF_DAY);
        int minuto = calendario.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int horaSel, int minutoSel) {
                        etHora.setText(String.format("%02d:%02d", horaSel, minutoSel));
                    }
                }, hora, minuto, true
        );

        dialog.show();
    }

    private void cargarMedicos() {
        Cursor cursor = db.getReadableDatabase().rawQuery(
                "SELECT id, nombre, apellido FROM usuarios WHERE tipo='medico'",
                null
        );

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

        String[] medicos = new String[cursor.getCount()];
        final int[] idsMedicos = new int[cursor.getCount()];

        int index = 0;
        while (cursor.moveToNext()) {
            medicos[index] = cursor.getString(1) + " " + cursor.getString(2);
            idsMedicos[index] = cursor.getInt(0);
            index++;
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                medicos
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMedicos.setAdapter(adapter);

        spinnerMedicos.setTag(idsMedicos);
    }

    private void agendarCita() {
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

        int selectedPosition = spinnerMedicos.getSelectedItemPosition();
        int[] idsMedicos = (int[]) spinnerMedicos.getTag();
        int idMedico = idsMedicos[selectedPosition];

        boolean exito = db.agendarCita(idPaciente, idMedico, fecha, hora, motivo);

        if (exito) {
            Toast.makeText(this, "Cita agendada exitosamente", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al agendar la cita", Toast.LENGTH_SHORT).show();
        }
    }
}

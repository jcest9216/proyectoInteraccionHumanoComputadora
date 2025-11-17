package com.example.proyectohumanocomputadora;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CitasMedicoActivity extends AppCompatActivity {

    private ListView listViewCitasMedico;
    private TextView tvNoCitas;
    private Button btnVolver;
    private DB db;
    private int idMedico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citas_medico);

        db = new DB(this);

        // Obtener ID del médico desde SharedPreferences
        SharedPreferences prefs = getSharedPreferences("mis_preferencias", MODE_PRIVATE);
        idMedico = prefs.getInt("idUsuario", -1);

        // Inicializar vistas
        listViewCitasMedico = findViewById(R.id.listViewCitasMedico);
        tvNoCitas = findViewById(R.id.tvNoCitas);
        btnVolver = findViewById(R.id.btnVolver);

        // Cargar citas del médico
        cargarCitasMedico();

        // Configurar botón volver
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Regresar al dashboard
            }
        });
    }

    private void cargarCitasMedico() {
        // Obtener citas
        Cursor cursorCitas = db.obtenerCitasMedico(idMedico);

        if (cursorCitas == null || cursorCitas.getCount() == 0) {
            // No hay citas
            tvNoCitas.setVisibility(View.VISIBLE);
            listViewCitasMedico.setVisibility(View.GONE);
            if (cursorCitas != null) cursorCitas.close();
            return;
        }

        // Crear lista para el adapter
        List<Map<String, String>> citasList = new ArrayList<>();

        while (cursorCitas.moveToNext()) {
            int idCita = cursorCitas.getInt(cursorCitas.getColumnIndexOrThrow("id"));
            int idPaciente = cursorCitas.getInt(cursorCitas.getColumnIndexOrThrow("idPaciente"));
            String fecha = cursorCitas.getString(cursorCitas.getColumnIndexOrThrow("fecha"));
            String hora = cursorCitas.getString(cursorCitas.getColumnIndexOrThrow("hora"));
            String motivo = cursorCitas.getString(cursorCitas.getColumnIndexOrThrow("motivo"));
            String estado = cursorCitas.getString(cursorCitas.getColumnIndexOrThrow("estado"));

            // Obtener nombre del paciente
            String nombrePaciente = obtenerNombrePaciente(idPaciente);

            // Crear item para la lista
            Map<String, String> citaMap = new HashMap<>();
            citaMap.put("idCita", String.valueOf(idCita));
            citaMap.put("paciente", "Paciente: " + nombrePaciente);
            citaMap.put("fecha", fecha);
            citaMap.put("hora", hora);
            citaMap.put("motivo", motivo);
            citaMap.put("estado", estado);

            citasList.add(citaMap);
        }
        cursorCitas.close();

        // Configurar el adapter
        String[] from = {"paciente", "fecha", "hora", "motivo"};
        int[] to = {
                R.id.tvPaciente,
                R.id.tvFecha,
                R.id.tvHora,
                R.id.tvMotivo,
        };

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                citasList,
                R.layout.item_cita_medico,
                from,
                to
        ) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                // Configurar el botón de cancelar para cada item
                Button btnCancelar = view.findViewById(R.id.btnCancelar);
                Map<String, String> cita = (Map<String, String>) getItem(position);
                String idCita = cita.get("idCita");
                String estado = cita.get("estado");

                // Solo mostrar botón de cancelar si la cita está pendiente
                if ("pendiente".equals(estado)) {
                    btnCancelar.setVisibility(View.VISIBLE);
                    btnCancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cancelarCita(Integer.parseInt(idCita));
                        }
                    });
                } else {
                    btnCancelar.setVisibility(View.GONE);
                }

                return view;
            }
        };

        listViewCitasMedico.setAdapter(adapter);

        // Configurar click listener para los items de la lista
        listViewCitasMedico.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> citaSeleccionada = (Map<String, String>) parent.getItemAtPosition(position);
                String paciente = citaSeleccionada.get("paciente").replace("Paciente: ", "");
                String fecha = citaSeleccionada.get("fecha");
                String hora = citaSeleccionada.get("hora");

                Toast.makeText(CitasMedicoActivity.this,
                        "Cita con " + paciente + " - " + fecha + " " + hora,
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Mostrar lista y ocultar mensaje de no citas
        listViewCitasMedico.setVisibility(View.VISIBLE);
        tvNoCitas.setVisibility(View.GONE);
    }

    private String obtenerNombrePaciente(int idPaciente) {
        Cursor cursor = db.getReadableDatabase().rawQuery(
                "SELECT nombre, apellido FROM usuarios WHERE id=?",
                new String[]{String.valueOf(idPaciente)}
        );

        String nombrePaciente = "Paciente no encontrado";
        if (cursor.moveToFirst()) {
            nombrePaciente = cursor.getString(0) + " " + cursor.getString(1);
        }
        cursor.close();

        return nombrePaciente;
    }

    private void cancelarCita(int idCita) {
        boolean exito = db.actualizarEstadoCita(idCita, "cancelada");
        if (exito) {
            Toast.makeText(this, "Cita cancelada exitosamente", Toast.LENGTH_SHORT).show();
            cargarCitasMedico(); // Recargar la lista
        } else {
            Toast.makeText(this, "Error al cancelar la cita", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar citas por si hubo cambios
        cargarCitasMedico();
    }
}
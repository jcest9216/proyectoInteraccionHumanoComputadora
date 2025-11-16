package com.example.proyectohumanocomputadora;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsuario, etPassword, etNombre, etApellido, etClavePrivada;
    private Button btnRegistrar;
    private TextView tvVolverLogin;
    private DB db;

    //Clave privada para registrar médicos
    private static final String CLAVE_MEDICO = "MED2024";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DB(this);

        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etClavePrivada = findViewById(R.id.etClaveMedico); // <- NUEVO CAMPO
        btnRegistrar = findViewById(R.id.btnRegistrar);
        tvVolverLogin = findViewById(R.id.tvVolverLogin);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String usuario = etUsuario.getText().toString();
                String password = etPassword.getText().toString();
                String nombre = etNombre.getText().toString();
                String apellido = etApellido.getText().toString();
                String clavePrivada = etClavePrivada.getText().toString();

                // Validaciones básicas
                if (usuario.isEmpty() || password.isEmpty() || nombre.isEmpty() || apellido.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Llena todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Si ingresa clave privada correcta → se registra como MÉDICO
                String tipoUsuario;
                if (clavePrivada.equals(CLAVE_MEDICO)) {
                    tipoUsuario = "medico";
                } else {
                    tipoUsuario = "paciente";
                }

                boolean res = db.registrarUsuario(
                        usuario,
                        password,
                        tipoUsuario,
                        nombre,
                        apellido
                );

                if (res) {
                    if (tipoUsuario.equals("medico")) {
                        Toast.makeText(RegisterActivity.this, "Médico registrado correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Usuario registrado", Toast.LENGTH_SHORT).show();
                    }
                    finish(); // volver al login
                } else {
                    Toast.makeText(RegisterActivity.this, "Error, el usuario ya existe", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvVolverLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}

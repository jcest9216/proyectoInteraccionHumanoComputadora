package com.example.proyectohumanocomputadora;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsuario, etPassword, etNombre, etApellido;
    private Button btnRegistrar;
    private DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DB(this);

        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean res = db.registrarUsuario(
                        etUsuario.getText().toString(),
                        etPassword.getText().toString(),
                        "paciente",  // por defecto paciente
                        etNombre.getText().toString(),
                        etApellido.getText().toString()
                );

                if (res) {
                    Toast.makeText(RegisterActivity.this, "Usuario registrado", Toast.LENGTH_SHORT).show();
                    finish(); // cerrar y volver al login
                } else {
                    Toast.makeText(RegisterActivity.this, "Error, usuario ya existe", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

package com.example.proyectohumanocomputadora;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsuario, edtPassword;
    private Button btnLogin;
    private TextView txtRegistrar;
    private DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar base de datos
        db = new DB(this);

        // Vincular componentes
        edtUsuario = findViewById(R.id.edtUsuario);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegistrar = findViewById(R.id.txtRegistrar);

        // Evento botón login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuario = edtUsuario.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                if (usuario.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Ingrese usuario y contraseña", Toast.LENGTH_SHORT).show();
                    return;
                }

                Cursor cursor = db.login(usuario, password);

                if (cursor.moveToFirst()) {
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    String tipo = cursor.getString(cursor.getColumnIndex("tipo"));
                    cursor.close();

                    if (tipo.equals("paciente")) {
                        // Abrir dashboard de paciente
                        Intent intent = new Intent(LoginActivity.this, dashboards.class);
                        intent.putExtra("idPaciente", id);
                        startActivity(intent);
                        finish();
                    } else if (tipo.equals("medico")) {
                        // Aquí podrías abrir dashboard de médico
                        Toast.makeText(LoginActivity.this, "Login de médico correcto", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    cursor.close();
                    Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Evento texto "Registrarse"
        txtRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}

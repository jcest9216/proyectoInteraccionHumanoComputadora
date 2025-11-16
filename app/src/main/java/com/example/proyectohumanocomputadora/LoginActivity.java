package com.example.proyectohumanocomputadora;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvRegister;

    private DB db;  // BASE DE DATOS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        db = new DB(this);

        // BOTÓN LOGIN
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        // IR A REGISTER
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void login() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty()) {
            etUsername.setError("Ingresa tu usuario");
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Ingresa tu contraseña");
            return;
        }

        // 🔎 VERIFICAR EN LA BASE DE DATOS
        Cursor cursor = db.login(username, password);

        if (cursor != null && cursor.moveToFirst()) {

            // OBTENER ID Y NOMBRE DEL USUARIO
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));

            guardarSesion(id, username);

            // IR AL DASHBOARD
            Intent intent = new Intent(LoginActivity.this, dashboards.class);
            intent.putExtra("idUsuario", id);
            intent.putExtra("nombreUsuario", nombre);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarSesion(int idUsuario, String usuario) {
        SharedPreferences prefs = getSharedPreferences("mis_preferencias", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("logueado", true);
        editor.putInt("idUsuario", idUsuario);
        editor.putString("usuario", usuario);
        editor.apply();
    }
}

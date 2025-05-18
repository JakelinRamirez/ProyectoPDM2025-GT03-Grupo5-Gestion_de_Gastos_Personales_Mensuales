package com.example.proyectopdm2025_gt03_grupo5_gestion_de_gastos_personales_mensuales;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.BuildConfig;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    // Variables UI
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin, buttonRegister;
    private ProgressBar progressBar;

    // Firebase Auth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Asegúrate de tener este layout

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Vincular elementos UI
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);
        progressBar = findViewById(R.id.progressBar);

        // Configurar listeners
        buttonLogin.setOnClickListener(v -> loginUser());
        buttonRegister.setOnClickListener(v -> {

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Verificar si el usuario ya está autenticado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            redirectToDashboard();
        }
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validaciones básicas (solo formato)
        if (!validateInputs(email, password)) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);


        new Handler().postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            redirectToDashboard();

            // Opcional: Mostrar mensaje en modo debug
            if (BuildConfig.DEBUG) {
                Toast.makeText(this, "Acceso directo activado (Modo prueba)", Toast.LENGTH_SHORT).show();
            }
        }, 1000); // Pequeño delay para simular carga
    }

    // Mantén el método de validación original


    private boolean validateInputs(String email, String password) {
        if (email.isEmpty()) {
            editTextEmail.setError("Email es requerido");
            editTextEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Email no válido");
            editTextEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Contraseña es requerida");
            editTextPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Mínimo 6 caracteres");
            editTextPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void crearUsuarioEjemplo() {
        String email = "test@ejemplo.com";
        String password = "12345678"; // Mínimo 6 caracteres

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("AUTH", "Usuario creado: " + email);
                        Toast.makeText(this, "Usuario creado exitosamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("AUTH", "Error al crear usuario", task.getException());
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void redirectToDashboard() {
        startActivity(new Intent(Login.this, MainDashboard.class));
        finish(); // Evitar volver atrás con el botón
    }

    // Método para recuperar contraseña (opcional)
    public void onForgotPasswordClick(View view) {
        String email = editTextEmail.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Ingresa tu email");
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Email de recuperación enviado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
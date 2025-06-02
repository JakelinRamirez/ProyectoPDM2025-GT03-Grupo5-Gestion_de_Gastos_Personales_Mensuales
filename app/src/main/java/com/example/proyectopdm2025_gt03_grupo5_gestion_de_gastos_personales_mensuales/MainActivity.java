package com.example.proyectopdm2025_gt03_grupo5_gestion_de_gastos_personales_mensuales;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectopdm2025_gt03_grupo5_gestion_de_gastos_personales_mensuales.ui.dashboard.DashboardActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editCorreo, editContraseña;
    private Button buttonLogin;
    private TextView txtRegistrate;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editCorreo = findViewById(R.id.editCorreo);
        editContraseña = findViewById(R.id.editContraseña);
        buttonLogin = findViewById(R.id.buttonLogin);
        txtRegistrate = findViewById(R.id.Registrate);

        dbHelper = new DBHelper(this);

        buttonLogin.setOnClickListener(view -> verificarUsuario());

        txtRegistrate.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RegistroUsuarioActivity.class);
            startActivity(intent);
        });
    }

    private void verificarUsuario() {
        String correo = editCorreo.getText().toString().trim();
        String contrasena = editContraseña.getText().toString().trim();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Usuario WHERE email = ? AND contrasena = ?", new String[]{correo, contrasena});

        if (cursor.moveToFirst()) {
            // Usuario válido
            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, MainDashboard.class);
            startActivity(intent);
            finish(); // evita volver atrás con botón
        } else {
            Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        db.close();
    }
}




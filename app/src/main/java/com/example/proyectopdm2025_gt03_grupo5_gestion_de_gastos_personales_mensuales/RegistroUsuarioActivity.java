package com.example.proyectopdm2025_gt03_grupo5_gestion_de_gastos_personales_mensuales;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;



public class RegistroUsuarioActivity extends AppCompatActivity {

    private EditText editNombre, editEmail, editContrasena, editRol;
    private Button btnRegistrar;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);

        editNombre     = findViewById(R.id.editNombre);
        editEmail      = findViewById(R.id.editEmail);
        editContrasena = findViewById(R.id.editContrasena);
        editRol        = findViewById(R.id.editRol);
        btnRegistrar   = findViewById(R.id.btnRegistrar);

        dbHelper = new DBHelper(this);

        btnRegistrar.setOnClickListener(view -> registrarUsuario());

        Toolbar toolbar = findViewById(R.id.toolbarRegistro);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");


        // Mostrar la flecha de volver
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


    }

    private void registrarUsuario() {
        String nombre     = editNombre.getText().toString().trim();
        String email      = editEmail.getText().toString().trim();
        String contrasena = editContrasena.getText().toString().trim();
        String rol        = editRol.getText().toString().trim();

        if (nombre.isEmpty() || email.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("email", email);
        values.put("contrasena", contrasena);
        values.put("nombreRol", rol.isEmpty() ? null : rol);

        long result = db.insert("Usuario", null, values);

        if (result == -1) {
            Toast.makeText(this, "Error al registrar. El correo ya existe.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
            editNombre.setText("");
            editEmail.setText("");
            editContrasena.setText("");
            editRol.setText("");

            Intent intent = new Intent(RegistroUsuarioActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        db.close();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // cierra esta Activity y vuelve a la anterior
        return true;
    }

}

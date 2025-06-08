package com.example.proyectopdm2025_gt03_grupo5_gestion_de_gastos_personales_mensuales;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;


import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.core.view.GravityCompat;

import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class ActualizarActivity extends AppCompatActivity {
    private EditText editNombre, editEmail, editContrasena;
    private Button btnGuardar;
    private DBHelper dbHelper;
    private int usuarioId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Usamos el layout base con el menú y la barra
        setContentView(R.layout.activity_base_drawer);

        // Inflar la vista específica de esta actividad dentro del contenedor base
        FrameLayout contentFrame = findViewById(R.id.content_frame);
        View contentView = getLayoutInflater().inflate(R.layout.activity_actualizar, contentFrame, false);
        contentFrame.addView(contentView);

        SharedPreferences prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        usuarioId = prefs.getInt("usuarioId", -1);

        if (usuarioId == -1) {
            Toast.makeText(this, "No se encontró el usuario", Toast.LENGTH_SHORT).show();
            finish(); // o redirigir al login
            return;
        }


        dbHelper = new DBHelper(this);

        editNombre = contentView.findViewById(R.id.editNombre);
        editEmail = contentView.findViewById(R.id.editEmail);
        editContrasena = contentView.findViewById(R.id.editContrasena);
        btnGuardar = contentView.findViewById(R.id.btnGuardar);

        // Configurar el menú lateral

        NavigationView navigationView = findViewById(R.id.navigationView);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Actualizar");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        // Cargar datos del usuario
        Cursor cursor = dbHelper.obtenerUsuarioPorId(usuarioId);
        if (cursor.moveToFirst()) {
            editNombre.setText(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
            editEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            editContrasena.setText(cursor.getString(cursor.getColumnIndexOrThrow("contrasena")));
        }
        cursor.close();

        // Botón guardar cambios
        btnGuardar.setOnClickListener(v -> {
            String nuevoNombre = editNombre.getText().toString();
            String nuevoEmail = editEmail.getText().toString();
            String nuevaContrasena = editContrasena.getText().toString();

            boolean actualizado = dbHelper.actualizarUsuario(usuarioId, nuevoNombre, nuevoEmail, nuevaContrasena);

            if (actualizado) {
                Toast.makeText(this, "Usuario actualizado correctamente", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, ConfiguracionActivity.class);
                startActivity(intent);
                finish(); // Cierra esta actividad para evitar volver atrás con el botón
            } else {
                Toast.makeText(this, "Error al actualizar usuario", Toast.LENGTH_SHORT).show();
            }

        });

        // Mostrar icono del menú (≡)
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.white));
        toolbar.setTitleTextColor(Color.WHITE);


        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.inicio) {
                startActivity(new Intent(this, MainDashboard.class));
            } else if (id == R.id.registrar_gastos) {
                startActivity(new Intent(this, RegistrarGastoActivity.class));
            } else if (id == R.id.analisis) {
                startActivity(new Intent(this, AnalisisActivity.class));
            } else if (id == R.id.objetivos) {
                startActivity(new Intent(this, ObjetivosActivity.class));
            } else if (id == R.id.categorias) {
                startActivity(new Intent(this, CategoriasActivity.class));
            } else if (id == R.id.exportar_datos) {
                startActivity(new Intent(this, ExportarDatosActivity.class));
            } else if (id == R.id.crear_recordatorios) {
                startActivity(new Intent(this, RecordatoriosActivity.class));
            } else if (id == R.id.configuracion) {
                startActivity(new Intent(this, ConfiguracionActivity.class));
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }
}

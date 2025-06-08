package com.example.proyectopdm2025_gt03_grupo5_gestion_de_gastos_personales_mensuales;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class ConfiguracionActivity extends AppCompatActivity {

    private Button btnActualizarUsuario, btnEliminarCuenta, btnCerrarSesion;
    private DBHelper dbHelper;
    private int usuarioId = 1; // Este valor debería venir del login o SharedPreferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Usamos el layout base con el menú y la barra
        setContentView(R.layout.activity_base_drawer);

        // Inflar la vista específica de esta actividad dentro del contenedor base
        FrameLayout contentFrame = findViewById(R.id.content_frame);
        View contentView = getLayoutInflater().inflate(R.layout.activity_configuracion, contentFrame, false);
        contentFrame.addView(contentView);

        btnActualizarUsuario = contentView.findViewById(R.id.btnActualizarUsuario);
        btnEliminarCuenta = contentView.findViewById(R.id.btnEliminarCuenta);
        btnCerrarSesion = contentView.findViewById(R.id.btnCerrarSesion);

        dbHelper = new DBHelper(this);

        // Configurar el menú lateral

        NavigationView navigationView = findViewById(R.id.navigationView);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Configuracion de cuenta");
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        btnActualizarUsuario.setOnClickListener(v -> {
            Intent intent = new Intent(this, ActualizarActivity.class);
            intent.putExtra("usuarioId", usuarioId); // opcional si lo usas
            startActivity(intent);
        });

        btnEliminarCuenta.setOnClickListener(v -> mostrarConfirmacionEliminacion());

        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());


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
            } else if (id == R.id.objetivos) {
                startActivity(new Intent(this, ObjetivosActivity.class));
            } else if (id == R.id.categorias) {
                startActivity(new Intent(this, CategoriasActivity.class));
            } else if (id == R.id.configuracion) {
                startActivity(new Intent(this, ConfiguracionActivity.class));
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void mostrarConfirmacionEliminacion() {
        new AlertDialog.Builder(this)
                .setTitle("¿Eliminar cuenta?")
                .setMessage("¿Estás seguro de que quieres eliminar tu cuenta? Esta acción no se puede deshacer.")
                .setPositiveButton("Sí", (dialog, which) -> {
                    eliminarCuenta();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarCuenta() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int eliminados = db.delete("Usuario", "id = ?", new String[]{String.valueOf(usuarioId)});
        db.close();

        if (eliminados > 0) {
            Toast.makeText(this, "Cuenta eliminada", Toast.LENGTH_SHORT).show();
            cerrarSesion(); // Volver a login o salir
        } else {
            Toast.makeText(this, "Error al eliminar la cuenta", Toast.LENGTH_SHORT).show();
        }
    }

    private void cerrarSesion() {
        // Aquí podrías borrar sesión guardada, SharedPreferences, etc.
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Evita volver con "atrás"
        startActivity(intent);
        finish();
    }
}

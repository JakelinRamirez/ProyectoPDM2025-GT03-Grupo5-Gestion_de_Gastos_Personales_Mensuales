package com.example.proyectopdm2025_gt03_grupo5_gestion_de_gastos_personales_mensuales;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;
import java.util.Locale;

public class ObjetivosActivity extends AppCompatActivity {

    private EditText editMontoLimite, editFechaInicio, editFechaFin;
    private Spinner spinnerTipo;
    private Button btnGuardar;
    private DBHelper dbHelper;
    private int usuarioId = 1; // Reemplazar por el ID del usuario actual si lo tienes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Usar el layout base con menú y barra
        setContentView(R.layout.activity_base_drawer);

        // Inyectar la vista específica de esta actividad
        FrameLayout contentFrame = findViewById(R.id.content_frame);
        View contentView = getLayoutInflater().inflate(R.layout.activity_objetivos, contentFrame, false);
        contentFrame.addView(contentView);

        // Inicializar componentes usando contentView
        dbHelper = new DBHelper(this);
        editMontoLimite = contentView.findViewById(R.id.editMontoLimite);
        editFechaInicio = contentView.findViewById(R.id.editFechaInicio);
        editFechaFin = contentView.findViewById(R.id.editFechaFin);
        spinnerTipo = contentView.findViewById(R.id.spinnerTipo);
        btnGuardar = contentView.findViewById(R.id.btnGuardarObjetivo);

        Button btnVerObjetivos = contentView.findViewById(R.id.btnVerObjetivos);
        btnVerObjetivos.setOnClickListener(v -> {
            startActivity(new Intent(ObjetivosActivity.this, VerObjetivosActivity.class));
        });

        ArrayAdapter<String> tipoAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Mensual"}
        );
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(tipoAdapter);

        editFechaInicio.setOnClickListener(v -> mostrarDatePicker(editFechaInicio));
        editFechaFin.setOnClickListener(v -> mostrarDatePicker(editFechaFin));
        btnGuardar.setOnClickListener(v -> guardarObjetivo());

        // Configurar el menú lateral

        NavigationView navigationView = findViewById(R.id.navigationView);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Agrega Objetivos");
        getSupportActionBar().setDisplayShowTitleEnabled(true);


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
            }  else if (id == R.id.objetivos) {
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

    private void mostrarDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String fecha = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            editText.setText(fecha);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void guardarObjetivo() {
        String montoTxt = editMontoLimite.getText().toString().trim();
        String tipo = spinnerTipo.getSelectedItem().toString();
        String fechaInicio = editFechaInicio.getText().toString().trim();
        String fechaFin = editFechaFin.getText().toString().trim();

        if (montoTxt.isEmpty() || fechaInicio.isEmpty()) {
            Toast.makeText(this, "Completa los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        double montoLimite = Double.parseDouble(montoTxt);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("usuario_id", usuarioId);
        values.put("monto_limite", montoLimite);
        values.put("tipo", tipo);
        values.put("fecha_inicio", fechaInicio);
        values.put("fecha_fin", fechaFin.isEmpty() ? null : fechaFin);

        long result = db.insert("Objetivo", null, values);
        db.close();

        if (result != -1) {
            Toast.makeText(this, "Objetivo guardado", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
        }
    }
}

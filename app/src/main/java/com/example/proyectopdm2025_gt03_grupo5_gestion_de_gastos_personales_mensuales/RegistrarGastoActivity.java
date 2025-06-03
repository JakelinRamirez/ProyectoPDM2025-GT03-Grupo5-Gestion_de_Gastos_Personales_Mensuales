package com.example.proyectopdm2025_gt03_grupo5_gestion_de_gastos_personales_mensuales;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class RegistrarGastoActivity extends AppCompatActivity {

    private EditText editMonto, editFecha, editDescripcion;
    private Spinner spinnerCategoria, spinnerMetodo;
    private Button btnGuardar;
    private DBHelper dbHelper;

    private ArrayAdapter<String> categoriaAdapter, metodoAdapter;
    private ArrayList<Integer> categoriaIds, metodoIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_gasto);

        editMonto = findViewById(R.id.editMonto);
        editFecha = findViewById(R.id.editFecha);
        editDescripcion = findViewById(R.id.editDescripcion);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        spinnerMetodo = findViewById(R.id.spinnerMetodo);
        btnGuardar = findViewById(R.id.btnGuardar);
        Button btnVerGastos = findViewById(R.id.btnVerGastos);
        btnVerGastos.setOnClickListener(v -> {
            startActivity(new Intent(RegistrarGastoActivity.this, VerGastosActivity.class));
        });

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        LinearLayout menuIcon = findViewById(R.id.menuIcon);

        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.inicio) {
                startActivity(new Intent(this, MainDashboard.class));
            } else if (id == R.id.registrar_gastos) {
                startActivity(new Intent(this, RegistrarGastoActivity.class));
            } else if (id == R.id.analisis) {
                startActivity(new Intent(this, AnalisisActivity.class));
            }
            else if  (id == R.id.objetivos) {
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




        dbHelper = new DBHelper(this);

        cargarCategorias();
        cargarMetodosPago();

        btnGuardar.setOnClickListener(v -> registrarGasto());
    }

    private void cargarCategorias() {
        categoriaIds = new ArrayList<>();
        ArrayList<String> nombres = new ArrayList<>();

        // Opción inicial
        nombres.add("Seleccionar categoría");
        categoriaIds.add(-1); // ID inválido para validar

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, nombre FROM Categoria", null);
        while (cursor.moveToNext()) {
            categoriaIds.add(cursor.getInt(0));
            nombres.add(cursor.getString(1));
        }
        cursor.close();

        categoriaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombres);
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(categoriaAdapter);
    }


    private void cargarMetodosPago() {
        metodoIds = new ArrayList<>();
        ArrayList<String> nombres = new ArrayList<>();

        nombres.add("Seleccionar método de pago");
        metodoIds.add(-1);


        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, nombre FROM MetodoPago", null);
        while (cursor.moveToNext()) {
            metodoIds.add(cursor.getInt(0));
            nombres.add(cursor.getString(1));
        }
        cursor.close();
        metodoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombres);
        spinnerMetodo.setAdapter(metodoAdapter);
    }

    private void registrarGasto() {
        String montoStr = editMonto.getText().toString();
        String fecha = editFecha.getText().toString();
        String descripcion = editDescripcion.getText().toString();

        if (montoStr.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Monto y fecha son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        double monto = Double.parseDouble(montoStr);
        int categoriaId = categoriaIds.get(spinnerCategoria.getSelectedItemPosition());
        int metodoId = metodoIds.get(spinnerMetodo.getSelectedItemPosition());
        int usuarioId = 1; // 🔐 usar ID de usuario real cuando tengas login

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("monto", monto);
        values.put("fecha", fecha);
        values.put("descripcion", descripcion);
        values.put("usuario_id", usuarioId);
        values.put("categoria_id", categoriaId);
        values.put("metodo_pago_id", metodoId);

        long resultado = db.insert("Gasto", null, values);

        if (resultado != -1) {
            Toast.makeText(this, "Gasto registrado correctamente", Toast.LENGTH_SHORT).show();
            editMonto.setText("");
            editFecha.setText("");
            editDescripcion.setText("");
        } else {
            Toast.makeText(this, "Error al registrar gasto", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }


}

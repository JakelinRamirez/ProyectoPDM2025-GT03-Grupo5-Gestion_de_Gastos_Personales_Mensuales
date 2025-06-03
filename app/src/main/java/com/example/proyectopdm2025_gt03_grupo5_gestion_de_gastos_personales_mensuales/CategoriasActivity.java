package com.example.proyectopdm2025_gt03_grupo5_gestion_de_gastos_personales_mensuales;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;


import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class CategoriasActivity extends AppCompatActivity {

    private EditText editNombreCategoria;
    private Button btnAgregarCategoria;
    private ListView listViewCategorias;
    private DBHelper dbHelper;

    private ArrayList<String> listaCategorias;
    private ArrayList<Integer> idsCategorias;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Usamos el layout base con el menú y la barra
        setContentView(R.layout.activity_base_drawer);

        // Inflar la vista específica de esta actividad dentro del contenedor base
        FrameLayout contentFrame = findViewById(R.id.content_frame);
        View contentView = getLayoutInflater().inflate(R.layout.activity_categorias, contentFrame, false);
        contentFrame.addView(contentView);

        // Referencias de los componentes específicos de activity_categorias
        editNombreCategoria = contentView.findViewById(R.id.editNombreCategoria);
        btnAgregarCategoria = contentView.findViewById(R.id.btnAgregarCategoria);
        listViewCategorias = contentView.findViewById(R.id.listViewCategorias);
        dbHelper = new DBHelper(this);

        btnAgregarCategoria.setOnClickListener(v -> agregarCategoria());
        cargarCategorias();



        // Configurar el menú lateral

        NavigationView navigationView = findViewById(R.id.navigationView);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Categoria");
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


    private void agregarCategoria() {
        String nombre = editNombreCategoria.getText().toString().trim();
        if (nombre.isEmpty()) {
            Toast.makeText(this, "Ingresa un nombre", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);

        long result = db.insert("Categoria", null, values);
        if (result != -1) {
            Toast.makeText(this, "Categoría agregada", Toast.LENGTH_SHORT).show();
            editNombreCategoria.setText("");
            cargarCategorias();
        } else {
            Toast.makeText(this, "Error al agregar", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }

    private void cargarCategorias() {
        listaCategorias = new ArrayList<>();
        idsCategorias = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, nombre FROM Categoria", null);

        while (cursor.moveToNext()) {
            idsCategorias.add(cursor.getInt(0));
            listaCategorias.add(cursor.getString(1));
        }

        cursor.close();
        db.close();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaCategorias);
        listViewCategorias.setAdapter(adapter);

        listViewCategorias.setOnItemClickListener((parent, view, position, id) -> {
            int idCategoria = idsCategorias.get(position);
            String nombre = listaCategorias.get(position);

            new AlertDialog.Builder(this)
                    .setTitle("Eliminar categoría")
                    .setMessage("¿Deseas eliminar \"" + nombre + "\"?")
                    .setPositiveButton("Sí", (dialog, which) -> eliminarCategoria(idCategoria))
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void eliminarCategoria(int idCategoria) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete("Categoria", "id = ?", new String[]{String.valueOf(idCategoria)});
        db.close();

        if (rows > 0) {
            Toast.makeText(this, "Categoría eliminada", Toast.LENGTH_SHORT).show();
            cargarCategorias();
        } else {
            Toast.makeText(this, "No se pudo eliminar", Toast.LENGTH_SHORT).show();
        }
    }
}

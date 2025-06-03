package com.example.proyectopdm2025_gt03_grupo5_gestion_de_gastos_personales_mensuales;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class VerObjetivosActivity extends AppCompatActivity {

    private ListView listaObjetivos;
    private DBHelper dbHelper;
    private ArrayList<Integer> idsObjetivos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Establece la vista base que tiene el DrawerLayout y el content_frame
        setContentView(R.layout.activity_base_drawer);

        // Inyecta el contenido específico de esta actividad
        FrameLayout contentFrame = findViewById(R.id.content_frame);
        View contentView = getLayoutInflater().inflate(R.layout.activity_ver_objetivos, contentFrame, false);
        contentFrame.addView(contentView);

        // Inicializa la vista dentro del layout inyectado
        listaObjetivos = contentView.findViewById(R.id.listaObjetivos);
        dbHelper = new DBHelper(this);
        cargarObjetivos();

        // Configurar el menú lateral

        NavigationView navigationView = findViewById(R.id.navigationView);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ver Objetivos");
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


    private void cargarObjetivos() {
        ArrayList<String> objetivos = new ArrayList<>();
        idsObjetivos = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, monto_limite, tipo, fecha_inicio, fecha_fin FROM Objetivo ORDER BY fecha_inicio DESC", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            double monto = cursor.getDouble(1);
            String tipo = cursor.getString(2);
            String inicio = cursor.getString(3);
            String fin = cursor.getString(4);

            String info = "Límite: $" + monto + "\nTipo: " + tipo + "\nDesde: " + inicio;
            if (fin != null && !fin.isEmpty()) {
                info += "\nHasta: " + fin;
            }

            objetivos.add(info);
            idsObjetivos.add(id);
        }

        cursor.close();
        db.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, objetivos);
        listaObjetivos.setAdapter(adapter);

        listaObjetivos.setOnItemClickListener((parent, view, position, id) -> {
            int objetivoId = idsObjetivos.get(position);

            new AlertDialog.Builder(this)
                    .setTitle("Eliminar objetivo")
                    .setMessage("¿Deseas eliminar este objetivo?")
                    .setPositiveButton("Sí", (dialog, which) -> eliminarObjetivo(objetivoId))
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void eliminarObjetivo(int idObjetivo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int filas = db.delete("Objetivo", "id = ?", new String[]{String.valueOf(idObjetivo)});
        db.close();

        if (filas > 0) {
            Toast.makeText(this, "Objetivo eliminado", Toast.LENGTH_SHORT).show();
            cargarObjetivos(); // Recargar lista
        } else {
            Toast.makeText(this, "No se pudo eliminar", Toast.LENGTH_SHORT).show();
        }
    }
}

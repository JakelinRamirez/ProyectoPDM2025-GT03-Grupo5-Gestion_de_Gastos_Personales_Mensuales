package com.example.proyectopdm2025_gt03_grupo5_gestion_de_gastos_personales_mensuales;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
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

public class VerGastosActivity extends AppCompatActivity {

    private ListView listaGastos;
    private DBHelper dbHelper;
    private ArrayList<Integer> idsGastos;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Establecer el layout base con barra y menú lateral
        setContentView(R.layout.activity_base_drawer);

        // Inyectar el contenido de la vista específica (activity_ver_gastos.xml)
        FrameLayout contentFrame = findViewById(R.id.content_frame);
        View contentView = getLayoutInflater().inflate(R.layout.activity_ver_gastos, contentFrame, false);
        contentFrame.addView(contentView);

        // Inicializar elementos dentro del layout inyectado
        listaGastos = contentView.findViewById(R.id.listaGastos);
        dbHelper = new DBHelper(this);
        cargarGastos();

          // Configurar el menú lateral

        NavigationView navigationView = findViewById(R.id.navigationView);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ver Gastos");
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


    private void cargarGastos() {
        ArrayList<String> gastos = new ArrayList<>();
        idsGastos = new ArrayList<>(); // Inicializar la lista de IDs

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, monto, fecha, descripcion FROM Gasto ORDER BY fecha DESC", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0); // ← Obtenemos el ID
            double monto = cursor.getDouble(1);
            String fecha = cursor.getString(2);
            String descripcion = cursor.getString(3);

            idsGastos.add(id); // ← Guardamos el ID en la lista
            String info = "Monto: $" + monto + "\nFecha: " + fecha + "\nDescripción: " + descripcion;
            gastos.add(info);
        }

        cursor.close();
        db.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, gastos);
        listaGastos.setAdapter(adapter);

        listaGastos.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < idsGastos.size()) {
                int gastoId = idsGastos.get(position);

                new AlertDialog.Builder(this)
                        .setTitle("Eliminar gasto")
                        .setMessage("¿Deseas eliminar este gasto?")
                        .setPositiveButton("Sí", (dialog, which) -> eliminarGasto(gastoId))
                        .setNegativeButton("No", null)
                        .show();
            } else {
                Toast.makeText(this, "Error al obtener ID del gasto", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void eliminarGasto(int idGasto) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int filas = db.delete("Gasto", "id = ?", new String[]{String.valueOf(idGasto)});
        db.close();

        if (filas > 0) {
            Toast.makeText(this, "Gasto eliminado", Toast.LENGTH_SHORT).show();
            cargarGastos(); // Recargar lista
        } else {
            Toast.makeText(this, "No se pudo eliminar", Toast.LENGTH_SHORT).show();
        }
    }

}

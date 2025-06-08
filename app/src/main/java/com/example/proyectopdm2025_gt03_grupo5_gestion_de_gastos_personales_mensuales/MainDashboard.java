package com.example.proyectopdm2025_gt03_grupo5_gestion_de_gastos_personales_mensuales;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.animation.ObjectAnimator;
import android.Manifest;
import android.content.pm.PackageManager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.navigation.NavigationView;
import com.pdfview.PDFView;
import android.widget.Button;

import java.util.ArrayList;

public class MainDashboard extends AppCompatActivity {

    private static final String CHANNEL_ID = "welcome_channel";
    private static final int NOTIFICATION_ID = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;

    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_dashboard);

        PDFView pdfView = findViewById(R.id.pdfView);
        Button btnShowPDF = findViewById(R.id.button2);

        btnShowPDF.setOnClickListener(v -> {
            if (pdfView.getVisibility() == View.GONE) {
                pdfView.setVisibility(View.VISIBLE);
                pdfView.fromAsset("politicas_privacidad.pdf").show();
                btnShowPDF.setText("Ocultar PDF");
            } else {
                pdfView.setVisibility(View.GONE);
                btnShowPDF.setText("Mostrar PDF");
            }
        });

        pieChart = findViewById(R.id.pieChart);
        Log.d("Dashboard", "PieChart encontrado? " + (pieChart != null));

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT c.nombre, SUM(g.monto) FROM Gasto g " +
                        "JOIN Categoria c ON g.categoria_id = c.id " +
                        "WHERE strftime('%Y-%m', g.fecha) = strftime('%Y-%m', date('now')) " +
                        "GROUP BY c.nombre", null);

        ArrayList<PieEntry> entries = new ArrayList<>();
        double totalGastos = 0;
        while (cursor.moveToNext()) {
            String categoria = cursor.getString(0);
            float monto = (float) cursor.getDouble(1);
            totalGastos += monto;
            entries.add(new PieEntry(monto, categoria));
        }
        cursor.close();

        double objetivoMensual = 0;
        Cursor objetivoCursor = db.rawQuery(
                "SELECT monto_limite FROM Objetivo WHERE tipo = 'Mensual' " +
                        "AND strftime('%Y-%m', fecha_inicio) <= strftime('%Y-%m', date('now')) " +
                        "AND (fecha_fin IS NULL OR strftime('%Y-%m', fecha_fin) >= strftime('%Y-%m', date('now'))) " +
                        "ORDER BY fecha_inicio DESC LIMIT 1", null);

        if (objetivoCursor.moveToFirst()) {
            objetivoMensual = objetivoCursor.getDouble(0);
        }
        objetivoCursor.close();
        db.close();

        TextView prespuestoText = findViewById(R.id.textView9);
        prespuestoText.setText("Gasto Total: $" + String.format("%.2f", totalGastos));

        ProgressBar progressBar = findViewById(R.id.progressBar);
        TextView porcentajeView = findViewById(R.id.textView13);
        int porcentaje = (objetivoMensual > 0) ? (int)((totalGastos / objetivoMensual) * 100) : 0;
        progressBar.setProgress(porcentaje);
        porcentajeView.setText(porcentaje + "%");

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS, 255);
        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.getLegend().setEnabled(false); // Oculta la leyenda completa
        pieChart.invalidate();

        createNotificationChannel();
        checkAndShowNotification();

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

        OneTimeWorkRequest trabajo = new OneTimeWorkRequest.Builder(TareaWorker.class).build();
        WorkManager.getInstance(this).enqueue(trabajo);

        TextView txtMontoPresupuesto = findViewById(R.id.txtMontoPresupuesto);



// Gasto total
        double totalGasto = dbHelper.obtenerTotalGastoDelMes();

// Presupuesto objetivo mensual
        double objetivo = dbHelper.obtenerObjetivoMensual();

// Actualizar texto
        txtMontoPresupuesto.setText("Total de gasto: $" + totalGasto + "\nPresupuesto: $" + objetivo);


    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notificaciones de Bienvenida";
            String description = "Canal para notificaciones al abrir la aplicación";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void checkAndShowNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {
                showWelcomeNotification();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_REQUEST_CODE);
            }
        } else {
            showWelcomeNotification();
        }
    }

    private void showWelcomeNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.w("Notification", "No se tienen permisos para mostrar notificaciones");
                return;
            }
        }

        Intent intent = new Intent(this, MainDashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Bienvenido a Gestión de Gastos")
                .setContentText("¡Comienza a registrar y controlar tus gastos mensuales!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        try {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } catch (SecurityException e) {
            Log.e("Notification", "Error de seguridad al mostrar notificación", e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showWelcomeNotification();
            }
        }
    }
}

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.proyectopdm2025_gt03_grupo5_gestion_de_gastos_personales_mensuales.ui.register.RegisterActivity;
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
        PDFView pdfView;
        Button btnShowPDF;
        pdfView = findViewById(R.id.pdfView);
        btnShowPDF = findViewById(R.id.button2);

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

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(40f, "Comida"));
        entries.add(new PieEntry(30f, "Transporte"));
        entries.add(new PieEntry(20f, "Entretenimiento"));
        entries.add(new PieEntry(10f, "Otros"));

        PieDataSet dataSet = new PieDataSet(entries, "Gastos");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS, 255);
        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        // Configurar barra de progreso
        ProgressBar progressBar = findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setProgress(75);
            ObjectAnimator.ofInt(progressBar, "progress", 0, 75)
                    .setDuration(1000)
                    .start();
        }

        // Configurar notificación de bienvenida
        createNotificationChannel();
        checkAndShowNotification();

        // Configuración para menu desplegable
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        LinearLayout menuIcon = findViewById(R.id.menuIcon);

// Abrir el menú al hacer clic en el ícono
        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

// Manejar clics en los items del menú
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.registrar_gastos) {
                Intent intent = new Intent(MainDashboard.this, RegistrarGastoActivity.class);
                startActivity(intent);
            } else if (id == R.id.analisis) {
                Intent intent = new Intent(MainDashboard.this, AnalisisActivity.class);
                startActivity(intent);
            } else if (id == R.id.objetivos) {
                // Acción para establecer objetivos
            } // ... y así con los demás ítems

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        OneTimeWorkRequest trabajo = new OneTimeWorkRequest.Builder(TareaWorker.class).build();
        WorkManager.getInstance(this).enqueue(trabajo);
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
                // Opcional: puedes explicar al usuario por qué necesitas el permiso
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_REQUEST_CODE);
            }
        } else {
            // Para versiones anteriores a Android 13, no se necesita permiso explícito
            showWelcomeNotification();
        }
    }

    private void showWelcomeNotification() {
        // Verificar si tenemos permiso (solo necesario para Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // No tenemos permiso, no mostrar notificación
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
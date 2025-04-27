package com.example.proyectopdm2025_gt03_grupo5_gestion_de_gastos_personales_mensuales;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import java.util.ArrayList;
import java.util.List;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import androidx.core.content.ContextCompat;
import com.github.mikephil.charting.utils.ColorTemplate;
import android.util.Log;
import android.widget.ProgressBar;
import android.animation.ObjectAnimator;




public class MainDashboard extends AppCompatActivity {

    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        pieChart = findViewById(R.id.pieChart);
        Log.d("Dashboard", "PieChart encontrado? " + (pieChart != null));


        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(40f, "Comida"));
        entries.add(new PieEntry(30f, "Transporte"));
        entries.add(new PieEntry(20f, "Entretenimiento"));
        entries.add(new PieEntry(10f, "Otros"));

        PieDataSet dataSet = new PieDataSet(entries, "Gastos");

        // Solución
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS, 255);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);


        ProgressBar progressBar = findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setProgress(75);
            ObjectAnimator.ofInt(progressBar, "progress", 0, 75)
                    .setDuration(1000)
                    .start();
        }


    }
}

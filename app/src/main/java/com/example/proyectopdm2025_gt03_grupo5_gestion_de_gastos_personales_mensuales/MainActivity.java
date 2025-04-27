package com.example.proyectopdm2025_gt03_grupo5_gestion_de_gastos_personales_mensuales;
import com.example.proyectopdm2025_gt03_grupo5_gestion_de_gastos_personales_mensuales.ui.analysis.AnalysisActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;


public class MainActivity extends AppCompatActivity {
    Spinner comboCategoria;
    Spinner comboCategoria2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);





        // Crear un Intent para abrir AnalysisActivity automáticamente
        Intent intent = new Intent(MainActivity.this, AnalysisActivity.class);
        startActivity(intent);  // Iniciar AnalysisActivity

        // Opcional: Finalizar MainActivity si no quieres que el usuario regrese a esta actividad
        finish();




    }
}


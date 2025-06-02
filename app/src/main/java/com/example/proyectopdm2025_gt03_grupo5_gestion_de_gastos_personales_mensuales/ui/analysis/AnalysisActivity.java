package com.example.proyectopdm2025_gt03_grupo5_gestion_de_gastos_personales_mensuales.ui.analysis;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectopdm2025_gt03_grupo5_gestion_de_gastos_personales_mensuales.R;

public class AnalysisActivity extends AppCompatActivity{
    Spinner comboCategoria, comboCategoria2, comboMes1,comboMes2;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analisis);
        //Arreglo del spinner 1
        comboCategoria=(Spinner) findViewById(R.id.spinnerCategorias);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Categorias, android.R.layout.simple_spinner_item);
        comboCategoria.setAdapter(adapter);
        //Arreglo del spinner 2
        comboCategoria2=(Spinner) findViewById(R.id.spinnerPeriodo);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.Periodos, android.R.layout.simple_spinner_dropdown_item);
        comboCategoria2.setAdapter(adapter2);

        //spinner de mes
        comboMes1=(Spinner) findViewById(R.id.spinnerMes1);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this, R.array.Mes, android.R.layout.simple_spinner_dropdown_item);
        comboMes1.setAdapter(adapter3);

        comboMes2=(Spinner) findViewById(R.id.spinnerMes2);
        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(this, R.array.Mes, android.R.layout.simple_spinner_dropdown_item);
        comboMes2.setAdapter(adapter4);
    }
}

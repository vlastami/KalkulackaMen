package com.example.moneyAndroidApp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;

public class ChartLandscapeActivity extends AppCompatActivity {

    private LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_landscape);

        String baseCurrency = getIntent().getStringExtra("baseCurrency");
        String targetCurrency = getIntent().getStringExtra("targetCurrency");
        String fromDate = getIntent().getStringExtra("fromDate");
        String toDate = getIntent().getStringExtra("toDate");

        chart = findViewById(R.id.chart);

        LineData lineData = ChartDataHolder.getInstance().getLineData();
        if (lineData != null) {
            chart.setData(lineData);
            chart.invalidate();
        } else {
            // Show some error message or do error handling here
        }
    }
}

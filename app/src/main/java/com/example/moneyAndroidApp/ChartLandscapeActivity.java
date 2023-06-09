package com.example.moneyAndroidApp;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;

public class ChartLandscapeActivity extends AppCompatActivity {

    private LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_landscape);

        chart = findViewById(R.id.chart);

        LineData lineData = ChartDataHolder.getInstance().getLineData();
        if (lineData != null) {
            chart.setData(lineData);
            chart.invalidate();
        } else {
            Log.d(TAG, "onCreate: No data to display");
        }
    }
}

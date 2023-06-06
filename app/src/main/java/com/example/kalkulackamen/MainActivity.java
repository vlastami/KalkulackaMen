package com.example.kalkulackamen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button convertButton = findViewById(R.id.convert_button);
        convertButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ConvertActivity.class);
            startActivity(intent);
        });

        Button historicalDataButton = findViewById(R.id.historical_data_button);
        historicalDataButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, HistoricalDataActivity.class);
            startActivity(intent);
        });
    }
}

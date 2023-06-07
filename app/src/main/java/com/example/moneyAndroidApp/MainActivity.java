package com.example.moneyAndroidApp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override //přepisuje metodu ze své nadřazené třídy (AppCompatActivity)
    // nemůže být private, aby mohla být volána androidem
    protected void onCreate(Bundle savedInstanceState) { //lifecycle metoda aktivity (první zavolaná po vytvoření)
        super.onCreate(savedInstanceState); // kvůli android frameworku
        setContentView(R.layout.activity_main); //přiřazujeme xml

        Button convertButton = findViewById(R.id.convert_button); //objekt Button - hledáme podle id
        convertButton.setOnClickListener(v -> { //onClick event, lambda výraz
            Intent intent = new Intent(this, ConvertActivity.class); //zavolat ConvertActivity
            startActivity(intent); // odešle request do ConvertActivity
        });

        Button historicalDataButton = findViewById(R.id.historical_data_button);
        historicalDataButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, HistoricalDataActivity.class);
            startActivity(intent);
        });
    }
}

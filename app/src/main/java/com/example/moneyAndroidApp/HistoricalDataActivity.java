package com.example.moneyAndroidApp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;

import java.util.Calendar;

public class HistoricalDataActivity extends AppCompatActivity {

    private EditText fromDateField;
    private EditText toDateField;
    private Spinner baseCurrencySpinner;
    private Spinner targetCurrencySpinner;
    private Button displayDataButton;
    private Button landscapeButton;
    private LineChart chart;
    private LineData lineData;
    private String baseCurrency;
    private String targetCurrency;
    private String fromDate;
    private String toDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historical_data);

        fromDateField = findViewById(R.id.from_date_field);
        toDateField = findViewById(R.id.to_date_field);
        baseCurrencySpinner = findViewById(R.id.base_currency_spinner);
        targetCurrencySpinner = findViewById(R.id.target_currency_spinner);
        displayDataButton = findViewById(R.id.display_data_button);
        chart = findViewById(R.id.chart);
        landscapeButton = findViewById(R.id.landscape_button);

        landscapeButton.setOnClickListener(v -> {
            ChartDataHolder.getInstance().setLineData(lineData); //data se uloží sem
            Intent intent = new Intent(this, ChartLandscapeActivity.class);
            intent.putExtra("baseCurrency", baseCurrency);
            intent.putExtra("targetCurrency", targetCurrency);
            intent.putExtra("fromDate", fromDate);
            intent.putExtra("toDate", toDate);
            startActivity(intent);
        });

        fromDateField.setOnClickListener(v -> showDatePickerDialog(fromDateField));
        toDateField.setOnClickListener(v -> showDatePickerDialog(toDateField));

        String[] currencies = {"USD", "EUR"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies); // druh layoutu
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        baseCurrencySpinner.setAdapter(adapter);
        targetCurrencySpinner.setAdapter(adapter);

        HistoricalDataService historicalDataService = new HistoricalDataService(this, chart);

        displayDataButton.setOnClickListener(v -> {
            baseCurrency = baseCurrencySpinner.getSelectedItem().toString();
            targetCurrency = targetCurrencySpinner.getSelectedItem().toString();
            fromDate = fromDateField.getText().toString();
            toDate = toDateField.getText().toString();
            TextView descriptionText = findViewById(R.id.description_text);

            historicalDataService.fetchHistoricalData(fromDate, toDate, baseCurrency, targetCurrency, new HistoricalDataService.DataFetchedCallback() {
                @Override
                public void onDataFetched(LineData fetchedLineData) {
                    lineData = fetchedLineData;
                    chart.setData(lineData);
                    String description = "This chart shows the exchange rate from " + baseCurrency + " to " + targetCurrency + " for each day from " + fromDate + " to " + toDate + ".";
                    descriptionText.setText(description);
                    chart.invalidate();
                }
            });
        });
    }

    private void showDatePickerDialog(EditText dateField) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (DatePicker view, int year1, int monthOfYear, int dayOfMonth) ->
                        dateField.setText(String.format("%04d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth)),
                year,
                month,
                day);
        datePickerDialog.show();
    }
}

package com.example.kalkulackamen;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.Calendar;
import java.util.List;

public class HistoricalDataActivity extends AppCompatActivity {

    private EditText fromDateField;
    private EditText toDateField;
    private Spinner baseCurrencySpinner;
    private Spinner targetCurrencySpinner;
    private Button displayDataButton;
    private LineChart chart;

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

        fromDateField.setOnClickListener(v -> showDatePickerDialog(fromDateField));
        toDateField.setOnClickListener(v -> showDatePickerDialog(toDateField));

        String[] currencies = {"USD", "EUR"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        baseCurrencySpinner.setAdapter(adapter);
        targetCurrencySpinner.setAdapter(adapter);

        HistoricalDataService historicalDataService = new HistoricalDataService(this, chart);

        displayDataButton.setOnClickListener(v -> {
            String baseCurrency = baseCurrencySpinner.getSelectedItem().toString();
            String targetCurrency = targetCurrencySpinner.getSelectedItem().toString();
            String fromDate = fromDateField.getText().toString();
            String toDate = toDateField.getText().toString();

            historicalDataService.fetchHistoricalData(fromDate, toDate, baseCurrency, targetCurrency, new HistoricalDataService.DataFetchedCallback() {
                @Override
                public void onDataFetched(LineData lineData) {
                    chart.setData(lineData);
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

    private void populateChart(LineChart chart, List<Entry> entries) {
        LineDataSet dataSet = new LineDataSet(entries, "Exchange Rate"); // add entries to dataset
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate(); // refresh
    }
}

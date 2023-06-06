package com.example.kalkulackamen;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the chart
        LineChart chart = findViewById(R.id.chart);

        // Initialize the services
        ExchangeRateService exchangeRateService = new ExchangeRateService();
        HistoricalDataService historicalDataService = new HistoricalDataService(this, chart);
        CurrencyConverter currencyConverter = new CurrencyConverter(exchangeRateService);

        // Populate the spinners
        String[] currencies = {"CZK", "EUR", "USD"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);

        Spinner inputCurrencySpinner = findViewById(R.id.input_currency);
        inputCurrencySpinner.setAdapter(adapter);

        Spinner outputCurrencySpinner = findViewById(R.id.output_currency);
        outputCurrencySpinner.setAdapter(adapter);

        inputCurrencySpinner.setSelection(adapter.getPosition("USD"));
        outputCurrencySpinner.setSelection(adapter.getPosition("EUR"));

        // Set up the convert button
        EditText inputAmountEditText = findViewById(R.id.input_amount);
        TextView outputAmountTextView = findViewById(R.id.output_amount);
        Button convertButton = findViewById(R.id.convert_button);

        // Initially disable the convert button
        convertButton.setEnabled(false);

        // Set up the convert button click listener
        convertButton.setOnClickListener(v -> {
            String inputCurrency = inputCurrencySpinner.getSelectedItem().toString();
            String outputCurrency = outputCurrencySpinner.getSelectedItem().toString();
            double inputAmount = Double.parseDouble(inputAmountEditText.getText().toString());

            double outputAmount = currencyConverter.convert(inputCurrency, outputCurrency, inputAmount);
            outputAmountTextView.setText(decimalFormat.format(outputAmount));

            historicalDataService.fetchHistoricalData(inputCurrency, outputCurrency);
        });

        // Fetch exchange rates
        exchangeRateService.fetchExchangeRates(() -> convertButton.setEnabled(true));
    }
}

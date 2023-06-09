package com.example.moneyAndroidApp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ConvertActivity extends AppCompatActivity {

    private Spinner inputCurrencySpinner;
    private Spinner targetCurrencySpinner;
    private EditText amountField;
    private TextView resultField;
    private CurrencyConverter currencyConverter;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //protected je dostupné v package a potomcích, private ve třídě, public všude
        super.onCreate(savedInstanceState); //kdybych tu neměla super, tak se onCreate předka nezavolá, protože overridujeme tady
        setContentView(R.layout.activity_convert);

        inputCurrencySpinner = findViewById(R.id.input_currency_spinner);
        targetCurrencySpinner = findViewById(R.id.target_currency_spinner);
        amountField = findViewById(R.id.amount_field);
        resultField = findViewById(R.id.result_field);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.currency_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        inputCurrencySpinner.setAdapter(adapter);
        targetCurrencySpinner.setAdapter(adapter);

        ExchangeRateService exchangeRateService = new ExchangeRateService(); //bylo by lepší mít ji deklarovanou na úrovni třídy??
        currencyConverter = new CurrencyConverter(exchangeRateService);

        Button convertButton = findViewById(R.id.start_conversion_button);
        convertButton.setOnClickListener(v -> {

            String inputCurrency = inputCurrencySpinner.getSelectedItem().toString();
            String targetCurrency = targetCurrencySpinner.getSelectedItem().toString();
            double amount = Double.parseDouble(amountField.getText().toString());


            exchangeRateService.fetchExchangeRates(() -> {
                double result = currencyConverter.convert(inputCurrency, targetCurrency, amount);


                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());


                runOnUiThread(() -> resultField.setText(String.format(Locale.getDefault(),
                        "On %s, your input of %s %s equals %.2f %s based on the current exchange rate.",
                        currentDate,
                        amount,
                        inputCurrency,
                        result,
                        targetCurrency
                )));
            });
        });
    }
}


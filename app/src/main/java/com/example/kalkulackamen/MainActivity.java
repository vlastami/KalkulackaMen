package com.example.kalkulackamen;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Color;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class MainActivity extends AppCompatActivity {
    private double czkToEurRate;
    private double czkToUsdRate;
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Populate the spinners
        String[] currencies = {"CZK", "EUR", "USD"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);

        Spinner inputCurrencySpinner = findViewById(R.id.input_currency);
        inputCurrencySpinner.setAdapter(adapter);

        Spinner outputCurrencySpinner = findViewById(R.id.output_currency);
        outputCurrencySpinner.setAdapter(adapter);

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

            double outputAmount = convert(inputCurrency, outputCurrency, inputAmount);
            outputAmountTextView.setText(decimalFormat.format(outputAmount));

            fetchHistoricalData(inputCurrency, outputCurrency);
        });

        // Fetch exchange rates
        fetchExchangeRates(convertButton);
    }

    private double convert(String inputCurrency, String outputCurrency, double amount) {
        if (inputCurrency.equals("CZK")) {
            if (outputCurrency.equals("EUR")) {
                return amount / czkToEurRate;
            } else if (outputCurrency.equals("USD")) {
                return amount / czkToUsdRate;
            } else {
                return amount;
            }
        } else if (inputCurrency.equals("EUR")) {
            if (outputCurrency.equals("CZK")) {
                return amount * czkToEurRate;
            } else if (outputCurrency.equals("USD")) {
                return amount * czkToEurRate / czkToUsdRate;
            } else {
                return amount;
            }
        } else if (inputCurrency.equals("USD")) {
            if (outputCurrency.equals("CZK")) {
                return amount * czkToUsdRate;
            } else if (outputCurrency.equals("EUR")) {
                return amount * czkToUsdRate / czkToEurRate;
            } else {
                return amount;
            }
        } else {
            return 0.0;
        }
    }

    private void fetchExchangeRates(Button convertButton) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://www.cnb.cz/cs/financni_trhy/devizovy_trh/kurzy_devizoveho_trhu/denni_kurz.xml")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        Log.i("fetchExchangeRates", "Response: " + responseBody);

                        XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
                        XmlPullParser myParser = xmlFactoryObject.newPullParser();

                        myParser.setInput(new StringReader(responseBody));
                        int event = myParser.getEventType();
                        while (event != XmlPullParser.END_DOCUMENT) {
                            String name = myParser.getName();
                            switch (event) {
                                case XmlPullParser.START_TAG:
                                    if (name.equals("radek")) {
                                        String currencyCode = myParser.getAttributeValue(null, "kod");
                                        if (currencyCode.equals("EUR") || currencyCode.equals("USD")) {
                                            double rate = Double.parseDouble(myParser.getAttributeValue(null, "kurz").replace(",", "."));
                                            double quantity = Double.parseDouble(myParser.getAttributeValue(null, "mnozstvi"));
                                            double ratePerUnit = rate / quantity;

                                            if (currencyCode.equals("EUR")) {
                                                czkToEurRate = ratePerUnit;
                                            } else if (currencyCode.equals("USD")) {
                                                czkToUsdRate = ratePerUnit;
                                            }
                                        }
                                    }
                                    break;
                            }
                            event = myParser.next();
                        }

                        // Enable the convert button after the exchange rates have been fetched
                        runOnUiThread(() -> convertButton.setEnabled(true));

                    } catch (XmlPullParserException | IOException e) {
                        e.printStackTrace();
                        Log.e("fetchExchangeRates", "Parsing failed", e);
                    }
                } else {
                    Log.e("fetchExchangeRates", "Response not successful. Code: " + response.code());
                }
            }
        });
    }


    private void fetchHistoricalData(String baseCurrency, String targetCurrency) {
        OkHttpClient client = new OkHttpClient();

        String url = "https://api.frankfurter.app/" + baseCurrency + ".." + targetCurrency + "?period=1w";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    // Parse the response
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        JSONObject rates = json.getJSONObject("rates");

                        ArrayList<Entry> entries = new ArrayList<Entry>();

                        Iterator<String> dates = rates.keys();
                        int index = 0;
                        while (dates.hasNext()) {
                            String date = dates.next();
                            double rate = rates.getJSONObject(date).getDouble(targetCurrency);
                            entries.add(new Entry(index++, (float) rate));
                        }

                        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
                        dataSet.setColor(Color.RED);
                        dataSet.setValueTextColor(Color.BLACK);

                        LineData lineData = new LineData(dataSet);

                        runOnUiThread(() -> {
                            LineChart chart = findViewById(R.id.chart);
                            chart.setData(lineData);
                            chart.invalidate(); // refresh
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}

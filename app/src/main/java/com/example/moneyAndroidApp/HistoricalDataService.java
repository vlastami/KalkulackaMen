package com.example.moneyAndroidApp;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class HistoricalDataService {
    private Activity activity;
    private LineChart chart;

    public interface DataFetchedCallback { // interface pro callback - funkce se zavolá, když jsou data úspěšně načtená (aktualizace dat v grafu)
        void onDataFetched(LineData lineData);
    }

    public HistoricalDataService(Activity activity, LineChart chart) { //konstruktor třídy
        this.activity = activity;
        this.chart = chart;
    }

    //stahování historických dat pomocí knihovny OkHttpClient
    public void fetchHistoricalData(String fromDate, String toDate, String baseCurrency, String targetCurrency, DataFetchedCallback callback) {
        if (!targetCurrency.equals("USD") && !targetCurrency.equals("EUR")) {
            return;
        }

        OkHttpClient client = new OkHttpClient(); // asynchronní požadavek

        String baseUrl = "https://api.frankfurter.app/";

        String url = baseUrl + fromDate + ".." + toDate + "?from=" + baseCurrency + "&to=" + targetCurrency;


        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            //Kód bude spuštěn, pokud dojde k chybě při pokusu o provedení požadavku
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Kód bude spuštěn, pokud požadavek úspěšně vrátí odpověď
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    try {
                        String responseString = response.body().string();
                        Log.d("HistoricalDataService", "Response: " + responseString);

                        JSONObject json = new JSONObject(responseString);
                        JSONObject rates = new JSONObject(responseString).getJSONObject("rates");
                        Iterator<String> dates = rates.keys();

                        ArrayList<Entry> entries = new ArrayList<>();
                        int i = 0;
                        while (dates.hasNext()) {
                            String date = dates.next();
                            double rate = rates.getJSONObject(date).getDouble(targetCurrency);
                            Log.d("HistoricalDataService", "Entry: " + i + " => Date: " + date + ", Rate: " + rate);
                            entries.add(new Entry(i++, (float) rate));
                        }

                        LineDataSet dataSet = new LineDataSet(entries, "Historical Data");
                        Log.d("HistoricalDataService", "DataSet: " + dataSet.toString());

                        dataSet.setColor(Color.RED);
                        dataSet.setValueTextColor(Color.BLACK);

                        LineData lineData = new LineData(dataSet);
                        Log.d("HistoricalDataService", "LineData: " + lineData.toString());

                        activity.runOnUiThread(() -> {
                            callback.onDataFetched(lineData);
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}

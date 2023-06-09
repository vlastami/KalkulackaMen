package com.example.moneyAndroidApp;

import okhttp3.*;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.StringReader;

public class ExchangeRateService {
    private double czkToEurRate;
    private double czkToUsdRate;

    public double getCzkToEurRate() {
        return czkToEurRate;
    }

    public double getCzkToUsdRate() {
        return czkToUsdRate;
    }

    public void fetchExchangeRates(Runnable onRatesFetched) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://www.cnb.cz/cs/financni_trhy/devizovy_trh/kurzy_devizoveho_trhu/denni_kurz.xml")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException { //doporučení ide
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();

                        XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
                        XmlPullParser myParser = xmlFactoryObject.newPullParser();

                        myParser.setInput(new StringReader(responseBody));
                        int event = myParser.getEventType();
                        while (event != XmlPullParser.END_DOCUMENT) {
                            String name = myParser.getName();
                            if (event == XmlPullParser.START_TAG && name.equals("radek")) {
                                String currencyCode = myParser.getAttributeValue(null, "kod");
                                if (currencyCode.equals("EUR") || currencyCode.equals("USD")) {
                                    double rate = Double.parseDouble(myParser.getAttributeValue(null, "kurz")
                                            .replace(",", ".")
                                    );
                                    double quantity = Double.parseDouble(myParser.getAttributeValue(null, "mnozstvi"));
                                    double ratePerUnit = rate / quantity;

                                    if (currencyCode.equals("EUR")) {
                                        czkToEurRate = ratePerUnit;
                                    } else {
                                        czkToUsdRate = ratePerUnit;
                                    }
                                }
                            }
                            event = myParser.next();
                        }

                        new Handler(Looper.getMainLooper()).post(onRatesFetched);

                    } catch (XmlPullParserException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    throw new IOException("Unexpected response code " + response.code());
                }
            }
        });
    }
}

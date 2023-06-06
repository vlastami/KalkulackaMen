package com.example.kalkulackamen;

import okhttp3.*;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import android.os.Handler;
import android.os.Looper;

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
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();

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

                        // Run the passed in Runnable after the exchange rates have been fetched
                        // Need to make sure it runs on the UI thread
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

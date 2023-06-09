package com.example.moneyAndroidApp;

import java.text.DecimalFormat;

public class CurrencyConverter {
    private ExchangeRateService exchangeRateService;
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public CurrencyConverter(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    public double convert(String inputCurrency, String outputCurrency, double amount) {
        double czkToEurRate = exchangeRateService.getCzkToEurRate();
        double czkToUsdRate = exchangeRateService.getCzkToUsdRate();

        switch (inputCurrency) { //breaky jsou v Javě dobrovolné, máme returny
            case "CZK":
                if (outputCurrency.equals("EUR")) {
                    return amount / czkToEurRate;
                }
                if (outputCurrency.equals("USD")) {
                    return amount / czkToUsdRate;
                }
                return amount;

            case "EUR":
                if (outputCurrency.equals("CZK")) {
                    return amount * czkToEurRate;
                }
                if (outputCurrency.equals("USD")) {
                    return amount * czkToEurRate / czkToUsdRate;
                }
                return amount;

            case "USD":
                if (outputCurrency.equals("CZK")) {
                    return amount * czkToUsdRate;
                }
                if (outputCurrency.equals("EUR")) {
                    return amount * czkToUsdRate / czkToEurRate;
                }
                return amount;

        }
        return 0.0;
    }
}

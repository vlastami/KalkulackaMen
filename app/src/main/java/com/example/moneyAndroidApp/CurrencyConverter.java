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
}

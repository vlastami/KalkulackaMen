package com.example.moneyAndroidApp;

import com.github.mikephil.charting.data.LineData;

// singleton - mohu získat instanci této třídy pouze jednou
public class ChartDataHolder {
    private static final ChartDataHolder instance = new ChartDataHolder(); //při zavolání getInstance se mi vždy vrátí tato instance;
    private LineData lineData;

    private ChartDataHolder() {}

    public static ChartDataHolder getInstance() { //statická metoda se volá na třídě
        return instance;
    }

    public LineData getLineData() {
        return lineData;
    }

    public void setLineData(LineData lineData) {
        this.lineData = lineData;
    }
}


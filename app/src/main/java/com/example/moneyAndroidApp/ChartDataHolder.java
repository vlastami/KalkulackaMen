package com.example.moneyAndroidApp;

import com.github.mikephil.charting.data.LineData;

public class ChartDataHolder {
    private static final ChartDataHolder instance = new ChartDataHolder();
    private LineData lineData;

    private ChartDataHolder() {}

    public static ChartDataHolder getInstance() {
        return instance;
    }

    public LineData getLineData() {
        return lineData;
    }

    public void setLineData(LineData lineData) {
        this.lineData = lineData;
    }
}


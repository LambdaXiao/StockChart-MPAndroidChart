package com.github.mikephil.charting.stockChart.model.bean;


import com.github.mikephil.charting.stockChart.model.KLineDataModel;

import java.util.ArrayList;

import static java.lang.Float.NaN;

/**
 * Created by Administrator on 2018/3/19.
 */

public class SMAEntity {
    private ArrayList<Float> SMAs;

    public SMAEntity(ArrayList<KLineDataModel> kLineBeen, int n) {
        this(kLineBeen, n, NaN);
    }

    public SMAEntity(ArrayList<KLineDataModel> kLineBeen, int n, float defult) {
        SMAs = new ArrayList<>();

        float sma = 0.0f;
        int index = n - 1;
        if (kLineBeen != null && kLineBeen.size() > 0) {
            for (int i = 0; i < kLineBeen.size(); i++) {
                if (i >= index) {
                    sma = getSMA(sma, (float) kLineBeen.get(i).getClose(), n, 1);
                } else {
                    sma = (float) kLineBeen.get(i).getClose();
                }
                SMAs.add(sma);
            }
        }
    }

    public float getSMA(float perSMA, float close, int n, int m) {
        return close * m / n + perSMA * (n - m) / n;
    }

    public ArrayList<Float> getSMAs() {
        return SMAs;
    }
}

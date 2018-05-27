package com.github.mikephil.charting.stockChart.model.bean;


import com.github.mikephil.charting.stockChart.model.KLineDataModel;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Float.NaN;

/**
 * Created by Administrator on 2018/3/14.
 */

public class CCIEntity {
    private ArrayList<Float> CCIs;

    public CCIEntity(ArrayList<KLineDataModel> kLineBeen, int n) {
        this(kLineBeen, n, NaN);
    }

    public CCIEntity(List<KLineDataModel> kLineBeen, int n, float defult) {
        CCIs = new ArrayList<>();

        float cci = 0.0f;
        float typ = 0.0f;
        float ma = 0.0f;
        float avedev = 0.0f;
        if (kLineBeen != null && kLineBeen.size() > 0) {
            {
                for (int i = 0; i < kLineBeen.size(); i++) {
                    int index = n - 1;
                    typ = (float) ((kLineBeen.get(i).getHigh() + kLineBeen.get(i).getLow() + kLineBeen.get(i).getClose()) / 3);
                    if (i >= index) {
                        ma = getSum(i - index, i, kLineBeen) / n;
                        avedev = getSMA(avedev, (float) Math.abs(kLineBeen.get(i).getClose() - ma), n);
                    } else {
                        ma = defult;
                        avedev = (float) Math.abs(kLineBeen.get(i).getClose());
                    }
                    cci = (float) ((typ - ma) / (0.015 * avedev));
                    CCIs.add(cci);
                }
            }
        }
    }

    private static float getSum(Integer a, Integer b, List<KLineDataModel> datas) {
        float sum = 0;
        for (int i = a; i <= b; i++) {
            sum += datas.get(i).getClose();
        }
        return sum;
    }

    private static float getSMA(float perSAM, float price, int n) {
        return perSAM * (n - 1) / n + price * 1 / n;
    }

    public ArrayList<Float> getCCIs() {
        return CCIs;
    }

}

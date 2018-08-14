package com.github.mikephil.charting.stockChart.model.bean;

import com.github.mikephil.charting.stockChart.model.KLineDataModel;

import java.util.ArrayList;

import static java.lang.Float.NaN;

/**
 * Created by loro on 2017/3/7.
 */

public class BOLLEntity {
    private ArrayList<Float> UPs;
    private ArrayList<Float> MBs;
    private ArrayList<Float> DNs;

    /**
     * 得到BOLL指标
     *
     * @param kLineBeens
     * @param n
     */
    public BOLLEntity(ArrayList<KLineDataModel> kLineBeens, int n) {
        this(kLineBeens, n, 0);
    }

    /**
     * 得到BOLL指标
     *
     * @param kLineBeens
     * @param n
     * @param defult
     */
    public BOLLEntity(ArrayList<KLineDataModel> kLineBeens, int n, float defult) {
        UPs = new ArrayList<>();
        MBs = new ArrayList<>();
        DNs = new ArrayList<>();

        float ma = 0.0f;
        float md = 0.0f;
        float mb = 0.0f;
        float up = 0.0f;
        float dn = 0.0f;

        if (kLineBeens != null && kLineBeens.size() > 0) {
            float closeSum = 0.0f;
            float sum = 0.0f;
            int index = 0;
            int index2 = n - 1;
            for (int i = 0; i < kLineBeens.size(); i++) {
                int k = i - n + 1;
                if (i >= n) {
                    index = n;
                } else {
                    index = i + 1;
                }
                closeSum = getSumClose(k, i, kLineBeens);
                ma = closeSum / index;
                sum = getSum(k, i, ma, kLineBeens);
                md = (float) Math.sqrt(sum / index);
                mb = ((closeSum - (float) kLineBeens.get(i).getClose()) / (index - 1));
                up = mb + (2 * md);
                dn = mb - (2 * md);

                if (i < index2) {
                    mb = defult;
                    up = defult;
                    dn = defult;
                }
                UPs.add(up);
                MBs.add(mb);
                DNs.add(dn);
            }
        }
    }

    private Float getSum(Integer a, Integer b, Float ma, ArrayList<KLineDataModel> kLineBeens) {
        if (a < 0) {
            a = 0;
        }
        KLineDataModel kLineBean;
        float sum = 0.0f;
        for (int i = a; i <= b; i++) {
            kLineBean = kLineBeens.get(i);
            sum += ((kLineBean.getClose() - ma) * (kLineBean.getClose() - ma));
        }
        return sum;
    }

    private Float getSumClose(Integer a, Integer b, ArrayList<KLineDataModel> kLineBeens) {
        if (a < 0) {
            a = 0;
        }
        KLineDataModel kLineBean;
        float close = 0.0f;
        for (int i = a; i <= b; i++) {
            kLineBean = kLineBeens.get(i);
            close += kLineBean.getClose();
        }

        return close;
    }


    public ArrayList<Float> getUPs() {
        return UPs;
    }

    public ArrayList<Float> getMBs() {
        return MBs;
    }

    public ArrayList<Float> getDNs() {
        return DNs;
    }
}

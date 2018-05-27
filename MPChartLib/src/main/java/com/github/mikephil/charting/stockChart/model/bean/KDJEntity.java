package com.github.mikephil.charting.stockChart.model.bean;


import com.github.mikephil.charting.stockChart.model.KLineDataModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by loro on 2017/3/2.
 */

public class KDJEntity {

    private ArrayList<Float> Ks;
    private ArrayList<Float> Ds;
    private ArrayList<Float> Js;

    /**
     * 得到KDJ
     *
     * @param kLineBeens
     * @param n          N为0时，时间范围取之前所有
     */
    public KDJEntity(List<KLineDataModel> kLineBeens, int n, int m1, int m2) {
        Ks = new ArrayList<Float>();
        Ds = new ArrayList<Float>();
        Js = new ArrayList<Float>();

        ArrayList<Float> ks = new ArrayList<Float>();
        ArrayList<Float> ds = new ArrayList<Float>();
        ArrayList<Float> js = new ArrayList<Float>();

        float k = 50.0f;
        float d = 50.0f;
        float j = 0.0f;
        float rSV = 0.0f;

        if (kLineBeens != null && kLineBeens.size() > 0) {

            KLineDataModel kLineBean = kLineBeens.get(0);
            float high = (float) kLineBean.getHigh();
            float low = (float) kLineBean.getLow();

            for (int i = 0; i < kLineBeens.size(); i++) {
                kLineBean = kLineBeens.get(i);
                if (i > 0) {
                    if (n == 0) {
                        high = high > kLineBean.getHigh() ? high : (float) kLineBean.getHigh();
                        low = low < kLineBean.getLow() ? low : (float) kLineBean.getLow();
                    } else {
                        int t = i - n + 1;
                        Float[] wrs = getHighAndLowByK(t, i, (ArrayList<KLineDataModel>) kLineBeens);
                        high = wrs[0];
                        low = wrs[1];
                    }
                }
                if (high != low) {
                    rSV = (float) ((kLineBean.getClose() - low) / (high - low) * 100);
                } else {
                    rSV = 0;
                }
                k = k * (m1 - 1.0f) / m1 + rSV / m1;
                d = d * (m2 - 1.0f) / m2 + k / m2;
                j = (3 * k) - (2 * d);

                //其他软件没有大于100小于0的值，但是我算出来确实有，其它软件在0和100的时候出现直线，怀疑也是做了处理
                j = j < 0 ? 0 : j;
                j = j > 100 ? 100 : j;

                ks.add(k);
                ds.add(d);
                js.add(j);
            }
            for (int i = 0; i < ks.size(); i++) {
                Ks.add(ks.get(i));
                Ds.add(ds.get(i));
                Js.add(js.get(i));
            }
        }
    }

    /**
     * 得到某区间内最高价和最低价
     *
     * @param a          开始位置 可以为0
     * @param b          结束位置
     * @param kLineBeens
     * @return
     */
    private Float[] getHighAndLowByK(Integer a, Integer b, ArrayList<KLineDataModel> kLineBeens) {
        if (a < 0) {
            a = 0;
        }

        KLineDataModel kLineBean = kLineBeens.get(a);
        float high = (float) kLineBean.getHigh();
        float low = (float) kLineBean.getLow();
        Float[] wrs = new Float[2];
        for (int i = a; i <= b; i++) {
            kLineBean = kLineBeens.get(i);
            high = high > kLineBean.getHigh() ? high : (float) kLineBean.getHigh();
            low = low < kLineBean.getLow() ? low : (float) kLineBean.getLow();
        }

        wrs[0] = high;
        wrs[1] = low;
        return wrs;
    }


    public ArrayList<Float> getK() {
        return Ks;
    }

    public ArrayList<Float> getD() {
        return Ds;
    }

    public ArrayList<Float> getJ() {
        return Js;
    }
}

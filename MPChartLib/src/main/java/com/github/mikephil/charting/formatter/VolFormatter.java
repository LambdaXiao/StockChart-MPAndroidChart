package com.github.mikephil.charting.formatter;


import com.github.mikephil.charting.components.AxisBase;

import java.text.DecimalFormat;

/**
 * 坐标label格式化
 */
public class VolFormatter implements IAxisValueFormatter {

    private int unit;
    private DecimalFormat mFormat;
    private String u;

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        int e = (int) Math.floor(Math.log10(value));
        if (e >= 8) {
            unit = 8;
        } else if (e >= 4) {
            unit = 4;
        } else {
            unit = 1;
        }

        if (e == 1) {
            mFormat = new DecimalFormat("#0");
        } else {
            mFormat = new DecimalFormat("#0.00");
        }
        value = value / (int) Math.pow(10, unit);
        if (value == 0) {
            int e2 = (int) Math.floor(Math.log10(axis.getAxisMaximum()));
            if (e2 >= 8) {
                u = "亿手";
            } else if (e2 >= 4) {
                u = "万手";
            } else {
                u = "手";
            }
            return u;
        }
        return mFormat.format(value);
    }

}

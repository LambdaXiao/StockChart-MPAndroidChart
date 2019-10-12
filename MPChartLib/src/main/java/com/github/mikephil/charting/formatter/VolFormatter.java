package com.github.mikephil.charting.formatter;


import android.content.Context;

import com.github.mikephil.charting.R;
import com.github.mikephil.charting.components.AxisBase;

import java.text.DecimalFormat;

/**
 * 坐标label格式化
 */
public class VolFormatter extends ValueFormatter {

    private int unit;
    private DecimalFormat mFormat;
    private String u;
    private String assetId;
    private Context context;

    public VolFormatter(Context context,String assetId){
        this.context = context;
        this.assetId = assetId;
    }
    @Override
    public String getAxisLabel(float value, AxisBase axis) {
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
                u = assetId.endsWith(".HK")?context.getResources().getString(R.string.billions_gu):context.getResources().getString(R.string.billions_shou);
            } else if (e2 >= 4) {
                u = assetId.endsWith(".HK")?context.getResources().getString(R.string.millions_gu):context.getResources().getString(R.string.millions_shou);
            } else {
                u = assetId.endsWith(".HK")?context.getResources().getString(R.string.gu):context.getResources().getString(R.string.shou);
            }
            return u;
        }
        return mFormat.format(value);
    }

}

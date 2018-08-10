package com.github.mikephil.charting.stockChart.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/8.
 */
public class TimeDataModel implements Serializable {
    // 合约ID
    public String m_szInstrumentID;//16

    //时间戳
    private Long timeMills = 0L;
    //现价
    private double nowPrice;
    //均价
    private double averagePrice;
    //分钟成交量
    private int volume;
    //今开
    private double open;
    //昨收
    private double preClose;
    private double per;
    private double cha;
    private double total;
    private int color = 0xff000000;

    public Long getTimeMills() {
        return timeMills;
    }

    public void setTimeMills(Long timeMills) {
        this.timeMills = timeMills;
    }

    public double getNowPrice() {
        return nowPrice;
    }

    public void setNowPrice(double nowPrice) {
        this.nowPrice = nowPrice;
    }

    public double getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(double averagePrice) {
        this.averagePrice = averagePrice;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getPreClose() {
        return preClose;
    }

    public void setPreClose(double preClose) {
        this.preClose = preClose;
    }

    public double getPer() {
        return per;
    }

    public void setPer(double per) {
        this.per = per;
    }

    public double getCha() {
        return cha;
    }

    public void setCha(double cha) {
        this.cha = cha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object obj) {
        TimeDataModel model = (TimeDataModel) obj;
        return getTimeMills().equals(model.getTimeMills());
    }
}

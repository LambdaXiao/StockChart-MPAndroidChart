package com.github.mikephil.charting.stockChart.data;

import com.github.mikephil.charting.stockChart.model.TimeDataModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ly on 2018/1/22.
 */

public class KTimeData {
    private ArrayList<TimeDataModel> realTimeDatas = new ArrayList<>();//分时数据
    private double baseValue = 0;//分时图基准值
    private double permaxmin = 0;//分时图价格最大区间值
    private int mAllVolume = 0;//分时图总成交量
    private double volMaxTimeLine;//分时图最大成交量
    private double perPerMaxmin = 0;
    private double perVolMaxTimeLine = 0;

    /**
     * 外部传JSONObject解析获得分时数据集
     */
    public void parseTimeData(JSONObject object) {
        if (object != null) {
            realTimeDatas.clear();
            double preClose = object.optDouble("preClose");
            JSONArray data = object.optJSONArray("data");
            if (data != null) {
                for (int i = 0; i < data.length(); i++) {
                    TimeDataModel timeDatamodel = new TimeDataModel();
                    timeDatamodel.setTimeMills(data.optJSONArray(i).optLong(0, 0L));
                    timeDatamodel.setNowPrice(data.optJSONArray(i).optDouble(1));
                    timeDatamodel.setAveragePrice(data.optJSONArray(i).optDouble(2));
                    timeDatamodel.setVolume(Double.valueOf(data.optJSONArray(i).optString(3)).intValue());
                    timeDatamodel.setOpen(data.optJSONArray(i).optDouble(4));
                    timeDatamodel.setPreClose(preClose);
                    if (i == 0) {
                        mAllVolume = timeDatamodel.getVolume();
                        permaxmin = 0;
                        volMaxTimeLine = 0;
                        if (baseValue == 0) {
                            baseValue = timeDatamodel.getPreClose();
                        }
                    } else {
                        mAllVolume += timeDatamodel.getVolume();
                    }
                    timeDatamodel.setCha(timeDatamodel.getNowPrice() - baseValue);
                    timeDatamodel.setPer(timeDatamodel.getCha() / baseValue);
                    if (Math.abs(timeDatamodel.getCha()) > permaxmin / 1.2) {
                        perPerMaxmin = permaxmin;
                        permaxmin = (float) Math.abs(timeDatamodel.getCha()) * (1.2f);//最大值和百分比都增加20%，防止内容顶在边框
                    }
                    perVolMaxTimeLine = volMaxTimeLine;
                    volMaxTimeLine = Math.max(timeDatamodel.getVolume(), volMaxTimeLine);
                    realTimeDatas.add(timeDatamodel);
                }
            }
        }
    }

    public void removeLastData() {
        TimeDataModel realTimeData = getRealTimeData().get(getRealTimeData().size() - 1);
        mAllVolume -= realTimeData.getVolume();
        volMaxTimeLine = perVolMaxTimeLine;
        getRealTimeData().remove(getRealTimeData().size() - 1);
    }

    public synchronized ArrayList<TimeDataModel> getRealTimeData() {
        return realTimeDatas;
    }

    public void resetTimeData() {
        permaxmin = 0;
        baseValue = 0;
        getRealTimeData().clear();
    }

    //分时图左Y轴最小值
    public float getMin() {
        return (float) (baseValue - permaxmin);
    }

    //分时图左Y轴最大值
    public float getMax() {
        return (float) (baseValue + permaxmin);
    }

    //分时图右Y轴最大涨跌值
    public float getPercentMax() {
        return (float) ((permaxmin) / baseValue);
    }

    //分时图右Y轴最大涨跌值
    public float getPercentMin() {
        return (float) -getPercentMax();
    }

    //分时图最大成交量
    public float getVolMaxTime() {
        return (float) volMaxTimeLine;
    }

    //分时图分钟数据集合
    public ArrayList<TimeDataModel> getDatas() {
        return realTimeDatas;
    }

}

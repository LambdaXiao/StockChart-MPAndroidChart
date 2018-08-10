package com.github.mikephil.charting.stockChart.data;

import android.util.SparseArray;

import com.github.mikephil.charting.stockChart.model.TimeDataModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.github.mikephil.charting.utils.DataTimeUtil.secToDateForFiveDay;

/**
 * 分时数据解析
 */

public class TimeDataManage {
    private ArrayList<TimeDataModel> realTimeDatas = new ArrayList<>();//分时数据
    private double baseValue = 0;//分时图基准值
    private double permaxmin = 0;//分时图价格最大区间值
    private int mAllVolume = 0;//分时图总成交量
    private double volMaxTimeLine;//分时图最大成交量
    private double perPerMaxmin = 0;
    private double perVolMaxTimeLine = 0;
    private SparseArray<String> fiveDayXLabels = new SparseArray<String>();//专用于五日分时横坐标轴刻度
    private List<Integer> fiveDayXLabelKey = new ArrayList<Integer>();//专用于五日分时横坐标轴刻度
    private String assetId;
    private SimpleDateFormat sf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    /**
     * 外部传JSONObject解析获得分时数据集
     */
    public void parseTimeData(JSONObject object, String assetId) {
        this.assetId = assetId;
        if (object != null) {
            realTimeDatas.clear();
            fiveDayXLabels.clear();
            getFiveDayXLabelKey(assetId);
            String preDate = null;
            int index = 0;
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
                        if (fiveDayXLabelKey.size() > index) {
                            fiveDayXLabels.put(fiveDayXLabelKey.get(index), secToDateForFiveDay(timeDatamodel.getTimeMills()));
                            index++;
                        }
                    } else {
                        mAllVolume += timeDatamodel.getVolume();
                        if (fiveDayXLabelKey.size() > index && !secToDateForFiveDay(timeDatamodel.getTimeMills()).equals(preDate)) {
                            fiveDayXLabels.put(fiveDayXLabelKey.get(index), secToDateForFiveDay(timeDatamodel.getTimeMills()));
                            index++;
                        }
                    }
                    preDate = secToDateForFiveDay(timeDatamodel.getTimeMills());
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

    /**
     * 外部传JSONObject解析获得分时数据集(用于解析美股分时)
     */
    public void parseUSTimeData(JSONObject object, String assetId) {
        this.assetId = assetId;
        if (object != null) {
            realTimeDatas.clear();
            getFiveDayXLabelKey(assetId);
            double preClose = object.optDouble("prevClose");
            mAllVolume = object.optInt("totalVolume");
            JSONArray data = object.optJSONArray("data");
            if (data != null) {
                for (int i = 0; i < data.length(); i++) {
                    TimeDataModel timeDatamodel = new TimeDataModel();
                    try {
                        timeDatamodel.setTimeMills(sf.parse(data.optJSONObject(i).optString("date")).getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    timeDatamodel.setNowPrice(data.optJSONObject(i).optDouble("price"));
                    timeDatamodel.setVolume(data.optJSONObject(i).optInt("volume"));
                    timeDatamodel.setCha(data.optJSONObject(i).optDouble("change"));
                    timeDatamodel.setPer(data.optJSONObject(i).optDouble("changePct"));
                    timeDatamodel.setPreClose(preClose);

                    if (i == 0) {
                        permaxmin = 0;
                        volMaxTimeLine = 0;
                        if (baseValue == 0) {
                            baseValue = timeDatamodel.getPreClose();
                        }
                    }

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

    public String getAssetId() {
        return assetId;
    }

    /**
     * 当日分时X轴刻度线
     *
     * @return
     */
    public SparseArray<String> getOneDayXLabels(boolean landscape) {
        SparseArray<String> xLabels = new SparseArray<String>();
        if (assetId.endsWith(".HK")) {
            if (landscape) {
                xLabels.put(0, "09:30");
                xLabels.put(60, "10:30");
                xLabels.put(120, "11:30");
                xLabels.put(180, "13:30");
                xLabels.put(240, "14:30");
                xLabels.put(300, "15:30");
                xLabels.put(330, "16:00");
            } else {
                xLabels.put(0, "09:30");
                xLabels.put(75, "");
                xLabels.put(150, "12:00/13:00");
                xLabels.put(240, "");
                xLabels.put(330, "16:00");
            }
        }else if (assetId.endsWith(".US")){
            xLabels.put(0, "09:30");
            xLabels.put(120, "11:30");
            xLabels.put(210, "13:00");
            xLabels.put(300, "14:30");
            xLabels.put(390, "16:00");
        } else {
            xLabels.put(0, "09:30");
            xLabels.put(60, "10:30");
            xLabels.put(120, "11:30/13:00");
            xLabels.put(180, "14:00");
            xLabels.put(240, "15:00");
        }
        return xLabels;
    }

    /**
     * 五日分时X轴刻度线
     *
     * @return
     */
    public SparseArray<String> getFiveDayXLabels() {
        for (int i = fiveDayXLabels.size(); i < fiveDayXLabelKey.size(); i++) {
            fiveDayXLabels.put(fiveDayXLabelKey.get(i), "");
        }
        return fiveDayXLabels;
    }

    private List<Integer> getFiveDayXLabelKey(String assetId) {
        fiveDayXLabelKey.clear();
        if (assetId.endsWith(".HK")) {
            fiveDayXLabelKey.add(0);
            fiveDayXLabelKey.add(82);
            fiveDayXLabelKey.add(165);
            fiveDayXLabelKey.add(248);
            fiveDayXLabelKey.add(331);
            fiveDayXLabelKey.add(414);
        } else {
            fiveDayXLabelKey.add(0);
            fiveDayXLabelKey.add(60);
            fiveDayXLabelKey.add(121);
            fiveDayXLabelKey.add(182);
            fiveDayXLabelKey.add(243);
            fiveDayXLabelKey.add(304);
        }
        return fiveDayXLabelKey;
    }

}

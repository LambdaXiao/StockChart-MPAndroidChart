package com.github.mikephil.charting.stockChart.dataManage;

import android.util.SparseArray;

import com.github.mikephil.charting.stockChart.model.TimeDataModel;

import org.json.JSONArray;
import org.json.JSONObject;

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
    private double max = 0;//分时图最大价格
    private double min = 0;//分时图最小价格
    private double perVolMaxTimeLine = 0;
    private SparseArray<String> fiveDayXLabels = new SparseArray<String>();//专用于五日分时横坐标轴刻度
    private List<Integer> fiveDayXLabelKey = new ArrayList<Integer>();//专用于五日分时横坐标轴刻度
    private String assetId;//股票代号
    private double preClose;//昨收价

    /**
     * 外部传JSONObject解析获得分时数据集
     */
    public void parseTimeData(JSONObject object, String assetId, double preClosePrice) {
        this.assetId = assetId;
        if (object != null) {
            realTimeDatas.clear();
            fiveDayXLabels.clear();
            getFiveDayXLabelKey(assetId);
            String preDate = null;
            int index = 0;
            preClose = Double.isNaN(object.optDouble("preClose")) ? 0 : object.optDouble("preClose");
            JSONArray data = object.optJSONArray("data");
            if (data != null) {
                for (int i = 0; i < data.length(); i++) {
                    TimeDataModel timeDatamodel = new TimeDataModel();
                    timeDatamodel.setTimeMills(data.optJSONArray(i).optLong(0, 0L));
                    timeDatamodel.setNowPrice(Double.isNaN(data.optJSONArray(i).optDouble(1)) ? 0 : data.optJSONArray(i).optDouble(1));
                    timeDatamodel.setAveragePrice(Double.isNaN(data.optJSONArray(i).optDouble(2)) ? 0 : data.optJSONArray(i).optDouble(2));
                    timeDatamodel.setVolume(Double.valueOf(data.optJSONArray(i).optString(3)).intValue());
                    timeDatamodel.setOpen(Double.isNaN(data.optJSONArray(i).optDouble(4)) ? 0 : data.optJSONArray(i).optDouble(4));
                    timeDatamodel.setPreClose(preClose == 0 ? (preClosePrice == 0 ? timeDatamodel.getOpen() : preClosePrice) : preClose);

                    if (i == 0) {
                        preClose = timeDatamodel.getPreClose();
                        mAllVolume = timeDatamodel.getVolume();
                        max = timeDatamodel.getNowPrice();
                        min = timeDatamodel.getNowPrice();
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
                    timeDatamodel.setCha(timeDatamodel.getNowPrice() - preClose);
                    timeDatamodel.setPer(timeDatamodel.getCha() / preClose);

                    max = Math.max(timeDatamodel.getNowPrice(), max);
                    min = Math.min(timeDatamodel.getNowPrice(), min);

                    perVolMaxTimeLine = volMaxTimeLine;
                    volMaxTimeLine = Math.max(timeDatamodel.getVolume(), volMaxTimeLine);
                    realTimeDatas.add(timeDatamodel);
                }
                permaxmin = (max - min) / 2;
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
        baseValue = 0;
        getRealTimeData().clear();
    }

    //分时图左Y轴最大值
    public float getMax() {
        return (float) (baseValue + baseValue * getPercentMax());
    }

    //分时图左Y轴最小值
    public float getMin() {
        return (float) (baseValue + baseValue * getPercentMin());
    }

    //分时图右Y轴最大涨跌值
    public float getPercentMax() {
        //0.1表示Y轴最大涨跌值再增加10%，使图线不至于顶到最顶部
        return (float) ((max - baseValue) / baseValue + Math.abs(max - baseValue > min - baseValue ? max - baseValue : min - baseValue) / baseValue * 0.1);
    }

    //分时图右Y轴最小涨跌值
    public float getPercentMin() {
        //0.1表示Y轴最小涨跌值再减小10%，使图线不至于顶到最底部
        return (float) ((min - baseValue) / baseValue - Math.abs(max - baseValue > min - baseValue ? max - baseValue : min - baseValue) / baseValue * 0.1);
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

    public double getPreClose() {
        return preClose;
    }

    /**
     * 当日分时X轴刻度线
     *
     * @return
     */
    public SparseArray<String> getOneDayXLabels(boolean landscape) {
        SparseArray<String> xLabels = new SparseArray<String>();
        if (assetId.endsWith(".HK")) {
            //港股横坐标刻度
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
            //港股横坐标刻度
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

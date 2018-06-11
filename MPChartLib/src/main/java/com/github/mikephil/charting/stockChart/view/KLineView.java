package com.github.mikephil.charting.stockChart.view;

import android.content.Context;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.github.mikephil.charting.R;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.VolFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.stockChart.BarBottomMarkerView;
import com.github.mikephil.charting.stockChart.CandleCombinedChart;
import com.github.mikephil.charting.stockChart.CoupleChartGestureListener;
import com.github.mikephil.charting.stockChart.KRightMarkerView;
import com.github.mikephil.charting.stockChart.LeftMarkerView;
import com.github.mikephil.charting.stockChart.MyCombinedChart;
import com.github.mikephil.charting.stockChart.data.KLineData;
import com.github.mikephil.charting.stockChart.enums.TimeType;
import com.github.mikephil.charting.utils.CommonUtil;
import com.github.mikephil.charting.utils.DataTimeUtil;
import com.github.mikephil.charting.utils.NumberUtils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * K线view
 */
public class KLineView extends BaseView {

    private Context mContext;
    private CandleCombinedChart candleChart;
    private MyCombinedChart barChart;
    private TextView chartTypeDes;

    private XAxis xAxisBar, xAxisK;
    private YAxis axisLeftBar, axisLeftK;
    private YAxis axisRightBar, axisRightK;

    private KLineData kLineData;
    private CoupleChartGestureListener gestureListenerCandle;
    private CoupleChartGestureListener gestureListenerBar;
    private boolean landscape = false;//是否横屏
    private int maxVisibleXCount = 100;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            candleChart.setAutoScaleMinMaxEnabled(true);
            barChart.setAutoScaleMinMaxEnabled(true);
            candleChart.notifyDataSetChanged();
            barChart.notifyDataSetChanged();
            candleChart.invalidate();
            barChart.invalidate();
        }
    };

    public KLineView(Context context) {
        this(context, null);
    }

    public KLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_kline, this);
        candleChart = (CandleCombinedChart) findViewById(R.id.candleChart);
        barChart = (MyCombinedChart) findViewById(R.id.barchart);
        chartTypeDes = (TextView) findViewById(R.id.chart_type_des);

    }

    /**
     * 初始化图表数据
     */
    public void initChart(boolean landscape) {
        this.landscape = landscape;
        //蜡烛图
        candleChart.setDrawBorders(true);
        candleChart.setBorderWidth(0.7f);
        candleChart.setBorderColor(ContextCompat.getColor(mContext, R.color.border_color));
        candleChart.setDragEnabled(true);
        candleChart.setScaleYEnabled(false);
        candleChart.setDescription(null);
        candleChart.setHardwareAccelerationEnabled(true);
        Legend mChartKlineLegend = candleChart.getLegend();
        mChartKlineLegend.setEnabled(false);
        candleChart.setDragDecelerationEnabled(true);
        candleChart.setDragDecelerationFrictionCoef(0.1f);//0.92持续滚动时的速度快慢，[0,1) 0代表立即停止。
        candleChart.setDoubleTapToZoomEnabled(false);
        candleChart.setNoDataText("加载中...");

        //副图
        barChart.setDrawBorders(true);
        barChart.setBorderWidth(0.7f);
        barChart.setBorderColor(ContextCompat.getColor(mContext, R.color.border_color));
        barChart.setDragEnabled(true);
        barChart.setScaleYEnabled(false);
        barChart.setHardwareAccelerationEnabled(true);
        barChart.setDescription(null);
        Legend mChartChartsLegend = barChart.getLegend();
        mChartChartsLegend.setEnabled(false);
        barChart.setDragDecelerationEnabled(true);
        barChart.setDragDecelerationFrictionCoef(0.1f);//设置太快，切换滑动源滑动不同步
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setNoDataText("加载中...");

        //蜡烛图X轴
        xAxisK = candleChart.getXAxis();
        xAxisK.setDrawLabels(true);
        xAxisK.setDrawGridLines(false);
        xAxisK.setDrawAxisLine(false);
        xAxisK.setLabelCount(4);
        xAxisK.setTextColor(ContextCompat.getColor(mContext, R.color.label_text));
        xAxisK.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisK.setAvoidFirstLastClipping(true);


        //蜡烛图左Y轴
        axisLeftK = candleChart.getAxisLeft();
        axisLeftK.setDrawGridLines(true);
        axisLeftK.setDrawAxisLine(false);
        axisLeftK.setDrawLabels(true);
        axisLeftK.setLabelCount(5, true);
        axisLeftK.enableGridDashedLine(CommonUtil.dip2px(mContext, 4), CommonUtil.dip2px(mContext, 3), 0);
        axisLeftK.setTextColor(ContextCompat.getColor(mContext, R.color.label_text));
        axisLeftK.setGridColor(ContextCompat.getColor(mContext, R.color.grid_color));
        axisLeftK.setValueLineInside(true);
        axisLeftK.setDrawTopBottomGridLine(false);
        axisLeftK.setPosition(landscape ? YAxis.YAxisLabelPosition.OUTSIDE_CHART : YAxis.YAxisLabelPosition.INSIDE_CHART);
        axisLeftK.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return NumberUtils.keepPrecisionR(value, 2);
            }
        });

        //蜡烛图右Y轴
        axisRightK = candleChart.getAxisRight();
        axisRightK.setDrawLabels(false);
        axisRightK.setDrawGridLines(false);
        axisRightK.setDrawAxisLine(false);
        axisRightK.setPosition(landscape ? YAxis.YAxisLabelPosition.OUTSIDE_CHART : YAxis.YAxisLabelPosition.INSIDE_CHART);
        axisRightK.setGridColor(ContextCompat.getColor(mContext, R.color.grid_color));

        //副图X轴
        xAxisBar = barChart.getXAxis();
        xAxisBar.setDrawGridLines(false);
        xAxisBar.setDrawAxisLine(false);
        xAxisBar.setDrawLabels(false);
        xAxisBar.setTextColor(ContextCompat.getColor(mContext, R.color.label_text));
        xAxisBar.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisBar.setGridColor(ContextCompat.getColor(mContext, R.color.grid_color));
        xAxisBar.setAvoidFirstLastClipping(true);

        //副图左Y轴
        axisLeftBar = barChart.getAxisLeft();
        axisLeftBar.setAxisMinimum(0);
        axisLeftBar.setDrawGridLines(false);
        axisLeftBar.setDrawAxisLine(false);
        axisLeftBar.setTextColor(ContextCompat.getColor(mContext, R.color.label_text));
        axisLeftBar.setDrawLabels(true);
        axisLeftBar.setLabelCount(2, true);
        axisLeftBar.setValueLineInside(true);
        axisLeftBar.setPosition(landscape ? YAxis.YAxisLabelPosition.OUTSIDE_CHART : YAxis.YAxisLabelPosition.INSIDE_CHART);

        //副图右Y轴
        axisRightBar = barChart.getAxisRight();
        axisRightBar.setDrawLabels(false);
        axisRightBar.setDrawGridLines(false);
        axisRightBar.setDrawAxisLine(false);

        //设置图表边距
        int left_right = 0;
        if (landscape) {
            left_right = CommonUtil.dip2px(mContext, 50);
        } else {
            left_right = CommonUtil.dip2px(mContext, 5);
        }
        candleChart.setViewPortOffsets(left_right, CommonUtil.dip2px(mContext, 5), left_right, CommonUtil.dip2px(mContext, 15));
        barChart.setViewPortOffsets(left_right, CommonUtil.dip2px(mContext, 5), left_right, CommonUtil.dip2px(mContext, 15));

        //手势联动监听
        gestureListenerCandle = new CoupleChartGestureListener(candleChart, new Chart[]{barChart});
        gestureListenerBar = new CoupleChartGestureListener(barChart, new Chart[]{candleChart});
        candleChart.setOnChartGestureListener(gestureListenerCandle);
        barChart.setOnChartGestureListener(gestureListenerBar);

        candleChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                candleChart.highlightValue(h);
                if (barChart.getData().getBarData().getDataSets().size() != 0) {
                    Highlight highlight = new Highlight(h.getX(), h.getDataSetIndex(), h.getStackIndex());
                    highlight.setDataIndex(h.getDataIndex());
                    barChart.highlightValues(new Highlight[]{highlight});
                } else {
                    Highlight highlight = new Highlight(h.getX(), 2, h.getStackIndex());
                    highlight.setDataIndex(0);
                    barChart.highlightValues(new Highlight[]{highlight});
                }
            }

            @Override
            public void onNothingSelected() {
                barChart.highlightValues(null);
            }
        });
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

            @Override
            public void onValueSelected(Entry e, Highlight h) {
                barChart.highlightValue(h);
                Highlight highlight = new Highlight(h.getX(), 0, h.getStackIndex());
                highlight.setDataIndex(1);
                candleChart.highlightValues(new Highlight[]{highlight});
            }

            @Override
            public void onNothingSelected() {
                candleChart.highlightValues(null);
            }
        });


    }

    /**
     * 设置K线数据
     */
    public void setDataToChart(KLineData data) {
        kLineData = data;
        if (kLineData.getKLineDatas().size() == 0) {
            candleChart.setNoDataText("暂无数据");
            barChart.setNoDataText("暂无数据");
            return;
        }
        setMarkerView(kLineData);
        setBottomMarkerView(kLineData);

        kLineData.initCandle();
        kLineData.initMaLine();
        kLineData.initVolume();

        candleChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int index = (int) (value - kLineData.getOffSet());
                if (index <= 0 || index >= kLineData.getxVals().size()) {
                    return value + "";
                } else {
                    return kLineData.getxVals().get(index);
                }
            }
        });

        barChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int index = (int) (value - kLineData.getOffSet());
                if (index <= 0 || index >= kLineData.getKLineDatas().size()) {
                    return value + "";
                } else {
                    return DataTimeUtil.secToDate(kLineData.getKLineDatas().get(index).getDateMills());
                }
            }
        });

        axisLeftBar.setValueFormatter(new VolFormatter());

        float xScale = calMaxScale(1020, kLineData.getxVals().size());

        ViewPortHandler viewPortHandlerCombin = candleChart.getViewPortHandler();
        viewPortHandlerCombin.setMinMaxScaleX(3, 40);
        Matrix touchMatrix = viewPortHandlerCombin.getMatrixTouch();
        touchMatrix.setScale(xScale, 1f);

        ViewPortHandler viewPortHandlerBar = barChart.getViewPortHandler();
        viewPortHandlerBar.setMinMaxScaleX(3, 40);
        Matrix touchBar = viewPortHandlerBar.getMatrixTouch();
        touchBar.setScale(xScale, 1f);
//        candleChart.setVisibleXRange(60,1);
//        barChart.setVisibleXRange(60,1);
//        candleChart.zoom(3,0,0,0);
//        barChart.zoom(3,0,0,0);

        CombinedData combinedKlineData = new CombinedData();
        //数据少于一屏是填充一屏
        if (!kLineData.getKLineDatas().isEmpty() && kLineData.getKLineDatas().size() < maxVisibleXCount) {
            ArrayList<CandleEntry> paddingEntries = new ArrayList<>();
            for (int i = kLineData.getKLineDatas().size(); i < maxVisibleXCount; i++) {
                kLineData.getxVals().add("");
                paddingEntries.add(new CandleEntry(i + 0.5f, 0, 0, 0, 0));
            }
            CandleDataSet paddingDataSet = new CandleDataSet(paddingEntries, "");
            paddingDataSet.setHighlightEnabled(false);
            paddingDataSet.setDrawHorizontalHighlightIndicator(false);
            paddingDataSet.setDrawValues(false);
            paddingDataSet.setVisible(false);
            combinedKlineData.setData(new CandleData(kLineData.getCandleDataSet(), paddingDataSet));
        } else {
            combinedKlineData.setData(new CandleData(kLineData.getCandleDataSet()));
        }
        combinedKlineData.setData(new LineData(kLineData.getLineDataMA()));
        candleChart.setData(combinedKlineData);

        CombinedData combinedChartsData = new CombinedData();
        //数据少于一屏是填充一屏
        if (!kLineData.getKLineDatas().isEmpty() && kLineData.getKLineDatas().size() < maxVisibleXCount) {
            ArrayList<BarEntry> paddingEntries = new ArrayList<>();
            for (int i = kLineData.getKLineDatas().size(); i < maxVisibleXCount; i++) {
                paddingEntries.add(new BarEntry(i + 0.5f, 0));
            }
            BarDataSet paddingDataSet = new BarDataSet(paddingEntries, "");
            paddingDataSet.setHighlightEnabled(false);
            paddingDataSet.setVisible(false);
            paddingDataSet.setDrawValues(false);
            combinedChartsData.setData(new BarData(kLineData.getVolumeDataSet(), paddingDataSet));
        } else {
            combinedChartsData.setData(new BarData(kLineData.getVolumeDataSet()));
        }
        combinedChartsData.setData(new LineData());
        barChart.setData(combinedChartsData);

        candleChart.getXAxis().setAxisMaximum(combinedKlineData.getXMax() + kLineData.getOffSet());
        barChart.getXAxis().setAxisMaximum(combinedChartsData.getXMax() + kLineData.getOffSet());
        if (kLineData.getKLineDatas().size() > maxVisibleXCount) {
            candleChart.moveViewToX(kLineData.getKLineDatas().size() - 1);
            barChart.moveViewToX(kLineData.getKLineDatas().size() - 1);
        }
        handler.sendEmptyMessageDelayed(0, 0);

    }

    protected int chartType = 1;
    protected int chartTypes = 4;

    public void doCandleChartSwitch() {
        chartType++;
        if (chartType > chartTypes) {
            chartType = 1;
        }
        switch (chartType) {
            case 1:
                setMAToChart();
                break;
            case 2:
                setEMAToChart();
                break;
            case 3:
                setSMAToChart();
                break;
            case 4:
                setBOLLToChart();
                break;
            default:
                break;
        }
    }

    protected int chartType1 = 1;
    protected int chartTypes1 = 5;

    public void doBarChartSwitch() {
        chartType1++;
        if (chartType1 > chartTypes1) {
            chartType1 = 1;
        }
        switch (chartType1) {
            case 1:
                setVolumeToChart();
                break;
            case 2:
                setMACDToChart();
                break;
            case 3:
                setKDJToChart();
                break;
            case 4:
                setCCIToChart();
                break;
            case 5:
                setRSIToChart();
                break;
            default:
                break;
        }
    }

    /**
     * 蜡烛图指标MA
     */
    public void setMAToChart() {
        if (candleChart != null && candleChart.getLineData() != null) {
            candleChart.getLineData().clearValues();

            kLineData.initMaLine();
            CombinedData combinedData = candleChart.getData();
            combinedData.setData(new LineData(kLineData.getLineDataMA()));
            candleChart.notifyDataSetChanged();
        }
    }

    /**
     * 蜡烛图指标EMA
     */
    public void setEMAToChart() {
        if (candleChart != null && candleChart.getLineData() != null) {
            candleChart.getLineData().clearValues();

            kLineData.initEMA();
            CombinedData combinedData = candleChart.getData();
            combinedData.setData(new LineData(kLineData.getLineDataEMA()));
            candleChart.notifyDataSetChanged();
        }
    }

    /**
     * 蜡烛图指标SMA
     */
    public void setSMAToChart() {
        if (candleChart != null && candleChart.getLineData() != null) {
            candleChart.getLineData().clearValues();

            kLineData.initSMA();
            CombinedData combinedData = candleChart.getData();
            combinedData.setData(new LineData(kLineData.getLineDataSMA()));
            candleChart.notifyDataSetChanged();
        }
    }

    /**
     * 蜡烛图指标BOLL
     */
    public void setBOLLToChart() {
        if (candleChart != null && candleChart.getLineData() != null) {
            candleChart.getLineData().clearValues();

            kLineData.initBOLL();
            CombinedData combinedData = candleChart.getData();
            combinedData.setData(new LineData(kLineData.getLineDataBOLL()));
            candleChart.notifyDataSetChanged();
        }
    }


    /**
     * 副图指标成交量
     */
    public void setVolumeToChart() {
        if (barChart != null) {
            if (barChart.getBarData() != null) {
                barChart.getBarData().clearValues();
            }
            if (barChart.getLineData() != null) {
                barChart.getLineData().clearValues();
            }
            chartTypeDes.setText("VOL");
            kLineData.initVolume();
            axisLeftBar.setAxisMinimum(0);

            CombinedData combinedData = barChart.getData();
            combinedData.setData(new BarData(kLineData.getVolumeDataSet()));
            barChart.notifyDataSetChanged();
        }
    }

    /**
     * 副图指标MACD
     */
    public void setMACDToChart() {
        if (barChart != null) {
            if (barChart.getBarData() != null) {
                barChart.getBarData().clearValues();
            }
            if (barChart.getLineData() != null) {
                barChart.getLineData().clearValues();
            }
            chartTypeDes.setText("MACD");
            kLineData.initMACD();
            axisLeftBar.resetAxisMinimum();

            CombinedData combinedData = barChart.getData();
            combinedData.setData(new LineData(kLineData.getLineDataMACD()));
            combinedData.setData(new BarData(kLineData.getBarDataMACD()));
            barChart.notifyDataSetChanged();
        }
    }

    /**
     * 副图指标KDJ
     */
    public void setKDJToChart() {
        if (barChart != null) {
            if (barChart.getBarData() != null) {
                barChart.getBarData().clearValues();
            }
            if (barChart.getLineData() != null) {
                barChart.getLineData().clearValues();
            }
            chartTypeDes.setText("KDJ");
            kLineData.initKDJ();
            axisLeftBar.resetAxisMinimum();

            CombinedData combinedData = barChart.getData();
            combinedData.setData(new LineData(kLineData.getLineDataKDJ()));
            barChart.notifyDataSetChanged();
        }
    }

    /**
     * 副图指标CCI
     */
    public void setCCIToChart() {
        if (barChart != null) {
            if (barChart.getBarData() != null) {
                barChart.getBarData().clearValues();
            }
            if (barChart.getLineData() != null) {
                barChart.getLineData().clearValues();
            }
            chartTypeDes.setText("CCI");
            kLineData.initCCI();
            axisLeftBar.resetAxisMinimum();

            CombinedData combinedData = barChart.getData();
            combinedData.setData(new LineData(kLineData.getLineDataCCI()));
            barChart.notifyDataSetChanged();
        }
    }

    /**
     * 副图指标RSI
     */
    public void setRSIToChart() {
        if (barChart != null) {
            if (barChart.getBarData() != null) {
                barChart.getBarData().clearValues();
            }
            if (barChart.getLineData() != null) {
                barChart.getLineData().clearValues();
            }
            chartTypeDes.setText("RSI");
            kLineData.initRSI();
            axisLeftBar.resetAxisMinimum();

            CombinedData combinedData = barChart.getData();
            combinedData.setData(new LineData(kLineData.getLineDataRSI()));
            barChart.notifyDataSetChanged();
        }
    }

    public void setMarkerView(KLineData kLineData) {
        LeftMarkerView leftMarkerView = new LeftMarkerView(mContext, R.layout.my_markerview, 2);
        KRightMarkerView rightMarkerView = new KRightMarkerView(mContext, R.layout.my_markerview, 2);
        candleChart.setMarker(leftMarkerView, rightMarkerView, kLineData);
    }

    public void setBottomMarkerView(KLineData kLineData) {
        BarBottomMarkerView bottomMarkerView = new BarBottomMarkerView(mContext, R.layout.my_markerview);
        barChart.setMarker(bottomMarkerView, kLineData, TimeType.TIME_DATE);
    }

    public float calMaxScale(float viewPortWidth, float count) {
        float xScale = 1;
        if (count >= 180) {
            xScale = 20 / (viewPortWidth / count);
        } else {
            xScale = 20 / (viewPortWidth / count);
        }
        return xScale;
    }

    public void addAData(KLineData kLineData) {
        int size = kLineData.getKLineDatas().size();
        CombinedData combinedData0 = barChart.getData();
        IBarDataSet barDataSet = combinedData0.getBarData().getDataSetByIndex(0);
        if (barDataSet == null) {//当没有数据时
            return;
        }
        float color = kLineData.getKLineDatas().get(size - 1).getOpen() > kLineData.getKLineDatas().get(size - 1).getClose() ? 0f : 1f;
        BarEntry barEntry = new BarEntry(size - 1 + kLineData.getOffSet(), kLineData.getKLineDatas().get(size - 1).getVolume(), color);

        barDataSet.addEntry(barEntry);

        CombinedData combinedData = candleChart.getData();
        CandleData candleData = combinedData.getCandleData();
        String xValue = DataTimeUtil.secToDate(kLineData.getKLineDatas().get(size - 1).getDateMills());
        kLineData.getxVals().add(xValue);

        ICandleDataSet candleDataSet = candleData.getDataSetByIndex(0);
        int i = size - 1;
        candleDataSet.addEntry(new CandleEntry(i + kLineData.getOffSet(), (float) kLineData.getKLineDatas().get(i).getHigh(), (float) kLineData.getKLineDatas().get(i).getLow(), (float) kLineData.getKLineDatas().get(i).getOpen(), (float) kLineData.getKLineDatas().get(i).getClose()));
        kLineData.getxVals().add(DataTimeUtil.secToDate(kLineData.getKLineDatas().get(i).getDateMills()));

//        doRefreshK(chartType);
//        doRefreshCharts(chartType1);

        candleChart.getXAxis().setAxisMaximum(i + 1f);
        barChart.getXAxis().setAxisMaximum(i + 1f);
        barChart.notifyDataSetChanged();
        candleChart.notifyDataSetChanged();
        barChart.invalidate();
        candleChart.invalidate();
    }

    public void updateAData(KLineData kLineData) {
        int size = kLineData.getKLineDatas().size();
        CombinedData combinedData = candleChart.getData();
        CandleData candleData = combinedData.getCandleData();
        ICandleDataSet candleDataSet = candleData.getDataSetByIndex(0);
        candleDataSet.removeEntry(size - 1);
        int i = size - 1;
        candleDataSet.addEntry(new CandleEntry(i + kLineData.getOffSet(), (float) kLineData.getKLineDatas().get(i).getHigh(), (float) kLineData.getKLineDatas().get(i).getLow(), (float) kLineData.getKLineDatas().get(i).getOpen(), (float) kLineData.getKLineDatas().get(i).getClose()));
        if (chartType1 == 1) {
            CombinedData combinedData0 = barChart.getData();
            IBarDataSet barDataSet = combinedData0.getBarData().getDataSetByIndex(0);
            barDataSet.removeEntry(size - 1);
            float color = kLineData.getKLineDatas().get(size - 1).getOpen() > kLineData.getKLineDatas().get(size - 1).getClose() ? 0f : 1f;
            BarEntry barEntry = new BarEntry(size - 1 + kLineData.getOffSet(), kLineData.getKLineDatas().get(size - 1).getVolume(), color);
            barDataSet.addEntry(barEntry);
        } else {
//            doRefreshCharts(chartType1);
        }
        if (chartType == 1) {
            kLineData.setOneMaValue(combinedData.getLineData(), i);
        } else {
//            doRefreshK(chartType);
        }

        barChart.notifyDataSetChanged();
        candleChart.notifyDataSetChanged();
        barChart.invalidate();
        candleChart.invalidate();
    }

    public void doRefreshK(int chartType) {
        switch (chartType) {
            case 1:
                setMAToChart();
                break;
            case 2:
                setEMAToChart();
                break;
            case 3:
                setSMAToChart();
                break;
            case 4:
                setBOLLToChart();
                break;
            default:
                break;
        }
    }

    public void doRefreshCharts(int chartType) {
        switch (chartType) {
            case 1:
                setVolumeToChart();
                break;
            case 2:
                setMACDToChart();
                break;
            case 3:
                setKDJToChart();
                break;
            case 4:
                setCCIToChart();
                break;
            case 5:
                setRSIToChart();
                break;
            default:
                break;
        }
    }

    public CoupleChartGestureListener getGestureListenerCandle() {
        return gestureListenerCandle;
    }

    public CoupleChartGestureListener getGestureListenerBar() {
        return gestureListenerBar;
    }
}

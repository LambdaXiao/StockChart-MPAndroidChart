package com.github.mikephil.charting.stockChart.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;

import com.github.mikephil.charting.R;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.stockChart.BarBottomMarkerView;
import com.github.mikephil.charting.stockChart.CoupleChartGestureListener;
import com.github.mikephil.charting.stockChart.LeftMarkerView;
import com.github.mikephil.charting.stockChart.TimeBarChart;
import com.github.mikephil.charting.stockChart.TimeLineChart;
import com.github.mikephil.charting.stockChart.TimeRightMarkerView;
import com.github.mikephil.charting.stockChart.TimeXAxis;
import com.github.mikephil.charting.stockChart.data.KTimeData;
import com.github.mikephil.charting.stockChart.model.CirclePositionTime;
import com.github.mikephil.charting.stockChart.model.TimeDataModel;
import com.github.mikephil.charting.utils.CommonUtil;
import com.github.mikephil.charting.utils.NumberUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 当日分时图view
 */
public class OneDayView extends BaseView {

    private Context mContext;
    TimeLineChart lineChart;
    TimeBarChart barChart;
    FrameLayout cirCleView;

    private LineDataSet d1, d2;
    private BarDataSet barDataSet;

    TimeXAxis xAxisLine;
    YAxis axisRightLine;
    YAxis axisLeftLine;

    TimeXAxis xAxisBar;
    YAxis axisLeftBar;
    YAxis axisRightBar;

    private CoupleChartGestureListener gestureListenerLine;
    private CoupleChartGestureListener gestureListenerBar;
    private boolean landscape = false;

    public OneDayView(Context context) {
        this(context, null);
    }

    public OneDayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_one_day, this);
        lineChart = (TimeLineChart) findViewById(R.id.line_chart);
        barChart = (TimeBarChart) findViewById(R.id.bar_chart);
        cirCleView = (FrameLayout) findViewById(R.id.circle_frame_time);

        EventBus.getDefault().register(this);

        playHeartbeatAnimation(cirCleView.findViewById(R.id.anim_view));

    }

    /**
     * 初始化图表属性
     */
    public void initChart(boolean landscape) {
        this.landscape = landscape;
        //主图
        lineChart.setScaleEnabled(false);
        lineChart.setDrawBorders(true);
        lineChart.setBorderColor(ContextCompat.getColor(mContext, R.color.border_color));
        lineChart.setBorderWidth(0.7f);
        lineChart.setNoDataText("暂无数据");
        Legend lineChartLegend = lineChart.getLegend();
        lineChartLegend.setEnabled(false);
        lineChart.setDescription(null);
        //副图
        barChart.setScaleEnabled(false);
        barChart.setDrawBorders(true);
        barChart.setBorderColor(ContextCompat.getColor(mContext, R.color.border_color));
        barChart.setBorderWidth(0.7f);
        barChart.setNoDataText("暂无数据");
        Legend barChartLegend = barChart.getLegend();
        barChartLegend.setEnabled(false);
        barChart.setDescription(null);

        //主图X轴
        xAxisLine = (TimeXAxis) lineChart.getXAxis();
        xAxisLine.setDrawAxisLine(false);
        xAxisLine.setTextColor(ContextCompat.getColor(mContext, R.color.label_text));
        xAxisLine.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisLine.setAvoidFirstLastClipping(true);
        xAxisLine.setGridColor(ContextCompat.getColor(mContext, R.color.grid_color));
        xAxisLine.setTextColor(ContextCompat.getColor(mContext, R.color.label_text));

        //主图左Y轴
        axisLeftLine = lineChart.getAxisLeft();
        axisLeftLine.setLabelCount(5, true);
        axisLeftLine.setDrawGridLines(false);
        axisLeftLine.setValueLineInside(true);
        axisLeftLine.setDrawTopBottomGridLine(false);
        axisLeftLine.setDrawAxisLine(false);
        axisLeftLine.setPosition(landscape?YAxis.YAxisLabelPosition.OUTSIDE_CHART:YAxis.YAxisLabelPosition.INSIDE_CHART);
        axisLeftLine.setGridColor(ContextCompat.getColor(mContext, R.color.grid_color));
        axisLeftLine.setTextColor(ContextCompat.getColor(mContext, R.color.label_text));
        axisLeftLine.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return NumberUtils.keepPrecisionR(value, 2);
            }
        });

        //主图右Y轴
        axisRightLine = lineChart.getAxisRight();
        axisRightLine.setLabelCount(5, true);
        axisRightLine.setDrawTopBottomGridLine(false);
        axisRightLine.setDrawGridLines(true);
        axisRightLine.enableGridDashedLine(CommonUtil.dip2px(mContext, 4), CommonUtil.dip2px(mContext, 3), 0);
        axisRightLine.setDrawAxisLine(false);
        axisRightLine.setValueLineInside(true);
        axisRightLine.setPosition(landscape?YAxis.YAxisLabelPosition.OUTSIDE_CHART:YAxis.YAxisLabelPosition.INSIDE_CHART);
        axisRightLine.setAxisLineColor(ContextCompat.getColor(mContext, R.color.grid_color));
        axisRightLine.setTextColor(ContextCompat.getColor(mContext, R.color.label_text));
        axisRightLine.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                DecimalFormat mFormat = new DecimalFormat("#0.00%");
                return mFormat.format(value);
            }
        });

        //副图X轴
        xAxisBar = (TimeXAxis) barChart.getXAxis();
        xAxisBar.setDrawLabels(false);
        xAxisBar.setDrawAxisLine(false);
        xAxisBar.setTextColor(ContextCompat.getColor(mContext, R.color.label_text));
        xAxisBar.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisBar.setAvoidFirstLastClipping(true);
        xAxisBar.setGridColor(ContextCompat.getColor(mContext, R.color.grid_color));

        //副图左Y轴
        axisLeftBar = barChart.getAxisLeft();
        axisLeftBar.setDrawGridLines(false);
        axisLeftBar.setDrawAxisLine(false);
        axisLeftBar.setTextColor(ContextCompat.getColor(mContext, R.color.label_text));
        axisLeftBar.setPosition(landscape?YAxis.YAxisLabelPosition.OUTSIDE_CHART:YAxis.YAxisLabelPosition.INSIDE_CHART);
        axisLeftBar.setDrawLabels(true);
        axisLeftBar.setLabelCount(2, true);
        axisLeftBar.setAxisMinimum(0);
        axisLeftBar.setSpaceTop(5);
        axisLeftBar.setValueLineInside(true);
        axisLeftBar.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (value == 0) {
                    return "";
                } else {
                    return (int) value + "";
                }
            }
        });

        //副图右Y轴
        axisRightBar = barChart.getAxisRight();
        axisRightBar.setDrawLabels(false);
        axisRightBar.setDrawGridLines(true);
        axisRightBar.setDrawAxisLine(false);
        axisRightBar.setLabelCount(3, false);
        axisRightBar.setDrawTopBottomGridLine(false);
        axisRightBar.enableGridDashedLine(CommonUtil.dip2px(mContext, 4), CommonUtil.dip2px(mContext, 3), 0);

        //设置图表偏移边距
        int left_right = 0;
        if(landscape){
            left_right = CommonUtil.dip2px(mContext, 50);
        }else {
            left_right = CommonUtil.dip2px(mContext, 5);
        }
        lineChart.setViewPortOffsets(left_right, CommonUtil.dip2px(mContext, 5), left_right, CommonUtil.dip2px(mContext, 15));
        barChart.setViewPortOffsets(left_right, 0, left_right, CommonUtil.dip2px(mContext, 15));

        //手势联动监听
        gestureListenerLine = new CoupleChartGestureListener(lineChart, new Chart[]{barChart});
        gestureListenerBar = new CoupleChartGestureListener(barChart, new Chart[]{lineChart});
        lineChart.setOnChartGestureListener(gestureListenerLine);
        barChart.setOnChartGestureListener(gestureListenerBar);

        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                lineChart.highlightValue(h);
                barChart.highlightValue(new Highlight(h.getX(), h.getDataSetIndex(), -1));
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
                lineChart.highlightValue(new Highlight(h.getX(), h.getDataSetIndex(), -1));
            }

            @Override
            public void onNothingSelected() {
                lineChart.highlightValues(null);
            }
        });

    }

    /**
     * 是否显示坐标轴label
     *
     * @param isShow
     */
    private void setShowLabels(boolean isShow) {
        lineChart.getAxisLeft().setDrawLabels(isShow);
        lineChart.getAxisRight().setDrawLabels(isShow);
        lineChart.getXAxis().setDrawLabels(isShow);
        barChart.getAxisLeft().setDrawLabels(isShow);
    }

    /**
     * 设置分时数据
     *
     * @param mData
     */
    public void setDataToChart(KTimeData mData) {

        if (mData.getDatas().size() == 0) {
            cirCleView.setVisibility(View.GONE);
            return;
        } else {
            cirCleView.setVisibility(View.VISIBLE);
        }

        setShowLabels(true);
        setMarkerView(mData);
        setBottomMarkerView(mData);

        axisLeftLine.setAxisMinimum(mData.getMin());
        axisLeftLine.setAxisMaximum(mData.getMax());


        if (Float.isNaN(mData.getPercentMax()) || mData.getPercentMax() == 0) {
            axisLeftBar.setAxisMaximum(0);
            axisRightLine.setAxisMinimum(-0.01f);
            axisRightLine.setAxisMaximum(0.01f);
        } else {
            axisLeftBar.setAxisMaximum(mData.getVolMaxTime());
            axisRightLine.setAxisMinimum(mData.getPercentMin());
            axisRightLine.setAxisMaximum(mData.getPercentMax());
        }

        ArrayList<Entry> lineCJEntries = new ArrayList<>();
        ArrayList<Entry> lineJJEntries = new ArrayList<>();
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0, j = 0; i < mData.getDatas().size(); i++, j++) {
            TimeDataModel t = mData.getDatas().get(j);
            if (t == null) {
                lineCJEntries.add(new Entry(i, Float.NaN));
                lineJJEntries.add(new Entry(i, Float.NaN));
                barEntries.add(new BarEntry(i, Float.NaN));
                continue;
            }
            lineCJEntries.add(new Entry(i, (float) mData.getDatas().get(i).getNowPrice()));
            lineJJEntries.add(new Entry(i, (float) mData.getDatas().get(i).getAveragePrice()));
            barEntries.add(new BarEntry(i, mData.getDatas().get(i).getVolume()));
        }
        d1 = new LineDataSet(lineCJEntries, "成交价");
        d2 = new LineDataSet(lineJJEntries, "均价");
        d1.setDrawValues(false);
        d2.setDrawValues(false);
        d1.setLineWidth(0.7f);
        d2.setLineWidth(0.7f);
        d1.setColor(ContextCompat.getColor(mContext, R.color.minute_blue));
        d2.setColor(ContextCompat.getColor(mContext, R.color.minute_yellow));
        d1.setDrawFilled(true);
        d1.setFillColor(ContextCompat.getColor(mContext, R.color.fill_Color));
        d1.setHighLightColor(ContextCompat.getColor(mContext, R.color.highLight_Color));
        d2.setHighlightEnabled(false);
        d1.setDrawCircles(false);
        d2.setDrawCircles(false);
        d1.setAxisDependency(YAxis.AxisDependency.LEFT);
        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(d1);
        sets.add(d2);
        LineData cd = new LineData(sets);
        lineChart.setData(cd);

        barDataSet = new BarDataSet(barEntries, "成交量");
        barDataSet.setHighLightColor(ContextCompat.getColor(mContext, R.color.highLight_Color));
        barDataSet.setDrawValues(false);
        List<Integer> list = new ArrayList<>();
        list.add(ContextCompat.getColor(mContext, R.color.up_color));
        list.add(ContextCompat.getColor(mContext, R.color.down_color));
        barDataSet.setColors(list);
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        //下面方法需在填充数据后调用
        Long timeMilli = 242L;
        xAxisLine.setXLabels(getXLabels());
        xAxisLine.setLabelCount(getXLabels().size(), false);
        xAxisBar.setXLabels(getXLabels());
        xAxisBar.setLabelCount(getXLabels().size(), false);
        lineChart.setVisibleXRange(timeMilli, timeMilli);
        barChart.setVisibleXRange(timeMilli, timeMilli);

        lineChart.moveViewToX(mData.getDatas().size() - 1);
        barChart.moveViewToX(mData.getDatas().size() - 1);
        lineChart.invalidate();
        barChart.invalidate();
    }

    public void dynamicsAddOne(String xValue, float chPrice, float junPrice, float vol, int length) {
        int index = length - 1;
        LineData lineData = lineChart.getData();
        ILineDataSet d1 = lineData.getDataSetByIndex(0);
        d1.addEntry(new Entry(index, chPrice));
        ILineDataSet d2 = lineData.getDataSetByIndex(1);
        d2.addEntry(new Entry(index, junPrice));

        BarData barData = barChart.getData();
        IBarDataSet barDataSet = barData.getDataSetByIndex(0);
        barDataSet.addEntry(new BarEntry(index, vol));
        lineData.notifyDataChanged();
        lineChart.notifyDataSetChanged();
        Long timeMilli = 242L;
        if (timeMilli != null) {
            lineChart.setVisibleXRange(timeMilli, timeMilli);
        }
        lineChart.moveViewToX(index);

        barData.notifyDataChanged();
        barChart.notifyDataSetChanged();
        if (timeMilli != null) {
            barChart.setVisibleXRange(timeMilli, timeMilli);
        }
        barChart.moveViewToX(index);
    }

    public void dynamicsUpdateOne(String xValue, float chPrice, float junPrice, float vol, int length) {
        int index = length - 1;
        LineData lineData = lineChart.getData();
        ILineDataSet d1 = lineData.getDataSetByIndex(0);
        Entry e = d1.getEntryForIndex(index);
        d1.removeEntry(e);
        d1.addEntry(new Entry(index, chPrice));

        ILineDataSet d2 = lineData.getDataSetByIndex(1);
        Entry e2 = d2.getEntryForIndex(index);
        d2.removeEntry(e2);
        d2.addEntry(new Entry(index, junPrice));

        BarData barData = barChart.getData();
        IBarDataSet barDataSet = barData.getDataSetByIndex(0);
        barDataSet.removeEntry(index);
        barDataSet.addEntry(new BarEntry(index, vol));

        //不可见修改数据不刷新
        lineData.notifyDataChanged();
        lineChart.notifyDataSetChanged();
        lineChart.moveViewToX(index);

        barData.notifyDataChanged();
        barChart.notifyDataSetChanged();
        barChart.moveViewToX(index);
    }

    public void cleanData() {
        if (lineChart != null && lineChart.getLineData() != null) {
            setShowLabels(false);
            lineChart.clearValues();
            barChart.clearValues();
        }
        if (cirCleView != null) {
            cirCleView.setVisibility(View.GONE);
        }
    }

    private void setMarkerView(KTimeData mData) {
        LeftMarkerView leftMarkerView = new LeftMarkerView(mContext, R.layout.my_markerview, new DecimalFormat("#0.00"));
        TimeRightMarkerView rightMarkerView = new TimeRightMarkerView(mContext, R.layout.my_markerview);
        lineChart.setMarker(leftMarkerView, rightMarkerView, mData);
    }

    private void setBottomMarkerView(KTimeData kDatas) {
        BarBottomMarkerView bottomMarkerView = new BarBottomMarkerView(mContext, R.layout.my_markerview);
        barChart.setMarker(bottomMarkerView, kDatas);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(CirclePositionTime position) {
        cirCleView.setX(position.cx - CommonUtil.dip2px(mContext, 7));
        cirCleView.setY(position.cy - CommonUtil.dip2px(mContext, 9));
    }

    private void playHeartbeatAnimation(final View heartbeatView) {
        AnimationSet swellAnimationSet = new AnimationSet(true);
        swellAnimationSet.addAnimation(new ScaleAnimation(1.0f, 2.0f, 1.0f, 2.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
        swellAnimationSet.setDuration(1000);
        swellAnimationSet.setInterpolator(new AccelerateInterpolator());
        swellAnimationSet.setFillAfter(true);//动画终止时停留在最后一帧~不然会回到没有执行之前的状态
        heartbeatView.startAnimation(swellAnimationSet);
        swellAnimationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                AnimationSet shrinkAnimationSet = new AnimationSet(true);
                shrinkAnimationSet.addAnimation(new ScaleAnimation(2.0f, 1.0f, 2.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
                shrinkAnimationSet.setDuration(1000);
                shrinkAnimationSet.setInterpolator(new DecelerateInterpolator());
                shrinkAnimationSet.setFillAfter(false);
                heartbeatView.startAnimation(shrinkAnimationSet);// 动画结束时重新开始，实现心跳的View
                shrinkAnimationSet.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        playHeartbeatAnimation(heartbeatView);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });
    }

    private SparseArray<String> getXLabels() {
        SparseArray<String> xLabels = new SparseArray<>();
        xLabels.put(0, "09:30");
        xLabels.put(60, "10:30");
        xLabels.put(121, "11:30/13:00");
        xLabels.put(182, "14:00");
        xLabels.put(241, "15:00");
        return xLabels;
    }

    public void eventBusUnregister() {
        EventBus.getDefault().unregister(this);
    }

    public CoupleChartGestureListener getGestureListenerLine() {
        return gestureListenerLine;
    }

    public CoupleChartGestureListener getGestureListenerBar() {
        return gestureListenerBar;
    }
}

package com.github.mikephil.charting.stockChart.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.stockChart.dataManage.KLineDataManage;
import com.github.mikephil.charting.stockChart.enums.TimeType;
import com.github.mikephil.charting.stockChart.markerView.BarBottomMarkerView;
import com.github.mikephil.charting.stockChart.renderer.MyCombinedChartRenderer;
import com.github.mikephil.charting.utils.DataTimeUtil;

/**
 * Created by ly on 2018/3/14.
 */
public class MyCombinedChart extends CombinedChart {
    private BarBottomMarkerView markerBottom;
    private KLineDataManage kLineData;

    public MyCombinedChart(Context context) {
        super(context);
    }

    public MyCombinedChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCombinedChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void initRenderer() {
        mRenderer = new MyCombinedChartRenderer(this, mAnimator, mViewPortHandler);
    }

    private TimeType timeType = TimeType.TIME_DATE;

    public void setMarker(BarBottomMarkerView markerBottom, KLineDataManage kLineData, TimeType timeType) {
        this.markerBottom = markerBottom;
        this.kLineData = kLineData;
        this.timeType = timeType;
    }

    @Override
    protected void drawMarkers(Canvas canvas) {
        // if there is no marker view or drawing marker is disabled
        if (!isDrawMarkersEnabled() || !valuesToHighlight()) {
            return;
        }

        for (int i = 0; i < mIndicesToHighlight.length; i++) {

            Highlight highlight = mIndicesToHighlight[i];

            IDataSet set = mData.getDataSetByIndex(highlight.getDataSetIndex());
            if(set == null){
                continue;
            }
            Entry e = mData.getEntryForHighlight(mIndicesToHighlight[i]);
            int entryIndex = set.getEntryIndex(e);

            // make sure entry not null
            if (e == null || entryIndex > set.getEntryCount() * mAnimator.getPhaseX()) {
                continue;
            }

            float[] pos = getMarkerPosition(highlight);

            // check bounds
            if (!mViewPortHandler.isInBounds(pos[0], pos[1])) {
                continue;
            }

            String date = null;

            if (this.timeType == TimeType.TIME_HOUR) {
                date = DataTimeUtil.secToTime(kLineData.getKLineDatas().get((int) e.getX()).getDateMills());
            } else {
                date = DataTimeUtil.secToDate(kLineData.getKLineDatas().get((int) e.getX()).getDateMills());
            }

            markerBottom.setData(date);
            markerBottom.refreshContent(e, highlight);
            markerBottom.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            markerBottom.layout(0, 0, markerBottom.getMeasuredWidth(), markerBottom.getMeasuredHeight());

            int width = markerBottom.getWidth() / 2;
            if (mViewPortHandler.contentRight() - pos[0] <= width) {
                markerBottom.draw(canvas, mViewPortHandler.contentRight() - markerBottom.getWidth() / 2, mViewPortHandler.contentBottom() + markerBottom.getHeight());//-markerBottom.getHeight()   CommonUtil.dip2px(getContext(),65.8f)
            } else if (pos[0] - mViewPortHandler.contentLeft() <= width) {
                markerBottom.draw(canvas, mViewPortHandler.contentLeft() + markerBottom.getWidth() / 2, mViewPortHandler.contentBottom() + markerBottom.getHeight());
            } else {
                markerBottom.draw(canvas, pos[0], mViewPortHandler.contentBottom() + markerBottom.getHeight());
            }
        }
    }


}

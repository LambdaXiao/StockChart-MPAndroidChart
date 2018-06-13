package com.github.mikephil.charting.stockChart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by ly on 2016/9/24.
 */
public class TimeBarChartRenderer extends BarChartRenderer {

    public TimeBarChartRenderer(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    @Override
    protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {
        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        mBarBorderPaint.setColor(dataSet.getBarBorderColor());
        mBarBorderPaint.setStrokeWidth(Utils.convertDpToPixel(dataSet.getBarBorderWidth()));

        final boolean drawBorder = dataSet.getBarBorderWidth() > 0.f;

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        // draw the bar shadow before the values
        if (mChart.isDrawBarShadowEnabled()) {
            mShadowPaint.setColor(dataSet.getBarShadowColor());

            BarData barData = mChart.getBarData();

            final float barWidth = barData.getBarWidth();
            final float barWidthHalf = barWidth / 2.0f;
            float x;

            for (int i = 0, count = Math.min((int) (Math.ceil((float) (dataSet.getEntryCount()) * phaseX)), dataSet.getEntryCount());
                 i < count;
                 i++) {

                BarEntry e = dataSet.getEntryForIndex(i);

                x = e.getX() + offSet;

                mBarShadowRectBuffer.left = x - barWidthHalf;
                mBarShadowRectBuffer.right = x + barWidthHalf;

                trans.rectValueToPixel(mBarShadowRectBuffer);

                if (!mViewPortHandler.isInBoundsLeft(mBarShadowRectBuffer.right)) {
                    continue;
                }

                if (!mViewPortHandler.isInBoundsRight(mBarShadowRectBuffer.left)) {
                    break;
                }

                mBarShadowRectBuffer.top = mViewPortHandler.contentTop();
                mBarShadowRectBuffer.bottom = mViewPortHandler.contentBottom();

                c.drawRect(mBarShadowRectBuffer, mShadowPaint);
            }
        }

        // initialize the buffer
        BarBuffer buffer = mBarBuffers[index];
        buffer.setPhases(phaseX, phaseY);
        buffer.setDataSet(index);
        buffer.setInverted(mChart.isInverted(dataSet.getAxisDependency()));
        buffer.setBarWidth(mChart.getBarData().getBarWidth());

        buffer.feed(dataSet);

        trans.pointValuesToPixel(buffer.buffer);

        for (int j = 0; j < buffer.size(); j += 4) {

            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                continue;
            }

            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) {
                break;
            }


            // Set the color for the currently drawn value. If the index
            // is out of bounds, reuse colors.
            mRenderPaint.setColor(dataSet.getColor(j / 4));


            int i = j / 4;
//                if (i > 0) {
            Object openClose = dataSet.getEntryForIndex(i).getData();
            if (openClose == null) {
                if (i > 0) {
                    if (dataSet.getEntryForIndex(i).getY() > dataSet.getEntryForIndex(i - 1).getY()) {
                        mRenderPaint.setColor(dataSet.getIncreasingColor() == ColorTemplate.COLOR_NONE ?
                                dataSet.getColor(j) :
                                dataSet.getIncreasingColor());
                        mRenderPaint.setStyle(dataSet.getIncreasingPaintStyle());
                    } else {
                        mRenderPaint.setColor(dataSet.getDecreasingColor() == ColorTemplate.COLOR_NONE ?
                                dataSet.getColor(j) :
                                dataSet.getDecreasingColor());
                        mRenderPaint.setStyle(dataSet.getDecreasingPaintStyle());
                    }
                } else {
                    mRenderPaint.setColor(dataSet.getIncreasingColor() == ColorTemplate.COLOR_NONE ?
                            dataSet.getColor(j) :
                            dataSet.getIncreasingColor());
                    mRenderPaint.setStyle(dataSet.getIncreasingPaintStyle());
                }
            } else {//根据开平判断柱状图的颜色填充
                float value = (Float) openClose;
                if (value > 0) {//表示增加
                    mRenderPaint.setColor(dataSet.getIncreasingColor() == ColorTemplate.COLOR_NONE ?
                            dataSet.getColor(j) :
                            dataSet.getIncreasingColor());
                    mRenderPaint.setStyle(dataSet.getIncreasingPaintStyle());
                } else if (value <= 0) {
                    mRenderPaint.setColor(dataSet.getDecreasingColor() == ColorTemplate.COLOR_NONE ?
                            dataSet.getColor(j) :
                            dataSet.getDecreasingColor());
                    mRenderPaint.setStyle(dataSet.getDecreasingPaintStyle());
                }
//                else if (value == 0) {
//                    mRenderPaint.setColor(dataSet.getNeutralColor() == ColorTemplate.COLOR_NONE ?
//                            dataSet.getColor(j) :
//                            dataSet.getNeutralColor());
//                    mRenderPaint.setStyle(dataSet.getDecreasingPaintStyle());
//                }
            }


            c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                    buffer.buffer[j + 3], mRenderPaint);

            if (drawBorder) {
                c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3], mBarBorderPaint);
            }
        }
    }


}

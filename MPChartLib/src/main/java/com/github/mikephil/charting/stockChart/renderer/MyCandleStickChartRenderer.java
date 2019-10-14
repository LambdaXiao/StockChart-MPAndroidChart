package com.github.mikephil.charting.stockChart.renderer;

import android.graphics.Canvas;
import android.graphics.Color;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.interfaces.dataprovider.CandleDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.renderer.CandleStickChartRenderer;
import com.github.mikephil.charting.utils.NumberUtils;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

/**
 * 描述：蜡烛图渲染
 */
public class MyCandleStickChartRenderer extends CandleStickChartRenderer {
    public MyCandleStickChartRenderer(CandleDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    @Override
    public void drawValues(Canvas c) {

        List<ICandleDataSet> dataSets = mChart.getCandleData().getDataSets();

        mValuePaint.setColor(Color.parseColor("#406ebc"));
        for (int i = 0; i < dataSets.size(); i++) {

            ICandleDataSet dataSet = dataSets.get(i);

            if (!shouldDrawValues(dataSet) || dataSet.getEntryCount() < 1) {
                continue;
            }

            // apply the text-styling defined by the DataSet
            applyValueTextStyle(dataSet);

            Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

            mXBounds.set(mChart, dataSet);

            float[] positions = trans.generateTransformedValuesCandle(
                    dataSet, mAnimator.getPhaseX(), mAnimator.getPhaseY(), mXBounds.min, mXBounds.max);

            //计算最大值和最小值
            float maxValue = 0, minValue = 0;
            int maxIndex = 0, minIndex = 0;
            CandleEntry maxEntry = null, minEntry = null;
            boolean firstInit = true;
            for (int j = 0; j < positions.length; j += 2) {

                float x = positions[j];
                float y = positions[j + 1];

                if (!mViewPortHandler.isInBoundsRight(x)) {
                    break;
                }

                if (!mViewPortHandler.isInBoundsLeft(x) || !mViewPortHandler.isInBoundsY(y)) {
                    continue;
                }

                CandleEntry entry = dataSet.getEntryForIndex(j / 2 + mXBounds.min);

                if (firstInit) {
                    maxValue = entry.getHigh();
                    minValue = entry.getLow();
                    firstInit = false;
                    maxEntry = entry;
                    minEntry = entry;
                } else {
                    if (entry.getHigh() > maxValue) {
                        maxValue = entry.getHigh();
                        maxIndex = j;
                        maxEntry = entry;
                    }

                    if (entry.getLow() < minValue) {
                        minValue = entry.getLow();
                        minIndex = j;
                        minEntry = entry;
                    }

                }
            }

            //绘制最大值和最小值
            if (maxIndex > minIndex) {
                //画右边
                String highString = NumberUtils.keepPrecisionR(minValue, dataSet.getPrecision());

                //计算显示位置
                //计算文本宽度
                int highStringWidth = Utils.calcTextWidth(mValuePaint, "──• " + highString);
                int highStringHeight = Utils.calcTextHeight(mValuePaint, "──• " + highString);

                float[] tPosition = new float[2];
                tPosition[0] = minEntry == null ? 0f : minEntry.getX();
                tPosition[1] = minEntry == null ? 0f : minEntry.getLow();
                trans.pointValuesToPixel(tPosition);
                if (tPosition[0] + highStringWidth / 2 > mViewPortHandler.contentRight()) {
                    c.drawText(highString + " •──", tPosition[0] - highStringWidth / 2, tPosition[1] + highStringHeight / 2, mValuePaint);
                } else {
                    c.drawText("──• " + highString, tPosition[0] + highStringWidth / 2, tPosition[1] + highStringHeight / 2, mValuePaint);
                }
            } else {
                //画左边
                String highString = NumberUtils.keepPrecisionR(minValue, dataSet.getPrecision());

                //计算显示位置
                int highStringWidth = Utils.calcTextWidth(mValuePaint, highString + " •──");
                int highStringHeight = Utils.calcTextHeight(mValuePaint, highString + " •──");

                float[] tPosition = new float[2];
                tPosition[0] = minEntry == null ? 0f : minEntry.getX();
                tPosition[1] = minEntry == null ? 0f : minEntry.getLow();
                trans.pointValuesToPixel(tPosition);
                if (tPosition[0] - highStringWidth / 2 < mViewPortHandler.contentLeft()) {
                    c.drawText("──• " + highString, tPosition[0] + highStringWidth / 2, tPosition[1] + highStringHeight / 2, mValuePaint);
                } else {
                    c.drawText(highString + " •──", tPosition[0] - highStringWidth / 2, tPosition[1] + highStringHeight / 2, mValuePaint);
                }
            }


            //这里画的是上面两个点
            if (maxIndex > minIndex) {
                //画左边
                String highString = NumberUtils.keepPrecisionR(maxValue, dataSet.getPrecision());

                int highStringWidth = Utils.calcTextWidth(mValuePaint, highString + " •──");
                int highStringHeight = Utils.calcTextHeight(mValuePaint, highString + " •──");

                float[] tPosition = new float[2];
                tPosition[0] = maxEntry == null ? 0f : maxEntry.getX();
                tPosition[1] = maxEntry == null ? 0f : maxEntry.getHigh();
                trans.pointValuesToPixel(tPosition);
                if ((tPosition[0] - highStringWidth / 2) < mViewPortHandler.contentLeft()) {
                    c.drawText("──• " + highString, tPosition[0] + highStringWidth / 2, tPosition[1] + highStringHeight / 2, mValuePaint);
                } else {
                    c.drawText(highString + " •──", tPosition[0] - highStringWidth / 2, tPosition[1] + highStringHeight / 2, mValuePaint);
                }
            } else {
                //画右边
                String highString = NumberUtils.keepPrecisionR(maxValue, dataSet.getPrecision());

                //计算显示位置
                int highStringWidth = Utils.calcTextWidth(mValuePaint, "──• " + highString);
                int highStringHeight = Utils.calcTextHeight(mValuePaint, "──• " + highString);

                float[] tPosition = new float[2];
                tPosition[0] = maxEntry == null ? 0f : maxEntry.getX();
                tPosition[1] = maxEntry == null ? 0f : maxEntry.getHigh();
                trans.pointValuesToPixel(tPosition);
                if (tPosition[0] + highStringWidth / 2 > mViewPortHandler.contentRight()) {
                    c.drawText(highString + " •──", tPosition[0] - highStringWidth / 2, tPosition[1] + highStringHeight / 2, mValuePaint);
                } else {
                    c.drawText("──• " + highString, tPosition[0] + highStringWidth / 2, tPosition[1] + highStringHeight / 2, mValuePaint);
                }
            }
        }
    }
}

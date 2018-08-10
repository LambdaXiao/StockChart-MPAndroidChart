package com.github.mikephil.charting.stockChart;

import android.graphics.Canvas;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.renderer.YAxisRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * 为每个label设置颜色
 */
public class ColorContentYAxisRenderer extends YAxisRenderer {
    private int[] mLabelColorArray;

    public ColorContentYAxisRenderer(ViewPortHandler viewPortHandler, YAxis yAxis, Transformer trans) {
        super(viewPortHandler, yAxis, trans);
    }

    /**
     * 给每个label单独设置颜色
     */
    public void setLabelColor(int[] labelColorArray) {
        mLabelColorArray = labelColorArray;
    }

    @Override
    protected void drawYLabels(Canvas c, float fixedPosition, float[] positions, float offset) {
        final int from = mYAxis.isDrawBottomYLabelEntryEnabled() ? 0 : 1;
        final int to = mYAxis.isDrawTopYLabelEntryEnabled()
                ? mYAxis.mEntryCount
                : (mYAxis.mEntryCount - 1);
        int originalColor = mAxisLabelPaint.getColor();
        // draw
        if (mYAxis.isValueLineInside()) {
            for (int i = from; i < to; i++) {
                if (mLabelColorArray != null && i >= 0 && i < mLabelColorArray.length) {
                    int labelColor = mLabelColorArray[i];
                    mAxisLabelPaint.setColor(labelColor);
                } else {
                    mAxisLabelPaint.setColor(originalColor);
                }
                String text = mYAxis.getFormattedLabel(i);
                if (i == 0) {
                    c.drawText(text, fixedPosition, mViewPortHandler.contentBottom() - Utils.convertDpToPixel(1), mAxisLabelPaint);
                } else if (i == to - 1) {
                    c.drawText(text, fixedPosition, mViewPortHandler.contentTop() + Utils.convertDpToPixel(8), mAxisLabelPaint);
                } else {
                    c.drawText(text, fixedPosition, positions[i * 2 + 1] + offset, mAxisLabelPaint);
                }
            }
        } else {
            for (int i = from; i < to; i++) {
                if (mLabelColorArray != null && i >= 0 && i < mLabelColorArray.length) {
                    int labelColor = mLabelColorArray[i];
                    mAxisLabelPaint.setColor(labelColor);
                } else {
                    mAxisLabelPaint.setColor(originalColor);
                }
                String text = mYAxis.getFormattedLabel(i);
                c.drawText(text, fixedPosition, positions[i * 2 + 1] + offset, mAxisLabelPaint);
            }
        }
    }
}

package com.github.mikephil.charting.stockChart.renderer;

import android.graphics.Canvas;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.renderer.YAxisRenderer;
import com.github.mikephil.charting.utils.NumberUtils;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * 为每个label设置颜色
 */
public class ColorContentYAxisRenderer extends YAxisRenderer {
    private int[] mLabelColorArray;
    private double mClosePrice = 0;//昨收价
    private boolean landscape;//是否横屏

    public ColorContentYAxisRenderer(ViewPortHandler viewPortHandler, YAxis yAxis, Transformer trans) {
        super(viewPortHandler, yAxis, trans);
    }

    /**
     * 给每个label单独设置颜色
     */
    public void setLabelColor(int[] labelColorArray) {
        mLabelColorArray = labelColorArray;
    }

    /**
     * 昨收价
     *
     * @param closePrice
     */
    public void setClosePrice(double closePrice) {
        mClosePrice = closePrice;
    }

    public void setLandscape(boolean landscape) {
        this.landscape = landscape;
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
                if (!landscape && i > from && i < to - 1) {
                    continue;
                }
                String text = mYAxis.getFormattedLabel(i);
                if (mLabelColorArray != null && mLabelColorArray.length == 3) {
                    if ((!text.endsWith("%") && NumberUtils.String2Double(text) > mClosePrice) || (text.endsWith("%") && NumberUtils.String2Double(text.substring(0, text.length() - 1)) > 0)) {
                        mAxisLabelPaint.setColor(mLabelColorArray[0]);
                    } else if ((!text.endsWith("%") && NumberUtils.String2Double(text) == mClosePrice) || (text.endsWith("%") && NumberUtils.String2Double(text.substring(0, text.length() - 1)) == 0)) {
                        mAxisLabelPaint.setColor(mLabelColorArray[1]);
                    } else {
                        mAxisLabelPaint.setColor(mLabelColorArray[2]);
                    }
                } else {
                    mAxisLabelPaint.setColor(originalColor);
                }
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
                if (!landscape && i > from && i < to - 1) {
                    continue;
                }
                String text = mYAxis.getFormattedLabel(i);
                if (mLabelColorArray != null && mLabelColorArray.length == 3) {
                    if ((!text.endsWith("%") && NumberUtils.String2Double(text) > mClosePrice) || (text.endsWith("%") && NumberUtils.String2Double(text.substring(0, text.length() - 1)) > 0)) {
                        mAxisLabelPaint.setColor(mLabelColorArray[0]);
                    } else if ((!text.endsWith("%") && NumberUtils.String2Double(text) == mClosePrice) || (text.endsWith("%") && NumberUtils.String2Double(text.substring(0, text.length() - 1)) == 0)) {
                        mAxisLabelPaint.setColor(mLabelColorArray[1]);
                    } else {
                        mAxisLabelPaint.setColor(mLabelColorArray[2]);
                    }
                } else {
                    mAxisLabelPaint.setColor(originalColor);
                }
                c.drawText(text, fixedPosition, positions[i * 2 + 1] + offset, mAxisLabelPaint);
            }
        }
    }
}

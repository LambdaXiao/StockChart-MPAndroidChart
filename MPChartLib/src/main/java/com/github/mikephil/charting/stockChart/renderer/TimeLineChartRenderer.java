package com.github.mikephil.charting.stockChart.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.stockChart.event.BaseEvent;
import com.github.mikephil.charting.stockChart.model.CirclePositionTime;
import com.github.mikephil.charting.renderer.LineChartRenderer;
import com.github.mikephil.charting.utils.NumberUtils;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by ly on 2017/7/3.
 */

public class TimeLineChartRenderer extends LineChartRenderer {


    public TimeLineChartRenderer(LineDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    /**
     * Draws a normal line.
     *
     * @param c
     * @param dataSet
     */
    @Override
    protected void drawLinear(Canvas c, ILineDataSet dataSet) {

        int entryCount = dataSet.getEntryCount();

        final boolean isDrawSteppedEnabled = dataSet.isDrawSteppedEnabled();
        final int pointsPerEntryPair = isDrawSteppedEnabled ? 4 : 2;

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        float phaseY = mAnimator.getPhaseY();

        mRenderPaint.setStyle(Paint.Style.STROKE);

        Canvas canvas = null;

        // if the data-set is dashed, draw on bitmap-canvas
        if (dataSet.isDashedLineEnabled()) {
            canvas = mBitmapCanvas;
        } else {
            canvas = c;
        }

        mXBounds.set(mChart, dataSet);

        // if drawing filled is enabled
        if (dataSet.isDrawFilledEnabled() && entryCount > 0) {
            drawLinearFill(c, dataSet, trans, mXBounds);
        }

        // more than 1 color
        if (dataSet.getColors().size() > 1) {

            if (mLineBuffer.length <= pointsPerEntryPair * 2) {
                mLineBuffer = new float[pointsPerEntryPair * 4];
            }

            for (int j = mXBounds.min; j <= mXBounds.range + mXBounds.min; j++) {

                Entry e = dataSet.getEntryForIndex(j);
                if (e == null) {
                    continue;
                }

                mLineBuffer[0] = e.getX();
                mLineBuffer[1] = e.getY() * phaseY;

                if (j < mXBounds.max) {

                    e = dataSet.getEntryForIndex(j + 1);

                    if (e == null) {
                        break;
                    }

                    if (isDrawSteppedEnabled) {
                        mLineBuffer[2] = e.getX();
                        mLineBuffer[3] = mLineBuffer[1];
                        mLineBuffer[4] = mLineBuffer[2];
                        mLineBuffer[5] = mLineBuffer[3];
                        mLineBuffer[6] = e.getX();
                        mLineBuffer[7] = e.getY() * phaseY;
                    } else {
                        mLineBuffer[2] = e.getX();
                        mLineBuffer[3] = e.getY() * phaseY;
                    }

                } else {
                    mLineBuffer[2] = mLineBuffer[0];
                    mLineBuffer[3] = mLineBuffer[1];
                }

                trans.pointValuesToPixel(mLineBuffer);

                if (!mViewPortHandler.isInBoundsRight(mLineBuffer[0])) {
                    break;
                }

                // make sure the lines don't do shitty things outside
                // bounds
                if (!mViewPortHandler.isInBoundsLeft(mLineBuffer[2])
                        || (!mViewPortHandler.isInBoundsTop(mLineBuffer[1]) && !mViewPortHandler
                        .isInBoundsBottom(mLineBuffer[3]))) {
                    continue;
                }

                // get the color that is set for this line-segment
                mRenderPaint.setColor(dataSet.getColor(j));

                canvas.drawLines(mLineBuffer, 0, pointsPerEntryPair * 2, mRenderPaint);
            }

        } else { // only one color per dataset

            if (mLineBuffer.length < Math.max((entryCount) * pointsPerEntryPair, pointsPerEntryPair) * 2) {
                mLineBuffer = new float[Math.max((entryCount) * pointsPerEntryPair, pointsPerEntryPair) * 4];
            }

            Entry e1, e2;

            e1 = dataSet.getEntryForIndex(mXBounds.min);

            if (e1 != null) {

                int j = 0;
                for (int x = mXBounds.min; x <= mXBounds.range + mXBounds.min; x++) {

                    e1 = dataSet.getEntryForIndex(x == 0 ? 0 : (x - 1));
                    e2 = dataSet.getEntryForIndex(x);

                    if (e1 == null || e2 == null) {
                        continue;
                    }

                    mLineBuffer[j++] = e1.getX();
                    mLineBuffer[j++] = e1.getY() * phaseY;

                    if (isDrawSteppedEnabled) {
                        mLineBuffer[j++] = e2.getX();
                        mLineBuffer[j++] = e1.getY() * phaseY;
                        mLineBuffer[j++] = e2.getX();
                        mLineBuffer[j++] = e1.getY() * phaseY;
                    }
                    //这些点与点之间不连接，用于五日分时
                    if (dataSet.getTimeDayType() == 5 && dataSet.getXLabels().indexOfKey(x == 0 ? 0 : (x - 1)) > 0) {
                        mLineBuffer[j++] = e1.getX();
                        mLineBuffer[j++] = e1.getY() * phaseY;
                    } else {
                        mLineBuffer[j++] = e2.getX();
                        mLineBuffer[j++] = e2.getY() * phaseY;
                    }
                }

                if (j > 0) {
                    trans.pointValuesToPixel(mLineBuffer);

                    final int size = Math.max((mXBounds.range + 1) * pointsPerEntryPair, pointsPerEntryPair) * 2;

                    mRenderPaint.setColor(dataSet.getColor());

                    int max = mXBounds.range + mXBounds.min;
//                    Log.e("内容",j+" "+mLineBuffer.length+" "+max);
                    canvas.drawLines(mLineBuffer, 0, size, mRenderPaint);
                }
            }
        }

        if (dataSet.isDrawCircleDashMarkerEnabled()) {//这个地方做了限制，一般是不会绘制的
            drawCircleDashMarker(canvas, dataSet, entryCount);//画虚线圆点和MarkerView
        }

        mRenderPaint.setPathEffect(null);
    }

    public void drawCircleDashMarker(Canvas canvas, ILineDataSet dataSet, int count) {

        //画虚线圆点和MarkerView
        PathEffect effects = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);
        Path path = new Path();
        // 应为这里会复用之前的mLineBuffer，在总长度不变的情况下，取一般就是之前的位置，mXBounds.range + mXBounds.min 是最新的K线的变动范围
        int pointOffset = (mXBounds.range + mXBounds.min + 1) * 4;
        if (dataSet.getEntryCount() != 0) {
            //画虚线参数设置
            path.moveTo(mLineBuffer[pointOffset - 2], mLineBuffer[pointOffset - 1]);
            path.lineTo(mViewPortHandler.contentRight(), mLineBuffer[pointOffset - 1]);
            mRenderPaint.setPathEffect(effects);

            Entry e = dataSet.getEntryForIndex(count - 1);//Utils.convertDpToPixel(35)
            mRenderPaint.setTextSize(Utils.convertDpToPixel(10));
            String text = NumberUtils.keepPrecisionR(e.getY(), dataSet.getPrecision());
            int width = Utils.calcTextWidth(mRenderPaint, text);
            int height = Utils.calcTextHeight(mRenderPaint, text);
            float rectLeft = mViewPortHandler.contentRight() - width - Utils.convertDpToPixel(4);
            float circleX = mLineBuffer[pointOffset - 2];

            if (circleX >= rectLeft) {
                mRenderPaint.setColor(Color.parseColor("#A65198FA"));
                mRenderPaint.setStyle(Paint.Style.FILL);
                float x = mLineBuffer[pointOffset - 2];
                float y = mLineBuffer[pointOffset - 1];
                if (y > mViewPortHandler.contentTop() + mViewPortHandler.getChartHeight() / 2) {
                    canvas.drawRect(rectLeft, y - Utils.convertDpToPixel(22), mViewPortHandler.contentRight(), y - Utils.convertDpToPixel(6), mRenderPaint);
                    Path pathS = new Path();
                    pathS.moveTo(x, y - Utils.convertDpToPixel(3));// 此点为多边形的起点
                    pathS.lineTo(x - Utils.convertDpToPixel(3), y - Utils.convertDpToPixel(6));
                    pathS.lineTo(x + Utils.convertDpToPixel(3), y - Utils.convertDpToPixel(6));
                    pathS.close(); // 使这些点构成封闭的多边形
                    canvas.drawPath(pathS, mRenderPaint);
                    mRenderPaint.setColor(Color.parseColor("#66FFFFFF"));
                    canvas.drawText(text, rectLeft + Utils.convertDpToPixel(2), y - Utils.convertDpToPixel(10), mRenderPaint);
                } else {
                    canvas.drawRect(rectLeft, y + Utils.convertDpToPixel(6), mViewPortHandler.contentRight(), y + Utils.convertDpToPixel(22), mRenderPaint);
                    Path pathS = new Path();
                    pathS.moveTo(x, y + Utils.convertDpToPixel(1));// 此点为多边形的起点
                    pathS.lineTo(x - Utils.convertDpToPixel(3), y + Utils.convertDpToPixel(6));
                    pathS.lineTo(x + Utils.convertDpToPixel(3), y + Utils.convertDpToPixel(6));
                    pathS.close(); // 使这些点构成封闭的多边形
                    canvas.drawPath(pathS, mRenderPaint);
                    mRenderPaint.setColor(Color.parseColor("#66FFFFFF"));
                    canvas.drawText(text, rectLeft + Utils.convertDpToPixel(2), y + Utils.convertDpToPixel(10) + height, mRenderPaint);
                }
            } else {
                canvas.drawPath(path, mRenderPaint);
                mRenderPaint.setStyle(Paint.Style.FILL);
                canvas.drawRect(rectLeft, mLineBuffer[pointOffset - 1] - Utils.convertDpToPixel(8), mViewPortHandler.contentRight(), mLineBuffer[pointOffset - 1] + Utils.convertDpToPixel(8), mRenderPaint);
                mRenderPaint.setColor(Color.parseColor("#FFFFFF"));
                canvas.drawText(text, rectLeft + Utils.convertDpToPixel(2), mLineBuffer[pointOffset - 1] + Utils.convertDpToPixel(3), mRenderPaint);
            }
        }
        mRenderPaint.setColor(Color.RED);
        postPosition(dataSet, mLineBuffer[pointOffset - 2], mLineBuffer[pointOffset - 1]);
    }

    public void postPosition(ILineDataSet dataSet, float x, float y) {
        CirclePositionTime position = new CirclePositionTime();
        position.cx = x;
        position.cy = y;
        BaseEvent event = new BaseEvent(dataSet.getTimeDayType());
        event.obj = position;
        EventBus.getDefault().post(event);
    }

}

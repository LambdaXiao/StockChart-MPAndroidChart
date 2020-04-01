package com.github.mikephil.charting.stockChart.enums;

/**
 * 画图类型
 */
public enum ChartType {
    ONE_DAY(241),//沪深股票当日分时线总点数
    FIVE_DAY(305),//沪深股票五日分时线总点数
    HK_ONE_DAY(331),//港股当日分时线总点数
    HK_FIVE_DAY(415),//港股五日分时线总点数
    US_ONE_DAY(390),//美股当日分时线总点数
    US_FIVE_DAY(488),//美股五日分时线总点数
    K_DAY_SMALL(100),//竖屏日K展示的总数据条数
    K_WEEK_SMALL(100),//竖屏周K展示的总数据条数
    K_MONTH_SMALL(100),//竖屏月K展示的总数据条数
    K_DAY_BIG(1000),//横屏日K展示的总数据条数
    K_WEEK_BIG(1000),//横屏周K展示的总数据条数
    K_MONTH_BIG(1000);//横屏月K展示的总数据条数


    private int pointNum = 0;

    ChartType(int num) {
        this.pointNum = num;
    }

    public int getPointNum() {
        return pointNum;
    }
}

package com.github.mikephil.charting.stockChart.enums;

/**
 * 画图类型
 */
public enum ChartType {
    ONE_DAY(241),
    FIVE_DAY(305),
    HK_ONE_DAY(331),
    HK_FIVE_DAY(415),
    US_ONE_DAY(390),
    US_FIVE_DAY(488),
    K_DAY_SMALL(100),
    K_WEEK_SMALL(100),
    K_MONTH_SMALL(100),
    K_DAY_BIG(1000),
    K_WEEK_BIG(1000),
    K_MONTH_BIG(1000);


    private int pointNum = 0;

    ChartType(int num) {
        this.pointNum = num;
    }

    public int getPointNum() {
        return pointNum;
    }
}

package com.github.mikephil.charting.stockChart.model.bean;


import com.github.mikephil.charting.stockChart.model.KLineDataModel;

import java.util.ArrayList;

import static java.lang.Float.NaN;

/**
 * Created by Administrator on 2018/3/14.
 */

public class SAREntity {
    public SAREntity(ArrayList<KLineDataModel> kLineBeens, int n) {
        this(kLineBeens, n, NaN);
    }

    public SAREntity(ArrayList<KLineDataModel> datas, int num, float defult) {
        // MARK: - 《SAM一线天指标》 处理算法
        /**
         处理SAM运算
         1.计算每个点往后num周期内的最高交易量，最后少于num的条数，只计算最后个数的最高交易量
         2.在主图蜡烛柱边框加颜色显示
         3.在主图收盘价记录点线
         4.在副图交易量柱边框加颜色显示
         5.在副图交易量记录点线
         - parameter num:   天数
         - parameter datas: 数据集
         */
        float max_vol_price = 0;  //最大交易量的收盘价
        float max_vol = 0;      //最大交易量
        int max_index = 0;       //最大交易量的位置
        for (int index = 0; index < datas.size(); index++) {
            //超过了num周期都没找到最大值，重新在index后num个寻找
            if (index - max_index == num) {
                max_vol_price = 0;
                max_vol = 0;
                max_index = 0;
                for (int j = index - num + 1; j < index; j++) {

                    float c = (float) datas.get(j).getClose();
                    int v = (int) datas.get(j).getVolume();

                    if (v > max_vol) {
                        max_vol_price = c;
                        max_vol = v;
                        max_index = j;
                    }
                }

                //重置最大值之后的计算数值
                for (int j = index; j < max_index; j++) {
//                        datas.get(j).extVal["\(self.key(CHSeriesKey.timeline))"] = max_vol_price;
//                        datas.get(j).extVal["\(self.key(CHSeriesKey.volume))"] = max_vol;
                }

            } else {
                //每位移一个数，计算是否最大交易量
                float c = (float) datas.get(index).getClose();
                int v = (int) datas.get(index).getVolume();

                if (v > max_vol) {
                    max_vol_price = c;
                    max_vol = v;
                    max_index = index;
                }
            }

            if (index > num - 1) {
//                    data.extVal["\(self.key(CHSeriesKey.timeline))"] = max_vol_price;
//                    data.extVal["\(self.key(CHSeriesKey.volume))"] = max_vol;

                //记录填充颜色的最大值
//                    String priceName = "\(CHSeriesKey.timeline)_BAR";
//                    String volumeName = "\(CHSeriesKey.volume)_BAR";
//                    String maxData = datas[max_index];
//                    maxData.extVal["\(self.key(priceName))"] = max_vol_price;
//                    maxData.extVal["\(self.key(volumeName))"] = max_vol;
            } else if (index == num - 1) {
                //补充开头没有画的线
                for (int j = index; j < max_index; j++) {
//                        datas[j].extVal["\(self.key(CHSeriesKey.timeline))"] = max_vol_price;
//                        datas[j].extVal["\(self.key(CHSeriesKey.volume))"] = max_vol;
                }
            }

        }

        //绘制最后一段的线
        for (int j = max_index; j < datas.size(); j++) {
//                datas[j].extVal["\(self.key(CHSeriesKey.timeline))"] = max_vol_price;
//                datas[j].extVal["\(self.key(CHSeriesKey.volume))"] = max_vol;
        }
//            return datas;
    }

}

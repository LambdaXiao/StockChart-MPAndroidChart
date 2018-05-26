package com.android.stockapp.ui.market.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.stockapp.R;
import com.android.stockapp.ui.base.BaseFragment;
import com.android.stockapp.common.data.Constant;
import com.github.mikephil.charting.myChart.data.KTimeData;
import com.github.mikephil.charting.myChart.view.OneDayView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ly on 2016/9/5.
 */
public class ChartTimeFragment extends BaseFragment {

    @BindView(R.id.chart)
    OneDayView chart;
    Unbinder unbinder;

    private KTimeData kTimeData = new KTimeData();
    private JSONObject object;
    @Override
    public int setLayoutId() {
        return R.layout.fragment_time;
    }

    @Override
    public void initBase(View view) {

        //测试数据
        try {
            object = new JSONObject(Constant.TIMEDATA);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        kTimeData.parseTimeData(object);
        chart.setDataToChart(kTimeData);
    }

    @Override
    public void onDestroy() {
        chart.eventBusUnregister();
        super.onDestroy();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
package com.android.stockapp.ui.market.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.stockapp.R;
import com.android.stockapp.common.data.Constant;
import com.android.stockapp.ui.base.BaseFragment;
import com.github.mikephil.charting.myChart.data.KLineData;
import com.github.mikephil.charting.myChart.view.KLineView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ly on 2016/11/8.
 */
public class ChartKLineFragment extends BaseFragment {


    @BindView(R.id.combinedchart)
    KLineView combinedchart;
    Unbinder unbinder;

    private KLineData kLineData;
    private JSONObject object;

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_kline;
    }

    @Override
    protected void initBase(View view) {
        kLineData = new KLineData(getActivity());

        try {
            object = new JSONObject(Constant.KLINEDATA);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        kLineData.parseKlineData(object);
        combinedchart.setDataToChart(kLineData);
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

package com.android.stockapp.ui.market.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.stockapp.R;
import com.android.stockapp.common.data.Constant;
import com.android.stockapp.ui.base.BaseFragment;
import com.android.stockapp.ui.market.activity.StockDetailLandActivity;
import com.github.mikephil.charting.stockChart.CoupleChartGestureListener;
import com.github.mikephil.charting.stockChart.data.KLineData;
import com.github.mikephil.charting.stockChart.view.KLineView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * K线
 */
public class ChartKLineFragment extends BaseFragment {


    @BindView(R.id.combinedchart)
    KLineView combinedchart;
    Unbinder unbinder;

    private int mType;//日K：1；周K：7；月K：30；年K：365
    private boolean land;//是否横屏
    private KLineData kLineData;
    private JSONObject object;

    public static ChartKLineFragment newInstance(int type,boolean land){
        ChartKLineFragment fragment = new ChartKLineFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putBoolean("landscape",land);
        fragment.setArguments(bundle);
        return fragment;

    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_kline;
    }

    @Override
    protected void initBase(View view) {
        kLineData = new KLineData(getActivity());
        combinedchart.initChart(land);
        try {
            object = new JSONObject(Constant.KLINEDATA);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        kLineData.parseKlineData(object);
        combinedchart.setDataToChart(kLineData);

        combinedchart.getGestureListenerCandle().setCoupleClick(new CoupleChartGestureListener.CoupleClick() {
            @Override
            public void singleClickListener() {
                if(land) {
                    combinedchart.doCandleChartSwitch();
                }else {
                    Intent intent = new Intent(getActivity(), StockDetailLandActivity.class);
                    getActivity().startActivity(intent);
                }
            }
        });

        combinedchart.getGestureListenerBar().setCoupleClick(new CoupleChartGestureListener.CoupleClick() {
            @Override
            public void singleClickListener() {
                if(land) {
                    combinedchart.doBarChartSwitch();
                }else {
                    Intent intent = new Intent(getActivity(), StockDetailLandActivity.class);
                    getActivity().startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getArguments().getInt("type");
        land = getArguments().getBoolean("landscape");
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

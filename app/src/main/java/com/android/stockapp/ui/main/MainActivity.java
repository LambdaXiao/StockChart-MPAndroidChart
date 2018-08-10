package com.android.stockapp.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.stockapp.R;
import com.android.stockapp.application.MyApplication;
import com.android.stockapp.ui.market.activity.StockDetailActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

//        EasyHttp.post("/mktinfo_api/get_quot")
//                .upJson("{\"version\":\"1.4.2\",\"osType\":0,\"params\":{\"assetIds\":[\"00665.HK\",\"06837.HK\",\"HSI.IDX.HK\"],\"isRealQuote\":\"1\",\"fields\":\"0|1|2|9|10|38|36|42|15|999\",\"sessionId\":\"208a061421b141c591f8fc27dc2dd9537034\"},\"id\":\"1515401807153000024android1.4.2\",\"ranNum\":\"iBestAppfinalparams\",\"sign\":\"pIf56ZnwChlX5dpp0Kf3s\\/bPJlg=\"}")
//                .execute(new SimpleCallBack<String>() {
//                    @Override
//                    public void onError(ApiException e) {
//                        Log.e("Error",e.getMessage());
//                    }
//
//                    @Override
//                    public void onSuccess(String response) {
//                        Log.e("Success",response);
//                    }
//                });

    }

    @OnClick(R.id.btn_test)
    public void onViewClicked() {
        startActivity(new Intent(MainActivity.this, StockDetailActivity.class));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        MyApplication.getApplication().initDayNight();
    }
}

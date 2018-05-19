package com.android.stockapp.ui.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.stockapp.R;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EasyHttp.post("/mktinfo_api/get_quot")
                .upJson("{\"version\":\"1.4.2\",\"osType\":0,\"params\":{\"assetIds\":[\"00665.HK\",\"06837.HK\",\"HSI.IDX.HK\"],\"isRealQuote\":\"1\",\"fields\":\"0|1|2|9|10|38|36|42|15|999\",\"sessionId\":\"208a061421b141c591f8fc27dc2dd9537034\"},\"id\":\"1515401807153000024android1.4.2\",\"ranNum\":\"iBestAppfinalparams\",\"sign\":\"pIf56ZnwChlX5dpp0Kf3s\\/bPJlg=\"}")
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        Log.e("Error",e.getMessage());
                    }

                    @Override
                    public void onSuccess(String response) {
                        Log.e("Success",response);
                    }
                });
    }
}

package com.android.stockapp.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.android.stockapp.R;
import com.android.stockapp.application.MyApplication;
import com.android.stockapp.ui.market.activity.StockDetailActivity;
import com.android.stockapp.ui.mpchartexample.notimportant.MPMainActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        MyApplication.getApplication().initDayNight();
    }

    @OnClick({R.id.btn_test, R.id.btn_mp})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_test:
                startActivity(new Intent(MainActivity.this, StockDetailActivity.class));
                break;
            case R.id.btn_mp:
                startActivity(new Intent(MainActivity.this, MPMainActivity.class));
                break;
        }
    }
}

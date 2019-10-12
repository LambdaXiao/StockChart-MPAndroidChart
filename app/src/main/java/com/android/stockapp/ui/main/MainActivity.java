package com.android.stockapp.ui.main;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

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

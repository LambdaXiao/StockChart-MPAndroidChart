package com.android.stockapp.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatDelegate;
import android.widget.Toast;

import com.android.stockapp.common.data.Constants;
import com.android.stockapp.ui.base.BaseApp;
import com.android.stockapp.ui.market.activity.StockDetailActivity;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.cache.converter.SerializableDiskConverter;


public class MyApplication extends BaseApp {
    @Override
    public void onCreate() {
        super.onCreate();
//        netWorkInit();
        initDayNight();
    }

    public static MyApplication getApplication() {
        return (MyApplication) getApp();
    }

    public void initDayNight(){
        //初始化夜间模式
        SharedPreferences sp = getSharedPreferences(Constants.SP_FILE,Context.MODE_PRIVATE);
        if(sp.getBoolean(Constants.DAY_NIGHT_MODE,false)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
    private void netWorkInit(){
        EasyHttp.init(this);

        //这里涉及到安全我把url去掉了，demo都是调试通的
        String Url = "";


        //设置请求头
//        HttpHeaders headers = new HttpHeaders();
//        headers.put("User-Agent", SystemInfoUtils.getUserAgent(this, AppConstant.APPID));
//        //设置请求参数
//        HttpParams params = new HttpParams();
//        params.put("appId", AppConstant.APPID);
        EasyHttp.getInstance()
                .debug("RxEasyHttp", true)
                .setReadTimeOut(60 * 1000)
                .setWriteTimeOut(60 * 1000)
                .setConnectTimeout(60 * 1000)
                .setRetryCount(3)//默认网络不好自动重试3次
                .setRetryDelay(500)//每次延时500ms重试
                .setRetryIncreaseDelay(500)//每次延时叠加500ms
                .setBaseUrl(Url)
                .setCacheDiskConverter(new SerializableDiskConverter())//默认缓存使用序列化转化
                .setCacheMaxSize(50 * 1024 * 1024)//设置缓存大小为50M
                .setCacheVersion(1)//缓存版本为1
//                .setHostnameVerifier(new UnSafeHostnameVerifier(Url))//全局访问规则
                .setCertificates();//信任所有证书
                //.addConverterFactory(GsonConverterFactory.create(gson))//本框架没有采用Retrofit的Gson转化，所以不用配置
//                .addCommonHeaders(headers)//设置全局公共头
//                .addCommonParams(params);//设置全局公共参数
//                .addInterceptor(new CustomSignInterceptor());//添加参数签名拦截器
        //.addInterceptor(new HeTInterceptor());//处理自己业务的拦截器
    }
}

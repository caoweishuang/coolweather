package com.coolweather.app.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolweather.app.R;
import com.coolweather.app.util.HttpCallBackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

/**
 * Created by root on 9/2/15.
 */
public class WeatherActivity extends Activity {

    private LinearLayout linearLayout;
    private TextView tvcity_name;
    private TextView tvpublish;
    private TextView tvweather;
    private TextView tvdate;
    private TextView tvtemp1;
    private TextView tvtemp2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);
        getWidget();

        String countyCode = getIntent().getStringExtra("county_code");
        if(!TextUtils.isEmpty(countyCode)){
            tvpublish.setText("正在同步中......");
            linearLayout.setVisibility(View.VISIBLE);
            tvcity_name.setVisibility(View.VISIBLE);
            queryWeatherCode(countyCode);
        }else{
            showWeather();
        }
    }

    private void getWidget() {
        linearLayout = (LinearLayout)findViewById(R.id.LlWeather_info);
        tvcity_name = (TextView)findViewById(R.id.TvCity_name);
        tvpublish = (TextView)findViewById(R.id.TvPublish);
        tvdate = (TextView)findViewById(R.id.TvDate);
        tvweather = (TextView)findViewById(R.id.TvWeather);
        tvtemp1 = (TextView)findViewById(R.id.TvTemp1);
        tvtemp2 = (TextView)findViewById(R.id.TvTemp2);
    }

    private void queryWeatherCode(String countyCode){
        String address = "http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
        queryFromServer(address,"countyCode");
    }

    private void queryWeatherInfo(String weatherCode){
        String address = "http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
        queryFromServer(address,"weatherCode");
    }

    private void queryFromServer(final String address,final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void finish(String response) {
                if("countyCode".equals(type)){
                    if(!TextUtils.isEmpty(response)){
                        String[] array = response.split("\\|");
                        if(array != null && array.length == 2){
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                }else{
                    if("weatherCode".equals(type)){
                        Utility.handleWeatherResponse(WeatherActivity.this,response);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showWeather();
                            }
                        });
                    }
                }

            }

            @Override
            public void error(Exception e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvpublish.setText("同步失败");
                    }
                });
            }
        });


    }

    private void showWeather() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        tvcity_name.setText(sharedPreferences.getString("city_name",""));
        tvpublish.setText("今天"+sharedPreferences.getString("publish_time","")+"发布");
        tvdate.setText(sharedPreferences.getString("current_date",""));
        tvweather.setText(sharedPreferences.getString("weather_desp",""));
        tvtemp1.setText(sharedPreferences.getString("temp1",""));
        tvtemp2.setText(sharedPreferences.getString("temp2",""));
        linearLayout.setVisibility(View.VISIBLE);
        tvcity_name.setVisibility(View.VISIBLE);

    }
}

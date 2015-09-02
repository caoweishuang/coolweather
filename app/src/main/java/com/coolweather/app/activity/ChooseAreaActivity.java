package com.coolweather.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.app.R;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallBackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 9/1/15.
 */
public class ChooseAreaActivity extends Activity {

    private static final int PROVINCE_LEVEL = 0;
    private static final int CITY_LEVEL = 1;
    private static final int COUNTY_LEVEL = 2;

    private ProgressDialog progressDialog;
    private TextView tvTitle;
    private ListView lvList;
    private CoolWeatherDB coolWeatherDB;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<String>();

    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    private Province selectedProvince;
    private City selectedCity;

    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_area);
        lvList = (ListView)findViewById(R.id.Lvlist);
        tvTitle = (TextView)findViewById(R.id.Tv_Title);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        lvList.setAdapter(adapter);
        coolWeatherDB = CoolWeatherDB.getInstance(this);
        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(currentLevel == PROVINCE_LEVEL){
                    selectedProvince = provinceList.get(position);
                    Log.i("TAG",selectedProvince.getProvinceName());
                    queryCities();
                }else if(currentLevel == CITY_LEVEL){
                    selectedCity = cityList.get(position);
                    queryCounties();
                }
            }
        });
        queryProvinces();

    }

    private void queryProvinces() {
        provinceList = coolWeatherDB.loadProvinces();

        if(provinceList.size()>0){
            dataList.clear();
            for(Province province: provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            lvList.setSelection(0);
            tvTitle.setText("中国");
            currentLevel = PROVINCE_LEVEL;
            Log.i("TAG","SUCCESS");
        }else{
            queryFromServer(null,"province");
        }
    }

    private void queryCities() {
        cityList = coolWeatherDB.loadCity(selectedProvince.getId());
        if(cityList.size() > 0){
            dataList.clear();
            for(City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            lvList.setSelection(0);
            tvTitle.setText(selectedProvince.getProvinceName());
            currentLevel = CITY_LEVEL;
        }else{
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }
    }

    private void queryCounties() {

        countyList = coolWeatherDB.loadCounty(selectedCity.getId());
        if(countyList.size() > 0){
            dataList.clear();
            for(County county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            lvList.setSelection(0);
            tvTitle.setText(selectedCity.getCityName());
            currentLevel = COUNTY_LEVEL;
        }else{
            queryFromServer(selectedCity.getCityCode(),"county");
        }
    }

    private void queryFromServer(final String code, final String type) {
        String address;
        if(!TextUtils.isEmpty(code)){
            address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
        }else{
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void finish(String response) {
                boolean result = false;
                if("province".equals(type)){
                    result = Utility.handleProvinceResponse(response,coolWeatherDB);
                }else if("city".equals(type)){
                    result = Utility.handleCityResponse(response,selectedProvince.getId(),coolWeatherDB);
                }else if("county".equals(type)){
                    result = Utility.handleCountyResponse(response,selectedCity.getId(),coolWeatherDB);
                }
                if(result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void error(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showProgressDialog() {

        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载....");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void closeProgressDialog() {
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

    public void onBackPressed(){
        if(currentLevel == COUNTY_LEVEL){
            queryCities();
        }else if(currentLevel == CITY_LEVEL){
            queryProvinces();
        }else if(currentLevel == PROVINCE_LEVEL){
            finish();
        }
    }
}




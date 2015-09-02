package com.coolweather.app.util;

import android.text.TextUtils;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

/**
 * Created by root on 9/1/15.
 */
public class Utility {

    //解析处理从网络上获得的省级数据
    public synchronized static boolean handleProvinceResponse(String response,CoolWeatherDB coolWeatherDB){
        if(!TextUtils.isEmpty(response)){
            String[] allProvinces = response.split(",");
            if(allProvinces != null && allProvinces.length > 0){
                for(String p: allProvinces){
                    String[] array = p.split("\\|");
                    Province province  =new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    //解析处理网络上返回的城市信息
    public static boolean handleCityResponse(String response,int provinceId,CoolWeatherDB coolWeatherDB){

        if(!TextUtils.isEmpty(response)){
            String[] allCities = response.split(",");
            if(allCities.length > 0 && allCities != null){
                for(String c: allCities){

                    City city = new City();
                    String[] array = c.split("\\|");
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    //解析处理网络返回的乡镇信息
    public static boolean handleCountyResponse(String response,int cityId,CoolWeatherDB coolWeatherDB){
        if(!TextUtils.isEmpty(response)){
            String[] allCounties = response.split(",");
            if(allCounties != null && allCounties.length > 0){
                for(String c:allCounties){
                    County county = new County();
                    String[] array = c.split("\\|");
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }
}


package com.coolweather.app.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by root on 9/1/15.
 */
public class HttpUtil {

    //从网络上获取地址信息
    public static void sendHttpRequest(final String address,final HttpCallBackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(address);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(8000);
                    httpURLConnection.setReadTimeout(8000);
                    InputStream is = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder response = new StringBuilder();
                    String read = null;
                    while((read = bufferedReader.readLine())!=null){
                        response.append(read);
                        Log.i("REQUEST","SUCCESS");
                    }
                    if(listener != null){
                        listener.finish(response.toString());
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                    if(listener != null){
                        listener.error(e);
                    }
                }
            }
        }).start();
    }
}

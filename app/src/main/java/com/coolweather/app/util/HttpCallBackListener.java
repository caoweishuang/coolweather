package com.coolweather.app.util;

/**
 * Created by root on 9/1/15.
 */
public interface HttpCallBackListener {
    public void finish(String response);
    public void error(Exception e);
}

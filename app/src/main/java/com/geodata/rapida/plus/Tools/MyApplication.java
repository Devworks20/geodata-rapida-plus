package com.geodata.rapida.plus.Tools;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

@SuppressLint("Registered")
public class MyApplication extends Application
{
    public static MyApplication instance;

    @Override
    public void onCreate()
    {
        super.onCreate();
        instance = this;
    }

    @Override
    public Context getApplicationContext()
    {
        return super.getApplicationContext();
    }

    public static MyApplication getInstance()
    {
        return instance;
    }
}
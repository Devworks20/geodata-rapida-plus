package com.geodata.rapida.plus.Retrofit.Client;

import com.geodata.rapida.plus.Tools.APIUrls;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient
{
    public static int MY_SOCKET_TIMEOUT_MINUTE = 1; //VOLLEY TIME SOCKET - 1 MINUTE

    public static Retrofit getClient()
    {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(MY_SOCKET_TIMEOUT_MINUTE, TimeUnit.MINUTES)
                .writeTimeout(MY_SOCKET_TIMEOUT_MINUTE, TimeUnit.MINUTES)
                .readTimeout(MY_SOCKET_TIMEOUT_MINUTE, TimeUnit.MINUTES)
                .addInterceptor(interceptor).build();

        return new Retrofit.Builder()
                .baseUrl(APIUrls.TEST_PUBLIC_SERVER_SRMS)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    public static Retrofit getClient2()
    {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(MY_SOCKET_TIMEOUT_MINUTE, TimeUnit.MINUTES)
                .writeTimeout(MY_SOCKET_TIMEOUT_MINUTE, TimeUnit.MINUTES)
                .readTimeout(MY_SOCKET_TIMEOUT_MINUTE, TimeUnit.MINUTES)
                .addInterceptor(interceptor).build();

        return new Retrofit.Builder()
                .baseUrl(APIUrls.TEST_PUBLIC_SERVER_BMS)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }


}

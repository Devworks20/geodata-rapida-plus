package com.geodata.rapida.plus.Tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VolleyCatch
{
    private static final String TAG = VolleyCatch.class.getSimpleName();

    String Logs = "";

    public void parseVolleyError(String sAPI, VolleyError error, Context context)
    {
        try
        {
            NetworkResponse response = error.networkResponse;

            if(error.networkResponse.data != null)
            {
                try
                {
                    String tempLogs = new String(error.networkResponse.data,"UTF-8");

                    if (tempLogs.equals(""))
                    {
                        initMessageLogs(error, context);
                    }
                    else
                    {
                        Logs = tempLogs;
                    }
                }
                catch (UnsupportedEncodingException e)
                {
                    Logs = e.toString();
                }
            }
            else
            {
                initMessageLogs(error, context);
            }

            Logs = sAPI + ": " + Logs;

            Log.e(TAG, Logs);

            if (response.statusCode != 404)
            {
                writeToFile(Logs);
            }
        }
        catch (Exception e)
        {
            Logs = sAPI + ": An unknown error occurred.";

            Log.e(TAG,Logs);

            writeToFile(Logs);
        }
    }

    private void initMessageLogs(VolleyError error, Context context)
    {
        String statusCode =  String.valueOf(error.networkResponse.statusCode);

        if(error instanceof NoConnectionError)
        {
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = null;

            if (cm != null)
            {
                activeNetwork = cm.getActiveNetworkInfo();
            }

            if(activeNetwork != null && activeNetwork.isConnectedOrConnecting())
            {
                Logs = "Server is not connected to internet. " + statusCode;
            }
            else
            {
                Logs  = "Your device is not connected to internet. " + statusCode;
            }
        }
        else if (error instanceof NetworkError || error.getCause() instanceof ConnectException)
        {
            Logs  = "Your device is not connected to internet. " + statusCode;
        }
        else if (error.getCause() instanceof MalformedURLException)
        {
            Logs  = "Bad Request. " + statusCode;
        }
        else if (error instanceof ParseError || error.getCause() instanceof IllegalStateException
                || error.getCause() instanceof JSONException
                || error.getCause() instanceof XmlPullParserException)
        {
            Logs  = "Parse Error (because of invalid json or xml). " + statusCode;
        }
        else if (error.getCause() instanceof OutOfMemoryError)
        {
            Logs  = "Out Of Memory Error. " + statusCode;
        }
        else if (error instanceof AuthFailureError)
        {
            Logs  = "Server couldn't find the authenticated request. " + statusCode;
        }
        else if (error instanceof ServerError || error.getCause() instanceof ServerError)
        {
            Logs  = "Server is not responding. " + statusCode;
        }
        else if (error instanceof TimeoutError || error.getCause() instanceof SocketTimeoutException
                || error.getCause() instanceof SocketException)
        {
            Logs  = "Connection timeout error. " + statusCode;
        }
        else
        {
            Logs  = "An unknown error occurred. " + statusCode;
        }
    }

    public void writeToFile(String dataString)
    {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date(System.currentTimeMillis());
        String dateTime = dateFormat.format(now);

        String path = Environment.getExternalStorageDirectory() + File.separator  + "/SRI/";
        File folder = new File(path);

        /*if (!folder.exists())
        {
            folder.mkdirs();
        }

        File file = new File(folder, "logs.txt");

        try
        {
            if (!file.exists())
            {
                file.createNewFile();
            }

            FileOutputStream fOut = new FileOutputStream(file, true);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(dateTime + " " + dataString + ";");
            myOutWriter.close();
            fOut.flush();
            fOut.close();
        }
        catch (IOException e)
        {
            Log.i(TAG, "File write failed: " + e.toString());
        }*/
    }

}

package com.geodata.rapida.plus.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.Tools.ClearApplicationCache;

import java.io.File;

public class AttachmentWebOnlineViewActivity extends AppCompatActivity
{
    private static final String TAG = AttachmentWebOnlineViewActivity.class.getSimpleName();

    WebView wb_attachment;
    String URL, Type;

    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachment_web_online_view);

        initViews();
    }

    private void initViews()
    {
        Bundle extras = getIntent().getExtras();

        if(extras != null)
        {
            URL  = extras.getString("AttachmentPath");
            Type = extras.getString("Type");
        }

        pDialog = new ProgressDialog(AttachmentWebOnlineViewActivity.this);
        pDialog.setTitle(Type);
        pDialog.setMessage("Loading...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        wb_attachment = findViewById(R.id.wb_attachment);
        wb_attachment.setWebViewClient(new MyBrowser());

        iniListeners();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void iniListeners()
    {
        wb_attachment.getSettings().setJavaScriptEnabled(true);
        wb_attachment.getSettings().setLoadWithOverviewMode(true);
        wb_attachment.getSettings().setBuiltInZoomControls(true);
        wb_attachment.getSettings().setUseWideViewPort(true);
        wb_attachment.getSettings().setLoadsImagesAutomatically(true);
        wb_attachment.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        wb_attachment.loadUrl(URL);
    }

    private class MyBrowser extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            view.loadUrl(url);

            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            pDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed()
    {
        try
        {
            wb_attachment.clearCache(true);
            wb_attachment.clearFormData();
            wb_attachment.clearHistory();
            wb_attachment.clearSslPreferences();

            // Clear all the cookies
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();

            WebStorage.getInstance().deleteAllData();

            File fileCache = getApplicationContext().getCacheDir();
            ClearApplicationCache.clearCache(fileCache);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        if (pDialog.isShowing())
        {
            pDialog.dismiss();
        }

        Intent dataReturn = new Intent();
        dataReturn.putExtra("Attachment", "Success");
        setResult(RESULT_OK, dataReturn);
        finish();
    }
}
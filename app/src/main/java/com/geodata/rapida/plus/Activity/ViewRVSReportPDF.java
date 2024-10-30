package com.geodata.rapida.plus.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.Retrofit.Client.APIClient;
import com.geodata.rapida.plus.Retrofit.Interface.APIClientInterface;
import com.geodata.rapida.plus.Tools.UserAccount;
import com.geodata.rapida.plus.Tools.VolleyCatch;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewRVSReportPDF extends AppCompatActivity
{
    private static final String TAG = ViewRVSReportPDF.class.getSimpleName();

    PDFView pdfView;

    ImageView iv_back;

    String  FileName, BuildingName;

    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rvs_report_pdf);

        initViews();
    }

    private void initViews()
    {
        Bundle extras = getIntent().getExtras();

        if(extras != null)
        {
            FileName     = extras.getString("FileName");
            BuildingName = extras.getString("BuildingName");
        }

        iv_back  = findViewById(R.id.iv_back);
        pdfView  = findViewById(R.id.pdfView);

        initListeners();
    }

    private void initListeners()
    {
        iv_back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        initViewPDF();
    }

    private void initViewPDF()
    {
        try
        {
            if (FileName != null)
            {
                File folderPath = new File(Environment.getExternalStorageDirectory(), "/SRI/RVS");

                Log.e(TAG, "FileName: " + FileName);

                file = new File(folderPath, FileName);

                if (file.exists())
                {
                    Log.e(TAG, "FILE EXIST");

                    pdfView.setBackgroundColor(Color.LTGRAY);

                    pdfView.fromFile(file)
                            //.pages(0, 1, 2, 3, 4) // all pages are displayed by default
                            .enableSwipe(true) // allows to block changing pages using swipe
                            .swipeHorizontal(false)
                            .enableDoubletap(true)
                            .defaultPage(0)
                            .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                            .password(null)
                            .scrollHandle(new DefaultScrollHandle(this))
                            .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                            .spacing(10) // spacing between pages in dp. To define spacing color, set view background
                            .load();
                }
                else
                {
                    finish();

                    Toast.makeText(getApplicationContext(), "File attachment does not exist.", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                finish();

                Toast.makeText(getApplicationContext(), "File attachment does not exist.", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

}
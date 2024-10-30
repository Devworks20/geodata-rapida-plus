package com.geodata.rapida.plus.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.Retrofit.Client.APIClient;
import com.geodata.rapida.plus.Retrofit.Interface.APIClientInterface;
import com.geodata.rapida.plus.Retrofit.Model.ReportPDFClass;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryOnlineMissionOrders;
import com.geodata.rapida.plus.Tools.UserAccount;
import com.geodata.rapida.plus.Tools.VolleyCatch;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PreviewReportDESAActivity extends AppCompatActivity
{
    private static final String TAG = PreviewReportRVSScoringActivity.class.getSimpleName();

    PDFView pdfView;

    ImageView iv_back;

    String SeismicityRegion, FILE, MissionOrderID, sDateNow;

    Button btn_submit;

    LinearLayout ll_loading;

    APIClientInterface apiInterface;
    VolleyCatch volleyCatch = new VolleyCatch();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_report_desa);

        initViews();
    }

    private void initViews()
    {
        try
        {
            apiInterface = APIClient.getClient().create(APIClientInterface.class);

            Bundle extras = getIntent().getExtras();

            if(extras != null)
            {
                SeismicityRegion = extras.getString("SeismicityRegion");
                FILE             = extras.getString("FILE");
                MissionOrderID   = extras.getString("MissionOrderID");
            }

            new initUpdateDate().execute();

            ll_loading  = findViewById(R.id.ll_loading);

            pdfView  = findViewById(R.id.pdfView);
            iv_back  = findViewById(R.id.iv_back);
            btn_submit = findViewById(R.id.btn_submit);

            initListeners();

            initViewPDF();

            Log.e(TAG, "initPostDESAReport  CALLED - " + MissionOrderID);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
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

        btn_submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (haveNetworkConnection(getApplicationContext()))
                {
                    if (!checkPDFReportOnline())
                    {
                        File file = new File(FILE);

                        if (file.exists())
                        {
                            initButtonSendPDF();
                        }
                        else
                        {
                            Toast.makeText(PreviewReportDESAActivity.this, "There is something wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(PreviewReportDESAActivity.this, "You already sent the PDF report.", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(PreviewReportDESAActivity.this, "You have no internet connection. try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initViewPDF()
    {
        try
        {
            File file = new File(FILE);

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
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initButtonSendPDF()
    {
        try
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(PreviewReportDESAActivity.this);
            builder.setTitle("Submit Report");
            builder.setMessage("Are you sure you want to submit this DESA Report?");
            builder.setCancelable(false);
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    dialogInterface.cancel();
                }
            });
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    ll_loading.setVisibility(View.VISIBLE);

                    initPostDESAReport();
                }
            });
            builder.show();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initPostDESAReport()
    {
        try
        {
            Log.e(TAG, "initPostDESAReport CALLED - " + MissionOrderID);
            File file = new File(FILE);

            if (file.exists())
            {
                RequestBody PDFContent = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part PDFReport = MultipartBody.Part.createFormData("pdfFile", file.getName(), PDFContent);
                RequestBody sMissionOrderID = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(MissionOrderID));

                Call<String> callToken = apiInterface.PostRESAorDESAReport(PDFReport, sMissionOrderID);

                callToken.enqueue(new Callback<String>()
                {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                    {
                        if (response.isSuccessful())
                        {
                            if (response.body() != null)
                            {
                                Log.e(TAG, "RESPONSE: " + response.toString());
                                initUpdateMissionOrderStatus();
                            }
                            else
                            {
                                String Logs = "POST PDF: Server Response Null";
                                Log.e(TAG, Logs);
                                volleyCatch.writeToFile(Logs);
                                ll_loading.setVisibility(View.GONE);
                                Toast.makeText(PreviewReportDESAActivity.this, "Failed to send PDF.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            String Logs = convertingResponseError(response.errorBody());
                            String rLogs = "Post PDF Failed: " + Logs;
                            Log.e(TAG, rLogs);
                            volleyCatch.writeToFile(rLogs);
                            ll_loading.setVisibility(View.GONE);
                            Toast.makeText(PreviewReportDESAActivity.this, "Failed to send PDF.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                    {
                        String Logs = "Post PDF Failure: " + t.getMessage();
                        volleyCatch.writeToFile(Logs);
                        Log.e(TAG, Logs);
                        ll_loading.setVisibility(View.GONE);
                        Toast.makeText(PreviewReportDESAActivity.this, "Failed to send PDF.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
            {
                ll_loading.setVisibility(View.GONE);
                Toast.makeText(PreviewReportDESAActivity.this, "PDF File report not exist.", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
            ll_loading.setVisibility(View.GONE);
            Toast.makeText(PreviewReportDESAActivity.this, "Failed to send PDF.", Toast.LENGTH_SHORT).show();
        }
    }


    private void initUpdateMissionOrderStatus()
    {
        try
        {
            RepositoryOnlineMissionOrders.updateTheMissionOrderStatus(getApplicationContext(),
                    UserAccount.UserAccountID, MissionOrderID, sDateNow,"Complete");

            Log.e(TAG,  "Date Now:  " + sDateNow);

            ll_loading.setVisibility(View.GONE);

            Intent intent = new Intent(PreviewReportDESAActivity.this, NavigationActivity.class);
            startActivity(intent);
            finish();

            Toast.makeText(PreviewReportDESAActivity.this, "Successfully Submitted!", Toast.LENGTH_LONG).show();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }



    private Boolean checkPDFReportOnline()
    {
        Cursor cursor = RepositoryOnlineMissionOrders.realAllData(getApplicationContext(),String.valueOf(UserAccount.employeeID), MissionOrderID);

        if (cursor.getCount()!=0)
        {
            if (cursor.moveToFirst())
            {
                String InspectionStatus = cursor.getString(cursor.getColumnIndex("InspectionStatus"));

                return InspectionStatus.equals("Complete");
            }
        }
        return false;
    }

    private String convertingResponseError(ResponseBody responseBody)
    {
        StringBuilder sb = new StringBuilder();

        try
        {
            if (responseBody != null)
            {
                BufferedReader reader;

                reader = new BufferedReader(new InputStreamReader(responseBody.byteStream()));

                String line;
                try
                {
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                    }
                }
                catch (IOException e)
                {
                    Log.e(TAG, e.toString());
                }
            }
            else
            {
                sb.append("");
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());

            sb.append("");
        }
        return sb.toString();
    }

    //Network Validation
    private boolean haveNetworkConnection(Context context)
    {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null)
        {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
            {
                haveConnectedWifi = true;
            }
            else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                haveConnectedMobile = true;
            }
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    @SuppressLint("StaticFieldLeak")
    public class initUpdateDate extends AsyncTask<String, Void, String>
    {
        @Override
        protected void onPreExecute()
        {
            Date now = new Date(System.currentTimeMillis());
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");

            sDateNow = dateFormat.format(now);

            Log.e(TAG,"Executing...");
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                NTPUDPClient timeClient = new NTPUDPClient();
                InetAddress inetAddress = InetAddress.getByName("time-a.nist.gov");
                TimeInfo timeInfo = timeClient.getTime(inetAddress);

                //long returnTime = timeInfo.getReturnTime(); //local device time
                long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime(); //server time

                Date time = new Date(returnTime);

                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
                sDateNow = dateFormat.format(time);
            }
            catch (Exception e)
            {
                Log.e(TAG,e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
        }
    }

}
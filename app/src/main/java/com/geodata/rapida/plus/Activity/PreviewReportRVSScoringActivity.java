package com.geodata.rapida.plus.Activity;

import androidx.annotation.NonNull;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.Retrofit.Client.APIClient;
import com.geodata.rapida.plus.Retrofit.Interface.APIClientInterface;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryFinalBuildingScores;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryOnlineMissionOrders;
import com.geodata.rapida.plus.Tools.SendPDFForm;
import com.geodata.rapida.plus.Tools.UserAccount;
import com.geodata.rapida.plus.Tools.VolleyCatch;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
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

public class PreviewReportRVSScoringActivity extends AppCompatActivity
{
    private static final String TAG = PreviewReportRVSScoringActivity.class.getSimpleName();

    PDFView pdfView;

    ImageView iv_back;

    String SeismicityRegion, FILE, MissionOrderID, sDateNow;

    Button btn_send, btn_submit;

    LinearLayout ll_loading;

    APIClientInterface apiInterface;
    VolleyCatch volleyCatch = new VolleyCatch();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_report_rvs_scoring);

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
                       initButtonSendPDF();
                    }
                    else
                    {
                        Toast.makeText(PreviewReportRVSScoringActivity.this, "You already sent the PDF report.", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(PreviewReportRVSScoringActivity.this, "You have no internet connection. try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initViewPDF()
    {
        try
        {
           // File file = new File("/storage/emulated/0/SRI/MissionOrder/Attachment/Deleted Data in Existing.docx");
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
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PreviewReportRVSScoringActivity.this);
            builder.setTitle("Submit Report");
            builder.setMessage("Are you sure you want to submit this RVS Report?");
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
                    File file = new File(FILE);

                    if (file.exists())
                    {
                        Log.e(TAG, "File Name: " + file.getName());

                        ll_loading.setVisibility(View.VISIBLE);

                        try
                        {
                            RequestBody PDFContent = RequestBody.create(MediaType.parse("multipart/form-data"), Objects.requireNonNull(file));
                            MultipartBody.Part PDFReport = MultipartBody.Part.createFormData("pdfFile", file.getName(), PDFContent);

                            RequestBody sMissionOrderID = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(MissionOrderID));

                            Call<String> callToken = apiInterface.POSTSeismicInspectionReport(PDFReport, sMissionOrderID);

                            callToken.enqueue(new Callback<String>()
                            {
                                @Override
                                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                                {
                                    try
                                    {
                                        if (response.isSuccessful() && response.body() != null) {
                                            Log.e(TAG, "RESPONSE: " + response.toString());

                                            initFinalScore();

                                            initUpdateMissionOrderStatus();
                                        }
                                        else
                                        {
                                            String errorLogs = (response.errorBody() != null) ? convertingResponseError(response.errorBody()) : "Unknown Error";
                                            String Logs = (response.isSuccessful()) ? "Post PDF: Server Response Null" : "Post PDF Failed: " + errorLogs;

                                            Log.e(TAG, Logs);
                                            volleyCatch.writeToFile(Logs);

                                            try
                                            {
                                                if (!response.isSuccessful())
                                                {
                                                    String[] out = errorLogs.split(":");
                                                    String result = out[1].replaceAll("[\\[\\](){}]", "");
                                                }
                                            }
                                            catch (Exception e)
                                            {
                                                Log.e(TAG, e.toString());
                                            }

                                            ll_loading.setVisibility(View.GONE);
                                            Toast.makeText(PreviewReportRVSScoringActivity.this, "Failed to send PDF.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    catch (Exception e)
                                    {
                                        Log.e(TAG, e.toString());

                                        ll_loading.setVisibility(View.GONE);
                                        Toast.makeText(PreviewReportRVSScoringActivity.this, "Failed to send PDF.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                                {
                                    String Logs = "Post PDF Failure: " + t.getMessage();

                                    Log.e(TAG, Logs);
                                    volleyCatch.writeToFile(Logs);

                                    ll_loading.setVisibility(View.GONE);
                                    Toast.makeText(PreviewReportRVSScoringActivity.this, "Failed to send PDF.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        catch (Exception e)
                        {
                            Log.e(TAG, e.toString());

                            ll_loading.setVisibility(View.GONE);
                            Toast.makeText(PreviewReportRVSScoringActivity.this, "Failed to send PDF.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            builder.show();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }




    private void initFinalScore()
    {
        try
        {
            switch (SeismicityRegion.toLowerCase())
            {
                case "high":
                    SeismicityRegion = "High Seismicity";
                    break;
                case "low":
                    SeismicityRegion = "Low Seismicity";
                    break;
                case "moderate":
                    SeismicityRegion = "Moderate Seismicity";
                    break;
            }

            Cursor cursor = RepositoryFinalBuildingScores.realAllData(getApplicationContext(),
                    UserAccount.UserAccountID, MissionOrderID, SeismicityRegion);

            if (cursor.getCount()!=0)
            {
                if (cursor.moveToFirst())
                {
                    do
                    {
                        String FinalScore = cursor.getString(cursor.getColumnIndex("FinalScore"));

                        Log.e(TAG, "FINAL SCORE: " + FinalScore);

                        initPostFinalScore(FinalScore);
                    }
                    while (cursor.moveToNext());
                }
            }
            else
            {
                Log.e(TAG, "FINAL SCORE: EMPTY" );
            }

        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initPostFinalScore(String FinalScore)
    {
        try
        {
            Call<String> postFinalScore = apiInterface.POSTSeismicFinalScore(Integer.parseInt(MissionOrderID), Double.parseDouble(FinalScore));

            postFinalScore.enqueue(new Callback<String>()
            {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                {
                    if (response.isSuccessful() && response.body() != null)
                    {
                        Log.e(TAG, "Response Final Score: " + response.body());
                    }
                    else
                    {
                        String errorLogs = (response.errorBody() != null) ? convertingResponseError(response.errorBody()) : "Unknown Error";

                        String Logs = (response.isSuccessful()) ? "Post Final Score: Server Response Null" :
                                                                  "Post Final Score Failed: " + errorLogs;

                        Log.e(TAG, Logs);
                        volleyCatch.writeToFile(Logs);

                        try
                        {
                            if (!response.isSuccessful())
                            {
                                String[] out = errorLogs.split(":");
                                String result = out[1].replaceAll("[\\[\\](){}]", "");
                            }
                        }
                        catch (Exception e)
                        {
                            Log.e(TAG, e.toString());
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                {
                    String Logs = "Post Final Score Failure: " + t.getMessage();

                    Log.e(TAG, Logs);
                    volleyCatch.writeToFile(Logs);
                }
            });
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initUpdateMissionOrderStatus()
    {
        try
        {
            RepositoryOnlineMissionOrders.updateTheMissionOrderStatus(getApplicationContext(),
                    UserAccount.UserAccountID, MissionOrderID, sDateNow,"Complete");

            ll_loading.setVisibility(View.GONE);

            Intent intent = new Intent(PreviewReportRVSScoringActivity.this, NavigationActivity.class);
            startActivity(intent);
            finish();

            Toast.makeText(PreviewReportRVSScoringActivity.this, "Successfully Submitted!", Toast.LENGTH_LONG).show();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
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

    private Boolean checkPDFReportOnline()
    {
        Cursor cursor = RepositoryOnlineMissionOrders.realAllData(getApplicationContext(), String.valueOf(UserAccount.employeeID), MissionOrderID);

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

                Log.e(TAG,  "Date Now:  " + sDateNow);
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
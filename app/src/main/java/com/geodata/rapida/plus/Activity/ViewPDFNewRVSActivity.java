package com.geodata.rapida.plus.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.Retrofit.Client.APIClient;
import com.geodata.rapida.plus.Retrofit.Interface.APIClientInterface;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryNewRVSBuildings;
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

public class ViewPDFNewRVSActivity extends AppCompatActivity
{
    private static final String TAG = ViewPDFNewRVSActivity.class.getSimpleName();

    PDFView pdfView;

    LinearLayout ll_loading;

    ImageView iv_back;

    TextView tv_building_report;

    String  FileName, BuildingName, AssetID, AssetInfoBuildingID,
            FinalScoreWF, FinalScoreSF, FinalScoreCF, ScreenerID;

    File file;

    VolleyCatch volleyCatch = new VolleyCatch();
    APIClientInterface apiInterface;

    Button btn_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf_new_rvs_activity);

        initViews();
    }

    private void initViews()
    {
        apiInterface = APIClient.getClient().create(APIClientInterface.class);

        Bundle extras = getIntent().getExtras();

        if(extras != null)
        {
            FileName     = extras.getString("FileName");
            BuildingName = extras.getString("BuildingName");
            AssetID      = extras.getString("AssetID");
            AssetInfoBuildingID  = extras.getString("AssetInfoBuildingID");
            FinalScoreWF   = extras.getString("FinalScoreWF");
            FinalScoreSF   = extras.getString("FinalScoreSF");
            FinalScoreCF   = extras.getString("FinalScoreCF");
        }

        ScreenerID = String.valueOf(UserAccount.employeeID);

        ll_loading         = findViewById(R.id.ll_loading);
        iv_back            = findViewById(R.id.iv_back);
        tv_building_report = findViewById(R.id.tv_building_report);
        //tv_building_report.setText(BuildingName);

        pdfView     = findViewById(R.id.pdfView);
        btn_submit = findViewById(R.id.btn_submit);

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

        btn_submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (haveNetworkConnection(getApplicationContext()))
                {
                    initButtonSendPDF();
                }
                else
                {
                    Toast.makeText(ViewPDFNewRVSActivity.this, "You have no internet connection. try again later.", Toast.LENGTH_SHORT).show();
                }
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
                File folderPath = new File(Environment.getExternalStorageDirectory(), "SRI/RVS/List Building");

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


    private void initButtonSendPDF()
    {
        try
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(ViewPDFNewRVSActivity.this);
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
                    if (file.exists())
                    {
                        Log.e(TAG, "File Name: " + file.getName());
                        ll_loading.setVisibility(View.VISIBLE);

                        try
                        {
                            RequestBody PDFContent = RequestBody.create(MediaType.parse("multipart/form-data"), Objects.requireNonNull(file));
                            MultipartBody.Part PDFReport = MultipartBody.Part.createFormData("pdfFile", file.getName(), PDFContent);
                            RequestBody sAssetID = RequestBody.create(MediaType.parse("text/plain"), AssetID);
                            RequestBody sScreenerID = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(ScreenerID));

                            Log.e(TAG, "AssetID: " + AssetID + " ScreenerID: " + ScreenerID);

                            Call<Integer> callToken = apiInterface.POSTEarthquakeInspectionReport(PDFReport, sAssetID, sScreenerID);

                            callToken.enqueue(new Callback<Integer>()
                            {
                                @Override
                                public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response)
                                {
                                    if (response.isSuccessful())
                                    {
                                        if (response.body() != null)
                                        {
                                            Log.e(TAG, "RESPONSE: " + response.body());
                                            initPostFinalScore(response.body());
                                        }
                                        else
                                        {
                                            String Logs = "POST PDF: Server Response Null";
                                            Log.e(TAG, Logs);
                                            volleyCatch.writeToFile(Logs);
                                            ll_loading.setVisibility(View.GONE);
                                            Toast.makeText(getApplicationContext(), "Failed to send the PDF report.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else
                                    {
                                        String Logs = convertingResponseError(response.errorBody());
                                        String rLogs = "Post PDF Failed: " + Logs;
                                        Log.e(TAG, rLogs);
                                        volleyCatch.writeToFile(rLogs);
                                        Toast.makeText(getApplicationContext(), "Failed to send the PDF report.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t)
                                {
                                    String Logs = "Post PDF Failure: " + t.getMessage();
                                    Log.e(TAG, Logs);
                                    volleyCatch.writeToFile(Logs);
                                    ll_loading.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "Failed to send the PDF report.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        catch (Exception e)
                        {
                            Log.e(TAG, e.toString());
                            ll_loading.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Failed to send the PDF report.", Toast.LENGTH_SHORT).show();
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

    private void initPostFinalScore(Integer EarthquakeRVSReportID)
    {
        try
        {
            Log.e(TAG, "initPostFinalScore CALLED");

            Call<String> postFinalScore = apiInterface.POSTEarthquakeFinalScore(FinalScoreWF, FinalScoreSF, FinalScoreCF, EarthquakeRVSReportID);

            postFinalScore.enqueue(new Callback<String>()
            {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                {
                    if (response.isSuccessful())
                    {
                        if (response.body() != null)
                        {
                            Log.e(TAG, "Response Final Score: " + response.body());
                            ll_loading.setVisibility(View.GONE);
                            initUpdateRVSStatus();
                        }
                        else
                        {
                            String Logs = "Post Final Score: Server Response Null";
                            Log.e(TAG, Logs);
                            volleyCatch.writeToFile(Logs);
                            ll_loading.setVisibility(View.GONE);
                            Toast.makeText(ViewPDFNewRVSActivity.this, "Sending final score failed.", Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        String Logs = convertingResponseError(response.errorBody());
                        String rLogs = "Post Final Score Failed: " + Logs;
                        Log.e(TAG, rLogs);
                        volleyCatch.writeToFile(rLogs);
                        ll_loading.setVisibility(View.GONE);
                        Toast.makeText(ViewPDFNewRVSActivity.this, "Sending final score failed.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                {
                    String Logs = "Post Final Score Failure: " + t.getMessage();
                    Log.e(TAG, Logs);
                    volleyCatch.writeToFile(Logs);
                    ll_loading.setVisibility(View.GONE);
                    Toast.makeText(ViewPDFNewRVSActivity.this, "Sending final score failed.", Toast.LENGTH_LONG).show();
                }
            });
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
            ll_loading.setVisibility(View.GONE);
            Toast.makeText(ViewPDFNewRVSActivity.this, "Sending final score failed.", Toast.LENGTH_LONG).show();
        }
    }

    private void initUpdateRVSStatus()
    {
        try
        {
            RepositoryNewRVSBuildings.updateNewRVSBuildingStatus(getApplicationContext(), AssetID);

            Intent intent = new Intent(ViewPDFNewRVSActivity.this, NavigationActivity.class);
            startActivity(intent);
            finish();

            Toast.makeText(ViewPDFNewRVSActivity.this, "Successfully Submitted!", Toast.LENGTH_LONG).show();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
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

}
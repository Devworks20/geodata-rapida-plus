package com.geodata.rapida.plus.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.Retrofit.Client.APIClient;
import com.geodata.rapida.plus.Retrofit.Interface.APIClientInterface;
import com.geodata.rapida.plus.Retrofit.Model.AssignedInspectorsListModel;
import com.geodata.rapida.plus.Retrofit.Model.BuildingInformationModel;
import com.geodata.rapida.plus.Retrofit.Model.BuildingOccupancyListModel;
import com.geodata.rapida.plus.Retrofit.Model.BuildingTypeModel;
import com.geodata.rapida.plus.Retrofit.Model.EarthquakeRVSReportModel;
import com.geodata.rapida.plus.Retrofit.Model.FallingHazardsModel;
import com.geodata.rapida.plus.Retrofit.Model.MOFileAttachmentsList;
import com.geodata.rapida.plus.Retrofit.Model.MissionOrdersModel;
import com.geodata.rapida.plus.Retrofit.Model.NoOfPersonsModel;
import com.geodata.rapida.plus.Retrofit.Model.OccupanciesModel;
import com.geodata.rapida.plus.Retrofit.Model.ReportPDFClass;
import com.geodata.rapida.plus.Retrofit.Model.SoilTypesModel;
import com.geodata.rapida.plus.SQLite.Class.AddBuildingScoresClass;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryBuildingScores;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryBuildingType;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryEarthquakeRVSReport;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryFallingHazards;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryNoOfPersons;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryOccupancies;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryOnlineAssignedInspectors;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryOnlineBuildingInformation;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryOnlineBuildingOccupancyList;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryOnlineMOFileAttachmentsList;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryOnlineMissionOrders;
import com.geodata.rapida.plus.SQLite.Repository.RepositorySoilTypes;
import com.geodata.rapida.plus.Tools.UserAccount;
import com.geodata.rapida.plus.Tools.VolleyCatch;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetAllDataActivity extends AppCompatActivity
{
    private static final String TAG = GetAllDataActivity.class.getSimpleName();

    Timer timer;

    VolleyCatch volleyCatch = new VolleyCatch();

    Boolean runningThread = false;

    int noSynced = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_all_data);

        initViews();
    }

    private void initViews()
    {
        try
        {
            timer = new Timer();

            initTimerAPIs();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initTimerAPIs()
    {
        try
        {
            timer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    runOnUiThread(new Runnable()
                    {
                        public void run()
                        {
                            Log.e(TAG, "REFRESH COUNT SYNC: " + noSynced);

                            if (runningThread)
                            {
                                if (noSynced == 9)
                                {
                                    Intent intent = new Intent(GetAllDataActivity.this, NavigationActivity.class);
                                    startActivity(intent);
                                    finish();

                                    Toast.makeText(GetAllDataActivity.this, "Successfully Logged-in.", Toast.LENGTH_LONG).show();

                                    runningThread = false;
                                    noSynced = 0;
                                    timer.cancel();
                                }
                            }
                            else
                            {
                                Log.e(TAG, "CALLED: initCallAPIs");

                                runningThread = true;

                                initSetAllData();
                            }
                        }
                    });
                }
            }, 2000, 1000);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initSetAllData()
    {
        try
        {
            initSaveBuildingsScoring();

            /* GET SEISMIC */
            initGetAllSeismicMissionOrders();

            initGetAllFallingHazards();


            /* GET EARTHQUAKE */
            initGetAllEarthquakeMissionOrders();

            initGetAllEarthquakeInspectionRVSReports();


            /* GET FILE MAINTENANCE */
            initGetAllOccupancies();

            initGetAllNoOfPersons();

            initGetAllSoilTypes();

            initGetAllBuildingTypes();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    //Save Building Scoring
    private void initSaveBuildingsScoring()
    {
        try
        {
            Cursor cursor = RepositoryBuildingScores.realAllData(getApplicationContext());

            if (cursor.getCount() == 0)
            {
                List<AddBuildingScoresClass> addBuildingScoresClassList;

                addBuildingScoresClassList = new ArrayList<>();

                AddBuildingScoresClass addBuildingScoresClass;

                //region HIGH SEISMICITY
                //region HIGH - W1
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("W1");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("4.4");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("W1");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("W1");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("W1");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-2.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("W1");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("W1");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("0.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("W1");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("+2.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("W1");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("0.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("W1");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("0.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("W1");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("0.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                //endregion

                //region HIGH - W2
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("W2");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("3.8");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("W2");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("W2");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("W2");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-2.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("W2");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("W2");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("-1.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("W2");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("+2.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("W2");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("W2");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("W2");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region HIGH - S1
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S1 (MRF)");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("2.8");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S1 (MRF)");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("+0.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S1 (MRF)");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("+0.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S1 (MRF)");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-1.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S1 (MRF)");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S1 (MRF)");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("-1.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S1 (MRF)");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("+1.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S1 (MRF)");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S1 (MRF)");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-0.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S1 (MRF)");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-1.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region HIGH - S2
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S2 (BR)");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("3.0");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S2 (BR)");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("+0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S2 (BR)");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("+0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S2 (BR)");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-1.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S2 (BR)");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S2 (BR)");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S2 (BR)");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("+1.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S2 (BR)");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S2 (BR)");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-0.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S2 (BR)");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-1.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region HIGH - S3
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S3 (LM)");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("3.2");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S3 (LM)");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S3 (LM)");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S3 (LM)");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S3 (LM)");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S3 (LM)");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("-0.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S3 (LM)");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S3 (LM)");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S3 (LM)");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-0.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S3 (LM)");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-1.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region HIGH - S4
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S4 (RC SW)");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("2.8");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S4 (RC SW)");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("+0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S4 (RC SW)");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("+0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S4 (RC SW)");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-1.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S4 (RC SW)");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S4 (RC SW)");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S4 (RC SW)");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("+1.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S4 (RC SW)");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S4 (RC SW)");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-0.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S4 (RC SW)");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-1.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region HIGH - S5
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S5 (URM INF)");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("2.0");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S5 (URM INF)");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("+0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S5 (URM INF)");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("+0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S5 (URM INF)");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-1.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S5 (URM INF)");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S5 (URM INF)");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("-0.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S5 (URM INF)");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S5 (URM INF)");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S5 (URM INF)");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("S5 (URM INF)");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region HIGH - C1
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("C1 (MRF)");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("2.5");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("C1 (MRF)");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("+0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("C1 (MRF)");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("+0.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("C1 (MRF)");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-1.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("C1 (MRF)");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("C1 (MRF)");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("-1.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("C1 (MRF)");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("+1.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("C1 (MRF)");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("C1 (MRF)");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-0.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("C1 (MRF)");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-1.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region HIGH - C2
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("C2 (SW)");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("2.8");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("C2 (SW)");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("+0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("C2 (SW)");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("+0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("C2 (SW)");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-1.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("C2 (SW)");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("C2 (SW)");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("-1.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("C2 (SW)");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("+2.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("C2 (SW)");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("C2 (SW)");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-0.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("C2 (SW)");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region HIGH - C3
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("C3 (URM INF)");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("1.6");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("C3 (URM INF)");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("+0.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("C3 (URM INF)");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("+0.3");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("C3 (URM INF)");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-1.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("C3 (URM INF)");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("C3 (URM INF)");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("-1.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("C3 (URM INF)");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("C3 (URM INF)");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("C3 (URM INF)");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("C3 (URM INF)");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region HIGH - PC1
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("PC1 (TU)");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("2.6");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("PC1 (TU)");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("PC1 (TU)");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("PC1 (TU)");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("PC1 (TU)");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("PC1 (TU)");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("PC1 (TU)");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("+2.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("PC1 (TU)");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("PC1 (TU)");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-0.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("PC1 (TU)");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region HIGH - PC2
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("PC2");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("2.4");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("PC2");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("+0.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("PC2");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("+0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("PC2");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-1.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("PC2");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("PC2");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("PC2");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("PC2");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("PC2");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-0.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("PC2");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-1.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region HIGH - RM1
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("RM1 (FD)");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("2.8");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("RM1 (FD)");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("+0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("RM1 (FD)");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("RM1 (FD)");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-1.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("RM1 (FD)");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("RM1 (FD)");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("-1.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("RM1 (FD)");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("+2.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("RM1 (FD)");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("RM1 (FD)");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-0.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("RM1 (FD)");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region HIGH - RM2
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("RM2 (RD)");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("2.8");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("RM2 (RD)");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("+0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("RM2 (RD)");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("+0.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("RM2 (RD)");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-1.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("RM2 (RD)");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("RM2 (RD)");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("RM2 (RD)");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("+2.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("RM2 (RD)");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("RM2 (RD)");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-0.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("RM2 (RD)");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-0.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region HIGH - URM
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("URM");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("1.8");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("URM");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("0.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("URM");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("URM");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-1.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("URM");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("URM");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("-0.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("URM");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("URM");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("URM");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-0.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("High Seismicity");
                addBuildingScoresClass.setBuildingType("URM");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion
                //endregion

                //region LOW SEISMICITY
                //region LOW - W1
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("W1");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("7.4");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("W1");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("W1");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("W1");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-4.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("W1");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("W1");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("W1");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("0.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("W1");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("W1");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-1.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("W1");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-1.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                //endregion

                //region LOW - W2
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("W2");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("6.0");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("W2");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("W2");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("W2");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-3.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("W2");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("W2");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("W2");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("+0.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("W2");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("W2");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("W2");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-2.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region LOW - S1
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S1 (MRF)");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("4.6");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S1 (MRF)");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("+0.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S1 (MRF)");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("+1.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S1 (MRF)");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-2.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S1 (MRF)");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S1 (MRF)");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S1 (MRF)");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("+0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S1 (MRF)");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S1 (MRF)");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-1.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S1 (MRF)");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-2.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region LOW - S2
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S2 (BR)");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("4.8");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S2 (BR)");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("+0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S2 (BR)");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("+1.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S2 (BR)");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-2.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S2 (BR)");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S2 (BR)");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S2 (BR)");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("+0.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S2 (BR)");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S2 (BR)");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-1.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S2 (BR)");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-2.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region LOW - S3
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S3 (LM)");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("4.6");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S3 (LM)");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S3 (LM)");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S3 (LM)");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S3 (LM)");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S3 (LM)");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S3 (LM)");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S3 (LM)");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S3 (LM)");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-1.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S3 (LM)");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-2.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region LOW - S4
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S4 (RC SW)");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("4.8");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S4 (RC SW)");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("+0.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S4 (RC SW)");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("+1.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S4 (RC SW)");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-2.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S4 (RC SW)");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S4 (RC SW)");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S4 (RC SW)");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("-0.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S4 (RC SW)");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S4 (RC SW)");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-1.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S4 (RC SW)");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-2.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region LOW - S5
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S5 (URM INF)");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("5.0");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S5 (URM INF)");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("+0.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S5 (URM INF)");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("+1.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S5 (URM INF)");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-2.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S5 (URM INF)");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S5 (URM INF)");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S5 (URM INF)");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S5 (URM INF)");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S5 (URM INF)");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S5 (URM INF)");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-2.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region LOW - C1
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("C1 (MRF)");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("4.4");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("C1 (MRF)");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("+0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("C1 (MRF)");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("+1.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("C1 (MRF)");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-1.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("C1 (MRF)");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("C1 (MRF)");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("C1 (MRF)");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("+0.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("C1 (MRF)");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("C1 (MRF)");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-1.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("C1 (MRF)");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-2.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region LOW - C2
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("C2 (SW)");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("4.8");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("C2 (SW)");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("-0.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("C2 (SW)");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("0.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("C2 (SW)");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-2.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("C2 (SW)");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("C2 (SW)");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("C2 (SW)");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("+0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("C2 (SW)");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("C2 (SW)");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("C2 (SW)");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-2.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region LOW - C3
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("C3 (URM INF)");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("4.4");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("C3 (URM INF)");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("C3 (URM INF)");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("C3 (URM INF)");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-2.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("C3 (URM INF)");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("C3 (URM INF)");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("C3 (URM INF)");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("C3 (URM INF)");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("C3 (URM INF)");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("C3 (URM INF)");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-2.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region LOW - PC1
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("PC1 (TU)");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("4.4");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("PC1 (TU)");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("PC1 (TU)");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("PC1 (TU)");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("PC1 (TU)");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("PC1 (TU)");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("PC1 (TU)");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("+0.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("PC1 (TU)");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("PC1 (TU)");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("PC1 (TU)");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-1.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region LOW - PC2
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("PC2");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("4.6");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("PC2");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("-0.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("PC2");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("-0.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("PC2");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-1.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("PC2");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("PC2");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("PC2");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("PC2");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("PC2");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-1.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("PC2");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-2.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region LOW - RM1
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("RM1 (FD)");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("4.8");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("RM1 (FD)");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("RM1 (FD)");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("RM1 (FD)");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-2.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("RM1 (FD)");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("RM1 (FD)");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("RM1 (FD)");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("+0.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("RM1 (FD)");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("RM1 (FD)");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("RM1 (FD)");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-1.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region LOW - RM2
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("RM2 (RD)");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("4.6");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("RM2 (RD)");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("-0.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("RM2 (RD)");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("0.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("RM2 (RD)");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-1.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("RM2 (RD)");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("RM2 (RD)");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("RM2 (RD)");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("+0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("RM2 (RD)");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("RM2 (RD)");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("RM2 (RD)");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-1.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion HIGH - RM2

                //region LOW - URM
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("URM");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("4.6");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("URM");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("-0.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("URM");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("URM");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-1.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("URM");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("URM");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("URM");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("+0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("URM");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("URM");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("URM");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-1.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion
                //endregion

                //region MODERATE SEISMICITY
                //region MODERATE - W1
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("W1");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("5.2");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("W1");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("W1");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("W1");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-3.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("W1");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("W1");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("0.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("W1");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("+1.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("W1");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("W1");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-0.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("W1");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-1.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                //endregion

                //region MODERATE - W2
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("W2");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("4.8");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("W2");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("W2");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("W2");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-3.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("W2");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("W2");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("-0.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("W2");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("+1.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("W2");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("W2");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-1.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("W2");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-1.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region MODERATE - S1
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S1 (MRF)");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("3.6");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S1 (MRF)");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("+0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S1 (MRF)");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("+1.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S1 (MRF)");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-2.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S1 (MRF)");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S1 (MRF)");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S1 (MRF)");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("+1.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S1 (MRF)");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S1 (MRF)");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-1.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S1 (MRF)");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-1.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region MODERATE - S2
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S2 (BR)");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("3.6");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S2 (BR)");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("+0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S2 (BR)");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("+1.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S2 (BR)");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-2.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S2 (BR)");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S2 (BR)");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S2 (BR)");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("+1.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S2 (BR)");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S2 (BR)");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-1.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S2 (BR)");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-1.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region MODERATE - S3
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S3 (LM)");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("3.8");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S3 (LM)");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S3 (LM)");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S3 (LM)");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S3 (LM)");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S3 (LM)");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S3 (LM)");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S3 (LM)");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S3 (LM)");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-1.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S3 (LM)");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-1.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region MODERATE - S4
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S4 (RC SW)");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("3.6");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S4 (RC SW)");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("+0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S4 (RC SW)");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("+1.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S4 (RC SW)");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-2.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S4 (RC SW)");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S4 (RC SW)");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S4 (RC SW)");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("+1.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S4 (RC SW)");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S4 (RC SW)");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-1.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S4 (RC SW)");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-1.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region MODERATE - S5
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S5 (URM INF)");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("3.6");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Low Seismicity");
                addBuildingScoresClass.setBuildingType("S5 (URM INF)");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("+0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S5 (URM INF)");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("+0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S5 (URM INF)");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-2.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S5 (URM INF)");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S5 (URM INF)");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("-0.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S5 (URM INF)");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S5 (URM INF)");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S5 (URM INF)");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-1.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("S5 (URM INF)");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-1.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region MODERATE - C1
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("C1 (MRF)");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("3.0");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("C1 (MRF)");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("+0.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("C1 (MRF)");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("+0.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("C1 (MRF)");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-2.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("C1 (MRF)");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("C1 (MRF)");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("-1.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("C1 (MRF)");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("+1.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("C1 (MRF)");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("C1 (MRF)");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-1.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("C1 (MRF)");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-1.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region MODERATE - C2
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("C2 (SW)");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("3.6");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("C2 (SW)");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("+0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("C2 (SW)");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("+0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("C2 (SW)");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-2.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("C2 (SW)");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("C2 (SW)");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("C2 (SW)");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("+1.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("C2 (SW)");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("C2 (SW)");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-1.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("C2 (SW)");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-1.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region MODERATE - C3
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("C3 (URM INF)");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("3.2");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("C3 (URM INF)");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("+0.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("C3 (URM INF)");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("+0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("C3 (URM INF)");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-2.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("C3 (URM INF)");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("C3 (URM INF)");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("-1.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("C3 (URM INF)");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("C3 (URM INF)");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("C3 (URM INF)");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-1.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("C3 (URM INF)");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-1.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region MODERATE - PC1
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("PC1 (TU)");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("3.2");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("PC1 (TU)");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("PC1 (TU)");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("PC1 (TU)");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("PC1 (TU)");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("PC1 (TU)");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("-0.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("PC1 (TU)");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("+1.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("PC1 (TU)");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("PC1 (TU)");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-1.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("PC1 (TU)");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-1.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region MODERATE - PC2
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("PC2");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("3.2");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("PC2");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("+0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("PC2");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("+0.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("PC2");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-1.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("PC2");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("PC2");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("PC2");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("PC2");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("PC2");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-1.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("PC2");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-1.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region MODERATE - RM1
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("RM1 (FD)");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("3.6");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("RM1 (FD)");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("+0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("RM1 (FD)");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("RM1 (FD)");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-2.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("RM1 (FD)");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("RM1 (FD)");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("RM1 (FD)");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("2.0");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("RM1 (FD)");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("RM1 (FD)");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-1.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("RM1 (FD)");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-1.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion

                //region MODERATE - RM2
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("RM2 (RD)");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("3.4");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("RM2 (RD)");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("+0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("RM2 (RD)");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("+0.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("RM2 (RD)");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-1.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("RM2 (RD)");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("RM2 (RD)");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("RM2 (RD)");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("+1.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("RM2 (RD)");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("RM2 (RD)");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-1.2");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("RM2 (RD)");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-1.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion HIGH - RM2

                //region MODERATE - URM
                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("URM");
                addBuildingScoresClass.setModifiers("Basic Score");
                addBuildingScoresClass.setScores("3.4");
                addBuildingScoresClass.setIsActive("1");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("URM");
                addBuildingScoresClass.setModifiers("Mid Rise (4 to 7 stories)");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("URM");
                addBuildingScoresClass.setModifiers("High Rise (> 7 stories)");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("URM");
                addBuildingScoresClass.setModifiers("Vertical Irregularity");
                addBuildingScoresClass.setScores("-1.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("URM");
                addBuildingScoresClass.setModifiers("Plan Irregularity");
                addBuildingScoresClass.setScores("-0.5");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("URM");
                addBuildingScoresClass.setModifiers("Pre-Code");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("URM");
                addBuildingScoresClass.setModifiers("Post-Benchmark");
                addBuildingScoresClass.setScores("N/A");

                addBuildingScoresClassList.add(addBuildingScoresClass);

                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("URM");
                addBuildingScoresClass.setModifiers("Soil Type C");
                addBuildingScoresClass.setScores("-0.4");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("URM");
                addBuildingScoresClass.setModifiers("Soil Type D");
                addBuildingScoresClass.setScores("-0.8");

                addBuildingScoresClassList.add(addBuildingScoresClass);


                addBuildingScoresClass = new AddBuildingScoresClass();
                addBuildingScoresClass.setCategory("Moderate Seismicity");
                addBuildingScoresClass.setBuildingType("URM");
                addBuildingScoresClass.setModifiers("Soil Type E");
                addBuildingScoresClass.setScores("-1.6");

                addBuildingScoresClassList.add(addBuildingScoresClass);
                //endregion
                //endregion

                int size = addBuildingScoresClassList.size();

                for(int i=0; i < size ; i++)
                {
                    AddBuildingScoresClass addBuildingScoresClass1 = addBuildingScoresClassList.get(i);

                    RepositoryBuildingScores.saveBuildingScores(getApplicationContext(), addBuildingScoresClass1);
                }
            }
            cursor.close();

            noSynced = noSynced + 1;
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());

            noSynced = noSynced + 1;
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


    /* GET - SEISMIC */
    private void initGetAllSeismicMissionOrders()
    {
        try
        {
            Log.e(TAG, "CALLED: GET ALL Seismic MissionOrders");

            APIClientInterface apiInterface = APIClient.getClient().create(APIClientInterface.class);

            Call<List<MissionOrdersModel>> callLogin = apiInterface.GETAllSeismicMissionOrders(UserAccount.employeeID);

            callLogin.enqueue(new Callback<List<MissionOrdersModel>>()
            {
                @Override
                public void onResponse(@NonNull Call<List<MissionOrdersModel>> call, @NonNull Response<List<MissionOrdersModel>> response)
                {
                    if(response.isSuccessful())
                    {
                        if(response.body() != null)
                        {
                            final List<MissionOrdersModel> missionOrdersModelList = response.body();

                            for (int i = 0; i < missionOrdersModelList.size(); i++)
                            {

                                //region GET MISSION ORDER
                                MissionOrdersModel missionOrdersModel = new MissionOrdersModel();

                                missionOrdersModel.setScreenerID(String.valueOf(UserAccount.employeeID));
                                missionOrdersModel.setMissionOrderType("SRI");

                                missionOrdersModel.setApprovedBy(missionOrdersModelList.get(i).getApprovedBy() != null?
                                        missionOrdersModelList.get(i).getApprovedBy():"");

                                missionOrdersModel.setApprovedByID(missionOrdersModelList.get(i).getApprovedByID() != null?
                                        missionOrdersModelList.get(i).getApprovedByID():"");

                                missionOrdersModel.setAssetID(missionOrdersModelList.get(i).getAssetID() != null?
                                        missionOrdersModelList.get(i).getAssetID():"");

                                missionOrdersModel.setDateIssued(missionOrdersModelList.get(i).getDateIssued() != null?
                                        missionOrdersModelList.get(i).getDateIssued():"");

                                missionOrdersModel.setDateReported(missionOrdersModelList.get(i).getDateReported() != null?
                                        missionOrdersModelList.get(i).getDateReported():"");

                                missionOrdersModel.setEndorsedForApproval(missionOrdersModelList.get(i).getEndorsedForApproval() != null?
                                        missionOrdersModelList.get(i).getEndorsedForApproval():"");

                                missionOrdersModel.setEndorsedForApprovalID(missionOrdersModelList.get(i).getEndorsedForApprovalID() != null?
                                        missionOrdersModelList.get(i).getEndorsedForApprovalID():"");

                                missionOrdersModel.setInspectionStatus(missionOrdersModelList.get(i).getInspectionStatus() != null?
                                        missionOrdersModelList.get(i).getInspectionStatus():"");

                                missionOrdersModel.setInventoryYear(missionOrdersModelList.get(i).getInventoryYear() != null?
                                        missionOrdersModelList.get(i).getInventoryYear():"");

                                missionOrdersModel.setMissionOrderID(missionOrdersModelList.get(i).getMissionOrderID() != null?
                                        missionOrdersModelList.get(i).getMissionOrderID():"");

                                missionOrdersModel.setMissionOrderNo(missionOrdersModelList.get(i).getMissionOrderNo() != null?
                                        missionOrdersModelList.get(i).getMissionOrderNo():"");

                                missionOrdersModel.setReasonForScreening(missionOrdersModelList.get(i).getReasonForScreening() != null?
                                        missionOrdersModelList.get(i).getReasonForScreening():"");

                                missionOrdersModel.setRemarks(missionOrdersModelList.get(i).getRemarks() != null?
                                        missionOrdersModelList.get(i).getRemarks():"");

                                missionOrdersModel.setScreeningSchedule(missionOrdersModelList.get(i).getScreeningSchedule() != null?
                                        missionOrdersModelList.get(i).getScreeningSchedule():"");

                                missionOrdersModel.setScreeningType(missionOrdersModelList.get(i).getScreeningType());

                                missionOrdersModel.setSignaturePath(missionOrdersModelList.get(i).getSignaturePath());


                                Cursor cursor = RepositoryOnlineMissionOrders.realAllData(getApplicationContext(),
                                        String.valueOf(UserAccount.employeeID), missionOrdersModelList.get(i).getMissionOrderNo());

                                if (cursor != null && cursor.getCount() == 0)
                                {
                                    Date now = new Date(System.currentTimeMillis());
                                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
                                    String dtAdded = dateFormat.format(now);
                                    missionOrdersModel.setDtAdded(dtAdded);

                                    RepositoryOnlineMissionOrders.saveMissionOrders(getApplicationContext(), missionOrdersModel);
                                }
                                else
                                {
                                    RepositoryOnlineMissionOrders.updateTheMissionOrderStatus(getApplicationContext(), missionOrdersModel);
                                }
                                //endregion

                                //Download and Save the Signature to external device
                                initDownloadSignatureViaURL(missionOrdersModelList.get(i).getSignaturePath(),
                                        String.valueOf(missionOrdersModelList.get(i).getMissionOrderID()));

                                //region GET BUILDING INFORMATION
                                BuildingInformationModel buildingInformationModel = new BuildingInformationModel();

                                buildingInformationModel.setScreenerID(String.valueOf(UserAccount.employeeID));
                                buildingInformationModel.setMissionOrderID(String.valueOf(missionOrdersModelList.get(i).getMissionOrderID()));

                                buildingInformationModel.setAccountCode(missionOrdersModelList.get(i).getBuildingInformationModel().getAccountCode());

                                buildingInformationModel.setAccountCodeID(missionOrdersModelList.get(i).getBuildingInformationModel().getAccountCodeID());
                                buildingInformationModel.setAge(missionOrdersModelList.get(i).getBuildingInformationModel().getAge());
                                buildingInformationModel.setAgency(missionOrdersModelList.get(i).getBuildingInformationModel().getAgency());
                                buildingInformationModel.setAgencyID(missionOrdersModelList.get(i).getBuildingInformationModel().getAgencyID());
                                buildingInformationModel.setArea_Purok_Sitio(missionOrdersModelList.get(i).getBuildingInformationModel().getArea_Purok_Sitio());
                                buildingInformationModel.setAssessedValue(missionOrdersModelList.get(i).getBuildingInformationModel().getAssessedValue());

                                buildingInformationModel.setAssetID(missionOrdersModelList.get(i).getBuildingInformationModel().getAssetID() != null ?
                                                                    missionOrdersModelList.get(i).getBuildingInformationModel().getAssetID():"");


                                buildingInformationModel.setAveAreaPerFloor(missionOrdersModelList.get(i).getBuildingInformationModel().getAveAreaPerFloor() != null ?
                                                                            missionOrdersModelList.get(i).getBuildingInformationModel().getAveAreaPerFloor():"");

                                buildingInformationModel.setBIN(missionOrdersModelList.get(i).getBuildingInformationModel().getBIN() != null ?
                                                                missionOrdersModelList.get(i).getBuildingInformationModel().getBIN():"");

                                buildingInformationModel.setBIRZonalValue(missionOrdersModelList.get(i).getBuildingInformationModel().getBIRZonalValue());
                                buildingInformationModel.setBldgConditionID(missionOrdersModelList.get(i).getBuildingInformationModel().getBldgConditionID());

                                buildingInformationModel.setBldgGroup(missionOrdersModelList.get(i).getBuildingInformationModel().getBldgGroup());

                                buildingInformationModel.setBldgGroupID(missionOrdersModelList.get(i).getBuildingInformationModel().getBldgGroupID() != null ?
                                                                        missionOrdersModelList.get(i).getBuildingInformationModel().getBldgGroupID():"");

                                buildingInformationModel.setBldgOwnershipTypeID(missionOrdersModelList.get(i).getBuildingInformationModel().getBldgOwnershipTypeID() != null ?
                                                                                missionOrdersModelList.get(i).getBuildingInformationModel().getBldgOwnershipTypeID():"");

                                buildingInformationModel.setBldg_Rm_Flr(missionOrdersModelList.get(i).getBuildingInformationModel().getBldg_Rm_Flr() != null ?
                                                                        missionOrdersModelList.get(i).getBuildingInformationModel().getBldg_Rm_Flr():"");

                                buildingInformationModel.setBookValue(missionOrdersModelList.get(i).getBuildingInformationModel().getBookValue() != null ?
                                                                      missionOrdersModelList.get(i).getBuildingInformationModel().getBookValue():"");

                                buildingInformationModel.setBuildingCode(missionOrdersModelList.get(i).getBuildingInformationModel().getBuildingCode() != null ?
                                                                         missionOrdersModelList.get(i).getBuildingInformationModel().getBuildingCode():"");

                                buildingInformationModel.setBuildingConditionReport(missionOrdersModelList.get(i).getBuildingInformationModel().getBuildingConditionReport());

                                buildingInformationModel.setBuildingDisplayID(missionOrdersModelList.get(i).getBuildingInformationModel().getBuildingDisplayID() != null ?
                                                                              missionOrdersModelList.get(i).getBuildingInformationModel().getBuildingDisplayID():"");

                                buildingInformationModel.setBuildingInfoID(missionOrdersModelList.get(i).getBuildingInformationModel().getBuildingInfoID() != null ?
                                                                           missionOrdersModelList.get(i).getBuildingInformationModel().getBuildingInfoID():"");

                                buildingInformationModel.setBuildingInventoryYearID(missionOrdersModelList.get(i).getBuildingInformationModel().getBuildingInventoryYearID() != null ?
                                                                                    missionOrdersModelList.get(i).getBuildingInformationModel().getBuildingInventoryYearID():"");

                                buildingInformationModel.setBuildingLandPin(missionOrdersModelList.get(i).getBuildingInformationModel().getBuildingLandPin() != null ?
                                                                            missionOrdersModelList.get(i).getBuildingInformationModel().getBuildingLandPin():"");

                                buildingInformationModel.setBuildingName(missionOrdersModelList.get(i).getBuildingInformationModel().getBuildingName() != null ?
                                                                         missionOrdersModelList.get(i).getBuildingInformationModel().getBuildingName():"");

                                buildingInformationModel.setBuildingNoOfPersons(missionOrdersModelList.get(i).getBuildingInformationModel().getBuildingNoOfPersons());
                                buildingInformationModel.setBuildingOwnershipType(missionOrdersModelList.get(i).getBuildingInformationModel().getBuildingOwnershipType());
                                buildingInformationModel.setBuildingSoilType(missionOrdersModelList.get(i).getBuildingInformationModel().getBuildingSoilType());

                                buildingInformationModel.setContactNo(missionOrdersModelList.get(i).getBuildingInformationModel().getContactNo() != null ?
                                                                      missionOrdersModelList.get(i).getBuildingInformationModel().getContactNo():"");

                                buildingInformationModel.setCost(missionOrdersModelList.get(i).getBuildingInformationModel().getCost() != null ?
                                                                 missionOrdersModelList.get(i).getBuildingInformationModel().getCost():"");

                                buildingInformationModel.setCostPerSqm(missionOrdersModelList.get(i).getBuildingInformationModel().getCostPerSqm() != null ?
                                                                       missionOrdersModelList.get(i).getBuildingInformationModel().getCostPerSqm():"");

                                buildingInformationModel.setDPWHOB_ID(missionOrdersModelList.get(i).getBuildingInformationModel().getDPWHOB_ID() != null ?
                                                                       missionOrdersModelList.get(i).getBuildingInformationModel().getDPWHOB_ID():"");

                                buildingInformationModel.setDateFinished(missionOrdersModelList.get(i).getBuildingInformationModel().getDateFinished() != null ?
                                                                         missionOrdersModelList.get(i).getBuildingInformationModel().getDateFinished():"");

                                buildingInformationModel.setDateSaved(missionOrdersModelList.get(i).getBuildingInformationModel().getDateSaved() != null ?
                                                                      missionOrdersModelList.get(i).getBuildingInformationModel().getDateSaved():"");

                                buildingInformationModel.setDateUpdated(missionOrdersModelList.get(i).getBuildingInformationModel().getDateUpdated() != null ?
                                                                        missionOrdersModelList.get(i).getBuildingInformationModel().getDateUpdated():"");

                                buildingInformationModel.setDistrictOffice(missionOrdersModelList.get(i).getBuildingInformationModel().getDistrictOffice());

                                buildingInformationModel.setDistrictOfficeID(missionOrdersModelList.get(i).getBuildingInformationModel().getDistrictOfficeID() != null ?
                                                                             missionOrdersModelList.get(i).getBuildingInformationModel().getDistrictOfficeID():"");

                                buildingInformationModel.setEmail(missionOrdersModelList.get(i).getBuildingInformationModel().getEmail() !=null ?
                                                                  missionOrdersModelList.get(i).getBuildingInformationModel().getEmail():"");

                                buildingInformationModel.setFaultDistance(missionOrdersModelList.get(i).getBuildingInformationModel().getFaultDistance() !=null ?
                                                                          missionOrdersModelList.get(i).getBuildingInformationModel().getFaultDistance():"");

                                buildingInformationModel.setFilesUrl(missionOrdersModelList.get(i).getBuildingInformationModel().getFilesUrl() !=null ?
                                                                     missionOrdersModelList.get(i).getBuildingInformationModel().getFilesUrl():"");

                                buildingInformationModel.setFloorArea(missionOrdersModelList.get(i).getBuildingInformationModel().getFloorArea() !=null ?
                                                                      missionOrdersModelList.get(i).getBuildingInformationModel().getFloorArea():"");

                                buildingInformationModel.setGOB_ID(missionOrdersModelList.get(i).getBuildingInformationModel().getGOB_ID() !=null ?
                                                                   missionOrdersModelList.get(i).getBuildingInformationModel().getGOB_ID():"");

                                buildingInformationModel.setHeight(missionOrdersModelList.get(i).getBuildingInformationModel().getHeight() !=null ?
                                                                   missionOrdersModelList.get(i).getBuildingInformationModel().getHeight():"");

                                buildingInformationModel.setImageFile(missionOrdersModelList.get(i).getBuildingInformationModel().getImageFile() !=null ?
                                                                      missionOrdersModelList.get(i).getBuildingInformationModel().getImageFile():"");

                                buildingInformationModel.setImageUrl(missionOrdersModelList.get(i).getBuildingInformationModel().getImageUrl() !=null ?
                                                                     missionOrdersModelList.get(i).getBuildingInformationModel().getImageUrl():"");

                                buildingInformationModel.setInventoryYear(missionOrdersModelList.get(i).getBuildingInformationModel().getInventoryYear() != null ?
                                                                          missionOrdersModelList.get(i).getBuildingInformationModel().getInventoryYear():"");

                                buildingInformationModel.setLat(missionOrdersModelList.get(i).getBuildingInformationModel().getLat() !=null ?
                                                                missionOrdersModelList.get(i).getBuildingInformationModel().getLat():"");

                                buildingInformationModel.setLifeSpanOfBuilding(missionOrdersModelList.get(i).getBuildingInformationModel().getLifeSpanOfBuilding() !=null ?
                                                                               missionOrdersModelList.get(i).getBuildingInformationModel().getLifeSpanOfBuilding():"");

                                buildingInformationModel.setLocation(missionOrdersModelList.get(i).getBuildingInformationModel().getLocation() !=null ?
                                                                     missionOrdersModelList.get(i).getBuildingInformationModel().getLocation():"");

                                buildingInformationModel.setLong(missionOrdersModelList.get(i).getBuildingInformationModel().getLong() !=null ?
                                                                 missionOrdersModelList.get(i).getBuildingInformationModel().getLong():"");

                                buildingInformationModel.setAltitude(missionOrdersModelList.get(i).getBuildingInformationModel().getAltitude() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getAltitude():"");


                                buildingInformationModel.setLotArea(missionOrdersModelList.get(i).getBuildingInformationModel().getLotArea() !=null ?
                                                                    missionOrdersModelList.get(i).getBuildingInformationModel().getLotArea():"");

                                buildingInformationModel.setLotOwnershipType(missionOrdersModelList.get(i).getBuildingInformationModel().getLotOwnershipType());

                                buildingInformationModel.setLotOwnershipTypeID(missionOrdersModelList.get(i).getBuildingInformationModel().getLotOwnershipTypeID() !=null ?
                                                                               missionOrdersModelList.get(i).getBuildingInformationModel().getLotOwnershipTypeID():"");

                                buildingInformationModel.setNearestFault(missionOrdersModelList.get(i).getBuildingInformationModel().getNearestFault() !=null ?
                                                                         missionOrdersModelList.get(i).getBuildingInformationModel().getNearestFault():"");

                                buildingInformationModel.setNoOfFloors(missionOrdersModelList.get(i).getBuildingInformationModel().getNoOfFloors() !=null ?
                                                                         missionOrdersModelList.get(i).getBuildingInformationModel().getNoOfFloors():"");

                                buildingInformationModel.setNoOfPersonsID(missionOrdersModelList.get(i).getBuildingInformationModel().getNoOfPersonsID() !=null ?
                                                                         missionOrdersModelList.get(i).getBuildingInformationModel().getNoOfPersonsID():"");

                                buildingInformationModel.setNoOfUnits(missionOrdersModelList.get(i).getBuildingInformationModel().getNoOfUnits() !=null ?
                                                                      missionOrdersModelList.get(i).getBuildingInformationModel().getNoOfUnits():"");

                                buildingInformationModel.setOccupancies(missionOrdersModelList.get(i).getBuildingInformationModel().getOccupancies() !=null ?
                                                                        missionOrdersModelList.get(i).getBuildingInformationModel().getOccupancies():"");

                                buildingInformationModel.setOjectID(missionOrdersModelList.get(i).getBuildingInformationModel().getOjectID() !=null ?
                                                                    missionOrdersModelList.get(i).getBuildingInformationModel().getOjectID():"");

                                buildingInformationModel.setOpenSpace(missionOrdersModelList.get(i).getBuildingInformationModel().getOpenSpace() !=null ?
                                                                      missionOrdersModelList.get(i).getBuildingInformationModel().getOpenSpace():"");

                                buildingInformationModel.setOwnerName(missionOrdersModelList.get(i).getBuildingInformationModel().getOwnerName() !=null ?
                                                                      missionOrdersModelList.get(i).getBuildingInformationModel().getOwnerName():"");

                                buildingInformationModel.setOwnershipType(missionOrdersModelList.get(i).getBuildingInformationModel().getOwnershipType() !=null ?
                                                                          missionOrdersModelList.get(i).getBuildingInformationModel().getOwnershipType():"");

                                buildingInformationModel.setPosition(missionOrdersModelList.get(i).getBuildingInformationModel().getPosition() !=null ?
                                                                     missionOrdersModelList.get(i).getBuildingInformationModel().getPosition():"");

                                buildingInformationModel.setRemarks(missionOrdersModelList.get(i).getBuildingInformationModel().getRemarks() !=null ?
                                                                    missionOrdersModelList.get(i).getBuildingInformationModel().getRemarks():"");

                                buildingInformationModel.setSeismicity(missionOrdersModelList.get(i).getBuildingInformationModel().getSeismicity() !=null ?
                                                                       missionOrdersModelList.get(i).getBuildingInformationModel().getSeismicity():"");

                                buildingInformationModel.setSoilTypeID(missionOrdersModelList.get(i).getBuildingInformationModel().getSoilTypeID() !=null ?
                                                                       missionOrdersModelList.get(i).getBuildingInformationModel().getSoilTypeID():"");

                                buildingInformationModel.setStructureType(missionOrdersModelList.get(i).getBuildingInformationModel().getStructureType());

                                buildingInformationModel.setStructureTypeID(missionOrdersModelList.get(i).getBuildingInformationModel().getStructureTypeID() !=null ?
                                                                            missionOrdersModelList.get(i).getBuildingInformationModel().getStructureTypeID():"");

                                buildingInformationModel.setTCTNo(missionOrdersModelList.get(i).getBuildingInformationModel().getTCTNo() !=null ?
                                                                  missionOrdersModelList.get(i).getBuildingInformationModel().getTCTNo():"");

                                buildingInformationModel.setValueOfRepair(missionOrdersModelList.get(i).getBuildingInformationModel().getValueOfRepair() !=null ?
                                                                          missionOrdersModelList.get(i).getBuildingInformationModel().getValueOfRepair():"");

                                buildingInformationModel.setZipCode(missionOrdersModelList.get(i).getBuildingInformationModel().getZipCode() !=null ?
                                                                    missionOrdersModelList.get(i).getBuildingInformationModel().getZipCode():"");

                                buildingInformationModel.setBrgyCode(missionOrdersModelList.get(i).getBuildingInformationModel().getBrgyCode() !=null ?
                                                                     missionOrdersModelList.get(i).getBuildingInformationModel().getBrgyCode():"");

                                buildingInformationModel.setCitymunCode(missionOrdersModelList.get(i).getBuildingInformationModel().getCitymunCode() !=null ?
                                                                        missionOrdersModelList.get(i).getBuildingInformationModel().getCitymunCode():"");

                                buildingInformationModel.setGisBarangay(missionOrdersModelList.get(i).getBuildingInformationModel().getGisBarangay());
                                buildingInformationModel.setGisCity(missionOrdersModelList.get(i).getBuildingInformationModel().getGisCity());
                                buildingInformationModel.setGisProvince(missionOrdersModelList.get(i).getBuildingInformationModel().getGisProvince());
                                buildingInformationModel.setGisRegion(missionOrdersModelList.get(i).getBuildingInformationModel().getGisRegion());


                                buildingInformationModel.setIsForInspection(missionOrdersModelList.get(i).getBuildingInformationModel().getIsForInspection() !=null ?
                                                                            missionOrdersModelList.get(i).getBuildingInformationModel().getIsForInspection():"");

                                buildingInformationModel.setProvCode(missionOrdersModelList.get(i).getBuildingInformationModel().getProvCode() !=null ?
                                                                     missionOrdersModelList.get(i).getBuildingInformationModel().getProvCode():"");

                                buildingInformationModel.setRegCode(missionOrdersModelList.get(i).getBuildingInformationModel().getRegCode() !=null ?
                                                                    missionOrdersModelList.get(i).getBuildingInformationModel().getRegCode():"");


                                Cursor cursor2 = RepositoryOnlineBuildingInformation.realAllData3(getApplicationContext(),
                                        buildingInformationModel.getScreenerID(),
                                        buildingInformationModel.getMissionOrderID());

                                if (cursor2 != null && cursor2.getCount() != 0)
                                {
                                    RepositoryOnlineBuildingInformation.updateBuildingInformation(getApplicationContext(), buildingInformationModel);
                                }
                                else
                                {
                                    RepositoryOnlineBuildingInformation.saveBuildingInformation(getApplicationContext(), buildingInformationModel);
                                }
                                //endregion

                                //region GET Occupancy List
                                if (missionOrdersModelList.get(i).getBuildingInformationModel() != null &&
                                    missionOrdersModelList.get(i).getBuildingInformationModel().getOccupancies() != null &&
                                   !missionOrdersModelList.get(i).getBuildingInformationModel().getOccupancies().isEmpty())
                                {
                                    String occupancy = missionOrdersModelList.get(i).getBuildingInformationModel().getOccupancies();

                                    BuildingOccupancyListModel buildingOccupancyListModel = new BuildingOccupancyListModel();

                                    buildingOccupancyListModel.setScreenerID(String.valueOf(UserAccount.employeeID));
                                    buildingOccupancyListModel.setMissionOrderID(String.valueOf(missionOrdersModelList.get(i).getMissionOrderID()));
                                    buildingOccupancyListModel.setOccupancy(occupancy);

                                    Cursor cursor1 = RepositoryOnlineBuildingOccupancyList.realAllData(
                                            getApplicationContext(),
                                            buildingOccupancyListModel.getScreenerID(),
                                            buildingOccupancyListModel.getMissionOrderID(),
                                            buildingOccupancyListModel.getOccupancy()
                                    );

                                    if (cursor1 != null && cursor1.getCount() == 0)
                                    {
                                        RepositoryOnlineBuildingOccupancyList.saveBuildingOccupancyList(getApplicationContext(), buildingOccupancyListModel);
                                    }
                                }
                                //endregion

                                //region GET Assigned Inspection
                                try
                                {
                                    if (missionOrdersModelList.get(i).getAssignedInspectorsListModel() != null &&
                                       !missionOrdersModelList.get(i).getAssignedInspectorsListModel().toString().equals("[]") &&
                                       !missionOrdersModelList.get(i).getAssignedInspectorsListModel().isEmpty())
                                    {
                                        for (AssignedInspectorsListModel assignedInspector : missionOrdersModelList.get(i).getAssignedInspectorsListModel())
                                        {
                                            AssignedInspectorsListModel assignedInspectorsClass = new AssignedInspectorsListModel();

                                            assignedInspectorsClass.setScreenerID(String.valueOf(UserAccount.employeeID));
                                            assignedInspectorsClass.setMissionOrderID(String.valueOf(missionOrdersModelList.get(i).getMissionOrderID()));
                                            assignedInspectorsClass.setInspector(assignedInspector.getInspector());
                                            assignedInspectorsClass.setPosition(assignedInspector.getPosition());
                                            assignedInspectorsClass.setTL(assignedInspector.getTL());

                                            Cursor cursor1 = RepositoryOnlineAssignedInspectors.realAllData4(getApplicationContext(),
                                                    assignedInspectorsClass.getMissionOrderID(), assignedInspectorsClass.getInspector());

                                            if (cursor1 != null && cursor1.getCount() != 0)
                                            {
                                                if (cursor1.moveToFirst())
                                                {
                                                    String ID = cursor1.getString(cursor1.getColumnIndex("ID"));

                                                    RepositoryOnlineAssignedInspectors.updateAssignedInspectors(getApplicationContext(), ID,
                                                            assignedInspectorsClass);
                                                }
                                            }
                                            else
                                            {
                                                RepositoryOnlineAssignedInspectors.saveAssignedInspectors(getApplicationContext(), assignedInspectorsClass);
                                            }
                                        }
                                    }
                                }
                                catch (Exception e)
                                {
                                    Log.e(TAG, e.toString());
                                }
                                //endregion

                                //region GET Previous/Attachment File of Mission Order
                                try
                                {
                                    if (missionOrdersModelList.get(i).getMoFileAttachmentsLists() != null &&
                                        !missionOrdersModelList.get(i).getMoFileAttachmentsLists().toString().equals("[]"))
                                    {
                                        for (MOFileAttachmentsList moFileAttachment : missionOrdersModelList.get(i).getMoFileAttachmentsLists())
                                        {
                                            if (moFileAttachment.getMOAttachmentFilePath() != null &&
                                               !moFileAttachment.getMOAttachmentFilePath().equals("null") &&
                                               !moFileAttachment.getMOAttachmentFilePath().isEmpty())
                                            {
                                                MOFileAttachmentsList moFileAttachmentsList = new MOFileAttachmentsList();

                                                moFileAttachmentsList.setScreenerID(String.valueOf(UserAccount.employeeID));
                                                moFileAttachmentsList.setMissionOrderID(String.valueOf(missionOrdersModelList.get(i).getMissionOrderID()));
                                                moFileAttachmentsList.setMOAttachmentFilePath(moFileAttachment.getMOAttachmentFilePath());
                                                moFileAttachmentsList.setPreviousReport(moFileAttachment.getPreviousReport());

                                                String fileName = moFileAttachment.getMOAttachmentFilePath().substring(
                                                        moFileAttachment.getMOAttachmentFilePath().lastIndexOf("/") + 1
                                                );

                                                moFileAttachmentsList.setFileName(fileName);

                                                initSaveSeismicAttachments(moFileAttachmentsList);
                                            }
                                        }
                                    }
                                }
                                catch (Exception e)
                                {
                                    Log.e(TAG, e.toString());
                                }
                                //endregion

                                //IF REPORTED..
                                initGetAllSeismicInspectionReports(Integer.parseInt(missionOrdersModelList.get(i).getMissionOrderID()));
                            }
                        }
                        else
                        {
                            String Logs = "GET SRI Mission Orders: Server Response Null";

                            Log.e(TAG, Logs);
                            volleyCatch.writeToFile(Logs);
                        }
                    }
                    else
                    {
                        String Logs = "GET SRI Mission Orders Failed: " + convertingResponseError(response.errorBody());

                        Log.e(TAG, Logs);
                        volleyCatch.writeToFile(Logs);
                    }

                    noSynced = noSynced + 1;
                }

                @Override
                public void onFailure(@NonNull Call<List<MissionOrdersModel>> call, @NonNull Throwable t)
                {
                    String Logs = "GET SRI Mission Orders Failure: " + t.getMessage();

                    Log.e(TAG, Logs);
                    volleyCatch.writeToFile(Logs);

                    noSynced = noSynced + 1;
                }
            });
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());

            noSynced = noSynced + 1;
        }
    }

    //Seismic Attachment - Saving Attachments to External Devices
    private void initSaveSeismicAttachments(MOFileAttachmentsList moFileAttachmentsList)
    {
        try
        {
            Thread thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        Cursor cursor1 = RepositoryOnlineMOFileAttachmentsList.realAllData4(getApplicationContext(), moFileAttachmentsList);

                        if (cursor1.getCount() == 0)
                        {
                            RepositoryOnlineMOFileAttachmentsList.saveMOFileAttachment(getApplicationContext(), moFileAttachmentsList);
                        }

                        String fileURL = moFileAttachmentsList.getMOAttachmentFilePath();

                        String ExternalPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SRI/MissionOrder/Attachment";

                        String sFileName = fileURL.substring(fileURL.lastIndexOf("/") + 1);
                        moFileAttachmentsList.setFileName(sFileName);

                        File fExternalPath = new File(ExternalPath);

                        if (!fExternalPath.exists())
                        {
                            File wallpaperDirectory = new File(ExternalPath);
                            boolean isCreated = wallpaperDirectory.mkdirs();
                        }

                        URL u = new URL(fileURL);

                        try (InputStream is = u.openStream();
                             DataInputStream dis = new DataInputStream(is);
                             FileOutputStream fos = new FileOutputStream(new File(ExternalPath + "/" + sFileName)))
                        {

                            byte[] buffer = new byte[1024];
                            int length;

                            while ((length = dis.read(buffer)) > 0)
                            {
                                fos.write(buffer, 0, length);
                            }
                        }
                        catch (IOException | SecurityException e)
                        {
                            Log.e(TAG, "Download File Attachment error: " + e.toString());
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
            });

            thread.start();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }


    private void initGetAllSeismicInspectionReports(Integer MissionOrderID)
    {
        Log.e(TAG, "GETPDFReport Called - MISSION ORDER: " + MissionOrderID);

        APIClientInterface apiInterface = APIClient.getClient().create(APIClientInterface.class);

        Call<ReportPDFClass> getPDFReport = apiInterface.GETAllSeismicInspectionReports(UserAccount.employeeID, MissionOrderID);

        getPDFReport.enqueue(new Callback<ReportPDFClass>()
        {
            @Override
            public void onResponse(@NonNull Call<ReportPDFClass> call, @NonNull Response<ReportPDFClass> response)
            {
                try
                {
                    if (response.isSuccessful() && response.body() != null)
                    {
                        ReportPDFClass reportPDFClass = response.body();

                        if (reportPDFClass.getFilePath() != null && !reportPDFClass.getFilePath().isEmpty() && !reportPDFClass.getFilePath().equals("null"))
                        {
                            String FileName = reportPDFClass.getFilePath().substring(reportPDFClass.getFilePath().lastIndexOf("/") + 1);

                            RepositoryOnlineMissionOrders.updateReportPathOfMissionOrder(getApplicationContext(),
                                    UserAccount.UserAccountID, String.valueOf(MissionOrderID), reportPDFClass.getFilePath());

                            initSaveSeismicInspectionReport(FileName, reportPDFClass.getFilePath());
                        }
                    }
                    else
                    {
                        String errorLogs = (response.errorBody() != null) ? convertingResponseError(response.errorBody()) : "Unknown Error";
                        String Logs = (response.isSuccessful()) ? "PDF Reports Failed: Server Response Null" :
                                "PDF Reports Failed: " + errorLogs;

                        Log.e(TAG, Logs);
                        volleyCatch.writeToFile(Logs);
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, "Exception in onResponse: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReportPDFClass> call, @NonNull Throwable t)
            {
                String Logs = "Get PDF Reports Failure: " + t.getMessage();

                Log.e(TAG, Logs);
                volleyCatch.writeToFile(Logs);
            }
        });
    }


    //Seismic Inspection Report - Saving PDF File to External Devices
    private void initSaveSeismicInspectionReport(final String fileName, final String fileURL)
    {
        try
        {
            Thread thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        String AttachmentPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SRI/RVS Scoring";

                        File fileAttachment = new File(AttachmentPath);

                        if (!fileAttachment.exists())
                        {
                            File wallpaperDirectory = new File(AttachmentPath);

                            boolean isCreated =  wallpaperDirectory.mkdirs();
                        }

                        URL u = new URL(fileURL);
                        InputStream is = u.openStream();

                        DataInputStream dis = new DataInputStream(is);

                        byte[] buffer = new byte[1024];
                        int length;

                        FileOutputStream fos = new FileOutputStream(new File(AttachmentPath + "/" + fileName));

                        while ((length = dis.read(buffer))>0)
                        {
                            fos.write(buffer, 0, length);
                        }
                    }
                    catch (IOException | SecurityException e)
                    {
                        Log.e(TAG, "Download SRI PDF Report error: " + e.toString());
                    }
                }
            });

            thread.start();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initGetAllFallingHazards()
    {
        try
        {
            if (haveNetworkConnection(getApplicationContext()))
            {
                Log.e(TAG, "CALLED: GET All Falling Hazards");

                APIClientInterface apiInterface = APIClient.getClient().create(APIClientInterface.class);

                Call<List<FallingHazardsModel>> callLogin = apiInterface.GETAllFallingHazards();

                callLogin.enqueue(new Callback<List<FallingHazardsModel>>()
                {
                    @Override
                    public void onResponse(@NonNull Call<List<FallingHazardsModel>> call, @NonNull Response<List<FallingHazardsModel>> response)
                    {
                        try
                        {
                            if (response.isSuccessful() && response.body() != null)
                            {
                                List<FallingHazardsModel> fallingHazardsModelList = response.body();

                                for (FallingHazardsModel model : fallingHazardsModelList)
                                {
                                    Cursor cursor = RepositoryFallingHazards.realAllData2(getApplicationContext(), String.valueOf(model.getFallingHazardID()));

                                    if (cursor != null && cursor.getCount() == 0)
                                    {
                                        FallingHazardsModel fallingHazardsModel = new FallingHazardsModel();
                                        fallingHazardsModel.setFallingHazardID(model.getFallingHazardID());
                                        fallingHazardsModel.setFallingHazardDesc(model.getFallingHazardDesc());

                                        RepositoryFallingHazards.saveFallingHazards(getApplicationContext(), fallingHazardsModel);
                                    }
                                }
                            }
                            else
                            {
                                String errorLogs = (response.errorBody() != null) ? convertingResponseError(response.errorBody()) : "Unknown Error";

                                String Logs = (response.isSuccessful()) ? "GET All Falling Hazards: Server Response Null" :
                                                                          "GET All Falling Hazards Failed: " + errorLogs;

                                Log.e(TAG, Logs);
                                volleyCatch.writeToFile(Logs);
                            }

                            noSynced++;
                        }
                        catch (Exception e)
                        {
                            Log.e(TAG, "Exception in onResponse: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<FallingHazardsModel>> call, @NonNull Throwable t)
                    {
                        String Logs = "GET All Falling Hazards Failure: " + t.getMessage();

                        Log.e(TAG, Logs);
                        volleyCatch.writeToFile(Logs);

                        noSynced++;
                    }
                });
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception in try-catch: " + e.getMessage());
            noSynced++;
        }
    }





    /* GET - EARTHQUAKE */
    private void initGetAllEarthquakeMissionOrders()
    {
        try
        {
            APIClientInterface apiInterface = APIClient.getClient().create(APIClientInterface.class);

            Log.e(TAG, "CALLED: GET All EDI Mission Orders");

            Call<List<MissionOrdersModel>> callLogin = apiInterface.GETAllEarthquakeMissionOrders(UserAccount.employeeID);

            callLogin.enqueue(new Callback<List<MissionOrdersModel>>()
            {
                @Override
                public void onResponse(@NonNull Call<List<MissionOrdersModel>> call, @NonNull Response<List<MissionOrdersModel>> response)
                {
                    if(response.isSuccessful())
                    {
                        if(response.body() != null)
                        {
                            final List<MissionOrdersModel> missionOrdersModelList = response.body();

                            for (int i = 0; i < missionOrdersModelList.size(); i++)
                            {
                                //region GET MISSION ORDER
                                MissionOrdersModel missionOrdersModel = new MissionOrdersModel();

                                missionOrdersModel.setScreenerID(String.valueOf(UserAccount.employeeID));
                                missionOrdersModel.setMissionOrderType("EDI");

                                missionOrdersModel.setApprovedBy(missionOrdersModelList.get(i).getApprovedBy() != null?
                                                                 missionOrdersModelList.get(i).getApprovedBy():"");

                                missionOrdersModel.setApprovedByID(missionOrdersModelList.get(i).getApprovedByID() != null?
                                                                    missionOrdersModelList.get(i).getApprovedByID():"");

                                missionOrdersModel.setAssetID(missionOrdersModelList.get(i).getAssetID() != null?
                                                              missionOrdersModelList.get(i).getAssetID():"");

                                missionOrdersModel.setDateIssued(missionOrdersModelList.get(i).getDateIssued() != null?
                                                                 missionOrdersModelList.get(i).getDateIssued():"");

                                missionOrdersModel.setDateReported(missionOrdersModelList.get(i).getDateReported() != null?
                                                                   missionOrdersModelList.get(i).getDateReported():"");

                                missionOrdersModel.setEndorsedForApproval(missionOrdersModelList.get(i).getEndorsedForApproval() != null?
                                                                         missionOrdersModelList.get(i).getEndorsedForApproval():"");

                                missionOrdersModel.setEndorsedForApprovalID(missionOrdersModelList.get(i).getEndorsedForApprovalID() != null?
                                                                            missionOrdersModelList.get(i).getEndorsedForApprovalID():"");

                                missionOrdersModel.setInspectionStatus(missionOrdersModelList.get(i).getInspectionStatus() != null?
                                                                       missionOrdersModelList.get(i).getInspectionStatus():"");

                                missionOrdersModel.setInventoryYear(missionOrdersModelList.get(i).getInventoryYear() != null?
                                                                    missionOrdersModelList.get(i).getInventoryYear():"");

                                missionOrdersModel.setMissionOrderID(missionOrdersModelList.get(i).getMissionOrderID() != null?
                                                                     missionOrdersModelList.get(i).getMissionOrderID():"");

                                missionOrdersModel.setMissionOrderNo(missionOrdersModelList.get(i).getMissionOrderNo() != null?
                                                                     missionOrdersModelList.get(i).getMissionOrderNo():"");

                                missionOrdersModel.setReasonForScreening(missionOrdersModelList.get(i).getReasonForScreening() != null?
                                                                         missionOrdersModelList.get(i).getReasonForScreening():"");

                                missionOrdersModel.setRemarks(missionOrdersModelList.get(i).getRemarks() != null?
                                                              missionOrdersModelList.get(i).getRemarks():"");

                                missionOrdersModel.setScreeningSchedule(missionOrdersModelList.get(i).getScreeningSchedule() != null?
                                                                        missionOrdersModelList.get(i).getScreeningSchedule():"");

                                missionOrdersModel.setScreeningType(missionOrdersModelList.get(i).getScreeningType());

                                missionOrdersModel.setSignaturePath(missionOrdersModelList.get(i).getSignaturePath());


                                Cursor cursor = RepositoryOnlineMissionOrders.realAllData(getApplicationContext(),
                                        String.valueOf(UserAccount.employeeID), missionOrdersModelList.get(i).getMissionOrderNo());

                                if (cursor.getCount() == 0)
                                {
                                    Date now = new Date(System.currentTimeMillis());
                                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
                                    String dtAdded = dateFormat.format(now);
                                    missionOrdersModel.setDtAdded(dtAdded);

                                    RepositoryOnlineMissionOrders.saveMissionOrders(getApplicationContext(), missionOrdersModel);
                                }
                                else
                                {
                                    RepositoryOnlineMissionOrders.updateTheMissionOrderStatus(getApplicationContext(), missionOrdersModel);
                                }
                                //endregion

                                //Download and Save the Signature to external device
                                initDownloadSignatureViaURL(missionOrdersModelList.get(i).getSignaturePath(),
                                        String.valueOf(missionOrdersModelList.get(i).getMissionOrderID()));

                                //region GET BUILDING INFORMATION
                                BuildingInformationModel buildingInformationModel = new BuildingInformationModel();

                                buildingInformationModel.setScreenerID(String.valueOf(UserAccount.employeeID));
                                buildingInformationModel.setMissionOrderID(String.valueOf(missionOrdersModelList.get(i).getMissionOrderID()));

                                buildingInformationModel.setAccountCode(missionOrdersModelList.get(i).getBuildingInformationModel().getAccountCode());

                                buildingInformationModel.setAccountCodeID(missionOrdersModelList.get(i).getBuildingInformationModel().getAccountCodeID());
                                buildingInformationModel.setAge(missionOrdersModelList.get(i).getBuildingInformationModel().getAge());
                                buildingInformationModel.setAgency(missionOrdersModelList.get(i).getBuildingInformationModel().getAgency());
                                buildingInformationModel.setAgencyID(missionOrdersModelList.get(i).getBuildingInformationModel().getAgencyID());
                                buildingInformationModel.setArea_Purok_Sitio(missionOrdersModelList.get(i).getBuildingInformationModel().getArea_Purok_Sitio());
                                buildingInformationModel.setAssessedValue(missionOrdersModelList.get(i).getBuildingInformationModel().getAssessedValue());

                                buildingInformationModel.setAssetID(missionOrdersModelList.get(i).getBuildingInformationModel().getAssetID() != null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getAssetID():"");


                                buildingInformationModel.setAveAreaPerFloor(missionOrdersModelList.get(i).getBuildingInformationModel().getAveAreaPerFloor() != null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getAveAreaPerFloor():"");

                                buildingInformationModel.setBIN(missionOrdersModelList.get(i).getBuildingInformationModel().getBIN() != null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getBIN():"");

                                buildingInformationModel.setBIRZonalValue(missionOrdersModelList.get(i).getBuildingInformationModel().getBIRZonalValue());
                                buildingInformationModel.setBldgConditionID(missionOrdersModelList.get(i).getBuildingInformationModel().getBldgConditionID());

                                buildingInformationModel.setBldgGroup(missionOrdersModelList.get(i).getBuildingInformationModel().getBldgGroup());

                                buildingInformationModel.setBldgGroupID(missionOrdersModelList.get(i).getBuildingInformationModel().getBldgGroupID() != null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getBldgGroupID():"");

                                buildingInformationModel.setBldgOwnershipTypeID(missionOrdersModelList.get(i).getBuildingInformationModel().getBldgOwnershipTypeID() != null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getBldgOwnershipTypeID():"");

                                buildingInformationModel.setBldg_Rm_Flr(missionOrdersModelList.get(i).getBuildingInformationModel().getBldg_Rm_Flr() != null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getBldg_Rm_Flr():"");

                                buildingInformationModel.setBookValue(missionOrdersModelList.get(i).getBuildingInformationModel().getBookValue() != null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getBookValue():"");

                                buildingInformationModel.setBuildingCode(missionOrdersModelList.get(i).getBuildingInformationModel().getBuildingCode() != null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getBuildingCode():"");

                                buildingInformationModel.setBuildingConditionReport(missionOrdersModelList.get(i).getBuildingInformationModel().getBuildingConditionReport());

                                buildingInformationModel.setBuildingDisplayID(missionOrdersModelList.get(i).getBuildingInformationModel().getBuildingDisplayID() != null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getBuildingDisplayID():"");

                                buildingInformationModel.setBuildingInfoID(missionOrdersModelList.get(i).getBuildingInformationModel().getBuildingInfoID() != null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getBuildingInfoID():"");

                                buildingInformationModel.setBuildingInventoryYearID(missionOrdersModelList.get(i).getBuildingInformationModel().getBuildingInventoryYearID() != null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getBuildingInventoryYearID():"");

                                buildingInformationModel.setBuildingLandPin(missionOrdersModelList.get(i).getBuildingInformationModel().getBuildingLandPin() != null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getBuildingLandPin():"");

                                buildingInformationModel.setBuildingName(missionOrdersModelList.get(i).getBuildingInformationModel().getBuildingName() != null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getBuildingName():"");

                                buildingInformationModel.setBuildingNoOfPersons(missionOrdersModelList.get(i).getBuildingInformationModel().getBuildingNoOfPersons());
                                buildingInformationModel.setBuildingOwnershipType(missionOrdersModelList.get(i).getBuildingInformationModel().getBuildingOwnershipType());
                                buildingInformationModel.setBuildingSoilType(missionOrdersModelList.get(i).getBuildingInformationModel().getBuildingSoilType());

                                buildingInformationModel.setContactNo(missionOrdersModelList.get(i).getBuildingInformationModel().getContactNo() != null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getContactNo():"");

                                buildingInformationModel.setCost(missionOrdersModelList.get(i).getBuildingInformationModel().getCost() != null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getCost():"");

                                buildingInformationModel.setCostPerSqm(missionOrdersModelList.get(i).getBuildingInformationModel().getCostPerSqm() != null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getCostPerSqm():"");

                                buildingInformationModel.setDPWHOB_ID(missionOrdersModelList.get(i).getBuildingInformationModel().getDPWHOB_ID() != null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getDPWHOB_ID():"");

                                buildingInformationModel.setDateFinished(missionOrdersModelList.get(i).getBuildingInformationModel().getDateFinished() != null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getDateFinished():"");

                                buildingInformationModel.setDateSaved(missionOrdersModelList.get(i).getBuildingInformationModel().getDateSaved() != null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getDateSaved():"");

                                buildingInformationModel.setDateUpdated(missionOrdersModelList.get(i).getBuildingInformationModel().getDateUpdated() != null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getDateUpdated():"");

                                buildingInformationModel.setDistrictOffice(missionOrdersModelList.get(i).getBuildingInformationModel().getDistrictOffice());

                                buildingInformationModel.setDistrictOfficeID(missionOrdersModelList.get(i).getBuildingInformationModel().getDistrictOfficeID() != null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getDistrictOfficeID():"");

                                buildingInformationModel.setEmail(missionOrdersModelList.get(i).getBuildingInformationModel().getEmail() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getEmail():"");

                                buildingInformationModel.setFaultDistance(missionOrdersModelList.get(i).getBuildingInformationModel().getFaultDistance() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getFaultDistance():"");

                                buildingInformationModel.setFilesUrl(missionOrdersModelList.get(i).getBuildingInformationModel().getFilesUrl() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getFilesUrl():"");

                                buildingInformationModel.setFloorArea(missionOrdersModelList.get(i).getBuildingInformationModel().getFloorArea() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getFloorArea():"");

                                buildingInformationModel.setGOB_ID(missionOrdersModelList.get(i).getBuildingInformationModel().getGOB_ID() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getGOB_ID():"");

                                buildingInformationModel.setHeight(missionOrdersModelList.get(i).getBuildingInformationModel().getHeight() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getHeight():"");

                                buildingInformationModel.setImageFile(missionOrdersModelList.get(i).getBuildingInformationModel().getImageFile() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getImageFile():"");

                                buildingInformationModel.setImageUrl(missionOrdersModelList.get(i).getBuildingInformationModel().getImageUrl() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getImageUrl():"");

                                buildingInformationModel.setInventoryYear(missionOrdersModelList.get(i).getBuildingInformationModel().getInventoryYear() != null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getInventoryYear():"");

                                buildingInformationModel.setLat(missionOrdersModelList.get(i).getBuildingInformationModel().getLat() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getLat():"");

                                buildingInformationModel.setLifeSpanOfBuilding(missionOrdersModelList.get(i).getBuildingInformationModel().getLifeSpanOfBuilding() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getLifeSpanOfBuilding():"");

                                buildingInformationModel.setLocation(missionOrdersModelList.get(i).getBuildingInformationModel().getLocation() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getLocation():"");

                                buildingInformationModel.setLong(missionOrdersModelList.get(i).getBuildingInformationModel().getLong() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getLong():"");

                                buildingInformationModel.setAltitude(missionOrdersModelList.get(i).getBuildingInformationModel().getAltitude() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getAltitude():"");



                                buildingInformationModel.setLotArea(missionOrdersModelList.get(i).getBuildingInformationModel().getLotArea() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getLotArea():"");

                                buildingInformationModel.setLotOwnershipType(missionOrdersModelList.get(i).getBuildingInformationModel().getLotOwnershipType());

                                buildingInformationModel.setLotOwnershipTypeID(missionOrdersModelList.get(i).getBuildingInformationModel().getLotOwnershipTypeID() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getLotOwnershipTypeID():"");

                                buildingInformationModel.setNearestFault(missionOrdersModelList.get(i).getBuildingInformationModel().getNearestFault() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getNearestFault():"");

                                buildingInformationModel.setNoOfFloors(missionOrdersModelList.get(i).getBuildingInformationModel().getNoOfFloors() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getNoOfFloors():"");

                                buildingInformationModel.setNoOfPersonsID(missionOrdersModelList.get(i).getBuildingInformationModel().getNoOfPersonsID() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getNoOfPersonsID():"");

                                buildingInformationModel.setNoOfUnits(missionOrdersModelList.get(i).getBuildingInformationModel().getNoOfUnits() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getNoOfUnits():"");

                                buildingInformationModel.setOccupancies(missionOrdersModelList.get(i).getBuildingInformationModel().getOccupancies() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getOccupancies():"");

                                buildingInformationModel.setOjectID(missionOrdersModelList.get(i).getBuildingInformationModel().getOjectID() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getOjectID():"");

                                buildingInformationModel.setOpenSpace(missionOrdersModelList.get(i).getBuildingInformationModel().getOpenSpace() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getOpenSpace():"");

                                buildingInformationModel.setOwnerName(missionOrdersModelList.get(i).getBuildingInformationModel().getOwnerName() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getOwnerName():"");

                                buildingInformationModel.setOwnershipType(missionOrdersModelList.get(i).getBuildingInformationModel().getOwnershipType() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getOwnershipType():"");

                                buildingInformationModel.setPosition(missionOrdersModelList.get(i).getBuildingInformationModel().getPosition() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getPosition():"");

                                buildingInformationModel.setRemarks(missionOrdersModelList.get(i).getBuildingInformationModel().getRemarks() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getRemarks():"");

                                buildingInformationModel.setSeismicity(missionOrdersModelList.get(i).getBuildingInformationModel().getSeismicity() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getSeismicity():"");

                                buildingInformationModel.setSoilTypeID(missionOrdersModelList.get(i).getBuildingInformationModel().getSoilTypeID() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getSoilTypeID():"");

                                buildingInformationModel.setStructureType(missionOrdersModelList.get(i).getBuildingInformationModel().getStructureType());

                                buildingInformationModel.setStructureTypeID(missionOrdersModelList.get(i).getBuildingInformationModel().getStructureTypeID() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getStructureTypeID():"");

                                buildingInformationModel.setTCTNo(missionOrdersModelList.get(i).getBuildingInformationModel().getTCTNo() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getTCTNo():"");

                                buildingInformationModel.setValueOfRepair(missionOrdersModelList.get(i).getBuildingInformationModel().getValueOfRepair() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getValueOfRepair():"");

                                buildingInformationModel.setZipCode(missionOrdersModelList.get(i).getBuildingInformationModel().getZipCode() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getZipCode():"");

                                buildingInformationModel.setBrgyCode(missionOrdersModelList.get(i).getBuildingInformationModel().getBrgyCode() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getBrgyCode():"");

                                buildingInformationModel.setCitymunCode(missionOrdersModelList.get(i).getBuildingInformationModel().getCitymunCode() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getCitymunCode():"");

                                buildingInformationModel.setGisBarangay(missionOrdersModelList.get(i).getBuildingInformationModel().getGisBarangay());
                                buildingInformationModel.setGisCity(missionOrdersModelList.get(i).getBuildingInformationModel().getGisCity());
                                buildingInformationModel.setGisProvince(missionOrdersModelList.get(i).getBuildingInformationModel().getGisProvince());
                                buildingInformationModel.setGisRegion(missionOrdersModelList.get(i).getBuildingInformationModel().getGisRegion());


                                buildingInformationModel.setIsForInspection(missionOrdersModelList.get(i).getBuildingInformationModel().getIsForInspection() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getIsForInspection():"");

                                buildingInformationModel.setProvCode(missionOrdersModelList.get(i).getBuildingInformationModel().getProvCode() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getProvCode():"");

                                buildingInformationModel.setRegCode(missionOrdersModelList.get(i).getBuildingInformationModel().getRegCode() !=null ?
                                        missionOrdersModelList.get(i).getBuildingInformationModel().getRegCode():"");


                                Cursor cursor2 = RepositoryOnlineBuildingInformation.realAllData3(getApplicationContext(),
                                        buildingInformationModel.getScreenerID(),
                                        buildingInformationModel.getMissionOrderID());

                                if (cursor2.getCount() != 0)
                                {
                                    RepositoryOnlineBuildingInformation.updateBuildingInformation(getApplicationContext(), buildingInformationModel);
                                }
                                else
                                {
                                    RepositoryOnlineBuildingInformation.saveBuildingInformation(getApplicationContext(), buildingInformationModel);
                                }
                                //endregion

                                //region GET Occupancy List
                                try
                                {
                                    BuildingInformationModel buildingInfoModel = missionOrdersModelList.get(i).getBuildingInformationModel();

                                    if (buildingInfoModel != null && buildingInfoModel.getOccupancies() != null)
                                    {
                                        String occupancies = buildingInfoModel.getOccupancies();

                                        if (!occupancies.isEmpty()) {
                                            BuildingOccupancyListModel buildingOccupancyListModel = new BuildingOccupancyListModel();

                                            buildingOccupancyListModel.setScreenerID(String.valueOf(UserAccount.employeeID));
                                            buildingOccupancyListModel.setMissionOrderID(String.valueOf(missionOrdersModelList.get(i).getMissionOrderID()));
                                            buildingOccupancyListModel.setOccupancy(occupancies);

                                            Cursor cursor1 = RepositoryOnlineBuildingOccupancyList.realAllData(
                                                    getApplicationContext(),
                                                    buildingOccupancyListModel.getScreenerID(),
                                                    buildingOccupancyListModel.getMissionOrderID(),
                                                    buildingOccupancyListModel.getOccupancy()
                                            );

                                            if (cursor1 != null && cursor1.getCount() == 0)
                                            {
                                                RepositoryOnlineBuildingOccupancyList.saveBuildingOccupancyList(
                                                        getApplicationContext(), buildingOccupancyListModel);
                                            }
                                        }
                                    }
                                }
                                catch (Exception e)
                                {
                                    Log.e(TAG, e.toString());
                                }
                                //endregion

                                //region GET Assigned Inspection
                                try
                                {
                                    for (MissionOrdersModel model : missionOrdersModelList)
                                    {
                                        if (model.getAssignedInspectorsListModel() != null && !model.getAssignedInspectorsListModel().isEmpty())
                                        {
                                            for (AssignedInspectorsListModel inspectorModel : model.getAssignedInspectorsListModel())
                                            {
                                                AssignedInspectorsListModel assignedInspectorsClass = new AssignedInspectorsListModel();

                                                assignedInspectorsClass.setScreenerID(String.valueOf(UserAccount.employeeID));
                                                assignedInspectorsClass.setMissionOrderID(String.valueOf(model.getMissionOrderID()));
                                                assignedInspectorsClass.setInspector(inspectorModel.getInspector());
                                                assignedInspectorsClass.setPosition(inspectorModel.getPosition());
                                                assignedInspectorsClass.setTL(inspectorModel.getTL());

                                                Cursor cursor1 = RepositoryOnlineAssignedInspectors.realAllData4(
                                                        getApplicationContext(),
                                                        assignedInspectorsClass.getMissionOrderID(),
                                                        assignedInspectorsClass.getInspector()
                                                );

                                                if (cursor1 != null && cursor1.getCount() != 0)
                                                {
                                                    if (cursor1.moveToFirst())
                                                    {
                                                        String ID = cursor1.getString(cursor1.getColumnIndex("ID"));
                                                        RepositoryOnlineAssignedInspectors.updateAssignedInspectors(
                                                                getApplicationContext(), ID, assignedInspectorsClass);
                                                    }
                                                }
                                                else
                                                {
                                                    RepositoryOnlineAssignedInspectors.saveAssignedInspectors(
                                                            getApplicationContext(), assignedInspectorsClass);
                                                }
                                            }
                                        }
                                    }
                                }
                                catch (Exception e)
                                {
                                    Log.e(TAG, e.toString());
                                }
                                //endregion

                                //region GET Mission Attachments
                                try
                                {
                                    for (MissionOrdersModel model : missionOrdersModelList)
                                    {
                                        if (model.getMoFileAttachmentsLists() != null && !model.getMoFileAttachmentsLists().isEmpty())
                                        {
                                            for (MOFileAttachmentsList attachment : model.getMoFileAttachmentsLists())
                                            {
                                                if (attachment.getMOAttachmentFilePath() != null && !attachment.getMOAttachmentFilePath().isEmpty() &&
                                                    !attachment.getMOAttachmentFilePath().equals("null"))
                                                {
                                                    MOFileAttachmentsList moFileAttachmentsList = new MOFileAttachmentsList();

                                                    moFileAttachmentsList.setScreenerID(String.valueOf(UserAccount.employeeID));
                                                    moFileAttachmentsList.setMissionOrderID(String.valueOf(model.getMissionOrderID()));
                                                    moFileAttachmentsList.setMOAttachmentFilePath(attachment.getMOAttachmentFilePath());
                                                    moFileAttachmentsList.setPreviousReport(attachment.getPreviousReport());

                                                    String fileName = attachment.getMOAttachmentFilePath()
                                                            .substring(attachment.getMOAttachmentFilePath().lastIndexOf("/") + 1);
                                                    moFileAttachmentsList.setFileName(fileName);

                                                    initSaveEarthquakeAttachments(moFileAttachmentsList);
                                                }
                                            }
                                        }
                                    }
                                }
                                catch (Exception e)
                                {
                                    Log.e(TAG, e.toString());
                                }
                                //endregion

                                initGetEarthquakeDESAorRESAReports(
                                        missionOrdersModelList.get(i).getReasonForScreening(),
                                        Integer.parseInt(missionOrdersModelList.get(i).getMissionOrderID()));
                            }
                        }
                        else
                        {
                            String Logs = "GET All EDI Mission Orders: Server Response Null";

                            Log.e(TAG, Logs);
                            volleyCatch.writeToFile(Logs);
                        }
                    }
                    else
                    {
                        String Logs = "GET All EDI Mission Orders Failed: " + convertingResponseError(response.errorBody());

                        Log.e(TAG, Logs);
                        volleyCatch.writeToFile(Logs);
                    }

                    noSynced = noSynced + 1;
                }
                @Override
                public void onFailure(@NonNull Call<List<MissionOrdersModel>> call, @NonNull Throwable t)
                {
                    String Logs = "GET All EDI Mission Orders Failure: " + t.getMessage();

                    Log.e(TAG, Logs);
                    volleyCatch.writeToFile(Logs);

                    noSynced = noSynced + 1;
                }
            });
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());

            noSynced = noSynced + 1;
        }
    }

    //Earthquake Attachment - Saving Attachments to External Devices
    private void initSaveEarthquakeAttachments(MOFileAttachmentsList moFileAttachmentsList)
    {
        try
        {
            Thread thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        Cursor cursor1 = RepositoryOnlineMOFileAttachmentsList.realAllData4(getApplicationContext(), moFileAttachmentsList);

                        if (cursor1 != null && cursor1.getCount() == 0)
                        {
                            RepositoryOnlineMOFileAttachmentsList.saveMOFileAttachment(getApplicationContext(), moFileAttachmentsList);
                        }

                        String fileURL = moFileAttachmentsList.getMOAttachmentFilePath();

                        String ExternalPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                                + "/SRI/MissionOrder/Attachment";

                        String sFileName   = fileURL.substring(fileURL.lastIndexOf("/") + 1 );
                        moFileAttachmentsList.setFileName(sFileName);

                        File fExternalPath = new File(ExternalPath);

                        if (!fExternalPath.exists())
                        {
                            File wallpaperDirectory = new File(ExternalPath);

                            boolean isCreated =  wallpaperDirectory.mkdirs();
                        }

                        URL u = new URL(fileURL);
                        InputStream is = u.openStream();

                        DataInputStream dis = new DataInputStream(is);

                        byte[] buffer = new byte[1024];
                        int length;

                        FileOutputStream fos = new FileOutputStream(new File(ExternalPath + "/" + sFileName));

                        while ((length = dis.read(buffer))>0)
                        {
                            fos.write(buffer, 0, length);
                        }
                    }
                    catch (IOException | SecurityException e)
                    {
                        Log.e(TAG, "Download File Attachment error: " + e.toString());
                    }
                }
            });

            thread.start();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initGetEarthquakeDESAorRESAReports(String ReasonForScreening, Integer MissionOrderID)
    {
        try
        {
            String Option = "";
            APIClientInterface apiInterface = APIClient.getClient().create(APIClientInterface.class);
            Call<ReportPDFClass> getPDFReport = null;

            if (ReasonForScreening.contains("(RESA)"))
            {
                Option = "RESA";
                getPDFReport = apiInterface.GETAllEarthquakeRESAReports(UserAccount.employeeID, MissionOrderID);
            }
            else if (ReasonForScreening.contains("(DESA)"))
            {
                Option = "DESA";
                getPDFReport = apiInterface.GETAllEarthquakeDESAReports(UserAccount.employeeID, MissionOrderID);
            }

            if (getPDFReport != null)
            {
                String finalOption = Option;
                getPDFReport.enqueue(new Callback<ReportPDFClass>()
                {
                    @Override
                    public void onResponse(@NonNull Call<ReportPDFClass> call, @NonNull Response<ReportPDFClass> response)
                    {
                        if (response.isSuccessful())
                        {
                            if (response.body() != null)
                            {
                                final ReportPDFClass reportPDFClass = response.body();

                                if (reportPDFClass.getFilePath() != null
                                        && !reportPDFClass.getFilePath().isEmpty()
                                        && !reportPDFClass.getFilePath().equals("null"))
                                {
                                    String FileName = reportPDFClass.getFilePath().substring(
                                            reportPDFClass.getFilePath().lastIndexOf("/") + 1);
                                    RepositoryOnlineMissionOrders.updateReportPathOfMissionOrder(
                                            getApplicationContext(), UserAccount.UserAccountID, String.valueOf(MissionOrderID),
                                            reportPDFClass.getFilePath());
                                    initSaveEarthquakeDESAorRESAReport(finalOption, FileName, reportPDFClass.getFilePath());
                                }
                            }
                            else
                            {
                                String Logs = "PDF Reports Failed: Server Response Null";
                                Log.e(TAG, Logs);
                                volleyCatch.writeToFile(Logs);
                            }
                        }
                        else
                        {
                            String errorLogs = (response.errorBody() != null) ? convertingResponseError(response.errorBody()) : "Unknown Error";
                            String Logs = "PDF Reports Failed: " + errorLogs;
                            Log.e(TAG, Logs);
                            volleyCatch.writeToFile(Logs);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ReportPDFClass> call, @NonNull Throwable t)
                    {
                        String Logs = "Get PDF Reports Failure: " + t.getMessage();
                        Log.e(TAG, Logs);
                        volleyCatch.writeToFile(Logs);
                    }
                });
            }

            Log.e(TAG, "GETPDFReport Called");
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }


    //RESA or DESA Report - Saving PDF File to External Devices
    private void initSaveEarthquakeDESAorRESAReport(String Option, final String fileName, final String fileURL)
    {
        try
        {
            Thread thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        String AttachmentPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Option;

                        File fileAttachment = new File(AttachmentPath);

                        if (!fileAttachment.exists())
                        {
                            File wallpaperDirectory = new File(AttachmentPath);

                            boolean isCreated =  wallpaperDirectory.mkdirs();
                        }

                        URL u = new URL(fileURL);
                        InputStream is = u.openStream();

                        DataInputStream dis = new DataInputStream(is);

                        byte[] buffer = new byte[1024];
                        int length;

                        FileOutputStream fos = new FileOutputStream(new File(AttachmentPath + "/" + fileName));

                        while ((length = dis.read(buffer))>0)
                        {
                            fos.write(buffer, 0, length);
                        }
                    }
                    catch (IOException | SecurityException e)
                    {
                        Log.e(TAG, "Download SRI PDF Report error: " + e.toString());
                    }
                }
            });

            thread.start();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initGetAllEarthquakeInspectionRVSReports()
    {
        try
        {
            APIClientInterface apiInterface = APIClient.getClient().create(APIClientInterface.class);

            Log.e(TAG, "GET All EDI Report PDF");

            Call<List<EarthquakeRVSReportModel>> callLogin = apiInterface.GetAllEarthquakeRVSInspectionReports(UserAccount.employeeID);

            callLogin.enqueue(new Callback<List<EarthquakeRVSReportModel>>()
            {
                @Override
                public void onResponse(@NonNull Call<List<EarthquakeRVSReportModel>> call, @NonNull Response<List<EarthquakeRVSReportModel>> response)
                {
                    try
                    {
                        if (response.isSuccessful() && response.body() != null)
                        {
                            final List<EarthquakeRVSReportModel> earthquakeRVSReportModelList = response.body();

                            for (EarthquakeRVSReportModel model : earthquakeRVSReportModelList)
                            {
                                String EarthquakeRVSReportID = model.getEarthquakeRVSReportID();

                                if (EarthquakeRVSReportID != null)
                                {
                                    Cursor cursor = RepositoryEarthquakeRVSReport.realAllData3(getApplicationContext(), EarthquakeRVSReportID);

                                    if (cursor != null && cursor.getCount() == 0)
                                    {
                                        EarthquakeRVSReportModel earthquakeRVSReportModel = new EarthquakeRVSReportModel();

                                        earthquakeRVSReportModel.setUserAccountID(UserAccount.UserAccountID);
                                        earthquakeRVSReportModel.setEarthquakeRVSReportID(model.getEarthquakeRVSReportID());
                                        earthquakeRVSReportModel.setBuildingName(model.getBuildingName());
                                        earthquakeRVSReportModel.setConcreteFinalScore(model.getConcreteFinalScore());
                                        earthquakeRVSReportModel.setEarthquakeRVSReportPdfPath(model.getEarthquakeRVSReportPdfPath());
                                        earthquakeRVSReportModel.setFaultDistance(model.getFaultDistance());
                                        earthquakeRVSReportModel.setFinalScore(model.getFinalScore());
                                        earthquakeRVSReportModel.setNearestFault(model.getNearestFault());
                                        earthquakeRVSReportModel.setScreeningDate(model.getScreeningDate());
                                        earthquakeRVSReportModel.setSeismicity(model.getSeismicity());
                                        earthquakeRVSReportModel.setSteelFinalScore(model.getSteelFinalScore());

                                        RepositoryEarthquakeRVSReport.saveEarthquakeRVSReport(getApplicationContext(), earthquakeRVSReportModel);

                                        if (!TextUtils.isEmpty(earthquakeRVSReportModel.getEarthquakeRVSReportPdfPath()))
                                        {
                                            String FileName = earthquakeRVSReportModel.getEarthquakeRVSReportPdfPath()
                                                    .substring(earthquakeRVSReportModel.getEarthquakeRVSReportPdfPath().lastIndexOf("/") + 1);

                                            initSaveEarthquakeRVSInspectionReport(FileName, earthquakeRVSReportModel.getEarthquakeRVSReportPdfPath());
                                        }
                                    }
                                }
                            }
                        }
                        else
                        {
                            String errorLogs = (response.errorBody() != null) ? convertingResponseError(response.errorBody()) : "Unknown Error";
                            String Logs = "GET All EDI Report PDF Failed: " + errorLogs;
                            Log.e(TAG, Logs);
                            volleyCatch.writeToFile(Logs);
                        }

                        noSynced++;
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, "Exception in onResponse: " + e.getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<EarthquakeRVSReportModel>> call, @NonNull Throwable t)
                {
                    try
                    {
                        String Logs = "GET All EDI Report PDF Failure: " + t.getMessage();
                        Log.e(TAG, Logs);
                        volleyCatch.writeToFile(Logs);
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, "Exception in onFailure: " + e.getMessage());
                    }

                    noSynced++;
                }
            });
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception in try-catch: " + e.getMessage());
            noSynced++;
        }
    }

    //Earthquake RVS Inspection Report - Saving PDF File to External Devices
    private void initSaveEarthquakeRVSInspectionReport(final String fileName, final String fileURL)
    {
        try
        {
            Thread thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        String AttachmentPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SRI/RVS";

                        File fileAttachment = new File(AttachmentPath);

                        if (!fileAttachment.exists())
                        {
                            File wallpaperDirectory = new File(AttachmentPath);

                            boolean isCreated = wallpaperDirectory.mkdirs();
                        }

                        URL u = new URL(fileURL);
                        InputStream is = u.openStream();

                        DataInputStream dis = new DataInputStream(is);

                        byte[] buffer = new byte[1024];
                        int length;

                        FileOutputStream fos = new FileOutputStream(new File(AttachmentPath + "/" + fileName));

                        while ((length = dis.read(buffer))>0)
                        {
                            fos.write(buffer, 0, length);
                        }
                    }
                    catch (IOException | SecurityException e)
                    {
                        Log.e(TAG, "Download EDI PDF Report error: " + e.toString());
                    }
                }
            });

            thread.start();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }



    //Saving Signatures Images to External Devices
    private void initDownloadSignatureViaURL(final String SignaturePath, String MissionOrderID)
    {
        try
        {
            if (SignaturePath != null)
            {
                if (!SignaturePath.equals(""))
                {
                    Thread thread = new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(SignaturePath).getContent());

                                initSaveSignatureToExternalDevice(bitmap, MissionOrderID);
                            }
                            catch (IOException e)
                            {
                                Log.e(TAG, "Download Signature File error " + e.toString());
                            }
                        }
                    });
                    thread.start();
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initSaveSignatureToExternalDevice(Bitmap imageToSave, String MissionOrderID)
    {
        try
        {
            if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getApplicationContext()),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED

                    && ActivityCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                Log.e(TAG, "Permission for saving signature is not granted!");
            }
            else
            {
                try
                {
                    String SaveFolderName = Environment.getExternalStorageDirectory().getAbsolutePath()
                            + "/SRI/" + ".Signatures";

                    File fStorageForPhotos = new File(SaveFolderName);

                    if (!fStorageForPhotos.exists()) // Create a new folder if  folder not exist
                    {
                        boolean isCreated = fStorageForPhotos.mkdirs();
                    }

                    String SignatureName      = "MO-" + MissionOrderID + "-" + "BOD Signature";
                    String SignatureExtension = "png";
                    String SignaturePath      = SaveFolderName + "/" + SignatureName + "." + SignatureExtension;

                    File file = new File(SaveFolderName, SignatureName);

                    if (file.exists())
                    {
                        boolean isCreated =  file.delete();
                    }

                    try
                    {
                        FileOutputStream out = new FileOutputStream(file + "." + SignatureExtension);
                        //out.write(bitmapData);

                        //Saving 2
                        imageToSave.compress(Bitmap.CompressFormat.PNG, 100, out);

                        out.flush();
                        out.close();
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, "Saving Signature: " + e.toString());
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, "Saving Signature: " + e.toString());
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Saving Signature: " + e.toString());
        }
    }



    //GET FILE MAINTENANCE
    private void initGetAllOccupancies()
    {
        try
        {
            if (haveNetworkConnection(getApplicationContext()))
            {
                Log.e(TAG, "CALLED: GET All Occupancies");

                APIClientInterface apiInterface = APIClient.getClient2().create(APIClientInterface.class);

                Call<List<OccupanciesModel>> callLogin = apiInterface.GetAllOccupancies();

                callLogin.enqueue(new Callback<List<OccupanciesModel>>()
                {
                    @Override
                    public void onResponse(@NonNull Call<List<OccupanciesModel>> call, @NonNull Response<List<OccupanciesModel>> response)
                    {
                        if (response.isSuccessful() && response.body() != null)
                        {
                            final List<OccupanciesModel> occupanciesModelList = response.body();

                            for (OccupanciesModel occupanciesModel : occupanciesModelList)
                            {
                                String useOfCharacterOccupancyID = String.valueOf(occupanciesModel.getUseOfCharacterOccupancyID());

                                Cursor cursor = RepositoryOccupancies.realAllData2(getApplicationContext(), useOfCharacterOccupancyID);

                                if (cursor != null && cursor.getCount() == 0)
                                {
                                    RepositoryOccupancies.saveOccupancies(getApplicationContext(), occupanciesModel);
                                }
                            }
                        }
                        else
                        {
                            String Logs = response.isSuccessful()
                                    ? "GET All Occupancies: Server Response Null"
                                    : "GET All Occupancies Failed: " + convertingResponseError(response.errorBody());

                            Log.e(TAG, Logs);
                            volleyCatch.writeToFile(Logs);
                        }

                        noSynced++;
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<OccupanciesModel>> call, @NonNull Throwable t)
                    {
                        String Logs = "GET All Occupancies Failure: " + t.getMessage();
                        Log.e(TAG, Logs);
                        volleyCatch.writeToFile(Logs);
                        noSynced++;
                    }
                });
            }
            else
            {
                noSynced++;
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
            noSynced++;
        }
    }


    private void initGetAllNoOfPersons()
    {
        if (haveNetworkConnection(getApplicationContext()))
        {
            Log.e(TAG, "CALLED: GET All NoOfPersons");

            APIClientInterface apiInterface = APIClient.getClient2().create(APIClientInterface.class);
            Call<List<NoOfPersonsModel>> callLogin = apiInterface.GetAllNoOfPersons();

            callLogin.enqueue(new Callback<List<NoOfPersonsModel>>()
            {
                @Override
                public void onResponse(@NonNull Call<List<NoOfPersonsModel>> call, @NonNull Response<List<NoOfPersonsModel>> response)
                {
                    if (response.isSuccessful() && response.body() != null)
                    {
                        final List<NoOfPersonsModel> noOfPersonsModelList = response.body();

                        for (int i = 0; i < noOfPersonsModelList.size(); i++)
                        {
                            Cursor cursor = RepositoryNoOfPersons.realAllData2(getApplicationContext(),
                                    String.valueOf(noOfPersonsModelList.get(i).getNoOfPersonsID()));

                            if (cursor != null && cursor.getCount() == 0)
                            {
                                NoOfPersonsModel noOfPersonsModel = new NoOfPersonsModel();
                                noOfPersonsModel.setNoOfPersonsID(noOfPersonsModelList.get(i).getNoOfPersonsID());
                                noOfPersonsModel.setNoOfPersons(noOfPersonsModelList.get(i).getNoOfPersons());

                                RepositoryNoOfPersons.saveNoOfPersons(getApplicationContext(), noOfPersonsModel);
                            }
                        }
                    }
                    else
                    {
                        String Logs = response.isSuccessful() ? "GET All NoOfPersons: Server Response Null" : "GET All NoOfPersons Failed: " + convertingResponseError(response.errorBody());
                        Log.e(TAG, Logs);
                        volleyCatch.writeToFile(Logs);
                    }

                    noSynced = noSynced + 1;
                }

                @Override
                public void onFailure(@NonNull Call<List<NoOfPersonsModel>> call, @NonNull Throwable t)
                {
                    String Logs = "GET All NoOfPersons Failure: " + t.getMessage();
                    Log.e(TAG, Logs);
                    volleyCatch.writeToFile(Logs);
                    noSynced = noSynced + 1;
                }
            });
        }
        else
        {
            noSynced = noSynced + 1;
        }
    }


    private void initGetAllSoilTypes()
    {
        try
        {
            if (haveNetworkConnection(getApplicationContext()))
            {
                Log.e(TAG, "CALLED: GET All SoilTypes");

                APIClientInterface apiInterface = APIClient.getClient2().create(APIClientInterface.class);

                Call<List<SoilTypesModel>> callLogin = apiInterface.GetAllSoilTypes();
                callLogin.enqueue(new Callback<List<SoilTypesModel>>()
                {
                    @Override
                    public void onResponse(@NonNull Call<List<SoilTypesModel>> call, @NonNull Response<List<SoilTypesModel>> response)
                    {
                        if (response.isSuccessful() && response.body() != null)
                        {
                            final List<SoilTypesModel> soilTypesModelList = response.body();

                            for (SoilTypesModel model : soilTypesModelList)
                            {
                                Cursor cursor = RepositorySoilTypes.realAllData2(getApplicationContext(), String.valueOf(model.getBuildingSoilTypeID()));

                                if (cursor != null && cursor.getCount() == 0)
                                {
                                    RepositorySoilTypes.saveSoilTypes(getApplicationContext(), model);
                                }
                            }
                        }
                        else
                        {
                            String errorLog = response.isSuccessful() ? "GET All SoilTypes: Server Response Null" : "GET All SoilTypes Failed: " + convertingResponseError(response.errorBody());
                            Log.e(TAG, errorLog);
                            volleyCatch.writeToFile(errorLog);
                        }

                        noSynced++;
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<SoilTypesModel>> call, @NonNull Throwable t)
                    {
                        String errorLog = "GET All SoilTypes Failure: " + t.getMessage();
                        Log.e(TAG, errorLog);
                        volleyCatch.writeToFile(errorLog);
                        noSynced++;
                    }
                });
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());

            noSynced = noSynced + 1;
        }
    }

    private void initGetAllBuildingTypes()
    {
        try
        {
            Log.e(TAG, "CALLED: GET ALL Building Types");

            APIClientInterface apiInterface = APIClient.getClient2().create(APIClientInterface.class);

            Call<List<BuildingTypeModel>> callLogin = apiInterface.GETBuildingType();
            callLogin.enqueue(new Callback<List<BuildingTypeModel>>()
            {
                @Override
                public void onResponse(@NonNull Call<List<BuildingTypeModel>> call, @NonNull Response<List<BuildingTypeModel>> response)
                {
                    if (response.isSuccessful() && response.body() != null)
                    {
                        final List<BuildingTypeModel> buildingTypeModelList = response.body();

                        for (BuildingTypeModel model : buildingTypeModelList)
                        {
                            Cursor cursor = RepositoryBuildingType.realAllData2(getApplicationContext(), model);

                            if (cursor.getCount() == 0)
                            {
                                RepositoryBuildingType.saveBuildingType(getApplicationContext(), model);
                            }
                            else
                            {
                                RepositoryBuildingType.updateBuildingType(getApplicationContext(), model);
                            }
                            cursor.close();
                        }
                    }
                    else
                    {
                        String errorLog = response.isSuccessful() ? "GET All Building Types: Server Response Null" :
                                                                    "GET All Building Types Failed: " + convertingResponseError(response.errorBody());
                        Log.e(TAG, errorLog);
                        volleyCatch.writeToFile(errorLog);
                    }

                    noSynced++;
                }

                @Override
                public void onFailure(@NonNull Call<List<BuildingTypeModel>> call, @NonNull Throwable t)
                {
                    String errorLog = "GET All Building Types Failure: " + t.getMessage();
                    Log.e(TAG, errorLog);
                    volleyCatch.writeToFile(errorLog);
                    noSynced++;
                }
            });
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

}
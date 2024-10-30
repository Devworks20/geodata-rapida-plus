package com.geodata.rapida.plus.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.geodata.rapida.plus.Adapter.RVAdapterRVSListOfEDI;
import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.Retrofit.Client.APIClient;
import com.geodata.rapida.plus.Retrofit.Interface.APIClientInterface;
import com.geodata.rapida.plus.Retrofit.Model.EarthquakeRVSReportModel;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryEarthquakeRVSReport;
import com.geodata.rapida.plus.Tools.MyExceptionHandler;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RVSListFragment extends Fragment
{
    private static final String TAG = RVSListFragment.class.getSimpleName();

    View view;

    List<EarthquakeRVSReportModel> earthquakeRvsReportModel;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RVAdapterRVSListOfEDI rvAdapterRVSListOfEDI;

    SwipeRefreshLayout srl_refresh;

    VolleyCatch volleyCatch = new VolleyCatch();

    Timer timer;
    Boolean runningThread = false;
    int noSynced = 0;
    long mSystemStartTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(getActivity())); //Getting Crash - Restart


        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_rvs_list, container, false);

        initViews();

        return view;
    }

    private void initViews()
    {
        try
        {
            earthquakeRvsReportModel = new ArrayList<>();
            recyclerView = view.findViewById(R.id.rv_list);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            rvAdapterRVSListOfEDI = new RVAdapterRVSListOfEDI(getContext(), earthquakeRvsReportModel);
            recyclerView.setAdapter(rvAdapterRVSListOfEDI);

            srl_refresh = view.findViewById(R.id.srl_refresh);

            srl_refresh.setColorSchemeResources(R.color.colorPrimary,
                    android.R.color.holo_green_dark,
                    android.R.color.holo_orange_dark,
                    android.R.color.holo_blue_dark);

            initListeners();

            initGetRVSReport();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initListeners()
    {
        srl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                try
                {
                    if (haveNetworkConnection(requireContext()))
                    {
                        if (!runningThread)
                        {
                            Toast.makeText(getActivity(), "Please wait while refreshing.", Toast.LENGTH_SHORT).show();

                            timer = new Timer();

                            mSystemStartTime = System.currentTimeMillis();
                            initTimerAPIs();
                        }
                        else
                        {
                            Toast.makeText(getActivity(), "Please wait while refreshing.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        srl_refresh.setRefreshing(false);

                        Toast.makeText(getActivity(), "Internet connection required.", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, e.toString());
                }
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initGetRVSReport()
    {
        try
        {
            Cursor cursor = RepositoryEarthquakeRVSReport.realAllData2(getContext(), String.valueOf(UserAccount.UserAccountID));

            if (cursor.getCount()!=0)
            {
                if (cursor.moveToFirst())
                {
                    earthquakeRvsReportModel.clear();

                    do
                    {
                        EarthquakeRVSReportModel earthquakeRVSReportModel = new EarthquakeRVSReportModel();

                        earthquakeRVSReportModel.setID(cursor.getString(cursor.getColumnIndex("ID")));
                        earthquakeRVSReportModel.setUserAccountID(cursor.getString(cursor.getColumnIndex("UserAccountID")));
                        earthquakeRVSReportModel.setEarthquakeRVSReportID(cursor.getString(cursor.getColumnIndex("EarthquakeRVSReportID")));
                        earthquakeRVSReportModel.setBuildingName(cursor.getString(cursor.getColumnIndex("BuildingName")));
                        earthquakeRVSReportModel.setConcreteFinalScore(cursor.getString(cursor.getColumnIndex("ConcreteFinalScore")));
                        earthquakeRVSReportModel.setEarthquakeRVSReportPdfPath(cursor.getString(cursor.getColumnIndex("EarthquakeRVSReportPdfPath")));
                        earthquakeRVSReportModel.setFaultDistance(cursor.getString(cursor.getColumnIndex("FaultDistance")));
                        earthquakeRVSReportModel.setFinalScore(cursor.getString(cursor.getColumnIndex("FinalScore")));
                        earthquakeRVSReportModel.setNearestFault(cursor.getString(cursor.getColumnIndex("NearestFault")));
                        earthquakeRVSReportModel.setScreeningDate(cursor.getString(cursor.getColumnIndex("ScreeningDate")));
                        earthquakeRVSReportModel.setSeismicity(cursor.getString(cursor.getColumnIndex("Seismicity")));
                        earthquakeRVSReportModel.setSteelFinalScore(cursor.getString(cursor.getColumnIndex("SteelFinalScore")));

                        earthquakeRvsReportModel.add(earthquakeRVSReportModel);
                    }
                    while (cursor.moveToNext());

                    rvAdapterRVSListOfEDI.notifyDataSetChanged();
                }
            }
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
                    requireActivity().runOnUiThread(new Runnable()
                    {
                        public void run()
                        {
                            Log.e(TAG, "REFRESH COUNT SYNC: " + noSynced);

                            if (runningThread)
                            {
                                if (noSynced == 1)
                                {
                                    initGetRVSReport();

                                    runningThread = false;
                                    noSynced = 0;
                                    srl_refresh.setRefreshing(false);
                                    timer.cancel();

                                    Toast.makeText(getActivity(), "Successfully Updated.", Toast.LENGTH_SHORT).show();
                                }

                                long totalRequestTime = System.currentTimeMillis() - mSystemStartTime;

                                Log.e(TAG, "Total Time: " + totalRequestTime);

                                if (totalRequestTime >= 15000)
                                {
                                    runningThread = false;
                                    noSynced = 0;
                                    srl_refresh.setRefreshing(false);
                                    timer.cancel();

                                    Toast.makeText(getActivity(), "Failed to update. Please try again later", Toast.LENGTH_LONG).show();
                                }
                            }
                            else
                            {
                                runningThread = true;

                                initGetAllEarthquakeRVSReport();
                            }
                        }
                    });
                }
            }, 1000, 1000);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }



    private void initGetAllEarthquakeRVSReport()
    {
        try
        {
            APIClientInterface apiInterface = APIClient.getClient().create(APIClientInterface.class);
            Log.e(TAG, "GET All EarthquakeRVS Report");

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

                                    Cursor cursor = RepositoryEarthquakeRVSReport.realAllData3(getContext(), EarthquakeRVSReportID);

                                    if (cursor != null && cursor.getCount() == 0)
                                    {
                                        RepositoryEarthquakeRVSReport.saveEarthquakeRVSReport(getContext(), earthquakeRVSReportModel);
                                    }
                                    else
                                    {
                                        RepositoryEarthquakeRVSReport.updateEarthquakeRVSReport(getContext(), EarthquakeRVSReportID, earthquakeRVSReportModel);
                                    }

                                    if (earthquakeRVSReportModel.getEarthquakeRVSReportPdfPath() != null &&
                                            !earthquakeRVSReportModel.getEarthquakeRVSReportPdfPath().isEmpty())
                                    {
                                        String FileName = earthquakeRVSReportModel.getEarthquakeRVSReportPdfPath()
                                                .substring(earthquakeRVSReportModel.getEarthquakeRVSReportPdfPath().lastIndexOf("/") + 1);
                                        Download_EDI_RVS_Report_PDF(FileName, earthquakeRVSReportModel.getEarthquakeRVSReportPdfPath());
                                    }
                                }
                            }
                        }
                        else
                        {
                            String errorLogs = (response.errorBody() != null) ? convertingResponseError(response.errorBody()) : "Unknown Error";
                            String Logs = "GET All Earthquake RVS Report Failed: " + errorLogs;
                            Log.e(TAG, Logs);
                            volleyCatch.writeToFile(Logs);
                        }

                        noSynced++;
                        initGetRVSReport();
                        srl_refresh.setRefreshing(false);
                        Toast.makeText(getActivity(), "Successfully updated.", Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, "Exception in onResponse: " + e.toString());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<EarthquakeRVSReportModel>> call, @NonNull Throwable t)
                {
                    try
                    {
                        String Logs = "GET All Earthquake RVS Report Failure: " + t.getMessage();
                        Log.e(TAG, Logs);
                        volleyCatch.writeToFile(Logs);

                        if (runningThread)
                        {
                            noSynced++;
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, "Exception in onFailure: " + e.toString());
                    }
                }
            });
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception in try-catch: " + e.toString());
            noSynced++;
        }
    }


    public void Download_EDI_RVS_Report_PDF(final String fileName, final String fileURL)
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
                    catch (IOException | SecurityException mue)
                    {
                        Log.e(TAG, mue.toString());
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



    //Network Validations
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

}
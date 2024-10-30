package com.geodata.rapida.plus.Fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.geodata.rapida.plus.Activity.NavigationActivity;
import com.geodata.rapida.plus.Adapter.RVAdapterMissionOrder;
import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.Retrofit.Interface.APIClientInterface;
import com.geodata.rapida.plus.SQLite.Class.MissionOrderOfEDI;
import com.geodata.rapida.plus.SQLite.Class.MissionOrdersClass;
import com.geodata.rapida.plus.SQLite.Main.SQLiteDbContext;
import com.geodata.rapida.plus.Tools.MyExceptionHandler;
import com.geodata.rapida.plus.Tools.VolleyCatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.Toast;
import com.geodata.rapida.plus.Retrofit.Client.APIClient;
import com.geodata.rapida.plus.Retrofit.Model.AssignedInspectorsListModel;
import com.geodata.rapida.plus.Retrofit.Model.BuildingInformationModel;
import com.geodata.rapida.plus.Retrofit.Model.BuildingOccupancyListModel;
import com.geodata.rapida.plus.Retrofit.Model.FallingHazardsModel;
import com.geodata.rapida.plus.Retrofit.Model.MOFileAttachmentsList;
import com.geodata.rapida.plus.Retrofit.Model.MissionOrdersModel;
import com.geodata.rapida.plus.Retrofit.Model.NoOfPersonsModel;
import com.geodata.rapida.plus.Retrofit.Model.OccupanciesModel;
import com.geodata.rapida.plus.Retrofit.Model.ReportPDFClass;
import com.geodata.rapida.plus.Retrofit.Model.SoilTypesModel;
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
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MissionOrderListFragment extends Fragment
{
    View view;

    private static final String TAG = MissionOrderListFragment.class.getSimpleName();

    List<MissionOrdersClass> missionOrdersModelList;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RVAdapterMissionOrder rvAdapterMissionOrder;

    Spinner spnr_Status;

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

        view = inflater.inflate(R.layout.fragment_mission_order_list, container, false);

        initViews();

        return view;
    }

    private void initViews()
    {

        missionOrdersModelList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.rv_list);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        rvAdapterMissionOrder = new RVAdapterMissionOrder(getContext(), missionOrdersModelList);
        recyclerView.setAdapter(rvAdapterMissionOrder);

        final String[] StringArrayNatureIncident   = getResources().getStringArray(R.array.status_array);
        ArrayAdapter<String> adapterNatureIncident = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, StringArrayNatureIncident);

        spnr_Status = view.findViewById(R.id.spnr_Status);
        spnr_Status.setAdapter(adapterNatureIncident);
        spnr_Status.setSelection(adapterNatureIncident.getPosition("All"));

        srl_refresh = view.findViewById(R.id.srl_refresh);
        srl_refresh.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        initListeners();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initSetMissionOrderList()
    {
        try
        {
            Cursor cursor = RepositoryOnlineMissionOrders.realAllData2(getContext(), String.valueOf(UserAccount.employeeID));

            if (cursor.getCount()!=0)
            {
                missionOrdersModelList.clear();

                if (cursor.moveToFirst())
                {
                    do
                    {
                        if (cursor.getString(cursor.getColumnIndex("MissionOrderType")).equalsIgnoreCase("SRI"))
                        {
                            if (!cursor.getString(cursor.getColumnIndex("ReasonForScreening")).
                                    equalsIgnoreCase("Rapid Evaluation and Safety Assessment (RESA)"))
                            {
                                MissionOrdersClass cValues = new MissionOrdersClass();

                                cValues.setApprovedBy(cursor.getString(cursor.getColumnIndex("ApprovedBy")));
                                cValues.setAssetID(cursor.getString(cursor.getColumnIndex("AssetID")));
                                cValues.setDateIssued(cursor.getString(cursor.getColumnIndex("DateIssued")));

                                cValues.setDateReported(cursor.getString(cursor.getColumnIndex("DateReported")) != null ?
                                                        cursor.getString(cursor.getColumnIndex("DateReported")):"");

                                cValues.setInspectionStatus(!cursor.getString(cursor.getColumnIndex("InspectionStatus")).equals("") ?
                                                            cursor.getString(cursor.getColumnIndex("InspectionStatus")):"Pending");

                                cValues.setMissionOrderID(cursor.getString(cursor.getColumnIndex("MissionOrderID")));
                                cValues.setMissionOrderNo(cursor.getString(cursor.getColumnIndex("MissionOrderNo")));
                                cValues.setReasonForInspector(cursor.getString(cursor.getColumnIndex("ReasonForScreening")));

                                cValues.setBuildingName(cursor.getString(cursor.getColumnIndex("BuildingName")));
                                cValues.setSeismicityRegion(cursor.getString(cursor.getColumnIndex("Seismicity")));
                                cValues.setScreeningDate(cursor.getString(cursor.getColumnIndex("ScreeningSchedule")));
                                cValues.setScreeningType(cursor.getString(cursor.getColumnIndex("ScreeningType")));

                                cValues.setIsActive(cursor.getString(cursor.getColumnIndex("isActive")));
                                cValues.setDtAdded(cursor.getString(cursor.getColumnIndex("dtAdded")));

                                missionOrdersModelList.add(cValues);
                            }
                        }
                    }
                    while (cursor.moveToNext());
                }

                if (spnr_Status.getSelectedItem().toString() != null &&
                    !spnr_Status.getSelectedItem().toString().equalsIgnoreCase("All"))
                {
                    initFilterCategoryList(spnr_Status.getSelectedItem().toString());
                }
                else
                {
                    initFilterCategoryList("all");
                }
            }
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

        spnr_Status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString();

                initFilterCategoryList(selectedItem);

            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
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
                                if (noSynced == 5)
                                {
                                    Log.e(TAG, "REFRESH DONE");

                                    initSetMissionOrderList();

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
                                Log.e(TAG, "CALLED: initCallAPIs");

                                runningThread = true;

                                initSetAllData();
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

    private void initSetAllData()
    {
        try
        {
            //SRMS
            initGetAllFallingHazards();

            initGetAllSRIMissionOrders();

            //BMS
            initGetAllOccupancies();

            initGetAllNoOfPersons();

            initGetAllSoilTypes();
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
            if (haveNetworkConnection(requireContext()))
            {
                Log.e(TAG, "CALLED: GET ALL FALLING HAZARDS");

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
                                    String FallingHazardID = String.valueOf(model.getFallingHazardID());

                                    FallingHazardsModel fallingHazardsModel = new FallingHazardsModel();
                                    fallingHazardsModel.setFallingHazardID(model.getFallingHazardID());
                                    fallingHazardsModel.setFallingHazardDesc(model.getFallingHazardDesc());

                                    Cursor cursor = RepositoryFallingHazards.realAllData2(getContext(), FallingHazardID);

                                    if (cursor != null && cursor.getCount() != 0)
                                    {
                                        if (cursor.moveToFirst())
                                        {
                                            RepositoryFallingHazards.updateFallingHazards(getContext(), FallingHazardID, fallingHazardsModel);
                                        }
                                    }
                                    else
                                    {
                                        RepositoryFallingHazards.saveFallingHazards(getContext(), fallingHazardsModel);
                                    }
                                }
                            }
                            else
                            {
                                String errorLogs = (response.errorBody() != null) ? convertingResponseError(response.errorBody()) : "Unknown Error";
                                String Logs = (response.isSuccessful()) ? "GET ALL FALLING HAZARDS: Server Response Null" :
                                                                          "GET ALL FALLING HAZARDS Failed: " + errorLogs;

                                Log.e(TAG, Logs);
                                volleyCatch.writeToFile(Logs);
                            }

                            if (runningThread)
                            {
                                noSynced++;
                            }
                        }
                        catch (Exception e)
                        {
                            Log.e(TAG, "Exception in onResponse: " + e.toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<FallingHazardsModel>> call, @NonNull Throwable t)
                    {
                        String Logs = "GET ALL FALLING HAZARDS Failure: " + t.getMessage();

                        Log.e(TAG, Logs);
                        volleyCatch.writeToFile(Logs);

                        if (runningThread)
                        {
                            noSynced = noSynced + 1;
                        }
                    }
                });
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());

            if (runningThread)
            {
                noSynced = noSynced + 1;
            }
        }
    }





    private void initGetAllOccupancies()
    {
        try
        {
            if (haveNetworkConnection(requireContext()))
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

                                Cursor cursor = RepositoryOccupancies.realAllData2(requireContext(), useOfCharacterOccupancyID);

                                if (cursor != null && cursor.getCount() == 0)
                                {
                                    RepositoryOccupancies.saveOccupancies(requireContext(), occupanciesModel);
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
        try
        {
            if (haveNetworkConnection(requireContext()))
            {
                Log.e(TAG, "CALLED: GET ALL NO. OF PERSONS");

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

                            for (NoOfPersonsModel noOfPersonsModel : noOfPersonsModelList)
                            {
                                String NoOfPersonsID = String.valueOf(noOfPersonsModel.getNoOfPersonsID());

                                Cursor cursor = RepositoryNoOfPersons.realAllData2(getContext(), NoOfPersonsID);

                                if (cursor != null && cursor.getCount() != 0)
                                {
                                    if (cursor.moveToFirst())
                                    {
                                        RepositoryNoOfPersons.updateNoOfPersons(getContext(), NoOfPersonsID, noOfPersonsModel);
                                    }
                                }
                                else
                                {
                                    RepositoryNoOfPersons.saveNoOfPersons(getContext(), noOfPersonsModel);
                                }
                            }
                        }
                        else
                        {
                            String Logs = response.isSuccessful()
                                    ? "GET ALL NO. OF PERSONS: Server Response Null"
                                    : "GET ALL NO. OF PERSONS Failed: " + convertingResponseError(response.errorBody());

                            Log.e(TAG, Logs);
                            volleyCatch.writeToFile(Logs);
                        }

                        noSynced++;
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<NoOfPersonsModel>> call, @NonNull Throwable t)
                    {
                        String Logs = "GET ALL NO. OF PERSONS Failure: " + t.getMessage();

                        Log.e(TAG, Logs);
                        volleyCatch.writeToFile(Logs);

                        if (runningThread)
                        {
                            noSynced++;
                        }
                    }
                });
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
            noSynced++;
        }
    }

    private void initGetAllSoilTypes()
    {
        if (!haveNetworkConnection(requireContext()))
        {
            return;
        }

        Log.e(TAG, "CALLED: GET ALL SOIL TYPES");

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

                    for (int i = 0; i < soilTypesModelList.size(); i++)
                    {
                        SoilTypesModel soilTypesModel = new SoilTypesModel();
                        soilTypesModel.setBuildingSoilTypeID(soilTypesModelList.get(i).getBuildingSoilTypeID());
                        soilTypesModel.setBuildingSoilTypeCode(soilTypesModelList.get(i).getBuildingSoilTypeCode());
                        soilTypesModel.setBuildingSoilTypeDesc(soilTypesModelList.get(i).getBuildingSoilTypeDesc());

                        String BuildingSoilTypeID = String.valueOf(soilTypesModelList.get(i).getBuildingSoilTypeID());
                        Cursor cursor = RepositorySoilTypes.realAllData2(getContext(), BuildingSoilTypeID);

                        if (cursor != null && cursor.getCount() != 0)
                        {
                            if (cursor.moveToFirst())
                            {
                                RepositorySoilTypes.updateSoilTypes(getContext(), BuildingSoilTypeID, soilTypesModel);
                            }
                        }
                        else
                        {
                            RepositorySoilTypes.saveSoilTypes(getContext(), soilTypesModel);
                        }
                    }
                }
                else
                {
                    String Logs = response.isSuccessful() ? "GET ALL SOIL TYPES: Server Response Null" : "GET ALL SOIL TYPES Failed: " +
                            convertingResponseError(response.errorBody());

                    Log.e(TAG, Logs);
                    volleyCatch.writeToFile(Logs);
                }

                noSynced = noSynced + 1;
            }

            @Override
            public void onFailure(@NonNull Call<List<SoilTypesModel>> call, @NonNull Throwable t)
            {
                String Logs = "GET ALL SOIL TYPES Failure: " + t.getMessage();
                Log.e(TAG, Logs);
                volleyCatch.writeToFile(Logs);

                if (runningThread)
                {
                    noSynced = noSynced + 1;
                }
            }
        });
    }




    private void initGetAllSRIMissionOrders()
    {
        try
        {
            Log.e(TAG, "CALLED: GET All SRI Mission Orders");

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

                                missionOrdersModel.setApprovedBy(missionOrdersModelList.get(i).getApprovedBy());
                                missionOrdersModel.setApprovedByID(missionOrdersModelList.get(i).getApprovedByID());
                                missionOrdersModel.setAssetID(missionOrdersModelList.get(i).getAssetID());
                                missionOrdersModel.setDateIssued(missionOrdersModelList.get(i).getDateIssued());
                                missionOrdersModel.setDateReported(missionOrdersModelList.get(i).getDateReported() != null?
                                        missionOrdersModelList.get(i).getDateReported():"");
                                missionOrdersModel.setEndorsedForApproval(missionOrdersModelList.get(i).getEndorsedForApproval());
                                missionOrdersModel.setEndorsedForApprovalID(missionOrdersModelList.get(i).getEndorsedForApprovalID());
                                missionOrdersModel.setInspectionStatus(missionOrdersModelList.get(i).getInspectionStatus());

                                missionOrdersModel.setMissionOrderID(missionOrdersModelList.get(i).getMissionOrderID());
                                missionOrdersModel.setMissionOrderNo(missionOrdersModelList.get(i).getMissionOrderNo());
                                missionOrdersModel.setReasonForScreening(missionOrdersModelList.get(i).getReasonForScreening());
                                missionOrdersModel.setRemarks(missionOrdersModelList.get(i).getRemarks() != null?
                                        missionOrdersModelList.get(i).getRemarks():"");
                                missionOrdersModel.setScreeningSchedule(missionOrdersModelList.get(i).getScreeningSchedule());
                                missionOrdersModel.setScreeningType(missionOrdersModelList.get(i).getScreeningType());
                                missionOrdersModel.setSignaturePath(missionOrdersModelList.get(i).getSignaturePath());

                                Cursor cursor = RepositoryOnlineMissionOrders.realAllData(getContext(),
                                        String.valueOf(UserAccount.employeeID), missionOrdersModelList.get(i).getMissionOrderNo());

                                if (cursor.getCount() == 0)
                                {
                                    Date now = new Date(System.currentTimeMillis());
                                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
                                    String dtAdded = dateFormat.format(now);
                                    missionOrdersModel.setDtAdded(dtAdded);

                                    RepositoryOnlineMissionOrders.saveMissionOrders(getContext(), missionOrdersModel);
                                }
                                else
                                {
                                    RepositoryOnlineMissionOrders.updateTheMissionOrderStatus(getContext(), missionOrdersModel);
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


                                Cursor cursor2 = RepositoryOnlineBuildingInformation.realAllData3(getContext(),
                                        buildingInformationModel.getScreenerID(),
                                        buildingInformationModel.getMissionOrderID());

                                if (cursor2.getCount() != 0)
                                {
                                    RepositoryOnlineBuildingInformation.updateBuildingInformation(getContext(), buildingInformationModel);
                                }
                                else
                                {
                                    RepositoryOnlineBuildingInformation.saveBuildingInformation(getContext(), buildingInformationModel);
                                }
                                //endregion

                                //region GET Occupancy List
                                if (missionOrdersModelList.get(i).getBuildingInformationModel().getOccupancies() != null &&
                                    !missionOrdersModelList.get(i).getBuildingInformationModel().getOccupancies().equals(""))
                                {
                                    BuildingOccupancyListModel buildingOccupancyListModel = new BuildingOccupancyListModel();

                                    buildingOccupancyListModel.setScreenerID(String.valueOf(UserAccount.employeeID));
                                    buildingOccupancyListModel.setMissionOrderID(String.valueOf(missionOrdersModelList.get(i).getMissionOrderID()));
                                    buildingOccupancyListModel.setOccupancy(missionOrdersModelList.get(i).getBuildingInformationModel().getOccupancies());

                                    Cursor cursor1 = RepositoryOnlineBuildingOccupancyList.realAllData(getContext(),
                                            buildingOccupancyListModel.getScreenerID(),
                                            buildingOccupancyListModel.getMissionOrderID(),
                                            buildingOccupancyListModel.getOccupancy());

                                    if (cursor1.getCount() == 0)
                                    {
                                        RepositoryOnlineBuildingOccupancyList.saveBuildingOccupancyList(getContext(), buildingOccupancyListModel);
                                    }
                                }
                                //endregion

                                //region GET Assigned Inspection
                                try
                                {
                                    if (missionOrdersModelList.get(i).getAssignedInspectorsListModel() != null &&
                                        !missionOrdersModelList.get(i).getAssignedInspectorsListModel().toString().equals("[]"))
                                    {
                                        for (int f = 0; f < missionOrdersModelList.get(i).getAssignedInspectorsListModel().size(); f++)
                                        {
                                            AssignedInspectorsListModel assignedInspectorsClass = new AssignedInspectorsListModel();

                                            assignedInspectorsClass.setScreenerID(String.valueOf(UserAccount.employeeID));
                                            assignedInspectorsClass.setMissionOrderID(String.valueOf(missionOrdersModelList.get(i).getMissionOrderID()));
                                            assignedInspectorsClass.setInspector(missionOrdersModelList.get(i).getAssignedInspectorsListModel().get(f).getInspector());
                                            assignedInspectorsClass.setPosition(missionOrdersModelList.get(i).getAssignedInspectorsListModel().get(f).getPosition());
                                            assignedInspectorsClass.setTL(missionOrdersModelList.get(i).getAssignedInspectorsListModel().get(f).getTL());

                                            Cursor cursor1 = RepositoryOnlineAssignedInspectors.realAllData4(getContext(),
                                                    assignedInspectorsClass.getMissionOrderID(),
                                                    assignedInspectorsClass.getInspector());

                                            if (cursor1.getCount() != 0)
                                            {
                                                if (cursor1.moveToFirst())
                                                {
                                                    String ID = cursor1.getString(cursor1.getColumnIndex("ID"));

                                                    RepositoryOnlineAssignedInspectors.updateAssignedInspectors(getContext(), ID, assignedInspectorsClass);
                                                }
                                            }
                                            else
                                            {
                                                RepositoryOnlineAssignedInspectors.saveAssignedInspectors(getContext(), assignedInspectorsClass);
                                            }
                                        }
                                    }
                                    else
                                    {
                                        Log.e(TAG, "NO INSPECTOR");
                                    }
                                }
                                catch (Exception e)
                                {
                                    Log.e(TAG, e.toString());
                                }
                                //endregion

                                //region GET Previous /Attachment File of Mission Order
                                try
                                {
                                    if (missionOrdersModelList.get(i).getMoFileAttachmentsLists() != null &&
                                        !missionOrdersModelList.get(i).getMoFileAttachmentsLists().toString().equals("[]"))
                                    {
                                        for (int f = 0; f < missionOrdersModelList.get(i).getMoFileAttachmentsLists().size(); f++)
                                        {
                                            MOFileAttachmentsList moFileAttachmentsList = new MOFileAttachmentsList();

                                            moFileAttachmentsList.setScreenerID(String.valueOf(UserAccount.employeeID));
                                            moFileAttachmentsList.setMissionOrderID(String.valueOf(missionOrdersModelList.get(i).getMissionOrderID()));
                                            moFileAttachmentsList.setMOAttachmentFilePath(missionOrdersModelList.get(i).getMoFileAttachmentsLists().get(f).getMOAttachmentFilePath());
                                            moFileAttachmentsList.setPreviousReport(missionOrdersModelList.get(i).getMoFileAttachmentsLists().get(f).getPreviousReport());

                                            if (missionOrdersModelList.get(i).getMoFileAttachmentsLists().get(f).getMOAttachmentFilePath() != null)
                                            {
                                                if (!missionOrdersModelList.get(i).getMoFileAttachmentsLists().get(f).getMOAttachmentFilePath().equals("null") &&
                                                    !missionOrdersModelList.get(i).getMoFileAttachmentsLists().get(f).getMOAttachmentFilePath().equals(""))
                                                {
                                                    String FileName = missionOrdersModelList.get(i).getMoFileAttachmentsLists().get(f).getMOAttachmentFilePath().
                                                            substring(missionOrdersModelList.get(i).getMoFileAttachmentsLists().get(f).getMOAttachmentFilePath().lastIndexOf("/") + 1);

                                                    moFileAttachmentsList.setFileName(FileName);

                                                    initDownloadAttachmentFileAndSave(moFileAttachmentsList);
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

                                //IF REPORTED..
                                initGetMissionOrderPDFReport(Integer.parseInt(missionOrdersModelList.get(i).getMissionOrderID()));
                            }
                        }
                        else
                        {
                            String Logs = "GET All SRI Mission Orders: Server Response Null";

                            Log.e(TAG, Logs);
                            volleyCatch.writeToFile(Logs);
                        }
                    }
                    else
                    {
                        String Logs = "GET All SRI Mission Orders Failed: " + convertingResponseError(response.errorBody());

                        Log.e(TAG, Logs);
                        volleyCatch.writeToFile(Logs);
                    }

                    noSynced = noSynced + 1;
                }
                @Override
                public void onFailure(@NonNull Call<List<MissionOrdersModel>> call, @NonNull Throwable t)
                {
                    String Logs = "GET All SRI Mission Orders Failure: " + t.getMessage();

                    Log.e(TAG, Logs);
                    volleyCatch.writeToFile(Logs);

                    if (runningThread)
                    {
                        noSynced = noSynced + 1;
                    }
                }
            });
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());

            noSynced = noSynced + 1;
        }
    }

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
                                //Download Via URL - Image to Bitmap
                                Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(SignaturePath).getContent());

                                initSaveSignatureToExternalDevice(bitmap, MissionOrderID);
                            }
                            catch (IOException e)
                            {
                                Log.e(TAG, "Download SRI Signature File error 1:" + e.toString());
                            }
                        }
                    });
                    thread.start();
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Download SRI Signature File error 2:" + e.toString());
        }
    }

    private void initSaveSignatureToExternalDevice(Bitmap imageToSave, String MissionOrderID)
    {
        try
        {
            if (ActivityCompat.checkSelfPermission(requireActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED

                    && ActivityCompat.checkSelfPermission(requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                Log.e(TAG, "Permission for saving signature to external device is not granted!");
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
                    //String SignaturePath      = SaveFolderName + "/" + SignatureName + "." + SignatureExtension;

                    File file = new File(SaveFolderName, SignatureName);

                    if (file.exists())
                    {
                        boolean isDeleted = file.delete();
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
                        Log.e(TAG, "Saving Signature Error 1: " + e.toString());
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, "Saving Signature Error 2: " + e.toString());
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Saving Signature Error 3: " + e.toString());
        }
    }

    private void initDownloadAttachmentFileAndSave(MOFileAttachmentsList moFileAttachmentsList)
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
                        Cursor cursor1 = RepositoryOnlineMOFileAttachmentsList.realAllData4(getContext(), moFileAttachmentsList);

                        if (cursor1.getCount() == 0)
                        {
                            RepositoryOnlineMOFileAttachmentsList.saveMOFileAttachment(getContext(), moFileAttachmentsList);
                        }

                        String fileURL = moFileAttachmentsList.getMOAttachmentFilePath();

                        String ExternalPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                                + "/SRI/MissionOrder/Attachment";

                        String sFileName   = fileURL.substring(fileURL.lastIndexOf("/") + 1 );
                        moFileAttachmentsList.setFileName(sFileName);

                        Log.e(TAG, "FILE NAME: " + sFileName);

                        File fExternalPath = new File(ExternalPath);

                        if (!fExternalPath.exists())
                        {
                            File wallpaperDirectory = new File(ExternalPath);

                            boolean isCreated = wallpaperDirectory.mkdirs();
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
                        Log.e(TAG, "Download SRI Attachment File error 1: " + e.toString() + "\n" +
                                "Mission Order No.: " + moFileAttachmentsList.getMissionOrderID());
                    }
                }
            });

            thread.start();
        }
        catch (Exception e)
        {
            Log.e(TAG, "Download SRI Attachment File error 2: " + e.toString());
        }
    }


    //Get Mission Order Report - PDF
    private void initGetMissionOrderPDFReport(Integer MissionOrderID)
    {
        try
        {
            APIClientInterface apiInterface = APIClient.getClient().create(APIClientInterface.class);

            Call<ReportPDFClass> getPDFReport = apiInterface.GETAllSeismicInspectionReports(UserAccount.employeeID, MissionOrderID);

            getPDFReport.enqueue(new Callback<ReportPDFClass>()
            {
                @Override
                public void onResponse(@NonNull Call<ReportPDFClass> call, @NonNull Response<ReportPDFClass> response)
                {
                    if (response.isSuccessful() && response.body() != null)
                    {
                        final ReportPDFClass reportPDFClass = response.body();

                        String filePath = reportPDFClass.getFilePath();

                        if (filePath != null && !filePath.equals("null") && !filePath.isEmpty())
                        {
                            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);

                            // Download and save the mission order PDF report
                            initDownloadSRIMissionOrderReportPDFAndSave(fileName, filePath);

                            // Update the report path of the mission order
                            RepositoryOnlineMissionOrders.updateReportPathOfMissionOrder(getContext(),
                                    UserAccount.UserAccountID, String.valueOf(MissionOrderID), filePath);

                            Log.e(TAG, "GETPDFReport Called - MISSION ORDER: " + MissionOrderID);
                        }
                    }
                    else
                    {
                        String errorLogs = response.errorBody() != null ? convertingResponseError(response.errorBody()) : "Unknown Error";
                        String logs = "PDF Reports Failed: " + errorLogs;
                        Log.e(TAG, logs);
                        volleyCatch.writeToFile(logs);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ReportPDFClass> call, @NonNull Throwable t)
                {
                    String logs = "Get PDF Reports Failure: " + t.getMessage();
                    Log.e(TAG, logs);
                    volleyCatch.writeToFile(logs);
                }
            });
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }


    public void initDownloadSRIMissionOrderReportPDFAndSave(final String fileName, final String fileURL)
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

                        //String FullFilePath = AttachmentPath + "/" + fileName;

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
                        Log.e(TAG, "Download SRI PDF File error 1: " + e.toString());
                    }
                }
            });

            thread.start();
        }
        catch (Exception e)
        {
            Log.e(TAG, "Download SRI PDF File error 2: " + e.toString());
        }
    }






    @Override
    public void onResume()
    {
        super.onResume();

        initSetMissionOrderList();
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


    //FILTER
    private void initFilterCategoryList(String text)
    {
        ArrayList<MissionOrdersClass> filteredList = new ArrayList<>();

        for (MissionOrdersClass item : missionOrdersModelList)
        {
            if (text.equalsIgnoreCase("all"))
            {
                filteredList.add(item);
            }
            else
            {
                if (item.getInspectionStatus().equalsIgnoreCase(text))
                {
                    filteredList.add(item);
                }
            }
        }

        rvAdapterMissionOrder.filterMissionOrderList(filteredList);
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

}
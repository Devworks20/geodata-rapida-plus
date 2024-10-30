package com.geodata.rapida.plus.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.geodata.rapida.plus.Adapter.RVAdapterMissionOrderOfEDI;
import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.Retrofit.Client.APIClient;
import com.geodata.rapida.plus.Retrofit.Interface.APIClientInterface;
import com.geodata.rapida.plus.Retrofit.Model.AssignedInspectorsListModel;
import com.geodata.rapida.plus.Retrofit.Model.BuildingInformationModel;
import com.geodata.rapida.plus.Retrofit.Model.BuildingOccupancyListModel;
import com.geodata.rapida.plus.Retrofit.Model.EarthquakeRVSReportModel;
import com.geodata.rapida.plus.Retrofit.Model.MOFileAttachmentsList;
import com.geodata.rapida.plus.Retrofit.Model.MissionOrdersModel;
import com.geodata.rapida.plus.Retrofit.Model.ReportPDFClass;
import com.geodata.rapida.plus.SQLite.Class.MissionOrderOfEDI;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryEarthquakeRVSReport;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryOnlineAssignedInspectors;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryOnlineBuildingInformation;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryOnlineBuildingOccupancyList;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryOnlineMOFileAttachmentsList;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryOnlineMissionOrders;
import com.geodata.rapida.plus.Tools.MyExceptionHandler;
import com.geodata.rapida.plus.Tools.Settings;
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
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EarthquakeDamageInspectionMOFragment extends Fragment
{
    private static final String TAG = EarthquakeDamageInspectionMOFragment.class.getSimpleName();

    View view;

    List<MissionOrderOfEDI> missionOrderOfEDIList;
    RecyclerView rv_list_earthquake_damage_list;
    RecyclerView.LayoutManager layoutManager;
    RVAdapterMissionOrderOfEDI rvAdapterMissionOrderOfEDI;

    Spinner spnr_Status;

    SwipeRefreshLayout srl_refresh;

    APIClientInterface apiInterface;

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
        view = inflater.inflate(R.layout.fragment_earthquake_damage_inspection_mo, container, false);

        initViews();

        return view;
    }

    private void initViews()
    {
        apiInterface = APIClient.getClient().create(APIClientInterface.class);

        missionOrderOfEDIList = new ArrayList<>();
        rv_list_earthquake_damage_list = view.findViewById(R.id.rv_list_earthquake_damage_list);
        rv_list_earthquake_damage_list.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        rv_list_earthquake_damage_list.setLayoutManager(layoutManager);
        rvAdapterMissionOrderOfEDI = new RVAdapterMissionOrderOfEDI(getContext(), missionOrderOfEDIList);
        rv_list_earthquake_damage_list.setAdapter(rvAdapterMissionOrderOfEDI);

        final String[] StringArrayNatureIncident   = getResources().getStringArray(R.array.status_array);
        ArrayAdapter<String> adapterNatureIncident = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, StringArrayNatureIncident);
        spnr_Status  = view.findViewById(R.id.spnr_Status);
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
            String Option = Settings.APPLICATION_NAME;

            Cursor cursor = RepositoryOnlineMissionOrders.realAllData3(getContext(), String.valueOf(UserAccount.employeeID));

            if (cursor.getCount()!=0)
            {
                missionOrderOfEDIList.clear();

                if (cursor.moveToFirst())
                {
                    int condition1 = (Option.equals("Emergency Damage Assessment") || Option.equals("Seismic Resiliency Survey")) ? 1 : 2;

                    do
                    {
                        String MissionOrderType = cursor.getString(cursor.getColumnIndex("MissionOrderType"));
                        String ReasonForScreening = cursor.getString(cursor.getColumnIndex("ReasonForScreening"));

                        boolean conditionMet = (condition1 == 1) ?
                                (Option.equals("Seismic Resiliency Survey") ? MissionOrderType.contains("SRI") : MissionOrderType.contains("EDI"))
                                : ReasonForScreening.contains(Option);

                        if (conditionMet)
                        {
                            MissionOrderOfEDI cValues = new MissionOrderOfEDI();

                            cValues.setApprovedBy(cursor.getString(cursor.getColumnIndex("ApprovedBy")));
                            cValues.setAssetID(cursor.getString(cursor.getColumnIndex("AssetID")));
                            cValues.setDateIssued(cursor.getString(cursor.getColumnIndex("DateIssued")));
                            cValues.setDateReported(cursor.getString(cursor.getColumnIndex("DateReported")) != null ?
                                    cursor.getString(cursor.getColumnIndex("DateReported")) : "");
                            cValues.setInspectionStatus(!cursor.getString(cursor.getColumnIndex("InspectionStatus")).equals("") ?
                                    cursor.getString(cursor.getColumnIndex("InspectionStatus")) : "Pending");
                            cValues.setMissionOrderID(cursor.getString(cursor.getColumnIndex("MissionOrderID")));
                            cValues.setMissionOrderNo(cursor.getString(cursor.getColumnIndex("MissionOrderNo")));
                            cValues.setReasonForScreening(cursor.getString(cursor.getColumnIndex("ReasonForScreening")));
                            cValues.setScreeningType(cursor.getString(cursor.getColumnIndex("ScreeningType")));
                            cValues.setSeismicityRegion(cursor.getString(cursor.getColumnIndex("Seismicity")));
                            cValues.setScreeningSchedule(cursor.getString(cursor.getColumnIndex("ScreeningSchedule")));
                            cValues.setBuildingName(cursor.getString(cursor.getColumnIndex("BuildingName")));
                            cValues.setIsActive(cursor.getString(cursor.getColumnIndex("isActive")));
                            cValues.setDtAdded(cursor.getString(cursor.getColumnIndex("dtAdded")));

                            missionOrderOfEDIList.add(cValues);
                        }
                    }
                    while (cursor.moveToNext());

                    String selectedStatus = spnr_Status.getSelectedItem().toString();
                    initFilterCategoryList((selectedStatus != null && !selectedStatus.equals("All")) ? selectedStatus : "");
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
                if (haveNetworkConnection(requireContext()))
                {
                    if (!runningThread)
                    {
                        Toast.makeText(getContext(), "Please wait while refreshing.", Toast.LENGTH_SHORT).show();

                        timer = new Timer();

                        mSystemStartTime = System.currentTimeMillis();
                        initTimerAPIs();
                    }
                    else
                    {
                        Toast.makeText(getContext(), "Please wait while refreshing.", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getContext(), "Internet connection required.", Toast.LENGTH_SHORT).show();

                    srl_refresh.setRefreshing(false);
                }
            }
        });

        spnr_Status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString();

                if(selectedItem.equals("Pending") || selectedItem.equals("Complete"))
                {
                    initFilterCategoryList(selectedItem);
                }
                else
                {
                    initFilterCategoryList("");
                }

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
                                long totalRequestTime = System.currentTimeMillis() - mSystemStartTime;
                                Log.e(TAG, "Total Time: " + totalRequestTime);

                                if (noSynced == 2)
                                {
                                    runningThread = false;
                                    noSynced = 0;
                                    srl_refresh.setRefreshing(false);
                                    timer.cancel();

                                    Toast.makeText(getActivity(), "Successfully Updated.", Toast.LENGTH_SHORT).show();

                                    initSetMissionOrderList();
                                }

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
            initGetAllEDIMissionOrders();

            initGetAllEDIRVSReport();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initGetAllEDIMissionOrders()
    {
        try
        {
            if (haveNetworkConnection(requireContext()))
            {
                Log.e(TAG, "GET All EDI Mission Orders");

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

                                int countSaved = 0;

                                for (int i = 0; i < missionOrdersModelList.size(); i++)
                                {
                                    countSaved = countSaved + 1;

                                    //region GET MISSION ORDER
                                    MissionOrdersModel missionOrdersModel = new MissionOrdersModel();

                                    missionOrdersModel.setMissionOrderType("EDI");
                                    missionOrdersModel.setScreenerID(String.valueOf(UserAccount.employeeID));

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

                                    if (cursor != null && cursor.getCount() != 0)
                                    {
                                        if (cursor.moveToFirst())
                                        {
                                            RepositoryOnlineMissionOrders.updateTheMissionOrderStatus(getContext(), missionOrdersModel);
                                        }
                                    }
                                    else
                                    {
                                        Log.e(TAG, "MISSION ORDER SAVED");

                                        Date now = new Date(System.currentTimeMillis());
                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
                                        String dtAdded = dateFormat.format(now);
                                        missionOrdersModel.setDtAdded(dtAdded);

                                        RepositoryOnlineMissionOrders.saveMissionOrders(getContext(), missionOrdersModel);
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

                                    if (cursor2 != null && cursor2.getCount() != 0)
                                    {
                                        RepositoryOnlineBuildingInformation.updateBuildingInformation(getContext(), buildingInformationModel);
                                    }
                                    else
                                    {
                                        RepositoryOnlineBuildingInformation.saveBuildingInformation(getContext(), buildingInformationModel);
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
                                                getContext(),
                                                buildingOccupancyListModel.getScreenerID(),
                                                buildingOccupancyListModel.getMissionOrderID(),
                                                buildingOccupancyListModel.getOccupancy()
                                        );

                                        if (cursor1 != null && cursor1.getCount() == 0)
                                        {
                                            RepositoryOnlineBuildingOccupancyList.saveBuildingOccupancyList(getContext(), buildingOccupancyListModel);
                                        }
                                    }
                                    //endregion

                                    //region GET Assigned Inspection
                                    try
                                    {
                                        List<AssignedInspectorsListModel> assignedInspectorsList = missionOrdersModelList.get(i).getAssignedInspectorsListModel();

                                        if (assignedInspectorsList != null && !assignedInspectorsList.isEmpty())
                                        {
                                            for (int f = 0; f < assignedInspectorsList.size(); f++)
                                            {
                                                AssignedInspectorsListModel assignedInspectorsClass = assignedInspectorsList.get(f);

                                                if (assignedInspectorsClass == null)
                                                {
                                                    Log.e(TAG, "AssignedInspectorsListModel is null at index " + f);
                                                    continue; // Skip if AssignedInspectorsListModel is null
                                                }

                                                assignedInspectorsClass.setScreenerID(String.valueOf(UserAccount.employeeID));
                                                assignedInspectorsClass.setMissionOrderID(String.valueOf(missionOrdersModelList.get(i).getMissionOrderID()));

                                                String inspector = assignedInspectorsClass.getInspector();

                                                if (inspector != null && !inspector.isEmpty())
                                                {
                                                    Cursor cursor1 = RepositoryOnlineAssignedInspectors.realAllData4(getContext(),
                                                            assignedInspectorsClass.getMissionOrderID(), inspector);

                                                    if (cursor1 != null && cursor1.getCount() != 0)
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
                                        }
                                    }
                                    catch (Exception e)
                                    {
                                        Log.e(TAG, "An error occurred: " + e.getMessage());
                                    }
                                    //endregion

                                    //region GET Previous / Attachment File of Mission Order
                                    try
                                    {
                                        List<MOFileAttachmentsList> attachments = missionOrdersModelList.get(i).getMoFileAttachmentsLists();

                                        if (attachments == null || attachments.isEmpty())
                                        {
                                            Log.d(TAG, "No attachments found.");
                                            return; // Early return if no attachments
                                        }

                                        Log.d(TAG, "Attachments found: " + attachments.size());

                                        for (int f = 0; f < attachments.size(); f++)
                                        {
                                            MOFileAttachmentsList moFileAttachmentsList = attachments.get(f);

                                            if (moFileAttachmentsList == null)
                                            {
                                                Log.e(TAG, "MOFileAttachmentsList is null at index " + f);
                                                continue; // Skip if MOFileAttachmentsList is null
                                            }

                                            moFileAttachmentsList.setScreenerID(String.valueOf(UserAccount.employeeID));
                                            moFileAttachmentsList.setMissionOrderID(String.valueOf(missionOrdersModelList.get(i).getMissionOrderID()));

                                            String attachmentFilePath = moFileAttachmentsList.getMOAttachmentFilePath();

                                            if (attachmentFilePath != null && !attachmentFilePath.isEmpty() && !attachmentFilePath.equals("null"))
                                            {
                                                String fileName = attachmentFilePath.substring(attachmentFilePath.lastIndexOf("/") + 1);
                                                moFileAttachmentsList.setFileName(fileName);

                                                initDownloadAttachmentFileAndSave(moFileAttachmentsList);
                                            }
                                        }
                                    }
                                    catch (Exception e)
                                    {
                                        Log.e(TAG, "An error occurred: " + e.getMessage());
                                    }
                                    //endregion

                                    //IF REPORTED..
                                    initGetEarthquakeDESAorRESAReports(
                                            missionOrdersModelList.get(i).getReasonForScreening(),
                                            Integer.parseInt(missionOrdersModelList.get(i).getMissionOrderID()));
                                }

                                if (missionOrdersModelList.size() == countSaved)
                                {
                                    noSynced = noSynced + 1;
                                }
                            }
                            else
                            {
                                String Logs = "GET All EDI Mission Orders: Server Response Null";

                                Log.e(TAG, Logs);
                                volleyCatch.writeToFile(Logs);

                                noSynced = noSynced + 1;
                            }
                        }
                        else
                        {
                            String Logs = "GET All EDI Mission Orders Failed: " + convertingResponseError(response.errorBody());

                            Log.e(TAG, Logs);
                            volleyCatch.writeToFile(Logs);

                            noSynced = noSynced + 1;
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<List<MissionOrdersModel>> call, @NonNull Throwable t)
                    {
                        String Logs = "GET All EDI Mission Orders Failure: " + t.getMessage();

                        Log.e(TAG, Logs);

                        if(runningThread)
                        {
                            noSynced = noSynced + 1;
                        }
                    }
                });
            }
            else
            {
                noSynced = noSynced + 1;
            }
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
                                Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(SignaturePath).getContent());

                                initSaveSignatureToExternalDevice(bitmap, MissionOrderID);
                            }
                            catch (IOException e)
                            {
                                Log.e(TAG, "Download EDI Signature File error: " + e.toString());
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
            if (ActivityCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED

                    && ActivityCompat.checkSelfPermission(requireContext(),
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
                        boolean isCreated = file.delete();
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
                        Log.e(TAG, "Saving Signature 1: " + e.toString());
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, "Saving Signature 2: " + e.toString());
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Saving Signature 3: " + e.toString());
        }
    }

    public void initDownloadAttachmentFileAndSave(MOFileAttachmentsList moFileAttachmentsList)
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
            Log.e(TAG, "Download EDI Attachment File error : " + e.toString());
        }
    }

    private void initGetEarthquakeDESAorRESAReports(String ReasonForScreening, Integer MissionOrderID)
    {
        try
        {
            Log.e(TAG, "GET PDF Report Called");

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
                        if (response.isSuccessful() && response.body() != null)
                        {
                            final ReportPDFClass reportPDFClass = response.body();

                            if (reportPDFClass.getFilePath() != null && !reportPDFClass.getFilePath().isEmpty() &&
                               !reportPDFClass.getFilePath().equals("null"))
                            {
                                String FileName = reportPDFClass.getFilePath()
                                        .substring(reportPDFClass.getFilePath().lastIndexOf("/") + 1);

                                RepositoryOnlineMissionOrders.updateReportPathOfMissionOrder(getContext(),
                                        UserAccount.UserAccountID, String.valueOf(MissionOrderID), reportPDFClass.getFilePath());

                                initSaveEarthquakeDESAorRESAReport(finalOption, FileName, reportPDFClass.getFilePath());
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
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }


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
                        String AttachmentPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SRI/" + Option;

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
                        Log.e(TAG, "Download EDI PDF File error : " + e.toString());
                    }
                }
            });

            thread.start();
        }
        catch (Exception e)
        {
            Log.e(TAG, "Download EDI PDF File error : " + e.toString());
        }
    }


    private void initGetAllEDIRVSReport()
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
                    if (response.isSuccessful() && response.body() != null)
                    {
                        final List<EarthquakeRVSReportModel> earthquakeRVSReportModelList = response.body();

                        for (EarthquakeRVSReportModel model : earthquakeRVSReportModelList)
                        {
                            String EarthquakeRVSReportID = model.getEarthquakeRVSReportID();

                            if (EarthquakeRVSReportID != null)
                            {
                                Cursor cursor = RepositoryEarthquakeRVSReport.realAllData3(getContext(), EarthquakeRVSReportID);

                                if (cursor != null && cursor.getCount() != 0)
                                {
                                    if (cursor.moveToFirst())
                                    {
                                        RepositoryEarthquakeRVSReport.updateEarthquakeRVSReport(getContext(), EarthquakeRVSReportID, model);
                                    }
                                }
                                else
                                {
                                    RepositoryEarthquakeRVSReport.saveEarthquakeRVSReport(getContext(), model);
                                }

                                if (model.getEarthquakeRVSReportPdfPath() != null && !model.getEarthquakeRVSReportPdfPath().isEmpty())
                                {
                                    String FileName = model.getEarthquakeRVSReportPdfPath()
                                            .substring(model.getEarthquakeRVSReportPdfPath().lastIndexOf("/") + 1);

                                    initDownloadEDIRVSReport_PDF(FileName, model.getEarthquakeRVSReportPdfPath());
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

                @Override
                public void onFailure(@NonNull Call<List<EarthquakeRVSReportModel>> call, @NonNull Throwable t)
                {
                    String Logs = "GET All EDI Report PDF Failure: " + t.getMessage();
                    Log.e(TAG, Logs);
                    volleyCatch.writeToFile(Logs);

                    if (runningThread)
                    {
                        noSynced++;
                    }
                }
            });
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
            noSynced++;
        }
    }



    private void initDownloadEDIRVSReport_PDF(final String fileName, final String fileURL)
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
        ArrayList<MissionOrderOfEDI> filteredList = new ArrayList<>();

        for (MissionOrderOfEDI item : missionOrderOfEDIList)
        {
            if (item.getInspectionStatus().toLowerCase().contains(text.toLowerCase()))
            {
                filteredList.add(item);
            }
        }
        rvAdapterMissionOrderOfEDI.filterMissionOrderList(filteredList);
    }

    //Check Network
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
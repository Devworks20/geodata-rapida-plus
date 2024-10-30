package com.geodata.rapida.plus.Fragment;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.Retrofit.Client.APIClient;
import com.geodata.rapida.plus.Retrofit.Interface.APIClientInterface;
import com.geodata.rapida.plus.SQLite.Main.SQLiteDbContext;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryFinalBuildingScores;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryOnlineBuildingInformation;
import com.geodata.rapida.plus.Tools.MyExceptionHandler;
import com.geodata.rapida.plus.Tools.UserAccount;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuildingInformationFragment extends Fragment
{
    private static final String TAG = BuildingInformationFragment.class.getSimpleName();

    TextView tv_BuildingName, tv_address, tv_longitudeX, tv_latitudeY, tv_altitudeY, tv_DateBuilt, tv_BuildingAge, tv_NoOfStories, tv_Occupancy,
             tv_SoilType, tv_NoOfPersons, tv_BuildingType, tv_TotalFloorArea, tv_Height, tv_OpenSpace,
             tv_AverageAreaPerFlr, tv_costPer, tv_NoOfUnits, tv_BldgContactPerson, tv_BldgContactNo, tv_BldgEmail,
            tv_FaultDistance, tv_NearestFault;

    View view;

    String TabType, MissionOrderID, SeismicityRegion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(getActivity())); //Getting Crash - Restart

        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_building_information, container, false);

        initViews();

        return view;
    }

    private void initViews()
    {
        try
        {
            Bundle extras = requireActivity().getIntent().getExtras();

            if(extras != null)
            {
                TabType          = extras.getString("TabType");
                MissionOrderID   = extras.getString("MissionOrderID");
                SeismicityRegion = extras.getString("SeismicityRegion");

                Log.e(TAG, "TabType: " + TabType);
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        tv_BuildingName      = view.findViewById(R.id.tv_BuildingName);
        tv_address           = view.findViewById(R.id.tv_address);

        tv_longitudeX  = view.findViewById(R.id.tv_longitudeX);
        tv_latitudeY  = view.findViewById(R.id.tv_latitudeY);
        tv_altitudeY  = view.findViewById(R.id.tv_altitudeY);

        tv_DateBuilt         = view.findViewById(R.id.tv_DateBuilt);
        tv_BuildingAge       = view.findViewById(R.id.tv_BuildingAge);
        tv_NoOfStories       = view.findViewById(R.id.tv_NoOfStories);
        tv_Occupancy         = view.findViewById(R.id.tv_Occupancy);
        tv_SoilType          = view.findViewById(R.id.tv_SoilType);
        tv_NoOfPersons       = view.findViewById(R.id.tv_NoOfPersons);
        tv_BuildingType      = view.findViewById(R.id.tv_BuildingType);
        tv_TotalFloorArea    = view.findViewById(R.id.tv_TotalFloorArea);
        tv_Height            = view.findViewById(R.id.tv_Height);
        tv_OpenSpace         = view.findViewById(R.id.tv_OpenSpace);
        tv_AverageAreaPerFlr = view.findViewById(R.id.tv_AverageAreaPerFlr);
        tv_costPer           = view.findViewById(R.id.tv_costPer);
        tv_NoOfUnits         = view.findViewById(R.id.tv_NoOfUnits);
        tv_BldgContactPerson = view.findViewById(R.id.tv_BldgContactPerson);
        tv_BldgContactNo     = view.findViewById(R.id.tv_BldgContactNo);
        tv_BldgEmail         = view.findViewById(R.id.tv_BldgEmail);
        tv_FaultDistance     = view.findViewById(R.id.tv_FaultDistance);
        tv_NearestFault      = view.findViewById(R.id.tv_NearestFault);

        initSetDetails();
    }

    @SuppressLint("SetTextI18n")
    private void initSetDetails()
    {
        try
        {
            String ScreenerID = String.valueOf(UserAccount.employeeID);

            Cursor cursor = RepositoryOnlineBuildingInformation.realAllData2(getContext(), ScreenerID, MissionOrderID);

            if (cursor.getCount() != 0)
            {
                if (cursor.moveToFirst())
                {
                    tv_BuildingName.setText(cursor.getString(cursor.getColumnIndex("BuildingName")));
                    tv_address.setText(cursor.getString(cursor.getColumnIndex("Location")));

                    tv_longitudeX.setText(cursor.getString(cursor.getColumnIndex("Long")));
                    tv_latitudeY.setText(cursor.getString(cursor.getColumnIndex("Lat")));

                    if (!cursor.getString(cursor.getColumnIndex("Altitude")).trim().isEmpty() ||
                        cursor.getString(cursor.getColumnIndex("Altitude")).trim() != null)
                    {
                       // tv_altitudeY.setText(cursor.getString(cursor.getColumnIndex("Altitude")) + " meters");
                    }

                    tv_altitudeY.setText("14 meters");

                    tv_DateBuilt.setText(cursor.getString(cursor.getColumnIndex("DateFinished")));
                    tv_BuildingAge.setText(cursor.getString(cursor.getColumnIndex("Age")));
                    tv_NoOfStories.setText(cursor.getString(cursor.getColumnIndex("NoOfFloors")));
                    tv_Occupancy.setText(cursor.getString(cursor.getColumnIndex("Occupancies")));
                    tv_SoilType.setText(cursor.getString(cursor.getColumnIndex("BuildingSoilType")));

                    tv_NoOfPersons.setText(cursor.getString(cursor.getColumnIndex("BuildingNoOfPersons")));
                    tv_BuildingType.setText(cursor.getString(cursor.getColumnIndex("StructureType")));
                    tv_TotalFloorArea.setText(cursor.getString(cursor.getColumnIndex("FloorArea")));
                    tv_Height.setText(cursor.getString(cursor.getColumnIndex("Height")));
                    tv_OpenSpace.setText(cursor.getString(cursor.getColumnIndex("OpenSpace")));
                    tv_AverageAreaPerFlr.setText(cursor.getString(cursor.getColumnIndex("AveAreaPerFloor")));
                    tv_costPer.setText(cursor.getString(cursor.getColumnIndex("CostPerSqm")));
                    tv_NoOfUnits.setText(cursor.getString(cursor.getColumnIndex("NoOfUnits")));
                    tv_BldgContactPerson.setText(cursor.getString(cursor.getColumnIndex("OwnerName")));
                    tv_BldgContactNo.setText(cursor.getString(cursor.getColumnIndex("ContactNo")));
                    tv_BldgEmail.setText(cursor.getString(cursor.getColumnIndex("Email")));

                    tv_FaultDistance.setText(cursor.getString(cursor.getColumnIndex("FaultDistance")));
                    tv_NearestFault.setText(cursor.getString(cursor.getColumnIndex("NearestFault")));
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }
}
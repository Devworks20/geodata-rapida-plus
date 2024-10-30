package com.geodata.rapida.plus.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.geodata.rapida.plus.Adapter.RVAdapterBuildingScore;
import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.SQLite.Class.FinalBuildingScoresClass;
import com.geodata.rapida.plus.SQLite.Class.ScoringBuildingsClass;
import com.geodata.rapida.plus.SQLite.Class.TempBuildingScoresClass;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryBuildingScores;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryBuildingType;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryFinalBuildingScores;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryOnlineBuildingInformation;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryTempBuildingScores;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryUserAccount;
import com.geodata.rapida.plus.Tools.UserAccount;

import java.util.ArrayList;
import java.util.List;

public class BuildingScoreActivity extends AppCompatActivity
{
    private static final String TAG = BuildingScoreActivity.class.getSimpleName();

    String MissionOrderID, Category, BuildingType, NoOfStories, sBuildingType1="", sBuildingType2="",
           sBuildingTypeOutput1="", sBuildingTypeOutput2="", SoilType, DateBuilt, SelectedBuildingType;

    ImageView iv_back;
    TextView tv_BuildingType, tv_BuildingType2, tv_finalScore1, tv_finalScore2;

    LinearLayout ll_final_score_failed;

    Button btn_apply_final_score;

    List<ScoringBuildingsClass> scoringBuildingsClassList;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RVAdapterBuildingScore rvAdapterBuildingScore;

    Boolean isPreCode1 = false, isPostCode1 = false,
            isPreCode2 = false, isPostCode2 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_score);

        initViews();

        initListeners();
    }

    private void initViews()
    {
        try
        {
            iv_back          = findViewById(R.id.iv_back);
            tv_BuildingType  = findViewById(R.id.tv_BuildingType);
            tv_BuildingType2 = findViewById(R.id.tv_BuildingType2);
            tv_finalScore1   = findViewById(R.id.tv_finalScore1);
            tv_finalScore2   = findViewById(R.id.tv_finalScore2);

            ll_final_score_failed = findViewById(R.id.ll_final_score_failed);

            recyclerView     = findViewById(R.id.rv_reports);


            btn_apply_final_score  = findViewById(R.id.btn_apply_final_score);

            Bundle extras = getIntent().getExtras();

            if(extras != null)
            {
                MissionOrderID       = extras.getString("MissionOrderID");
                Category             = extras.getString("Category");
                SoilType             = extras.getString("SoilType");
                DateBuilt            = extras.getString("DateBuilt");
                SelectedBuildingType = extras.getString("SelectedBuildingType");

                switch (Category.toLowerCase())
                {
                    case "high":
                        Category = "High Seismicity";
                        break;
                    case "moderate":
                    case "medium":
                        Category = "Moderate Seismicity";
                        break;
                    case "very low":
                    case "low":
                        Category = "Low Seismicity";
                        break;
                }

                BuildingType    = extras.getString("BuildingType");
                NoOfStories     = extras.getString("NoOfStories");

                if (BuildingType.contains(","))
                {
                    Log.e(TAG, "BuildingType: " + BuildingType);

                    String[] BuildingTypeFinal =  BuildingType.split(",");

                    tv_BuildingType.setText(BuildingTypeFinal[0]);
                    tv_BuildingType2.setText(BuildingTypeFinal[1]);

                    sBuildingType1 = BuildingTypeFinal[0];
                    sBuildingType2 = BuildingTypeFinal[1];

                    initFinalOutputBuildingType(sBuildingType1, sBuildingType2);
                }
                else
                {
                    tv_BuildingType.setText(BuildingType);
                    sBuildingType1 = BuildingType;

                    initFinalOutputBuildingType(sBuildingType1, "");

                    tv_BuildingType2.setText("");
                    tv_finalScore2.setVisibility(View.INVISIBLE);
                }

                initSetBuilding();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initSetBuilding()
    {
        try
        {
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);

            scoringBuildingsClassList = new ArrayList<>();

            rvAdapterBuildingScore  = new RVAdapterBuildingScore(this, MissionOrderID, NoOfStories,
                    tv_finalScore1, tv_finalScore2, scoringBuildingsClassList, ll_final_score_failed);

            recyclerView.setAdapter(rvAdapterBuildingScore);

            if (SelectedBuildingType != null && SelectedBuildingType.contains(","))
            {
                try
                {
                    //Building Type 1
                    String[] BuildingTypeFinal =  SelectedBuildingType.split(",");

                    String SelectedBuildingType1 = BuildingTypeFinal[0];
                    String SelectedBuildingType2 = BuildingTypeFinal[1];

                    if (DateBuilt != null && DateBuilt.contains(","))
                    {
                        String[] DateBuiltFinal =  DateBuilt.split(",");
                        String DateBuiltOutput = DateBuiltFinal[1].replace(" ", "");
                        int iDateBuilt = Integer.parseInt(DateBuiltOutput);

                        //Building Type 1
                        Cursor cursor = RepositoryBuildingType.realAllData3(getApplicationContext(), SelectedBuildingType1);
                        if (cursor.getCount() != 0)
                        {
                            if (cursor.moveToFirst())
                            {
                                int iPreCode = Integer.parseInt(cursor.getString(cursor.getColumnIndex("PreCode")) != null ?
                                        cursor.getString(cursor.getColumnIndex("PreCode")):"0");

                                int iPostBenchmark = Integer.parseInt(cursor.getString(cursor.getColumnIndex("PostBenchmark")) != null ?
                                        cursor.getString(cursor.getColumnIndex("PostBenchmark")):"0");

                                if (iDateBuilt < iPreCode && iPreCode != 0)
                                {
                                    isPreCode1 = true;
                                }
                                else if (iDateBuilt > iPostBenchmark && iPostBenchmark != 0)
                                {
                                    isPostCode1 = true;
                                }
                            }
                        }

                        //Building Type2
                        Cursor cursor2 = RepositoryBuildingType.realAllData3(getApplicationContext(), SelectedBuildingType2);
                        if (cursor2.getCount() != 0)
                        {
                            if (cursor2.moveToFirst())
                            {
                                int iPreCode = Integer.parseInt(cursor2.getString(cursor2.getColumnIndex("PreCode")) != null ?
                                        cursor2.getString(cursor2.getColumnIndex("PreCode")):"0");

                                int iPostBenchmark = Integer.parseInt(cursor2.getString(cursor2.getColumnIndex("PostBenchmark")) != null ?
                                        cursor2.getString(cursor2.getColumnIndex("PostBenchmark")):"0");

                                if (iDateBuilt < iPreCode && iPreCode != 0)
                                {
                                    isPreCode2 = true;
                                }
                                else if (iDateBuilt > iPostBenchmark && iPostBenchmark != 0)
                                {
                                    isPostCode2 = true;
                                }
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, e.toString());
                }
            }
            else
            {
                if (SelectedBuildingType != null)
                {
                    if (DateBuilt != null && DateBuilt.contains(","))
                    {
                        try
                        {
                            String[] DateBuiltFinal =  DateBuilt.split(",");
                            String DateBuiltOutput = DateBuiltFinal[1].replace(" ", "");
                            int iDateBuilt = Integer.parseInt(DateBuiltOutput);

                            //Building Type 1
                            Cursor cursor = RepositoryBuildingType.realAllData3(getApplicationContext(), SelectedBuildingType);
                            if (cursor.getCount() != 0)
                            {
                                if (cursor.moveToFirst())
                                {
                                    int iPreCode1 = Integer.parseInt(cursor.getString(cursor.getColumnIndex("PreCode")) != null ?
                                                                     cursor.getString(cursor.getColumnIndex("PreCode")):"0");

                                    Log.e(TAG, "iDateBuilt: "+ iDateBuilt + "-iPreCode1: " + iPreCode1);

                                    int iPostBenchmark1 = Integer.parseInt(cursor.getString(cursor.getColumnIndex("PostBenchmark")) != null ?
                                                                           cursor.getString(cursor.getColumnIndex("PostBenchmark")):"0");

                                    if (iDateBuilt < iPreCode1 && iPreCode1 != 0)
                                    {
                                        isPreCode1 = true;
                                    }
                                    else if (iDateBuilt > iPostBenchmark1 && iPostBenchmark1 != 0)
                                    {
                                        isPostCode1 = true;
                                    }
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            Log.e(TAG, e.toString());
                        }
                    }
                }
            }

            Cursor cursor1 = RepositoryTempBuildingScores.selectBuildingScores3(getApplicationContext(),
                    UserAccount.UserAccountID, MissionOrderID, Category, sBuildingType1, sBuildingType2);

            if (cursor1.getCount()!= 0)
            {
                Log.e(TAG, "Default 1 - NoOfStories: " + NoOfStories + "/Category: " + Category +
                        "/sBuildingType1: " + sBuildingType1 + "/sBuildingType2: " + sBuildingType2);

                if (cursor1.moveToFirst())
                {
                    Log.e(TAG, "THIS IS WORKING.. aaa");

                    do
                    {
                        ScoringBuildingsClass cValues = new ScoringBuildingsClass();

                        String Modifiers = cursor1.getString(cursor1.getColumnIndex("Modifiers"));
                        cValues.setModifiers(Modifiers);

                        //Building Type 1
                        if (cursor1.getString(cursor1.getColumnIndex("isActive1")) == null)
                        {
                            Cursor cursor2 = RepositoryBuildingScores.selectBuildingScores3(getApplicationContext(),
                                    Category, sBuildingType1, Modifiers);

                            if (cursor2.getCount()!=0)
                            {
                                if (cursor2.moveToFirst())
                                {
                                    String BuildingID1    = cursor2.getString(cursor2.getColumnIndex("ID"));
                                    String BuildingScore1 = cursor2.getString(cursor2.getColumnIndex("Scores"));

                                    if (Modifiers.equalsIgnoreCase("Basic Score"))
                                    {
                                        cValues.setIsActive1("1");
                                    }
                                    else
                                    {
                                        cValues.setIsActive1("0");

                                        if (NoOfStories != null && !NoOfStories.equalsIgnoreCase(""))
                                        {
                                            int iNoOfStories = Integer.parseInt(NoOfStories);

                                            if ((iNoOfStories == 4 || iNoOfStories == 5 || iNoOfStories == 6 ||
                                                 iNoOfStories == 7) && Modifiers.equalsIgnoreCase("Mid Rise (4 to 7 stories)"))
                                            {
                                                Log.e(TAG, "NO. 1 SELECTED");

                                                cValues.setIsActive1("1");
                                            }
                                            else if (iNoOfStories > 7 && Modifiers.equalsIgnoreCase("High Rise (> 7 stories)"))
                                            {
                                                Log.e(TAG, "NO. 2 SELECTED");

                                                cValues.setIsActive1("1");
                                            }
                                        }

                                        if (SoilType != null && !SoilType.equalsIgnoreCase(""))
                                        {
                                            if (SoilType.contains("Dense Soil") && Modifiers.equalsIgnoreCase("Soil Type C"))
                                            {
                                                cValues.setIsActive1("1");
                                            }
                                            else  if (SoilType.contains("Stiff Soil") && Modifiers.equalsIgnoreCase("Soil Type D"))
                                            {
                                                cValues.setIsActive1("1");
                                            }
                                            else  if (SoilType.contains("Soft Soil") && Modifiers.equalsIgnoreCase("Soil Type E"))
                                            {
                                                cValues.setIsActive1("1");
                                            }
                                        }

                                        if (isPreCode1 && Modifiers.equalsIgnoreCase("Pre-Code"))
                                        {
                                            cValues.setIsActive1("1");
                                        }
                                        else if (isPostCode1 && Modifiers.equalsIgnoreCase("Post-Benchmark"))
                                        {
                                            cValues.setIsActive1("1");
                                        }
                                    }
                                    cValues.setBuildingID1(BuildingID1);
                                    cValues.setBuildingScore1(BuildingScore1);
                                }
                            }

                            cursor2.close();
                        }
                        else
                        {
                            Log.e(TAG, "THIS IS WORKING... BBB");

                            String isActive1      = cursor1.getString(cursor1.getColumnIndex("isActive1"));
                            String BuildingID1    = cursor1.getString(cursor1.getColumnIndex("BuildingID1"));
                            String BuildingScore1 = cursor1.getString(cursor1.getColumnIndex("BuildingScore1"));

                            cValues.setIsActive1(isActive1);
                            cValues.setBuildingID1(BuildingID1);
                            cValues.setBuildingScore1(BuildingScore1);
                        }

                        //Building Type 2
                        if (!sBuildingType2.equalsIgnoreCase(""))
                        {
                            if (cursor1.getString(cursor1.getColumnIndex("isActive2")) == null)
                            {
                                Cursor cursor2 = RepositoryBuildingScores.selectBuildingScores3(getApplicationContext(),
                                        Category, sBuildingType2, Modifiers);

                                if (cursor2.getCount()!=0)
                                {
                                    if (cursor2.moveToFirst())
                                    {
                                        String BuildingID2    = cursor2.getString(cursor2.getColumnIndex("ID"));
                                        String BuildingScore2 = cursor2.getString(cursor2.getColumnIndex("Scores"));

                                        if (Modifiers.equalsIgnoreCase("Basic Score"))
                                        {
                                            cValues.setIsActive2("1");
                                        }
                                        else
                                        {
                                            cValues.setIsActive2("0");

                                            if (NoOfStories != null && !NoOfStories.equalsIgnoreCase(""))
                                            {
                                                int iNoOfStories = Integer.parseInt(NoOfStories);

                                                if ((iNoOfStories == 4 || iNoOfStories == 5 || iNoOfStories == 6 ||
                                                     iNoOfStories == 7) && Modifiers.equalsIgnoreCase("Mid Rise (4 to 7 stories)"))
                                                {
                                                    Log.e(TAG, "NO. 1 SELECTED");

                                                    cValues.setIsActive2("1");
                                                }
                                                else if (iNoOfStories > 7 && Modifiers.equalsIgnoreCase("High Rise (> 7 stories)"))
                                                {
                                                    Log.e(TAG, "NO. 2 SELECTED");

                                                    cValues.setIsActive2("1");
                                                }
                                            }

                                            if (SoilType != null && !SoilType.equalsIgnoreCase(""))
                                            {
                                                if (SoilType.contains("Dense Soil") && Modifiers.equalsIgnoreCase("Soil Type C"))
                                                {
                                                    cValues.setIsActive2("1");
                                                }
                                                else  if (SoilType.contains("Stiff Soil") && Modifiers.equalsIgnoreCase("Soil Type D"))
                                                {
                                                    cValues.setIsActive2("1");
                                                }
                                                else  if (SoilType.contains("Soft Soil") && Modifiers.equalsIgnoreCase("Soil Type E"))
                                                {
                                                    cValues.setIsActive2("1");
                                                }
                                            }

                                            if (isPreCode2 && Modifiers.equalsIgnoreCase("Pre-Code"))
                                            {
                                                cValues.setIsActive2("1");
                                            }
                                            else if (isPostCode2 && Modifiers.equalsIgnoreCase("Post-Benchmark"))
                                            {
                                                cValues.setIsActive2("1");
                                            }
                                        }
                                        cValues.setBuildingID2(BuildingID2);
                                        cValues.setBuildingScore2(BuildingScore2);
                                    }
                                }

                                cursor2.close();
                            }
                            else
                            {
                                String isActive2      = cursor1.getString(cursor1.getColumnIndex("isActive2"));
                                String BuildingID2    = cursor1.getString(cursor1.getColumnIndex("BuildingID2"));
                                String BuildingScore2 = cursor1.getString(cursor1.getColumnIndex("BuildingScore2"));

                                cValues.setIsActive2(isActive2);
                                cValues.setBuildingID2(BuildingID2);
                                cValues.setBuildingScore2(BuildingScore2);
                            }
                        }
                        else
                        {
                            cValues.setIsActive2("0");
                            cValues.setBuildingID2("0");
                            cValues.setBuildingScore2("0");
                        }

                        scoringBuildingsClassList.add(cValues);
                    }
                    while (cursor1.moveToNext());

                    rvAdapterBuildingScore.notifyDataSetChanged();
                }
            }
            else
            {
                Log.e(TAG, "Default 2 - NoOfStories: " + NoOfStories + "/Category: " + Category +
                                "/sBuildingType1: " + sBuildingType1 + "/sBuildingType2: " + sBuildingType2);

                Cursor cursor = RepositoryBuildingScores.selectBuildingScores(this, Category, sBuildingType1, sBuildingType2);

                if (cursor.getCount()!=0)
                {
                    if (cursor.moveToFirst())
                    {
                        do
                        {
                            String isActive2 = "0", BuildingID2 = "", BuildingScore2= "0";

                            String Modifiers = cursor.getString(cursor.getColumnIndex("Modifiers"));

                            String isActive1      = cursor.getString(cursor.getColumnIndex("isActive1"));
                            String BuildingID1    = cursor.getString(cursor.getColumnIndex("BuildingID1"));
                            String BuildingScore1 = cursor.getString(cursor.getColumnIndex("BuildingScore1"));

                            if (!sBuildingType2.equalsIgnoreCase(""))
                            {
                                isActive2      = cursor.getString(cursor.getColumnIndex("isActive2"));
                                BuildingID2    = cursor.getString(cursor.getColumnIndex("BuildingID2"));
                                BuildingScore2 = cursor.getString(cursor.getColumnIndex("BuildingScore2"));

                                if (isPreCode2 && Modifiers.equalsIgnoreCase("Pre-Code"))
                                {
                                    isActive2 = "1";
                                }
                                else if (isPreCode2 && Modifiers.equalsIgnoreCase("Post-Benchmark"))
                                {
                                    isActive2 = "1";
                                }
                            }

                            if (NoOfStories != null && !NoOfStories.equalsIgnoreCase(""))
                            {
                                int iNoOfStories = Integer.parseInt(NoOfStories);

                                if ((iNoOfStories == 4 || iNoOfStories == 5 || iNoOfStories == 6 ||
                                     iNoOfStories == 7) && Modifiers.equalsIgnoreCase("Mid Rise (4 to 7 stories)"))
                                {
                                    Log.e(TAG, "NO. 1 SELECTED");

                                    isActive1 = "1";
                                    isActive2 = "1";
                                }
                                else if (iNoOfStories > 7 && Modifiers.equalsIgnoreCase("High Rise (> 7 stories)"))
                                {
                                    Log.e(TAG, "NO. 2 SELECTED");

                                    isActive1 = "1";
                                    isActive2 = "1";
                                }
                            }

                            if (SoilType != null && !SoilType.equalsIgnoreCase(""))
                            {
                                if (SoilType.contains("Dense Soil") && Modifiers.equalsIgnoreCase("Soil Type C"))
                                {
                                    isActive1 = "1";
                                    isActive2 = "1";
                                }
                                else if (SoilType.contains("Stiff Soil") && Modifiers.equalsIgnoreCase("Soil Type D"))
                                {
                                    isActive1 = "1";
                                    isActive2 = "1";
                                }
                                else  if (SoilType.contains("Soft Soil") && Modifiers.equalsIgnoreCase("Soil Type E"))
                                {
                                    isActive1 = "1";
                                    isActive2 = "1";
                                }
                            }

                            if (isPreCode1 && Modifiers.equalsIgnoreCase("Pre-Code"))
                            {
                                isActive1 = "1";
                            }
                            else if (isPostCode1 && Modifiers.equalsIgnoreCase("Post-Benchmark"))
                            {
                                isActive1 = "1";
                            }

                            ScoringBuildingsClass cValues = new ScoringBuildingsClass();
                            cValues.setModifiers(Modifiers);
                            cValues.setIsActive1(isActive1);
                            cValues.setIsActive2(isActive2);
                            cValues.setBuildingID1(BuildingID1);
                            cValues.setBuildingScore1(BuildingScore1);
                            cValues.setBuildingID2(BuildingID2);
                            cValues.setBuildingScore2(BuildingScore2);

                            scoringBuildingsClassList.add(cValues);
                        }
                        while (cursor.moveToNext());

                        rvAdapterBuildingScore.notifyDataSetChanged();
                    }
                }

                cursor.close();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "initSetBuilding: " + e.toString());
        }
    }





    private void initListeners()
    {
        iv_back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                initCancelReport();
            }
        });

        btn_apply_final_score.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int RiseCountSelected = 0, PrePostCountSelected = 0;

                int size = scoringBuildingsClassList.size();

                for (int i = 0; i < size; i++)  //Updating Score's
                {
                    ScoringBuildingsClass scoringBuildingsClass = scoringBuildingsClassList.get(i);

                    if (!scoringBuildingsClass.getModifiers().equalsIgnoreCase("Basic Score"))
                    {
                        if (scoringBuildingsClass.getModifiers().equalsIgnoreCase("Mid Rise (4 to 7 stories)") &&
                            scoringBuildingsClass.getIsActive1().equalsIgnoreCase("1"))
                        {
                            RiseCountSelected = RiseCountSelected  + 1;
                        }

                        if (scoringBuildingsClass.getModifiers().equalsIgnoreCase("High Rise (> 7 stories)") &&
                            scoringBuildingsClass.getIsActive1().equalsIgnoreCase("1"))
                        {
                            RiseCountSelected = RiseCountSelected  + 1;
                        }


                        if (scoringBuildingsClass.getModifiers().equalsIgnoreCase("Pre-Code") &&
                           (scoringBuildingsClass.getIsActive1().equalsIgnoreCase("1") ||
                            scoringBuildingsClass.getIsActive2().equalsIgnoreCase("1")))
                        {
                            PrePostCountSelected = PrePostCountSelected  + 1;
                        }

                        if (scoringBuildingsClass.getModifiers().equalsIgnoreCase("Post-Benchmark") &&
                           (scoringBuildingsClass.getIsActive1().equalsIgnoreCase("1") ||
                            scoringBuildingsClass.getIsActive2().equalsIgnoreCase("1")))
                        {
                            PrePostCountSelected = PrePostCountSelected  + 1;
                        }
                    }
                }

                if (RiseCountSelected != 0 && PrePostCountSelected != 0)
                {
                    initComputeFinalScore();
                }
                else
                {
                    if (RiseCountSelected == 0)
                    {
                        Toast.makeText(getApplicationContext(), "Mid Rise or High Rise is a required field", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Pre-code or Post Benchmark is a required field", Toast.LENGTH_SHORT).show();
                    }

                    //initWarningPrompt();
                }
            }
        });
    }

    private void initWarningPrompt()
    {
        try
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(BuildingScoreActivity.this);
            LayoutInflater inflater = getLayoutInflater();

            @SuppressLint("InflateParams")
            View view = inflater.inflate(R.layout.custom_dialog_title, null);

            TextView textView = view.findViewById(R.id.tv_dialog_title);
            textView.setTextColor(Color.BLACK);
            String sTitle = "Warning";
            textView.setText(sTitle);
            builder.setCustomTitle(view);
            builder.setMessage("Scoring Incomplete.");
            builder.setCancelable(true);
            builder.setPositiveButton("OK", null);
            builder.show();
        }
        catch (Exception e)
        {
            Log.e(TAG, toString());
        }
    }

    private void initComputeFinalScore()
    {
        try
        {
            final AlertDialog.Builder ADCancel = new AlertDialog.Builder(BuildingScoreActivity.this, R.style.MyAlertDialogStyle);
            ADCancel.setCancelable(false);

            LayoutInflater inflater = getLayoutInflater();

            @SuppressLint("InflateParams")
            View view = inflater.inflate(R.layout.custom_dialog_title, null);

            TextView textView = view.findViewById(R.id.tv_dialog_title);
            String sTitle = "Confirmation";
            textView.setText(sTitle);
            textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

            ADCancel.setCustomTitle(view);
            ADCancel.setMessage("Are you sure you want to apply the final score?");

            ADCancel.setPositiveButton("YES", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    Cursor cTempScore =  RepositoryTempBuildingScores.selectBuildingScores(getApplicationContext(),
                            UserAccount.UserAccountID, MissionOrderID, Category);

                    if (cTempScore.getCount()!=0)
                    {
                        //This will remove the temporary score if existing
                        RepositoryTempBuildingScores.removesBuildingScore(getApplicationContext(),
                                UserAccount.UserAccountID, MissionOrderID, Category);
                    }


                    int countSelected1 = 0, countSelected2 = 0;

                    int size = scoringBuildingsClassList.size();

                    for (int i = 0; i < size; i++)  //Updating Score's
                    {
                        ScoringBuildingsClass scoringBuildingsClass = scoringBuildingsClassList.get(i);

                        //Building Score for - 1
                        TempBuildingScoresClass tempBuildingScoresClass = new TempBuildingScoresClass();
                        tempBuildingScoresClass.setUserAccountID(UserAccount.UserAccountID);
                        tempBuildingScoresClass.setMissionOrderID(MissionOrderID);
                        tempBuildingScoresClass.setCategory(Category);
                        tempBuildingScoresClass.setBuildingID(scoringBuildingsClass.getBuildingID1());
                        tempBuildingScoresClass.setBuildingType(sBuildingType1);
                        tempBuildingScoresClass.setModifiers(scoringBuildingsClass.getModifiers());
                        tempBuildingScoresClass.setScores(scoringBuildingsClass.getBuildingScore1());
                        tempBuildingScoresClass.setIsActive(scoringBuildingsClass.getIsActive1());

                        RepositoryTempBuildingScores.saveBuildingScores(getApplicationContext(), tempBuildingScoresClass);

                        if (!sBuildingType2.equalsIgnoreCase(""))
                        {
                            //Building Score for - 2
                            TempBuildingScoresClass tempBuildingScoresClass2 = new TempBuildingScoresClass();
                            tempBuildingScoresClass2.setUserAccountID(UserAccount.UserAccountID);
                            tempBuildingScoresClass2.setMissionOrderID(MissionOrderID);
                            tempBuildingScoresClass2.setCategory(Category);
                            tempBuildingScoresClass2.setBuildingID(scoringBuildingsClass.getBuildingID2());
                            tempBuildingScoresClass2.setBuildingType(sBuildingType2);
                            tempBuildingScoresClass2.setModifiers(scoringBuildingsClass.getModifiers());
                            tempBuildingScoresClass2.setScores(scoringBuildingsClass.getBuildingScore2());
                            tempBuildingScoresClass2.setIsActive(scoringBuildingsClass.getIsActive2());

                            RepositoryTempBuildingScores.saveBuildingScores(getApplicationContext(), tempBuildingScoresClass2);
                        }

                        if (!scoringBuildingsClass.getModifiers().equalsIgnoreCase("Basic Score"))
                        {
                            if (scoringBuildingsClass.getIsActive1().equalsIgnoreCase("1"))
                            {
                                countSelected1 = countSelected1 + 1;
                            }

                            if (scoringBuildingsClass.getIsActive2().equalsIgnoreCase("1"))
                            {
                                countSelected2 = countSelected2 + 1;
                            }
                        }
                    }

                    //region FINAL SCORE - SAVE
                    String BuildingType1 = tv_BuildingType.getText().toString();

                    FinalBuildingScoresClass finalBuildingScoresClass1 = new FinalBuildingScoresClass();
                    finalBuildingScoresClass1.setUserAccountID(UserAccount.UserAccountID);
                    finalBuildingScoresClass1.setMissionOrderID(MissionOrderID);
                    finalBuildingScoresClass1.setCategory(Category);
                    finalBuildingScoresClass1.setBuildingScoreNo("1");
                    finalBuildingScoresClass1.setBuildingType(BuildingType1);
                    finalBuildingScoresClass1.setFinalScore(tv_finalScore1.getText().toString());
                    finalBuildingScoresClass1.setIsActive("1");

                    //Final Score - 1
                    Cursor cursor = RepositoryFinalBuildingScores.realAllData2(getApplicationContext(), UserAccount.UserAccountID,
                            MissionOrderID, "1", BuildingType1);

                    if (countSelected1 > 0)
                    {
                        if (cursor.getCount()!=0)
                        {
                            if (cursor.moveToFirst())
                            {
                                String ID = cursor.getString(cursor.getColumnIndex("ID"));

                                //RepositoryFinalBuildingScores.updateFinalBuildingScore(getApplicationContext(), ID, finalBuildingScoresClass1);
                                RepositoryFinalBuildingScores.removeBuildingFinalScores(getApplication(), ID);
                            }
                        }
                        else
                        {
                            Cursor cursor1 =  RepositoryFinalBuildingScores.realAllData4(getApplicationContext(), UserAccount.UserAccountID,
                                    MissionOrderID, "1");

                            if (cursor1.getCount()!= 0)
                            {
                                if (cursor1.moveToFirst())
                                {
                                    String ID = cursor1.getString(cursor1.getColumnIndex("ID"));

                                  //RepositoryFinalBuildingScores.updateFinalBuildingScore(getApplicationContext(), ID, finalBuildingScoresClass1);
                                    RepositoryFinalBuildingScores.removeBuildingFinalScores(getApplication(), ID);
                                }
                            }
                        }
                        RepositoryFinalBuildingScores.saveFinalBuildingScore(getApplicationContext(), finalBuildingScoresClass1);
                    }
                    else
                    {
                        if (cursor.getCount()!=0)
                        {
                            if (cursor.moveToFirst())
                            {
                                String ID = cursor.getString(cursor.getColumnIndex("ID"));

                                RepositoryFinalBuildingScores.removeBuildingFinalScores(getApplication(), ID);
                            }
                        }
                    }

                    //Final Score - 2
                    String BuildingType2 = tv_BuildingType2.getText().toString();

                    if (!BuildingType2.equalsIgnoreCase(""))
                    {
                        FinalBuildingScoresClass finalBuildingScoresClass2 = new FinalBuildingScoresClass();
                        finalBuildingScoresClass2.setUserAccountID(UserAccount.UserAccountID);
                        finalBuildingScoresClass2.setMissionOrderID(MissionOrderID);
                        finalBuildingScoresClass2.setCategory(Category);
                        finalBuildingScoresClass2.setBuildingScoreNo("2");
                        finalBuildingScoresClass2.setBuildingType(BuildingType2);
                        finalBuildingScoresClass2.setFinalScore(tv_finalScore2.getText().toString());
                        finalBuildingScoresClass2.setIsActive("1");

                        Cursor cursor2 = RepositoryFinalBuildingScores.realAllData2(getApplicationContext(), UserAccount.UserAccountID,
                                MissionOrderID, "2", BuildingType2);

                        if (countSelected2 > 0)
                        {
                            if (cursor2.getCount()!=0)
                            {
                                if (cursor2.moveToFirst())
                                {
                                    String ID = cursor2.getString(cursor2.getColumnIndex("ID"));

                                    //RepositoryFinalBuildingScores.updateFinalBuildingScore(getApplicationContext(), ID, finalBuildingScoresClass2);
                                    RepositoryFinalBuildingScores.removeBuildingFinalScores(getApplication(), ID);
                                }
                            }
                            else
                            {
                                Cursor cursor1 = RepositoryFinalBuildingScores.realAllData4(getApplicationContext(), UserAccount.UserAccountID,
                                        MissionOrderID, "2");

                                if (cursor1.getCount()!=0)
                                {
                                    if (cursor1.moveToFirst())
                                    {
                                        String ID = cursor1.getString(cursor1.getColumnIndex("ID"));

                                        //RepositoryFinalBuildingScores.updateFinalBuildingScore(getApplicationContext(), ID, finalBuildingScoresClass2);
                                        RepositoryFinalBuildingScores.removeBuildingFinalScores(getApplication(), ID);
                                    }
                                }
                            }

                            RepositoryFinalBuildingScores.saveFinalBuildingScore(getApplicationContext(), finalBuildingScoresClass2);
                        }
                        else
                        {
                            if (cursor2.getCount()!=0)
                            {
                                if (cursor2.moveToFirst())
                                {
                                    String ID = cursor2.getString(cursor2.getColumnIndex("ID"));

                                    RepositoryFinalBuildingScores.removeBuildingFinalScores(getApplication(), ID);
                                }
                            }
                        }
                    }
                    else
                    {
                        Cursor cursor1 = RepositoryFinalBuildingScores.realAllData4(getApplicationContext(), UserAccount.UserAccountID,
                                MissionOrderID, "2");

                        if (cursor1.getCount()!=0)
                        {
                            if (cursor1.moveToFirst())
                            {
                                String ID = cursor1.getString(cursor1.getColumnIndex("ID"));

                                //RepositoryFinalBuildingScores.updateFinalBuildingScore(getApplicationContext(), ID, finalBuildingScoresClass2);
                                RepositoryFinalBuildingScores.removeBuildingFinalScores(getApplication(), ID);
                            }
                        }
                    }

                    //endregion

                    dialog.dismiss();

                    Intent dataReturn = new Intent();
                    dataReturn.putExtra("Result", "Success");
                    setResult(RESULT_OK, dataReturn);
                    finish();
                }
            });

            ADCancel.setNegativeButton("NO" , null);
            ADCancel.show();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void onBackPressed()
    {
        initCancelReport();
    }

    private void initCancelReport()
    {
        try
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(BuildingScoreActivity.this);
            builder.setTitle("Cancel Scoring");
            builder.setMessage("Are you sure you want to exit without applying the Final Score?");
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
                    finish();
                }
            });
            builder.show();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initFinalOutputBuildingType(String BuildingType1, String BuildingType2)
    {

        switch (BuildingType1)
        {
            case "W1":
                sBuildingTypeOutput1 = BuildingType1 + " - " + "Light Wood frame single-or multiple- family dwellings of one or more stories in height";
                break;
            case "W2":
                sBuildingTypeOutput1 = BuildingType1 + " - " + "Light wood-frame buildings larger than 5,000 square feet";
                break;
            case "S1":
            case "S1 (MRF)":
                sBuildingTypeOutput1 = BuildingType1 + " - " + "Steel moment-resisting frame buildings";
                break;
            case "S2":
            case "S2 (BR)":
                sBuildingTypeOutput1 = BuildingType1 + " - " + "Braced steel frame buildings";
                break;
            case "S3":
            case "S3 (LM)":
                sBuildingTypeOutput1 = BuildingType1 + " - " + "Light metal buildings";
                break;
            case "S4":
            case "S4 (RC SW)":
                sBuildingTypeOutput1 = BuildingType1 + " - " + "Steel frame buildings with cast-in-place concrete shear walls";
                break;
            case "S5":
            case "S5 (URM INF)":
                sBuildingTypeOutput1 = BuildingType1 + " - " + "Steel frame buildings with unreinforced masonry infill walls";
                break;
            case "C1":
            case "C1 (MRF)":
                sBuildingTypeOutput1 = BuildingType1 + " - " + "Concrete moment-resisting frame buildings";
                break;
            case "C2":
            case "C2 (SW)":
                sBuildingTypeOutput1 = BuildingType1 + " - " + "Concrete shear-wall buildings";
                break;
            case "C3":
            case "C3 (URM INF)":
                sBuildingTypeOutput1 = BuildingType1 + " - " + "Concrete frame buildings with unreinforced masonry infill walls";
                break;
            case "PC1":
            case "PC1 (TU)":
                sBuildingTypeOutput1 = BuildingType1 + " - " + "Tilt-up buildings";
                break;
            case "PC2":
                sBuildingTypeOutput1 = BuildingType1 + " - " + "Precast concrete frame buildings";
                break;
            case "RM1":
            case "RM1 (FD)":
                sBuildingTypeOutput1 = BuildingType1 + " - " + "Reinforced masonry buildings with flexible floor and roof diaphragms";
                break;
            case "RM2":
            case "RM2 (RD)":
                sBuildingTypeOutput1 = BuildingType1 + " - " + "Reinforced masonry buildings with rigid floor and roof diaphragms";
                break;
            case "URM":
                sBuildingTypeOutput1 = BuildingType1 + " - " + "Unreinforced masonry bearing-wall buildings";
                break;
        }

        switch (BuildingType2)
        {
            case "W1":
                sBuildingTypeOutput2 = BuildingType2 + " - " + "Light Wood frame single-or multiple- family dwellings of one or more stories in height";
                break;
            case "W2":
                sBuildingTypeOutput2 = BuildingType2 + " - " + "Light wood-frame buildings larger than 5,000 square feet";
                break;
            case "S1":
            case "S1 (MRF)":
                sBuildingTypeOutput2 = BuildingType2 + " - " + "Steel moment-resisting frame buildings";
                break;
            case "S2":
            case "S2 (BR)":
                sBuildingTypeOutput2 = BuildingType2 + " - " + "Braced steel frame buildings";
                break;
            case "S3":
            case "S3 (LM)":
                sBuildingTypeOutput2 = BuildingType2 + " - " + "Light metal buildings";
                break;
            case "S4":
            case "S4 (RC SW)":
                sBuildingTypeOutput2 = BuildingType2 + " - " + "Steel frame buildings with cast-in-place concrete shear walls";
                break;
            case "S5":
            case "S5 (URM INF)":
                sBuildingTypeOutput2 = BuildingType2 + " - " + "Steel frame buildings with unreinforced masonry infill walls";
                break;
            case "C1":
            case "C1 (MRF)":
                sBuildingTypeOutput2 = BuildingType2 + " - " + "Concrete moment-resisting frame buildings";
                break;
            case "C2":
            case "C2 (SW)":
                sBuildingTypeOutput2 = BuildingType2 + " - " + "Concrete shear-wall buildings";
                break;
            case "C3":
            case "C3 (URM INF)":
                sBuildingTypeOutput2 = BuildingType2 + " - " + "Concrete frame buildings with unreinforced masonry infill walls";
                break;
            case "PC1":
            case "PC1 (TU)":
                sBuildingTypeOutput2 = BuildingType2 + " - " + "Tilt-up buildings";
                break;
            case "PC2":
                sBuildingTypeOutput2 = BuildingType2 + " - " + "Precast concrete frame buildings";
                break;
            case "RM1":
            case "RM1 (FD)":
                sBuildingTypeOutput2 = BuildingType2 + " - " + "Reinforced masonry buildings with flexible floor and roof diaphragms";
                break;
            case "RM2":
            case "RM2 (RD)":
                sBuildingTypeOutput2 = BuildingType2 + " - " + "Reinforced masonry buildings with rigid floor and roof diaphragms";
                break;
            case "URM":
                sBuildingTypeOutput2 = BuildingType2 + " - " + "Unreinforced masonry bearing-wall buildings";
                break;
        }

    }

}
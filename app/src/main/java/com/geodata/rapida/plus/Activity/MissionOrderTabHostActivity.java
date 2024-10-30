package com.geodata.rapida.plus.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.geodata.rapida.plus.Adapter.AdapterMissionOrder;
import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryOnlineAssignedInspectors;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryOnlineBuildingInformation;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryUserAccount;
import com.geodata.rapida.plus.Tools.MyExceptionHandler;
import com.geodata.rapida.plus.Tools.SlidingTabLayout;
import com.geodata.rapida.plus.Tools.UserAccount;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MissionOrderTabHostActivity extends AppCompatActivity
{
    private static final String TAG = MissionOrderTabHostActivity.class.getSimpleName();

    String MissionOrderNo, MissionOrderID, AssetID, SeismicityRegion, dtAdded;

    ImageView iv_back;
    TextView tv_MissionOrderNo;

    AdapterMissionOrder mAdapter;
    ViewPager viewPager;

    SlidingTabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this)); //Getting Crash - Restart

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_order_tab_host);

        initViews();
    }

    private void initViews()
    {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        toolbar.setTitle("");
       // setSupportActionBar(toolbar);

        try
        {
            Bundle extras = getIntent().getExtras();

            if(extras != null)
            {
                MissionOrderNo       = extras.getString("MissionOrderNo");
                MissionOrderID       = extras.getString("MissionOrderID");
                AssetID              = extras.getString("AssetID");
                SeismicityRegion     = extras.getString("SeismicityRegion");
                dtAdded              = extras.getString("dtAdded");
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        tv_MissionOrderNo = findViewById(R.id.tv_MissionOrderNo);
        iv_back           = findViewById(R.id.iv_back);
        viewPager         = findViewById(R.id.view_pager);

        initTabViews();
    }

    private void initTabViews()
    {
        tv_MissionOrderNo.setText(MissionOrderNo);

        Cursor cursor = RepositoryOnlineBuildingInformation.realAllData2(getApplicationContext(),
               String.valueOf(UserAccount.employeeID), MissionOrderID);

        if (cursor.getCount() != 0)
        {
            if (cursor.moveToFirst())
            {
                String Status             = cursor.getString(cursor.getColumnIndex("InspectionStatus"));
                String ReasonForScreening = cursor.getString(cursor.getColumnIndex("ReasonForScreening"));

                String Status2;

                Cursor cursor2 = RepositoryOnlineAssignedInspectors.realAllData3(getApplicationContext(),
                        MissionOrderID, String.valueOf(UserAccount.employeeID), UserAccount.CompleteName);

                if (cursor2.getCount()!=0)
                {
                    Status2 = "0";
                }
                else
                {
                    Status2 = "1";
                }
                cursor2.close();

                int numOfTabs;
                CharSequence[] Titles;

                if (Status2.equals("1"))
                {
                    Cursor cursor1 = RepositoryOnlineAssignedInspectors.realAllData5(getApplicationContext(), UserAccount.CompleteName);

                    if (cursor1.getCount() != 0)
                    {
                        numOfTabs = 3;

                        if (ReasonForScreening.contains("RESA"))
                        {
                            Titles = new CharSequence[]{"Bldg Information", "Mission Order", "RESA"};
                        }
                        else if (ReasonForScreening.contains("DESA"))
                        {
                            Titles = new CharSequence[]{"Bldg Information", "Mission Order", "DESA"};
                        }
                        else
                        {
                            Titles = new CharSequence[]{"Bldg Information", "Mission Order", "SEISMIC SURVEY"};
                        }
                    }
                    else
                    {
                        numOfTabs = 2;
                        Titles = new CharSequence[]{"Bldg Information", "Mission Order"};
                    }
                    cursor1.close();
                }
                else
                {
                    numOfTabs = 2;
                    Titles = new CharSequence[]{"Bldg Information", "Mission Order"};
                }

                mAdapter = new AdapterMissionOrder(getSupportFragmentManager(), Titles, numOfTabs, Status, Status2, ReasonForScreening);
            }
        }
        cursor.close();

        viewPager.setAdapter(mAdapter);

        tabs = findViewById(R.id.slider_tab_layout);
        tabs.setDistributeEvenly(true);

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer()
        {
            @Override
            public int getIndicatorColor(int position)
            {
                return getResources().getColor(R.color.tab_color_customize);
            }
        });

        tabs.setViewPager(viewPager);

        initListeners();
    }

    private void initListeners()
    {
        iv_back.setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();


    }
}
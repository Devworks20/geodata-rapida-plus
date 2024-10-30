package com.geodata.rapida.plus.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.geodata.rapida.plus.Fragment.EarthquakeDamageInspectionMOFragment;
import com.geodata.rapida.plus.Fragment.MissionOrderListFragment;
import com.geodata.rapida.plus.Fragment.NewRVSFragment;
import com.geodata.rapida.plus.Fragment.RVSListFragment;
import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryUserAccount;
import com.geodata.rapida.plus.Tools.MyExceptionHandler;
import com.geodata.rapida.plus.Tools.Settings;
import com.geodata.rapida.plus.Tools.UserAccount;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private static final String TAG = NavigationActivity.class.getSimpleName();

    Toolbar toolbar;
    DrawerLayout drawer_layout;
    ActionBarDrawerToggle actionBarDrawerToggle;

    LinearLayout ll_logout;

    MissionOrderListFragment missionOrderListFragment;
    NewRVSFragment newRVSFragment;
    RVSListFragment rvsListFragment;
    EarthquakeDamageInspectionMOFragment earthquakeDamageInspectionMOFragment;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    CircleImageView civ_account_image;

    TextView tv_account_name, tv_account_title;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(NavigationActivity.this)); //Getting Crash - Restart

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        initViews();
    }

    private void initViews()
    {
        toolbar = findViewById(R.id.nav_action);

        if (Settings.APPLICATION_NAME.equalsIgnoreCase("FEMA-154 Seismic Resiliency Inspection"))
        {
            toolbar.setTitle("FEMA-154 Seismic Resiliency Inspection");
        }
        else if (Settings.APPLICATION_NAME.equalsIgnoreCase("Seismic Resiliency Survey"))
        {
            toolbar.setTitle("Seismic Resiliency Survey");
        }
        else if (Settings.APPLICATION_NAME.equalsIgnoreCase("DESA"))
        {
            toolbar.setTitle("Detailed Evaluation and Safety Assessment");
        }
        else if (Settings.APPLICATION_NAME.equalsIgnoreCase("RESA"))
        {
            toolbar.setTitle("Rapid Evaluation and Safety Assessment");
        }
        else if (Settings.APPLICATION_NAME.equalsIgnoreCase("RVS"))
        {
            toolbar.setTitle("Rapid Visual Screening");
        }



        setSupportActionBar(toolbar);

        drawer_layout         = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer_layout, R.string.txt_open, R.string.txt_close);

        drawer_layout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView  = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ll_logout = findViewById(R.id.ll_logout);


        View header = navigationView.getHeaderView(0);
        civ_account_image     = header.findViewById(R.id.civ_account_image);
        tv_account_name       = header.findViewById(R.id.tv_account_name);
        tv_account_name.setText(UserAccount.CompleteName);
        tv_account_title      = header.findViewById(R.id.tv_account_title);

        //Init Fragments
        missionOrderListFragment             = new MissionOrderListFragment();
        newRVSFragment                       = new NewRVSFragment();
        rvsListFragment                      = new RVSListFragment();
        earthquakeDamageInspectionMOFragment = new EarthquakeDamageInspectionMOFragment();

        fragmentManager     = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.frameLayout, missionOrderListFragment);
        fragmentTransaction.add(R.id.frameLayout, newRVSFragment);
        fragmentTransaction.add(R.id.frameLayout, rvsListFragment);
        fragmentTransaction.add(R.id.frameLayout, earthquakeDamageInspectionMOFragment);

        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,0);
        fragmentTransaction.commit();


        if (Settings.APPLICATION_NAME.equalsIgnoreCase("FEMA-154 Seismic Resiliency Inspection") ||
            Settings.APPLICATION_NAME.equalsIgnoreCase("Seismic Resiliency Survey"))
        {
            showHideFragment(null, missionOrderListFragment);
            showHideFragment(newRVSFragment, null);
            showHideFragment(rvsListFragment, null);
            showHideFragment(earthquakeDamageInspectionMOFragment, null);
        }
        else if (Settings.APPLICATION_NAME.equalsIgnoreCase("Emergency Damage Assessment") ||
                 Settings.APPLICATION_NAME.equalsIgnoreCase("DESA") ||
                 Settings.APPLICATION_NAME.equalsIgnoreCase("RESA"))
        {
            showHideFragment(null, earthquakeDamageInspectionMOFragment);
            showHideFragment(newRVSFragment, null);
            showHideFragment(rvsListFragment, null);
            showHideFragment(missionOrderListFragment,null);
        }
        else if (Settings.APPLICATION_NAME.equalsIgnoreCase("RVS"))
        {
            showHideFragment(null, newRVSFragment);
            showHideFragment(earthquakeDamageInspectionMOFragment, null);
            showHideFragment(rvsListFragment, null);
            showHideFragment(missionOrderListFragment,null);
        }

        initListeners();
    }


    private void initListeners()
    {
        ll_logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                initLogout();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id==R.id.nav_mission_order)
        {
            //toolbar.setTitle("Seismic Resiliency Inspection");
            //toolbar.setTitle("Seismic Resiliency Survey");
            //toolbar.setTitle("FEMA-154 Seismic Resiliency Inspection");

            if(newRVSFragment.isVisible())
            {
                showHideFragment(newRVSFragment, missionOrderListFragment);
            }
            else if(rvsListFragment.isVisible())
            {
                showHideFragment(rvsListFragment, missionOrderListFragment);
            }
            else if(earthquakeDamageInspectionMOFragment.isVisible())
            {
                showHideFragment(earthquakeDamageInspectionMOFragment, missionOrderListFragment);
            }
            else
            {
                showHideFragment(null, missionOrderListFragment);
            }
        }

        else if(id==R.id.nav_edi_mission_order)
        {
            //toolbar.setTitle("Earthquake Damage Inspection");
            //toolbar.setTitle("Emergency Damage Assessment");

            if (Settings.APPLICATION_NAME.equalsIgnoreCase("RESA"))
            {
                toolbar.setTitle("Rapid Evaluation and Safety Assessment");
            }
            else if  (Settings.APPLICATION_NAME.equalsIgnoreCase("DESA"))
            {
                toolbar.setTitle("Detailed Evaluation and Safety Assessment");
            }

            if(missionOrderListFragment.isVisible())
            {
                showHideFragment(missionOrderListFragment, earthquakeDamageInspectionMOFragment);
            }
            else if(newRVSFragment.isVisible())
            {
                showHideFragment(newRVSFragment, earthquakeDamageInspectionMOFragment);
            }
            else if(rvsListFragment.isVisible())
            {
                showHideFragment(rvsListFragment, earthquakeDamageInspectionMOFragment);
            }
            else
            {
                showHideFragment(null, earthquakeDamageInspectionMOFragment);
            }
        }
        else if(id==R.id.nav_new_rvs)
        {
            //toolbar.setTitle("Earthquake Damage Inspection");
            //toolbar.setTitle("Emergency Damage Assessment");
           // toolbar.setTitle("Rapid Visual Screening");

            if(missionOrderListFragment.isVisible())
            {
                showHideFragment(missionOrderListFragment, newRVSFragment);
            }
            else if(rvsListFragment.isVisible())
            {
                showHideFragment(rvsListFragment, newRVSFragment);
            }
            else if(earthquakeDamageInspectionMOFragment.isVisible())
            {
                showHideFragment(earthquakeDamageInspectionMOFragment, newRVSFragment);
            }
            else
            {
                showHideFragment(null, newRVSFragment);
            }
        }
        else if(id==R.id.nav_rvs_list) //Not Use
        {
            if(missionOrderListFragment.isVisible())
            {
                showHideFragment(missionOrderListFragment, rvsListFragment);
            }
            else if(newRVSFragment.isVisible())
            {
                showHideFragment(newRVSFragment, rvsListFragment);
            }
            else if(earthquakeDamageInspectionMOFragment.isVisible())
            {
                showHideFragment(earthquakeDamageInspectionMOFragment, rvsListFragment);
            }
            else
            {
                showHideFragment(null, rvsListFragment);
            }
        }
        else if(id==R.id.nav_data_privacy_policy)
        {
            Intent intent = new Intent(NavigationActivity.this, DataPrivacyPolicyActivity.class);
            startActivity(intent);
        }
        else if(id==R.id.nav_terms_and_condition)
        {
            Intent intent = new Intent(NavigationActivity.this, TermsAndConditionsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showHideFragment(Fragment fragment1, Fragment fragment2)
    {
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();

        fragTransaction.setCustomAnimations(android.R.animator.fade_in,0);

        if (fragment1 != null)
        {
            fragTransaction.hide(fragment1);
        }

        if (fragment2!= null)
        {
            if(fragment2 == newRVSFragment || fragment2 == rvsListFragment || fragment2 == earthquakeDamageInspectionMOFragment)
            {
                fragTransaction.detach(fragment2);
                fragTransaction.attach(fragment2);
            }

            fragTransaction.show(fragment2);
        }

        fragTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initLogout()
    {
        try
        {
            final AlertDialog.Builder builder = new AlertDialog.Builder(NavigationActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.custom_dialog_title, null);
            TextView textView = view.findViewById(R.id.tv_dialog_title);
            String sTitle = "Logout";
            textView.setText(sTitle);
            builder.setCustomTitle(view);
            builder.setMessage("Are you sure you want to logout your SRI account?");
            builder.setCancelable(true);
            builder.setNegativeButton("Cancel ", null);
            builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    try
                    {
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("isLogin", null);
                        editor.apply();

                        //RepositoryUserAccount.removeUser(getApplicationContext());

                        dialog.dismiss();

                        Intent intent = new Intent(NavigationActivity.this, FirstActivity.class);
                        startActivity(intent);
                        finish();

                        Toast.makeText(getApplicationContext(), "Account Logout.", Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
            });
            builder.show();
        }
        catch (Exception e)
        {
            Log.e(TAG, toString());
        }
    }


}
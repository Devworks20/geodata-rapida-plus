package com.geodata.rapida.plus.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Process;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.geodata.rapida.plus.SQLite.Repository.RepositoryUserAccount;
import com.geodata.rapida.plus.Tools.UserAccount;

public class FirstActivity extends AppCompatActivity
{
    private static final String TAG = FirstActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        initViews();
    }

    private void initViews()
    {
        try
        {
            Bundle extras = getIntent().getExtras();

            if(extras != null)
            {
                if (getIntent().getBooleanExtra("crash", false))
                {
                    Intent getIn = getIntent();
                    String crashedReport = getIn.getStringExtra("crashed");

                    Validation(crashedReport);
                }
            }
            else
            {
                Log.e(TAG, "DIRECTING HOME");

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

                if (prefs.contains("isLogin") && prefs.getString("isLogin", null) != null)
                {
                    Cursor cursor = RepositoryUserAccount.realAllData(getApplicationContext());

                    if (cursor.getCount()!=0)
                    {
                        if (cursor.moveToLast())
                        {
                            String CompleteName =  cursor.getString(cursor.getColumnIndex("CompleteName"));
                            String Username     =  cursor.getString(cursor.getColumnIndex("Username"));

                            Toast.makeText(this, "Welcome back " + CompleteName, Toast.LENGTH_LONG).show();

                            UserAccount.employeeID    = Integer.parseInt(cursor.getString(cursor.getColumnIndex("EmployeeID")));
                            UserAccount.UserAccountID = cursor.getString(cursor.getColumnIndex("EmployeeID"));
                            UserAccount.CompleteName  = CompleteName;

                            Log.e(TAG, "UserAccount.UserAccountID: " + UserAccount.UserAccountID);

                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("isLogin", "Done");
                            editor.apply();

                            Intent intent = new Intent(FirstActivity.this, NavigationActivity.class);
                            intent.putExtra("Username", Username);
                            startActivity(intent);
                            finish();
                        }
                        cursor.close();
                    }
                    else
                    {
                        Intent intent = new Intent(FirstActivity.this, LandingPageActivity.class);
                        startActivity(intent);
                    }
                }
                else
                {
                    Intent intent = new Intent(FirstActivity.this, LandingPageActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void Validation(String message)
    {
        DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface alertDialog, int which)
            {
                if (which == DialogInterface.BUTTON_POSITIVE)
                {
                    Process.killProcess(Process.myPid());
                }
            }
        };

        AlertDialog.Builder mValid = new AlertDialog.Builder(this);
        mValid.setTitle("General Error: ");
        mValid.setMessage(message);
        mValid.setCancelable(false);
        mValid.setPositiveButton("Ok", ok);
        mValid.show();
    }
}
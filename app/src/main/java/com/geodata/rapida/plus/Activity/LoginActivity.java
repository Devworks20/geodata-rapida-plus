package com.geodata.rapida.plus.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.Retrofit.Client.APIClient;
import com.geodata.rapida.plus.Retrofit.Interface.APIClientInterface;
import com.geodata.rapida.plus.Retrofit.Model.LoginModel;
import com.geodata.rapida.plus.Retrofit.Model.TokenModel;
import com.geodata.rapida.plus.SQLite.Class.UserAccountClass;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryUserAccount;
import com.geodata.rapida.plus.Tools.EncodeDecodeAES;
import com.geodata.rapida.plus.Tools.Settings;
import com.geodata.rapida.plus.Tools.UserAccount;
import com.geodata.rapida.plus.Tools.VolleyCatch;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity
{
    private static final String TAG = LoginActivity.class.getSimpleName();

    EditText edt_username, edt_password;

    Button btn_login;

    TextView tv_terms, tv_privacy_policy, tv_what_is_rapida_mobile_app;
    TextView tv_forgot_password, tv_application_name;

    LinearLayout ll_loading, ll_login_layout;

    APIClientInterface apiInterface;
    VolleyCatch volleyCatch = new VolleyCatch();

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        ActivityCompat.requestPermissions(this,new String[]
                {
                        Manifest.permission.INTERNET,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                },1);

        super.onCreate(savedInstanceState);

       /*
       setContentView(R.layout.activity_login);
       initViews();
       */

        setContentView(R.layout.activity_login_2);
        initViews2();

        tv_application_name = findViewById(R.id.tv_application_name);

        if (Settings.APPLICATION_NAME.equalsIgnoreCase("Seismic Resiliency Survey"))
        {
            tv_application_name.setText("Seismic Resiliency Survey");

            btn_login.setBackground(getDrawable(R.drawable.button_light_blue_custom));
        }
        else if (Settings.APPLICATION_NAME.equalsIgnoreCase("Emergency Damage Assessment"))
        {
            tv_application_name.setText("Emergency Damage Assessment");

            btn_login.setBackground(getDrawable(R.drawable.button_light_blue_custom));
        }

        if (Settings.APPLICATION_NAME.equalsIgnoreCase("FEMA-154 Seismic Resiliency Inspection"))
        {
            tv_application_name.setText("FEMA-154 Seismic Resiliency Inspection");

            btn_login.setBackground(getDrawable(R.drawable.button_orange_custom));
        }
        else if (Settings.APPLICATION_NAME.equalsIgnoreCase("DESA"))
        {
            tv_application_name.setText("Detailed Evaluation and Safety Assessment (DESA)");

            btn_login.setBackground(getDrawable(R.drawable.button_violet_custom));
        }
        else if (Settings.APPLICATION_NAME.equalsIgnoreCase("RESA"))
        {
            tv_application_name.setText("Rapid Evaluation and Safety Assessment (RESA)");

            btn_login.setBackground(getDrawable(R.drawable.button_green_custom));
        }
        else if (Settings.APPLICATION_NAME.equalsIgnoreCase("RVS"))
        {
            tv_application_name.setText("Rapid Visual Screening (RVS)");

            btn_login.setBackground(getDrawable(R.drawable.button_red_custom));
        }
    }

    private void initViews()
    {
        apiInterface = APIClient.getClient().create(APIClientInterface.class);

        edt_username = findViewById(R.id.edt_username);
        edt_password = findViewById(R.id.edt_password);

        btn_login = findViewById(R.id.btn_login);

        tv_terms          = findViewById(R.id.tv_terms);
        tv_privacy_policy = findViewById(R.id.tv_privacy_policy);
        tv_forgot_password           = findViewById(R.id.tv_forgot_password);
        tv_what_is_rapida_mobile_app = findViewById(R.id.tv_what_is_rapida_mobile_app);

        ll_loading       = findViewById(R.id.ll_loading);
        ll_login_layout  = findViewById(R.id.ll_login_layout);

        initListeners();
    }

    private void initListeners()
    {
        btn_login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                initLoginUserAccount();
            }
        });

        tv_forgot_password.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        tv_terms.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(LoginActivity.this, TermsAndConditionsActivity.class);
                startActivity(intent);
            }
        });

        tv_privacy_policy.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(LoginActivity.this, DataPrivacyPolicyActivity.class);
                startActivity(intent);
            }
        });

        tv_what_is_rapida_mobile_app.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(LoginActivity.this, WhatIsRapidAActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initLoginUserAccount()
    {
        try
        {
            if (initCheckFields())
            {
                String Username = edt_username.getText().toString();
                String Password = edt_password.getText().toString();

                Cursor cursor = RepositoryUserAccount.realAllData3(getApplicationContext(), Username);

                if (cursor.getCount()!=0)
                {
                    if (cursor.moveToLast())
                    {
                        String CompleteName =  cursor.getString(cursor.getColumnIndex("CompleteName"));

                        UserAccount.employeeID    = Integer.parseInt(cursor.getString(cursor.getColumnIndex("EmployeeID")));
                        UserAccount.UserAccountID = cursor.getString(cursor.getColumnIndex("EmployeeID"));
                        UserAccount.CompleteName  = CompleteName;

                        Log.e(TAG, "UserAccount.UserAccountID: " + UserAccount.UserAccountID);

                        Intent intent = new Intent(LoginActivity.this, NavigationActivity.class);
                        intent.putExtra("Username", Username);
                        // intent.putExtra("Option", "All");
                        intent.putExtra("Option", "Seismic Resiliency Survey F154");
                        //intent.putExtra("Option", "Emergency Damage Inspection");
                        startActivity(intent);
                        finish();

                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("isLogin", "Done");
                        editor.apply();
                    }
                }
                else
                {
                    if (haveNetworkConnection(getApplicationContext()))
                    {
                        initCloseKeyboard();

                        ll_login_layout.setVisibility(View.GONE);
                        ll_loading.setVisibility(View.VISIBLE);

                        Call<TokenModel> callToken = apiInterface.ValidateToken("password", Username, Password);

                        callToken.enqueue(new Callback<TokenModel>()
                        {
                            @Override
                            public void onResponse(@NonNull Call<TokenModel> call, @NonNull Response<TokenModel> response)
                            {
                                if (response.isSuccessful() && response.body() != null)
                                {
                                    TokenModel tokenModel = response.body();
                                    String TokenMain = "Bearer " + tokenModel.getAccess_token();

                                    Log.e(TAG, "TokenMain: " + TokenMain);

                                    final Map<String, String> headers = new HashMap<>();
                                    headers.put("Content-Type", "application/json; charset=UTF-8");
                                    headers.put("Authorization", TokenMain);

                                    Call<LoginModel> callLogin = apiInterface.LoginUserAccount(headers, Username, Password);

                                    callLogin.enqueue(new Callback<LoginModel>()
                                    {
                                        @Override
                                        public void onResponse(@NonNull Call<LoginModel> call, @NonNull Response<LoginModel> response)
                                        {
                                            if (response.isSuccessful() && response.body() != null)
                                            {
                                                final LoginModel loginModel = response.body();
                                                UserAccount.employeeID = loginModel.getEmployeeID();
                                                UserAccount.UserAccountID = String.valueOf(loginModel.getEmployeeID());
                                                UserAccount.CompleteName = loginModel.getCompleteName();
                                                Log.e(TAG, "UserAccount.employeeID: " + UserAccount.employeeID);

                                                UserAccountClass userAccountClass = new UserAccountClass();
                                                userAccountClass.setUsername(Username);

                                                try
                                                {
                                                    EncodeDecodeAES aes = new EncodeDecodeAES();
                                                    String encryptedPassword = EncodeDecodeAES.bytesToHex(aes.encrypt(Password));
                                                    userAccountClass.setPassword(encryptedPassword);
                                                }
                                                catch (Exception e)
                                                {
                                                    Log.e(TAG, e.toString());
                                                    userAccountClass.setPassword(Password);
                                                }

                                                userAccountClass.setRoleName(loginModel.getRoleName());
                                                userAccountClass.setCompleteName(loginModel.getCompleteName());
                                                userAccountClass.setAppID(loginModel.getAppID());
                                                userAccountClass.setPosition(loginModel.getPosition());
                                                userAccountClass.setDtAdded(loginModel.getRoleName());
                                                userAccountClass.setEmployeeID(loginModel.getEmployeeID());
                                                userAccountClass.setUserAccountID(String.valueOf(loginModel.getEmployeeID()));

                                                Cursor cursor = RepositoryUserAccount.realAllData2(getApplicationContext(), String.valueOf(loginModel.getEmployeeID()));

                                               if (cursor.getCount() == 0)
                                                {
                                                    RepositoryUserAccount.saveAccount(getApplicationContext(), userAccountClass);
                                                }

                                                ll_loading.setVisibility(View.GONE);
                                                ll_login_layout.setVisibility(View.VISIBLE);

                                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                                                SharedPreferences.Editor editor = prefs.edit();
                                                editor.putString("isLogin", "Done");
                                                editor.apply();

                                                Intent intent = new Intent(LoginActivity.this, GetAllDataActivity.class);
                                                intent.putExtra("Option", "All");
                                                startActivity(intent);
                                                finish();
                                            }
                                            else
                                            {
                                                String errorLog = response.isSuccessful() ? "Login: Server Response Null" : "Login Failed: " + convertingResponseError(response.errorBody());
                                                Log.e(TAG, errorLog);
                                                volleyCatch.writeToFile(errorLog);
                                                ll_loading.setVisibility(View.GONE);
                                                ll_login_layout.setVisibility(View.VISIBLE);
                                                Toast.makeText(LoginActivity.this, "Login Access Failed.", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<LoginModel> call, @NonNull Throwable t)
                                        {
                                            String errorLog = "Login Failure: " + t.getMessage();
                                            Log.e(TAG, errorLog);
                                            volleyCatch.writeToFile(errorLog);
                                            ll_loading.setVisibility(View.GONE);
                                            ll_login_layout.setVisibility(View.VISIBLE);
                                            Toast.makeText(LoginActivity.this, "Login Access Failed.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                else
                                {
                                    String errorLog = "Login Access Failed.";

                                    if (response.body() == null)
                                    {
                                        errorLog = "Login Token: Server Response Null";
                                    }
                                    else
                                    {
                                        errorLog = "Login Token Failed: " + convertingResponseError(response.errorBody());
                                    }

                                    Log.e(TAG, errorLog);
                                    volleyCatch.writeToFile(errorLog);

                                    ll_loading.setVisibility(View.GONE);
                                    ll_login_layout.setVisibility(View.VISIBLE);

                                    Toast.makeText(LoginActivity.this, "Login Access Failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(@NonNull Call<TokenModel> call, @NonNull Throwable t)
                            {
                                String errorLog = "Login Token Failure: " + t.getMessage();
                                Log.e(TAG, errorLog);
                                volleyCatch.writeToFile(errorLog);

                                ll_loading.setVisibility(View.GONE);
                                ll_login_layout.setVisibility(View.VISIBLE);
                                Toast.makeText(LoginActivity.this, "Login Access Failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "You have no internet connection. try again later.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        catch (Exception e)
        {
            String Logs = "Getting Login: " + e.getMessage();

            Log.e(TAG, Logs);
            volleyCatch.writeToFile(Logs);

            Toast.makeText(LoginActivity.this, "Login Access Failed.", Toast.LENGTH_SHORT).show();
        }
    }




    //2
    private void initViews2()
    {
        apiInterface = APIClient.getClient().create(APIClientInterface.class);

        edt_username = findViewById(R.id.edt_username);
        edt_password = findViewById(R.id.edt_password);

        btn_login = findViewById(R.id.btn_login);

        tv_forgot_password = findViewById(R.id.tv_forgot_password);

        ll_loading       = findViewById(R.id.ll_loading);
        ll_login_layout  = findViewById(R.id.ll_login_layout);

        initListeners2();
    }

    private void initListeners2()
    {
        btn_login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                initLoginUserAccount2();
            }
        });

        tv_forgot_password.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initLoginUserAccount2()
    {
        try
        {
            if (initCheckFields())
            {

                String Username = edt_username.getText().toString();
                String Password = edt_password.getText().toString();

                Cursor cursor = RepositoryUserAccount.realAllData3(getApplicationContext(), Username);

                if (cursor.getCount()!=0)
                {
                    if (cursor.moveToLast())
                    {
                        String CompleteName =  cursor.getString(cursor.getColumnIndex("CompleteName"));

                        UserAccount.employeeID    = Integer.parseInt(cursor.getString(cursor.getColumnIndex("EmployeeID")));
                        UserAccount.UserAccountID = cursor.getString(cursor.getColumnIndex("EmployeeID"));
                        UserAccount.CompleteName  = CompleteName;

                        Log.e(TAG, "UserAccount.UserAccountID: " + UserAccount.UserAccountID);

                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("isLogin", "Done");
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, NavigationActivity.class);
                        intent.putExtra("Username", Username);
                        startActivity(intent);
                        finish();
                    }
                }
                else
                {
                    if (haveNetworkConnection(getApplicationContext()))
                    {
                        initCloseKeyboard();

                        ll_login_layout.setVisibility(View.GONE);
                        ll_loading.setVisibility(View.VISIBLE);

                        Call<TokenModel> callToken = apiInterface.ValidateToken("password", Username, Password);

                        callToken.enqueue(new Callback<TokenModel>()
                        {
                            @Override
                            public void onResponse(@NonNull Call<TokenModel> call, @NonNull Response<TokenModel> response)
                            {
                                if (response.isSuccessful() && response.body() != null)
                                {
                                    TokenModel tokenModel = response.body();
                                    String TokenMain = "Bearer " + tokenModel.getAccess_token();
                                    Log.e(TAG, "TokenMain: " + TokenMain);

                                    //Authorization
                                    final Map<String, String> headers = new HashMap<>();
                                    headers.put("Content-Type", "application/json; charset=UTF-8");
                                    headers.put("Authorization", TokenMain);

                                    Call<LoginModel> callLogin = apiInterface.LoginUserAccount(headers, Username, Password);

                                    callLogin.enqueue(new Callback<LoginModel>()
                                    {
                                        @Override
                                        public void onResponse(@NonNull Call<LoginModel> call, @NonNull Response<LoginModel> response)
                                        {
                                            if (response.isSuccessful() && response.body() != null)
                                            {
                                                final LoginModel loginModel = response.body();

                                                UserAccount.employeeID = loginModel.getEmployeeID();
                                                UserAccount.UserAccountID = String.valueOf(loginModel.getEmployeeID());
                                                UserAccount.CompleteName = loginModel.getCompleteName();

                                                Log.e(TAG, "UserAccount.employeeID: " + UserAccount.employeeID);

                                                UserAccountClass userAccountClass = new UserAccountClass();
                                                userAccountClass.setUsername(Username);

                                                try
                                                {
                                                    EncodeDecodeAES aes = new EncodeDecodeAES();
                                                    String encryptedPassword = EncodeDecodeAES.bytesToHex(aes.encrypt(Password));

                                                    userAccountClass.setPassword(encryptedPassword);
                                                }
                                                catch (Exception e)
                                                {
                                                    Log.e(TAG, e.toString());
                                                    userAccountClass.setPassword(Password);
                                                }

                                                userAccountClass.setRoleName(loginModel.getRoleName());
                                                userAccountClass.setCompleteName(loginModel.getCompleteName());
                                                userAccountClass.setAppID(loginModel.getAppID());
                                                userAccountClass.setPosition(loginModel.getPosition());
                                                userAccountClass.setDtAdded(loginModel.getRoleName());
                                                userAccountClass.setEmployeeID(loginModel.getEmployeeID());
                                                userAccountClass.setUserAccountID(String.valueOf(loginModel.getEmployeeID()));

                                                Cursor cursor = RepositoryUserAccount.realAllData2(getApplicationContext(), String.valueOf(loginModel.getEmployeeID()));

                                                if (cursor.getCount() == 0)
                                                {
                                                    RepositoryUserAccount.saveAccount(getApplicationContext(), userAccountClass);
                                                }

                                                ll_loading.setVisibility(View.GONE);
                                                ll_login_layout.setVisibility(View.VISIBLE);

                                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                                                SharedPreferences.Editor editor = prefs.edit();
                                                editor.putString("isLogin", "Done");
                                                editor.apply();

                                                Intent intent = new Intent(LoginActivity.this, GetAllDataActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else
                                            {
                                                String errorLog = response.isSuccessful() ? "Login: Server Response Null" : "Login Failed: " + convertingResponseError(response.errorBody());
                                                Log.e(TAG, errorLog);
                                                volleyCatch.writeToFile(errorLog);
                                                ll_loading.setVisibility(View.GONE);
                                                ll_login_layout.setVisibility(View.VISIBLE);
                                                Toast.makeText(LoginActivity.this, "Login Access Failed.", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<LoginModel> call, @NonNull Throwable t)
                                        {
                                            String errorLog = "Login Failure: " + t.getMessage();
                                            Log.e(TAG, errorLog);
                                            volleyCatch.writeToFile(errorLog);
                                            ll_loading.setVisibility(View.GONE);
                                            ll_login_layout.setVisibility(View.VISIBLE);
                                            Toast.makeText(LoginActivity.this, "Login Access Failed.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                else
                                {
                                    String errorLog = "Login Access Failed.";

                                    if (response.body() == null)
                                    {
                                        errorLog = "Login Token: Server Response Null";
                                    }
                                    else
                                    {
                                        errorLog = "Login Token Failed: " + convertingResponseError(response.errorBody());
                                    }

                                    Log.e(TAG, errorLog);
                                    volleyCatch.writeToFile(errorLog);

                                    ll_loading.setVisibility(View.GONE);
                                    ll_login_layout.setVisibility(View.VISIBLE);

                                    Toast.makeText(LoginActivity.this, "Login Access Failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(@NonNull Call<TokenModel> call, @NonNull Throwable t)
                            {
                                String errorLog = "Login Token Failure: " + t.getMessage();
                                Log.e(TAG, errorLog);
                                volleyCatch.writeToFile(errorLog);

                                ll_loading.setVisibility(View.GONE);
                                ll_login_layout.setVisibility(View.VISIBLE);
                                Toast.makeText(LoginActivity.this, "Login Access Failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "You have no internet connection. try again later.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        catch (Exception e)
        {
            String Logs = "Getting Login: " + e.getMessage();

            Log.e(TAG, Logs);
            volleyCatch.writeToFile(Logs);

            Toast.makeText(LoginActivity.this, "Login Access Failed.", Toast.LENGTH_SHORT).show();
        }
    }






    private boolean initCheckFields()
    {
        if (edt_username.getText().toString().isEmpty())
        {
            edt_username.setError("Please input Username");
        }
        else if (edt_password.getText().toString().isEmpty())
        {
            edt_password.setError("Please input Username");
        }
        else
        {
            return true;
        }
        return false;
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

    private void initCloseKeyboard()
    {
        View view = getCurrentFocus();

        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
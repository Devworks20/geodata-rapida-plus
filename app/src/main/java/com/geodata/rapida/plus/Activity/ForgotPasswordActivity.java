package com.geodata.rapida.plus.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.Retrofit.Client.APIClient;
import com.geodata.rapida.plus.Retrofit.Interface.APIClientInterface;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity
{
    private static final String TAG = ForgotPasswordActivity.class.getSimpleName();

    LinearLayout ll_loading, ll_first_layout, ll_second_layout;

    ImageView iv_back;

    TextView tv_returnToLogin, tv_returnToLogin2;

    EditText edt_emailAddressOrMobileNo;

    Button btn_reset_password;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initViews();
    }

    private void initViews()
    {
        ll_loading       = findViewById(R.id.ll_loading);
        ll_first_layout  = findViewById(R.id.ll_first_layout);
        ll_second_layout = findViewById(R.id.ll_second_layout);

        iv_back          = findViewById(R.id.iv_back);

        tv_returnToLogin  = findViewById(R.id.tv_returnToLogin);
        tv_returnToLogin2 = findViewById(R.id.tv_returnToLogin2);

        edt_emailAddressOrMobileNo = findViewById(R.id.edt_emailAddressOrMobileNo);

        btn_reset_password = findViewById(R.id.btn_reset_password);

        initListeners();
    }

    private void initListeners()
    {
        iv_back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        tv_returnToLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        tv_returnToLogin2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        btn_reset_password.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (checkFields())
                {
                    initCloseKeyboard();

                    ll_loading.setVisibility(View.VISIBLE);

                    initRequestForgotPasswordAPI(edt_emailAddressOrMobileNo.getText().toString());
                }
            }
        });
    }

    private void initRequestForgotPasswordAPI(String Email)
    {
        if (!haveNetworkConnection(this))
        {
            ll_loading.setVisibility(View.GONE);
            Toast.makeText(this, "You have no internet, please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        APIClientInterface apiInterface = APIClient.getClient().create(APIClientInterface.class);
        Call<String> postRequestForgotPassword = apiInterface.RequestForgotPassword(Email);

        postRequestForgotPassword.enqueue(new Callback<String>()
        {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
            {
                if (response.isSuccessful())
                {
                    String sResponse = response.body();

                    if (sResponse != null && sResponse.equalsIgnoreCase("Email sent."))
                    {
                        Log.e(TAG, "Response: " + sResponse);

                        ll_first_layout.setVisibility(View.GONE);
                        ll_second_layout.setVisibility(View.VISIBLE);
                        ll_loading.setVisibility(View.GONE);

                        Toast.makeText(ForgotPasswordActivity.this, "Email Sent.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        handleFailure("We couldn't find an account with the email you entered.");
                    }
                }
                else
                {
                    handleFailure(convertingResponseError(response.errorBody()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
            {
                handleFailure("We couldn't find an account with the email you entered.");
            }
        });
    }

    private void handleFailure(String errorMessage)
    {
        ll_loading.setVisibility(View.GONE);
        Toast.makeText(ForgotPasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
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

    private boolean checkFields()
    {
        if (edt_emailAddressOrMobileNo.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Please enter your Email address or Mobile number first.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed()
    {
        finish();
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
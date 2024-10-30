package com.geodata.rapida.plus.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.Tools.Settings;

public class LandingPageActivity extends AppCompatActivity
{
    ImageView iv_app_icon;
    TextView tv_app_name;

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customize_layout_landing_page);

        iv_app_icon = findViewById(R.id.iv_app_icon);
        tv_app_name = findViewById(R.id.tv_app_name);

        if (Settings.APPLICATION_NAME.equalsIgnoreCase("Seismic Resiliency Survey"))
        {
            iv_app_icon.setImageDrawable(getDrawable(R.drawable.srms_logo));

            tv_app_name.setText("Seismic Resiliency Survey");
        }
        else if (Settings.APPLICATION_NAME.equalsIgnoreCase("Emergency Damage Assessment"))
        {
            iv_app_icon.setImageDrawable(getDrawable(R.drawable.srms_logo));

            tv_app_name.setText("Emergency Damage Assessment");
        }
        else if (Settings.APPLICATION_NAME.equalsIgnoreCase("FEMA-154 Seismic Resiliency Inspection"))
        {
            iv_app_icon.setImageDrawable(getDrawable(R.drawable.icon_set_fema));

            tv_app_name.setText("FEMA-154 Seismic Resiliency Inspection");
        }
        else if (Settings.APPLICATION_NAME.equalsIgnoreCase("DESA"))
        {
            iv_app_icon.setImageDrawable(getDrawable(R.drawable.icon_set_desa));

            tv_app_name.setText("Detailed Evaluation and Safety Assessment (DESA)");
        }
        else if (Settings.APPLICATION_NAME.equalsIgnoreCase("RESA"))
        {
            iv_app_icon.setImageDrawable(getDrawable(R.drawable.icon_set_resa));

            tv_app_name.setText("Rapid Evaluation and Safety Assessment (RESA)");
        }
        else if (Settings.APPLICATION_NAME.equalsIgnoreCase("RVS"))
        {
            iv_app_icon.setImageDrawable(getDrawable(R.drawable.icon_set_rvs));

            tv_app_name.setText("Rapid Visual Screening (RVS)");
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                startActivity(new Intent(LandingPageActivity.this, LoginActivity.class));
                finish();
            }
        },3000);
    }
}
package com.geodata.rapida.plus.Tools;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GPSTracker extends Service
{
    private static final String TAG = GPSTracker.class.getSimpleName();

    Context mContext;

    // Flag for GPS status
    boolean isGPSEnabled = false;

    // Flag for network status
    boolean isNetworkEnabled = false;

    // Flag for GPS status
    boolean canGetLocation = false;

    Location location;
    double latitude;
    double longitude;


    Geocoder geocoder;

    // How many Geocoder should return our GPSTracker
    int geocoderMaxResults = 1;


    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1000; // 1000 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    Activity activity;

    public GPSTracker(Context context, Activity activity)
    {
        this.mContext = context;
        this.activity = activity;

        getLocation();
    }

    public Location getLocation()
    {
        try
        {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            // Getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER); //always NETWORK_PROVIDER


            // No network provider is enabled
            if (!isGPSEnabled && !isNetworkEnabled)
            {
                //Log.e(TAG, "GPS IS: " + isGPSEnabled + " AND Network IS: " + isNetworkEnabled);
            }
            else
            {
                this.canGetLocation = true;

                if (isNetworkEnabled)
                {
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_NETWORK_STATE) !=
                            PackageManager.PERMISSION_GRANTED &&

                            ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_WIFI_STATE) !=
                                    PackageManager.PERMISSION_GRANTED  &&

                            ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CHANGE_WIFI_STATE) !=
                                    PackageManager.PERMISSION_GRANTED  &&

                            ActivityCompat.checkSelfPermission(mContext, Manifest.permission.INTERNET) !=
                                    PackageManager.PERMISSION_GRANTED )
                    {
                        //TODO: nothing;
                    }
                    else
                    {
                        //Log.e(TAG, "Network Enabled");

                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, mLocationListener);

                        if (locationManager != null)
                        {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                            if (location != null)
                            {
                                latitude  = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                            else
                            {
                                //Log.e(TAG, "Network location NULL");
                            }
                        }
                        else
                        {
                            //Log.e(TAG, "Network locationManager NULL");
                        }
                    }
                }
            }

            // If GPS enabled, get latitude/longitude using GPS Services
            if (isGPSEnabled)
            {
                if (location == null)
                {
                    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 50);
                    }
                    else
                    {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, mLocationListener);

                        //Log.e(TAG, "GPS Enabled");

                        if (locationManager != null)
                        {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            if (location != null)
                            {
                                latitude  = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                            else
                            {
                                //Log.e(TAG, "GPS location NULL");
                            }
                        }
                        else
                        {
                            //Log.e(TAG, "GPS locationManager NULL");
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        return location;
    }

    private final LocationListener mLocationListener = new LocationListener()
    {

        @Override
        public void onLocationChanged(final Location location)
        {
            //Log.e(TAG, "onLocationChanged");

            if (location != null)
            {
                latitude  = location.getLatitude();
                longitude = location.getLongitude();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            //Log.e(TAG, "onStatusChanged");
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            //Log.e(TAG, "onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            //Log.e(TAG, "onProviderDisabled");
        }
    };

    public double getLatitude()
    {
        if (location != null)
        {
            latitude = location.getLatitude();
        }

        return latitude;
    }

    public double getLongitude()
    {
        if (location != null)
        {
            longitude = location.getLongitude();
        }

        return longitude;
    }

    public boolean canGetLocation()
    {

        return this.canGetLocation;
    }

    public void showSettingsAlert()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        alertDialog.setTitle("GPS Settings");

        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }



    public List<Address> getGeocoderAddress()
    {
          geocoder = new Geocoder(mContext, Locale.ENGLISH);
        //geocoder = new Geocoder(mContext, Locale.getDefault());

        if (location != null)
        {
            try
            {
                return geocoder.getFromLocation(latitude, longitude, this.geocoderMaxResults);
            }
            catch (IOException e)
            {
                //Log.e(TAG, "Impossible to connect to Geocoder", e);
            }
        }
        else
        {
            if (latitude != 0.0 && longitude != 0.0)
            {
                try
                {
                    return geocoder.getFromLocation(latitude, longitude, this.geocoderMaxResults);
                }
                catch (IOException e)
                {
                    //Log.e(TAG, "Impossible to connect to Geocoder", e);
                }
            }
        }
        return null;
    }

    public String getAddressLine()
    {
        List<Address> addresses = getGeocoderAddress();

        if (addresses != null && addresses.size() > 0)
        {
            Address address = addresses.get(0);

            return address.getAddressLine(0);
        }
        else
        {
            return null;
        }
    }

    public String getLocality()
    {
        List<Address> addresses = getGeocoderAddress();

        if (addresses != null && addresses.size() > 0)
        {
            Address address = addresses.get(0);

            return address.getLocality();
        }
        else
        {
            return null;
        }
    }

    public String getAdminArea()
    {
        List<Address> addresses = getGeocoderAddress();

        if (addresses != null && addresses.size() > 0)
        {
            Address address = addresses.get(0);

            return address.getAdminArea();
        }
        else
        {
            return null;
        }
    }

    public String getPostalCode()
    {
        List<Address> addresses = getGeocoderAddress();

        if (addresses != null && addresses.size() > 0)
        {
            Address address = addresses.get(0);

            return address.getPostalCode();
        }
        else
        {
            return null;
        }
    }

    public String getCountryName()
    {
        List<Address> addresses = getGeocoderAddress();

        if (addresses != null && addresses.size() > 0)
        {
            Address address = addresses.get(0);

            String countryName = address.getCountryName();

            return countryName;
        }
        else
        {
            return null;
        }
    }

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;

    }

}
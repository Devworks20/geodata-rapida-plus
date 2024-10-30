package com.geodata.rapida.plus.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.SQLite.Class.ImagesClass;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryImages;
import com.geodata.rapida.plus.Tools.GPS;
import com.geodata.rapida.plus.Tools.GPSTracker;
import com.geodata.rapida.plus.Tools.UserAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class AttachOtherImagesActivity extends AppCompatActivity
{
    private static final String TAG = AttachOtherImagesActivity.class.getSimpleName();

    public static int MY_PERMISSIONS_REQUEST_CODE_CAMERA  = 102;
    public static int MY_PERMISSIONS_REQUEST_CODE_STORAGE = 103;

    private static final int IMAGE_PICK_CAMERA_CODE = 1001;
    private static final int IMAGE_PICK_GALLERY_CODE = 1002;

    String MissionOrderID, SaveFolderName, Option, SeismicityRegion, CameraFileName,
            ImageName, ImageExtension, ImagePath, ImageType;

    Uri imageUri;

    private GoogleApiClient googleApiClient;
    GPSTracker gps;
    double latitude, longitude;
    String sLatitude, sLongitude;
    final static int REQUEST_LOCATION = 199;
    public static int MY_PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1002;
    Boolean isGPSStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attaching_image);

        enableLoc();

        initGetGPSState();

        initView();
    }

    private void enableLoc()
    {
        try
        {
            if (googleApiClient == null)
            {
                googleApiClient = new GoogleApiClient.Builder(this)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks()
                        {
                            @Override
                            public void onConnected(Bundle bundle)
                            {

                            }

                            @Override
                            public void onConnectionSuspended(int i)
                            {
                                googleApiClient.connect();
                            }
                        })
                        .addOnConnectionFailedListener(connectionResult ->
                                Log.d("Location error", "Location error " + connectionResult.getErrorCode())
                        ).build();
                googleApiClient.connect();

                LocationRequest locationRequest = LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(30 * 1000);
                locationRequest.setFastestInterval(5 * 1000);
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest);

                builder.setAlwaysShow(true);

                PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
                result.setResultCallback(result1 ->
                {
                    final Status status = result1.getStatus();

                    if (status.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED)
                    {
                        try
                        {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(AttachOtherImagesActivity.this, REQUEST_LOCATION);
                        }
                        catch (IntentSender.SendIntentException e)
                        {
                            // Ignore the error.
                        }
                    }
                });
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initGetGPSState()
    {
        try
        {
            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                isGPSStatus = true;

                getLocation(null);
            }
            else
            {
                isGPSStatus = false;
            }

            this.registerReceiver(GPSStateReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    //region Broadcast Receiver GPS
    private final BroadcastReceiver GPSStateReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            try
            {
                if (intent.getAction().matches(LocationManager.PROVIDERS_CHANGED_ACTION))
                {
                    LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    boolean isON = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                    if (isON)
                    {
                        if (!isGPSStatus)
                        {
                            getLocation(null);

                            isGPSStatus = true;
                        }
                    }
                    else
                    {
                        isGPSStatus = true;
                    }
                }
            }
            catch (Exception e)
            {
                Log.e(TAG, e.toString());
            }
        }
    };
    //endregion Device Data

    public void getLocation(String ImagePath2)
    {
        Log.e(TAG, "THIS IS CALLED");

        try
        {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(AttachOtherImagesActivity.this, new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                }, MY_PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION);
            }
            else
            {
                gps = new GPSTracker(getApplicationContext(), AttachOtherImagesActivity.this);

                // Check if GPS enabled
                if (gps.canGetLocation())
                {
                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            latitude  = gps.getLatitude();
                            longitude = gps.getLongitude();

                            sLatitude  = Double.toString(latitude);
                            sLongitude = Double.toString(longitude);

                            String addressStr = gps.getAddressLine();

                            if((latitude != 0.0 && longitude != 0.0) && addressStr!= null)
                            {
                                Log.e(TAG, "CURRENT ADDRESS: " + addressStr);

                                if (ImagePath2 != null)
                                {
                                    try
                                    {
                                        ExifInterface exif = new ExifInterface(ImagePath);
                                        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE,      GPS.convert(latitude));
                                        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF,  GPS.latitudeRef(latitude));
                                        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE,     GPS.convert(longitude));
                                        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, GPS.longitudeRef(longitude));
                                        exif.saveAttributes();
                                    }
                                    catch (Exception e)
                                    {
                                        Log.e(TAG, e.toString());
                                    }
                                }
                            }
                        }
                    }, 500);
                }
                else
                {
                    gps.showSettingsAlert();
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }


    private void initView()
    {
        SaveFolderName = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/SRI/" + ".Attachments";

        Bundle extras = getIntent().getExtras();

        if(extras != null)
        {
            MissionOrderID   = extras.getString("MissionOrderID");
            Option           = extras.getString("Option");
            SeismicityRegion = extras.getString("SeismicityRegion");
            ImageType        = extras.getString("ImageType");
        }

        initListeners();
    }

    private void initListeners()
    {
        try
        {
            if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getApplicationContext()),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                }, MY_PERMISSIONS_REQUEST_CODE_STORAGE);
            }
            else
            {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{
                            Manifest.permission.CAMERA
                    }, MY_PERMISSIONS_REQUEST_CODE_CAMERA);
                }
                else
                {
                    if (Option!= null && Option.equals("CAMERA"))
                    {
                        initGetPictureFromCamera();
                    }
                    else
                    {
                        initGetPictureGallery();
                    }
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initGetPictureFromCamera()
    {
        try
        {
            File fStorageForPhotos = new File(SaveFolderName);

            if (!fStorageForPhotos.exists()) // Create a new folder if  folder not exist
            {
                fStorageForPhotos.mkdirs();
            }

            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy hh-mm-ss a");
            Date now       = new Date(System.currentTimeMillis());
            String DateNow = dateFormat.format(now);

            ImageName =  ImageType + "-" + DateNow;
            ImageExtension = "png";
            ImagePath = SaveFolderName + "/" + ImageName + "." + ImageExtension;

            File outFile = new File(ImagePath);

            CameraFileName = outFile.toString();
            Uri outURI     = Uri.fromFile(outFile);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, outURI);
            startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initGetPictureGallery()
    {
        try
        {
            /*
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_PICK_GALLERY_CODE);
            */

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        try
        {
            super.onActivityResult(requestCode, resultCode, data);

            if (resultCode == Activity.RESULT_OK)
            {
                if (requestCode == IMAGE_PICK_CAMERA_CODE)
                {
                    imageUri = null;

                    if (data != null)
                    {
                        imageUri = data.getData();
                    }
                    else
                    {
                        imageUri = Uri.fromFile(new File(CameraFileName));
                    }

                    try
                    {
                        File file = new File(CameraFileName);

                        if (!file.exists())
                        {
                            file.mkdir();
                        }

                        initSavePhoto("Camera");

                        //Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getApplicationContext()).getContentResolver(), PhotoURI);
                        //Bitmap photo = (Bitmap)data.getExtras().get("data");
                        //createDirectoryAndSaveFile(photo);
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
                else if (requestCode == IMAGE_PICK_GALLERY_CODE)
                {
                    imageUri = null;

                    if (data != null)
                    {
                        imageUri = data.getData();
                    }

                    if (imageUri != null)
                    {
                        try
                        {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getApplicationContext()).getContentResolver(), imageUri);
                            //Bitmap bitMapCustomize = Bitmap.createScaledBitmap(bitmap, 1000, 1000, false);
                            createDirectoryAndSaveFile(bitmap);
                        }
                        catch (Exception e)
                        {
                            Log.e(TAG, "Saving Error: " +  e.toString());

                            finish();
                        }
                    }
                    else
                    {
                        finish();

                        Toast.makeText(this, "There is something, Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else
            {
                finish();

                Toast.makeText(this, "Capturing Canceled!", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());

            finish();

            Toast.makeText(this, "Capturing Canceled!", Toast.LENGTH_SHORT).show();
        }
    }

    private void createDirectoryAndSaveFile(Bitmap imageToSave)
    {
        try
        {
            if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getApplicationContext()),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED

                    && ActivityCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                Log.e(TAG, "Permission is denied");
            }
            else
            {
                try
                {
                    File fStorageForPhotos = new File(SaveFolderName);

                    if (!fStorageForPhotos.exists()) // Create a new folder if  folder not exist
                    {
                        fStorageForPhotos.mkdirs();
                    }

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy hh-mm-ss a");
                    Date now       = new Date(System.currentTimeMillis());
                    String DateNow = dateFormat.format(now);

                    ImageName      =  ImageType + "-" + DateNow;
                    ImageExtension = "png";
                    ImagePath      = SaveFolderName + "/" + ImageName + "." + ImageExtension;

                    File file = new File(SaveFolderName, ImageName);

                    try //FILE TO PNG.
                    {
                        FileOutputStream out = new FileOutputStream(file + "." + ImageExtension);

                        imageToSave.setHasAlpha(true);
                        imageToSave.compress(Bitmap.CompressFormat.PNG, 100, out);

                        out.flush();
                        out.close();

                        initSavePhoto("Gallery");
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, "Saving Image: " + e.toString());
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, "Saving Image: " + e.toString());
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Saving Image: " + e.toString());
        }
    }

    private void initSavePhoto(String option)
    {
        try
        {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy hh:mm:ss a");
            Date now = new Date(System.currentTimeMillis());
            String dateAdded = dateFormat.format(now);

            ImagesClass imagesClass = new ImagesClass();

            imagesClass.setUserAccountID(UserAccount.UserAccountID);
            imagesClass.setImageID(MissionOrderID);
            imagesClass.setCategory(SeismicityRegion);
            imagesClass.setImageType(ImageType);
            imagesClass.setImageName(ImageName);
            imagesClass.setImagePath(ImagePath);
            imagesClass.setImageExtension(ImageExtension);
            imagesClass.setDtAdded(dateAdded);

            RepositoryImages.savePhoto(getApplicationContext(), imagesClass);

            if (option.equalsIgnoreCase("Camera"))
            {
                getLocation(ImagePath);
            }

            Intent dataReturn = new Intent();
            dataReturn.putExtra("Result", "Success");
            setResult(RESULT_OK, dataReturn);
            finish();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }


    private void initSettingsPrompt(final String requestPermission, final int requestPermissionCode)
    {
        try
        {
            final AlertDialog.Builder ADSettings = new AlertDialog.Builder(AttachOtherImagesActivity.this, R.style.MyAlertDialogStyle);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.custom_dialog_title, null);
            TextView textView = view.findViewById(R.id.tv_dialog_title);
            String sTitle = "Permission Request";
            textView.setText(sTitle);
            ADSettings.setCustomTitle(view);
            ADSettings.setMessage("You need to allow the Permission Requests.");
            ADSettings.setCancelable(true);
            ADSettings.setNeutralButton("SETTINGS", (dialog, which) ->
            {
                dialog.dismiss();

                final Intent i = new Intent();
                i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                i.addCategory(Intent.CATEGORY_DEFAULT);
                i.setData(Uri.parse("package:" + AttachOtherImagesActivity.this.getPackageName()));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                getApplication().startActivity(i);
            });
            ADSettings.setNegativeButton("CLOSE", null);
            ADSettings.setPositiveButton("OK", (dialog, which) ->
            {
                dialog.dismiss();

                switch (requestPermission)
                {
                    case "CAMERA":
                        requestPermissions(new String[]
                                {
                                        Manifest.permission.CAMERA
                                }, requestPermissionCode);
                        break;
                    case "STORAGE":
                        requestPermissions(new String[]
                                {
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.CAMERA
                                }, requestPermissionCode);
                        break;
                    case "ACCESS_FINE_LOCATION":
                        requestPermissions(new String[]
                                {
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                }, requestPermissionCode);
                        break;
                }
            });
            ADSettings.show();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == MY_PERMISSIONS_REQUEST_CODE_STORAGE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                if (ActivityCompat.checkSelfPermission(AttachOtherImagesActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    initSettingsPrompt("CAMERA", MY_PERMISSIONS_REQUEST_CODE_CAMERA);
                }
                else
                {
                    if (Option!= null &&  Option.equals("CAMERA"))
                    {
                        initGetPictureFromCamera();
                    }
                    else
                    {
                        initGetPictureGallery();
                    }
                }
            }
            else
            {
                initSettingsPrompt("STORAGE", MY_PERMISSIONS_REQUEST_CODE_STORAGE);
            }
        }
        else if (requestCode == MY_PERMISSIONS_REQUEST_CODE_CAMERA)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                if (Option!= null &&  Option.equals("CAMERA"))
                {
                    initGetPictureFromCamera();
                }
                else
                {
                    initGetPictureGallery();
                }
            }
            else
            {
                initSettingsPrompt("CAMERA", MY_PERMISSIONS_REQUEST_CODE_CAMERA);
            }
        }
        else if (requestCode == MY_PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED)
                {
                    initSettingsPrompt("ACCESS_FINE_LOCATION", MY_PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION);
                }
                else
                {
                    getLocation(null);
                }
            }
            else
            {
                initSettingsPrompt("ACCESS_FINE_LOCATION", MY_PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION);
            }
        }
    }

    public void addImageToGallery(final String filePath, final Context context)
    {
        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        imageUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

}
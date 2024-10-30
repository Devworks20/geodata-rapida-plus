package com.geodata.rapida.plus.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.SQLite.Class.InspectorSignatureClass;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryInspectorSignature;
import com.geodata.rapida.plus.Tools.CustomSignaturePad;
import com.geodata.rapida.plus.Tools.UserAccount;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class CreateSignatureActivity extends AppCompatActivity
{
    private static final String TAG = CreateSignatureActivity.class.getSimpleName();

    ImageView iv_back;

    Button btn_clear, btn_save;
    CustomSignaturePad customSignaturePad;

    LinearLayout ll_add_signature, ll_loading_screen;

    Uri imageUri;

    String ID, Option, MissionOrderID, SaveFolderName,
            SignatureName, SignatureExtension, SignaturePath, CameraFileName;

    int MY_PERMISSIONS_REQUEST_CODE_STORAGE = 1001, IMAGE_PICK_GALLERY_CODE = 1002,
            MY_PERMISSIONS_REQUEST_CODE_CAMERA = 1003, IMAGE_PICK_CAMERA_CODE = 1004;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_signature);

        initViews();
    }

    private void initViews()
    {
        SaveFolderName = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/SRI/" + ".Signatures";

        //SaveFolderName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures";

        ll_add_signature    = findViewById(R.id.ll_add_signature);
        ll_loading_screen   = findViewById(R.id.ll_loading_screen);

        iv_back             = findViewById(R.id.iv_back);
        btn_clear           = findViewById(R.id.btn_clear);
        btn_save            = findViewById(R.id.btn_save);
        customSignaturePad  = findViewById(R.id.custom_signature_pad);

        Bundle extras = getIntent().getExtras();

        if(extras != null)
        {
            ID             = extras.getString("ID");
            Option         = extras.getString("Option");
            MissionOrderID = extras.getString("MissionOrderID");

            if (Option.equalsIgnoreCase("Attach Signature") || Option.equalsIgnoreCase("Capture Signature"))
            {
                ll_add_signature.setVisibility(View.GONE);
                ll_loading_screen.setVisibility(View.VISIBLE);

                initAttachSignature();
            }
            else
            {
                ll_loading_screen.setVisibility(View.GONE);
                ll_add_signature.setVisibility(View.VISIBLE);
            }
        }

        initListeners();
    }

    private void initAttachSignature()
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
                    if (Option!= null && Option.equals("Capture Signature"))
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

            SignatureName      = "Created Signature-" + DateNow;
            SignatureExtension = "png";
            SignaturePath = SaveFolderName + "/" + SignatureName + "." + SignatureExtension;

            File outFile = new File(SignaturePath);

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
            /*Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_PICK_GALLERY_CODE);*/

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
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

        btn_clear.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                customSignaturePad.clearPad();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                initSaved(customSignaturePad.getSignatureBitmap());
            }
        });
    }

    private void initSavePhoto()
    {
        try
        {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy hh:mm:ss a");
            Date now = new Date(System.currentTimeMillis());
            String dateAdded = dateFormat.format(now);

            InspectorSignatureClass inspectorSignatureClass = new InspectorSignatureClass();

            inspectorSignatureClass.setUserAccountID(UserAccount.UserAccountID);
            inspectorSignatureClass.setMissionOrderID(MissionOrderID);
            inspectorSignatureClass.setSignatureID(ID);

            inspectorSignatureClass.setSignatureName(SignatureName);
            inspectorSignatureClass.setSignatureExtension(SignatureExtension);
            inspectorSignatureClass.setSignaturePath(SignaturePath);
            inspectorSignatureClass.setDtAdded(dateAdded);

            Cursor cursor = RepositoryInspectorSignature.realAllData2(getApplicationContext(), UserAccount.UserAccountID, MissionOrderID, ID);

            if (cursor.getCount()!=0)
            {
                if (cursor.moveToFirst())
                {
                    String ID = cursor.getString(cursor.getColumnIndex("ID"));

                    RepositoryInspectorSignature.updateInspectorSignature(getApplicationContext(), ID, inspectorSignatureClass);
                }
            }
            else
            {
                RepositoryInspectorSignature.saveInspectorSignature(getApplicationContext(), inspectorSignatureClass);
            }

            Intent returnIntent  = new Intent();
            returnIntent .putExtra("Result", "Success");
            setResult(Activity.RESULT_OK, returnIntent );
            finish();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initAskPermission()
    {
        Dexter.withContext(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener()
                {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport)
                    {
                        //Toast.makeText(SketchPadActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken)
                    {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == IMAGE_PICK_GALLERY_CODE)
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

                        initSaved(bitmap);
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, "Saving Error: " +  e.toString());
                    }
                }
                else
                {
                    finish();
                    Toast.makeText(this, "There is something, Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE)
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

                    initSavePhoto();

                    //Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getApplicationContext()).getContentResolver(), PhotoURI);
                    //Bitmap photo = (Bitmap)data.getExtras().get("data");
                    //createDirectoryAndSaveFile(photo);
                }
                catch (Exception e)
                {
                    Log.e(TAG, e.toString());
                }
            }
        }
        else
        {
            finish();
        }
    }

    private void initSaved(Bitmap imageToSave)
    {
        try
        {
            if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getApplicationContext()),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED

                    && ActivityCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                initAskPermission();
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

                    SignatureName      = "Created Signature-" + DateNow;
                    SignatureExtension = "png";
                    SignaturePath      = SaveFolderName + "/" + SignatureName + "." + SignatureExtension;

                    File file = new File(SaveFolderName, SignatureName);

                    if (file.exists())
                    {
                        file.delete();
                    }

                    try
                    {
                        FileOutputStream out = new FileOutputStream(file + "." + SignatureExtension);
                        //out.write(bitmapData);

                        //Saving 2
                        imageToSave.setHasAlpha(true);
                        imageToSave.compress(Bitmap.CompressFormat.PNG, 100, out);

                        out.flush();
                        out.close();

                        addImageToGallery(SignaturePath, getApplicationContext());
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, "Saving Signature: " + e.toString());
                    }

                    //SAVING TO DB.
                    initSavePhoto();

                }
                catch (Exception e)
                {
                    Log.e(TAG, "Saving Signature: " + e.toString());
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Saving Signature: " + e.toString());
        }
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        if (requestCode == MY_PERMISSIONS_REQUEST_CODE_STORAGE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                if (Option!= null && Option.equals("Capture Signature"))
                {
                    initGetPictureFromCamera();
                }
                else if (Option!= null && Option.equals("Attach Signature"))
                {
                    initGetPictureGallery();
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
                if (Option!= null && Option.equals("Capture Signature"))
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
    }

    private void initSettingsPrompt(final String requestPermission, final int requestPermissionCode)
    {
        try
        {
            final AlertDialog.Builder ADSettings = new AlertDialog.Builder(CreateSignatureActivity.this, R.style.MyAlertDialogStyle);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.custom_dialog_title, null);
            TextView textView = view.findViewById(R.id.tv_dialog_title);
            String sTitle = "Permission Request";
            textView.setText(sTitle);
            ADSettings.setCustomTitle(view);
            ADSettings.setMessage("You need to allow the Permission Requests.");
            ADSettings.setCancelable(true);
            ADSettings.setNeutralButton("SETTINGS", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();

                    final Intent i = new Intent();
                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    i.setData(Uri.parse("package:" + CreateSignatureActivity.this.getPackageName()));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    getApplication().startActivity(i);
                }
            });
            ADSettings.setNegativeButton("CLOSE", null);
            ADSettings.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();

                    if (requestPermission.equals("STORAGE"))
                    {
                        requestPermissions(new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA
                        }, requestPermissionCode);
                    }
                    else if (requestPermission.equals("CAMERA"))
                    {
                        requestPermissions(new String[]{
                                Manifest.permission.CAMERA
                        }, requestPermissionCode);
                    }

                }
            });
            ADSettings.show();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    public void addImageToGallery(final String filePath, final Context context)
    {
        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        imageUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

}
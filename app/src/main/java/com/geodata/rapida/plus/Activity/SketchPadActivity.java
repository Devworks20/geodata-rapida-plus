package com.geodata.rapida.plus.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.SQLite.Class.SketchImagesClass;
import com.geodata.rapida.plus.SQLite.Repository.RepositorySketchImages;
import com.geodata.rapida.plus.Tools.CustomSignaturePad;
import com.geodata.rapida.plus.Tools.UserAccount;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import yuku.ambilwarna.AmbilWarnaDialog;

public class SketchPadActivity extends AppCompatActivity
{
    private static final String TAG = SketchPadActivity.class.getSimpleName();

    int defaultColor ;

    CustomSignaturePad signaturePad;
    ImageView iv_back, iv_eraser, iv_done;;

    SeekBar seekBar;

    TextView txtPenSize, tv_minus, tv_plus;

    ImageButton  imgEraser, imgColor, imgSave;
    Button btn_save;

    String MissionOrderID, SaveFolderName, Option, SeismicityRegion, ImageName, ImageExtension, ImagePath,
            CameraFileName;

    LinearLayout ll_sketch_draw, ll_sketch_uploading;

    int MY_PERMISSIONS_REQUEST_CODE_STORAGE = 1001, IMAGE_PICK_GALLERY_CODE = 1002, IMAGE_PICK_CAMERA_CODE = 1003;

    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sketch_pad);

        initAskPermission();

        initViews();
    }

    private void initViews()
    {
        defaultColor = ContextCompat.getColor(SketchPadActivity.this, R.color.black);

        signaturePad = findViewById(R.id.signature_view);

        iv_back     = findViewById(R.id.iv_back);
        seekBar     = findViewById(R.id.penSize);
        txtPenSize  = findViewById(R.id.txtPenSize);
        tv_minus    = findViewById(R.id.tv_minus);
        tv_plus     = findViewById(R.id.tv_plus);
        imgEraser   = findViewById(R.id.btnEraser);
        imgColor    = findViewById(R.id.btnColor);
        imgSave     = findViewById(R.id.btnSave);
        iv_eraser   = findViewById(R.id.iv_eraser);
        iv_done     = findViewById(R.id.iv_done);

        btn_save    = findViewById(R.id.btn_save);

        ll_sketch_draw      = findViewById(R.id.ll_sketch_draw);
        ll_sketch_uploading = findViewById(R.id.ll_sketch_uploading);

        SaveFolderName = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/SRI/" + ".Sketches";


        Bundle extras = getIntent().getExtras();

        if(extras != null)
        {
            MissionOrderID   = extras.getString("MissionOrderID");
            Option           = extras.getString("Option");
            SeismicityRegion = extras.getString("SeismicityRegion");

            if (Option.equalsIgnoreCase("Sketch Upload"))
            {
                ll_sketch_draw.setVisibility(View.GONE);
                ll_sketch_uploading.setVisibility(View.VISIBLE);

                initSketchUpload();
            }
            else if (Option.equalsIgnoreCase("Sketch Capture"))
            {
                ll_sketch_draw.setVisibility(View.GONE);
                ll_sketch_uploading.setVisibility(View.VISIBLE);

                initSketchCapture();
            }
            else
            {
                ll_sketch_uploading.setVisibility(View.GONE);
                ll_sketch_draw.setVisibility(View.VISIBLE);
            }
        }

        initListeners();
    }

    private void initSketchUpload()
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
                initGetPictureGallery();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initSketchCapture()
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

            ImageName      = "Sketch Drawing - " + DateNow;
            ImageExtension = "png";
            ImagePath      = SaveFolderName + "/" + ImageName + "." + ImageExtension;

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



    private void initListeners()
    {
        iv_back.setOnClickListener(v -> finish());

        // Example: Use a seekBar to change pen size
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtPenSize.setText(progress + "dp");
                signaturePad.setMinimumWidth(progress);
                signaturePad.setMinimumHeight(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        tv_minus.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v)
            {
                String newString = txtPenSize.getText().toString().replace("dp", "");

                int penSize = Integer.parseInt(newString);

                if (penSize > 0)
                {
                    int finalSize = penSize - 1;

                    seekBar.setProgress(finalSize);
                    txtPenSize.setText(finalSize + "dp");
                }
            }
        });

        tv_plus.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v)
            {
                String newString = txtPenSize.getText().toString().replace("dp", "");

                int penSize = Integer.parseInt(newString);

                if (penSize != 100)
                {
                    int finalSize = penSize + 1;

                    seekBar.setProgress(finalSize);
                    txtPenSize.setText(finalSize + "dp");
                }
            }
        });

        imgEraser.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                signaturePad.clearPad();
            }
        });

        imgColor.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openColorPicker();
            }
        });

        imgSave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (signaturePad.getSignatureBitmap() != null)
                {
                    initSaved(signaturePad.getSignatureBitmap());
                }
                else
                {
                    Toast.makeText(SketchPadActivity.this, "Invalid!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (signaturePad.getSignatureBitmap() != null)
                {
                    DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            switch (which)
                            {
                                case DialogInterface.BUTTON_POSITIVE:
                                    initSaved(signaturePad.getSignatureBitmap());
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(SketchPadActivity.this);
                    builder.setTitle("Save the Sketched ?");
                    builder.setCancelable(false);
                    builder.setNegativeButton("NO", onClickListener);
                    builder.setPositiveButton("YES", onClickListener);
                    builder.show();
                }
                else
                {
                    Toast.makeText(SketchPadActivity.this, "Invalid!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        iv_eraser.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                signaturePad.clearPad();
            }
        });

        iv_done.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (signaturePad.getSignatureBitmap() != null)
                {
                    DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            switch (which)
                            {
                                case DialogInterface.BUTTON_POSITIVE:
                                    initSaved(signaturePad.getSignatureBitmap());
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(SketchPadActivity.this);
                    builder.setTitle("Save the Sketched ?");
                    builder.setCancelable(false);
                    builder.setNegativeButton("NO", onClickListener);
                    builder.setPositiveButton("YES", onClickListener);
                    builder.show();
                }
                else
                {
                    Toast.makeText(SketchPadActivity.this, "Invalid!", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    private void openColorPicker()
    {
        try
        {
            AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(this, defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                @Override
                public void onCancel(AmbilWarnaDialog dialog)
                {

                }

                @Override
                public void onOk(AmbilWarnaDialog dialog, int color)
                {
                    defaultColor = color;
                    signaturePad.setPenColor(defaultColor);
                }
            });

            ambilWarnaDialog.show();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
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

                    ImageName      = "Sketch Drawing - " + DateNow;
                    ImageExtension = "png";
                    ImagePath      = SaveFolderName + "/" + ImageName + "." + ImageExtension;

                    File file = new File(SaveFolderName, ImageName);

                    try //FILE TO PNG.
                    {
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();

                        imageToSave.setHasAlpha(true);
                        imageToSave.compress(Bitmap.CompressFormat.PNG, 0, bos);
                        byte[] bitmapData = bos.toByteArray();


                        FileOutputStream out = new FileOutputStream(file + "." + ImageExtension);
                        out.write(bitmapData);

                        out.flush();
                        out.close();
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, "Saving Sketch: " + e.toString());
                    }

                    initSavePhoto();
                }
                catch (Exception e)
                {
                    Log.e(TAG, "Saving Sketch: " + e.toString());
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Saving Sketch: " + e.toString());
        }
    }

    private void initSavePhoto()
    {
        try
        {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy hh:mm:ss a");
            Date now = new Date(System.currentTimeMillis());
            String dateAdded = dateFormat.format(now);

            SketchImagesClass sketchImagesClass = new SketchImagesClass();

            sketchImagesClass.setUserAccountID(UserAccount.UserAccountID);
            sketchImagesClass.setSketchID(MissionOrderID);
            sketchImagesClass.setCategory(SeismicityRegion);
            sketchImagesClass.setSketchName(ImageName);
            sketchImagesClass.setSketchExtension(ImageExtension);
            sketchImagesClass.setSketchPath(ImagePath);
            sketchImagesClass.setDtAdded(dateAdded);

            RepositorySketchImages.savePhoto(getApplicationContext(), sketchImagesClass);

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

}
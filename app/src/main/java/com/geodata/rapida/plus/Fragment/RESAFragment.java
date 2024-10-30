package com.geodata.rapida.plus.Fragment;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.geodata.rapida.plus.Activity.CreateSignatureActivity;
import com.geodata.rapida.plus.Activity.PreviewReportRESAActivity;
import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.SQLite.Class.InspectorSignatureClass;
import com.geodata.rapida.plus.SQLite.Class.RESAClass;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryInspectorSignature;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryOnlineAssignedInspectors;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryOnlineMissionOrders;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryRESA;
import com.geodata.rapida.plus.Tools.MyExceptionHandler;
import com.geodata.rapida.plus.Tools.UserAccount;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class RESAFragment extends Fragment
{
    private static final String TAG = RESAFragment.class.getSimpleName();

    View view;

    EditText edt_set_date, edt_set_time, edt_affiliation, edt_bldg_name, edt_bldg_address, edt_bldg_contact, edt_NoOfStoreyAboveGround,
            edt_NoOfStoreyBelowGround, edt_Approx, edt_NoOfResidentialUnits, edt_NoOfCommercialUnits, edt_ObservedConditions_Others,
            edt_ObservedConditions_Comments, edt_PreviousPosting_Date, edt_bldgBarricades, edt_bldgOtherRecommendation, edt_bldgSecondComments,
            edt_InspectedBy;

    Spinner spnr_TypeOfConstruction, spnr_PrimaryOccupancy, spnr_EstimatedBldgDamage, spnr_PreviousPosting, spnr_bldgDetailed;

    RadioButton rb_collapse_none, rb_collapse_minor, rb_collapse_moderate, rb_collapse_severe,
            rb_bldg_none, rb_bldg_minor, rb_bldg_moderate, rb_bldg_severe,
            rb_cracking_none, rb_cracking_minor, rb_cracking_moderate, rb_cracking_severe,
            rb_chimney_none, rb_chimney_minor, rb_chimney_moderate, rb_chimney_severe,
            rb_ground_none, rb_ground_minor, rb_ground_moderate, rb_ground_severe,
            rb_GreenPlacard, rb_YellowPlacard, rb_RedPlacard;

    ImageView iv_building_image, imgv_collapse, imgv_bldg, imgv_cracking, imgv_chimney,
            imgv_ground, imgv_PlaceCard, imgv_bldgSignature;

    Bitmap bmCollapse, bmBuildingStoryLeaning, bmCracking, bmChimney,
            bmGroundSlope, bmPosting, bmInspectorSignature;

    CheckBox cb_bldgBarricade, cb_bldgDetailed, cb_bldgOtherRecommendation;

    Button btn_take_photo, btn_view_photo, btn_save, btn_preview;

    Calendar iCalendar;

    //MAP VIEW
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE  = 1234;
    private static final float DEFAULT_ZOOM = 18f;

    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    MarkerOptions markerOptions;
    SupportMapFragment mapFragment;
    LatLng MapPosition;

    LatLng defaultCoordinate = new LatLng(Double.parseDouble("14.65735"), Double.parseDouble("120.96448"));

    String SaveFolderName,
            ImageName, ImageExtension, CameraFileName, sDateNow;

    int IMAGE_PICK_CAMERA_CODE          = 100, IMAGE_PICK_GALLERY_CODE          = 101,
        IMAGE_PICK_CAMERA_COLLAPSE      = 102, IMAGE_PICK_GALLERY_COLLAPSE      = 103,
        IMAGE_PICK_CAMERA_STORY_LEANING = 104, IMAGE_PICK_GALLERY_STORY_LEANING = 105,
        IMAGE_PICK_CAMERA_CRACKING      = 106, IMAGE_PICK_GALLERY_CRACKING      = 107,
        IMAGE_PICK_CAMERA_CHIMNEY       = 108, IMAGE_PICK_GALLERY_STORY_CHIMNEY = 109,
        IMAGE_PICK_CAMERA_GROUND        = 110, IMAGE_PICK_GALLERY_GROUND        = 111,
        IMAGE_PICK_CAMERA_PLACARD       = 112, IMAGE_PICK_GALLERY_PLACARD       = 113,
        IMAGE_PICK_CAMERA_SIGNATURE     = 114, IMAGE_PICK_GALLERY_SIGNATURE       = 115;

    Uri imageUri;

    String MissionOrderNo, MissionOrderID, SeismicityRegion, dtAdded, PdfFolderName, SignaturePath;

    ProgressDialog pDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(getActivity())); //Getting Crash - Restart

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_resa_layout, container, false);

        initViews();

        return view;
    }

    private void initViews()
    {
        if (haveNetworkConnection(requireContext()))
        {
            new initSetTimeAndDate().execute();
        }
        else
        {
            initSetDateTimeOffline();
        }

        pDialog = new ProgressDialog(getActivity());

        PdfFolderName =  "SRI" + "/" + "RESA";

        try
        {
            Bundle extras = requireActivity().getIntent().getExtras();

            if(extras != null)
            {
                MissionOrderNo   = extras.getString("MissionOrderNo");
                MissionOrderID   = extras.getString("MissionOrderID");
                SeismicityRegion = extras.getString("SeismicityRegion");
                dtAdded          = extras.getString("dtAdded");
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        SaveFolderName = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/SRI/RESA/Attachments/";

        edt_set_date = view.findViewById(R.id.edt_set_date);
        edt_set_time = view.findViewById(R.id.edt_set_time);

        edt_affiliation            = view.findViewById(R.id.edt_affiliation);
        edt_bldg_name              = view.findViewById(R.id.edt_bldg_name);
        edt_bldg_address           = view.findViewById(R.id.edt_bldg_address);
        edt_bldg_contact           = view.findViewById(R.id.edt_bldg_contact);
        edt_NoOfStoreyAboveGround  = view.findViewById(R.id.edt_NoOfStoreyAboveGround);

        edt_NoOfStoreyBelowGround      = view.findViewById(R.id.edt_NoOfStoreyBelowGround);
        edt_Approx                     = view.findViewById(R.id.edt_Approx);
        edt_NoOfResidentialUnits       = view.findViewById(R.id.edt_NoOfResidentialUnits);
        edt_NoOfCommercialUnits        = view.findViewById(R.id.edt_NoOfCommercialUnits);
        edt_ObservedConditions_Others  = view.findViewById(R.id.edt_ObservedConditions_Others);

        edt_ObservedConditions_Comments  = view.findViewById(R.id.edt_ObservedConditions_Comments);
        edt_PreviousPosting_Date         = view.findViewById(R.id.edt_PreviousPosting_Date);
        edt_bldgBarricades               = view.findViewById(R.id.edt_bldgBarricadesComment);
        edt_bldgBarricades.setEnabled(false);
        edt_bldgOtherRecommendation      = view.findViewById(R.id.edt_bldgOtherRecommendation);
        edt_bldgOtherRecommendation.setEnabled(false);
        edt_bldgSecondComments           = view.findViewById(R.id.edt_bldgSecondComments);
        edt_InspectedBy                  = view.findViewById(R.id.edt_InspectedBy);
        edt_InspectedBy.setText(UserAccount.CompleteName);

        spnr_TypeOfConstruction  = view.findViewById(R.id.spnr_TypeOfConstruction);
        spnr_PrimaryOccupancy    = view.findViewById(R.id.spnr_PrimaryOccupancy);
        spnr_EstimatedBldgDamage = view.findViewById(R.id.spnr_EstimatedBldgDamage);
        spnr_PreviousPosting     = view.findViewById(R.id.spnr_PreviousPosting);
        spnr_bldgDetailed        = view.findViewById(R.id.spnr_bldgDetailed);
        spnr_bldgDetailed.setEnabled(false);
        rb_collapse_none     =  view.findViewById(R.id.rb_collapse_none);
        rb_collapse_minor    =  view.findViewById(R.id.rb_collapse_minor);
        rb_collapse_moderate =  view.findViewById(R.id.rb_collapse_moderate);
        rb_collapse_severe   =  view.findViewById(R.id.rb_collapse_severe);

        rb_bldg_none         =  view.findViewById(R.id.rb_bldg_none);
        rb_bldg_minor        =  view.findViewById(R.id.rb_bldg_minor);
        rb_bldg_moderate     =  view.findViewById(R.id.rb_bldg_moderate);
        rb_bldg_severe       =  view.findViewById(R.id.rb_bldg_severe);

        rb_cracking_none     =  view.findViewById(R.id.rb_cracking_none);
        rb_cracking_minor    =  view.findViewById(R.id.rb_cracking_minor);
        rb_cracking_moderate =  view.findViewById(R.id.rb_cracking_moderate);
        rb_cracking_severe   =  view.findViewById(R.id.rb_cracking_severe);

        rb_chimney_none      =  view.findViewById(R.id.rb_chimney_none);
        rb_chimney_minor     =  view.findViewById(R.id.rb_chimney_minor);
        rb_chimney_moderate  =  view.findViewById(R.id.rb_chimney_moderate);
        rb_chimney_severe    =  view.findViewById(R.id.rb_chimney_severe);

        rb_ground_none       =  view.findViewById(R.id.rb_ground_none);
        rb_ground_minor      =  view.findViewById(R.id.rb_ground_minor);
        rb_ground_moderate   =  view.findViewById(R.id.rb_ground_moderate);
        rb_ground_severe     =  view.findViewById(R.id.rb_ground_severe);

        rb_GreenPlacard      =  view.findViewById(R.id.rb_GreenPlacard);
        rb_YellowPlacard     =  view.findViewById(R.id.rb_YellowPlacard);
        rb_RedPlacard        =  view.findViewById(R.id.rb_RedPlacard);

        iv_building_image  =  view.findViewById(R.id.iv_building_image);
        imgv_collapse      =  view.findViewById(R.id.imgv_collapse);
        imgv_bldg          =  view.findViewById(R.id.imgv_bldg);
        imgv_cracking      =  view.findViewById(R.id.imgv_cracking);
        imgv_chimney       =  view.findViewById(R.id.imgv_chimney);
        imgv_ground        =  view.findViewById(R.id.imgv_ground);
        imgv_PlaceCard     =  view.findViewById(R.id.imgv_PlaceCard);
        imgv_bldgSignature =  view.findViewById(R.id.imgv_bldgSignature);

        cb_bldgBarricade           =  view.findViewById(R.id.cb_bldgBarricade);
        cb_bldgDetailed            =  view.findViewById(R.id.cb_bldgDetailed);
        cb_bldgOtherRecommendation =  view.findViewById(R.id.cb_bldgOtherRecommendation);

        btn_take_photo = view.findViewById(R.id.btn_take_photo);
        btn_view_photo = view.findViewById(R.id.btn_view_photo);
        btn_save       = view.findViewById(R.id.btn_save);
        btn_preview    = view.findViewById(R.id.btn_preview);

        iCalendar = Calendar.getInstance();

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map);
        markerOptions = new MarkerOptions();

        initCheckBoxListeners();

        initSeDateAndTime();

        initListeners();

        getLocationPermission();

        initRetrieveRESAData();
    }

    private void initCheckBoxListeners()
    {
        cb_bldgBarricade.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (((CheckBox) v).isChecked())
                {
                    edt_bldgBarricades.setEnabled(true);
                }
                else
                {
                    edt_bldgBarricades.setEnabled(false);
                }
            }
        });

        cb_bldgDetailed.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (((CheckBox) v).isChecked())
                {
                    spnr_bldgDetailed.setEnabled(true);
                }
                else
                {
                    spnr_bldgDetailed.setEnabled(false);
                }
            }
        });

        cb_bldgOtherRecommendation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (((CheckBox) v).isChecked())
                {
                    edt_bldgOtherRecommendation.setEnabled(true);
                }
                else
                {
                    edt_bldgOtherRecommendation.setEnabled(false);
                }
            }
        });
    }

    private void initListeners()
    {
        try
        {

            final DatePickerDialog.OnDateSetListener PreviousPostingDate = new DatePickerDialog.OnDateSetListener()
            {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth)
                {
                    iCalendar.set(Calendar.YEAR, year);
                    iCalendar.set(Calendar.MONTH, monthOfYear);
                    iCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    String myFormat = "MM/dd/yyyy";
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                    edt_PreviousPosting_Date.setText(sdf.format(iCalendar.getTime()));
                }
            };

            edt_PreviousPosting_Date.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), PreviousPostingDate,
                            iCalendar.get(Calendar.YEAR),
                            iCalendar.get(Calendar.MONTH),
                            iCalendar.get(Calendar.DATE));

                    if(!datePickerDialog.isShowing())
                    {
                        datePickerDialog.show();
                    }
                }
            });
            edt_PreviousPosting_Date.setFocusable(false);


            btn_take_photo.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ImageName = "Building Image" + "-" + MissionOrderNo + "-" + dtAdded;
                    ImageExtension = "png";

                    initImageListeners(ImageName, ImageExtension, IMAGE_PICK_CAMERA_CODE, IMAGE_PICK_GALLERY_CODE, null);
                }
            });

            btn_view_photo.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try
                    {
                        ImageName = "Building Image" + "-" + MissionOrderNo + "-" + dtAdded;
                        ImageExtension = "png";

                        String ImagePath = SaveFolderName + "/" + ImageName + "." + ImageExtension;

                        File imageFile = new File(ImagePath);

                        if (imageFile.exists())
                        {
                            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bmOptions);

                            if (bitmap != null && btn_take_photo.getText().toString().equalsIgnoreCase("Change Photo"))
                            {
                                initLoadPhoto(imageFile, IMAGE_PICK_CAMERA_CODE, IMAGE_PICK_GALLERY_CODE, null, ImageName);
                            }
                            else
                            {
                                Toast.makeText(getContext(), "Please take photo first.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(getContext(), "Please take photo first.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
            });


            imgv_collapse.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ImageName = "Collapse Image" + "-" + MissionOrderNo + "-" + dtAdded;
                    ImageExtension = "png";

                    initImageListeners(ImageName, ImageExtension, IMAGE_PICK_CAMERA_COLLAPSE, IMAGE_PICK_GALLERY_COLLAPSE, imgv_collapse);
                }
            });

            imgv_bldg.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ImageName = "Story Leaning Image" + "-" + MissionOrderNo + "-" + dtAdded;
                    ImageExtension = "png";

                    initImageListeners(ImageName, ImageExtension, IMAGE_PICK_CAMERA_STORY_LEANING, IMAGE_PICK_GALLERY_STORY_LEANING, imgv_bldg);
                }
            });

            imgv_cracking.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ImageName = "Cracking Image" + "-" + MissionOrderNo + "-" + dtAdded;
                    ImageExtension = "png";

                    initImageListeners(ImageName, ImageExtension, IMAGE_PICK_CAMERA_CRACKING, IMAGE_PICK_GALLERY_CRACKING, imgv_cracking);
                }
            });

            imgv_chimney.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ImageName = "Chimney Image" + "-" + MissionOrderNo + "-" + dtAdded;
                    ImageExtension = "png";

                    initImageListeners(ImageName, ImageExtension, IMAGE_PICK_CAMERA_CHIMNEY, IMAGE_PICK_GALLERY_STORY_CHIMNEY, imgv_chimney);
                }
            });

            imgv_ground.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ImageName = "Ground Image" + "-" + MissionOrderNo + "-" + dtAdded;
                    ImageExtension = "png";

                    initImageListeners(ImageName, ImageExtension, IMAGE_PICK_CAMERA_GROUND, IMAGE_PICK_GALLERY_GROUND, imgv_ground);
                }
            });

            imgv_PlaceCard.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ImageName = "Placard Image" + "-" + MissionOrderNo + "-" + dtAdded;
                    ImageExtension = "png";

                    initImageListeners(ImageName, ImageExtension, IMAGE_PICK_CAMERA_PLACARD, IMAGE_PICK_GALLERY_PLACARD, imgv_PlaceCard);
                }
            });

            imgv_bldgSignature.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try
                    {
                        Cursor cursor = RepositoryInspectorSignature.realAllData(getContext(), "RESA", MissionOrderID);

                        if (cursor.getCount()!=0)
                        {
                            if (cursor.moveToFirst())
                            {
                                String SignaturePath = cursor.getString(cursor.getColumnIndex("SignaturePath"));

                                File file = new File(SignaturePath);

                                if (file.exists())
                                {
                                    final AlertDialog.Builder ADSettings = new AlertDialog.Builder(requireContext());
                                    LayoutInflater inflater = ((Activity) requireContext()).getLayoutInflater();

                                    View view = inflater.inflate(R.layout.custom_dialog_title, null);
                                    TextView textView = view.findViewById(R.id.tv_dialog_title);
                                    String sTitle = "Signature";
                                    textView.setText(sTitle);
                                    textView.setTextColor(Color.BLACK);

                                    ADSettings.setCustomTitle(view);
                                    ADSettings.setMessage("Are you sure you want to remove this ?");
                                    ADSettings.setCancelable(true);
                                    ADSettings.setNegativeButton("CLOSE", null);
                                    ADSettings.setPositiveButton("YES", new DialogInterface.OnClickListener()
                                    {
                                        @SuppressLint("NotifyDataSetChanged")
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            RepositoryInspectorSignature.removeInspectorSignature(getContext(),
                                                    UserAccount.UserAccountID, MissionOrderID, "RESA");

                                            file.delete(); //Delete the image.

                                            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                                            Bitmap bitmap = BitmapFactory.decodeFile(SignaturePath, bmOptions);

                                            Glide.with(requireContext()).load(bitmap).into(imgv_bldgSignature);
                                        }
                                    });
                                    ADSettings.show();
                                }
                                else
                                {
                                    initSetSignature();
                                }
                            }
                        }
                        else
                        {
                            initSetSignature();
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());

                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            /*final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener()
            {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                {
                    iCalendar.set(Calendar.YEAR, year);
                    iCalendar.set(Calendar.MONTH, monthOfYear);
                    iCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    String myFormat = "MMMM dd, yyyy"; //In which you need put here
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                    tv_set_date.setText(sdf.format(iCalendar.getTime()));
                }
            };

            tv_set_date.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    final DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), R.style.date_picker_dialog_theme, date,
                            iCalendar.get(Calendar.YEAR),
                            iCalendar.get(Calendar.MONTH),
                            iCalendar.get(Calendar.DAY_OF_MONTH));

                    //  datePickerDialog.getDatePicker().setMaxDate(iCalendar.getTimeInMillis());

                    if(!datePickerDialog.isShowing())
                    {
                        datePickerDialog.show();
                    }
                }
            });

            tv_set_time.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    initSetTime(tv_set_time);
                }
            });*/

            btn_save.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    String ScreenerID = String.valueOf(UserAccount.employeeID);

                    if (imgv_bldgSignature.getDrawable() == null)
                    {
                        Toast.makeText(getActivity(), "Signature first!", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Cursor cursor = RepositoryRESA.realAllData(getContext(), ScreenerID, MissionOrderID);

                        if (cursor.getCount()!=0)
                        {
                            initSaveRESA("Update");
                        }
                        else
                        {
                            initSaveRESA("Save");
                        }
                    }
                }
            });

            btn_preview.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    initCloseKeyboard();

                    pDialog.setTitle("Generating Preview Report");
                    //pDialog.setMessage("Loading...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(false);
                    pDialog.show();

                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            initPreview();
                        }
                    }, 200);
                }
            });
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initImageListeners(String ImageName, String ImageExtension,
                                    Integer ResultCodeCamera, Integer ResultCodeGallery,
                                    ImageView imageView)
    {
        try
        {
            String ImagePath = SaveFolderName + "/" + ImageName + "." + ImageExtension;

            File imageFile = new File(ImagePath);

            if (imageFile.exists())
            {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bmOptions);

                if (bitmap != null)
                {
                    if (imageView == null && btn_take_photo.getText().toString().equalsIgnoreCase("CHANGE PHOTO"))
                    {
                        initAddPhoto(ResultCodeCamera, ResultCodeGallery);
                    }
                    else
                    {
                        initLoadPhoto(imageFile, ResultCodeCamera, ResultCodeGallery, imageView, ImageName);
                    }
                }
                else
                {
                    initAddPhoto(ResultCodeCamera, ResultCodeGallery);
                }
            }
            else
            {
                initAddPhoto(ResultCodeCamera, ResultCodeGallery);
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }


    private Boolean initCheckListeners()
    {
        if (edt_ObservedConditions_Comments.getText().toString().isEmpty())
        {
            Toast.makeText(getContext(), "Comment is required", Toast.LENGTH_SHORT).show();

            edt_ObservedConditions_Comments.requestFocus();
        }
        else if (edt_bldgSecondComments.getText().toString().isEmpty())
        {
            Toast.makeText(getContext(), "Comment is required", Toast.LENGTH_SHORT).show();

            edt_bldgSecondComments.requestFocus();
        }
        else if (edt_InspectedBy.getText().toString().isEmpty())
        {
            Toast.makeText(getContext(), "Inspected by is required", Toast.LENGTH_SHORT).show();

            edt_InspectedBy.requestFocus();
        }
        else
        {
            return true;
        }
        return false;
    }


    private void initCloseKeyboard()
    {
        try
        {
            View view = requireActivity().getCurrentFocus();

            if (view != null)
            {
                Log.e(TAG, "KEYBOARD CLOSED FROM MAIN");

                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Closing error: " + e.toString());
        }
    }

    private void initPreview()
    {
        try
        {
            createFolder();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());

            pDialog.dismiss();
        }
    }

    private void createFolder()
    {
        File theFile = new File(Environment.getExternalStorageDirectory() + "/" + PdfFolderName);

        if (!theFile.exists())
        {
            theFile.mkdirs();

            initDocument();
        }
        else
        {
            initDocument();
        }
    }

    private void initDocument()
    {
        try
        {
            String FILE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + PdfFolderName + "/" + MissionOrderNo + "_RESA.pdf";

            Document document = new Document(PageSize.LETTER, 15, 15, 25, 25);

            try
            {
                RepositoryOnlineMissionOrders.updateReportPathOfMissionOrder(getContext(),
                        UserAccount.UserAccountID, MissionOrderID, FILE);

                PdfWriter.getInstance(document, new FileOutputStream(FILE));

                document.open();

                initGeneratePDF(document);
            }
            catch (FileNotFoundException | DocumentException e)
            {
                Log.e(TAG, e.toString());

                pDialog.dismiss();
            }
            document.close();

            Intent intent = new Intent(getActivity(), PreviewReportRESAActivity.class);
            intent.putExtra("SeismicityRegion", SeismicityRegion);
            intent.putExtra("FILE", FILE);
            intent.putExtra("MissionOrderID", MissionOrderID);
            startActivity(intent);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());

            pDialog.dismiss();
        }
    }


    private void initGeneratePDF(Document document) throws DocumentException  //Set PDF document Properties
    {
        PdfPTable table, table2;
        Phrase phrase, sPhrase;
        PdfPCell cells, pCell;
        Paragraph paragraph;

        // Font Style for Document
        Font small_font                = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL);
        Font smallest                  = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
        Font smallestBold              = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);

        Font smallNormal               = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
        Font smallNormal2              = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL | Font.UNDERLINE);
        Font smallBold                 = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

        Font header1                   = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);
        Font header2                   = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
        Font header3                   = new Font(Font.FontFamily.HELVETICA, 11, Font.UNDERLINE);
        Font header4                   = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD | Font.ITALIC);

        Font catFont                   = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
        Font catFont2                  = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font catFont3                  = new Font(Font.FontFamily.HELVETICA, 13, Font.NORMAL);
        Font catFont3_2                = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD);
        Font catFont4                  = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Font catFont5                  = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD | Font.ITALIC);
        Font catFont6                  = new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL);
        Font catFont7                  = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Font catFont8                   = new Font(Font.FontFamily.HELVETICA, 16, Font.NORMAL);
        try
        {
            //region HEADER
            table = new PdfPTable(3);
            table.setTotalWidth(580);
            table.setLockedWidth(true);
            table.setWidths(new int[]{90, 400, 90});

            //LOGO Image
            Drawable logo1 = ContextCompat.getDrawable(requireContext(), R.drawable.sample_logo_2);
            BitmapDrawable bitmapDrawable = ((BitmapDrawable) logo1);

            //Image Icon
            Drawable logo2 = ContextCompat.getDrawable(requireContext(), R.drawable.photo_not_exist);
            BitmapDrawable bitmapDrawable2 = ((BitmapDrawable) logo2);

            assert bitmapDrawable != null;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            getResizedBitmap(bitmap, 500).compress(Bitmap.CompressFormat.PNG, 100, stream);

            assert bitmapDrawable2 != null;
            Bitmap bitmap2 = bitmapDrawable2.getBitmap();
            ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
            getResizedBitmap(bitmap2, 500).compress(Bitmap.CompressFormat.PNG, 100, stream2);

            Image republicLogo1 = Image.getInstance(stream.toByteArray());
            republicLogo1.setAlignment(Image.ALIGN_RIGHT);
            republicLogo1.scaleAbsolute(65f, 20f);

            Image republicLogo2 = Image.getInstance(stream.toByteArray());
            republicLogo2.setAlignment(Image.ALIGN_LEFT);
            republicLogo2.scaleAbsolute(65f, 5f);

            Image republicLogo3 = Image.getInstance(stream.toByteArray());
            republicLogo3.setAlignment(Image.ALIGN_CENTER);
            republicLogo3.scaleAbsolute(65f, 5f);

            Image republicLogo4 = Image.getInstance(stream2.toByteArray());
            republicLogo4.setAlignment(Image.ALIGN_CENTER);
            republicLogo4.scaleToFit(300, 140);
            //republicLogo4.scaleAbsolute(180f, 0f);


            cells = new PdfPCell();
            cells.addElement(republicLogo1);
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setColspan(1);
            table.addCell(cells);

            Phrase headerPhrase = new Phrase(new Chunk("Republic of the Philippines", catFont));
            headerPhrase.add(new Phrase("\n" + "(AGENCY)", catFont));
            headerPhrase.add(new Phrase("\n" + "(Location)", catFont));
            headerPhrase.add(new Phrase("\n\n" + "Rapid Evaluation and Safety Assessment (RESA)", catFont2));

            cells = new PdfPCell(headerPhrase);
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setColspan(1);
            table.addCell(cells);

            cells = new PdfPCell();
            cells.addElement(republicLogo2);
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setColspan(1);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(" "));
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setColspan(3);
            table.addCell(cells);

            document.add(table);
            //endregion

            //region INSPECTION, BUILDING INFORMATION
            table = new PdfPTable(2);
            table.setWidthPercentage(100);


            cells = new PdfPCell(new Phrase("Inspection", catFont2));
            cells.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setColspan(2);
            table.addCell(cells);


            //INSPECTOR
            table2 = new PdfPTable(50);
            table2.setWidthPercentage(100);

            phrase = new Phrase("Inspected By: ", header1);
            phrase.setFont(smallNormal);
            pCell = new PdfPCell(phrase);
            pCell.setColspan(15);
            pCell.setBorder(Rectangle.NO_BORDER);
            pCell.setPaddingTop(5);
            table2.addCell(pCell);

            phrase = new Phrase();
            Cursor cursor2 = RepositoryOnlineAssignedInspectors.realAllData2(getContext(), UserAccount.UserAccountID, MissionOrderID);

            if (cursor2.getCount()!=0)
            {
                if (cursor2.moveToFirst())
                {
                    do
                    {
                        String Inspector = cursor2.getString(cursor2.getColumnIndex("Inspector"));

                        phrase.add(Inspector + "\n");
                    }
                    while (cursor2.moveToNext());
                }
                cursor2.close();
            }

            pCell = new PdfPCell(phrase);
            pCell.setColspan(85);
            pCell.setBorder(Rectangle.NO_BORDER);
            pCell.setPaddingTop(5);
            table2.addCell(pCell);
            cells.addElement(table2);

            cells.setColspan(1);
            cells.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
            cells.setPaddingLeft(20);
            cells.setPaddingBottom(10);
            table.addCell(cells);

          /*  sPhrase = new Phrase();
            sPhrase.add(new Phrase("Inspected By: " , header1));
            sPhrase.add(new Phrase(UserAccount.CompleteName , header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(1);
            table.addCell(cells);*/

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Inspector ID: " , header1));
            sPhrase.add(new Phrase(String.valueOf(UserAccount.employeeID) , header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setColspan(1);
            table.addCell(cells);


            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Affiliation: " , header1));
            sPhrase.add(new Phrase(edt_affiliation.getText().toString() , header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setColspan(2);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Date: " , header1));
            sPhrase.add(new Phrase(edt_set_date.getText().toString() , header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setColspan(1);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Time: " , header1));
            sPhrase.add(new Phrase(edt_set_time.getText().toString() , header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.BOTTOM | Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setColspan(1);
            table.addCell(cells);


            cells = new PdfPCell(new Phrase("Building Information", catFont2));
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setColspan(2);
            table.addCell(cells);



            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Name: " , header1));
            sPhrase.add(new Phrase(edt_bldg_name.getText().toString() , header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT );
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setColspan(1);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Building Contact: " , header1));
            sPhrase.add(new Phrase(edt_bldg_contact.getText().toString() , header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setColspan(1);
            table.addCell(cells);

            table2 = new PdfPTable(100);
            table2.setWidthPercentage(100);

            PdfPCell sCells = new PdfPCell(new Phrase("Address: ", header1));
            sCells.setBorder(Rectangle.NO_BORDER);
            sCells.setPaddingTop(5);
            sCells.setColspan(20);
            table2.addCell(sCells);

            sCells = new PdfPCell(new Phrase(edt_bldg_address.getText().toString(), header1));
            sCells.setBorder(Rectangle.NO_BORDER);
            sCells.setPaddingLeft(5);
            sCells.setPaddingTop(5);
            sCells.setColspan(80);
            table2.addCell(sCells);

            cells = new PdfPCell(new Phrase());
            cells.addElement(table2);
            cells.setBorder(Rectangle.LEFT );
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setColspan(1);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(" ", header1));
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setColspan(1);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Storeys", catFont2));
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setColspan(2);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Above ground: " , header1));
            sPhrase.add(new Phrase(edt_NoOfStoreyAboveGround.getText().toString() , header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setColspan(1);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Type of Construction: " , header1));

            if (!spnr_TypeOfConstruction.getSelectedItem().toString().equalsIgnoreCase("Select Type of Construction"))
            {
                sPhrase.add(new Phrase(spnr_TypeOfConstruction.getSelectedItem().toString() , header1));
            }

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setColspan(2);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Below ground: " , header1));
            sPhrase.add(new Phrase(edt_NoOfStoreyBelowGround.getText().toString() , header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setColspan(1);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Primary Occupancy: " , header1));

            if (!spnr_PrimaryOccupancy.getSelectedItem().toString().equalsIgnoreCase("Select Occupancy"))
            {
                sPhrase.add(new Phrase(spnr_PrimaryOccupancy.getSelectedItem().toString() , header1));
            }

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setColspan(2);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Approx. footprint area (sqm): " , header1));
            sPhrase.add(new Phrase(edt_Approx.getText().toString() , header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setColspan(2);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("No. residential units: " , header1));
            sPhrase.add(new Phrase(edt_NoOfResidentialUnits.getText().toString() , header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setColspan(2);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("No. commercial units: " , header1));
            sPhrase.add(new Phrase(edt_NoOfCommercialUnits.getText().toString() , header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingBottom(5);
            cells.setColspan(2);
            table.addCell(cells);

            document.add(table);

            //endregion

            //region SCORING
            table = new PdfPTable(100);
            table.setWidthPercentage(100);

            cells = new PdfPCell(new Phrase("Observed Conditions", catFont2));
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(10);
            cells.setColspan(40);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("None", catFont2));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(10);
            cells.setColspan(15);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Minor", catFont2));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(10);
            cells.setColspan(15);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Moderate", catFont2));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(10);
            cells.setColspan(15);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Severe", catFont2));
            cells.setBorder(Rectangle.RIGHT);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(10);
            cells.setColspan(15);
            table.addCell(cells);


            cells = new PdfPCell(new Phrase("Collapse, partial collapse or\nbuilding off foundation", header1));
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(40);
            table.addCell(cells);

            if(rb_collapse_none.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(15);
            table.addCell(cells);

            if(rb_collapse_minor.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(15);
            table.addCell(cells);

            if(rb_collapse_moderate.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(15);
            table.addCell(cells);


            if(rb_collapse_severe.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.RIGHT);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(15);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Building or story leaning", header1));
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(40);
            table.addCell(cells);

            if(rb_bldg_none.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(15);
            table.addCell(cells);

            if(rb_bldg_minor.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(15);
            table.addCell(cells);

            if(rb_bldg_moderate.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(15);
            table.addCell(cells);

            if(rb_bldg_severe.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.RIGHT);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(15);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Cracking damage to the walls,\nother structural damage", header1));
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(40);
            table.addCell(cells);

            if(rb_cracking_none.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(15);
            table.addCell(cells);

            if(rb_cracking_minor.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(15);
            table.addCell(cells);

            if(rb_cracking_moderate.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(15);
            table.addCell(cells);

            if(rb_cracking_severe.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.RIGHT);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(15);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Chimney, parapet, or other\nfalling hazard", header1));
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(40);
            table.addCell(cells);

            if(rb_chimney_none.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(15);
            table.addCell(cells);

            if(rb_chimney_minor.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(15);
            table.addCell(cells);

            if(rb_chimney_moderate.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(15);
            table.addCell(cells);

            if(rb_chimney_severe.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.RIGHT);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(15);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Ground slope movement or\nCracking", header1));
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setColspan(40);
            table.addCell(cells);

            if(rb_ground_none.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setColspan(15);
            table.addCell(cells);

            if(rb_ground_minor.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder( Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setColspan(15);
            table.addCell(cells);

            if(rb_ground_moderate.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setColspan(15);
            table.addCell(cells);

            if(rb_ground_severe.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.RIGHT);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setColspan(15);
            table.addCell(cells);
            //endregion

            //region COMMENTS
            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Other: " , catFont2));
            sPhrase.add(new Phrase(edt_ObservedConditions_Others.getText().toString() , header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(10);
            cells.setColspan(100);
            table.addCell(cells);


            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Comments: " , catFont2));
            sPhrase.add(new Phrase(edt_ObservedConditions_Comments.getText().toString() , header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setColspan(100);
            table.addCell(cells);


            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Estimated Estimated Building Damage (excluding content): " , catFont2));

            if (!spnr_EstimatedBldgDamage.getSelectedItem().toString().equalsIgnoreCase("Select Estimate Damage"))
            {
                sPhrase.add(new Phrase(spnr_EstimatedBldgDamage.getSelectedItem().toString(), header1));
            }
            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setColspan(100);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Previous posting:", catFont2));
            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(50);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Date: " , catFont2));
            sPhrase.add(new Phrase(edt_PreviousPosting_Date.getText().toString() , header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(50);
            table.addCell(cells);

            if(spnr_PreviousPosting.getSelectedItem().toString().equalsIgnoreCase("Inspected"))
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(7);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("INSPECTED (Green placard)", header1));
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(93);
            table.addCell(cells);

            if(spnr_PreviousPosting.getSelectedItem().toString().equalsIgnoreCase("Restricted Use"))
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(7);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("RESTRICTED (Yellow placard)", header1));
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(93);
            table.addCell(cells);


            if(spnr_PreviousPosting.getSelectedItem().toString().equalsIgnoreCase("Off Limits"))
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.LEFT  | Rectangle.BOTTOM);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setColspan(7);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("OFF LIMITS (Red placard)", header1));
            cells.setBorder(Rectangle.RIGHT | Rectangle.BOTTOM);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setColspan(93);
            table.addCell(cells);

             document.add(table);


             //NEW PAGE
             document.newPage();

             table = new PdfPTable(100);
             table.setWidthPercentage(100);

            //CURRENT POSTING
            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Posting: " , catFont2));
            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(100);
            table.addCell(cells);

            if(rb_GreenPlacard.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(7);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("INSPECTED (Green placard)", header1));
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(93);
            table.addCell(cells);

            if(rb_YellowPlacard.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(7);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("RESTRICTED (Yellow placard)", header1));
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(93);
            table.addCell(cells);


            if(rb_RedPlacard.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
            cells.setPaddingLeft(5);
            cells.setPaddingBottom(10);
            cells.setColspan(7);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("OFF LIMITS (Red placard)", header1));
            cells.setBorder(Rectangle.RIGHT | Rectangle.BOTTOM);
            cells.setPaddingLeft(5);
            cells.setPaddingBottom(10);
            cells.setColspan(93);
            table.addCell(cells);


            cells = new PdfPCell(new Phrase("Further Actions", catFont2));
            cells.setBorder(Rectangle.TOP | Rectangle.LEFT | Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(10);
            cells.setColspan(100);
            table.addCell(cells);

            if(cb_bldgBarricade.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(7);
            table.addCell(cells);

            if(cb_bldgBarricade.isChecked())
            {
                cells = new PdfPCell(new Phrase("Barricades needed in the areas: " + edt_bldgBarricades.getText().toString(), header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("Barricades needed in the areas: ", header1));
            }
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(93);
            table.addCell(cells);

            if(cb_bldgDetailed.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(7);
            table.addCell(cells);


            if (cb_bldgDetailed.isChecked())
            {
                cells = new PdfPCell(new Phrase("Detailed Evaluation required: " + spnr_bldgDetailed.getSelectedItem().toString(), header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("Detailed Evaluation required: ", header1));
            }
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(93);
            table.addCell(cells);

            if(cb_bldgOtherRecommendation.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setColspan(7);
            table.addCell(cells);

            if(cb_bldgOtherRecommendation.isChecked())
            {
                cells = new PdfPCell(new Phrase("Other recommendations: " + edt_bldgOtherRecommendation.getText().toString(), header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("Other recommendations: ", header1));
            }
            cells.setBorder(Rectangle.RIGHT | Rectangle.BOTTOM);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(10);
            cells.setColspan(93);
            table.addCell(cells);


            headerPhrase = new Phrase();
            headerPhrase.add(new Phrase("Comments: ", catFont2));
            headerPhrase.add(new Phrase(edt_bldgSecondComments.getText().toString(), catFont));

            cells = new PdfPCell(headerPhrase);
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(10);
            cells.setColspan(100);
            table.addCell(cells);

            //endregion

            //region ATTACHMENTS IMAGES
            //document.newPage();
            //table = new PdfPTable(100);
           // table.setWidthPercentage(100);


            if (bmCollapse != null || bmBuildingStoryLeaning != null || bmCracking != null ||
                    bmChimney != null || bmGroundSlope != null || bmPosting != null)
            {
                cells = new PdfPCell(new Phrase("Observed Conditions", catFont7));
                cells.setBorder(Rectangle.NO_BORDER);
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setPaddingLeft(5);
                cells.setPaddingTop(5);
                cells.setColspan(100);
                table.addCell(cells);
            }

            int countPicture = 0;

            //1
            if (bmCollapse != null)
            {
                countPicture = countPicture + 1;

                cells = new PdfPCell();

                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                getResizedBitmap(bmCollapse, 1000).compress(Bitmap.CompressFormat.PNG, 100, stream1);

                Image AttachmentOutput = Image.getInstance(stream1.toByteArray());
                AttachmentOutput.setAlignment(Image.ALIGN_CENTER);
                AttachmentOutput.scaleToFit(300, 140);

                cells.addElement(AttachmentOutput);

                table2 = new PdfPTable(100);
                table2.setWidthPercentage(100);

                sCells = new PdfPCell(new Phrase("\nCollapse, partial collapse or\n building off foundation", catFont3));
                sCells.setBorder(Rectangle.NO_BORDER);
                sCells.setPaddingTop(5);
                sCells.setColspan(100);
                sCells.setHorizontalAlignment(Element.ALIGN_CENTER);
                table2.addCell(sCells);

                cells.addElement(table2);

                cells.setBorder(Rectangle.NO_BORDER);
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setPaddingLeft(5);
                cells.setPaddingTop(15);
                cells.setColspan(50);
                table.addCell(cells);
            }

            if (bmBuildingStoryLeaning != null)
            {
                countPicture = countPicture + 1;

                cells = new PdfPCell();

                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                getResizedBitmap(bmBuildingStoryLeaning, 1000).compress(Bitmap.CompressFormat.PNG, 100, stream1);

                Image AttachmentOutput = Image.getInstance(stream1.toByteArray());
                AttachmentOutput.setAlignment(Image.ALIGN_CENTER);
                AttachmentOutput.scaleToFit(300, 140);

                cells.addElement(AttachmentOutput);

                table2 = new PdfPTable(100);
                table2.setWidthPercentage(100);

                sCells = new PdfPCell(new Phrase("\nBuilding or story leaning", catFont3));
                sCells.setBorder(Rectangle.NO_BORDER);
                sCells.setPaddingTop(5);
                sCells.setColspan(100);
                sCells.setHorizontalAlignment(Element.ALIGN_CENTER);
                table2.addCell(sCells);

                cells.addElement(table2);

                cells.setBorder(Rectangle.NO_BORDER);
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setPaddingLeft(5);
                cells.setPaddingTop(15);
                cells.setColspan(50);
                table.addCell(cells);
            }

            //2
            if (bmCracking != null)
            {
                countPicture = countPicture + 1;

                cells = new PdfPCell();
                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                getResizedBitmap(bmCracking, 1000).compress(Bitmap.CompressFormat.PNG, 100, stream1);

                Image AttachmentOutput = Image.getInstance(stream1.toByteArray());
                AttachmentOutput.setAlignment(Image.ALIGN_CENTER);
                AttachmentOutput.scaleToFit(300, 140);

                cells.addElement(AttachmentOutput);

                table2 = new PdfPTable(100);
                table2.setWidthPercentage(100);

                sCells = new PdfPCell(new Phrase("\nCracking damage to the walls,\nother structural damage", catFont3));
                sCells.setBorder(Rectangle.NO_BORDER);
                sCells.setPaddingTop(5);
                sCells.setColspan(100);
                sCells.setHorizontalAlignment(Element.ALIGN_CENTER);
                table2.addCell(sCells);

                cells.addElement(table2);

                cells.setBorder(Rectangle.NO_BORDER);
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setPaddingLeft(5);
                cells.setPaddingTop(5);
                cells.setColspan(50);
                table.addCell(cells);
            }

            if (bmChimney != null)
            {
                countPicture = countPicture + 1;

                cells = new PdfPCell();
                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                getResizedBitmap(bmChimney, 1000).compress(Bitmap.CompressFormat.PNG, 100, stream1);

                Image AttachmentOutput = Image.getInstance(stream1.toByteArray());
                AttachmentOutput.setAlignment(Image.ALIGN_CENTER);
                AttachmentOutput.scaleToFit(300, 140);

                cells.addElement(AttachmentOutput);

                table2 = new PdfPTable(100);
                table2.setWidthPercentage(100);

                sCells = new PdfPCell(new Phrase("\nChimney, parapet, or other\nfalling hazard", catFont3));
                sCells.setBorder(Rectangle.NO_BORDER);
                sCells.setPaddingTop(5);
                sCells.setColspan(100);
                sCells.setHorizontalAlignment(Element.ALIGN_CENTER);
                table2.addCell(sCells);

                cells.addElement(table2);

                cells.setBorder(Rectangle.NO_BORDER);
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setPaddingLeft(5);
                cells.setPaddingTop(5);
                cells.setColspan(50);
                table.addCell(cells);
            }

            //3
            if (bmGroundSlope != null)
            {
                countPicture = countPicture + 1;

                cells = new PdfPCell();
                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                getResizedBitmap(bmGroundSlope, 1000).compress(Bitmap.CompressFormat.PNG, 100, stream1);

                Image AttachmentOutput = Image.getInstance(stream1.toByteArray());
                AttachmentOutput.setAlignment(Image.ALIGN_CENTER);
                AttachmentOutput.scaleToFit(300, 140);

                cells.addElement(AttachmentOutput);

                table2 = new PdfPTable(100);
                table2.setWidthPercentage(100);

                sCells = new PdfPCell(new Phrase("\nGround slope movement or\ncracking", catFont3));
                sCells.setBorder(Rectangle.NO_BORDER);
                sCells.setPaddingTop(5);
                sCells.setColspan(100);
                sCells.setHorizontalAlignment(Element.ALIGN_CENTER);
                table2.addCell(sCells);

                cells.addElement(table2);

                cells.setBorder(Rectangle.NO_BORDER);
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setPaddingLeft(5);
                cells.setPaddingTop(5);
                cells.setColspan(50);
                table.addCell(cells);
            }

            if (countPicture == 1 || countPicture == 3  || countPicture == 5)
            {
                cells = new PdfPCell(new Phrase(" "));
                cells.setBorder(Rectangle.NO_BORDER);
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setPaddingLeft(5);
                cells.setPaddingTop(5);
                cells.setColspan(50);
                table.addCell(cells);
            }


            if (bmPosting != null)
            {
                cells = new PdfPCell();

                table2 = new PdfPTable(100);
                table2.setWidthPercentage(100);

                sCells = new PdfPCell(new Phrase("Posting", catFont7));
                sCells.setBorder(Rectangle.NO_BORDER);
                sCells.setPaddingTop(5);
                sCells.setColspan(100);
                sCells.setHorizontalAlignment(Element.ALIGN_CENTER);
                table2.addCell(sCells);

                sCells = new PdfPCell(new Phrase("INSPECTED(Green placard), RESTRICTED(Yellow placard), OFF LIMITS(Red placard)", catFont3_2));
                sCells.setBorder(Rectangle.NO_BORDER);
                sCells.setPaddingTop(10);
                sCells.setPaddingBottom(10);
                sCells.setColspan(100);
                sCells.setHorizontalAlignment(Element.ALIGN_CENTER);
                table2.addCell(sCells);

                cells.addElement(table2);

                //
                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                getResizedBitmap(bmPosting, 1000).compress(Bitmap.CompressFormat.PNG, 100, stream1);

                Image AttachmentOutput = Image.getInstance(stream1.toByteArray());
                AttachmentOutput.setAlignment(Image.ALIGN_CENTER);
                AttachmentOutput.scaleToFit(300, 140);

                cells.addElement(AttachmentOutput);

                cells.setBorder(Rectangle.NO_BORDER);
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setPaddingLeft(5);
                cells.setPaddingTop(5);
                cells.setPaddingBottom(40);
                cells.setColspan(100);
                table.addCell(cells);
            }

            //region SIGNATURE ATTACHMENT
            cells = new PdfPCell(new Phrase(" ", header2));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(10);
            cells.setColspan(70);
            table.addCell(cells);

            cells = new PdfPCell();

            table2 = new PdfPTable(100);
            table2.setWidthPercentage(100);

            sCells = new PdfPCell();

            if (bmInspectorSignature == null)
            {
                sCells.addElement(new Phrase(" \n "));
            }
            else
            {
                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                getResizedBitmap(bmInspectorSignature, 700).compress(Bitmap.CompressFormat.PNG, 100, stream1);

                Image AttachmentOutput = Image.getInstance(stream1.toByteArray());
                AttachmentOutput.setAlignment(Image.ALIGN_CENTER);
                AttachmentOutput.scaleToFit(150, 140);

                sCells.addElement(AttachmentOutput);
            }

            sCells.addElement(new Phrase(edt_InspectedBy.getText().toString().toUpperCase(), header1));
            sCells.setBorder(Rectangle.BOTTOM);
            sCells.setColspan(100);
            sCells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table2.addCell(sCells);

            /*sCells = new PdfPCell(new Phrase(edt_InspectedBy.getText().toString().toUpperCase(), header1));
            sCells.setBorder(Rectangle.BOTTOM);
            sCells.setColspan(100);
            sCells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table2.addCell(sCells);*/

            sCells = new PdfPCell(new Phrase("Building Inspector", header1));
            sCells.setBorder(Rectangle.TOP);
            sCells.setColspan(100);
            sCells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table2.addCell(sCells);

            cells.addElement(table2);
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setColspan(25);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(" ", header2));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setColspan(5);
            table.addCell(cells);

            //endregion

            document.add(table);
            //endregion
        }
        catch (Exception e)
        {
            Log.e(TAG, "Header: " + e.toString());
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize)
    {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    private void initRetrieveRESAData()
    {
        try
        {
            String ScreenerID    = String.valueOf(UserAccount.employeeID);

            Cursor cursor = RepositoryRESA.realAllData(getContext(), ScreenerID, MissionOrderID);

            Log.e(TAG, "MissionOrderID: " + MissionOrderID);

            if (cursor.getCount()!=0)
            {
                if (cursor.moveToFirst())
                {
                    String Affiliation                    = cursor.getString(cursor.getColumnIndex("Affiliation"));
                    String SetDate                        = cursor.getString(cursor.getColumnIndex("SetDate"));
                    String SetTime                        = cursor.getString(cursor.getColumnIndex("SetTime"));
                    String BuildingName                   = cursor.getString(cursor.getColumnIndex("BuildingName"));
                    String BuildingAddress                = cursor.getString(cursor.getColumnIndex("BuildingAddress"));
                    String BuildingContact                = cursor.getString(cursor.getColumnIndex("BuildingContact"));
                    String NoOfStoreyAboveGround          = cursor.getString(cursor.getColumnIndex("NoOfStoreyAboveGround"));
                    String NoOfStoreyBelowGround          = cursor.getString(cursor.getColumnIndex("NoOfStoreyBelowGround"));
                    String TypeOfConstruction             = cursor.getString(cursor.getColumnIndex("TypeOfConstruction"));
                    String PrimaryOccupancy               = cursor.getString(cursor.getColumnIndex("PrimaryOccupancy"));
                    String ApproxFootPrintAreaSM          = cursor.getString(cursor.getColumnIndex("ApproxFootPrintAreaSM"));
                    String NoOfResidentialUnits           = cursor.getString(cursor.getColumnIndex("NoOfResidentialUnits"));
                    String NoOfCommercialUnits            = cursor.getString(cursor.getColumnIndex("NoOfCommercialUnits"));
                    String BuildingOffFoundationType      = cursor.getString(cursor.getColumnIndex("BuildingOffFoundationType"));
                    String StoryLeaningType               = cursor.getString(cursor.getColumnIndex("StoryLeaningType"));
                    String OtherStructuralDamageType      = cursor.getString(cursor.getColumnIndex("OtherStructuralDamageType"));
                    String OtherFallingHazardType         = cursor.getString(cursor.getColumnIndex("OtherFallingHazardType"));
                    String CrackingType                   = cursor.getString(cursor.getColumnIndex("CrackingType"));
                    String OtherOptionalType              = cursor.getString(cursor.getColumnIndex("OtherOptionalType"));
                    String Comments                       = cursor.getString(cursor.getColumnIndex("Comments"));
                    String EstimatedBuildingDamage        = cursor.getString(cursor.getColumnIndex("EstimatedBuildingDamage"));
                    String PreviousPostingEstimatedDamage = cursor.getString(cursor.getColumnIndex("PreviousPostingEstimatedDamage"));
                    String PreviousPostingDate            = cursor.getString(cursor.getColumnIndex("PreviousPostingDate"));
                    String ColorPlacard                   = cursor.getString(cursor.getColumnIndex("ColorPlacard"));

                    String FurtherActionsBarricades             = cursor.getString(cursor.getColumnIndex("FurtherActionsBarricades"));
                    String FurtherActionsDetailedEvaluationRecommended = cursor.getString(cursor.getColumnIndex("FurtherActionsDetailedEvaluationRecommended"));
                    String FurtherActionsOtherRecommended       = cursor.getString(cursor.getColumnIndex("FurtherActionsOtherRecommended"));

                    String FurtherActionComments          = cursor.getString(cursor.getColumnIndex("FurtherActionComments"));
                    String FurtherActionEstimatedDamage   = cursor.getString(cursor.getColumnIndex("FurtherActionEstimatedDamage"));
                    String FurtherActionRecommendations   = cursor.getString(cursor.getColumnIndex("FurtherActionRecommendations"));
                    String SecondComments                 = cursor.getString(cursor.getColumnIndex("SecondComments"));
                    String InspectedBy                    = cursor.getString(cursor.getColumnIndex("InspectedBy"));

                    edt_affiliation.setText(Affiliation);
                    edt_bldg_name.setText(BuildingName);
                    edt_bldg_address.setText(BuildingAddress);
                    edt_bldg_contact.setText(BuildingContact);
                    edt_NoOfStoreyAboveGround.setText(NoOfStoreyAboveGround);
                    edt_NoOfStoreyBelowGround.setText(NoOfStoreyBelowGround);
                    edt_Approx.setText(ApproxFootPrintAreaSM);
                    edt_NoOfResidentialUnits.setText(NoOfResidentialUnits);
                    edt_NoOfCommercialUnits.setText(NoOfCommercialUnits);

                    edt_ObservedConditions_Others.setText(OtherOptionalType);
                    edt_ObservedConditions_Comments.setText(Comments);
                    edt_PreviousPosting_Date.setText(PreviousPostingDate);
                    edt_bldgBarricades.setText(FurtherActionComments);

                    edt_bldgOtherRecommendation.setText(FurtherActionRecommendations);
                    edt_bldgSecondComments.setText(SecondComments);
                    edt_InspectedBy.setText(InspectedBy);

                    edt_set_date.setText(SetDate);
                    edt_set_time.setText(SetTime);

                    initSetSpinnerValue(TypeOfConstruction, spnr_TypeOfConstruction);
                    initSetSpinnerValue(PrimaryOccupancy, spnr_PrimaryOccupancy);

                    switch (BuildingOffFoundationType)
                    {
                        case "None":
                            rb_collapse_none.setChecked(true);
                            break;
                        case "Minor":
                            rb_collapse_minor.setChecked(true);
                            break;
                        case "Moderate":
                            rb_collapse_moderate.setChecked(true);
                            break;
                        case "Severe":
                            rb_collapse_severe.setChecked(true);
                            break;
                    }

                    switch (StoryLeaningType)
                    {
                        case "None":
                            rb_bldg_none.setChecked(true);
                            break;
                        case "Minor":
                            rb_bldg_minor.setChecked(true);
                            break;
                        case "Moderate":
                            rb_bldg_moderate.setChecked(true);
                            break;
                        case "Severe":
                            rb_bldg_severe.setChecked(true);
                            break;
                    }

                    switch (OtherStructuralDamageType)
                    {
                        case "None":
                            rb_cracking_none.setChecked(true);
                            break;
                        case "Minor":
                            rb_cracking_minor.setChecked(true);
                            break;
                        case "Moderate":
                            rb_cracking_moderate.setChecked(true);
                            break;
                        case "Severe":
                            rb_cracking_severe.setChecked(true);
                            break;
                    }

                    switch (OtherFallingHazardType)
                    {
                        case "None":
                            rb_chimney_none.setChecked(true);
                            break;
                        case "Minor":
                            rb_chimney_minor.setChecked(true);
                            break;
                        case "Moderate":
                            rb_chimney_moderate.setChecked(true);
                            break;
                        case "Severe":
                            rb_chimney_severe.setChecked(true);
                            break;
                    }

                    switch (CrackingType)
                    {
                        case "None":
                            rb_ground_none.setChecked(true);
                            break;
                        case "Minor":
                            rb_ground_minor.setChecked(true);
                            break;
                        case "Moderate":
                            rb_ground_moderate.setChecked(true);
                            break;
                        case "Severe":
                            rb_ground_severe.setChecked(true);
                            break;
                    }

                    initSetSpinnerValue(EstimatedBuildingDamage, spnr_EstimatedBldgDamage);
                    initSetSpinnerValue(PreviousPostingEstimatedDamage, spnr_PreviousPosting);

                    switch (ColorPlacard)
                    {
                        case "Green":
                            rb_GreenPlacard.setChecked(true);
                            break;
                        case "Yellow":
                            rb_YellowPlacard.setChecked(true);
                            break;
                        case "Red":
                            rb_RedPlacard.setChecked(true);
                            break;
                    }

                    cb_bldgBarricade.setChecked(FurtherActionsBarricades.equalsIgnoreCase("1"));
                    if (FurtherActionsBarricades.equalsIgnoreCase("1"))
                    {
                        edt_bldgBarricades.setEnabled(true);
                    }

                    cb_bldgDetailed.setChecked(FurtherActionsDetailedEvaluationRecommended.equalsIgnoreCase("1"));
                    if (FurtherActionsDetailedEvaluationRecommended.equalsIgnoreCase("1"))
                    {
                        spnr_bldgDetailed.setEnabled(true);
                    }

                    cb_bldgOtherRecommendation.setChecked(FurtherActionsOtherRecommended.equalsIgnoreCase("1"));
                    if (FurtherActionsOtherRecommended.equalsIgnoreCase("1"))
                    {
                        edt_bldgOtherRecommendation.setEnabled(true);
                    }

                    initSetSpinnerValue(FurtherActionEstimatedDamage, spnr_bldgDetailed);
                }
            }
            else
            {
                initSetBuildingInformation();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initSetSpinnerValue(String Value, Spinner spinner)
    {
        try
        {
            if (Value != null && !Value.equalsIgnoreCase(""))
            {
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();

                int position = adapter.getPosition(Value);

                spinner.setSelection(position);
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initSetBuildingInformation()
    {
        try
        {
            Cursor cursor = RepositoryOnlineMissionOrders.realAllData5(getContext(),
                    String.valueOf(UserAccount.UserAccountID), MissionOrderID);

            if (cursor.getCount()!=0)
            {
                if (cursor.moveToFirst())
                {
                    String BuildingName        = cursor.getString(cursor.getColumnIndex("BuildingName"));
                    String BuildingFullAddress = cursor.getString(cursor.getColumnIndex("Location"));
                    String ContactNo           = cursor.getString(cursor.getColumnIndex("ContactNo"));
                    String StructuralTypeDesc  = cursor.getString(cursor.getColumnIndex("StructureType"));
                    String Occupancy           = cursor.getString(cursor.getColumnIndex("Occupancy"));

                    initSetSpinnerValue(StructuralTypeDesc, spnr_TypeOfConstruction);
                    initSetSpinnerValue(Occupancy, spnr_PrimaryOccupancy);

                    edt_bldg_name.setText(BuildingName);
                    edt_bldg_address.setText(BuildingFullAddress);
                    edt_bldg_contact.setText(ContactNo);
                }
            }

        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initSaveRESA(String option)
    {
        try
        {
            String ScreenerID    = String.valueOf(UserAccount.employeeID);

            String Affiliation               = edt_affiliation.getText().toString() != null ? edt_affiliation.getText().toString():"";
            String SetDate                   = edt_set_date.getText().toString() != null ? edt_set_date.getText().toString():"";
            String SetTime                   = edt_set_time.getText().toString() != null ? edt_set_time.getText().toString():"";

            String BuildingName              = edt_bldg_name.getText().toString() != null ? edt_bldg_name.getText().toString():"";
            String BuildingAddress           = edt_bldg_address.getText().toString() != null ? edt_bldg_address.getText().toString():"";
            String BuildingContact           = edt_bldg_contact.getText().toString() != null ? edt_bldg_contact.getText().toString():"";
            String NoOfStoreyAboveGround     = edt_NoOfStoreyAboveGround.getText().toString() != null ? edt_NoOfStoreyAboveGround.getText().toString():"";
            String NoOfStoreyBelowGround     = edt_NoOfStoreyBelowGround.getText().toString() != null ? edt_NoOfStoreyBelowGround.getText().toString():"";

            String TypeOfConstruction        = spnr_TypeOfConstruction.getSelectedItem().toString() != null ? spnr_TypeOfConstruction.getSelectedItem().toString():"";
            String PrimaryOccupancy          = spnr_PrimaryOccupancy.getSelectedItem().toString() != null ? spnr_PrimaryOccupancy.getSelectedItem().toString():"";
            String ApproxFootPrintAreaSM     = edt_Approx.getText().toString() != null ? edt_Approx.getText().toString():"";
            String NoOfResidentialUnits      = edt_NoOfResidentialUnits.getText().toString() != null ? edt_NoOfResidentialUnits.getText().toString():"";
            String NoOfCommercialUnits       = edt_NoOfCommercialUnits.getText().toString() != null ? edt_NoOfCommercialUnits.getText().toString():"";

            String BuildingOffFoundationType = "";
            if (rb_collapse_none.isChecked())
            {
                BuildingOffFoundationType = "None";
            }
            else if (rb_collapse_minor.isChecked())
            {
                BuildingOffFoundationType = "Minor";
            }
            else if (rb_collapse_moderate.isChecked())
            {
                BuildingOffFoundationType = "Moderate";
            }
            else if (rb_collapse_severe.isChecked())
            {
                BuildingOffFoundationType = "Severe";
            }

            String StoryLeaningType = "";
            if (rb_bldg_none.isChecked())
            {
                StoryLeaningType = "None";
            }
            else if (rb_bldg_minor.isChecked())
            {
                StoryLeaningType = "Minor";
            }
            else if (rb_bldg_moderate.isChecked())
            {
                StoryLeaningType = "Moderate";
            }
            else if (rb_bldg_severe.isChecked())
            {
                StoryLeaningType = "Severe";
            }

            String OtherStructuralDamageType = "";
            if (rb_cracking_none.isChecked())
            {
                OtherStructuralDamageType = "None";
            }
            else if (rb_cracking_minor.isChecked())
            {
                OtherStructuralDamageType = "Minor";
            }
            else if (rb_cracking_moderate.isChecked())
            {
                OtherStructuralDamageType = "Moderate";
            }
            else if (rb_cracking_severe.isChecked())
            {
                OtherStructuralDamageType = "Severe";
            }

            String OtherFallingHazardType = "";
            if (rb_chimney_none.isChecked())
            {
                OtherFallingHazardType = "None";
            }
            else if (rb_chimney_minor.isChecked())
            {
                OtherFallingHazardType = "Minor";
            }
            else if (rb_chimney_moderate.isChecked())
            {
                OtherFallingHazardType = "Moderate";
            }
            else if (rb_chimney_severe.isChecked())
            {
                OtherFallingHazardType = "Severe";
            }

            String CrackingType = "";
            if (rb_ground_none.isChecked())
            {
                CrackingType = "None";
            }
            else if (rb_ground_minor.isChecked())
            {
                CrackingType = "Minor";
            }
            else if (rb_ground_moderate.isChecked())
            {
                CrackingType = "Moderate";
            }
            else if (rb_ground_severe.isChecked())
            {
                CrackingType = "Severe";
            }

            String OtherOptionalType               = edt_ObservedConditions_Others.getText().toString() != null ? edt_ObservedConditions_Others.getText().toString():"";
            String Comments                        = edt_ObservedConditions_Comments.getText().toString() != null ? edt_ObservedConditions_Comments.getText().toString():"";
            String EstimatedBuildingDamage         = spnr_EstimatedBldgDamage.getSelectedItem().toString() != null ? spnr_EstimatedBldgDamage.getSelectedItem().toString():"";
            String PreviousPostingEstimatedDamage  = spnr_PreviousPosting.getSelectedItem().toString() != null ? spnr_PreviousPosting.getSelectedItem().toString():"";
            String PreviousPostingDate             = edt_PreviousPosting_Date.getText().toString() != null ? edt_PreviousPosting_Date.getText().toString():"";

            String ColorPlacard  = "";
            if (rb_GreenPlacard.isChecked())
            {
                ColorPlacard = "Green";
            }
            else if (rb_YellowPlacard.isChecked())
            {
                ColorPlacard = "Yellow";
            }
            else if (rb_RedPlacard.isChecked())
            {
                ColorPlacard = "Red";
            }

            String FurtherActionsBarricades = "";
            if (cb_bldgBarricade.isChecked())
            {
                FurtherActionsBarricades = "1";
            }
            else
            {
                FurtherActionsBarricades = "0";
            }

            String FurtherActionsDetailedEvaluationRecommended = "";
            if (cb_bldgDetailed.isChecked())
            {
                FurtherActionsDetailedEvaluationRecommended = "1";
            }
            else
            {
                FurtherActionsDetailedEvaluationRecommended = "0";
            }

            String FurtherActionsOtherRecommended = "";
            if (cb_bldgOtherRecommendation.isChecked())
            {
                FurtherActionsOtherRecommended = "1";
            }
            else
            {
                FurtherActionsOtherRecommended = "0";
            }

            String FurtherActionComments        = edt_bldgBarricades.getText().toString() != null ? edt_bldgBarricades.getText().toString():"";
            String FurtherActionEstimatedDamage = spnr_bldgDetailed.getSelectedItem().toString() != null ? spnr_bldgDetailed.getSelectedItem().toString():"";
            String FurtherActionRecommendations = edt_bldgOtherRecommendation.getText().toString() != null ? edt_bldgOtherRecommendation.getText().toString():"";
            String SecondComments               = edt_bldgSecondComments.getText().toString() != null ? edt_bldgSecondComments.getText().toString():"";
            String InspectedBy                  = edt_InspectedBy.getText().toString() != null ? edt_InspectedBy.getText().toString():"";

            RESAClass resaClass = new RESAClass();

            resaClass.setScreenerID(ScreenerID);
            resaClass.setMissionOrderID(MissionOrderID);
            resaClass.setAffiliation(Affiliation);
            resaClass.setSetDate(SetDate);
            resaClass.setSetTime(SetTime);
            resaClass.setBuildingName(BuildingName);
            resaClass.setBuildingAddress(BuildingAddress);
            resaClass.setBuildingContact(BuildingContact);
            resaClass.setNoOfStoreyAboveGround(NoOfStoreyAboveGround);
            resaClass.setNoOfStoreyBelowGround(NoOfStoreyBelowGround);
            resaClass.setTypeOfConstruction(TypeOfConstruction);
            resaClass.setPrimaryOccupancy(PrimaryOccupancy);
            resaClass.setApproxFootPrintAreaSM(ApproxFootPrintAreaSM);
            resaClass.setNoOfResidentialUnits(NoOfResidentialUnits);
            resaClass.setNoOfCommercialUnits(NoOfCommercialUnits);
            resaClass.setBuildingOffFoundationType(BuildingOffFoundationType);
            resaClass.setStoryLeaningType(StoryLeaningType);
            resaClass.setOtherStructuralDamageType(OtherStructuralDamageType);
            resaClass.setOtherFallingHazardType(OtherFallingHazardType);
            resaClass.setCrackingType(CrackingType);
            resaClass.setOtherOptionalType(OtherOptionalType);
            resaClass.setComments(Comments);
            resaClass.setEstimatedBuildingDamage(EstimatedBuildingDamage);
            resaClass.setPreviousPostingEstimatedDamage(PreviousPostingEstimatedDamage);
            resaClass.setPreviousPostingDate(PreviousPostingDate);

            resaClass.setColorPlacard(ColorPlacard);

            resaClass.setFurtherActionsBarricades(FurtherActionsBarricades);
            resaClass.setFurtherActionsDetailedEvaluationRecommended(FurtherActionsDetailedEvaluationRecommended);
            resaClass.setFurtherActionsOtherRecommended(FurtherActionsOtherRecommended);

            resaClass.setFurtherActionComments(FurtherActionComments);
            resaClass.setFurtherActionEstimatedDamage(FurtherActionEstimatedDamage);
            resaClass.setFurtherActionRecommendations(FurtherActionRecommendations);
            resaClass.setSecondComments(SecondComments);
            resaClass.setInspectedBy(InspectedBy);

            if (option.equals("Save"))
            {
                RepositoryRESA.saveRESA(getContext(), resaClass);

                initShowDialogSuccess("Saved");
            }
            else
            {
                RepositoryRESA.updateRESA(getContext(), ScreenerID, MissionOrderID, resaClass);

                initShowDialogSuccess("Updated");
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initShowDialogSuccess(String option)
    {
        try
        {
            final AlertDialog.Builder mBuilder = new AlertDialog.Builder(requireContext());
            mBuilder.setCancelable(false);
            LayoutInflater inflater = getLayoutInflater();

            View view = inflater.inflate(R.layout.custom_information_saving, null);

            String message;

            if (option.equalsIgnoreCase("Saved"))
            {
                message = "Your report have been successfully saved!";
            }
            else
            {
                message = "Your changes have been successfully updated!";
            }

            TextView tv_display = view.findViewById(R.id.tv_display);
            tv_display.setText(message);

            Button btn_ok = view.findViewById(R.id.btn_ok);

            mBuilder.setView(view);

            final AlertDialog dialog2 = mBuilder.create();

            dialog2.show();

            btn_ok.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    dialog2.dismiss();

                    requireActivity().finish();
                }
            });
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    //GOOGLE MAPS
    private void getLocationPermission()
    {
        Log.i(TAG, "getLocationPermission: getting location permissions");

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(requireContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            if (ContextCompat.checkSelfPermission(getContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                Log.i(TAG, "getLocationPermission: permissions granted");

                mLocationPermissionGranted = true;

                initMap();
            }
            else
            {
                ActivityCompat.requestPermissions(requireActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else
        {
            ActivityCompat.requestPermissions(requireActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }

        Log.i(TAG, "getLocationPermission: getting location permissions");
    }

    private void initMap()
    {
        Log.i(TAG, "initMap: initializing map");

        mapFragment.getMapAsync(new OnMapReadyCallback()
        {
            @Override
            public void onMapReady(GoogleMap googleMap)
            {
                //Toast.makeText(getActivity(), "Map is ready", Toast.LENGTH_SHORT).show();

                mMap = googleMap;

                if (mLocationPermissionGranted)
                {
                    getDeviceLocation(); //Getting Self Location

                    if (ActivityCompat.checkSelfPermission(requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                            (requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED)
                    {
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                    // mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    mMap.getUiSettings().setZoomControlsEnabled(true);

                    initListenersMaps();
                }
            }
        });
    }

    private void getDeviceLocation()
    {
        markerOptions.position(defaultCoordinate);

        mMap.clear();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultCoordinate, DEFAULT_ZOOM));
        mMap.addMarker(markerOptions).setTitle("GOOGLE MAP");
    }

    private void initListenersMaps()
    {
        //MAP CLICKED-MARK
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng latLng)
            {
                markerOptions = new MarkerOptions();
                markerOptions.position(latLng);

                String tempLongitude, tempLatitude;
                tempLatitude = Double.toString(latLng.latitude);
                tempLongitude = Double.toString(latLng.longitude);

                //edt_get_latitude.setText(tempLatitude);
                //edt_get_longitude.setText(tempLongitude);

                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                mMap.addMarker(markerOptions);

                MapPosition = latLng;
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.i(TAG, "onRequestPermissionsResult: called");

        mLocationPermissionGranted  = false;

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE)
        {
            if (grantResults.length > 0)
            {
                for (int grantResult : grantResults)
                {
                    if (grantResult != PackageManager.PERMISSION_GRANTED)
                    {
                        mLocationPermissionGranted = false;

                        Log.i(TAG, "onRequestPermissionsResult: permission failed");
                        return;
                    }
                }

                Log.i(TAG, "onRequestPermissionsResult: permission granted");
                mLocationPermissionGranted = true;

                initMap();
            }
        }
    }



    private void initSetTime(final TextView textView)
    {
        try
        {
            int mHour, mMinute;

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), R.style.date_picker_dialog_theme,
                    new TimePickerDialog.OnTimeSetListener()
                    {
                        @SuppressLint("SimpleDateFormat")
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                        {
                            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            c.set(Calendar.MINUTE, minute);
                            Format formatter;

                            formatter = new SimpleDateFormat("h:mm a");
                            String timeSet = formatter.format(c.getTime());

                            textView.setText(timeSet);
                        }
                    },
                    mHour, mMinute, false);
            //timePickerDialog.setTitle("Select Time");
            timePickerDialog.show();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initSeDateAndTime()
    {
        try
        {
            long timestampMilliseconds = System.currentTimeMillis();
            SimpleDateFormat DateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);

            DateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
            String actualDate = DateFormat.format(new Date(timestampMilliseconds));

            //SimpleDateFormat TimeFormat = new SimpleDateFormat("hh:mm:ss a", Locale.US);
            SimpleDateFormat TimeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
            TimeFormat.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));

            String actualTime = TimeFormat.format(new Date(timestampMilliseconds));

            edt_set_date.setText(actualDate);
            edt_set_time.setText(actualTime);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }


    private void initAddPhoto(int resultCodeCamera, int resultCodeGallery)
    {
        try
        {
            final CharSequence[] options = {"Capture Photo", "Attach Photo"};

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Take Photo");
            builder.setItems(options, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int item)
                {
                    if (options[item].equals("Capture Photo"))
                    {
                        initGetPictureFromCamera(resultCodeCamera);
                    }
                    else if (options[item].equals("Attach Photo"))
                    {
                        initGetPictureGallery(resultCodeGallery);
                    }
                }
            });
            builder.show();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initGetPictureFromCamera(int resultCode)
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

            ImageExtension = "png";
            String ImagePath = SaveFolderName + "/" + ImageName + "." + ImageExtension;
            SignaturePath = ImagePath;

            File outFile = new File(ImagePath);

            CameraFileName = outFile.toString();
            Uri outURI     = Uri.fromFile(outFile);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, outURI);
            startActivityForResult(intent, resultCode);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initGetPictureGallery(int resultCode)
    {
        try
        {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, resultCode);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    @SuppressLint("SetTextI18n")
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
                    try
                    {
                        imageUri = null;

                        if (data != null)
                        {
                            imageUri = data.getData();
                        }

                        if (imageUri == null && CameraFileName != null)
                        {
                            imageUri = Uri.fromFile(new File(CameraFileName));
                        }

                        if (imageUri != null && CameraFileName != null)
                        {
                            File file = new File(CameraFileName);

                            if (!file.exists())
                            {
                                boolean isCreated = file.mkdir();
                            }

                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);

                            if (bitmap != null)
                            {
                                btn_take_photo.setText("CHANGE PHOTO");
                                btn_view_photo.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
                else if (requestCode == IMAGE_PICK_GALLERY_CODE)
                {
                    if (data != null)
                    {
                        imageUri = data.getData();

                        if (imageUri != null)
                        {
                            try
                            {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
                                //Bitmap bitMapCustomize = Bitmap.createScaledBitmap(bitmap, 1000, 1000, false);

                                if (bitmap!= null)
                                {
                                    createDirectoryAndSaveFile(bitmap);

                                    btn_take_photo.setText("CHANGE PHOTO");
                                    btn_view_photo.setVisibility(View.VISIBLE);

                                    //iv_building_image.setVisibility(View.VISIBLE);
                                    //Glide.with(requireContext()).load(bitmap).into(iv_building_image);
                                }
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, "Saving Error: " +  e.toString());
                            }
                        }
                    }
                }

                else if (requestCode == IMAGE_PICK_CAMERA_COLLAPSE)
                {
                    try
                    {
                        imageUri = null;

                        if (data != null)
                        {
                            imageUri = data.getData();
                        }

                        if (imageUri == null && CameraFileName != null)
                        {
                            imageUri = Uri.fromFile(new File(CameraFileName));
                        }

                        if (imageUri != null && CameraFileName != null)
                        {
                            File file = new File(CameraFileName);

                            if (!file.exists())
                            {
                                boolean isCreated = file.mkdir();
                            }

                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);

                            if (bitmap != null)
                            {
                                bmCollapse = bitmap;
                                Glide.with(requireContext()).load(bitmap).into(imgv_collapse);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
                else if (requestCode == IMAGE_PICK_GALLERY_COLLAPSE)
                {
                    if (data != null)
                    {
                        imageUri = data.getData();

                        if (imageUri != null)
                        {
                            try
                            {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
                                //Bitmap bitMapCustomize = Bitmap.createScaledBitmap(bitmap, 1000, 1000, false);

                                if (bitmap != null)
                                {
                                    bmCollapse = bitmap;

                                    createDirectoryAndSaveFile(bitmap);

                                    Glide.with(requireContext()).load(bitmap).into(imgv_collapse);
                                }
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, "Saving Error: " +  e.toString());
                            }
                        }
                    }
                }

                else if (requestCode == IMAGE_PICK_CAMERA_STORY_LEANING)
                {
                    try
                    {
                        imageUri = null;

                        if (data != null)
                        {
                            imageUri = data.getData();
                        }

                        if (imageUri == null && CameraFileName != null)
                        {
                            imageUri = Uri.fromFile(new File(CameraFileName));
                        }

                        if (imageUri != null && CameraFileName != null)
                        {
                            File file = new File(CameraFileName);

                            if (!file.exists())
                            {
                                boolean isCreated = file.mkdir();
                            }

                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);

                            if (bitmap != null)
                            {
                                bmBuildingStoryLeaning = bitmap;
                                Glide.with(requireContext()).load(bitmap).into(imgv_bldg);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
                else if (requestCode == IMAGE_PICK_GALLERY_STORY_LEANING)
                {
                    if (data != null)
                    {
                        imageUri = data.getData();

                        if (imageUri != null)
                        {
                            try
                            {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
                                //Bitmap bitMapCustomize = Bitmap.createScaledBitmap(bitmap, 1000, 1000, false);

                                if (bitmap != null)
                                {
                                    bmBuildingStoryLeaning = bitmap;

                                    createDirectoryAndSaveFile(bitmap);

                                    Glide.with(requireContext()).load(bitmap).into(imgv_bldg);
                                }
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, "Saving Error: " +  e.toString());
                            }
                        }
                    }
                }

                else if (requestCode == IMAGE_PICK_CAMERA_CRACKING)
                {
                    try
                    {
                        imageUri = null;

                        if (data != null)
                        {
                            imageUri = data.getData();
                        }

                        if (imageUri == null && CameraFileName != null)
                        {
                            imageUri = Uri.fromFile(new File(CameraFileName));
                        }

                        if (imageUri != null && CameraFileName != null)
                        {
                            File file = new File(CameraFileName);

                            if (!file.exists())
                            {
                                boolean isCreated = file.mkdir();
                            }

                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);

                            if (bitmap != null)
                            {
                                bmCracking = bitmap;
                                Glide.with(requireContext()).load(bitmap).into(imgv_cracking);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
                else if (requestCode == IMAGE_PICK_GALLERY_CRACKING)
                {
                    if (data != null)
                    {
                        imageUri = data.getData();

                        if (imageUri != null)
                        {
                            try
                            {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
                                //Bitmap bitMapCustomize = Bitmap.createScaledBitmap(bitmap, 1000, 1000, false);

                                if (bitmap != null)
                                {
                                    bmCracking = bitmap;

                                    createDirectoryAndSaveFile(bitmap);

                                    Glide.with(requireContext()).load(bitmap).into(imgv_cracking);
                                }
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, "Saving Error: " +  e.toString());
                            }
                        }
                    }
                }

                else if (requestCode == IMAGE_PICK_CAMERA_CHIMNEY)
                {
                    try
                    {
                        imageUri = null;

                        if (data != null)
                        {
                            imageUri = data.getData();
                        }

                        if (imageUri == null && CameraFileName != null)
                        {
                            imageUri = Uri.fromFile(new File(CameraFileName));
                        }

                        if (imageUri != null && CameraFileName != null)
                        {
                            File file = new File(CameraFileName);

                            if (!file.exists())
                            {
                                boolean isCreated = file.mkdir();
                            }

                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);

                            if (bitmap != null)
                            {
                                bmChimney = bitmap;
                                Glide.with(requireContext()).load(bitmap).into(imgv_chimney);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
                else if (requestCode == IMAGE_PICK_GALLERY_STORY_CHIMNEY)
                {
                    if (data != null)
                    {
                        imageUri = data.getData();

                        if (imageUri != null)
                        {
                            try
                            {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
                                //Bitmap bitMapCustomize = Bitmap.createScaledBitmap(bitmap, 1000, 1000, false);

                                if (bitmap != null)
                                {
                                    bmChimney = bitmap;

                                    createDirectoryAndSaveFile(bitmap);

                                    Glide.with(requireContext()).load(bitmap).into(imgv_chimney);
                                }
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, "Saving Error: " +  e.toString());
                            }
                        }
                    }
                }

                else if (requestCode == IMAGE_PICK_CAMERA_GROUND)
                {
                    try
                    {
                        imageUri = null;

                        if (data != null)
                        {
                            imageUri = data.getData();
                        }

                        if (imageUri == null && CameraFileName != null)
                        {
                            imageUri = Uri.fromFile(new File(CameraFileName));
                        }

                        if (imageUri != null && CameraFileName != null)
                        {
                            File file = new File(CameraFileName);

                            if (!file.exists())
                            {
                                boolean isCreated = file.mkdir();
                            }

                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);

                            if (bitmap != null)
                            {
                                bmGroundSlope = bitmap;
                                Glide.with(requireContext()).load(bitmap).into(imgv_ground);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
                else if (requestCode == IMAGE_PICK_GALLERY_GROUND)
                {
                    imageUri = null;

                    if (data != null)
                    {
                        imageUri = data.getData();

                        if (imageUri != null)
                        {
                            try
                            {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
                                //Bitmap bitMapCustomize = Bitmap.createScaledBitmap(bitmap, 1000, 1000, false);

                                if (bitmap != null)
                                {
                                    bmGroundSlope = bitmap;

                                    createDirectoryAndSaveFile(bitmap);

                                    Glide.with(requireContext()).load(bitmap).into(imgv_ground);
                                }
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, "Saving Error: " +  e.toString());
                            }
                        }
                    }


                }

                else if (requestCode == IMAGE_PICK_CAMERA_PLACARD)
                {
                    try
                    {
                        imageUri = null;

                        if (data != null)
                        {
                            imageUri = data.getData();
                        }

                        if (imageUri == null && CameraFileName != null)
                        {
                            imageUri = Uri.fromFile(new File(CameraFileName));
                        }

                        if (imageUri != null && CameraFileName != null)
                        {
                            File file = new File(CameraFileName);

                            if (!file.exists())
                            {
                                boolean isCreated = file.mkdir();
                            }

                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);

                            if (bitmap != null)
                            {
                                bmPosting = bitmap;
                                Glide.with(requireContext()).load(bitmap).into(imgv_PlaceCard);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
                else if (requestCode == IMAGE_PICK_GALLERY_PLACARD)
                {
                    if (data != null)
                    {
                        imageUri = data.getData();

                        if (imageUri != null)
                        {
                            try
                            {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
                                //Bitmap bitMapCustomize = Bitmap.createScaledBitmap(bitmap, 1000, 1000, false);

                                if (bitmap != null)
                                {
                                    bmPosting = bitmap;

                                    createDirectoryAndSaveFile(bitmap);

                                    Glide.with(requireContext()).load(bitmap).into(imgv_PlaceCard);
                                }
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, "Saving Error: " +  e.toString());
                            }
                        }
                    }
                }

                //
                else if (requestCode == IMAGE_PICK_CAMERA_SIGNATURE)
                {
                    try
                    {
                        imageUri = null;

                        if (data != null)
                        {
                            imageUri = data.getData();
                        }

                        if (imageUri == null && CameraFileName != null)
                        {
                            imageUri = Uri.fromFile(new File(CameraFileName));
                        }

                        if (imageUri != null && CameraFileName != null)
                        {
                            File file = new File(CameraFileName);

                            if (!file.exists())
                            {
                                boolean isCreated = file.mkdir();
                            }

                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);

                            if (bitmap != null)
                            {
                                bmInspectorSignature = bitmap;

                                Glide.with(requireContext()).load(bitmap).into(imgv_bldgSignature);

                                initSaveSignaturePhoto();
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
                else if (requestCode == IMAGE_PICK_GALLERY_SIGNATURE)
                {
                    if (data != null)
                    {
                        imageUri = data.getData();

                        if (imageUri != null)
                        {
                            try
                            {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
                                //Bitmap bitMapCustomize = Bitmap.createScaledBitmap(bitmap, 1000, 1000, false);

                                if (bitmap != null)
                                {
                                    bmInspectorSignature = bitmap;

                                    createDirectoryAndSaveFile(bitmap);

                                    Glide.with(requireContext()).load(bitmap).into(imgv_bldgSignature);
                                }
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, "Saving Error: " +  e.toString());
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initSaveSignaturePhoto()
    {
        try
        {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy hh:mm:ss a");
            Date now = new Date(System.currentTimeMillis());
            String dateAdded = dateFormat.format(now);

            InspectorSignatureClass inspectorSignatureClass = new InspectorSignatureClass();

            inspectorSignatureClass.setUserAccountID(UserAccount.UserAccountID);
            inspectorSignatureClass.setMissionOrderID(MissionOrderID);
            inspectorSignatureClass.setSignatureID("RESA");

            inspectorSignatureClass.setSignatureName(ImageName);
            inspectorSignatureClass.setSignatureExtension(ImageExtension);
            inspectorSignatureClass.setSignaturePath(SignaturePath);
            inspectorSignatureClass.setDtAdded(dateAdded);

            Cursor cursor = RepositoryInspectorSignature.realAllData2(getContext(), UserAccount.UserAccountID, MissionOrderID, "RESA");

            if (cursor.getCount()!=0)
            {
                if (cursor.moveToFirst())
                {
                    String ID = cursor.getString(cursor.getColumnIndex("ID"));

                    RepositoryInspectorSignature.updateInspectorSignature(getContext(), ID, inspectorSignatureClass);
                }
            }
            else
            {
                RepositoryInspectorSignature.saveInspectorSignature(getContext(), inspectorSignatureClass);
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void createDirectoryAndSaveFile(Bitmap imageToSave)
    {
        try
        {
            if (ActivityCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED

                    && ActivityCompat.checkSelfPermission(requireContext(),
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

                    ImageExtension = "png";
                    String ImagePath = SaveFolderName + "/" + ImageName + "." + ImageExtension;

                    File file = new File(SaveFolderName, ImageName);

                    try //FILE TO PNG.
                    {
                        FileOutputStream out = new FileOutputStream(file + "." + ImageExtension);

                        imageToSave.compress(Bitmap.CompressFormat.PNG, 100, out);

                        out.flush();
                        out.close();
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, "Saving Image: " + e.toString());
                    }

                    //
                    String SignatureName = "Created Signature" + "-" + dtAdded;

                    if (ImageName.equalsIgnoreCase(SignatureName))
                    {
                        initSaveSignaturePhoto();
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



    private void initLoadPhoto(File file, int ResultCodeCamera, int ResultCodeGallery, ImageView imageView, String ImageName)
    {
        try
        {
            TextView title = new TextView(getContext());
            String sTitle = "Added Photo";
            title.setText(sTitle);
            title.setPadding(10, 20, 10, 0);
            title.setGravity(Gravity.CENTER);
            title.setTextColor(Color.parseColor("#198754"));
            title.setTextSize(20);

            final android.app.AlertDialog.Builder imageDialog = new android.app.AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle);
            final LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.view_image_dialog_layout, (ViewGroup) ((Activity) getContext()).findViewById(R.id.layout_root));

            imageDialog.setView(layout);
            imageDialog.setCustomTitle(title);

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
            //bitmap = initImageRotateNormal(file, bitmap);
            Bitmap bitMapCustomize = Bitmap.createScaledBitmap(bitmap, 1000, 1000, false);

            ImageView image =  layout.findViewById(R.id.imageView);
            image.setImageBitmap(bitMapCustomize);

            imageDialog.setNeutralButton("REMOVE", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener()
                    {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onClick(DialogInterface alertDialog, int which)
                        {
                            if (which == DialogInterface.BUTTON_POSITIVE)
                            {
                                if (imageView!= null)
                                {
                                    Glide.with(requireContext()).load(R.drawable.photo_not_exist).into(imageView);
                                }

                                initRemovedImages(ImageName);

                                boolean isDeleted = file.delete();

                                Toast.makeText(getActivity(), "Photo removed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    };

                    AlertDialog.Builder mValid = new AlertDialog.Builder(requireContext());
                    mValid.setTitle("Remove Photo");
                    mValid.setMessage("Please confirm to remove the photo");
                    mValid.setCancelable(false);
                    mValid.setPositiveButton("Confirm", ok);
                    mValid.setNegativeButton("Cancel", null);
                    mValid.show();
                }
            });

            imageDialog.setNegativeButton("CLOSE", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                }
            });

            imageDialog.setPositiveButton("CHANGE", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();

                    initAddPhoto(ResultCodeCamera, ResultCodeGallery);
                }
            });

            imageDialog.show();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    @SuppressLint("SetTextI18n")
    private void initRemovedImages(String ImageName)
    {
        if (ImageName.contains("Building Image"))
        {
            btn_take_photo .setText("TAKE PHOTO");
            btn_view_photo.setVisibility(View.GONE);
        }

        if (ImageName.contains("Collapse Image"))
        {
            bmCollapse = null;
        }

        if (ImageName.contains("Story Leaning Image"))
        {
            bmBuildingStoryLeaning = null;
        }

        if (ImageName.contains("Cracking Image"))
        {
            bmCracking = null;
        }

        if (ImageName.contains("Chimney Image"))
        {
            bmChimney = null;
        }

        if (ImageName.contains("Ground Image"))
        {
            bmGroundSlope = null;
        }

        if (ImageName.contains("Placard Image"))
        {
            bmPosting = null;
        }

    }

    //Customize Bitmap to Normal angle of Picture
    private static Bitmap initImageRotateNormal(File imagePath, Bitmap bitmap)
    {
        ExifInterface ei = null;

        try
        {
            ei = new ExifInterface((imagePath.getAbsolutePath()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        int orientation = Objects.requireNonNull(ei).getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap rotatedBitmap;

        switch (orientation)
        {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
        }

        return rotatedBitmap;
    }

    //Fix Auto rotate in Some Camera
    private static Bitmap rotateImage(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0,0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        initGetImageSaved();

        initGetSignature();

        if (pDialog.isShowing())
        {
            pDialog.dismiss();
        }
    }

    @SuppressLint("SetTextI18n")
    private void initGetImageSaved()
    {
        try
        {
            String ImageName_Building_Image = "Building Image" + "-" + MissionOrderNo + "-" + dtAdded;
            String ImagePath_Building_Image = SaveFolderName + "/" + ImageName_Building_Image + ".png";

            File ImageFile_Building_Image = new File(ImagePath_Building_Image);

            if (ImageFile_Building_Image.exists())
            {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(ImagePath_Building_Image, bmOptions);

                if(bitmap != null)
                {
                    //bitmap = initImageRotateNormal(ImageFile_Building_Image, bitmap);

                    btn_take_photo.setText("CHANGE PHOTO");
                    btn_view_photo.setVisibility(View.VISIBLE);

                    //iv_building_image.setVisibility(View.VISIBLE);
                    //Glide.with(requireContext()).load(bitmap).into(iv_building_image);
                }
            }

            String ImageName_Collapse = "Collapse Image" + "-" + MissionOrderNo + "-" + dtAdded;
            String ImagePath_Collapse = SaveFolderName + "/" + ImageName_Collapse + ".png";

            File ImageFile_Collapse = new File(ImagePath_Collapse);

            if (ImageFile_Collapse.exists())
            {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(ImagePath_Collapse, bmOptions);

                if (bitmap != null)
                {
                    //bitmap = initImageRotateNormal(ImageFile_Collapse, bitmap);

                    bmCollapse = bitmap;

                    Glide.with(requireContext()).load(bitmap).into(imgv_collapse);
                }
            }

            String ImageName_StoryLeaning = "Story Leaning Image" + "-" + MissionOrderNo + "-" + dtAdded;
            String ImagePath_StoryLeaning = SaveFolderName + "/" + ImageName_StoryLeaning + ".png";

            File ImageFile_StoryLeaning = new File(ImagePath_StoryLeaning);

            if (ImageFile_StoryLeaning.exists())
            {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(ImagePath_StoryLeaning, bmOptions);

                if (bitmap != null)
                {
                    //bitmap = initImageRotateNormal(ImageFile_StoryLeaning, bitmap);

                    bmBuildingStoryLeaning = bitmap;

                    Glide.with(requireContext()).load(bitmap).into(imgv_bldg);
                }
            }

            String ImageName_Cracking = "Cracking Image" + "-" + MissionOrderNo + "-" + dtAdded;
            String ImagePath_Cracking = SaveFolderName + "/" + ImageName_Cracking + ".png";

            File ImageFile_Cracking = new File(ImagePath_Cracking);

            if (ImageFile_Cracking.exists())
            {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(ImagePath_Cracking, bmOptions);

                if (bitmap != null)
                {
                    //bitmap = initImageRotateNormal(ImageFile_Cracking, bitmap);

                    bmCracking = bitmap;

                    Glide.with(requireContext()).load(bitmap).into(imgv_cracking);
                }
            }


            String ImageName_Chimney = "Chimney Image" + "-" + MissionOrderNo + "-" + dtAdded;
            String ImagePath_Chimney = SaveFolderName + "/" + ImageName_Chimney + ".png";

            File ImageFile_Chimney = new File(ImagePath_Chimney);

            if (ImageFile_Chimney.exists())
            {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(ImagePath_Chimney, bmOptions);

                if (bitmap != null)
                {
                    //bitmap = initImageRotateNormal(ImageFile_Chimney, bitmap);

                    bmChimney = bitmap;

                    Glide.with(requireContext()).load(bitmap).into(imgv_chimney);
                }
            }


            String ImageName_Ground = "Ground Image" + "-" + MissionOrderNo + "-" + dtAdded;
            String ImagePath_Ground = SaveFolderName + "/" + ImageName_Ground + ".png";

            File ImageFile_Ground = new File(ImagePath_Ground);

            if (ImageFile_Ground.exists())
            {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(ImagePath_Ground, bmOptions);

                if (bitmap != null)
                {
                    //bitmap = initImageRotateNormal(ImageFile_Ground, bitmap);

                    bmGroundSlope = bitmap;

                    Glide.with(requireContext()).load(bitmap).into(imgv_ground);
                }
            }


            String ImageName_Placard = "Placard Image" + "-" + MissionOrderNo + "-" + dtAdded;
            String ImagePath_Placard = SaveFolderName + "/" + ImageName_Placard + ".png";

            File ImageFile_Placard = new File(ImagePath_Placard);

            if (ImageFile_Placard.exists())
            {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(ImagePath_Placard, bmOptions);

                if (bitmap != null)
                {
                    //bitmap = initImageRotateNormal(ImageFile_Placard, bitmap);

                    bmPosting = bitmap;

                    Glide.with(requireContext()).load(bitmap).into(imgv_PlaceCard);
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initSetSignature()
    {
        try
        {
            final CharSequence[] options = {"Create Signature", "Capture Signature","Attach Signature"};

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Add Signature");
            builder.setItems(options, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int item)
                {
                    if (options[item].equals("Create Signature"))
                    {
                        Intent intent = new Intent(getContext(), CreateSignatureActivity.class);
                        intent.putExtra("Option", "Create Signature");
                        intent.putExtra("ID", "RESA");
                        intent.putExtra("MissionOrderID", MissionOrderID);
                        ((Activity) requireContext()).startActivityForResult(intent, 105);
                    }
                    else if (options[item].equals("Capture Signature"))
                    {
                        ImageName = "Created Signature" + "-" + dtAdded;
                        ImageExtension = "png";

                        initGetPictureFromCamera(IMAGE_PICK_CAMERA_SIGNATURE);
                    }
                    else if (options[item].equals("Attach Signature"))
                    {
                        ImageName = "Created Signature" + "-" + dtAdded;
                        ImageExtension = "png";

                        initGetPictureGallery(IMAGE_PICK_GALLERY_PLACARD);
                    }
                }
            });
            builder.show();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initGetSignature()
    {
        Cursor cursor = RepositoryInspectorSignature.realAllData(getContext(), "RESA", MissionOrderID);

        if (cursor.getCount()!=0)
        {
            if (cursor.moveToFirst())
            {
                String SignaturePath = cursor.getString(cursor.getColumnIndex("SignaturePath"));

                File file = new File(SignaturePath);

                if (file.exists())
                {
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeFile(SignaturePath, bmOptions);
                    //bitmap = initImageRotateNormal(file, bitmap);

                    bmInspectorSignature = bitmap;

                    Glide.with(requireContext()).load(bitmap).into(imgv_bldgSignature);
                }
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class initSetTimeAndDate extends AsyncTask<String, Void, String>
    {
        @Override
        protected void onPreExecute()
        {
            initSetDateTimeOffline();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                String TIME_SERVER = "time-a.nist.gov";

                NTPUDPClient timeClient = new NTPUDPClient();
                InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
                TimeInfo timeInfo = timeClient.getTime(inetAddress);

                //long returnTime = timeInfo.getReturnTime(); //local device time
                long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime(); //server time

                Date time = new Date(returnTime);

                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
                String dateNow = dateFormat.format(time);

                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aaa");
                String timeNow = timeFormat.format(time);

                sDateNow = dateNow;

                //sDateNow.setText(dateNow);
                //tv_TimeNow.setText(timeNow);
            }
            catch (Exception e)
            {
                Log.e(TAG,e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
        }

    }

    private void initSetDateTimeOffline()
    {
        //Current Date and Time
        Date now = new Date(System.currentTimeMillis());
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aaa");

        sDateNow = dateFormat.format(now);

        //  tv_DateNow.setText(dateNow);
        // tv_TimeNow.setText(timeNow);
    }

    private String FormatDate(String strDate)
    {
        String datetime;

        if (strDate != null)
        {
            @SuppressLint("SimpleDateFormat") DateFormat inputFormat = new SimpleDateFormat("MMMM dd, yyyy");
            @SuppressLint("SimpleDateFormat") DateFormat outputFormat  = new SimpleDateFormat("MM/dd/yyyy");
            try
            {
                Date convertedDate = inputFormat.parse(strDate);
                datetime = outputFormat.format(convertedDate);
            }
            catch (ParseException e)
            {
                datetime = "";
            }
        }
        else
        {
            datetime = "";
        }

        return  datetime;
    }

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

}

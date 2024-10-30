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
import com.geodata.rapida.plus.Activity.PreviewReportDESAActivity;
import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.SQLite.Class.DESAClass;
import com.geodata.rapida.plus.SQLite.Class.InspectorSignatureClass;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryDESA;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryInspectorSignature;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryOnlineAssignedInspectors;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryOnlineMissionOrders;
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

public class DESAFragment extends Fragment
{
    private static final String TAG = DESAFragment.class.getSimpleName();

    View view;

    EditText edt_set_date, edt_set_time, edt_affiliation, edt_bldg_name, edt_bldg_address,
            edt_bldg_contact, edt_NoOfStoreyAboveGround, edt_NoOfStoreyBelowGround,
            edt_Approx, edt_NoOfResidentialUnits, edt_NoOfCommercialUnits, edt_inspected_by,
            edt_overall_hazards_other, edt_PreviousPosting_Date, edt_bldgBarricadesComment, edt_bldgOtherRecommendation,
            edt_nonstructural_hazards_other, edt_geotechnical_hazard_other;

    Spinner spnr_TypeOfConstruction, spnr_PrimaryOccupancy, spnr_EstimatedBldgDamage, spnr_PreviousPosting, sp_EngineeringRecommendation;

    CheckBox cb_bldgBarricade, cb_EngineeringEvaluation, cb_bldgOtherRecommendation;

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

    ImageView iv_building_image, imgv_collapse, imgv_bldg, imgv_foundation,
            imgv_roof, imgv_column, imgv_diaphragms, imgv_walls, imgv_precast,
            imgv_parapets, imgv_cladding, imgv_ceiling, imgv_interior_walls,
            imgv_elevator, imgv_stair, imgv_electric, imgv_slope, imgv_ground,
            imgv_PlaceCard, imgv_bldgSignature;

    Bitmap bmCollapse, bmBuildingStoryLeaning, bmFoundation, bmRoofFloor,
            bmColumns, bmDiaphragms, bmWalls, bmPrecast,
            bmParapets, bmCladding, bmCeiling, bmInteriorWalls,
            bmElevators, bmStairs, bmElectric,
            bmSlope, bmGroundMovement, bmPosting, bmInspectorSignature;

    String SaveFolderName,
            ImageName, ImageExtension, ImagePath, CameraFileName;

    int IMAGE_PICK_CAMERA_CODE           = 100, IMAGE_PICK_GALLERY_CODE           = 101,
            IMAGE_PICK_CAMERA_COLLAPSE       = 102, IMAGE_PICK_GALLERY_COLLAPSE       = 103,
            IMAGE_PICK_CAMERA_STORY_LEANING  = 104, IMAGE_PICK_GALLERY_STORY_LEANING  = 105,
            IMAGE_PICK_CAMERA_FOUNDATION     = 106, IMAGE_PICK_GALLERY_FOUNDATION     = 107,
            IMAGE_PICK_CAMERA_ROOF           = 108, IMAGE_PICK_GALLERY_ROOF           = 109,
            IMAGE_PICK_CAMERA_COLUMN         = 110, IMAGE_PICK_GALLERY_COLUMN         = 111,
            IMAGE_PICK_CAMERA_DIAPHRAGMS     = 112, IMAGE_PICK_GALLERY_DIAPHRAGMS     = 113,
            IMAGE_PICK_CAMERA_WALLS          = 114, IMAGE_PICK_GALLERY_WALLS          = 115,
            IMAGE_PICK_CAMERA_PRECAST        = 116, IMAGE_PICK_GALLERY_PRECAST        = 117,
            IMAGE_PICK_CAMERA_PARAPETS       = 118, IMAGE_PICK_GALLERY_PARAPETS       = 119,
            IMAGE_PICK_CAMERA_CLADDING       = 120, IMAGE_PICK_GALLERY_CLADDING       = 121,
            IMAGE_PICK_CAMERA_CEILING        = 122, IMAGE_PICK_GALLERY_CEILING        = 123,
            IMAGE_PICK_CAMERA_INTERIOR_WALLS = 124, IMAGE_PICK_GALLERY_INTERIOR_WALLS = 125,
            IMAGE_PICK_CAMERA_ELEVATOR       = 126, IMAGE_PICK_GALLERY_ELEVATOR       = 127,
            IMAGE_PICK_CAMERA_STAIR          = 128, IMAGE_PICK_GALLERY_STAIR          = 129,
            IMAGE_PICK_CAMERA_ELECTRIC       = 130, IMAGE_PICK_GALLERY_ELECTRIC       = 131,
            IMAGE_PICK_CAMERA_SLOPE          = 132, IMAGE_PICK_GALLERY_SLOPE          = 133,
            IMAGE_PICK_CAMERA_GROUND         = 134, IMAGE_PICK_GALLERY_GROUND         = 135,
            IMAGE_PICK_CAMERA_PLACARD        = 136, IMAGE_PICK_GALLERY_PLACARD        = 137,
            IMAGE_PICK_CAMERA_SIGNATURE      = 138, IMAGE_PICK_GALLERY_SIGNATURE      = 139;

    Uri imageUri;

    String MissionOrderNo, MissionOrderID, SeismicityRegion,
            PdfFolderName, sDateNow, sTimeNow, dtAdded, SignaturePath;

    RadioButton rb_collapse_minor, rb_collapse_moderate, rb_collapse_severe,
            rb_bldg_minor, rb_bldg_moderate, rb_bldg_severe,
            rb_foundation_minor, rb_foundation_moderate, rb_foundation_severe,
            rb_roof_minor, rb_roof_moderate, rb_roof_severe,
            rb_columns_minor, rb_columns_moderate, rb_columns_severe,
            rb_diaphragms_minor, rb_diaphragms_moderate, rb_diaphragms_severe,
            rb_walls_minor, rb_walls_moderate, rb_walls_severe,
            rb_precast_minor, rb_precast_moderate, rb_precast_severe,
            rb_parapets_minor, rb_parapets_moderate, rb_parapets_severe,
            rb_cladding_minor, rb_cladding_moderate, rb_cladding_severe,
            rb_ceiling_minor, rb_ceiling_moderate, rb_ceiling_severe,
            rb_interior_walls_minor, rb_interior_walls_moderate, rb_interior_walls_severe,
            rb_elevators_minor, rb_elevators_moderate, rb_elevators_severe,
            rb_stairs_minor, rb_stairs_moderate, rb_stairs_severe,
            rb_electric_gas_minor, rb_electric_gas_moderate, rb_electric_gas_severe,
            rb_slope_failure_minor, rb_slope_failure_moderate, rb_slope_failure_severe,
            rb_ground_movement_minor, rb_ground_movement_moderate, rb_ground_movement_severe,
            rb_GreenPlacard, rb_YellowPlacard, rb_RedPlacard;

    EditText edt_collapse_comment, edt_bldg_comment, edt_foundation_comment,
            edt_roof_comment, edt_columns_comment, edt_diaphragms_comment,
            edt_walls_comment, edt_precast_comment, edt_parapets_comment,
            edt_cladding_comment, edt_ceiling_comment, edt_interior_walls_comment,
            edt_elevators_comment, edt_stairs_comment, edt_electric_gas_comment,
            edt_slope_failure_comment, edt_ground_movement_comment, edt_desa_comment;

    ProgressDialog pDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(getActivity())); //Getting Crash - Restart

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_desa_layout, container, false);

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

        PdfFolderName =  "SRI" + "/" + "DESA";

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
                + "/SRI/DESA/Attachments/";

        edt_affiliation           = view.findViewById(R.id.edt_affiliation);
        edt_bldg_name             = view.findViewById(R.id.edt_bldg_name);
        edt_bldg_address          = view.findViewById(R.id.edt_bldg_address);
        edt_bldg_contact          = view.findViewById(R.id.edt_bldg_contact);
        edt_NoOfStoreyAboveGround = view.findViewById(R.id.edt_NoOfStoreyAboveGround);
        edt_NoOfStoreyBelowGround = view.findViewById(R.id.edt_NoOfStoreyBelowGround);
        edt_Approx                = view.findViewById(R.id.edt_Approx);
        edt_NoOfResidentialUnits  = view.findViewById(R.id.edt_NoOfResidentialUnits);
        edt_NoOfCommercialUnits   = view.findViewById(R.id.edt_NoOfCommercialUnits);
        edt_inspected_by          = view.findViewById(R.id.edt_inspected_by);
        edt_inspected_by.setText(UserAccount.CompleteName);

        edt_overall_hazards_other       = view.findViewById(R.id.edt_overall_hazards_other);
        edt_PreviousPosting_Date        = view.findViewById(R.id.edt_PreviousPosting_Date);
        edt_bldgBarricadesComment       = view.findViewById(R.id.edt_bldgBarricadesComment);
        edt_bldgBarricadesComment.setEnabled(false);
        edt_bldgOtherRecommendation     = view.findViewById(R.id.edt_bldgOtherRecommendation);
        edt_bldgOtherRecommendation.setEnabled(false);
        edt_nonstructural_hazards_other = view.findViewById(R.id.edt_nonstructural_hazards_other);
        edt_geotechnical_hazard_other   = view.findViewById(R.id.edt_geotechnical_hazard_other);

        rb_collapse_minor           = view.findViewById(R.id.rb_collapse_minor);
        rb_collapse_moderate        = view.findViewById(R.id.rb_collapse_moderate);
        rb_collapse_severe          = view.findViewById(R.id.rb_collapse_severe);
        rb_bldg_minor               = view.findViewById(R.id.rb_bldg_minor);
        rb_bldg_moderate            = view.findViewById(R.id.rb_bldg_moderate);
        rb_bldg_severe              = view.findViewById(R.id.rb_bldg_severe);
        rb_foundation_minor         = view.findViewById(R.id.rb_foundation_minor);
        rb_foundation_moderate      = view.findViewById(R.id.rb_foundation_moderate);
        rb_foundation_severe        = view.findViewById(R.id.rb_foundation_severe);
        rb_roof_minor               = view.findViewById(R.id.rb_roof_minor);
        rb_roof_moderate            = view.findViewById(R.id.rb_roof_moderate);
        rb_roof_severe              = view.findViewById(R.id.rb_roof_severe);
        rb_columns_minor            = view.findViewById(R.id.rb_columns_minor);
        rb_columns_moderate         = view.findViewById(R.id.rb_columns_moderate);
        rb_columns_severe           = view.findViewById(R.id.rb_columns_severe);
        rb_diaphragms_minor         = view.findViewById(R.id.rb_diaphragms_minor);
        rb_diaphragms_moderate      = view.findViewById(R.id.rb_diaphragms_moderate);
        rb_diaphragms_severe        = view.findViewById(R.id.rb_diaphragms_severe);
        rb_walls_minor              = view.findViewById(R.id.rb_walls_minor);
        rb_walls_moderate           = view.findViewById(R.id.rb_walls_moderate);
        rb_walls_severe             = view.findViewById(R.id.rb_walls_severe);
        rb_precast_minor            = view.findViewById(R.id.rb_precast_minor);
        rb_precast_moderate         = view.findViewById(R.id.rb_precast_moderate);
        rb_precast_severe           = view.findViewById(R.id.rb_precast_severe);
        rb_parapets_minor           = view.findViewById(R.id.rb_parapets_minor);
        rb_parapets_moderate        = view.findViewById(R.id.rb_parapets_moderate);
        rb_parapets_severe          = view.findViewById(R.id.rb_parapets_severe);
        rb_cladding_minor           = view.findViewById(R.id.rb_cladding_minor);
        rb_cladding_moderate        = view.findViewById(R.id.rb_cladding_moderate);
        rb_cladding_severe          = view.findViewById(R.id.rb_cladding_severe);
        rb_ceiling_minor            = view.findViewById(R.id.rb_ceiling_minor);
        rb_ceiling_moderate         = view.findViewById(R.id.rb_ceiling_moderate);
        rb_ceiling_severe           = view.findViewById(R.id.rb_ceiling_severe);
        rb_interior_walls_minor     = view.findViewById(R.id.rb_interior_walls_minor);
        rb_interior_walls_moderate  = view.findViewById(R.id.rb_interior_walls_moderate);
        rb_interior_walls_severe    = view.findViewById(R.id.rb_interior_walls_severe);
        rb_elevators_minor          = view.findViewById(R.id.rb_elevators_minor);
        rb_elevators_moderate       = view.findViewById(R.id.rb_elevators_moderate);
        rb_elevators_severe         = view.findViewById(R.id.rb_elevators_severe);
        rb_stairs_minor             = view.findViewById(R.id.rb_stairs_minor);
        rb_stairs_moderate          = view.findViewById(R.id.rb_stairs_moderate);
        rb_stairs_severe            = view.findViewById(R.id.rb_stairs_severe);
        rb_electric_gas_minor       = view.findViewById(R.id.rb_electric_gas_minor);
        rb_electric_gas_moderate    = view.findViewById(R.id.rb_electric_gas_moderate);
        rb_electric_gas_severe      = view.findViewById(R.id.rb_electric_gas_severe);
        rb_slope_failure_minor      = view.findViewById(R.id.rb_slope_failure_minor);
        rb_slope_failure_moderate   = view.findViewById(R.id.rb_slope_failure_moderate);
        rb_slope_failure_severe     = view.findViewById(R.id.rb_slope_failure_severe);
        rb_ground_movement_minor    = view.findViewById(R.id.rb_ground_movement_minor);
        rb_ground_movement_moderate = view.findViewById(R.id.rb_ground_movement_moderate);
        rb_ground_movement_severe   = view.findViewById(R.id.rb_ground_movement_severe);
        rb_GreenPlacard  = view.findViewById(R.id.rb_GreenPlacard);
        rb_YellowPlacard = view.findViewById(R.id.rb_YellowPlacard);
        rb_RedPlacard    = view.findViewById(R.id.rb_RedPlacard);

        edt_collapse_comment         = view.findViewById(R.id.edt_collapse_comment);
        edt_bldg_comment             = view.findViewById(R.id.edt_bldg_comment);
        edt_foundation_comment       = view.findViewById(R.id.edt_foundation_comment);
        edt_roof_comment             = view.findViewById(R.id.edt_roof_comment);
        edt_columns_comment          = view.findViewById(R.id.edt_columns_comment);
        edt_diaphragms_comment       = view.findViewById(R.id.edt_diaphragms_comment);
        edt_walls_comment            = view.findViewById(R.id.edt_walls_comment);
        edt_precast_comment          = view.findViewById(R.id.edt_precast_comment);
        edt_parapets_comment         = view.findViewById(R.id.edt_parapets_comment);
        edt_cladding_comment         = view.findViewById(R.id.edt_cladding_comment);
        edt_ceiling_comment          = view.findViewById(R.id.edt_ceiling_comment);
        edt_interior_walls_comment   = view.findViewById(R.id.edt_interior_walls_comment);
        edt_elevators_comment        = view.findViewById(R.id.edt_elevators_comment);
        edt_stairs_comment           = view.findViewById(R.id.edt_stairs_comment);
        edt_electric_gas_comment     = view.findViewById(R.id.edt_electric_gas_comment);
        edt_slope_failure_comment    = view.findViewById(R.id.edt_slope_failure_comment);
        edt_ground_movement_comment  = view.findViewById(R.id.edt_ground_movement_comment);
        edt_desa_comment            = view.findViewById(R.id.edt_desa_comment);

        iv_building_image   =  view.findViewById(R.id.iv_building_image);
        imgv_collapse       =  view.findViewById(R.id.imgv_collapse);
        imgv_bldg           =  view.findViewById(R.id.imgv_bldg);
        imgv_foundation     =  view.findViewById(R.id.imgv_foundation);
        imgv_roof           =  view.findViewById(R.id.imgv_roof);
        imgv_column         =  view.findViewById(R.id.imgv_column);
        imgv_diaphragms     =  view.findViewById(R.id.imgv_diaphragms);
        imgv_walls          =  view.findViewById(R.id.imgv_walls);
        imgv_precast        =  view.findViewById(R.id.imgv_precast);
        imgv_parapets       =  view.findViewById(R.id.imgv_parapets);
        imgv_cladding       =  view.findViewById(R.id.imgv_cladding);
        imgv_ceiling        =  view.findViewById(R.id.imgv_ceiling);
        imgv_interior_walls = view.findViewById(R.id.imgv_interior_walls);
        imgv_elevator       =  view.findViewById(R.id.imgv_elevator);
        imgv_stair          =  view.findViewById(R.id.imgv_stair);
        imgv_electric       =  view.findViewById(R.id.imgv_electric);
        imgv_slope          =  view.findViewById(R.id.imgv_slope);
        imgv_ground         =  view.findViewById(R.id.imgv_ground);

        imgv_PlaceCard     =  view.findViewById(R.id.imgv_PlaceCard);
        imgv_bldgSignature =  view.findViewById(R.id.imgv_bldgSignature);


        edt_set_date = view.findViewById(R.id.edt_set_date);
        edt_set_time = view.findViewById(R.id.edt_set_time);

        spnr_TypeOfConstruction  = view.findViewById(R.id.spnr_TypeOfConstruction);
        spnr_PrimaryOccupancy    = view.findViewById(R.id.spnr_PrimaryOccupancy);
        spnr_EstimatedBldgDamage     = view.findViewById(R.id.spnr_EstimatedBldgDamage);
        spnr_PreviousPosting         = view.findViewById(R.id.spnr_PreviousPosting);
        sp_EngineeringRecommendation = view.findViewById(R.id.sp_EngineeringRecommendation);
        sp_EngineeringRecommendation.setEnabled(false);

        cb_bldgBarricade  = view.findViewById(R.id.cb_bldgBarricade);



        cb_EngineeringEvaluation   = view.findViewById(R.id.cb_EngineeringEvaluation);
        cb_bldgOtherRecommendation = view.findViewById(R.id.cb_bldgOtherRecommendation);

        btn_take_photo  = view.findViewById(R.id.btn_take_photo);
        btn_view_photo  = view.findViewById(R.id.btn_view_photo);
        btn_save      = view.findViewById(R.id.btn_save);
        btn_preview   = view.findViewById(R.id.btn_preview);

        iCalendar = Calendar.getInstance();

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map);
        markerOptions = new MarkerOptions();

        initCheckBoxListeners();

        initSeDateAndTime();

        initRetrieveDESAData();

        initListeners();
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
                    edt_bldgBarricadesComment.setEnabled(true);
                }
                else
                {
                    edt_bldgBarricadesComment.setEnabled(false);
                }
            }
        });

        cb_EngineeringEvaluation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (((CheckBox) v).isChecked())
                {
                    sp_EngineeringRecommendation.setEnabled(true);
                }
                else
                {
                    sp_EngineeringRecommendation.setEnabled(false);
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
            final DatePickerDialog.OnDateSetListener PreviousDate = new DatePickerDialog.OnDateSetListener()
            {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                {
                    iCalendar.set(Calendar.YEAR, year);
                    iCalendar.set(Calendar.MONTH, monthOfYear);
                    iCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    String myFormat = "MM/dd/yyyy"; //In which you need put here
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                    edt_PreviousPosting_Date.setText(sdf.format(iCalendar.getTime()));
                }
            };

            edt_PreviousPosting_Date.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    final DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), PreviousDate,
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


            final DatePickerDialog.OnDateSetListener CurrentDate = new DatePickerDialog.OnDateSetListener()
            {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                {
                    iCalendar.set(Calendar.YEAR, year);
                    iCalendar.set(Calendar.MONTH, monthOfYear);
                    iCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    String myFormat = "MM/dd/yyyy"; //In which you need put here
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                    edt_set_date.setText(sdf.format(iCalendar.getTime()));
                }
            };

            edt_set_date.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    final DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), CurrentDate,
                            iCalendar.get(Calendar.YEAR),
                            iCalendar.get(Calendar.MONTH),
                            iCalendar.get(Calendar.DATE));

                    if(!datePickerDialog.isShowing())
                    {
                        datePickerDialog.show();
                    }
                }
            });
            edt_set_date.setFocusable(false);


            btn_take_photo.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Log.e(TAG, "THIS IS CLICKED.");

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
                        ImageName = "Building Image" + "-" + MissionOrderNo  + "-" + dtAdded;
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
                    ImageExtension = "png";
                    ImageName = "Collapse Image" + "-" + MissionOrderNo  + "-" + dtAdded;

                    initImageListeners(ImageName, ImageExtension, IMAGE_PICK_CAMERA_COLLAPSE, IMAGE_PICK_GALLERY_COLLAPSE, imgv_collapse);
                }
            });

            imgv_bldg.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ImageExtension = "png";
                    ImageName = "Story Leaning Image" + "-" + MissionOrderNo  + "-" + dtAdded;

                    initImageListeners(ImageName, ImageExtension, IMAGE_PICK_CAMERA_STORY_LEANING, IMAGE_PICK_GALLERY_STORY_LEANING, imgv_bldg);
                }
            });

            imgv_foundation.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ImageExtension = "png";
                    ImageName = "Foundation Image" + "-" + MissionOrderNo  + "-" + dtAdded;

                    initImageListeners(ImageName, ImageExtension, IMAGE_PICK_CAMERA_FOUNDATION, IMAGE_PICK_GALLERY_FOUNDATION, imgv_foundation);
                }
            });

            imgv_roof.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ImageExtension = "png";
                    ImageName = "Roof Image" + "-" + MissionOrderNo  + "-" + dtAdded;

                    initImageListeners(ImageName, ImageExtension, IMAGE_PICK_CAMERA_ROOF, IMAGE_PICK_GALLERY_ROOF, imgv_roof);
                }
            });

            imgv_column.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ImageExtension = "png";
                    ImageName = "Column Image" + "-" + MissionOrderNo  + "-" + dtAdded;

                    initImageListeners(ImageName, ImageExtension, IMAGE_PICK_CAMERA_COLUMN, IMAGE_PICK_GALLERY_COLUMN, imgv_column);
                }
            });

            imgv_diaphragms.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ImageExtension = "png";
                    ImageName = "Diaphragms Image" + "-" + MissionOrderNo  + "-" + dtAdded;

                    initImageListeners(ImageName, ImageExtension, IMAGE_PICK_CAMERA_DIAPHRAGMS, IMAGE_PICK_GALLERY_DIAPHRAGMS, imgv_diaphragms);
                }
            });

            imgv_walls.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ImageExtension = "png";
                    ImageName = "Walls Image" + "-" + MissionOrderNo  + "-" + dtAdded;

                    initImageListeners(ImageName, ImageExtension, IMAGE_PICK_CAMERA_WALLS, IMAGE_PICK_GALLERY_WALLS, imgv_walls);
                }
            });

            imgv_precast.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ImageExtension = "png";
                    ImageName = "Precast Image" + "-" + MissionOrderNo  + "-" + dtAdded;

                    initImageListeners(ImageName, ImageExtension, IMAGE_PICK_CAMERA_PRECAST, IMAGE_PICK_GALLERY_PRECAST, imgv_precast);
                }
            });

            imgv_parapets.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ImageExtension = "png";
                    ImageName = "Parapets Image" + "-" + MissionOrderNo  + "-" + dtAdded;

                    initImageListeners(ImageName, ImageExtension, IMAGE_PICK_CAMERA_PARAPETS, IMAGE_PICK_GALLERY_PARAPETS, imgv_parapets);
                }
            });

            imgv_cladding.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ImageExtension = "png";
                    ImageName = "Cladding Image" + "-" + MissionOrderNo  + "-" + dtAdded;

                    initImageListeners(ImageName, ImageExtension, IMAGE_PICK_CAMERA_CLADDING, IMAGE_PICK_GALLERY_CLADDING, imgv_cladding);
                }
            });

            imgv_ceiling.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ImageExtension = "png";
                    ImageName = "Ceiling Image" + "-" + MissionOrderNo  + "-" + dtAdded;

                    initImageListeners(ImageName, ImageExtension, IMAGE_PICK_CAMERA_CEILING, IMAGE_PICK_GALLERY_CEILING, imgv_ceiling);
                }
            });

            imgv_interior_walls.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ImageExtension = "png";
                    ImageName = "Interior Walls Image" + "-" + MissionOrderNo  + "-" + dtAdded;

                    initImageListeners(ImageName, ImageExtension, IMAGE_PICK_CAMERA_INTERIOR_WALLS, IMAGE_PICK_GALLERY_INTERIOR_WALLS, imgv_interior_walls);
                }
            });

            imgv_elevator.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ImageExtension = "png";
                    ImageName = "Elevator Image" + "-" + MissionOrderNo  + "-" + dtAdded;

                    initImageListeners(ImageName, ImageExtension, IMAGE_PICK_CAMERA_ELEVATOR, IMAGE_PICK_GALLERY_ELEVATOR, imgv_elevator);
                }
            });

            imgv_stair.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ImageExtension = "png";
                    ImageName = "Stair Image" + "-" + MissionOrderNo  + "-" + dtAdded;

                    initImageListeners(ImageName, ImageExtension, IMAGE_PICK_CAMERA_STAIR, IMAGE_PICK_GALLERY_STAIR, imgv_stair);
                }
            });

            imgv_electric.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ImageExtension = "png";
                    ImageName = "Electric Image" + "-" + MissionOrderNo + "-" + dtAdded;

                    initImageListeners(ImageName, ImageExtension, IMAGE_PICK_CAMERA_ELECTRIC, IMAGE_PICK_GALLERY_ELECTRIC, imgv_electric);
                }
            });

            imgv_slope.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ImageExtension = "png";
                    ImageName = "Slope Image" + "-" + MissionOrderNo + "-" + dtAdded;

                    initImageListeners(ImageName, ImageExtension, IMAGE_PICK_CAMERA_SLOPE, IMAGE_PICK_GALLERY_SLOPE, imgv_slope);
                }
            });

            imgv_ground.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ImageExtension = "png";
                    ImageName = "Ground Image" + "-" + MissionOrderNo  + "-" + dtAdded;

                    initImageListeners(ImageName, ImageExtension, IMAGE_PICK_CAMERA_GROUND, IMAGE_PICK_GALLERY_GROUND, imgv_ground);
                }
            });

            imgv_PlaceCard.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try
                    {
                        ImageExtension = "png";
                        ImageName = "Placard Image" + "-" + MissionOrderNo  + "-" + dtAdded;

                        initImageListeners(ImageName, ImageExtension, IMAGE_PICK_CAMERA_PLACARD, IMAGE_PICK_GALLERY_PLACARD, imgv_PlaceCard);
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
            });

            imgv_bldgSignature.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try
                    {
                        Cursor cursor = RepositoryInspectorSignature.realAllData(getContext(), "DESA", MissionOrderID);

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

                                    @SuppressLint("InflateParams")
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
                                                    UserAccount.UserAccountID, MissionOrderID, "DESA");

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
                    }
                }
            });

           /* tv_set_date.setOnClickListener(new View.OnClickListener()
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
                    String ScreenerID    = String.valueOf(UserAccount.employeeID);

                    if (imgv_bldgSignature.getDrawable() == null)
                    {
                        Toast.makeText(getActivity(), "Signature first!", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Cursor cursor = RepositoryDESA.realAllData(getContext(), ScreenerID, MissionOrderID);

                        if (cursor.getCount()!=0)
                        {
                            initSaveDESA("Update");
                        }
                        else
                        {
                            initSaveDESA("Save");
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

            getLocationPermission();
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
            String FILE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + PdfFolderName + "/" + MissionOrderNo + "_DESA.pdf";

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

            Intent intent = new Intent(getActivity(), PreviewReportDESAActivity.class);
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
        PdfPCell cells, pCell, sCells;
        Paragraph paragraph;

        // Font Style for Document
        Font small_font                = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL);
        Font smallest                  = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
        Font smallestBold              = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);

        Font smallNormal               = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
        Font smallNormal2               = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL | Font.UNDERLINE);
        Font smallBold                 = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

        Font header1                   = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);
        Font header2                   = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
        Font header3                   = new Font(Font.FontFamily.HELVETICA, 11, Font.UNDERLINE);
        Font header4                   = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD | Font.ITALIC);

        Font catFont                   = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
        Font catFont2                  = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font catFont3                  = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD);
        Font catFont4                  = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Font catFont5                  = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD | Font.ITALIC);
        Font catFont6                  = new Font(Font.FontFamily.HELVETICA, 16, Font.NORMAL);
        Font catFont7                  = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);

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
            republicLogo1.scaleAbsolute(65f, 15f);

            Image republicLogo2 = Image.getInstance(stream.toByteArray());
            republicLogo2.setAlignment(Image.ALIGN_LEFT);
            republicLogo2.scaleAbsolute(65f, 5f);

            Image republicLogo3 = Image.getInstance(stream.toByteArray());
            republicLogo3.setAlignment(Image.ALIGN_CENTER);
            republicLogo3.scaleAbsolute(65f, 5f);

            Image republicLogo4 = Image.getInstance(stream2.toByteArray());
            republicLogo4.setAlignment(Image.ALIGN_CENTER);
            //republicLogo4.scaleAbsolute(180, 250);
            republicLogo4.scaleToFit(300, 140);

            cells = new PdfPCell();
            cells.addElement(republicLogo1);
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setColspan(1);
            cells.setPaddingTop(10);
            table.addCell(cells);

            Phrase headerPhrase = new Phrase(new Chunk("Republic of the Philippines", header1));
            headerPhrase.add(new Phrase("\n(AGENCY)", header1));
            headerPhrase.add(new Phrase("\n(Location)", header1));
            headerPhrase.add(new Phrase("\nDetailed Evaluation and Safety Assessment (DESA)", header2));

            cells = new PdfPCell(headerPhrase);
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setColspan(1);
            table.addCell(cells);

            cells = new PdfPCell();
            cells.addElement(republicLogo1);
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_LEFT);
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setColspan(1);
            cells.setPaddingTop(10);
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
            table2.addCell(pCell);
            cells.addElement(table2);

            cells.setColspan(1);
            cells.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
            cells.setPaddingLeft(20);
            table.addCell(cells);

           /* sPhrase = new Phrase();
            sPhrase.add(new Phrase("Inspected By: " , header1));
            sPhrase.add(new Phrase(UserAccount.CompleteName, header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setColspan(1);
            table.addCell(cells);*/


            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Inspector ID: " , header1));
            sPhrase.add(new Phrase(UserAccount.UserAccountID, header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setColspan(1);
            table.addCell(cells);


            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Affiliation " , header1));
            sPhrase.add(new Phrase(edt_affiliation.getText().toString(), header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setColspan(2);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Date: " , header1));
            sPhrase.add(new Phrase(edt_set_date.getText().toString(), header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
            cells.setPaddingLeft(5);
            cells.setPaddingBottom(5);
            cells.setColspan(1);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Time: " , header1));
            sPhrase.add(new Phrase(edt_set_time.getText().toString(), header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.BOTTOM | Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingBottom(5);
            cells.setColspan(1);
            table.addCell(cells);

            //BUILDING INFORMATION
            cells = new PdfPCell(new Phrase("Building Information", catFont2));
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setColspan(2);
            table.addCell(cells);


            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Name: " , header1));
            sPhrase.add(new Phrase(edt_bldg_name.getText().toString(), header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setColspan(1);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Building Contact: " , header1));
            sPhrase.add(new Phrase(edt_bldg_contact.getText().toString(), header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setColspan(1);
            table.addCell(cells);

            table2 = new PdfPTable(100);
            table2.setWidthPercentage(100);

            sCells = new PdfPCell(new Phrase("Address: ", header1));
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
            cells.setColspan(1);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(""));
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(1);
            table.addCell(cells);



            cells = new PdfPCell(new Phrase("Storeys", catFont2));
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setColspan(2);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Above ground: " , header1));
            sPhrase.add(new Phrase(edt_NoOfStoreyAboveGround.getText().toString(), header1));

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
            sPhrase.add(new Phrase("Approx. footprint area: " , header1));
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

            //region OVER-ALL HAZARDS
            table = new PdfPTable(100);
            table.setWidthPercentage(100);

            cells = new PdfPCell(new Phrase("Over-all hazards", catFont2));
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(30);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);


            cells = new PdfPCell(new Phrase("Minor/None", catFont2));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Moderate", catFont2));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Severe", catFont2));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Comment", catFont2));
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(25);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);


            cells = new PdfPCell(new Phrase("Collapse or partial collapse", header1));
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(30);
            table.addCell(cells);

            if (rb_collapse_minor.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if (rb_collapse_moderate.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if (rb_collapse_severe.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);



            cells = new PdfPCell(new Phrase(edt_collapse_comment.getText().toString(), header1));
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(25);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);


            cells = new PdfPCell(new Phrase("Building or story leaning", header1));
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(30);
            table.addCell(cells);

            if (rb_bldg_minor.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if (rb_bldg_moderate.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if (rb_bldg_severe.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);


            cells = new PdfPCell(new Phrase(edt_bldg_comment.getText().toString(), header1));
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(25);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Others: "  + edt_overall_hazards_other.getText().toString(), header1));
            cells.setBorder(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setPaddingLeft(5);
            cells.setColspan(100);
            table.addCell(cells);
            //endregion

            //region STRUCTURAL HAZARDS
            cells = new PdfPCell(new Phrase("Structural hazards", catFont2));
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(30);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Minor/None", catFont2));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Moderate", catFont2));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Severe", catFont2));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Comment", catFont2));
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(25);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Foundation", header1));
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(30);
            table.addCell(cells);

            if(rb_foundation_minor.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if(rb_foundation_moderate.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if(rb_foundation_severe.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(edt_foundation_comment.getText().toString(), header1));
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(25);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            //ROOF
            cells = new PdfPCell(new Phrase("Roof. floor(vertical loads)", header1));
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(30);
            table.addCell(cells);

            if(rb_roof_minor.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if(rb_roof_moderate.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if(rb_roof_severe.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(edt_roof_comment.getText().toString(), header1));
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(25);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            //COLUMNS
            cells = new PdfPCell(new Phrase("Columns, pilasters, corbels ", header1));
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(30);
            table.addCell(cells);

            if(rb_columns_minor.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if(rb_columns_moderate.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if(rb_columns_severe.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(edt_columns_comment.getText().toString(), header1));
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(25);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            //DIAPHRAGMS
            cells = new PdfPCell(new Phrase("Diaphragms, horizontal bracing", header1));
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(30);
            table.addCell(cells);

            if(rb_diaphragms_minor.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if(rb_diaphragms_moderate.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if(rb_diaphragms_severe.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(edt_diaphragms_comment.getText().toString(), header1));
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(25);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            //WALLS
            cells = new PdfPCell(new Phrase("Walls, vertical bracing", header1));
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(30);
            table.addCell(cells);

            if(rb_walls_minor.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[     ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if(rb_walls_moderate.isChecked())
            {
                //d
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if(rb_walls_severe.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(edt_walls_comment.getText().toString(), header1));
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(25);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            //PRE-CAST
            cells = new PdfPCell(new Phrase("Pre-cast connections", header1));
            cells.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(10);
            cells.setPaddingLeft(5);
            cells.setColspan(30);
            table.addCell(cells);

            if(rb_precast_minor.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.BOTTOM);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(10);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if(rb_precast_moderate.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.BOTTOM);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(10);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if(rb_precast_severe.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.BOTTOM);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(10);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(edt_precast_comment.getText().toString(), header1));
            cells.setBorder(Rectangle.RIGHT | Rectangle.BOTTOM);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(10);
            cells.setPaddingLeft(5);
            cells.setColspan(25);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);
            //endregion

            //region NONSTRUCTURAL HAZARDS
            cells = new PdfPCell(new Phrase("Nonstructural hazards", catFont2));
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(30);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Minor/None", catFont2));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Moderate", catFont2));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Severe", catFont2));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Comment", catFont2));
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(25);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            //PARAPETS
            cells = new PdfPCell(new Phrase("Parapets, ornamentations", header1));
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(30);
            table.addCell(cells);

            if (rb_parapets_minor.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if (rb_parapets_moderate.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if (rb_parapets_severe.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(edt_parapets_comment.getText().toString(), header1));
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(25);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            //CLADDING
            cells = new PdfPCell(new Phrase("Cladding, glazing", header1));
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(30);
            table.addCell(cells);

            if (rb_cladding_minor.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if (rb_cladding_moderate.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if (rb_cladding_severe.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(edt_cladding_comment.getText().toString(), header1));
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(25);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            //CEILING
            cells = new PdfPCell(new Phrase("Ceiling, light fixtures", header1));
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(30);
            table.addCell(cells);

            if (rb_ceiling_minor.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if (rb_ceiling_moderate.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if (rb_ceiling_severe.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(edt_ceiling_comment.getText().toString(), header1));
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(25);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            //INTERIOR
            cells = new PdfPCell(new Phrase("Interior walls, partitions", header1));
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(30);
            table.addCell(cells);

            if (rb_interior_walls_minor.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if (rb_interior_walls_moderate.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if (rb_interior_walls_severe.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(edt_interior_walls_comment.getText().toString(), header1));
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(25);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            //ELEVATORS
            cells = new PdfPCell(new Phrase("Elevators", header1));
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(30);
            table.addCell(cells);

            if (rb_elevators_minor.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if (rb_elevators_moderate.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if (rb_elevators_severe.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(edt_elevators_comment.getText().toString(), header1));
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(25);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            //STAIRS
            cells = new PdfPCell(new Phrase("Stairs, exit", header1));
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(30);
            table.addCell(cells);

            if (rb_stairs_minor.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if (rb_stairs_moderate.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if (rb_stairs_severe.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(edt_stairs_comment.getText().toString(), header1));
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(25);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);


            //ELECTRIC
            cells = new PdfPCell(new Phrase("Electric, gas", header1));
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(30);
            table.addCell(cells);

            if (rb_electric_gas_minor.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if (rb_electric_gas_moderate.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if (rb_electric_gas_severe.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(edt_electric_gas_comment.getText().toString(), header1));
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(25);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            //OTHERS
            cells = new PdfPCell(new Phrase("Others: " + edt_nonstructural_hazards_other.getText().toString(), header1));
            cells.setBorder(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setPaddingLeft(5);
            cells.setColspan(100);
            table.addCell(cells);

            document.add(table);

            //endregion

            //region GEOTECHINCAL HAZARDS
            document.newPage();

            table = new PdfPTable(100);
            table.setWidthPercentage(100);

            cells = new PdfPCell(new Phrase("Geotechnical Hazards", catFont2));
            cells.setBorder(Rectangle.LEFT | Rectangle.TOP);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(30);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Minor/None", catFont2));
            cells.setBorder(Rectangle.TOP);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Moderate", catFont2));
            cells.setBorder(Rectangle.TOP);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Severe", catFont2));
            cells.setBorder(Rectangle.TOP);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Comment", catFont2));
            cells.setBorder(Rectangle.RIGHT | Rectangle.TOP);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(25);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            //SLOPE FAILURE
            cells = new PdfPCell(new Phrase("Slope failure, debris", header1));
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(30);
            table.addCell(cells);

            if (rb_slope_failure_minor.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if (rb_slope_failure_moderate.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if (rb_slope_failure_severe.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(edt_slope_failure_comment.getText().toString(), header1));
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(25);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            //GROUND MOVEMENT
            cells = new PdfPCell(new Phrase("Ground movement fissures ", header1));
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(30);
            table.addCell(cells);

            if (rb_ground_movement_minor.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if (rb_ground_movement_moderate.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            if (rb_ground_movement_severe.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(15);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(edt_ground_movement_comment.getText().toString(), header1));
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(25);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cells);

            //OTHERS
            cells = new PdfPCell(new Phrase("Others: " + edt_geotechnical_hazard_other.getText().toString(), header1));
            cells.setBorder(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setPaddingLeft(5);
            cells.setColspan(100);
            table.addCell(cells);
            //endregion

            //region ESTIMATED BUILDING DAMAGE
            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Estimated Building Damage: " , catFont2));

            if(!spnr_EstimatedBldgDamage.getSelectedItem().toString().equalsIgnoreCase("Select Estimate Damage"))
            {
                sPhrase.add(new Phrase(spnr_EstimatedBldgDamage.getSelectedItem().toString() , header1));
            }
            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(100);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Previous Posting: " , catFont2));
            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingTop(10);
            cells.setPaddingBottom(10);
            cells.setPaddingLeft(5);
            cells.setColspan(50);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Date: " , catFont2));
            sPhrase.add(new Phrase(edt_PreviousPosting_Date.getText().toString() , header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingTop(10);
            cells.setPaddingBottom(10);
            cells.setPaddingLeft(5);
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
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(7);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("INSPECTED (Green placard)", header1));
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
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
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(7);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("RESTRICTED (Yellow placard)", header1));
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
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
            cells.setBorder( Rectangle.LEFT);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setPaddingLeft(5);
            cells.setColspan(7);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("OFF LIMITS (Red placard)", header1));
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setPaddingLeft(5);
            cells.setColspan(93);
            table.addCell(cells);
            //endregion

            //CURRENT POSTING
            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Posting: " , catFont2));
            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(100);
            table.addCell(cells);

            if (rb_GreenPlacard.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(7);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("INSPECTED (Green placard)", header1));
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(93);
            table.addCell(cells);

            if (rb_YellowPlacard.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(7);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("RESTRICTED (Yellow placard)", header1));
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(93);
            table.addCell(cells);


            if (rb_RedPlacard.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setPaddingLeft(5);
            cells.setColspan(7);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("OFF LIMITS (Red placard)", header1));
            cells.setBorder(Rectangle.BOTTOM | Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setPaddingLeft(5);
            cells.setColspan(93);
            table.addCell(cells);
            //endregion



            //region FURTHER ACTIONS
            cells = new PdfPCell(new Phrase("Further Actions", catFont2));
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(100);
            table.addCell(cells);

            if (cb_bldgBarricade.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(7);
            table.addCell(cells);

            if (cb_bldgBarricade.isChecked())
            {
                cells = new PdfPCell(new Phrase("Barricades needed in the areas: " +  edt_bldgBarricadesComment.getText().toString(), header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("Barricades needed in the areas: ", header1));
            }
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(93);
            table.addCell(cells);

            if (cb_EngineeringEvaluation.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(7);
            table.addCell(cells);

            if (cb_EngineeringEvaluation.isChecked())
            {
                cells = new PdfPCell(new Phrase("Engineering Evaluation Recommended: "  + sp_EngineeringRecommendation.getSelectedItem().toString() , header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("Engineering Evaluation Recommended: " , header1));
            }

            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(93);
            table.addCell(cells);

            if (cb_bldgOtherRecommendation.isChecked())
            {
                cells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("[      ]", header1));
            }
            cells.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setPaddingLeft(5);
            cells.setColspan(7);
            table.addCell(cells);

            if (cb_bldgOtherRecommendation.isChecked())
            {
                cells = new PdfPCell(new Phrase("Other Recommendations: " + edt_bldgOtherRecommendation.getText().toString(), header1));
            }
            else
            {
                cells = new PdfPCell(new Phrase("Other Recommendations: ", header1));
            }
            cells.setBorder( Rectangle.BOTTOM | Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setPaddingLeft(5);
            cells.setColspan(93);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Comments: " , header1));
            sPhrase.add(new Phrase(edt_desa_comment.getText().toString() , header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(15);
            cells.setPaddingLeft(5);
            cells.setColspan(100);
            table.addCell(cells);

            document.add(table);
            //endregion


            //region OVER-ALL HAZARDS ATTACHMENT
            if (bmCollapse != null || bmBuildingStoryLeaning != null)
            {
                table = new PdfPTable(100);
                table.setWidthPercentage(100);

                cells = new PdfPCell(new Phrase("Over-all hazards", catFont7));
                cells.setBorder(Rectangle.NO_BORDER);
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setPaddingLeft(5);
                cells.setPaddingTop(40);
                cells.setColspan(100);
                table.addCell(cells);
            }

            int FirstAttachmentCount = 0;

            if (bmCollapse != null)
            {
                FirstAttachmentCount = FirstAttachmentCount + 1;

                cells = new PdfPCell();

                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                getResizedBitmap(bmCollapse, 1000).compress(Bitmap.CompressFormat.PNG, 100, stream1);

                Image AttachmentOutput = Image.getInstance(stream1.toByteArray());
                AttachmentOutput.setAlignment(Image.ALIGN_CENTER);
                AttachmentOutput.scaleToFit(300, 140);

                cells.addElement(AttachmentOutput);

                table2 = new PdfPTable(100);
                table2.setWidthPercentage(100);

                sCells = new PdfPCell(new Phrase("Collapse or partial collapse", catFont));
                sCells.setBorder(Rectangle.NO_BORDER);
                sCells.setPaddingTop(5);
                sCells.setColspan(100);
                sCells.setHorizontalAlignment(Element.ALIGN_CENTER);
                table2.addCell(sCells);

                cells.addElement(table2);

                cells.setBorder(Rectangle.NO_BORDER);
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setPaddingLeft(5);
                cells.setPaddingTop(10);
                cells.setColspan(50);
                table.addCell(cells);
            }

            if (bmBuildingStoryLeaning != null)
            {
                FirstAttachmentCount = FirstAttachmentCount + 1;

                cells = new PdfPCell();

                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                getResizedBitmap(bmBuildingStoryLeaning, 1000).compress(Bitmap.CompressFormat.PNG, 100, stream1);

                Image AttachmentOutput = Image.getInstance(stream1.toByteArray());
                AttachmentOutput.setAlignment(Image.ALIGN_CENTER);
                AttachmentOutput.scaleToFit(300, 140);

                cells.addElement(AttachmentOutput);

                table2 = new PdfPTable(100);
                table2.setWidthPercentage(100);

                sCells = new PdfPCell(new Phrase("Building or story leaning", catFont));
                sCells.setBorder(Rectangle.NO_BORDER);
                sCells.setPaddingTop(5);
                sCells.setColspan(100);
                sCells.setHorizontalAlignment(Element.ALIGN_CENTER);
                table2.addCell(sCells);

                cells.addElement(table2);

                cells.setBorder(Rectangle.NO_BORDER);
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setPaddingLeft(5);
                cells.setPaddingTop(10);
                cells.setColspan(50);
                table.addCell(cells);
            }

            if (FirstAttachmentCount == 1)
            {
                cells = new PdfPCell(new Phrase(" "));
                cells.setBorder(Rectangle.NO_BORDER);
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setPaddingLeft(5);
                cells.setPaddingTop(15);
                cells.setColspan(50);
                table.addCell(cells);
            }

            if (bmCollapse != null || bmBuildingStoryLeaning != null)
            {
                document.add(table);
            }
            //endregion

            //region STRUCTURAL HAZARDS
            int SecondAttachmentCount = 0;

            if (bmFoundation != null || bmRoofFloor != null || bmColumns != null ||
                    bmDiaphragms != null || bmWalls != null || bmPrecast != null)
            {
                int countPicture = 0;

                if (bmFoundation != null || bmRoofFloor != null)
                {
                    countPicture = countPicture + 1;
                }
                if (bmColumns != null || bmDiaphragms != null)
                {
                    countPicture = countPicture + 1;
                }
                if (bmWalls != null || bmPrecast != null)
                {
                    countPicture = countPicture + 1;
                }

                if (FirstAttachmentCount != 0 && countPicture >= 2)
                {
                    document.newPage();
                }

                table = new PdfPTable(100);
                table.setWidthPercentage(100);

                cells = new PdfPCell(new Phrase("Structural hazards", catFont7));
                cells.setBorder(Rectangle.NO_BORDER);
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setPaddingLeft(5);
                cells.setPaddingTop(15);
                cells.setColspan(100);
                table.addCell(cells);
            }

            //1
            if (bmFoundation != null)
            {
                SecondAttachmentCount = SecondAttachmentCount + 1;

                cells = new PdfPCell();

                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                getResizedBitmap(bmFoundation, 1000).compress(Bitmap.CompressFormat.PNG, 100, stream1);

                Image AttachmentOutput = Image.getInstance(stream1.toByteArray());
                AttachmentOutput.setAlignment(Image.ALIGN_CENTER);
                AttachmentOutput.scaleToFit(300, 140);

                cells.addElement(AttachmentOutput);

                table2 = new PdfPTable(100);
                table2.setWidthPercentage(100);

                sCells = new PdfPCell(new Phrase("Foundation", catFont));
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
            if (bmRoofFloor != null)
            {
                SecondAttachmentCount = SecondAttachmentCount + 1;

                cells = new PdfPCell();

                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                getResizedBitmap(bmRoofFloor, 1000).compress(Bitmap.CompressFormat.PNG, 100, stream1);

                Image AttachmentOutput = Image.getInstance(stream1.toByteArray());
                AttachmentOutput.setAlignment(Image.ALIGN_CENTER);
                AttachmentOutput.scaleToFit(300, 140);

                cells.addElement(AttachmentOutput);

                table2 = new PdfPTable(100);
                table2.setWidthPercentage(100);

                sCells = new PdfPCell(new Phrase("Roof,floor(vertical loads)", catFont));
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

            //3
            if (bmColumns != null)
            {
                SecondAttachmentCount = SecondAttachmentCount + 1;

                cells = new PdfPCell();

                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                getResizedBitmap(bmColumns, 1000).compress(Bitmap.CompressFormat.PNG, 100, stream1);

                Image AttachmentOutput = Image.getInstance(stream1.toByteArray());
                AttachmentOutput.setAlignment(Image.ALIGN_CENTER);
                AttachmentOutput.scaleToFit(300, 140);

                cells.addElement(AttachmentOutput);

                table2 = new PdfPTable(100);
                table2.setWidthPercentage(100);

                sCells = new PdfPCell(new Phrase("Columns, plasters, corbels", catFont));
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

            if (bmDiaphragms != null)
            {
                SecondAttachmentCount = SecondAttachmentCount + 1;

                cells = new PdfPCell();

                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                getResizedBitmap(bmDiaphragms, 1000).compress(Bitmap.CompressFormat.PNG, 100, stream1);

                Image AttachmentOutput = Image.getInstance(stream1.toByteArray());
                AttachmentOutput.setAlignment(Image.ALIGN_CENTER);
                AttachmentOutput.scaleToFit(300, 140);

                cells.addElement(AttachmentOutput);

                table2 = new PdfPTable(100);
                table2.setWidthPercentage(100);

                sCells = new PdfPCell(new Phrase("Diaphragms, horizontal bracing", catFont));
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

            //4
            if (bmWalls != null)
            {
                SecondAttachmentCount = SecondAttachmentCount + 1;

                cells = new PdfPCell();

                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                getResizedBitmap(bmWalls, 1000).compress(Bitmap.CompressFormat.PNG, 100, stream1);

                Image AttachmentOutput = Image.getInstance(stream1.toByteArray());
                AttachmentOutput.setAlignment(Image.ALIGN_CENTER);
                AttachmentOutput.scaleToFit(300, 140);

                cells.addElement(AttachmentOutput);

                table2 = new PdfPTable(100);
                table2.setWidthPercentage(100);

                sCells = new PdfPCell(new Phrase("Walls, vertical bracing ", catFont));
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

            if (bmPrecast != null)
            {
                SecondAttachmentCount = SecondAttachmentCount + 1;

                cells = new PdfPCell();

                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                getResizedBitmap(bmPrecast, 1000).compress(Bitmap.CompressFormat.PNG, 100, stream1);

                Image AttachmentOutput = Image.getInstance(stream1.toByteArray());
                AttachmentOutput.setAlignment(Image.ALIGN_CENTER);
                AttachmentOutput.scaleToFit(300, 140);

                cells.addElement(AttachmentOutput);

                table2 = new PdfPTable(100);
                table2.setWidthPercentage(100);

                sCells = new PdfPCell(new Phrase("Diaphragms, horizontal bracing", catFont));
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

            if (SecondAttachmentCount == 1 || SecondAttachmentCount == 3 || SecondAttachmentCount == 5)
            {
                cells = new PdfPCell(new Phrase(" "));
                cells.setBorder(Rectangle.NO_BORDER);
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setPaddingLeft(5);
                cells.setPaddingTop(15);
                cells.setColspan(50);
                table.addCell(cells);
            }

            if (bmFoundation != null || bmRoofFloor != null || bmColumns != null ||
                    bmDiaphragms != null || bmWalls != null || bmPrecast != null)
            {
                document.add(table);
            }
            //endregion

            //region NONSTRUCTURAL HAZARDS
            if (bmParapets != null || bmCladding != null || bmCeiling != null || bmInteriorWalls != null ||
                    bmElevators != null || bmStairs!= null || bmElectric != null)
            {
                if (FirstAttachmentCount != 0 && (SecondAttachmentCount == 5 || SecondAttachmentCount == 6))
                {
                    document.newPage();
                }
                else if (FirstAttachmentCount == 0 && (SecondAttachmentCount == 3|| SecondAttachmentCount == 4))
                {
                    document.newPage();
                }
                else
                {
                    if (FirstAttachmentCount != 0)
                    {
                        document.newPage();
                    }
                }

                table = new PdfPTable(100);
                table.setWidthPercentage(100);

                cells = new PdfPCell(new Phrase("Nonstructural hazards", catFont7));
                cells.setBorder(Rectangle.NO_BORDER);
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setPaddingLeft(5);
                cells.setPaddingTop(15);
                cells.setColspan(100);
                table.addCell(cells);
            }

            int ThirdAttachmentCount = 0;

            if (bmParapets != null)
            {
                ThirdAttachmentCount = ThirdAttachmentCount + 1;

                cells = new PdfPCell();
                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                getResizedBitmap(bmParapets, 1000).compress(Bitmap.CompressFormat.PNG, 100, stream1);

                Image AttachmentOutput = Image.getInstance(stream1.toByteArray());
                AttachmentOutput.setAlignment(Image.ALIGN_CENTER);
                AttachmentOutput.scaleToFit(300, 140);

                cells.addElement(AttachmentOutput);

                table2 = new PdfPTable(100);
                table2.setWidthPercentage(100);

                sCells = new PdfPCell(new Phrase("Parapets ornamentations", catFont));
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

            if (bmCladding != null)
            {
                ThirdAttachmentCount = ThirdAttachmentCount + 1;

                cells = new PdfPCell();
                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                getResizedBitmap(bmCladding, 1000).compress(Bitmap.CompressFormat.PNG, 100, stream1);

                Image AttachmentOutput = Image.getInstance(stream1.toByteArray());
                AttachmentOutput.setAlignment(Image.ALIGN_CENTER);
                AttachmentOutput.scaleToFit(300, 140);

                cells.addElement(AttachmentOutput);

                table2 = new PdfPTable(100);
                table2.setWidthPercentage(100);

                sCells = new PdfPCell(new Phrase("Cladding, glazing", catFont));
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

            if (bmCeiling != null)
            {
                ThirdAttachmentCount = ThirdAttachmentCount + 1;

                cells = new PdfPCell();
                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                getResizedBitmap(bmCeiling, 1000).compress(Bitmap.CompressFormat.PNG, 100, stream1);

                Image AttachmentOutput = Image.getInstance(stream1.toByteArray());
                AttachmentOutput.setAlignment(Image.ALIGN_CENTER);
                AttachmentOutput.scaleToFit(300, 140);

                cells.addElement(AttachmentOutput);

                table2 = new PdfPTable(100);
                table2.setWidthPercentage(100);

                sCells = new PdfPCell(new Phrase("Ceiling, light fixtures", catFont));
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

            if (bmInteriorWalls != null)
            {
                Log.e(TAG, "bmInteriorWalls exist");

                ThirdAttachmentCount = ThirdAttachmentCount + 1;

                cells = new PdfPCell();

                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                getResizedBitmap(bmInteriorWalls, 1000).compress(Bitmap.CompressFormat.PNG, 100, stream1);

                Image AttachmentOutput = Image.getInstance(stream1.toByteArray());
                AttachmentOutput.setAlignment(Image.ALIGN_CENTER);
                AttachmentOutput.scaleToFit(300, 140);

                cells.addElement(AttachmentOutput);

                table2 = new PdfPTable(100);
                table2.setWidthPercentage(100);

                sCells = new PdfPCell(new Phrase("Interior walls, partitions", catFont));
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

            if (bmElevators != null)
            {
                ThirdAttachmentCount = ThirdAttachmentCount + 1;

                cells = new PdfPCell();
                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                getResizedBitmap(bmElevators, 1000).compress(Bitmap.CompressFormat.PNG, 100, stream1);

                Image AttachmentOutput = Image.getInstance(stream1.toByteArray());
                AttachmentOutput.setAlignment(Image.ALIGN_CENTER);
                AttachmentOutput.scaleToFit(300, 140);

                cells.addElement(AttachmentOutput);

                table2 = new PdfPTable(100);
                table2.setWidthPercentage(100);

                sCells = new PdfPCell(new Phrase("Elevators", catFont));
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

            if (bmStairs != null)
            {
                ThirdAttachmentCount = ThirdAttachmentCount + 1;

                cells = new PdfPCell();
                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                getResizedBitmap(bmStairs, 1000).compress(Bitmap.CompressFormat.PNG, 100, stream1);

                Image AttachmentOutput = Image.getInstance(stream1.toByteArray());
                AttachmentOutput.setAlignment(Image.ALIGN_CENTER);
                AttachmentOutput.scaleToFit(300, 140);

                cells.addElement(AttachmentOutput);

                table2 = new PdfPTable(100);
                table2.setWidthPercentage(100);

                sCells = new PdfPCell(new Phrase("Stairs, exit", catFont));
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

            if (bmElectric != null)
            {
                ThirdAttachmentCount = ThirdAttachmentCount + 1;

                cells = new PdfPCell();
                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                getResizedBitmap(bmElectric, 1000).compress(Bitmap.CompressFormat.PNG, 100, stream1);

                Image AttachmentOutput = Image.getInstance(stream1.toByteArray());
                AttachmentOutput.setAlignment(Image.ALIGN_CENTER);
                AttachmentOutput.scaleToFit(300, 140);

                cells.addElement(AttachmentOutput);

                table2 = new PdfPTable(100);
                table2.setWidthPercentage(100);

                sCells = new PdfPCell(new Phrase("Electric, gas", catFont));
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

            if(ThirdAttachmentCount == 1 || ThirdAttachmentCount == 3 || ThirdAttachmentCount == 5 || ThirdAttachmentCount == 7)
            {
                cells = new PdfPCell(new Phrase(" ", header2));
                cells.setBorder(Rectangle.NO_BORDER);
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setPaddingLeft(5);
                cells.setPaddingTop(5);
                cells.setColspan(50);
                table.addCell(cells);
            }

            if (bmParapets != null || bmCladding != null || bmCeiling != null || bmInteriorWalls != null ||
                    bmElevators != null || bmStairs!= null || bmElectric != null)
            {
                document.add(table);
            }
            //endregion

            //region GEOTECHNICAL
            if (bmSlope != null || bmGroundMovement != null)
            {
                if (ThirdAttachmentCount >= 5)
                {
                    document.newPage();
                }
                else if (FirstAttachmentCount == 0  && (SecondAttachmentCount == 1 || SecondAttachmentCount == 2) &&
                        (ThirdAttachmentCount == 1 || ThirdAttachmentCount == 2))
                {
                    document.newPage();
                }
                else if (FirstAttachmentCount == 0 && SecondAttachmentCount >=3 && ThirdAttachmentCount == 0)
                {
                    document.newPage();
                }
                else if (FirstAttachmentCount != 0 && SecondAttachmentCount >=3 && ThirdAttachmentCount == 0)
                {
                    document.newPage();
                }
                else if (FirstAttachmentCount != 0 && SecondAttachmentCount == 0 && ThirdAttachmentCount >= 2)
                {
                    document.newPage();
                }
                else if (FirstAttachmentCount != 0 && (SecondAttachmentCount == 3 || SecondAttachmentCount == 4) &&
                        (ThirdAttachmentCount == 1 || ThirdAttachmentCount == 2))
                {
                    document.newPage();
                }


                table = new PdfPTable(100);
                table.setWidthPercentage(100);

                cells = new PdfPCell(new Phrase("Geotechnical", catFont4));
                cells.setBorder(Rectangle.NO_BORDER);
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setPaddingLeft(5);
                cells.setPaddingTop(15);
                cells.setColspan(100);
                table.addCell(cells);
            }

            int FourthAttachmentCount = 0;

            if (bmSlope != null)
            {
                FourthAttachmentCount  = FourthAttachmentCount + 1;

                cells = new PdfPCell();

                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                getResizedBitmap(bmSlope, 1000).compress(Bitmap.CompressFormat.PNG, 100, stream1);

                Image AttachmentOutput = Image.getInstance(stream1.toByteArray());
                AttachmentOutput.setAlignment(Image.ALIGN_CENTER);
                AttachmentOutput.scaleToFit(300, 140);

                cells.addElement(AttachmentOutput);

                table2 = new PdfPTable(100);
                table2.setWidthPercentage(100);

                sCells = new PdfPCell(new Phrase("Slope failure, debris", catFont));
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

            if (bmGroundMovement != null)
            {
                FourthAttachmentCount  = FourthAttachmentCount + 1;

                cells = new PdfPCell();

                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                getResizedBitmap(bmGroundMovement, 1000).compress(Bitmap.CompressFormat.PNG, 100, stream1);

                Image AttachmentOutput = Image.getInstance(stream1.toByteArray());
                AttachmentOutput.setAlignment(Image.ALIGN_CENTER);
                AttachmentOutput.scaleToFit(300, 140);

                cells.addElement(AttachmentOutput);

                table2 = new PdfPTable(100);
                table2.setWidthPercentage(100);

                sCells = new PdfPCell(new Phrase("Ground movement fissures", catFont));
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

            if(FourthAttachmentCount == 1)
            {
                cells = new PdfPCell(new Phrase(" ", header2));
                cells.setBorder(Rectangle.NO_BORDER);
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setPaddingLeft(5);
                cells.setPaddingTop(5);
                cells.setColspan(50);
                table.addCell(cells);
            }

            if (bmSlope != null || bmGroundMovement != null)
            {
                document.add(table);
            }
            //endregion

            //region POSTING
            if (bmPosting != null)
            {
                if (FirstAttachmentCount != 0 && SecondAttachmentCount !=0 &&
                     (ThirdAttachmentCount ==3 || ThirdAttachmentCount == 4) && FourthAttachmentCount != 0)
                {
                    document.newPage();
                }

                table = new PdfPTable(100);
                table.setWidthPercentage(100);

                cells = new PdfPCell(new Phrase("Posting", catFont2));
                cells.setBorder(Rectangle.NO_BORDER);
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setPaddingLeft(5);
                cells.setPaddingTop(10);
                cells.setColspan(100);
                table.addCell(cells);

                cells = new PdfPCell(new Phrase("INSPECTED(Green placard), RESTRICTED(Yellow placard), OFF LIMITS(Red placard)", header2));
                cells.setBorder(Rectangle.NO_BORDER);
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setPaddingLeft(5);
                cells.setPaddingTop(5);
                cells.setColspan(100);
                table.addCell(cells);

                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                getResizedBitmap(bmPosting, 1000).compress(Bitmap.CompressFormat.PNG, 100, stream1);

                Image AttachmentOutput = Image.getInstance(stream1.toByteArray());
                AttachmentOutput.setAlignment(Image.ALIGN_CENTER);
                AttachmentOutput.scaleToFit(300, 140);

                cells = new PdfPCell();
                cells.addElement(AttachmentOutput);
                cells.setBorder(Rectangle.NO_BORDER);
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setPaddingLeft(5);
                cells.setColspan(100);
                table.addCell(cells);

                document.add(table);
            }
            //endregion

            //region DIGITAL SIGNATURE
            table = new PdfPTable(100);
            table.setWidthPercentage(100);

            cells = new PdfPCell(new Phrase(" ", header2));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setColspan(100);
            cells.setPaddingTop(20);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(" ", header2));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setColspan(70);
            cells.setPaddingTop(20);
            table.addCell(cells);

            table2 = new PdfPTable(100);
            table2.setWidthPercentage(100);

            sCells = new PdfPCell();

            if (bmInspectorSignature != null)
            {
                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                getResizedBitmap(bmInspectorSignature, 1000).compress(Bitmap.CompressFormat.PNG, 100, stream1);

                Image AttachmentOutput = Image.getInstance(stream1.toByteArray());
                AttachmentOutput.setAlignment(Image.ALIGN_CENTER);
                AttachmentOutput.scaleToFit(300, 80);

                sCells.addElement(AttachmentOutput);
            }
            else
            {
                sCells.addElement(new Phrase(" \n "));
            }

            sCells.addElement(new Phrase(edt_inspected_by.getText().toString().toUpperCase(), header1));
            sCells.setBorder(Rectangle.BOTTOM);
            sCells.setColspan(100);
            sCells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table2.addCell(sCells);

            sCells = new PdfPCell(new Phrase("Building Inspector", header1));
            sCells.setBorder(Rectangle.TOP);
            sCells.setColspan(100);
            sCells.setHorizontalAlignment(Element.ALIGN_CENTER);
            table2.addCell(sCells);

            cells = new PdfPCell(table2);
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(20);
            cells.setColspan(25);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(" ", header2));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(20);
            cells.setColspan(5);
            table.addCell(cells);

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


    private void initRetrieveDESAData()
    {
        try
        {
            String ScreenerID    = String.valueOf(UserAccount.employeeID);

            Cursor cursor = RepositoryDESA.realAllData(getContext(), ScreenerID, MissionOrderID);

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

                    //Comments
                    String CollapseComment                = cursor.getString(cursor.getColumnIndex("CollapseComment"));
                    String BuildingStoryLeaningComment    = cursor.getString(cursor.getColumnIndex("BuildingStoryLeaningComment"));
                    String FoundationComment              = cursor.getString(cursor.getColumnIndex("FoundationComment"));
                    String RoofFloorVLComment             = cursor.getString(cursor.getColumnIndex("RoofFloorVLComment"));
                    String CPCComment                     = cursor.getString(cursor.getColumnIndex("CPCComment"));
                    String DiaphragmsHBComment            = cursor.getString(cursor.getColumnIndex("DiaphragmsHBComment"));
                    String WallsVBComment                 = cursor.getString(cursor.getColumnIndex("WallsVBComment"));
                    String PrecastConnectionsComment      = cursor.getString(cursor.getColumnIndex("PrecastConnectionsComment"));
                    String ParapetsOrnamentationComment   = cursor.getString(cursor.getColumnIndex("ParapetsOrnamentationComment"));
                    String CladdingGlazingComment         = cursor.getString(cursor.getColumnIndex("CladdingGlazingComment"));
                    String CeilingLightFixturesComment    = cursor.getString(cursor.getColumnIndex("CeilingLightFixturesComment"));
                    String InteriorWallsPartitionsComment = cursor.getString(cursor.getColumnIndex("InteriorWallsPartitionsComment"));
                    String ElevatorsComment               = cursor.getString(cursor.getColumnIndex("ElevatorsComment"));
                    String StairsExitComment              = cursor.getString(cursor.getColumnIndex("StairsExitComment"));
                    String ElectricGasComment             = cursor.getString(cursor.getColumnIndex("ElectricGasComment"));
                    String SlopeFailureDebrisComment      = cursor.getString(cursor.getColumnIndex("SlopeFailureDebrisComment"));
                    String GroundMovementFissuresComment  = cursor.getString(cursor.getColumnIndex("GroundMovementFissuresComment"));

                    //Option Type
                    String CollapseType                   = cursor.getString(cursor.getColumnIndex("CollapseType"));
                    String BuildingStoryLeaningType       = cursor.getString(cursor.getColumnIndex("BuildingStoryLeaningType"));
                    String OverAllHazardsOtherType        = cursor.getString(cursor.getColumnIndex("OverAllHazardsOtherType"));
                    String FoundationType                 = cursor.getString(cursor.getColumnIndex("FoundationType"));
                    String RoofFloorVLType                = cursor.getString(cursor.getColumnIndex("RoofFloorVLType"));
                    String CPCType                        = cursor.getString(cursor.getColumnIndex("CPCType"));
                    String DiaphragmsHBType               = cursor.getString(cursor.getColumnIndex("DiaphragmsHBType"));
                    String WallsVBType                    = cursor.getString(cursor.getColumnIndex("WallsVBType"));
                    String PrecastConnectionsType         = cursor.getString(cursor.getColumnIndex("PrecastConnectionsType"));
                    String ParapetsOrnamentationType      = cursor.getString(cursor.getColumnIndex("ParapetsOrnamentationType"));
                    String CladdingGlazingType            = cursor.getString(cursor.getColumnIndex("CladdingGlazingType"));
                    String CeilingLightFixturesType       = cursor.getString(cursor.getColumnIndex("CeilingLightFixturesType"));
                    String InteriorWallsPartitionsType    = cursor.getString(cursor.getColumnIndex("InteriorWallsPartitionsType"));
                    String ElevatorsType                  = cursor.getString(cursor.getColumnIndex("ElevatorsType"));
                    String StairsExitType                 = cursor.getString(cursor.getColumnIndex("StairsExitType"));
                    String ElectricGasType                = cursor.getString(cursor.getColumnIndex("ElectricGasType"));
                    String NonstructuralHazardOtherType   = cursor.getString(cursor.getColumnIndex("NonstructuralHazardOtherType"));
                    String SlopeFailureDebrisType         = cursor.getString(cursor.getColumnIndex("SlopeFailureDebrisType"));
                    String GroundMovementFissuresType     = cursor.getString(cursor.getColumnIndex("GroundMovementFissuresType"));
                    String GeotechnicalHazardOther        = cursor.getString(cursor.getColumnIndex("GeotechnicalHazardOther"));

                    String EstimatedBuildingDamage        = cursor.getString(cursor.getColumnIndex("EstimatedBuildingDamage"));
                    String PreviousPostingEstimatedDamage = cursor.getString(cursor.getColumnIndex("PreviousPostingEstimatedDamage"));

                    String EngineeringEvaluationRecommendedType = cursor.getString(cursor.getColumnIndex("EngineeringEvaluationRecommendedType"));
                    String PreviousPostingDate            = cursor.getString(cursor.getColumnIndex("PreviousPostingDate"));
                    String ColorPlacard                   = cursor.getString(cursor.getColumnIndex("ColorPlacard"));
                    String FurtherActionsBarricades       = cursor.getString(cursor.getColumnIndex("FurtherActionsBarricades"));
                    String FurtherActionsEngineeringEvaluationRecommended = cursor.getString(cursor.getColumnIndex("FurtherActionsEngineeringEvaluationRecommended"));
                    String FurtherActionsOtherRecommendation  = cursor.getString(cursor.getColumnIndex("FurtherActionsOtherRecommendation"));
                    String BarricadesComment              = cursor.getString(cursor.getColumnIndex("BarricadesComment"));
                    String RecommendationsType            = cursor.getString(cursor.getColumnIndex("RecommendationsType"));
                    String Comments                       = cursor.getString(cursor.getColumnIndex("Comments"));
                    String InspectedBy                    = cursor.getString(cursor.getColumnIndex("InspectedBy"));


                    /* DETAILED EVALUATION AND SAFETY ASSESSMENT */
                    edt_affiliation.setText(Affiliation);
                    edt_set_date.setText(SetDate);
                    edt_set_time.setText(SetTime);

                    //Building Description
                    edt_bldg_name.setText(BuildingName);
                    edt_bldg_address.setText(BuildingAddress);
                    edt_bldg_contact.setText(BuildingContact);
                    edt_NoOfStoreyAboveGround.setText(NoOfStoreyAboveGround);
                    edt_NoOfStoreyBelowGround.setText(NoOfStoreyBelowGround);

                    initSetSpinnerValue(TypeOfConstruction, spnr_TypeOfConstruction);
                    initSetSpinnerValue(PrimaryOccupancy, spnr_PrimaryOccupancy);

                    edt_Approx.setText(ApproxFootPrintAreaSM);
                    edt_NoOfResidentialUnits.setText(NoOfResidentialUnits);
                    edt_NoOfCommercialUnits.setText(NoOfCommercialUnits);


                    //Comments
                    edt_collapse_comment.setText(CollapseComment);
                    edt_bldg_comment.setText(BuildingStoryLeaningComment);
                    edt_foundation_comment.setText(FoundationComment);
                    edt_roof_comment.setText(RoofFloorVLComment);
                    edt_columns_comment.setText(CPCComment);
                    edt_diaphragms_comment.setText(DiaphragmsHBComment);
                    edt_walls_comment.setText(WallsVBComment);
                    edt_precast_comment.setText(PrecastConnectionsComment);
                    edt_parapets_comment.setText(ParapetsOrnamentationComment);
                    edt_cladding_comment.setText(CladdingGlazingComment);
                    edt_ceiling_comment.setText(CeilingLightFixturesComment);
                    edt_interior_walls_comment.setText(InteriorWallsPartitionsComment);
                    edt_elevators_comment.setText(ElevatorsComment);
                    edt_stairs_comment.setText(StairsExitComment);
                    edt_electric_gas_comment.setText(ElectricGasComment);
                    edt_slope_failure_comment.setText(SlopeFailureDebrisComment);
                    edt_ground_movement_comment.setText(GroundMovementFissuresComment);

                    //Collapse
                    switch (CollapseType)
                    {
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

                    //Building or Story Leaning
                    switch (BuildingStoryLeaningType)
                    {
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

                    edt_overall_hazards_other.setText(OverAllHazardsOtherType);

                    //Foundation
                    switch (FoundationType)
                    {
                        case "Minor":
                            rb_foundation_minor.setChecked(true);
                            break;
                        case "Moderate":
                            rb_roof_moderate.setChecked(true);
                            break;
                        case "Severe":
                            rb_foundation_severe.setChecked(true);
                            break;
                    }

                    //Roof
                    switch (RoofFloorVLType)
                    {
                        case "Minor":
                            rb_roof_minor.setChecked(true);
                            break;
                        case "Moderate":
                            rb_roof_moderate.setChecked(true);
                            break;
                        case "Severe":
                            rb_roof_severe.setChecked(true);
                            break;
                    }

                    //Columns
                    switch (CPCType)
                    {
                        case "Minor":
                            rb_columns_minor.setChecked(true);
                            break;
                        case "Moderate":
                            rb_columns_moderate.setChecked(true);
                            break;
                        case "Severe":
                            rb_columns_severe.setChecked(true);
                            break;
                    }

                    //Diaphragms
                    switch (DiaphragmsHBType)
                    {
                        case "Minor":
                            rb_diaphragms_minor.setChecked(true);
                            break;
                        case "Moderate":
                            rb_diaphragms_moderate.setChecked(true);
                            break;
                        case "Severe":
                            rb_diaphragms_severe.setChecked(true);
                            break;
                    }

                    //Walls, Vertical Bracing
                    switch (WallsVBType)
                    {
                        case "Minor":
                            rb_walls_minor.setChecked(true);
                            break;
                        case "Moderate":
                            rb_walls_moderate.setChecked(true);
                            break;
                        case "Severe":
                            rb_walls_severe.setChecked(true);
                            break;
                    }

                    //Pre-cast Connection
                    switch (PrecastConnectionsType)
                    {
                        case "Minor":
                            rb_precast_minor.setChecked(true);
                            break;
                        case "Moderate":
                            rb_precast_moderate.setChecked(true);
                            break;
                        case "Severe":
                            rb_precast_severe.setChecked(true);
                            break;
                    }


                    //Parapets, Ornamentation
                    switch (ParapetsOrnamentationType)
                    {
                        case "Minor":
                            rb_parapets_minor.setChecked(true);
                            break;
                        case "Moderate":
                            rb_parapets_moderate.setChecked(true);
                            break;
                        case "Severe":
                            rb_parapets_severe.setChecked(true);
                            break;
                    }

                    //Parapets, Ornamentation
                    switch (CladdingGlazingType)
                    {
                        case "Minor":
                            rb_cladding_minor.setChecked(true);
                            break;
                        case "Moderate":
                            rb_cladding_moderate.setChecked(true);
                            break;
                        case "Severe":
                            rb_cladding_severe.setChecked(true);
                            break;
                    }

                    //Parapets, Ornamentation
                    switch (CeilingLightFixturesType)
                    {
                        case "Minor":
                            rb_ceiling_minor.setChecked(true);
                            break;
                        case "Moderate":
                            rb_ceiling_moderate.setChecked(true);
                            break;
                        case "Severe":
                            rb_ceiling_severe.setChecked(true);
                            break;
                    }

                    //Interior Walls Partitions
                    switch (InteriorWallsPartitionsType)
                    {
                        case "Minor":
                            rb_interior_walls_minor.setChecked(true);
                            break;
                        case "Moderate":
                            rb_interior_walls_moderate.setChecked(true);
                            break;
                        case "Severe":
                            rb_interior_walls_severe.setChecked(true);
                            break;
                    }

                    //Interior Walls Partitions
                    switch (ElevatorsType)
                    {
                        case "Minor":
                            rb_elevators_minor.setChecked(true);
                            break;
                        case "Moderate":
                            rb_elevators_moderate.setChecked(true);
                            break;
                        case "Severe":
                            rb_elevators_severe.setChecked(true);
                            break;
                    }

                    //Interior Walls Partitions
                    switch (StairsExitType)
                    {
                        case "Minor":
                            rb_stairs_minor.setChecked(true);
                            break;
                        case "Moderate":
                            rb_stairs_moderate.setChecked(true);
                            break;
                        case "Severe":
                            rb_stairs_severe.setChecked(true);
                            break;
                    }

                    //Interior Walls Partitions
                    switch (ElectricGasType)
                    {
                        case "Minor":
                            rb_electric_gas_minor.setChecked(true);
                            break;
                        case "Moderate":
                            rb_electric_gas_moderate.setChecked(true);
                            break;
                        case "Severe":
                            rb_electric_gas_severe.setChecked(true);
                            break;
                    }

                    edt_nonstructural_hazards_other.setText(NonstructuralHazardOtherType);


                    //Slope Failure Debris
                    switch (SlopeFailureDebrisType)
                    {
                        case "Minor":
                            rb_slope_failure_minor.setChecked(true);
                            break;
                        case "Moderate":
                            rb_slope_failure_moderate.setChecked(true);
                            break;
                        case "Severe":
                            rb_slope_failure_severe.setChecked(true);
                            break;
                    }

                    //Ground Movement Fissures
                    switch (GroundMovementFissuresType)
                    {
                        case "Minor":
                            rb_ground_movement_minor.setChecked(true);
                            break;
                        case "Moderate":
                            rb_ground_movement_moderate.setChecked(true);
                            break;
                        case "Severe":
                            rb_ground_movement_severe.setChecked(true);
                            break;
                    }

                    edt_geotechnical_hazard_other.setText(GeotechnicalHazardOther);


                    //Estimated Building Damage
                    initSetSpinnerValue(EstimatedBuildingDamage, spnr_EstimatedBldgDamage);
                    //Posting
                    initSetSpinnerValue(PreviousPostingEstimatedDamage, spnr_PreviousPosting);
                    //Engineering Recommendation
                    initSetSpinnerValue(EngineeringEvaluationRecommendedType, sp_EngineeringRecommendation);


                    edt_PreviousPosting_Date.setText(PreviousPostingDate);

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
                        edt_bldgBarricadesComment.setEnabled(true);
                    }

                    cb_EngineeringEvaluation.setChecked(FurtherActionsEngineeringEvaluationRecommended.equalsIgnoreCase("1"));
                    if (FurtherActionsEngineeringEvaluationRecommended.equalsIgnoreCase("1"))
                    {
                        cb_EngineeringEvaluation.setEnabled(true);
                    }

                    cb_bldgOtherRecommendation.setChecked(FurtherActionsOtherRecommendation.equalsIgnoreCase("1"));
                    if (FurtherActionsOtherRecommendation.equalsIgnoreCase("1"))
                    {
                        edt_bldgOtherRecommendation.setEnabled(true);
                    }

                    edt_bldgBarricadesComment.setText(BarricadesComment);
                    edt_bldgOtherRecommendation.setText(RecommendationsType);
                    edt_desa_comment.setText(Comments);
                    edt_inspected_by.setText(InspectedBy);
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
            if(Value != null && !Value.equalsIgnoreCase(""))
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





    private void initSaveDESA(String option)
    {
        try
        {
            String ScreenerID    = String.valueOf(UserAccount.employeeID);
            String sInspectedBy  = edt_inspected_by.getText().toString();

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

            String CollapseComment       = edt_collapse_comment.getText().toString() != null ? edt_collapse_comment.getText().toString():"";
            String BldgComment           = edt_bldg_comment.getText().toString() != null ? edt_bldg_comment.getText().toString():"";
            String FoundationComment     = edt_foundation_comment.getText().toString() != null ? edt_foundation_comment.getText().toString():"";
            String RoofComment           = edt_roof_comment.getText().toString() != null ? edt_roof_comment.getText().toString():"";
            String ColumnsComment        = edt_columns_comment.getText().toString() != null ? edt_columns_comment.getText().toString():"";
            String DiaphragmsComment     = edt_diaphragms_comment.getText().toString() != null ? edt_diaphragms_comment.getText().toString():"";
            String WallsComment          = edt_walls_comment.getText().toString() != null ? edt_walls_comment.getText().toString():"";
            String PrecastComment        = edt_precast_comment.getText().toString() != null ? edt_precast_comment.getText().toString():"";
            String ParapetsComment       = edt_parapets_comment.getText().toString() != null ? edt_parapets_comment.getText().toString():"";
            String CladdingComment       = edt_cladding_comment.getText().toString() != null ? edt_cladding_comment.getText().toString():"";
            String CeilingComment        = edt_ceiling_comment.getText().toString() != null ? edt_ceiling_comment.getText().toString():"";
            String InteriorWallsComment  = edt_interior_walls_comment.getText().toString() != null ? edt_interior_walls_comment.getText().toString():"";
            String ElevatorsComment      = edt_elevators_comment.getText().toString() != null ? edt_elevators_comment.getText().toString():"";
            String StairsComment         = edt_stairs_comment.getText().toString() != null ? edt_stairs_comment.getText().toString():"";
            String ElectricComment       = edt_electric_gas_comment.getText().toString() != null ? edt_electric_gas_comment.getText().toString():"";
            String SlopeFailureComment   = edt_slope_failure_comment.getText().toString() != null ? edt_slope_failure_comment.getText().toString():"";
            String GroundMovementComment = edt_ground_movement_comment.getText().toString() != null ? edt_ground_movement_comment.getText().toString():"";

            String Comments              = edt_desa_comment.getText().toString() != null ? edt_desa_comment.getText().toString():"";
            String AllHazardsOtherType   = edt_overall_hazards_other.getText().toString() != null ? edt_overall_hazards_other.getText().toString():"";
            String EstimatedBldgDamage   = spnr_EstimatedBldgDamage.getSelectedItem().toString() != null ? spnr_EstimatedBldgDamage.getSelectedItem().toString():"";
            String PreviousPostingEstimatedDamage = spnr_PreviousPosting.getSelectedItem().toString() != null ? spnr_PreviousPosting.getSelectedItem().toString():"";
            String PreviousPostingDate            = edt_PreviousPosting_Date.getText().toString() != null ? edt_PreviousPosting_Date.getText().toString():"";
            String BarricadesComment              = edt_bldgBarricadesComment.getText().toString() != null ? edt_bldgBarricadesComment.getText().toString():"";

            String EngineeringEvaluationRecommendedType = sp_EngineeringRecommendation.getSelectedItem().toString() != null ? sp_EngineeringRecommendation.getSelectedItem().toString():"";
            String RecommendationsType                  = edt_bldgOtherRecommendation.getText().toString() != null ? edt_bldgOtherRecommendation.getText().toString():"";
            String NonstructuralHazardsOther            = edt_nonstructural_hazards_other.getText().toString() != null ? edt_nonstructural_hazards_other.getText().toString():"";
            String GeotechnicalHazardOther              = edt_geotechnical_hazard_other.getText().toString() != null ? edt_geotechnical_hazard_other.getText().toString():"";

            String CollapseType = "";
            if (rb_collapse_minor.isChecked())
            {
                CollapseType = "Minor";
            }
            else if (rb_collapse_moderate.isChecked())
            {
                CollapseType = "Moderate";
            }
            else if (rb_collapse_severe.isChecked())
            {
                CollapseType = "Severe";
            }

            String BuildingType = "";
            if (rb_bldg_minor.isChecked())
            {
                BuildingType = "Minor";
            }
            else if (rb_bldg_moderate.isChecked())
            {
                BuildingType = "Moderate";
            }
            else if (rb_bldg_severe.isChecked())
            {
                BuildingType = "Severe";
            }

            String FoundationType = "";
            if (rb_foundation_minor.isChecked())
            {
                FoundationType = "Minor";
            }
            else if (rb_foundation_moderate.isChecked())
            {
                FoundationType = "Moderate";
            }
            else if (rb_foundation_severe.isChecked())
            {
                FoundationType = "Severe";
            }

            String RoofType = "";
            if (rb_roof_minor.isChecked())
            {
                RoofType = "Minor";
            }
            else if (rb_roof_moderate.isChecked())
            {
                RoofType = "Moderate";
            }
            else if (rb_roof_severe.isChecked())
            {
                RoofType = "Severe";
            }

            String ColumnsType = "";
            if (rb_columns_minor.isChecked())
            {
                ColumnsType = "Minor";
            }
            else if (rb_columns_moderate.isChecked())
            {
                ColumnsType = "Moderate";
            }
            else if (rb_columns_severe.isChecked())
            {
                ColumnsType = "Severe";
            }

            String DiaphragmsType = "";
            if (rb_diaphragms_minor.isChecked())
            {
                DiaphragmsType = "Minor";
            }
            else if (rb_diaphragms_moderate.isChecked())
            {
                DiaphragmsType = "Moderate";
            }
            else if (rb_diaphragms_severe.isChecked())
            {
                DiaphragmsType = "Severe";
            }

            String WallsType = "";
            if (rb_walls_minor.isChecked())
            {
                WallsType = "Minor";
            }
            else if (rb_walls_moderate.isChecked())
            {
                WallsType = "Moderate";
            }
            else if (rb_walls_severe.isChecked())
            {
                WallsType = "Severe";
            }

            String PrecastType = "";
            if (rb_precast_minor.isChecked())
            {
                PrecastType = "Minor";
            }
            else if (rb_precast_moderate.isChecked())
            {
                PrecastType = "Moderate";
            }
            else if (rb_precast_severe.isChecked())
            {
                PrecastType = "Severe";
            }

            String ParapetsType = "";
            if (rb_parapets_minor.isChecked())
            {
                ParapetsType = "Minor";
            }
            else if (rb_parapets_moderate.isChecked())
            {
                ParapetsType = "Moderate";
            }
            else if (rb_parapets_severe.isChecked())
            {
                ParapetsType = "Severe";
            }

            String CladdingType = "";
            if (rb_cladding_minor.isChecked())
            {
                CladdingType = "Minor";
            }
            else if (rb_cladding_moderate.isChecked())
            {
                CladdingType = "Moderate";
            }
            else if (rb_cladding_severe.isChecked())
            {
                CladdingType = "Severe";
            }

            String CeilingType = "";
            if (rb_ceiling_minor.isChecked())
            {
                CeilingType = "Minor";
            }
            else if (rb_ceiling_moderate.isChecked())
            {
                CeilingType = "Moderate";
            }
            else if (rb_ceiling_severe.isChecked())
            {
                CeilingType = "Severe";
            }

            String InteriorType = "";
            if (rb_interior_walls_minor.isChecked())
            {
                InteriorType = "Minor";
            }
            else if (rb_interior_walls_moderate.isChecked())
            {
                InteriorType = "Moderate";
            }
            else if (rb_interior_walls_severe.isChecked())
            {
                InteriorType = "Severe";
            }

            String ElevatorsType = "";
            if (rb_elevators_minor.isChecked())
            {
                ElevatorsType = "Minor";
            }
            else if (rb_elevators_moderate.isChecked())
            {
                ElevatorsType = "Moderate";
            }
            else if (rb_elevators_severe.isChecked())
            {
                ElevatorsType = "Severe";
            }

            String stairsMinorType = "";
            if (rb_stairs_minor.isChecked())
            {
                stairsMinorType = "Minor";
            }
            else if (rb_stairs_moderate.isChecked())
            {
                stairsMinorType = "Moderate";
            }
            else if (rb_stairs_severe.isChecked())
            {
                stairsMinorType = "Severe";
            }

            String ElectricType = "";
            if (rb_electric_gas_minor.isChecked())
            {
                ElectricType = "Minor";
            }
            else if (rb_electric_gas_moderate.isChecked())
            {
                ElectricType = "Moderate";
            }
            else if (rb_electric_gas_severe.isChecked())
            {
                ElectricType = "Severe";
            }

            String SlopeType = "";
            if (rb_slope_failure_minor.isChecked())
            {
                SlopeType = "Minor";
            }
            else if (rb_slope_failure_moderate.isChecked())
            {
                SlopeType = "Moderate";
            }
            else if (rb_slope_failure_severe.isChecked())
            {
                SlopeType = "Severe";
            }

            String GroundMovementType = "";
            if (rb_ground_movement_minor.isChecked())
            {
                GroundMovementType = "Minor";
            }
            else if (rb_ground_movement_moderate.isChecked())
            {
                GroundMovementType = "Moderate";
            }
            else if (rb_ground_movement_severe.isChecked())
            {
                GroundMovementType = "Severe";
            }


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

            String FurtherActionsEngineeringEvaluationRecommended = "";
            if (cb_EngineeringEvaluation.isChecked())
            {
                FurtherActionsEngineeringEvaluationRecommended = "1";
            }
            else
            {
                FurtherActionsEngineeringEvaluationRecommended = "0";
            }

            String FurtherActionsOtherRecommendation = "";
            if (cb_bldgOtherRecommendation.isChecked())
            {
                FurtherActionsOtherRecommendation = "1";
            }
            else
            {
                FurtherActionsOtherRecommendation = "0";
            }

            DESAClass desaClass = new DESAClass();

            desaClass.setScreenerID(ScreenerID);
            desaClass.setMissionOrderID(MissionOrderID);
            desaClass.setAffiliation(Affiliation);
            desaClass.setSetDate(SetDate);
            desaClass.setSetTime(SetTime);
            desaClass.setBuildingName(BuildingName);
            desaClass.setBuildingAddress(BuildingAddress);
            desaClass.setBuildingContact(BuildingContact);
            desaClass.setNoOfStoreyAboveGround(NoOfStoreyAboveGround);
            desaClass.setNoOfStoreyBelowGround(NoOfStoreyBelowGround);
            desaClass.setTypeOfConstruction(TypeOfConstruction);
            desaClass.setPrimaryOccupancy(PrimaryOccupancy);
            desaClass.setApproxFootPrintAreaSM(ApproxFootPrintAreaSM);
            desaClass.setNoOfResidentialUnits(NoOfResidentialUnits);
            desaClass.setNoOfCommercialUnits(NoOfCommercialUnits);

            desaClass.setCollapseType(CollapseType);
            desaClass.setCollapseComment(CollapseComment);

            desaClass.setBuildingStoryLeaningType(BuildingType);
            desaClass.setBuildingStoryLeaningComment(BldgComment);

            desaClass.setOverAllHazardsOtherType(AllHazardsOtherType);

            desaClass.setFoundationType(FoundationType);
            desaClass.setFoundationComment(FoundationComment);

            desaClass.setRoofFloorVLType(RoofType);
            desaClass.setRoofFloorVLComment(RoofComment);

            desaClass.setCPCType(ColumnsType);
            desaClass.setCPCComment(ColumnsComment);

            desaClass.setDiaphragmsHBType(DiaphragmsType);
            desaClass.setDiaphragmsHBComment(DiaphragmsComment);

            desaClass.setWallsVBType(WallsType);
            desaClass.setWallsVBComment(WallsComment);

            desaClass.setPrecastConnectionsType(PrecastType);
            desaClass.setPrecastConnectionsComment(PrecastComment);
            desaClass.setParapetsOrnamentationType(ParapetsType);
            desaClass.setParapetsOrnamentationComment(ParapetsComment);
            desaClass.setCladdingGlazingType(CladdingType);
            desaClass.setCladdingGlazingComment(CladdingComment);
            desaClass.setCeilingLightFixturesType(CeilingType);
            desaClass.setCeilingLightFixturesComment(CeilingComment);
            desaClass.setInteriorWallsPartitionsType(InteriorType);
            desaClass.setInteriorWallsPartitionsComment(InteriorWallsComment);
            desaClass.setElevatorsType(ElevatorsType);
            desaClass.setElevatorsComment(ElevatorsComment);
            desaClass.setStairsExitType(stairsMinorType);
            desaClass.setStairsExitComment(StairsComment);
            desaClass.setElectricGasType(ElectricType);
            desaClass.setElectricGasComment(ElectricComment);
            desaClass.setNonstructuralHazardOtherType(NonstructuralHazardsOther);
            desaClass.setSlopeFailureDebrisType(SlopeType);
            desaClass.setSlopeFailureDebrisComment(SlopeFailureComment);
            desaClass.setGroundMovementFissuresType(GroundMovementType);
            desaClass.setGroundMovementFissuresComment(GroundMovementComment);
            desaClass.setGeotechnicalHazardOther(GeotechnicalHazardOther);

            desaClass.setEstimatedBuildingDamage(EstimatedBldgDamage);
            desaClass.setPreviousPostingEstimatedDamage(PreviousPostingEstimatedDamage);
            desaClass.setPreviousPostingDate(PreviousPostingDate);
            desaClass.setColorPlacard(ColorPlacard);

            desaClass.setFurtherActionsBarricades(FurtherActionsBarricades);
            desaClass.setFurtherActionsEngineeringEvaluationRecommended(FurtherActionsEngineeringEvaluationRecommended);
            desaClass.setFurtherActionsOtherRecommendation(FurtherActionsOtherRecommendation);

            desaClass.setBarricadesComment(BarricadesComment);
            desaClass.setEngineeringEvaluationRecommendedType(EngineeringEvaluationRecommendedType);
            desaClass.setRecommendationsType(RecommendationsType);
            desaClass.setComments(Comments);
            desaClass.setInspectedBy(sInspectedBy);

            if (option.equals("Save"))
            {
                RepositoryDESA.saveDESA(getContext(), desaClass);

                initShowDialogSuccess("Saved");
            }
            else
            {
                RepositoryDESA.updateDESA(getContext(), ScreenerID, MissionOrderID, desaClass);

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
                message = "Your changes have been successfully saved!";
            }
            else
            {
                message = "Your changes have been successfully updated!";
            }

            TextView tv_display = view.findViewById(R.id.tv_display);
            tv_display.setText(message);

            Button btn_ok = view.findViewById(R.id.btn_ok);

            mBuilder.setView(view);
            AlertDialog dialog2 = mBuilder.create();
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
        try
        {
            Log.i(TAG, "getLocationPermission: getting location permissions");

            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION};

            if (ContextCompat.checkSelfPermission(requireContext(),
                    FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                if (ContextCompat.checkSelfPermission(requireContext(),
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
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initMap()
    {
        try
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
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void getDeviceLocation()
    {
        try
        {
            markerOptions.position(defaultCoordinate);

            mMap.clear();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultCoordinate, DEFAULT_ZOOM));
            mMap.addMarker(markerOptions).setTitle("GOOGLE MAP");
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initListenersMaps()
    {
        try
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
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        try
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
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
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
            SimpleDateFormat DateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

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
            ImagePath = SaveFolderName + "/" + ImageName + "." + ImageExtension;
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

                                if (bitmap != null)
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
                                    createDirectoryAndSaveFile(bitmap);

                                    bmCollapse = bitmap;
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

                        try
                        {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
                            //Bitmap bitMapCustomize = Bitmap.createScaledBitmap(bitmap, 1000, 1000, false);

                            if (bitmap != null)
                            {
                                createDirectoryAndSaveFile(bitmap);

                                bmBuildingStoryLeaning = bitmap;

                                Glide.with(requireContext()).load(bitmap).into(imgv_bldg);
                            }
                        }
                        catch (Exception e)
                        {
                            Log.e(TAG, "Saving Error: " +  e.toString());
                        }
                    }
                }

                else if (requestCode == IMAGE_PICK_CAMERA_FOUNDATION)
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
                                bmFoundation = bitmap;
                                Glide.with(requireContext()).load(bitmap).into(imgv_foundation);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
                else if (requestCode == IMAGE_PICK_GALLERY_FOUNDATION)
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
                                    bmFoundation = bitmap;

                                    createDirectoryAndSaveFile(bitmap);

                                    Glide.with(requireContext()).load(bitmap).into(imgv_foundation);
                                }
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, "Saving Error: " +  e.toString());
                            }
                        }
                    }
                }

                else if (requestCode == IMAGE_PICK_CAMERA_ROOF)
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
                                bmRoofFloor = bitmap;
                                Glide.with(requireContext()).load(bitmap).into(imgv_roof);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
                else if (requestCode == IMAGE_PICK_GALLERY_ROOF)
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
                                    bmRoofFloor = bitmap;

                                    createDirectoryAndSaveFile(bitmap);

                                    Glide.with(requireContext()).load(bitmap).into(imgv_roof);
                                }
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, "Saving Error: " +  e.toString());
                            }
                        }
                    }
                }

                else if (requestCode == IMAGE_PICK_CAMERA_COLUMN)
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
                                bmColumns = bitmap;
                                Glide.with(requireContext()).load(bitmap).into(imgv_column);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
                else if (requestCode == IMAGE_PICK_GALLERY_COLUMN)
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
                                    bmColumns = bitmap;

                                    createDirectoryAndSaveFile(bitmap);

                                    Glide.with(requireContext()).load(bitmap).into(imgv_column);
                                }
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, "Saving Error: " +  e.toString());
                            }
                        }
                    }
                }

                else if (requestCode == IMAGE_PICK_CAMERA_DIAPHRAGMS)
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
                                bmDiaphragms = bitmap;
                                Glide.with(requireContext()).load(bitmap).into(imgv_diaphragms);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
                else if (requestCode == IMAGE_PICK_GALLERY_DIAPHRAGMS)
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
                                    bmDiaphragms = bitmap;

                                    createDirectoryAndSaveFile(bitmap);

                                    Glide.with(requireContext()).load(bitmap).into(imgv_diaphragms);
                                }
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, "Saving Error: " +  e.toString());
                            }
                        }
                    }
                }

                else if (requestCode == IMAGE_PICK_CAMERA_WALLS)
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
                                bmWalls = bitmap;
                                Glide.with(requireContext()).load(bitmap).into(imgv_walls);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
                else if (requestCode == IMAGE_PICK_GALLERY_WALLS)
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
                                    bmWalls = bitmap;

                                    createDirectoryAndSaveFile(bitmap);

                                    Glide.with(requireContext()).load(bitmap).into(imgv_walls);
                                }
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, "Saving Error: " +  e.toString());
                            }
                        }
                    }
                }

                else if (requestCode == IMAGE_PICK_CAMERA_PRECAST)
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
                                bmPrecast = bitmap;
                                Glide.with(requireContext()).load(bitmap).into(imgv_precast);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
                else if (requestCode == IMAGE_PICK_GALLERY_PRECAST)
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
                                    bmPrecast = bitmap;

                                    createDirectoryAndSaveFile(bitmap);

                                    Glide.with(requireContext()).load(bitmap).into(imgv_precast);
                                }
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, "Saving Error: " +  e.toString());
                            }
                        }
                    }
                }

                else if (requestCode == IMAGE_PICK_CAMERA_PARAPETS)
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
                                bmParapets = bitmap;
                                Glide.with(requireContext()).load(bitmap).into(imgv_parapets);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
                else if (requestCode == IMAGE_PICK_GALLERY_PARAPETS)
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
                                    bmParapets = bitmap;

                                    createDirectoryAndSaveFile(bitmap);

                                    Glide.with(requireContext()).load(bitmap).into(imgv_parapets);
                                }
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, "Saving Error: " +  e.toString());
                            }
                        }
                    }
                }

                else if (requestCode == IMAGE_PICK_CAMERA_CLADDING)
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
                                bmCladding = bitmap;
                                Glide.with(requireContext()).load(bitmap).into(imgv_cladding);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
                else if (requestCode == IMAGE_PICK_GALLERY_CLADDING)
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
                                    bmCladding = bitmap;

                                    createDirectoryAndSaveFile(bitmap);

                                    Glide.with(requireContext()).load(bitmap).into(imgv_cladding);
                                }
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, "Saving Error: " +  e.toString());
                            }
                        }
                    }
                }

                else if (requestCode == IMAGE_PICK_CAMERA_CEILING)
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
                                bmCeiling = bitmap;
                                Glide.with(requireContext()).load(bitmap).into(imgv_ceiling);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
                else if (requestCode == IMAGE_PICK_GALLERY_CEILING)
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
                                    bmCeiling = bitmap;

                                    createDirectoryAndSaveFile(bitmap);

                                    Glide.with(requireContext()).load(bitmap).into(imgv_ceiling);
                                }
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, "Saving Error: " +  e.toString());
                            }
                        }
                    }
                }

                else if (requestCode == IMAGE_PICK_CAMERA_INTERIOR_WALLS)
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
                                bmInteriorWalls = bitmap;
                                Glide.with(requireContext()).load(bitmap).into(imgv_interior_walls);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }

                }
                else if (requestCode == IMAGE_PICK_GALLERY_INTERIOR_WALLS)
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
                                    bmInteriorWalls = bitmap;

                                    createDirectoryAndSaveFile(bitmap);

                                    Glide.with(requireContext()).load(bitmap).into(imgv_interior_walls);
                                }
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, "Saving Error: " +  e.toString());
                            }
                        }
                    }
                }

                else if (requestCode == IMAGE_PICK_CAMERA_ELEVATOR)
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
                                bmElevators = bitmap;
                                Glide.with(requireContext()).load(bitmap).into(imgv_elevator);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
                else if (requestCode == IMAGE_PICK_GALLERY_ELEVATOR)
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
                                    bmElevators = bitmap;

                                    createDirectoryAndSaveFile(bitmap);

                                    Glide.with(requireContext()).load(bitmap).into(imgv_elevator);
                                }
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, "Saving Error: " +  e.toString());
                            }
                        }
                    }
                }

                else if (requestCode == IMAGE_PICK_CAMERA_STAIR)
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
                                bmStairs = bitmap;
                                Glide.with(requireContext()).load(bitmap).into(imgv_stair);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
                else if (requestCode == IMAGE_PICK_GALLERY_STAIR)
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
                                    bmStairs = bitmap;

                                    createDirectoryAndSaveFile(bitmap);

                                    Glide.with(requireContext()).load(bitmap).into(imgv_stair);
                                }
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, "Saving Error: " +  e.toString());
                            }
                        }
                    }
                }

                else if (requestCode == IMAGE_PICK_CAMERA_ELECTRIC)
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
                                bmElectric = bitmap;
                                Glide.with(requireContext()).load(bitmap).into(imgv_electric);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
                else if (requestCode == IMAGE_PICK_GALLERY_ELECTRIC)
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
                                    bmElectric = bitmap;

                                    createDirectoryAndSaveFile(bitmap);

                                    Glide.with(requireContext()).load(bitmap).into(imgv_electric);
                                }
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, "Saving Error: " +  e.toString());
                            }
                        }
                    }
                }

                else if (requestCode == IMAGE_PICK_CAMERA_SLOPE)
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
                                bmSlope = bitmap;
                                Glide.with(requireContext()).load(bitmap).into(imgv_slope);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }
                else if (requestCode == IMAGE_PICK_GALLERY_SLOPE)
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
                                    bmSlope = bitmap;

                                    createDirectoryAndSaveFile(bitmap);

                                    Glide.with(requireContext()).load(bitmap).into(imgv_slope);
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
                                bmGroundMovement = bitmap;
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
                                    bmGroundMovement = bitmap;

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
                        Log.e(TAG, "error here.. " + e.toString());
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
            inspectorSignatureClass.setSignatureID("DESA");

            inspectorSignatureClass.setSignatureName(ImageName);
            inspectorSignatureClass.setSignatureExtension(ImageExtension);
            inspectorSignatureClass.setSignaturePath(SignaturePath);
            inspectorSignatureClass.setDtAdded(dateAdded);

            Cursor cursor = RepositoryInspectorSignature.realAllData2(getContext(), UserAccount.UserAccountID, MissionOrderID, "DESA");

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
                    ImagePath = SaveFolderName + "/" + ImageName + "." + ImageExtension;

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

    private void initLoadPhoto(File file, int resultCodeCamera, int ResultCodeGallery, ImageView imageView, String ImageName)
    {
        try
        {
            TextView title = new TextView(getContext());
            String sTitle = "Taken Photo";
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

                    initAddPhoto(resultCodeCamera, ResultCodeGallery);
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
        try
        {
            Log.e(TAG, "ImageName:" + ImageName);

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

            if (ImageName.contains("Foundation Image"))
            {
                bmFoundation = null;
            }

            if (ImageName.contains("Roof Image"))
            {
                bmRoofFloor = null;
            }

            if (ImageName.contains("Column Image"))
            {
                bmColumns = null;
            }

            if (ImageName.contains("Diaphragms Image"))
            {
                bmDiaphragms = null;
            }

            if (ImageName.contains("Walls Image"))
            {
                bmWalls = null;
            }

            if (ImageName.contains("Precast Image"))
            {
                bmPrecast = null;
            }

            if (ImageName.contains("Parapets Image"))
            {
                bmParapets = null;
            }

            if (ImageName.contains("Cladding Image"))
            {
                bmCladding = null;
            }

            if (ImageName.contains("Ceiling Image"))
            {
                bmCeiling = null;
            }

            if (ImageName.contains("Interior Walls Image-"))
            {
                Log.e(TAG, "PICTURE WALLS REMOVED.");

                bmInteriorWalls = null;
            }

            if (ImageName.contains("Elevator Image"))
            {
                bmElevators = null;
            }

            if (ImageName.contains("Stair Image"))
            {
                bmStairs = null;
            }

            if (ImageName.contains("Electric Image"))
            {
                bmElectric = null;
            }

            if (ImageName.contains("Slope Image"))
            {
                bmSlope = null;
            }

            if (ImageName.contains("Ground Image"))
            {
                bmGroundMovement = null;
            }

            if (ImageName.contains("Placard Image"))
            {
                bmPosting = null;
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
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
            String ImageName_Building_Image = "Building Image" + "-" + MissionOrderNo  + "-" + dtAdded;
            String ImagePath_Building_Image = SaveFolderName + "/" + ImageName_Building_Image + ".png";

            File ImageFile_Building_Image = new File(ImagePath_Building_Image);

            if (ImageFile_Building_Image.exists())
            {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(ImagePath_Building_Image, bmOptions);

                if(bitmap != null)
                {
                    btn_take_photo.setText("CHANGE PHOTO");
                    btn_view_photo.setVisibility(View.VISIBLE);

                    //bitmap = initImageRotateNormal(ImageFile_Building_Image, bitmap);
                    //iv_building_image.setVisibility(View.VISIBLE);
                    //Glide.with(requireContext()).load(bitmap).into(iv_building_image);
                }
            }

            String ImageName_Collapse = "Collapse Image" + "-" + MissionOrderNo  + "-" + dtAdded;
            String ImagePath_Collapse = SaveFolderName + "/" + ImageName_Collapse + ".png";

            File ImageFile_Collapse = new File(ImagePath_Collapse);

            if (ImageFile_Collapse.exists())
            {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(ImagePath_Collapse, bmOptions);

                if (bitmap != null)
                {
                   // bitmap = initImageRotateNormal(ImageFile_Collapse, bitmap);

                    bmCollapse = bitmap;

                    Glide.with(requireContext()).load(bitmap).into(imgv_collapse);
                }
            }

            String ImageName_StoryLeaning = "Story Leaning Image" + "-" + MissionOrderNo  + "-" + dtAdded;
            String ImagePath_StoryLeaning = SaveFolderName + "/" + ImageName_StoryLeaning + ".png";

            File ImageFile_StoryLeaning = new File(ImagePath_StoryLeaning);

            if (ImageFile_StoryLeaning.exists())
            {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(ImagePath_StoryLeaning, bmOptions);

                if (bitmap != null)
                {
                   // bitmap = initImageRotateNormal(ImageFile_StoryLeaning, bitmap);

                    bmBuildingStoryLeaning = bitmap;

                    Glide.with(requireContext()).load(bitmap).into(imgv_bldg);
                }
            }

            String ImageName_Foundation = "Foundation Image" + "-" + MissionOrderNo  + "-" + dtAdded;
            String ImagePath_Foundation = SaveFolderName + "/" + ImageName_Foundation + ".png";

            File ImageFile_Foundation = new File(ImagePath_Foundation);

            if (ImageFile_Foundation.exists())
            {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(ImagePath_Foundation, bmOptions);

                if (bitmap != null)
                {
                   // bitmap = initImageRotateNormal(ImageFile_Foundation, bitmap);

                    bmFoundation = bitmap;

                    Glide.with(requireContext()).load(bitmap).into(imgv_foundation);
                }
            }

            String ImageName_Roof = "Roof Image" + "-" + MissionOrderNo  + "-" + dtAdded;
            String ImagePath_Roof = SaveFolderName + "/" + ImageName_Roof + ".png";

            File ImageFile_Roof = new File(ImagePath_Roof);

            if (ImageFile_Roof.exists())
            {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(ImagePath_Roof, bmOptions);

                if (bitmap != null)
                {
                    //bitmap = initImageRotateNormal(ImageFile_Roof, bitmap);

                    bmRoofFloor = bitmap;

                    Glide.with(requireContext()).load(bitmap).into(imgv_roof);
                }
            }

            String ImageName_Column = "Column Image" + "-" + MissionOrderNo  + "-" + dtAdded;
            String ImagePath_Column = SaveFolderName + "/" + ImageName_Column + ".png";

            File ImageFile_Column = new File(ImagePath_Column);

            if (ImageFile_Column.exists())
            {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(ImagePath_Column, bmOptions);

                if (bitmap != null)
                {
                    //bitmap = initImageRotateNormal(ImageFile_Column, bitmap);

                    bmColumns = bitmap;

                    Glide.with(requireContext()).load(bitmap).into(imgv_column);
                }
            }

            String ImageName_Diaphragms = "Diaphragms Image" + "-" + MissionOrderNo  + "-" + dtAdded;
            String ImagePath_Diaphragms = SaveFolderName + "/" + ImageName_Diaphragms + ".png";

            File ImageFile_Diaphragms = new File(ImagePath_Diaphragms);

            if (ImageFile_Diaphragms.exists())
            {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(ImagePath_Diaphragms, bmOptions);

                if (bitmap != null)
                {
                   // bitmap = initImageRotateNormal(ImageFile_Diaphragms, bitmap);

                    bmDiaphragms = bitmap;

                    Glide.with(requireContext()).load(bitmap).into(imgv_diaphragms);
                }
            }

            String ImageName_Walls = "Walls Image" + "-" + MissionOrderNo  + "-" + dtAdded;
            String ImagePath_Walls = SaveFolderName + "/" + ImageName_Walls + ".png";

            File ImageFile_Walls = new File(ImagePath_Walls);

            if (ImageFile_Walls.exists())
            {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(ImagePath_Walls, bmOptions);

                if (bitmap != null)
                {
                   // bitmap = initImageRotateNormal(ImageFile_Walls, bitmap);

                    bmWalls = bitmap;

                    Glide.with(requireContext()).load(bitmap).into(imgv_walls);
                }
            }

            String ImageName_Precast = "Precast Image" + "-" + MissionOrderNo  + "-" + dtAdded;
            String ImagePath_Precast = SaveFolderName + "/" + ImageName_Precast + ".png";

            File ImageFile_Precast = new File(ImagePath_Precast);

            if (ImageFile_Precast.exists())
            {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(ImagePath_Precast, bmOptions);

                if (bitmap != null)
                {
                   // bitmap = initImageRotateNormal(ImageFile_Precast, bitmap);

                    bmPrecast = bitmap;

                    Glide.with(requireContext()).load(bitmap).into(imgv_precast);
                }
            }

            String ImageName_Parapets = "Parapets Image" + "-" + MissionOrderNo  + "-" + dtAdded;
            String ImagePath_Parapets = SaveFolderName + "/" + ImageName_Parapets + ".png";

            File ImageFile_Parapets = new File(ImagePath_Parapets);

            if (ImageFile_Parapets.exists())
            {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(ImagePath_Parapets, bmOptions);

                if (bitmap != null)
                {
                   // bitmap = initImageRotateNormal(ImageFile_Parapets, bitmap);

                    bmParapets = bitmap;

                    Glide.with(requireContext()).load(bitmap).into(imgv_parapets);
                }
            }

            String ImageName_Cladding = "Cladding Image" + "-" + MissionOrderNo  + "-" + dtAdded;
            String ImagePath_Cladding = SaveFolderName + "/" + ImageName_Cladding + ".png";

            File ImageFile_Cladding = new File(ImagePath_Cladding);

            if (ImageFile_Cladding.exists())
            {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(ImagePath_Cladding, bmOptions);

                if (bitmap != null)
                {
                   // bitmap = initImageRotateNormal(ImageFile_Cladding, bitmap);

                    bmCladding = bitmap;

                    Glide.with(requireContext()).load(bitmap).into(imgv_cladding);
                }
            }

            String ImageName_Ceiling = "Ceiling Image" + "-" + MissionOrderNo  + "-" + dtAdded;
            String ImagePath_Ceiling = SaveFolderName + "/" + ImageName_Ceiling + ".png";

            File ImageFile_Ceiling = new File(ImagePath_Ceiling);

            if (ImageFile_Ceiling.exists())
            {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(ImagePath_Ceiling, bmOptions);

                if (bitmap != null)
                {
                   // bitmap = initImageRotateNormal(ImageFile_Ceiling, bitmap);

                    bmCeiling = bitmap;

                    Glide.with(requireContext()).load(bitmap).into(imgv_ceiling);
                }
            }

            String ImageName_InteriorWalls = "Interior Walls Image" + "-" + MissionOrderNo  + "-" + dtAdded;
            String ImagePath_InteriorWalls = SaveFolderName + "/" + ImageName_InteriorWalls + ".png";

            File ImageFile_InteriorWalls = new File(ImagePath_InteriorWalls);

            if (ImageFile_InteriorWalls.exists())
            {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(ImagePath_InteriorWalls, bmOptions);

                if (bitmap != null)
                {
                   // bitmap = initImageRotateNormal(ImageFile_InteriorWalls, bitmap);

                    bmInteriorWalls = bitmap;

                    Glide.with(requireContext()).load(bitmap).into(imgv_interior_walls);

                    Log.e(TAG, "EXISTTTTTTTTTTTTTTTTTTTT");
                }
            }

            String ImageName_Elevator = "Elevator Image" + "-" + MissionOrderNo  + "-" + dtAdded;
            String ImagePath_Elevator = SaveFolderName + "/" + ImageName_Elevator + ".png";

            File ImageFile_Elevator = new File(ImagePath_Elevator);

            if (ImageFile_Elevator.exists())
            {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(ImagePath_Elevator, bmOptions);

                if (bitmap != null)
                {
                   // bitmap = initImageRotateNormal(ImageFile_Elevator, bitmap);

                    bmElevators = bitmap;

                    Glide.with(requireContext()).load(bitmap).into(imgv_elevator);
                }
            }

            String ImageName_Stair = "Stair Image"  + "-" + MissionOrderNo  + "-" + dtAdded;
            String ImagePath_Stair = SaveFolderName + "/" + ImageName_Stair + ".png";

            File ImageFile_Stair = new File(ImagePath_Stair);

            if (ImageFile_Stair.exists())
            {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(ImagePath_Stair, bmOptions);

                if (bitmap != null)
                {
                    //bitmap = initImageRotateNormal(ImageFile_Stair, bitmap);

                    bmStairs = bitmap;

                    Glide.with(requireContext()).load(bitmap).into(imgv_stair);
                }
            }

            String ImageName_Electric = "Electric Image" + "-" + MissionOrderNo  + "-" + dtAdded;
            String ImagePath_Electric = SaveFolderName + "/" + ImageName_Electric + ".png";

            File ImageFile_Electric = new File(ImagePath_Electric);

            if (ImageFile_Electric.exists())
            {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(ImagePath_Electric, bmOptions);

                if (bitmap != null)
                {
                    //bitmap = initImageRotateNormal(ImageFile_Electric, bitmap);

                    bmElectric = bitmap;

                    Glide.with(requireContext()).load(bitmap).into(imgv_electric);
                }
            }

            String ImageName_Slope = "Slope Image" + "-" + MissionOrderNo  + "-" + dtAdded;
            String ImagePath_Slope = SaveFolderName + "/" + ImageName_Slope + ".png";

            File ImageFile_Slope = new File(ImagePath_Slope);

            if (ImageFile_Slope.exists())
            {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(ImagePath_Slope, bmOptions);

                if (bitmap != null)
                {
                   // bitmap = initImageRotateNormal(ImageFile_Slope, bitmap);

                    bmSlope = bitmap;

                    Glide.with(requireContext()).load(bitmap).into(imgv_slope);
                }
            }

            String ImageName_Ground = "Ground Image" + "-" + MissionOrderNo  + "-" + dtAdded;
            String ImagePath_Ground = SaveFolderName + "/" + ImageName_Ground + ".png";

            File ImageFile_Ground = new File(ImagePath_Ground);

            if (ImageFile_Ground.exists())
            {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(ImagePath_Ground, bmOptions);

                if (bitmap != null)
                {
                    //bitmap = initImageRotateNormal(ImageFile_Ground, bitmap);

                    bmGroundMovement = bitmap;

                    Glide.with(requireContext()).load(bitmap).into(imgv_ground);
                }
            }

            String ImageName_Placard = "Placard Image" + "-" + MissionOrderNo  + "-" + dtAdded;
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
                        intent.putExtra("ID", "DESA");
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

                        initGetPictureGallery(IMAGE_PICK_GALLERY_SIGNATURE);
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
        try
        {
            Cursor cursor = RepositoryInspectorSignature.realAllData(getContext(), "DESA", MissionOrderID);

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
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
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
        try
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
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
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


    private Bitmap convertImageViewToBitmap(ImageView v)
    {
        Bitmap bm=((BitmapDrawable)v.getDrawable()).getBitmap();

        return bm;
    }
}
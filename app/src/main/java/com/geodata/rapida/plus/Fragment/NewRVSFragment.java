package com.geodata.rapida.plus.Fragment;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.geodata.rapida.plus.Activity.CreateSignatureActivity;
import com.geodata.rapida.plus.Activity.ViewPDFNewRVSActivity;
import com.geodata.rapida.plus.Adapter.RVAdapterBuildingInventoryYearList;
import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.Retrofit.Client.APIClient;
import com.geodata.rapida.plus.Retrofit.Interface.APIClientInterface;
import com.geodata.rapida.plus.Retrofit.Model.AllBarangaysOfEDIModel;
import com.geodata.rapida.plus.Retrofit.Model.AllCitiesOfEDIModel;
import com.geodata.rapida.plus.Retrofit.Model.AllDistrictOfficesOfEDIModel;
import com.geodata.rapida.plus.Retrofit.Model.AllProvincesOfEDIModel;
import com.geodata.rapida.plus.Retrofit.Model.AllRegionsOfEDIModel;
import com.geodata.rapida.plus.Retrofit.Model.BuildingInfoTableModel;
import com.geodata.rapida.plus.Retrofit.Model.BuildingInventoryYearDataModel;
import com.geodata.rapida.plus.Retrofit.Model.BuildingListOfEDIModel;
import com.geodata.rapida.plus.SQLite.Class.InspectorSignatureClass;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryBuildingScores;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryInspectorSignature;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryNewRVSBuildings;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryOnlineMissionOrders;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryRVSSaveAsDraft;
import com.geodata.rapida.plus.Tools.APIUrls;
import com.geodata.rapida.plus.Tools.MyExceptionHandler;
import com.geodata.rapida.plus.Tools.UserAccount;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.itextpdf.text.BaseColor;
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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewRVSFragment extends Fragment
{
    private static final String TAG = NewRVSFragment.class.getSimpleName();

    View view;

    //MAP VIEW
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE  = 1234;
    private static final float DEFAULT_ZOOM = 18f;

    private Boolean mLocationPermissionGranted = false, isSignatureDisplay = false;
    private GoogleMap mMap;
    MarkerOptions markerOptions;
    SupportMapFragment mapFragment;
    LatLng MapPosition;
    LatLng defaultCoordinate = new LatLng(Double.parseDouble("14.785731"), Double.parseDouble("121.028925"));

    ImageView iv_building_signature, iv_remove_building_signature;
    Bitmap bmInspectorSignature;

    CheckBox cbo_wood_frame, cbo_steel_frame, cbo_concrete_frame,
            cbo_pre_code, cbo_post_benchmark,
            cbo_soil_type_c2, cbo_soil_type_d3, cbo_soil_type_e4;

    Spinner spnr_residential,
            spnr_wf_vertical_irregularity, spnr_sf_vertical_irregularity, spnr_cf_vertical_irregularity,
            spnr_wf_plan_irregularity, spnr_sf_plan_irregularity, spnr_cf_plan_irregularity,
            spnr_wf_pre_code, spnr_sf_pre_code, spnr_cf_pre_code,
            spnr_wf_post_benchmark, spnr_sf_post_benchmark, spnr_cf_post_benchmark,
            spnr_wf_soil_type_c2, spnr_sf_soil_type_c2, spnr_cf_soil_type_c2,
            spnr_wf_soil_type_d3, spnr_sf_soil_type_d3, spnr_cf_soil_type_d3,
            spnr_wf_soil_type_e4, spnr_sf_soil_type_e4, spnr_cf_soil_type_e4,
            spnr_region, spnr_district_office, spnr_province, spnr_city_municipality, spnr_barangay;

    RadioGroup rg_wood_frame, rg_steel_frame, rg_concrete_frame;

    RadioButton rbn_low_rise_wf, rbn_low_rise_sf, rbn_low_rise_cf,
            rbn_mid_rise_wf, rbn_mid_rise_sf, rbn_mid_rise_cf,
            rbn_high_rise_wf, rbn_high_rise_sf, rbn_high_rise_cf;

    TextView tv_building_date, tv_final_score_wf, tv_final_score_st, tv_final_score_cf;

    EditText edt_building_name, edt_cons_status, edt_visit_no,
            edt_storeys_no, edt_floor_are_no, edt_building_permit_no,
            edt_building_lot, edt_building_street, edt_building_block, edt_sub_division,
            edt_barangay, edt_district, edt_city, edt_owner_name,
            edt_company_name, edt_president_name, edt_structural_components, edt_non_structural_components,
            edt_ancillary_auxiliary, edt_remarks, edt_inspected_by;

    Button btn_select_building, btn_take_photo, btn_view_photo, btn_save, btn_preview;

    String SaveFolderName, ImageName, ImageExtension, sDateNow, AssetID="", AssetIDTemp="0", AssetInfoBuildingID="",
           pdfFileName, path, BuildingPhotoBase64 ="", setDateNow="", BuildingPhotoPath="", ImagePath, CameraFileName;


    List<BuildingListOfEDIModel> buildingListOfEDIModel;
    BuildingListOfEDIModel buildingListOfEDIModelTemp;

    AlertDialog customizeAlertDialog;

    List<BuildingInventoryYearDataModel> buildingInventoryYearDataModelList;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RVAdapterBuildingInventoryYearList rvAdapterBuildingInventoryYearList;

    ArrayList<Integer> AllRegionCodeArrayList = new ArrayList<>();
    ArrayList<String> AllRegionArrayList = new ArrayList<>();

    ArrayList<Integer> AllDistrictCodeArrayList  = new ArrayList<>();
    ArrayList<String> AllDistrictArrayList  = new ArrayList<>();

    ArrayList<Integer> AllProvincesCodeArrayList = new ArrayList<>();
    ArrayList<String> AllProvincesArrayList = new ArrayList<>();

    ArrayList<Integer> AllCitiesCodeArrayList = new ArrayList<>();
    ArrayList<String> AllCitiesArrayList    = new ArrayList<>();

    ArrayList<Integer> AllBarangaysCodeArrayList = new ArrayList<>();
    ArrayList<String> AllBarangaysArrayList = new ArrayList<>();


    int IMAGE_PICK_CAMERA_CODE = 100, IMAGE_PICK_GALLERY_CODE = 101,
        IMAGE_PICK_CAMERA_SIGNATURE = 102, IMAGE_PICK_GALLERY_SIGNATURE = 103,
        InventoryYear;

    ProgressDialog progressDialog;

    Uri imageUri;
    Bitmap bitmapBuildingPhoto = null;

    Calendar iCalendar;

    File pdfPath;

    ArrayAdapter<String> ADistrictOfficeArrayList;
    ArrayAdapter<String> AProvincesArrayList;
    ArrayAdapter<String> ACitiesArrayList;
    ArrayAdapter<String> ABarangaysArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(getActivity())); //Getting Crash - Restart

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_new_rvs, container, false);

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

        progressDialog = new ProgressDialog(getActivity());
        iCalendar = Calendar.getInstance();

        SaveFolderName = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/SRI/Earthquake Damage Inspection/Attachments/";

        buildingListOfEDIModel             = new ArrayList<>();
        buildingInventoryYearDataModelList = new ArrayList<>();

        iv_building_signature          = view.findViewById(R.id.iv_building_signature);
        iv_remove_building_signature   = view.findViewById(R.id.iv_remove_building_signature);

        cbo_wood_frame     = view.findViewById(R.id.cbo_wood_frame);
        cbo_steel_frame    = view.findViewById(R.id.cbo_steel_frame);
        cbo_concrete_frame = view.findViewById(R.id.cbo_concrete_frame);
        cbo_pre_code       = view.findViewById(R.id.cbo_pre_code);
        cbo_post_benchmark = view.findViewById(R.id.cbo_post_benchmark);
        cbo_soil_type_c2   = view.findViewById(R.id.cbo_soil_type_c2);
        cbo_soil_type_d3   = view.findViewById(R.id.cbo_soil_type_d3);
        cbo_soil_type_e4   = view.findViewById(R.id.cbo_soil_type_e4);

        spnr_residential = view.findViewById(R.id.spnr_residential);

        spnr_wf_vertical_irregularity  = view.findViewById(R.id.spnr_wf_vertical_irregularity);
        spnr_wf_vertical_irregularity.setSelection(25);
        spnr_wf_vertical_irregularity.setEnabled(false);

        spnr_sf_vertical_irregularity  = view.findViewById(R.id.spnr_sf_vertical_irregularity);
        spnr_sf_vertical_irregularity.setSelection(25);
        spnr_sf_vertical_irregularity.setEnabled(false);

        spnr_cf_vertical_irregularity  = view.findViewById(R.id.spnr_cf_vertical_irregularity);
        spnr_cf_vertical_irregularity.setSelection(25);
        spnr_cf_vertical_irregularity.setEnabled(false);

        spnr_wf_plan_irregularity  = view.findViewById(R.id.spnr_wf_plan_irregularity);
        spnr_wf_plan_irregularity.setSelection(25);
        spnr_wf_plan_irregularity.setEnabled(false);

        spnr_sf_plan_irregularity  = view.findViewById(R.id.spnr_sf_plan_irregularity);
        spnr_sf_plan_irregularity.setSelection(25);
        spnr_sf_plan_irregularity.setEnabled(false);

        spnr_cf_plan_irregularity  = view.findViewById(R.id.spnr_cf_plan_irregularity);
        spnr_cf_plan_irregularity.setSelection(25);
        spnr_cf_plan_irregularity.setEnabled(false);

        spnr_wf_pre_code = view.findViewById(R.id.spnr_wf_pre_code);
        spnr_wf_pre_code.setSelection(25);
        spnr_wf_pre_code.setEnabled(false);

        spnr_sf_pre_code = view.findViewById(R.id.spnr_sf_pre_code);
        spnr_sf_pre_code.setSelection(25);
        spnr_sf_pre_code.setEnabled(false);

        spnr_cf_pre_code = view.findViewById(R.id.spnr_cf_pre_code);
        spnr_cf_pre_code.setSelection(25);
        spnr_cf_pre_code.setEnabled(false);

        spnr_wf_post_benchmark = view.findViewById(R.id.spnr_wf_post_benchmark);
        spnr_wf_post_benchmark.setSelection(25);
        spnr_wf_post_benchmark.setEnabled(false);

        spnr_sf_post_benchmark = view.findViewById(R.id.spnr_sf_post_benchmark);
        spnr_sf_post_benchmark.setSelection(25);
        spnr_sf_post_benchmark.setEnabled(false);

        spnr_cf_post_benchmark = view.findViewById(R.id.spnr_cf_post_benchmark);
        spnr_cf_post_benchmark.setSelection(25);
        spnr_cf_post_benchmark.setEnabled(false);

        spnr_wf_soil_type_c2 = view.findViewById(R.id.spnr_wf_soil_type_c2);
        spnr_wf_soil_type_c2.setSelection(25);
        spnr_wf_soil_type_c2.setEnabled(false);

        spnr_sf_soil_type_c2 = view.findViewById(R.id.spnr_sf_soil_type_c2);
        spnr_sf_soil_type_c2.setSelection(25);
        spnr_sf_soil_type_c2.setEnabled(false);

        spnr_cf_soil_type_c2 = view.findViewById(R.id.spnr_cf_soil_type_c2);
        spnr_cf_soil_type_c2.setSelection(25);
        spnr_cf_soil_type_c2.setEnabled(false);

        spnr_wf_soil_type_d3 = view.findViewById(R.id.spnr_wf_soil_type_d3);
        spnr_wf_soil_type_d3.setSelection(25);
        spnr_wf_soil_type_d3.setEnabled(false);

        spnr_sf_soil_type_d3 = view.findViewById(R.id.spnr_sf_soil_type_d3);
        spnr_sf_soil_type_d3.setSelection(25);
        spnr_sf_soil_type_d3.setEnabled(false);

        spnr_cf_soil_type_d3 = view.findViewById(R.id.spnr_cf_soil_type_d3);
        spnr_cf_soil_type_d3.setSelection(25);
        spnr_cf_soil_type_d3.setEnabled(false);

        spnr_wf_soil_type_e4 = view.findViewById(R.id.spnr_wf_soil_type_e4);
        spnr_wf_soil_type_e4.setSelection(25);
        spnr_wf_soil_type_e4.setEnabled(false);

        spnr_sf_soil_type_e4 = view.findViewById(R.id.spnr_sf_soil_type_e4);
        spnr_sf_soil_type_e4.setSelection(25);
        spnr_sf_soil_type_e4.setEnabled(false);

        spnr_cf_soil_type_e4 = view.findViewById(R.id.spnr_cf_soil_type_e4);
        spnr_cf_soil_type_e4.setSelection(25);
        spnr_cf_soil_type_e4.setEnabled(false);

        rg_wood_frame        = view.findViewById(R.id.rg_wood_frame);
        rg_steel_frame       = view.findViewById(R.id.rg_steel_frame);
        rg_concrete_frame    = view.findViewById(R.id.rg_concrete_frame);

        rbn_low_rise_wf  = view.findViewById(R.id.rbn_low_rise_wf);
        rbn_low_rise_sf  = view.findViewById(R.id.rbn_low_rise_sf);
        rbn_low_rise_cf  = view.findViewById(R.id.rbn_low_rise_cf);
        rbn_mid_rise_wf  = view.findViewById(R.id.rbn_mid_rise_wf);
        rbn_mid_rise_sf  = view.findViewById(R.id.rbn_mid_rise_sf);
        rbn_mid_rise_cf  = view.findViewById(R.id.rbn_mid_rise_cf);
        rbn_high_rise_wf = view.findViewById(R.id.rbn_high_rise_wf);
        rbn_high_rise_sf = view.findViewById(R.id.rbn_high_rise_sf);
        rbn_high_rise_cf = view.findViewById(R.id.rbn_high_rise_cf);

        tv_building_date  = view.findViewById(R.id.tv_building_date);
        tv_final_score_wf = view.findViewById(R.id.tv_final_score_wf);
        tv_final_score_st = view.findViewById(R.id.tv_final_score_st);
        tv_final_score_cf = view.findViewById(R.id.tv_final_score_cf);

        edt_building_name             = view.findViewById(R.id.edt_building_name);
        edt_cons_status               = view.findViewById(R.id.edt_cons_status);
        edt_visit_no                  = view.findViewById(R.id.edt_visit_no);
        edt_storeys_no                = view.findViewById(R.id.edt_storeys_no);
        edt_floor_are_no              = view.findViewById(R.id.edt_floor_are_no);
        edt_building_permit_no        = view.findViewById(R.id.edt_building_permit_no);
        edt_building_lot              = view.findViewById(R.id.edt_building_lot);
        edt_building_street           = view.findViewById(R.id.edt_building_street);
        edt_building_block            = view.findViewById(R.id.edt_building_block);
        edt_sub_division              = view.findViewById(R.id.edt_sub_division);
        edt_barangay                  = view.findViewById(R.id.edt_barangay);
        edt_district                  = view.findViewById(R.id.edt_district);
        edt_city                      = view.findViewById(R.id.edt_city);
        edt_owner_name                = view.findViewById(R.id.edt_owner_name);
        edt_company_name              = view.findViewById(R.id.edt_company_name);
        edt_president_name            = view.findViewById(R.id.edt_president_name);
        edt_structural_components     = view.findViewById(R.id.edt_structural_components);
        edt_non_structural_components = view.findViewById(R.id.edt_non_structural_components);
        edt_ancillary_auxiliary       = view.findViewById(R.id.edt_ancillary_auxiliary);
        edt_remarks                   = view.findViewById(R.id.edt_remarks);
        edt_inspected_by              = view.findViewById(R.id.edt_inspected_by);
        edt_inspected_by.setText(UserAccount.CompleteName);

        btn_view_photo      = view.findViewById(R.id.btn_view_photo);
        btn_take_photo      = view.findViewById(R.id.btn_take_photo);
        btn_select_building = view.findViewById(R.id.btn_select_building);

        btn_save        = view.findViewById(R.id.btn_save);
        btn_preview     = view.findViewById(R.id.btn_preview);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map);
        markerOptions = new MarkerOptions();

        getLocationPermission();

        initListeners();

        initGetRegionsOfEDI();

        WoodFrameFinalScore();
        SteelFrameFinalScore();
        ConcreteFrameFinalScore();

        retrieveDataFromDB(UserAccount.UserAccountID, "");
    }

    private void initListeners()
    {
        btn_select_building.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (haveNetworkConnection(requireContext()))
                {
                    if (buildingListOfEDIModel!= null && buildingListOfEDIModel.size() != 0)
                    {
                        initSelectBuilding();
                    }
                    else
                    {
                        int InventoryYear    = 2022;
                        int AccountCode      = 0;
                        int District         = 0;
                        int Region           = 0;
                        int City             = 0;
                        int Province         = 0;
                        int Barangay         = 0;
                        int StructureType    = 0;
                        int BuildingAge      = 0;
                        int OccupancyType    = 0;
                        String ModuleName    = "null_value";
                        String SearchKeyword = "null_value";
                        String tableActionID = "null_value";
                        int page             = 1;

                        initGetAllBuildings(0, AccountCode, District, Region, City, Province, Barangay, StructureType, BuildingAge,
                                            OccupancyType, ModuleName, SearchKeyword, tableActionID, page, "Normal View");
                    }
                }
                else
                {
                    Toast.makeText(getActivity(), "You have no internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_take_photo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                initTakePhoto();
            }
        });

        btn_view_photo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (btn_take_photo.getText().toString().equalsIgnoreCase("TAKE PHOTO") && bitmapBuildingPhoto == null)
                {
                    Toast.makeText(getContext(), "Please take photo first.", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    initLoadPhoto(bitmapBuildingPhoto, IMAGE_PICK_CAMERA_CODE, IMAGE_PICK_GALLERY_CODE);
                }
            }
        });

        iv_building_signature.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        switch (which)
                        {
                            case DialogInterface.BUTTON_NEUTRAL:
                                Intent intent = new Intent(getContext(), CreateSignatureActivity.class);
                                intent.putExtra("Option", "Create Signature");
                                intent.putExtra("ID", "New RVS Signature");
                                intent.putExtra("MissionOrderID", AssetIDTemp);
                                startActivityForResult(intent, 105);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                ImageName = "Created Signature" + "-" + sDateNow;
                                ImageExtension = "png";
                                initGetPictureFromCamera(IMAGE_PICK_CAMERA_SIGNATURE);
                                break;

                            case DialogInterface.BUTTON_POSITIVE:
                                ImageName = "Created Signature" + "-" + sDateNow;
                                ImageExtension = "png";
                                initGetPictureGallery(IMAGE_PICK_GALLERY_SIGNATURE);
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.MyAlertDialogStyle);
                builder.setTitle("Add Signature");
                builder.setMessage("Please select option to add the signature.");
                builder.setCancelable(true);
                builder.setNeutralButton("Create Signature", onClickListener);
                builder.setNegativeButton("Capture Signature", onClickListener);
                builder.setPositiveButton("Upload Signature", onClickListener);
                builder.show();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener()
                {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(DialogInterface alertDialog, int which)
                    {
                        if (which == DialogInterface.BUTTON_POSITIVE)
                        {
                            initSaveOnly();
                        }
                    }
                };

                AlertDialog.Builder mValid = new AlertDialog.Builder(requireContext());
                mValid.setTitle("Confirm Save");
                mValid.setMessage("Are you sure you want to save this information ?");
                mValid.setCancelable(false);
                mValid.setPositiveButton("Confirm", ok);
                mValid.setNegativeButton("Cancel", null);
                mValid.show();
            }
        });

        btn_preview.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (edt_building_name.getText().toString()!= null)
                {
                    initReviewNewRVSReport(edt_building_name.getText().toString());
                }
                else
                {
                    Toast.makeText(getContext(), "Please select building first.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener()
        {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                {
                    iCalendar.set(Calendar.YEAR, year);
                    iCalendar.set(Calendar.MONTH, monthOfYear);
                    iCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    String myFormat = "MM/dd/yyyy"; //In which you need put here
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                    tv_building_date.setText(sdf.format(iCalendar.getTime()));
                }
            };

        tv_building_date.setOnClickListener(new View.OnClickListener()
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

        cbo_wood_frame.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                initEnableDisableWoodFrameRBN(isChecked);
            }
        });

        cbo_steel_frame.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                initEnableDisableSteelFrameRBN(isChecked);
            }
        });

        cbo_concrete_frame.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                initEnableDisableConRBN(isChecked);
            }
        });

        cbo_pre_code.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    cbo_post_benchmark.setChecked(false);

                    if (cbo_wood_frame.isChecked())
                    {
                        spnr_wf_pre_code.setEnabled(true);
                    }

                    if (cbo_steel_frame.isChecked())
                    {
                        spnr_sf_pre_code.setEnabled(true);
                    }

                    if (cbo_concrete_frame.isChecked())
                    {
                        spnr_cf_pre_code.setEnabled(true);
                    }
                }
                else
                {
                    spnr_wf_pre_code.setEnabled(false);
                    spnr_sf_pre_code.setEnabled(false);
                    spnr_cf_pre_code.setEnabled(false);

                    spnr_wf_pre_code.setSelection(25);
                    spnr_sf_pre_code.setSelection(25);
                    spnr_cf_pre_code.setSelection(25);
                }
            }
        });

        cbo_post_benchmark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    cbo_pre_code.setChecked(false);

                    if (cbo_wood_frame.isChecked())
                    {
                        spnr_wf_post_benchmark.setEnabled(true);
                    }

                    if (cbo_steel_frame.isChecked())
                    {
                        spnr_sf_post_benchmark.setEnabled(true);
                    }

                    if (cbo_concrete_frame.isChecked())
                    {
                        spnr_cf_post_benchmark.setEnabled(true);
                    }
                }
                else
                {
                    spnr_wf_post_benchmark.setEnabled(false);
                    spnr_sf_post_benchmark.setEnabled(false);
                    spnr_cf_post_benchmark.setEnabled(false);

                    spnr_wf_post_benchmark.setSelection(25);
                    spnr_sf_post_benchmark.setSelection(25);
                    spnr_cf_post_benchmark.setSelection(25);
                }
            }
        });

        cbo_soil_type_c2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    cbo_soil_type_d3.setChecked(false);
                    cbo_soil_type_e4.setChecked(false);

                    if (cbo_wood_frame.isChecked())
                    {
                        spnr_wf_soil_type_c2.setEnabled(true);
                    }

                    if (cbo_steel_frame.isChecked())
                    {
                        spnr_sf_soil_type_c2.setEnabled(true);
                    }

                    if (cbo_concrete_frame.isChecked())
                    {
                        spnr_cf_soil_type_c2.setEnabled(true);
                    }
                }
                else
                {
                    spnr_wf_soil_type_c2.setEnabled(false);
                    spnr_sf_soil_type_c2.setEnabled(false);
                    spnr_cf_soil_type_c2.setEnabled(false);

                    spnr_wf_soil_type_c2.setSelection(25);
                    spnr_sf_soil_type_c2.setSelection(25);
                    spnr_cf_soil_type_c2.setSelection(25);
                }
            }
        });

        cbo_soil_type_d3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    cbo_soil_type_c2.setChecked(false);
                    cbo_soil_type_e4.setChecked(false);

                    if (cbo_wood_frame.isChecked())
                    {
                        spnr_wf_soil_type_d3.setEnabled(true);
                    }

                    if (cbo_steel_frame.isChecked())
                    {
                        spnr_sf_soil_type_d3.setEnabled(true);
                    }

                    if (cbo_concrete_frame.isChecked())
                    {
                        spnr_cf_soil_type_d3.setEnabled(true);
                    }
                }
                else
                {
                    spnr_wf_soil_type_d3.setEnabled(false);
                    spnr_sf_soil_type_d3.setEnabled(false);
                    spnr_cf_soil_type_d3.setEnabled(false);

                    spnr_wf_soil_type_d3.setSelection(25);
                    spnr_sf_soil_type_d3.setSelection(25);
                    spnr_cf_soil_type_d3.setSelection(25);
                }
            }
        });

        cbo_soil_type_e4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    cbo_soil_type_c2.setChecked(false);
                    cbo_soil_type_d3.setChecked(false);

                    if (cbo_wood_frame.isChecked())
                    {
                        spnr_wf_soil_type_e4.setEnabled(true);
                    }

                    if (cbo_steel_frame.isChecked())
                    {
                        spnr_sf_soil_type_e4.setEnabled(true);
                    }

                    if (cbo_concrete_frame.isChecked())
                    {
                        spnr_cf_soil_type_e4.setEnabled(true);
                    }

                }
                else
                {
                    spnr_wf_soil_type_e4.setEnabled(false);
                    spnr_sf_soil_type_e4.setEnabled(false);
                    spnr_cf_soil_type_e4.setEnabled(false);

                    spnr_wf_soil_type_e4.setSelection(25);
                    spnr_sf_soil_type_e4.setSelection(25);
                    spnr_cf_soil_type_e4.setSelection(25);
                }
            }
        });

        initSpinnersListeners();
    }

    private void initReviewNewRVSReport(String BuildingName)
    {
        try
        {
            pdfFileName = BuildingName + "_" + AssetInfoBuildingID + ".pdf";

            path = "SRI/RVS/List Building";

            File folderPath = new File(Environment.getExternalStorageDirectory(), path);

            File file = new File(folderPath, pdfFileName);

            if (file.exists())
            {
                file.delete();
            }

            if (!folderPath.exists())
            {
                folderPath.mkdirs();

                initDocument(BuildingName);
            }
            else
                initDocument(BuildingName);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initDocument(String BuildingName)
    {
        try
        {
            Document document = new Document(PageSize.A4, 15, 15, 15, 15);

            try
            {
                pdfPath = new File(Environment.getExternalStorageDirectory(), path + "/" + pdfFileName);

                PdfWriter.getInstance(document, new FileOutputStream(pdfPath));

                document.open();

                initGeneratePDF(document);
            }
            catch (FileNotFoundException | DocumentException e)
            {
                Log.e(TAG, e.toString());
            }
            document.close();

            String FinalScoreWF = tv_final_score_wf.getText().toString();
            String FinalScoreSF = tv_final_score_st.getText().toString();
            String FinalScoreCF = tv_final_score_cf.getText().toString();

            Intent intent = new Intent(getContext(), ViewPDFNewRVSActivity.class);
            intent.putExtra("FileName",            pdfFileName);
            intent.putExtra("BuildingName",        BuildingName);
            intent.putExtra("AssetInfoBuildingID", AssetInfoBuildingID);
            intent.putExtra("AssetID",             AssetID);
            intent.putExtra("FinalScoreWF",        FinalScoreWF);
            intent.putExtra("FinalScoreSF",        FinalScoreSF);
            intent.putExtra("FinalScoreCF",        FinalScoreCF);
            startActivityForResult(intent, 100);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initGeneratePDF(Document document) throws DocumentException  //Set PDF document Properties
    {
        PdfPTable table, table2;
        Phrase sPhrase;
        PdfPCell cells, sCells;
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

            assert bitmapDrawable != null;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            getResizedBitmap(bitmap, 500).compress(Bitmap.CompressFormat.PNG, 100, stream);

            Image republicLogo1 = Image.getInstance(stream.toByteArray());
            republicLogo1.setAlignment(Image.ALIGN_RIGHT);
            republicLogo1.scaleAbsolute(65f, 20f);

            Image republicLogo2 = Image.getInstance(stream.toByteArray());
            republicLogo2.setAlignment(Image.ALIGN_LEFT);
            republicLogo2.scaleAbsolute(65f, 5f);

            Image republicLogo3 = Image.getInstance(stream.toByteArray());
            republicLogo3.setAlignment(Image.ALIGN_CENTER);
            republicLogo3.scaleAbsolute(65f, 5f);


            cells = new PdfPCell();
            cells.addElement(republicLogo1);
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setColspan(1);
            cells.setPaddingTop(10);
            table.addCell(cells);

            Phrase headerPhrase = new Phrase();
            headerPhrase.add(new Phrase("\nRepublic of the Philippines", header1));
            headerPhrase.add(new Phrase("\nCity / Municipality", header1));
            //headerPhrase.add(new Phrase("\nDEPARTMENT OF BUILDING OFFICIAL", header2));
            headerPhrase.add(new Phrase("\nAddress", header1));
            headerPhrase.add(new Phrase("\nRapid Visual Screening", header2));

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

            //region INSPECTION INFORMATION
            table = new PdfPTable(2);
            table.setWidthPercentage(100);

            cells = new PdfPCell(new Phrase("Inspection", catFont2));
            cells.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setColspan(2);
            table.addCell(cells);

            document.add(table);

            table = new PdfPTable(3);
            table.setWidthPercentage(100);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Inspected By: " , header2));
            sPhrase.add(new Phrase(UserAccount.CompleteName, header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setColspan(1);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Inspected ID: " , header2));
            sPhrase.add(new Phrase(UserAccount.UserAccountID, header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.BOTTOM);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setColspan(1);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Date: " , header2));

            if (!setDateNow.equalsIgnoreCase(""))
            {
                sPhrase.add(new Phrase(setDateNow, header1));
            }
            else
            {
                sPhrase.add(new Phrase(sDateNow, header1));
            }

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.RIGHT | Rectangle.BOTTOM);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setColspan(1);
            table.addCell(cells);

            document.add(table);
            //endregion

            //region BUILDING INFORMATION
            table = new PdfPTable(100);
            table.setWidthPercentage(100);

            cells = new PdfPCell(new Phrase("Building Information", catFont2));
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(100);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Building Name: " , header2));
            sPhrase.add(new Phrase(edt_building_name.getText().toString() , header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(50);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Construction Status: " , header2));
            sPhrase.add(new Phrase(edt_cons_status.getText().toString() , header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingTop(5);
            cells.setPaddingLeft(5);
            cells.setColspan(50);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Visit(s): " , header2));
            sPhrase.add(new Phrase(edt_visit_no.getText().toString() , header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(10);
            cells.setPaddingBottom(5);
            cells.setColspan(25);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Storey(s): " , header2));
            sPhrase.add(new Phrase(edt_storeys_no.getText().toString() , header1));

            cells = new PdfPCell(new Phrase(sPhrase));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setPaddingTop(10);
            cells.setPaddingBottom(5);
            cells.setPaddingLeft(5);
            cells.setColspan(25);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Floor Area: " , header2));
            sPhrase.add(new Phrase(edt_floor_are_no.getText().toString() , header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(10);
            cells.setPaddingBottom(5);
            cells.setPaddingLeft(5);
            cells.setColspan(25);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(" ", header1));
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(10);
            cells.setPaddingBottom(5);
            cells.setColspan(25);
            table.addCell(cells);


            //OCCUPANCY
            cells = new PdfPCell();
            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Occupancy: ", header2));
            sPhrase.add(new Phrase("\n"));
            cells.addElement(sPhrase);

            sPhrase = new Phrase();
            if (spnr_residential.getSelectedItem().toString().equalsIgnoreCase("Residential"))
            {
                sPhrase.add(new Phrase("[  x  ]", header1));
            }
            else
            {
                sPhrase.add(new Phrase("[      ]", header1));
            }
            sPhrase.add(new Phrase(" Residential", header1));
            cells.addElement(sPhrase);

            sPhrase = new Phrase();
            if (spnr_residential.getSelectedItem().toString().equalsIgnoreCase("Commercial"))
            {
                sPhrase.add(new Phrase("[  x  ]", header1));
            }
            else
            {
                sPhrase.add(new Phrase("[      ]", header1));
            }
            sPhrase.add(new Phrase(" Commercial", header1));
            cells.addElement(sPhrase);

            sPhrase = new Phrase();
            if (spnr_residential.getSelectedItem().toString().equalsIgnoreCase("Institutional"))
            {
                sPhrase.add(new Phrase("[  x  ]", header1));
            }
            else
            {
                sPhrase.add(new Phrase("[      ]", header1));
            }
            sPhrase.add(new Phrase(" Institutional", header1));
            cells.addElement(sPhrase);

            cells.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
            cells.setPaddingLeft(5);
            cells.setPaddingBottom(20);
            cells.setColspan(25);
            table.addCell(cells);


            cells = new PdfPCell();

            table2 = new PdfPTable(100);
            table2.setWidthPercentage(100);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Building Permit No: " , header2));
            sPhrase.add(new Phrase(edt_building_permit_no.getText().toString() , header1));
            sPhrase.add(new Phrase("\n"));

            sCells = new PdfPCell(sPhrase);
            sCells.setBorder(Rectangle.NO_BORDER);
            sCells.setColspan(50);
            table2.addCell(sCells);


            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Date Built: " , header2));

            if(!tv_building_date.getText().toString().equalsIgnoreCase("Select Date"))
            {
                sPhrase.add(new Phrase(tv_building_date.getText().toString() , header1));
            }

            sPhrase.add(new Phrase("\n"));

            sCells = new PdfPCell(sPhrase);
            sCells.setBorder(Rectangle.NO_BORDER);
            sCells.setColspan(50);
            table2.addCell(sCells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Address", header2));

            sCells = new PdfPCell(sPhrase);
            sCells.setBorder(Rectangle.NO_BORDER);
            sCells.setColspan(100);
            table2.addCell(sCells);


            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Lot: " , header2));
            sPhrase.add(new Phrase(edt_building_lot.getText().toString() , header1));

            sCells = new PdfPCell(sPhrase);
            sCells.setBorder(Rectangle.NO_BORDER);
            sCells.setPaddingTop(5);
            sCells.setColspan(15);
            table2.addCell(sCells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Block: " , header2));
            sPhrase.add(new Phrase(edt_building_block.getText().toString() , header1));

            sCells = new PdfPCell(sPhrase);
            sCells.setBorder(Rectangle.NO_BORDER);
            sCells.setPaddingLeft(5);
            sCells.setPaddingTop(5);
            sCells.setColspan(15);
            table2.addCell(sCells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Street: " , header2));
            sPhrase.add(new Phrase(edt_building_street.getText().toString() , header1));

            sCells = new PdfPCell(sPhrase);
            sCells.setBorder(Rectangle.NO_BORDER);
            sCells.setPaddingTop(5);
            sCells.setPaddingLeft(5);
            sCells.setColspan(30);
            table2.addCell(sCells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Subdivision: " , header2));
            sPhrase.add(new Phrase(edt_building_street.getText().toString() , header1));

            sCells = new PdfPCell(sPhrase);
            sCells.setBorder(Rectangle.NO_BORDER);
            sCells.setPaddingLeft(5);
            sCells.setPaddingTop(5);
            sCells.setColspan(40);
            table2.addCell(sCells);

            cells.addElement(table2);

            table2 = new PdfPTable(100);
            table2.setWidthPercentage(100);


            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Barangay: " , header2));
            sPhrase.add(new Phrase(edt_barangay.getText().toString() , header1));

            sCells = new PdfPCell(sPhrase);
            sCells.setBorder(Rectangle.NO_BORDER);
            sCells.setPaddingTop(5);
            sCells.setColspan(40);
            table2.addCell(sCells);


            sPhrase = new Phrase();
            sPhrase.add(new Phrase("District: " , header2));
            sPhrase.add(new Phrase(edt_district.getText().toString() , header1));

            sCells = new PdfPCell(sPhrase);
            sCells.setBorder(Rectangle.NO_BORDER);
            sCells.setPaddingLeft(5);
            sCells.setPaddingTop(5);
            sCells.setColspan(25);
            table2.addCell(sCells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("City: " , header2));
            sPhrase.add(new Phrase(edt_city.getText().toString() , header1));

            sCells = new PdfPCell(sPhrase);
            sCells.setBorder(Rectangle.NO_BORDER);
            sCells.setPaddingTop(5);
            sCells.setPaddingLeft(5);
            sCells.setColspan(35);
            table2.addCell(sCells);

            cells.addElement(table2);
            cells.setBorder(Rectangle.RIGHT | Rectangle.BOTTOM);
            cells.setPaddingLeft(5);
            cells.setPaddingBottom(10);
            cells.setColspan(75);
            table.addCell(cells);

            //endregion

            //region OWNER'S NAME / PRESIDENT'S NAME


            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Owner's Name: " , header2));
            sPhrase.add(new Phrase(edt_owner_name.getText().toString() , header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(100);
            table.addCell(cells);

            /*sPhrase = new Phrase();
            sPhrase.add(new Phrase("First Name: " , header1));
            sPhrase.add(new Phrase(" ", header1));
            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT );
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(27);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Middle Name: ", header1));
            sPhrase.add(new Phrase(" ", header1));
            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.NO_BORDER );
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(25);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Last Name: ", header1));
            sPhrase.add(new Phrase(" ", header1));
            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(48);
            table.addCell(cells);*/


            cells = new PdfPCell(new Phrase("If Company", catFont2));
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(10);
            cells.setColspan(100);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Company Name: " , header2));
            sPhrase.add(new Phrase(edt_company_name.getText().toString() , header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(10);
            cells.setColspan(100);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("President Name: " , header2));
            sPhrase.add(new Phrase(edt_president_name.getText().toString() , header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(10);
            cells.setColspan(100);
            table.addCell(cells);


            /*  sPhrase = new Phrase();
            sPhrase.add(new Phrase("First Name: ", header1));
            sPhrase.add(new Phrase(" ", header1));
            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setColspan(27);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase(" "));
            sPhrase.add(new Phrase(" "));
            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.NO_BORDER | Rectangle.BOTTOM);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setColspan(25);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Last Name: ", header1));
            sPhrase.add(new Phrase(" "));
            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.RIGHT | Rectangle.BOTTOM);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(5);
            cells.setColspan(48);
            table.addCell(cells);*/

            //endregion

            //region A.BASIC SCORE
            cells = new PdfPCell(new Phrase(" "));
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(40);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Wood Frame", header2));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(20);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Steel Frame ", header2));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(20);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Concrete Frame", header2));
            cells.setBorder(Rectangle.RIGHT);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(20);
            table.addCell(cells);


            cells = new PdfPCell(new Phrase("A. Basic Score", header2));
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(100);
            table.addCell(cells);


            table2 = new PdfPTable(1);
            table2.setWidthPercentage(100);

            sCells = new PdfPCell(new Phrase("Low-rise (1-3 stories)", header1));
            sCells.setColspan(1);
            sCells.setBorder(Rectangle.NO_BORDER);
            sCells.setPaddingTop(5);
            table2.addCell(sCells);

            sCells = new PdfPCell(new Phrase("Mid-rise (4-7 stories)", header1));
            sCells.setColspan(1);
            sCells.setBorder(Rectangle.NO_BORDER);
            sCells.setPaddingTop(5);
            table2.addCell(sCells);

            sCells = new PdfPCell(new Phrase("High-rise (>7 stories)", header1));
            sCells.setColspan(1);
            sCells.setBorder(Rectangle.NO_BORDER);
            sCells.setPaddingTop(5);
            table2.addCell(sCells);

            cells = new PdfPCell(table2);
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingLeft(10);
            cells.setPaddingTop(5);
            cells.setColspan(40);
            table.addCell(cells);

            //WOOD FRAME
            table2 = new PdfPTable(1);
            table2.setWidthPercentage(100);

            if (rbn_low_rise_wf.isChecked())
            {
                sCells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                sCells = new PdfPCell(new Phrase("[      ]", header1));
            }
            sCells.setColspan(1);
            sCells.setBorder(Rectangle.NO_BORDER);
            sCells.setHorizontalAlignment(Element.ALIGN_CENTER);
            sCells.setPaddingTop(5);
            table2.addCell(sCells);

            if (rbn_mid_rise_wf.isChecked())
            {
                sCells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                sCells = new PdfPCell(new Phrase("[      ]", header1));
            }
            sCells.setColspan(1);
            sCells.setBorder(Rectangle.NO_BORDER);
            sCells.setHorizontalAlignment(Element.ALIGN_CENTER);
            sCells.setPaddingTop(5);
            table2.addCell(sCells);

            if (rbn_high_rise_wf.isChecked())
            {
                sCells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                sCells = new PdfPCell(new Phrase("[      ]", header1));
            }
            sCells.setColspan(1);
            sCells.setBorder(Rectangle.NO_BORDER);
            sCells.setHorizontalAlignment(Element.ALIGN_CENTER);
            sCells.setPaddingTop(5);
            table2.addCell(sCells);

            cells = new PdfPCell(table2);
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(20);
            table.addCell(cells);

            //STEEL FRAME
            table2 = new PdfPTable(1);
            table2.setWidthPercentage(100);

            if (rbn_low_rise_sf.isChecked())
            {
                sCells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                sCells = new PdfPCell(new Phrase("[      ]", header1));
            }
            sCells.setColspan(1);
            sCells.setBorder(Rectangle.NO_BORDER);
            sCells.setHorizontalAlignment(Element.ALIGN_CENTER);
            sCells.setPaddingTop(5);
            table2.addCell(sCells);

            if (rbn_mid_rise_sf.isChecked())
            {
                sCells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                sCells = new PdfPCell(new Phrase("[      ]", header1));
            }
            sCells.setColspan(1);
            sCells.setBorder(Rectangle.NO_BORDER);
            sCells.setHorizontalAlignment(Element.ALIGN_CENTER);
            sCells.setPaddingTop(5);
            table2.addCell(sCells);

            if (rbn_high_rise_sf.isChecked())
            {
                sCells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                sCells = new PdfPCell(new Phrase("[      ]", header1));
            }
            sCells.setColspan(1);
            sCells.setBorder(Rectangle.NO_BORDER);
            sCells.setHorizontalAlignment(Element.ALIGN_CENTER);
            sCells.setPaddingTop(5);
            table2.addCell(sCells);

            cells = new PdfPCell(table2);
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(20);
            table.addCell(cells);

            //CONCRETE FRAME
            table2 = new PdfPTable(1);
            table2.setWidthPercentage(100);

            if (rbn_low_rise_cf.isChecked())
            {
                sCells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                sCells = new PdfPCell(new Phrase("[      ]", header1));
            }
            sCells.setColspan(1);
            sCells.setBorder(Rectangle.NO_BORDER);
            sCells.setHorizontalAlignment(Element.ALIGN_CENTER);
            sCells.setPaddingTop(5);
            table2.addCell(sCells);

            if (rbn_mid_rise_cf.isChecked())
            {
                sCells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                sCells = new PdfPCell(new Phrase("[      ]", header1));
            }
            sCells.setColspan(1);
            sCells.setBorder(Rectangle.NO_BORDER);
            sCells.setHorizontalAlignment(Element.ALIGN_CENTER);
            sCells.setPaddingTop(5);
            table2.addCell(sCells);

            if (rbn_high_rise_cf.isChecked())
            {
                sCells = new PdfPCell(new Phrase("[  x  ]", header1));
            }
            else
            {
                sCells = new PdfPCell(new Phrase("[      ]", header1));
            }
            sCells.setColspan(1);
            sCells.setBorder(Rectangle.NO_BORDER);
            sCells.setHorizontalAlignment(Element.ALIGN_CENTER);
            sCells.setPaddingTop(5);
            table2.addCell(sCells);

            cells = new PdfPCell(table2);
            cells.setBorder(Rectangle.RIGHT);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(20);
            table.addCell(cells);
            //endregion

            //region B. VERTICAL IRREGULARITY
            cells = new PdfPCell(new Phrase("B. Vertical Irregularity", header2));
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(10);
            cells.setColspan(40);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(spnr_wf_vertical_irregularity.getSelectedItem().toString(), header1));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(10);
            cells.setColspan(20);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(spnr_sf_vertical_irregularity.getSelectedItem().toString(), header1));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(10);
            cells.setColspan(20);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(spnr_cf_vertical_irregularity.getSelectedItem().toString(), header1));
            cells.setBorder(Rectangle.RIGHT);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(10);
            cells.setColspan(20);
            table.addCell(cells);
            //endregion

            //region C. PLAN IRREGULARITY
            cells = new PdfPCell(new Phrase("C. Plan Irregularity", header2));
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(10);
            cells.setColspan(40);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(spnr_wf_plan_irregularity.getSelectedItem().toString(), header1));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(10);
            cells.setColspan(20);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(spnr_sf_plan_irregularity.getSelectedItem().toString(), header1));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(10);
            cells.setColspan(20);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(spnr_cf_plan_irregularity.getSelectedItem().toString(), header1));
            cells.setBorder(Rectangle.RIGHT);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(10);
            cells.setColspan(20);
            table.addCell(cells);
            //endregion

            //region D. BUILDING AGE
            cells = new PdfPCell(new Phrase("D. Building Age", header2));
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(10);
            cells.setColspan(100);
            table.addCell(cells);

            table2 = new PdfPTable(100);
            table2.setWidthPercentage(100);

            if (cbo_pre_code.isChecked())
            {
                sCells = new PdfPCell(new Phrase("[ x ]"));
            }
            else
            {
                sCells = new PdfPCell(new Phrase("[    ]"));
            }
            sCells.setColspan(15);
            sCells.setBorder(Rectangle.NO_BORDER);
            table2.addCell(sCells);

            sCells = new PdfPCell(new Phrase("Pre-code (Constructed before 1972)", header1));
            sCells.setColspan(85);
            sCells.setBorder(Rectangle.NO_BORDER);
            table2.addCell(sCells);

            cells = new PdfPCell(table2);
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(40);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(spnr_wf_pre_code.getSelectedItem().toString(), header1));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(20);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(spnr_sf_pre_code.getSelectedItem().toString(), header1));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(20);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(spnr_cf_pre_code.getSelectedItem().toString(), header1));
            cells.setBorder(Rectangle.RIGHT);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(10);
            cells.setColspan(20);
            table.addCell(cells);


            table2 = new PdfPTable(100);
            table2.setWidthPercentage(100);

            if (cbo_post_benchmark.isChecked())
            {
                sCells = new PdfPCell(new Phrase("[ x ]"));
            }
            else
            {
                sCells = new PdfPCell(new Phrase("[    ]"));
            }
            sCells.setColspan(15);
            sCells.setBorder(Rectangle.NO_BORDER);
            table2.addCell(sCells);

            sCells = new PdfPCell(new Phrase("Post-Benchmark (Constructed after 1972)", header1));
            sCells.setColspan(85);
            sCells.setBorder(Rectangle.NO_BORDER);
            table2.addCell(sCells);

            cells = new PdfPCell(table2);
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(10);
            cells.setColspan(40);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(spnr_wf_post_benchmark.getSelectedItem().toString(), header1));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(10);
            cells.setColspan(20);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(spnr_sf_post_benchmark.getSelectedItem().toString(), header1));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(10);
            cells.setColspan(20);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(spnr_cf_post_benchmark.getSelectedItem().toString(), header1));
            cells.setBorder(Rectangle.RIGHT);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(10);
            cells.setColspan(20);
            table.addCell(cells);
            //endregion

            //region E. Soil Type
            cells = new PdfPCell(new Phrase("E. Soil Type", header2));
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(10);
            cells.setColspan(100);
            table.addCell(cells);

            table2 = new PdfPTable(100);
            table2.setWidthPercentage(100);


            if (cbo_soil_type_c2.isChecked())
            {
                sCells = new PdfPCell(new Phrase("[ x ]"));
            }
            else
            {
                sCells = new PdfPCell(new Phrase("[    ]"));
            }
            sCells.setColspan(15);
            sCells.setBorder(Rectangle.NO_BORDER);
            table2.addCell(sCells);

            sCells = new PdfPCell(new Phrase("Soil Type C2 (Soft Rock/Very Dense Oil)", header1));
            sCells.setColspan(85);
            sCells.setBorder(Rectangle.NO_BORDER);
            table2.addCell(sCells);

            cells = new PdfPCell(table2);
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(40);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(spnr_wf_soil_type_c2.getSelectedItem().toString(), header1));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(20);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(spnr_sf_soil_type_c2.getSelectedItem().toString(), header1));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(20);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(spnr_cf_soil_type_c2.getSelectedItem().toString(), header1));
            cells.setBorder(Rectangle.RIGHT);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(20);
            table.addCell(cells);


            table2 = new PdfPTable(100);
            table2.setWidthPercentage(100);

            if (cbo_soil_type_d3.isChecked())
            {
                sCells = new PdfPCell(new Phrase("[ x ]"));
            }
            else
            {
                sCells = new PdfPCell(new Phrase("[    ]"));
            }
            sCells.setColspan(15);
            sCells.setBorder(Rectangle.NO_BORDER);
            table2.addCell(sCells);

            sCells = new PdfPCell(new Phrase("Soil Type D3 (Stiff soil, or if no data assume for all buildings 1-2 storeys)", header1));
            sCells.setColspan(85);
            sCells.setBorder(Rectangle.NO_BORDER);
            table2.addCell(sCells);

            cells = new PdfPCell(table2);
            cells.setBorder(Rectangle.LEFT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(40);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(spnr_wf_soil_type_d3.getSelectedItem().toString(), header1));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(20);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(spnr_sf_soil_type_d3.getSelectedItem().toString(), header1));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(20);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(spnr_cf_soil_type_d3.getSelectedItem().toString(), header1));
            cells.setBorder(Rectangle.RIGHT);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(20);
            table.addCell(cells);


            table2 = new PdfPTable(100);
            table2.setWidthPercentage(100);

            if (cbo_soil_type_e4.isChecked())
            {
                sCells = new PdfPCell(new Phrase("[ x ]"));
            }
            else
            {
                sCells = new PdfPCell(new Phrase("[    ]"));
            }
            sCells.setColspan(15);
            sCells.setBorder(Rectangle.NO_BORDER);
            table2.addCell(sCells);

            sCells = new PdfPCell(new Phrase("Soil Type E4 (Stiff soil, or if no data assume for all buildings >2 storeys)", header1));
            sCells.setColspan(85);
            sCells.setBorder(Rectangle.NO_BORDER);
            table2.addCell(sCells);

            cells = new PdfPCell(table2);
            cells.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(10);
            cells.setColspan(40);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(spnr_wf_soil_type_e4.getSelectedItem().toString(), header1));
            cells.setBorder(Rectangle.BOTTOM);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(10);
            cells.setColspan(20);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(spnr_sf_soil_type_e4.getSelectedItem().toString(), header1));
            cells.setBorder(Rectangle.BOTTOM);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(10);
            cells.setColspan(20);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(spnr_cf_soil_type_e4.getSelectedItem().toString(), header1));
            cells.setBorder(Rectangle.RIGHT | Rectangle.BOTTOM);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(10);
            cells.setColspan(20);
            table.addCell(cells);

            document.add(table);
            //endregion

            //region FINAL SCORE
            document.newPage();

            table = new PdfPTable(100);
            table.setWidthPercentage(100);

            cells = new PdfPCell(new Phrase(" "));
            cells.setBorder(Rectangle.LEFT | Rectangle.TOP);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(40);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Wood Frame", header2));
            cells.setBorder(Rectangle.TOP);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(20);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Steel Frame ", header2));
            cells.setBorder(Rectangle.TOP);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(20);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("Concrete Frame", header2));
            cells.setBorder(Rectangle.TOP | Rectangle.RIGHT);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(20);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase("FINAL SCORE\n(if less than 2.0, building may vulnerable to Seismic Hazard)", header2));
            cells.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(10);
            cells.setPaddingBottom(10);
            cells.setColspan(40);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(tv_final_score_wf.getText().toString(), header1));
            cells.setBorder(Rectangle.NO_BORDER | Rectangle.BOTTOM);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(10);
            cells.setPaddingBottom(10);
            cells.setColspan(20);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(tv_final_score_st.getText().toString(), header1));
            cells.setBorder(Rectangle.NO_BORDER | Rectangle.BOTTOM);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(10);
            cells.setPaddingBottom(10);
            cells.setColspan(20);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(tv_final_score_cf.getText().toString(), header1));
            cells.setBorder(Rectangle.RIGHT | Rectangle.BOTTOM);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(10);
            cells.setPaddingBottom(10);
            cells.setColspan(20);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Structural Components: " , header2));
            sPhrase.add(new Phrase(edt_structural_components.getText().toString(), header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(100);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Non-Structural Components: " , header2));
            sPhrase.add(new Phrase(edt_non_structural_components.getText().toString(), header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(100);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Ancillary/Auxillary Components: " , header2));
            sPhrase.add(new Phrase(edt_ancillary_auxiliary.getText().toString(), header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setColspan(100);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase("Remarks: " , header2));
            sPhrase.add(new Phrase(edt_remarks.getText().toString(), header1));

            cells = new PdfPCell(sPhrase);
            cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
            cells.setPaddingLeft(5);
            cells.setPaddingTop(5);
            cells.setPaddingBottom(10);
            cells.setColspan(100);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(" ", header2));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(10);
            cells.setColspan(70);
            table.addCell(cells);

            cells = new PdfPCell();
            if (bmInspectorSignature != null)
            {
                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                getResizedBitmap(bmInspectorSignature, 1000).compress(Bitmap.CompressFormat.PNG, 100, stream1);

                Image AttachmentOutput = Image.getInstance(stream1.toByteArray());
                AttachmentOutput.setAlignment(Image.ALIGN_CENTER);
                AttachmentOutput.scaleToFit(300, 140);

                cells.addElement(AttachmentOutput);
            }
            else
            {
                cells.addElement(new Phrase(" \n "));
            }
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setColspan(25);
            cells.setPaddingTop(30);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(" ", header2));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(30);
            cells.setColspan(5);
            table.addCell(cells);


            cells = new PdfPCell(new Phrase(" ", header2));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setColspan(70);
            table.addCell(cells);

            sPhrase = new Phrase();
            sPhrase.add(new Phrase(edt_inspected_by.getText().toString(), header2));
            sPhrase.add(new Phrase("\nInspector", header2));

            cells = new PdfPCell(sPhrase);
            cells.setBackgroundColor(BaseColor.WHITE);
            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
            cells.setBorder(Rectangle.TOP);
            cells.setPaddingTop(5);
            cells.setColspan(25);
            table.addCell(cells);

            cells = new PdfPCell(new Phrase(" ", header2));
            cells.setBorder(Rectangle.NO_BORDER);
            cells.setPaddingTop(5);
            cells.setColspan(5);
            table.addCell(cells);

            document.add(table);


            //region ATTACHMENTS IMAGES
            document.newPage();

            table = new PdfPTable(100);
            table.setWidthPercentage(100);


            if (bitmapBuildingPhoto != null )
            {
                cells = new PdfPCell(new Phrase("Attachment", catFont4));
                cells.setBorder(Rectangle.NO_BORDER);
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setPaddingLeft(5);
                cells.setPaddingTop(5);
                cells.setColspan(100);
                table.addCell(cells);

                cells = new PdfPCell();

                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                getResizedBitmap(bitmapBuildingPhoto, 1000).compress(Bitmap.CompressFormat.PNG, 100, stream1);

                Image AttachmentOutput = Image.getInstance(stream1.toByteArray());
                AttachmentOutput.setAlignment(Image.ALIGN_CENTER);
                AttachmentOutput.scaleToFit(500, 500);

                cells.addElement(AttachmentOutput);
                cells.setBorder(Rectangle.NO_BORDER);
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setPaddingLeft(5);
                cells.setPaddingTop(50);
                cells.setColspan(100);
                table.addCell(cells);

                document.add(table);
            }
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



    private void initEnableDisableWoodFrameRBN(boolean isChecked)
    {
        if (isChecked)
        {
            rbn_low_rise_wf.setEnabled(true);
            rbn_mid_rise_wf.setEnabled(true);
            rbn_high_rise_wf.setEnabled(true);

            if (!edt_storeys_no.getText().toString().trim().isEmpty())
            {
                int iNoStory = Integer.parseInt(edt_storeys_no.getText().toString());

                if (iNoStory <= 3)
                {
                    rbn_low_rise_wf.setChecked(true);
                }
                else if (iNoStory <= 7)
                {
                    rbn_mid_rise_wf.setChecked(true);
                }
                else if (iNoStory <= 8)
                {
                    rbn_high_rise_wf.setChecked(true);
                }
            }

            spnr_wf_vertical_irregularity.setEnabled(true);
            spnr_wf_plan_irregularity.setEnabled(true);

            if (cbo_pre_code.isChecked())
            {
                spnr_wf_pre_code.setEnabled(true);
            }

            if (cbo_post_benchmark.isChecked())
            {
                spnr_wf_post_benchmark.setEnabled(true);
            }

            if (cbo_soil_type_c2.isChecked())
            {
                spnr_wf_soil_type_c2.setEnabled(true);
            }

            if (cbo_soil_type_d3.isChecked())
            {
                spnr_wf_soil_type_d3.setEnabled(true);
            }

            if (cbo_soil_type_e4.isChecked())
            {
                spnr_wf_soil_type_e4.setEnabled(true);
            }
        }
        else
        {
            rg_wood_frame.clearCheck();

            rbn_low_rise_wf.setEnabled(false);
            rbn_mid_rise_wf.setEnabled(false);
            rbn_high_rise_wf.setEnabled(false);

            spnr_wf_vertical_irregularity.setEnabled(false);
            spnr_wf_plan_irregularity.setEnabled(false);
            spnr_wf_pre_code.setEnabled(false);
            spnr_wf_post_benchmark.setEnabled(false);
            spnr_wf_soil_type_c2.setEnabled(false);
            spnr_wf_soil_type_d3.setEnabled(false);
            spnr_wf_soil_type_e4.setEnabled(false);

            rbn_low_rise_wf.setChecked(false);
            rbn_mid_rise_wf.setChecked(false);
            rbn_high_rise_wf.setChecked(false);

            spnr_wf_vertical_irregularity.setSelection(25);
            spnr_wf_plan_irregularity.setSelection(25);
            spnr_wf_pre_code.setSelection(25);
            spnr_wf_post_benchmark.setSelection(25);
            spnr_wf_soil_type_c2.setSelection(25);
            spnr_wf_soil_type_d3.setSelection(25);
            spnr_wf_soil_type_e4.setSelection(25);
        }
    }

    private void initEnableDisableSteelFrameRBN(boolean isChecked)
    {

        if (isChecked)
        {
            rbn_low_rise_sf.setEnabled(true);
            rbn_mid_rise_sf.setEnabled(true);
            rbn_high_rise_sf.setEnabled(true);

            if (!edt_storeys_no.getText().toString().trim().isEmpty())
            {
                int iNoStory = Integer.parseInt(edt_storeys_no.getText().toString());

                if (iNoStory <= 3)
                {
                    rbn_low_rise_sf.setChecked(true);
                }
                else if (iNoStory <= 7)
                {
                    rbn_mid_rise_sf.setChecked(true);
                }
                else if (iNoStory <= 8)
                {
                    rbn_high_rise_sf.setChecked(true);
                }
            }

            spnr_sf_vertical_irregularity.setEnabled(true);
            spnr_sf_plan_irregularity.setEnabled(true);

            if (cbo_pre_code.isChecked())
            {
                spnr_sf_pre_code.setEnabled(true);
            }

            if (cbo_post_benchmark.isChecked())
            {
                spnr_sf_post_benchmark.setEnabled(true);
            }

            if (cbo_soil_type_c2.isChecked())
            {
                spnr_sf_soil_type_c2.setEnabled(true);
            }

            if (cbo_soil_type_d3.isChecked())
            {
                spnr_sf_soil_type_d3.setEnabled(true);
            }

            if (cbo_soil_type_e4.isChecked())
            {
                spnr_sf_soil_type_e4.setEnabled(true);
            }
        }
        else
        {
            rg_steel_frame.clearCheck();

            rbn_low_rise_sf.setEnabled(false);
            rbn_mid_rise_sf.setEnabled(false);
            rbn_high_rise_sf.setEnabled(false);

            spnr_sf_vertical_irregularity.setEnabled(false);
            spnr_sf_plan_irregularity.setEnabled(false);
            spnr_sf_pre_code.setEnabled(false);
            spnr_sf_post_benchmark.setEnabled(false);
            spnr_sf_soil_type_c2.setEnabled(false);
            spnr_sf_soil_type_d3.setEnabled(false);
            spnr_sf_soil_type_e4.setEnabled(false);

            rbn_low_rise_sf.setChecked(false);
            rbn_mid_rise_sf.setChecked(false);
            rbn_high_rise_sf.setChecked(false);

            spnr_sf_vertical_irregularity.setSelection(25);
            spnr_sf_plan_irregularity.setSelection(25);
            spnr_sf_pre_code.setSelection(25);
            spnr_sf_post_benchmark.setSelection(25);
            spnr_sf_soil_type_c2.setSelection(25);
            spnr_sf_soil_type_d3.setSelection(25);
            spnr_sf_soil_type_e4.setSelection(25);
        }
    }

    private void initEnableDisableConRBN(boolean isChecked)
    {
        if (isChecked)
        {
            rbn_low_rise_cf.setEnabled(true);
            rbn_mid_rise_cf.setEnabled(true);
            rbn_high_rise_cf.setEnabled(true);

            if (!edt_storeys_no.getText().toString().trim().isEmpty())
            {
                int iNoStory = Integer.parseInt(edt_storeys_no.getText().toString());

                if (iNoStory <= 3)
                {
                    rbn_low_rise_cf.setChecked(true);
                }
                else if (iNoStory <= 7)
                {
                    rbn_mid_rise_cf.setChecked(true);
                }
                else if (iNoStory <= 8)
                {
                    rbn_high_rise_cf.setChecked(true);
                }
            }

            spnr_cf_vertical_irregularity.setEnabled(true);
            spnr_cf_plan_irregularity.setEnabled(true);

            if (cbo_pre_code.isChecked())
            {
                spnr_cf_pre_code.setEnabled(true);
            }

            if (cbo_post_benchmark.isChecked())
            {
                spnr_cf_post_benchmark.setEnabled(true);
            }

            if (cbo_soil_type_c2.isChecked())
            {
                spnr_cf_soil_type_c2.setEnabled(true);
            }

            if (cbo_soil_type_d3.isChecked())
            {
                spnr_cf_soil_type_d3.setEnabled(true);
            }

            if (cbo_soil_type_e4.isChecked())
            {
                spnr_cf_soil_type_e4.setEnabled(true);
            }
        }
        else
        {
            rg_concrete_frame.clearCheck();

            rbn_low_rise_cf.setEnabled(false);
            rbn_mid_rise_cf.setEnabled(false);
            rbn_high_rise_cf.setEnabled(false);

            spnr_cf_vertical_irregularity.setEnabled(false);
            spnr_cf_plan_irregularity.setEnabled(false);
            spnr_cf_pre_code.setEnabled(false);
            spnr_cf_post_benchmark.setEnabled(false);
            spnr_cf_soil_type_c2.setEnabled(false);
            spnr_cf_soil_type_d3.setEnabled(false);
            spnr_cf_soil_type_e4.setEnabled(false);

            rbn_low_rise_cf.setChecked(false);
            rbn_mid_rise_cf.setChecked(false);
            rbn_high_rise_cf.setChecked(false);

            spnr_cf_vertical_irregularity.setSelection(25);
            spnr_cf_plan_irregularity.setSelection(25);
            spnr_cf_pre_code.setSelection(25);
            spnr_cf_post_benchmark.setSelection(25);
            spnr_cf_soil_type_c2.setSelection(25);
            spnr_cf_soil_type_d3.setSelection(25);
            spnr_cf_soil_type_e4.setSelection(25);
        }
    }


    private void initSaveOnly()
    {
        try
        {
            if (bitmapBuildingPhoto != null)
            {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmapBuildingPhoto.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                BuildingPhotoBase64 = Base64.encodeToString(byteArray, Base64.NO_WRAP);
            }

            isSignatureDisplay = true;

            String EmployeeID         = UserAccount.UserAccountID;
            String BuildingName       = edt_building_name.getText().toString();
            String ConstructionStatus = edt_cons_status.getText().toString();
            String VisitNo            = edt_visit_no.getText().toString();
            String StoreysNo          = edt_storeys_no.getText().toString();

            String FloorNo           = edt_floor_are_no.getText().toString();
            String BuildingPermitNo  = edt_building_permit_no.getText().toString();
            String BuildingLot       = edt_building_lot.getText().toString();
            String BuildingStreet    = edt_building_street.getText().toString();
            String Residential       = spnr_residential.getSelectedItem().toString();

            String BuildingBlock        = edt_building_block.getText().toString();
            String BuildingSubDivision  = edt_sub_division.getText().toString();
            String BuildingBarangay     = edt_barangay.getText().toString();
            String BuildingDistrict     = edt_district.getText().toString();
            String BuildingCity         = edt_city.getText().toString();

            String OwnerName       = edt_owner_name.getText().toString();
            String CompanyName     = edt_company_name.getText().toString();
            String PresidentName   = edt_president_name.getText().toString();

            String StructuralComponents    = edt_structural_components.getText().toString();
            String NonStructuralComponents = edt_non_structural_components.getText().toString();
            String AncillaryAuxiliary      = edt_ancillary_auxiliary.getText().toString();
            String Remarks                 = edt_remarks.getText().toString();
            String InspectedBy             = edt_inspected_by.getText().toString();
            String BuildingDate            = tv_building_date.getText().toString();

            String VerticalIrregularityWF  = spnr_wf_vertical_irregularity.getSelectedItem().toString();
            String VerticalIrregularitySF  = spnr_sf_vertical_irregularity.getSelectedItem().toString();
            String VerticalIrregularityCF  = spnr_cf_vertical_irregularity.getSelectedItem().toString();

            String PlanIrregularityWF = spnr_wf_plan_irregularity.getSelectedItem().toString();
            String PlanIrregularitySF = spnr_sf_plan_irregularity.getSelectedItem().toString();
            String PlanIrregularityCF = spnr_cf_plan_irregularity.getSelectedItem().toString();

            String PreCodeWF = spnr_wf_pre_code.getSelectedItem().toString();
            String PreCodeSF = spnr_sf_pre_code.getSelectedItem().toString();
            String PreCodeCF = spnr_cf_pre_code.getSelectedItem().toString();

            String PostBenchmarkWF = spnr_wf_post_benchmark.getSelectedItem().toString();
            String PostBenchmarkSF = spnr_sf_post_benchmark.getSelectedItem().toString();
            String PostBenchmarkCF = spnr_cf_post_benchmark.getSelectedItem().toString();

            String SoilTypeC2WF  = spnr_wf_soil_type_c2.getSelectedItem().toString();
            String SoilTypeC2SF  = spnr_sf_soil_type_c2.getSelectedItem().toString();
            String SoilTypeC2CF  = spnr_cf_soil_type_c2.getSelectedItem().toString();

            String SoilTypeD3WF = spnr_wf_soil_type_d3.getSelectedItem().toString();
            String SoilTypeD3SF = spnr_sf_soil_type_d3.getSelectedItem().toString();
            String SoilTypeD3CF = spnr_cf_soil_type_d3.getSelectedItem().toString();

            String SoilTypeE4WF = spnr_wf_soil_type_e4.getSelectedItem().toString();
            String SoilTypeE4SF = spnr_sf_soil_type_e4.getSelectedItem().toString();
            String SoilTypeE4CF = spnr_cf_soil_type_e4.getSelectedItem().toString();

            String BasicScoreWoodFrame="", BasicScoreSteelFrame="", BasicScoreConcreteFrame="",
                   isWoodFrame, isSteelFrame, isConcreteFrame, isPreCode, isPostBenchmark,
                   isSoilTypeC2, isSoilTypeD3, isSoilTypeE4;

            if (cbo_wood_frame.isChecked())
            {
                isWoodFrame = "1";

                if (rbn_low_rise_wf.isChecked())
                {
                    BasicScoreWoodFrame = "Low-rise";
                }
                else if (rbn_mid_rise_wf.isChecked())
                {
                    BasicScoreWoodFrame = "Mid-rise";
                }
                else if (rbn_high_rise_wf.isChecked())
                {
                    BasicScoreWoodFrame = "High-rise";
                }
            }
            else
            {
                isWoodFrame = "0";
            }

            if (cbo_steel_frame.isChecked())
            {
                isSteelFrame = "1";

                if (rbn_low_rise_sf.isChecked())
                {
                    BasicScoreSteelFrame = "Low-rise";
                }
                else if (rbn_mid_rise_sf.isChecked())
                {
                    BasicScoreSteelFrame = "Mid-rise";
                }
                else if (rbn_high_rise_sf.isChecked())
                {
                    BasicScoreSteelFrame = "High-rise";
                }
            }
            else
            {
                isSteelFrame = "0";
            }

            if (cbo_concrete_frame.isChecked())
            {
                isConcreteFrame = "1";

                if (rbn_low_rise_cf.isChecked())
                {
                    BasicScoreConcreteFrame = "Low-rise";
                }
                else if (rbn_mid_rise_cf.isChecked())
                {
                    BasicScoreConcreteFrame = "Mid-rise";
                }
                else if (rbn_high_rise_cf.isChecked())
                {
                    BasicScoreConcreteFrame = "High-rise";
                }
            }
            else
            {
                isConcreteFrame = "0";
            }


            if (cbo_pre_code.isChecked())
            {
                isPreCode = "1";
            }
            else
            {
                isPreCode = "0";
            }

            if (cbo_post_benchmark.isChecked())
            {
                isPostBenchmark = "1";
            }
            else
            {
                isPostBenchmark = "0";
            }

            if (cbo_soil_type_c2.isChecked())
            {
                isSoilTypeC2 = "1";
            }
            else
            {
                isSoilTypeC2 = "0";
            }

            if (cbo_soil_type_d3.isChecked())
            {
                isSoilTypeD3 = "1";
            }
            else
            {
                isSoilTypeD3 = "0";
            }

            if (cbo_soil_type_e4.isChecked())
            {
                isSoilTypeE4 = "1";
            }
            else
            {
                isSoilTypeE4 = "0";
            }


            String WoodFrameFinalScore     = tv_final_score_wf.getText().toString();
            String SteelFrameFinalScore    = tv_final_score_st.getText().toString();
            String ConcreteFrameFinalScore = tv_final_score_cf.getText().toString();

            BuildingListOfEDIModel setBuildingListValue = new BuildingListOfEDIModel();

            //Building Information

            if (buildingListOfEDIModelTemp != null)
            {
                setBuildingListValue.setAssetInfoBuildingID(buildingListOfEDIModelTemp.getAssetInfoBuildingID());
                setBuildingListValue.setAssetID(buildingListOfEDIModelTemp.getAssetID());
                setBuildingListValue.setBIN(buildingListOfEDIModelTemp.getBIN());
                setBuildingListValue.setBuildingAge(buildingListOfEDIModelTemp.getBuildingAge());
                setBuildingListValue.setStructuralTypeDesc(buildingListOfEDIModelTemp.getStructuralTypeDesc());
                setBuildingListValue.setStructuralTypeCode(buildingListOfEDIModelTemp.getStructuralTypeCode());

                setBuildingListValue.setContactNo(buildingListOfEDIModelTemp.getContactNo());
                setBuildingListValue.setRegion(buildingListOfEDIModelTemp.getRegion());
                setBuildingListValue.setProvince(buildingListOfEDIModelTemp.getProvince());
                setBuildingListValue.setLatitude(buildingListOfEDIModelTemp.getLatitude());
                setBuildingListValue.setLongitude(buildingListOfEDIModelTemp.getLongitude());
                setBuildingListValue.setBuildingFullAddress(buildingListOfEDIModelTemp.getBuildingFullAddress());

            }

            setBuildingListValue.setScreenerID(EmployeeID);
            setBuildingListValue.setBuildingName(BuildingName);
            setBuildingListValue.setConstructionStatus(ConstructionStatus);
            setBuildingListValue.setVisitNo(VisitNo);
            setBuildingListValue.setStoreyNo(StoreysNo);
            setBuildingListValue.setFloorArea(FloorNo);
            setBuildingListValue.setResidential(Residential);
            setBuildingListValue.setBldgPermitNo(BuildingPermitNo);
            setBuildingListValue.setDateBuilt(BuildingDate);

            //Address
            setBuildingListValue.setLotArea(BuildingLot);
            setBuildingListValue.setBlockNo(BuildingBlock);
            setBuildingListValue.setStreet(BuildingStreet);
            setBuildingListValue.setSubdCompVill(BuildingSubDivision);
            setBuildingListValue.setBarangay(BuildingBarangay);
            setBuildingListValue.setDistrict(BuildingDistrict);
            setBuildingListValue.setCity(BuildingCity);

            setBuildingListValue.setOwnerName(OwnerName);
            setBuildingListValue.setCompanyName(CompanyName);
            setBuildingListValue.setPresidentName(PresidentName);

            //Basic Score
            setBuildingListValue.setIsWoodFrame(isWoodFrame);
            setBuildingListValue.setIsSteelFrame(isSteelFrame);
            setBuildingListValue.setIsConcreteFrame(isConcreteFrame);

            setBuildingListValue.setBasicScoreWF(BasicScoreWoodFrame);
            setBuildingListValue.setBasicScoreSF(BasicScoreSteelFrame);
            setBuildingListValue.setBasicScoreCF(BasicScoreConcreteFrame);

            //Vertical Irregularity
            setBuildingListValue.setVerticalIrregularityWF(VerticalIrregularityWF);
            setBuildingListValue.setVerticalIrregularitySF(VerticalIrregularitySF);
            setBuildingListValue.setVerticalIrregularityCF(VerticalIrregularityCF);

            //Plan Irregularity
            setBuildingListValue.setPlanIrregularityWF(PlanIrregularityWF);
            setBuildingListValue.setPlanIrregularitySF(PlanIrregularitySF);
            setBuildingListValue.setPlanIrregularityCF(PlanIrregularityCF);

            //Building Age
            setBuildingListValue.setIsPreCode(isPreCode);
            setBuildingListValue.setIsPostBenchmark(isPostBenchmark);
            setBuildingListValue.setPreCodeWF(PreCodeWF);
            setBuildingListValue.setPreCodeSF(PreCodeSF);
            setBuildingListValue.setPreCodeCF(PreCodeCF);
            setBuildingListValue.setPostBenchmarkWF(PostBenchmarkWF);
            setBuildingListValue.setPostBenchmarkSF(PostBenchmarkSF);
            setBuildingListValue.setPostBenchmarkCF(PostBenchmarkCF);

            //Soil Type
            setBuildingListValue.setIsSoilTypeC2(isSoilTypeC2);
            setBuildingListValue.setIsSoilTypeD3(isSoilTypeD3);
            setBuildingListValue.setIsSoilTypeE4(isSoilTypeE4);

            setBuildingListValue.setSoilTypeC2WF(SoilTypeC2WF);
            setBuildingListValue.setSoilTypeC2SF(SoilTypeC2SF);
            setBuildingListValue.setSoilTypeC2CF(SoilTypeC2CF);
            setBuildingListValue.setSoilTypeD3WF(SoilTypeD3WF);
            setBuildingListValue.setSoilTypeD3SF(SoilTypeD3SF);
            setBuildingListValue.setSoilTypeD3CF(SoilTypeD3CF);

            setBuildingListValue.setSoilTypeE4WF(SoilTypeE4WF);
            setBuildingListValue.setSoilTypeE4SF(SoilTypeE4SF);
            setBuildingListValue.setSoilTypeE4CF(SoilTypeE4CF);


            //Final Score
            setBuildingListValue.setWoodFrameFinalScore(WoodFrameFinalScore);
            setBuildingListValue.setSteelFrameFinalScore(SteelFrameFinalScore);
            setBuildingListValue.setConcreteFrameFinalScore(ConcreteFrameFinalScore);

            setBuildingListValue.setStructuralComponents(StructuralComponents);
            setBuildingListValue.setNonStructuralComponents(NonStructuralComponents);
            setBuildingListValue.setAncillaryAuxiliaryComponents(AncillaryAuxiliary);
            setBuildingListValue.setBuildingPhotoBase64(BuildingPhotoBase64);

            setBuildingListValue.setRemarks(Remarks);
            setBuildingListValue.setInspectedBy(InspectedBy);
            setBuildingListValue.setDtAdded(sDateNow);

            Cursor cursor = RepositoryNewRVSBuildings.retrieveData(getContext(), EmployeeID);

            if (cursor.getCount() != 0)
            {
                if (cursor.moveToFirst())
                {
                    String ID = cursor.getString(cursor.getColumnIndex("ID"));

                    //Update here..
                    RepositoryNewRVSBuildings.updateNewRVSBuildingData(getContext(), setBuildingListValue, ID);

                    Toast.makeText(getActivity(), "Update Successfully.", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Cursor cursor2 = RepositoryNewRVSBuildings.retrieveData2(getContext(), EmployeeID, setBuildingListValue.getAssetInfoBuildingID());

                if (cursor2.getCount() !=0)
                {
                    if (cursor2.moveToFirst())
                    {
                        String ID = cursor2.getString(cursor2.getColumnIndex("ID"));

                        //Update here..
                        RepositoryNewRVSBuildings.updateNewRVSBuildingData(getContext(), setBuildingListValue, ID);

                        Toast.makeText(getActivity(), "Update Successfully.", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    //Save here..
                    RepositoryNewRVSBuildings.saveNewRVSBuilding(getContext(), setBuildingListValue);

                    Toast.makeText(getActivity(), "Save Successfully.", Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private void initSelectBuilding()
    {
        try
        {
            final AlertDialog.Builder mBuilder = new AlertDialog.Builder(requireContext());

            mBuilder.setCancelable(false);
            LayoutInflater inflater = getLayoutInflater();

            View view = inflater.inflate(R.layout.custom_layout_select_building_rvs, null);

            recyclerView = view.findViewById(R.id.rv_list);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            rvAdapterBuildingInventoryYearList = new RVAdapterBuildingInventoryYearList(getContext(), buildingInventoryYearDataModelList, this);
            recyclerView.setAdapter(rvAdapterBuildingInventoryYearList);
            rvAdapterBuildingInventoryYearList.notifyDataSetChanged();

            ImageView iv_close                      = view.findViewById(R.id.iv_close);
            ImageView iv_search_building_name       = view.findViewById(R.id.iv_search_building_name);
            ImageView iv_clear_search_building_name = view.findViewById(R.id.iv_clear_search_building_name);

            spnr_region             = view.findViewById(R.id.spnr_region);
            spnr_district_office   = view.findViewById(R.id.spnr_district_office);
            spnr_province          = view.findViewById(R.id.spnr_province);
            spnr_city_municipality = view.findViewById(R.id.spnr_city_municipality);
            spnr_barangay          = view.findViewById(R.id.spnr_barangay);

            EditText edt_search_building_name = view.findViewById(R.id.edt_search_building_name);

            ArrayAdapter<String> ARegionArrayList = new ArrayAdapter<>(getActivity(), R.layout.custom_layout_spinner, AllRegionArrayList);
            ARegionArrayList.insert("Select Region", 0);
            ARegionArrayList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnr_region.setAdapter(ARegionArrayList);


            if (AProvincesArrayList != null)
            {
                AProvincesArrayList.clear();
            }
            AProvincesArrayList = new ArrayAdapter<>(getActivity(), R.layout.custom_layout_spinner, AllProvincesArrayList);
            AProvincesArrayList.insert("Select Province", 0);
            AProvincesArrayList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnr_province.setAdapter(AProvincesArrayList);


            if (ACitiesArrayList!= null)
            {
                ACitiesArrayList.clear();
            }
            ACitiesArrayList = new ArrayAdapter<>(getActivity(), R.layout.custom_layout_spinner, AllCitiesArrayList);
            ACitiesArrayList.insert("Select City/Municipality", 0);
            ACitiesArrayList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnr_city_municipality.setAdapter(ACitiesArrayList);


            if (ABarangaysArrayList!= null)
            {
                ABarangaysArrayList.clear();
            }
            ABarangaysArrayList = new ArrayAdapter<>(getActivity(), R.layout.custom_layout_spinner, AllBarangaysArrayList);
            ABarangaysArrayList.insert("Select Barangay", 0);
            ABarangaysArrayList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnr_barangay.setAdapter(ABarangaysArrayList);


            if (ADistrictOfficeArrayList != null)
            {
                ADistrictOfficeArrayList.clear();
            }
            ADistrictOfficeArrayList = new ArrayAdapter<>(getActivity(), R.layout.custom_layout_spinner, AllDistrictArrayList);
            ADistrictOfficeArrayList.insert("Select District", 0);
            ADistrictOfficeArrayList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnr_district_office.setAdapter(ADistrictOfficeArrayList);

            mBuilder.setView(view);
            customizeAlertDialog = mBuilder.create();
            customizeAlertDialog.show();

            InventoryYear        = 0;
            int AccountCode          = 0;
            final int[] DistrictCode = {0};
            final int[] RegionCode   = {0};
            final int[] cityCode     = {0};
            final int[] ProvinceCode = {0};
            final int[] BrgyCode     = {0};
            int StructureType      = 0;
            int BuildingAge        = 0;
            int OccupancyType      = 0;
            String ModuleName      = "null_value";
            String SearchKeyword   = "null_value";
            String tableActionID   = "null_value";
            int page               = 1;

            spnr_region.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    if (!spnr_region.getSelectedItem().toString().equals("Select Region"))
                    {
                        RegionCode[0] = AllRegionCodeArrayList.get(position);

                        initGetProvincesOfEDI(RegionCode[0]);
                        initGETDistrictOfficeOfEDI(RegionCode[0]);

                        rvAdapterBuildingInventoryYearList.notifyDataSetChanged();
                    }
                    else
                    {
                        RegionCode[0] = 0;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent)
                {

                }
            });

            spnr_province.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    if (!spnr_province.getSelectedItem().toString().equalsIgnoreCase("Select Province"))
                    {
                         ProvinceCode[0] = AllProvincesCodeArrayList.get(position);

                         initGetCitiesOfEDI(ProvinceCode[0]);
                    }
                    else
                    {
                        ProvinceCode[0] = 0;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent)
                {

                }
            });

            spnr_city_municipality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    if (!spnr_city_municipality.getSelectedItem().toString().equals("Select City/Municipality"))
                    {
                        cityCode[0] = AllCitiesCodeArrayList.get(position);

                        initGetBarangaysOfEDI(cityCode[0]);
                    }
                    else
                    {
                        cityCode[0] = 0;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent)
                {

                }
            });

            spnr_barangay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    if (!spnr_barangay.getSelectedItem().toString().equals("Select Barangay"))
                    {
                        BrgyCode[0] = AllBarangaysCodeArrayList.get(position);
                    }
                    else
                    {
                        BrgyCode[0] = 0;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent)
                {

                }
            });

            spnr_district_office.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    if (!spnr_district_office.getSelectedItem().toString().equals("Select District"))
                    {
                        DistrictCode[0] = AllDistrictCodeArrayList.get(position);
                    }
                    else
                    {
                        DistrictCode[0] = 0;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent)
                {

                }
            });

            iv_close.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    customizeAlertDialog.dismiss();
                }
            });

            iv_search_building_name.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Log.e(TAG, "InventoryYear: " + InventoryYear + " AccountCode: " + AccountCode + " DistrictCode: " + DistrictCode[0] +
                                    " RegionCode: " + RegionCode[0] + " cityCode: " + cityCode[0] + " ProvinceCode: " + ProvinceCode[0] +
                                    " BrgyCode: " + BrgyCode[0] + " StructureType: " + StructureType + " BuildingAge:" + BuildingAge +
                                    " OccupancyType: " + OccupancyType + " ModuleName: " + ModuleName + " SearchKeyword:" + SearchKeyword +
                                    " tableActionID: "  + tableActionID + " page: "+ page);

                    initGetAllBuildings(0, AccountCode, DistrictCode[0], RegionCode[0], cityCode[0], ProvinceCode[0], BrgyCode[0],
                            StructureType, BuildingAge, OccupancyType, ModuleName, SearchKeyword, tableActionID, page, "Filter View");
                }
            });

            iv_clear_search_building_name.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    edt_search_building_name.setText(null);
                }
            });

            edt_search_building_name.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after)
                {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {

                }

                @Override
                public void afterTextChanged(Editable s)
                {
                    if (s.length() > 0)
                    {
                        iv_clear_search_building_name.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        iv_clear_search_building_name.setVisibility(View.GONE);
                    }
                }
            });
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    public void initFetchBuildingInformation(String assetId)
    {
        if (!haveNetworkConnection(requireContext()))
        {
            handleFailure("No building information is available.");
            return;
        }

        progressDialog.setTitle("Retrieving Building Information...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        APIClientInterface apiInterface = APIClient.getClient2().create(APIClientInterface.class);

        Call<BuildingInventoryYearDataModel> callLogin = apiInterface.GETBuildingInventoryYear(assetId, InventoryYear);
        callLogin.enqueue(new Callback<BuildingInventoryYearDataModel>()
        {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<BuildingInventoryYearDataModel> call, @NonNull Response<BuildingInventoryYearDataModel> response)
            {
                buildingInventoryYearDataModelList.clear();
                if (response.isSuccessful())
                {
                    if (response.body() != null)
                    {
                        progressDialog.dismiss();

                        initSetSelectedBuilding(response.body());
                    }
                    else
                    {
                        handleFailure("No building information is available.");
                    }
                }
                else
                {
                    String Logs = "GET Building Info Failed: " + convertingResponseError(response.errorBody());
                    Log.e(TAG, Logs);
                    handleFailure("No building information is available.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<BuildingInventoryYearDataModel> call, @NonNull Throwable t)
            {
                String Logs = "GET Building Info Failure: " + t.getMessage();
                Log.e(TAG, Logs);
                handleFailure("No building information is available.");
            }
        });
    }

    private void handleFailure(String errorMessage)
    {
        Log.e(TAG, errorMessage);
        progressDialog.dismiss();
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    public void initSetSelectedBuilding(BuildingInventoryYearDataModel buildingInventoryYearDataModel)
    {
        try
        {
            if(customizeAlertDialog != null && customizeAlertDialog.isShowing())
            {
                edt_building_name.setText(buildingInventoryYearDataModel.getBuildingName());
                edt_storeys_no.setText(buildingInventoryYearDataModel.getNoOfFloors());

                String[] fDateFinished =  buildingInventoryYearDataModel.getDateFinished().split("T");
                tv_building_date.setText(FormatDate(fDateFinished[0]));

                //Address
                edt_floor_are_no.setText(buildingInventoryYearDataModel.getNoOfFloors());
                //edt_building_lot.setText(buildingInventoryYearDataModel.getLotArea());
                //edt_building_block.setText(buildingList.getBlockNo());
               // edt_building_street.setText(buildingList.getStreet());
               // edt_sub_division.setText(buildingList.getSubdCompVill());
                //edt_barangay.setText(buildingList.getBarangay());
                //edt_district.setText(buildingList.getDistrict());
                //edt_city.setText(buildingList.getCity());

                edt_owner_name.setText(buildingInventoryYearDataModel.getOwnerName());

               /* if (!buildingInventoryYearDataModel.getRemarks().equals(""))
                {
                    edt_remarks.setText(buildingInventoryYearDataModel.getRemarks());
                }*/

                AssetID     = buildingInventoryYearDataModel.getAssetID();
                AssetIDTemp = AssetID;

                AssetInfoBuildingID = buildingInventoryYearDataModel.getBuildingInfoID();

                String EmployeeID = UserAccount.UserAccountID;
                retrieveDataFromDB(EmployeeID, AssetInfoBuildingID);

                initDisplayAdminSignature();


                BuildingPhotoPath = APIUrls.TEST_PUBLIC_SERVER_BMS + buildingInventoryYearDataModel.getImageUrl();

                Log.e(TAG, "BuildingPhotoPath: " + BuildingPhotoPath);

                if (BuildingPhotoPath != null && !BuildingPhotoPath.equals(""))
                {
                    btn_take_photo.setText("CHANGE PHOTO");

                    btn_view_photo.setVisibility(View.VISIBLE);

                    try
                    {
                        Thread thread = new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                try
                                {
                                    bitmapBuildingPhoto = BitmapFactory.decodeStream((InputStream)new URL(BuildingPhotoPath).getContent());
                                }
                                catch (IOException e)
                                {
                                    Log.e(TAG, e.toString());
                                }
                            }
                        });
                        thread.start();
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }

                customizeAlertDialog.dismiss();

                Toast.makeText(getActivity(), edt_building_name.getText().toString() + " is selected.", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    @SuppressLint("SetTextI18n")
    private void retrieveDataFromDB(String ScreenerID, String AssetInfoBuildingID)
    {
        try
        {
            if (ScreenerID != null)
            {
                Cursor cursor = RepositoryNewRVSBuildings.retrieveData2(getContext(), ScreenerID, AssetInfoBuildingID);

                if (cursor != null && cursor.getCount()!=0)
                {
                    if (cursor.moveToFirst())
                    {
                        AssetIDTemp               = cursor.getString(cursor.getColumnIndex("AssetID"));
                        String BuildingName       = cursor.getString(cursor.getColumnIndex("BuildingName"));
                        String ConstructionStatus = cursor.getString(cursor.getColumnIndex("ConstructionStatus"));
                        String VisitNo            = cursor.getString(cursor.getColumnIndex("VisitNo"));
                        String StoreyNo           = cursor.getString(cursor.getColumnIndex("StoreyNo"));
                        String FloorArea          = cursor.getString(cursor.getColumnIndex("FloorArea"));
                        String Residential        = cursor.getString(cursor.getColumnIndex("Residential"));
                        String BldgPermitNo       = cursor.getString(cursor.getColumnIndex("BldgPermitNo"));
                        String DateBuilt          = cursor.getString(cursor.getColumnIndex("DateBuilt"));
                        String LotArea            = cursor.getString(cursor.getColumnIndex("LotArea"));
                        String BlockNo            = cursor.getString(cursor.getColumnIndex("BlockNo"));
                        String Street             = cursor.getString(cursor.getColumnIndex("Street"));
                        String SubdCompVill       = cursor.getString(cursor.getColumnIndex("SubdCompVill"));
                        String Barangay           = cursor.getString(cursor.getColumnIndex("Barangay"));
                        String District           = cursor.getString(cursor.getColumnIndex("District"));
                        String City               = cursor.getString(cursor.getColumnIndex("City"));
                        String OwnerName          = cursor.getString(cursor.getColumnIndex("OwnerName"));
                        String CompanyName        = cursor.getString(cursor.getColumnIndex("CompanyName"));
                        String PresidentName      = cursor.getString(cursor.getColumnIndex("PresidentName"));

                        String isWoodFrame       = cursor.getString(cursor.getColumnIndex("isWoodFrame"));
                        String isSteelFrame      = cursor.getString(cursor.getColumnIndex("isSteelFrame"));
                        String isConcreteFrame   = cursor.getString(cursor.getColumnIndex("isConcreteFrame"));

                        String BasicScoreWF      =  cursor.getString(cursor.getColumnIndex("BasicScoreWF"));
                        String BasicScoreSF      =  cursor.getString(cursor.getColumnIndex("BasicScoreSF"));
                        String BasicScoreCF      =  cursor.getString(cursor.getColumnIndex("BasicScoreCF"));

                        String isPreCode         =  cursor.getString(cursor.getColumnIndex("isPreCode"));
                        String isPostBenchmark   =  cursor.getString(cursor.getColumnIndex("isPostBenchmark"));
                        String isSoilTypeC2      =  cursor.getString(cursor.getColumnIndex("isSoilTypeC2"));
                        String isSoilTypeD3      =  cursor.getString(cursor.getColumnIndex("isSoilTypeD3"));
                        String isSoilTypeE4      =  cursor.getString(cursor.getColumnIndex("isSoilTypeE4"));

                        String VerticalIrregularityWF = cursor.getString(cursor.getColumnIndex("VerticalIrregularityWF"));
                        String VerticalIrregularitySF = cursor.getString(cursor.getColumnIndex("VerticalIrregularitySF"));
                        String VerticalIrregularityCF = cursor.getString(cursor.getColumnIndex("VerticalIrregularityCF"));
                        String PlanIrregularityWF = cursor.getString(cursor.getColumnIndex("PlanIrregularityWF"));
                        String PlanIrregularitySF = cursor.getString(cursor.getColumnIndex("PlanIrregularitySF"));
                        String PlanIrregularityCF = cursor.getString(cursor.getColumnIndex("PlanIrregularityCF"));
                        String PreCodeWF = cursor.getString(cursor.getColumnIndex("PreCodeWF"));
                        String PreCodeSF = cursor.getString(cursor.getColumnIndex("PreCodeSF"));
                        String PreCodeCF = cursor.getString(cursor.getColumnIndex("PreCodeCF"));
                        String PostBenchmarkWF = cursor.getString(cursor.getColumnIndex("PostBenchmarkWF"));
                        String PostBenchmarkSF = cursor.getString(cursor.getColumnIndex("PostBenchmarkSF"));
                        String PostBenchmarkCF = cursor.getString(cursor.getColumnIndex("PostBenchmarkCF"));
                        String SoilTypeC2WF = cursor.getString(cursor.getColumnIndex("SoilTypeC2WF"));
                        String SoilTypeC2SF = cursor.getString(cursor.getColumnIndex("SoilTypeC2SF"));
                        String SoilTypeC2CF = cursor.getString(cursor.getColumnIndex("SoilTypeC2CF"));
                        String SoilTypeD3WF = cursor.getString(cursor.getColumnIndex("SoilTypeD3WF"));
                        String SoilTypeD3SF = cursor.getString(cursor.getColumnIndex("SoilTypeD3SF"));
                        String SoilTypeD3CF = cursor.getString(cursor.getColumnIndex("SoilTypeD3CF"));

                        String SoilTypeE4WF = cursor.getString(cursor.getColumnIndex("SoilTypeE4WF"));
                        String SoilTypeE4SF = cursor.getString(cursor.getColumnIndex("SoilTypeE4SF"));
                        String SoilTypeE4CF = cursor.getString(cursor.getColumnIndex("SoilTypeE4CF"));

                        String StructuralComponents         = cursor.getString(cursor.getColumnIndex("StructuralComponents"));
                        String NonStructuralComponents      = cursor.getString(cursor.getColumnIndex("NonStructuralComponents"));
                        String AncillaryAuxiliaryComponents = cursor.getString(cursor.getColumnIndex("AncillaryAuxiliaryComponents"));
                        String Remarks                      = cursor.getString(cursor.getColumnIndex("Remarks"));
                        BuildingPhotoBase64                 =  cursor.getString(cursor.getColumnIndex("BuildingPhotoBase64"));
                        setDateNow                          =  cursor.getString(cursor.getColumnIndex("dtAdded"));

                        Log.e(TAG, "PlanIrregularityCF: " + PlanIrregularityCF);

                        edt_building_name.setText(BuildingName);
                        edt_cons_status.setText(ConstructionStatus);
                        edt_visit_no.setText(VisitNo);
                        edt_storeys_no.setText(StoreyNo);
                        edt_floor_are_no.setText(FloorArea);

                        initSetSpinnerValue(Residential, spnr_residential);

                        edt_building_permit_no.setText(BldgPermitNo);
                        tv_building_date.setText(DateBuilt);
                        edt_building_lot.setText(LotArea);
                        edt_building_block.setText(BlockNo);
                        edt_building_street.setText(Street);

                        edt_sub_division.setText(SubdCompVill);
                        edt_barangay.setText(Barangay);
                        edt_district.setText(District);
                        edt_city.setText(City);
                        edt_owner_name.setText(OwnerName);
                        edt_company_name.setText(CompanyName);
                        edt_president_name.setText(PresidentName);

                        cbo_wood_frame.setChecked(isWoodFrame.equalsIgnoreCase("1"));
                        cbo_steel_frame.setChecked(isSteelFrame.equalsIgnoreCase("1"));
                        cbo_concrete_frame.setChecked(isConcreteFrame.equalsIgnoreCase("1"));

                        switch (BasicScoreWF)
                        {
                            case "Low-rise":
                                rbn_low_rise_wf.setChecked(true);
                                break;
                            case "Mid-rise":
                                rbn_mid_rise_wf.setChecked(true);
                                break;
                            case "High-rise":
                                rbn_high_rise_wf.setChecked(true);
                                break;
                        }

                        switch (BasicScoreSF)
                        {
                            case "Low-rise":
                                rbn_low_rise_sf.setChecked(true);
                                break;
                            case "Mid-rise":
                                rbn_mid_rise_sf.setChecked(true);
                                break;
                            case "High-rise":
                                rbn_high_rise_sf.setChecked(true);
                                break;
                        }

                        switch (BasicScoreCF)
                        {
                            case "Low-rise":
                                rbn_low_rise_cf.setChecked(true);
                                break;
                            case "Mid-rise":
                                rbn_mid_rise_cf.setChecked(true);
                                break;
                            case "High-rise":
                                rbn_high_rise_cf.setChecked(true);
                                break;
                        }

                        cbo_pre_code.setChecked(isPreCode.equalsIgnoreCase("1"));
                        cbo_post_benchmark.setChecked(isPostBenchmark.equalsIgnoreCase("1"));
                        cbo_soil_type_c2.setChecked(isSoilTypeC2.equalsIgnoreCase("1"));
                        cbo_soil_type_d3.setChecked(isSoilTypeD3.equalsIgnoreCase("1"));
                        cbo_soil_type_e4.setChecked(isSoilTypeE4.equalsIgnoreCase("1"));

                        //Vertical Irregularity
                        initSetSpinnerValue(VerticalIrregularityWF, spnr_wf_vertical_irregularity);
                        initSetSpinnerValue(VerticalIrregularitySF, spnr_sf_vertical_irregularity);
                        initSetSpinnerValue(VerticalIrregularityCF, spnr_cf_vertical_irregularity);
                        //Plan Irregularity
                        initSetSpinnerValue(PlanIrregularityWF, spnr_wf_plan_irregularity);
                        initSetSpinnerValue(PlanIrregularitySF, spnr_sf_plan_irregularity);
                        initSetSpinnerValue(PlanIrregularityCF, spnr_cf_plan_irregularity);
                        //Pre-code
                        initSetSpinnerValue(PreCodeWF, spnr_wf_pre_code);
                        initSetSpinnerValue(PreCodeSF, spnr_sf_pre_code);
                        initSetSpinnerValue(PreCodeCF, spnr_cf_pre_code);
                        //PostBenchmark
                        initSetSpinnerValue(PostBenchmarkWF, spnr_wf_post_benchmark);
                        initSetSpinnerValue(PostBenchmarkSF, spnr_sf_post_benchmark);
                        initSetSpinnerValue(PostBenchmarkCF, spnr_cf_post_benchmark);
                        //SOIL Type C2
                        initSetSpinnerValue(SoilTypeC2WF, spnr_wf_soil_type_c2);
                        initSetSpinnerValue(SoilTypeC2SF, spnr_sf_soil_type_c2);
                        initSetSpinnerValue(SoilTypeC2CF, spnr_cf_soil_type_c2);
                        //SOIL Type D3
                        initSetSpinnerValue(SoilTypeD3WF, spnr_wf_soil_type_d3);
                        initSetSpinnerValue(SoilTypeD3SF, spnr_sf_soil_type_d3);
                        initSetSpinnerValue(SoilTypeD3CF, spnr_cf_soil_type_d3);

                        //SOIL Type E4
                        initSetSpinnerValue(SoilTypeE4WF, spnr_wf_soil_type_e4);
                        initSetSpinnerValue(SoilTypeE4SF, spnr_sf_soil_type_e4);
                        initSetSpinnerValue(SoilTypeE4CF, spnr_cf_soil_type_e4);


                        edt_structural_components.setText(StructuralComponents);
                        edt_non_structural_components.setText(NonStructuralComponents);
                        edt_ancillary_auxiliary.setText(AncillaryAuxiliaryComponents);
                        edt_remarks.setText(Remarks);

                        if (BuildingPhotoBase64 != null && !BuildingPhotoBase64.equals(""))
                        {
                            byte [] encodeByte = Base64.decode(BuildingPhotoBase64, Base64.DEFAULT);
                            bitmapBuildingPhoto = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                            btn_take_photo.setText("CHANGE PHOTO");
                            btn_view_photo.setVisibility(View.VISIBLE);
                        }

                        isSignatureDisplay = true;
                    }
                }
                else
                {
                    bitmapBuildingPhoto = null;
                    btn_take_photo.setText("TAKE PHOTO");
                    btn_view_photo.setVisibility(View.GONE);

                    isSignatureDisplay = false;
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "ERROR HERE...: " + e.toString());
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



    private void initTakePhoto()
    {
        try
        {
            final CharSequence[] options2 = {"Capture Photo", "Attach Photo"};

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

            if (btn_take_photo.getText().toString().equalsIgnoreCase("TAKE PHOTO") && bitmapBuildingPhoto == null)
            {
                builder.setTitle("Take Photo");
            }
            else
            {
                builder.setTitle("Change Photo");
            }

            builder.setItems(options2, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int item)
                {
                    if (options2[item].equals("Capture Photo"))
                    {
                        ImageName = "Building Image" + "-" + sDateNow;
                        ImageExtension = "png";

                        initGetPictureFromCamera(IMAGE_PICK_CAMERA_CODE);

                        dialog.dismiss();
                    }
                    else if (options2[item].equals("Attach Photo"))
                    {
                        ImageName = "Building Image" + "-" + sDateNow;
                        ImageExtension = "png";

                        initGetPictureGallery(IMAGE_PICK_GALLERY_CODE);

                        dialog.dismiss();
                    }
                }
            });

            builder.setPositiveButton("CANCEL", null);

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
            /*
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, resultCode);
            */

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), resultCode);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initLoadPhoto(Bitmap bitmap, int resultCodeCamera, int ResultCodeGallery)
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

            final AlertDialog.Builder imageDialog = new AlertDialog.Builder(requireContext(), R.style.MyAlertDialogStyle);

            final LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.view_image_dialog_layout, (ViewGroup) ((Activity) requireContext()).findViewById(R.id.layout_root));

            imageDialog.setView(layout);
            imageDialog.setCustomTitle(title);

            ImageView image =  layout.findViewById(R.id.imageView);

            //BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            //Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
            //bitmap = initImageRotateNormal(file, bitmap);

            if (bitmap != null)
            {
                Bitmap bitMapCustomize = Bitmap.createScaledBitmap(bitmap, 1000, 1000, false);
                image.setImageBitmap(bitMapCustomize);
            }
            else
            {

            }

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
                                //file.delete();
                                bitmapBuildingPhoto = null;
                                BuildingPhotoBase64 = "";

                                btn_take_photo.setText("TAKE PHOTO");
                                btn_view_photo.setVisibility(View.GONE);

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

                    initChangePhoto(resultCodeCamera, ResultCodeGallery);
                }
            });

            imageDialog.show();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initChangePhoto(int resultCodeCamera, int resultCodeGallery)
    {
        try
        {
            final CharSequence[] options = {"Capture Photo", "Attach Photo"};

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Change Photo");
            builder.setItems(options, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int item)
                {
                    if (options[item].equals("Capture Photo"))
                    {
                        dialog.dismiss();
                        initGetPictureFromCamera(resultCodeCamera);
                    }
                    else if (options[item].equals("Attach Photo"))
                    {
                        dialog.dismiss();
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



    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        try
        {
            super.onActivityResult(requestCode, resultCode, data);

            if (resultCode == Activity.RESULT_OK)
            {
                Log.e(TAG, "THIS IS WORKING...");

                if (requestCode == IMAGE_PICK_CAMERA_CODE)
                {
                    if (data != null)
                    {
                        try
                        {
                            bitmapBuildingPhoto = (Bitmap) data.getExtras().get("data");

                            if (bitmapBuildingPhoto != null)
                            {
                                btn_take_photo.setText("CHANGE PHOTO");
                                btn_view_photo.setVisibility(View.VISIBLE);
                            }
                        }
                        catch (Exception e)
                        {
                            Log.e(TAG,  e.toString());
                        }
                    }
                }
                else if (requestCode == IMAGE_PICK_GALLERY_CODE)
                {
                    /*imageUri = null;

                    if (data != null)
                    {
                        imageUri = data.getData();
                    }

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
                            }
                        }
                        catch (Exception e)
                        {
                            Log.e(TAG, "Saving Error: " +  e.toString());
                        }
                    }*/

                    if (data != null)
                    {
                        try
                        {
                            Uri imageUri = data.getData();

                            bitmapBuildingPhoto = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);

                            if (bitmapBuildingPhoto != null)
                            {
                                btn_take_photo.setText("CHANGE PHOTO");
                                btn_view_photo.setVisibility(View.VISIBLE);
                            }
                        }
                        catch (Exception e)
                        {
                            Log.e(TAG,  e.toString());
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

                                Glide.with(requireContext()).load(bitmap).into(iv_building_signature);

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

                                    Glide.with(requireContext()).load(bitmap).into(iv_building_signature);
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
            inspectorSignatureClass.setMissionOrderID(AssetIDTemp);
            inspectorSignatureClass.setSignatureID("DESA");

            inspectorSignatureClass.setSignatureName(ImageName);
            inspectorSignatureClass.setSignatureExtension(ImageExtension);
            inspectorSignatureClass.setSignaturePath(ImagePath);
            inspectorSignatureClass.setDtAdded(dateAdded);

            Cursor cursor = RepositoryInspectorSignature.realAllData2(getContext(), UserAccount.UserAccountID, AssetIDTemp, "New RVS Signature");

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


                    String ImagePath = SaveFolderName + "/" + ImageName + "." + "png";

                    File file = new File(SaveFolderName, ImageName);

                    try //FILE TO PNG.
                    {
                        FileOutputStream out = new FileOutputStream(file + "." + "png");

                        imageToSave.compress(Bitmap.CompressFormat.PNG, 100, out);

                        out.flush();
                        out.close();
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, "Saving Image: " + e.toString());
                    }

                    initSaveSignaturePhoto();
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



    private void initGetRegionsOfEDI()
    {
        if (!haveNetworkConnection(requireContext()))
        {
            return;
        }

        APIClientInterface apiInterface = APIClient.getClient2().create(APIClientInterface.class);
        Call<List<AllRegionsOfEDIModel>> callLogin = apiInterface.GETAllRegions();

        callLogin.enqueue(new Callback<List<AllRegionsOfEDIModel>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<AllRegionsOfEDIModel>> call, @NonNull Response<List<AllRegionsOfEDIModel>> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    AllRegionCodeArrayList.clear();
                    AllRegionCodeArrayList.add(0);
                    AllRegionArrayList.clear();

                    final List<AllRegionsOfEDIModel> allRegionsOfEDIModelList = response.body();

                    for (int i = 0; i < allRegionsOfEDIModelList.size(); i++)
                    {
                        AllRegionCodeArrayList.add(allRegionsOfEDIModelList.get(i).getRegCode());
                        AllRegionArrayList.add(allRegionsOfEDIModelList.get(i).getRegDesc());
                    }
                }
                else
                {
                    String Logs = response.isSuccessful() ? "Get Regions: Server Response Null" : "Get Regions Failed: " +
                            convertingResponseError(response.errorBody());

                    Log.e(TAG, Logs);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AllRegionsOfEDIModel>> call, @NonNull Throwable t)
            {
                String Logs = "Get Regions Failure: " + t.getMessage();
                Log.e(TAG, Logs);
            }
        });
    }

    private void initGetProvincesOfEDI(Integer regCode)
    {
        Log.e(TAG, "regCode:" + regCode);

        if (!haveNetworkConnection(requireContext()))
        {
            return;
        }

        APIClientInterface apiInterface = APIClient.getClient2().create(APIClientInterface.class);
        Call<List<AllProvincesOfEDIModel>> callLogin = apiInterface.GETAllProvinces(regCode);

        callLogin.enqueue(new Callback<List<AllProvincesOfEDIModel>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<AllProvincesOfEDIModel>> call, @NonNull Response<List<AllProvincesOfEDIModel>> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    AllProvincesCodeArrayList.clear();
                    AllProvincesCodeArrayList.add(0);
                    AllProvincesArrayList.clear();

                    final List<AllProvincesOfEDIModel> allProvincesOfEDIModelList = response.body();

                    for (int i = 0; i < allProvincesOfEDIModelList.size(); i++)
                    {
                        if (!allProvincesOfEDIModelList.get(i).getProvDesc().equals("") ||
                            !allProvincesOfEDIModelList.get(i).getProvDesc().equals(" "))
                        {
                            AllProvincesCodeArrayList.add(allProvincesOfEDIModelList.get(i).getProvCode());
                            AllProvincesArrayList.add(allProvincesOfEDIModelList.get(i).getProvDesc());
                        }
                    }

                    LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>(AllProvincesArrayList);
                    AllProvincesArrayList.clear();
                    AllProvincesArrayList.addAll(linkedHashSet);

                    AProvincesArrayList = new ArrayAdapter<>(getActivity(), R.layout.custom_layout_spinner, AllProvincesArrayList);
                    AProvincesArrayList.insert("Select Province", 0);
                    AProvincesArrayList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnr_province.setAdapter(AProvincesArrayList);
                }
                else
                {
                    String Logs = "Get Provinces: Server Response Null";
                    Log.e(TAG, Logs);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AllProvincesOfEDIModel>> call, @NonNull Throwable t)
            {
                String Logs = "Get Provinces Failure: " + t.getMessage();
                Log.e(TAG, Logs);
            }
        });
    }

    private void initGETDistrictOfficeOfEDI(Integer regCode)
    {
        if (!haveNetworkConnection(requireContext()))
        {
            return;
        }

        APIClientInterface apiInterface = APIClient.getClient2().create(APIClientInterface.class);
        Call<List<AllDistrictOfficesOfEDIModel>> callLogin = apiInterface.GETAllDistricts(regCode);

        callLogin.enqueue(new Callback<List<AllDistrictOfficesOfEDIModel>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<AllDistrictOfficesOfEDIModel>> call, @NonNull Response<List<AllDistrictOfficesOfEDIModel>> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    AllDistrictCodeArrayList.clear();
                    AllDistrictCodeArrayList.add(0);
                    AllDistrictArrayList.clear();

                    final List<AllDistrictOfficesOfEDIModel> allDistrictOfficesOfEDIModelList = response.body();

                    for (int i = 0; i < allDistrictOfficesOfEDIModelList.size(); i++)
                    {
                        if (!allDistrictOfficesOfEDIModelList.get(i).getDistrictOffice1().equals("") ||
                            !allDistrictOfficesOfEDIModelList.get(i).getDistrictOffice1().equals(" "))
                        {
                            AllDistrictCodeArrayList.add(allDistrictOfficesOfEDIModelList.get(i).getRegCode());
                            AllDistrictArrayList.add(allDistrictOfficesOfEDIModelList.get(i).getDistrictOffice1());
                        }
                    }

                    LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>(AllDistrictArrayList);
                    AllDistrictArrayList.clear();
                    AllDistrictArrayList.addAll(linkedHashSet);

                    ADistrictOfficeArrayList = new ArrayAdapter<>(getActivity(), R.layout.custom_layout_spinner, AllDistrictArrayList);
                    ADistrictOfficeArrayList.insert("Select District", 0);
                    ADistrictOfficeArrayList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnr_district_office.setAdapter(ADistrictOfficeArrayList);
                }
                else
                {
                    String Logs = "GET All District Offices : Server Response Null";
                    Log.e(TAG, Logs);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AllDistrictOfficesOfEDIModel>> call, @NonNull Throwable t)
            {
                String Logs = "GET All District Offices Failure: " + t.getMessage();
                Log.e(TAG, Logs);
            }
        });
    }


    private void initGetCitiesOfEDI(Integer provCode)
    {
        Log.e(TAG, "provCode: " + provCode);

        if (!haveNetworkConnection(requireContext()))
        {
            return;
        }

        APIClientInterface apiInterface = APIClient.getClient2().create(APIClientInterface.class);
        Call<List<AllCitiesOfEDIModel>> callLogin = apiInterface.GETAllCities(provCode);

        callLogin.enqueue(new Callback<List<AllCitiesOfEDIModel>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<AllCitiesOfEDIModel>> call, @NonNull Response<List<AllCitiesOfEDIModel>> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    AllCitiesCodeArrayList.clear();
                    AllCitiesCodeArrayList.add(0);
                    AllCitiesArrayList.clear();

                    final List<AllCitiesOfEDIModel> allCitiesOfEDIModelList = response.body();

                    for (int i = 0; i < allCitiesOfEDIModelList.size(); i++)
                    {
                        if (!allCitiesOfEDIModelList.get(i).getCitymunDesc().equals("") || !allCitiesOfEDIModelList.get(i).getCitymunDesc().equals(" "))
                        {
                            AllCitiesCodeArrayList.add(allCitiesOfEDIModelList.get(i).getCitymunCode());
                            AllCitiesArrayList.add(allCitiesOfEDIModelList.get(i).getCitymunDesc());
                        }
                    }

                    LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>(AllCitiesArrayList);
                    AllCitiesArrayList.clear();
                    AllCitiesArrayList.addAll(linkedHashSet);

                    ACitiesArrayList = new ArrayAdapter<>(getActivity(), R.layout.custom_layout_spinner, AllCitiesArrayList);
                    ACitiesArrayList.insert("Select City/Municipality", 0);
                    ACitiesArrayList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnr_city_municipality.setAdapter(ACitiesArrayList);
                }
                else
                {
                    String Logs = "Get Cities: Server Response Null";
                    Log.e(TAG, Logs);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AllCitiesOfEDIModel>> call, @NonNull Throwable t)
            {
                String Logs = "Get Cities Failure: " + t.getMessage();
                Log.e(TAG, Logs);
            }
        });
    }

    private void initGetBarangaysOfEDI(Integer cityCode)
    {
        if (!haveNetworkConnection(requireContext()))
        {
            return;
        }

        APIClientInterface apiInterface = APIClient.getClient2().create(APIClientInterface.class);
        Call<List<AllBarangaysOfEDIModel>> callLogin = apiInterface.GETAllBarangays(cityCode);

        callLogin.enqueue(new Callback<List<AllBarangaysOfEDIModel>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<AllBarangaysOfEDIModel>> call, @NonNull Response<List<AllBarangaysOfEDIModel>> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    AllBarangaysCodeArrayList.clear();
                    AllBarangaysCodeArrayList.add(0);
                    AllBarangaysArrayList.clear();

                    final List<AllBarangaysOfEDIModel> allProvincesOfEDIModelList = response.body();

                    for (int i = 0; i < allProvincesOfEDIModelList.size(); i++)
                    {
                        if (!allProvincesOfEDIModelList.get(i).getBrgyDesc().equals("") ||
                                !allProvincesOfEDIModelList.get(i).getBrgyDesc().equals(" "))
                        {
                            AllBarangaysCodeArrayList.add(allProvincesOfEDIModelList.get(i).getBrgyCode());
                            AllBarangaysArrayList.add(allProvincesOfEDIModelList.get(i).getBrgyDesc());
                        }
                    }

                    LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>(AllBarangaysArrayList);
                    AllBarangaysArrayList.clear();
                    AllBarangaysArrayList.addAll(linkedHashSet);

                    ABarangaysArrayList = new ArrayAdapter<>(getActivity(), R.layout.custom_layout_spinner, AllBarangaysArrayList);
                    ABarangaysArrayList.insert("Select Barangay", 0);
                    ABarangaysArrayList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnr_barangay.setAdapter(ABarangaysArrayList);
                }
                else
                {
                    String Logs = "Get Barangays: Server Response Null";
                    Log.e(TAG, Logs);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AllBarangaysOfEDIModel>> call, @NonNull Throwable t)
            {
                String Logs = "Get Barangays Failure: " + t.getMessage();
                Log.e(TAG, Logs);
            }
        });
    }

    private void initGetAllBuildings(int InventoryYear, int AccountCode, int District, int Region, int City, int Province, int Barangay, int StructureType,
                                     int BuildingAge, int OccupancyType, String ModuleName, String SearchKeyword, String tableActionID, int page, String Option)
    {
        try
        {
            if (haveNetworkConnection(requireContext()))
            {
                if (Option.equalsIgnoreCase("Filter View"))
                {
                    progressDialog.setTitle("Updating Buildings...");
                }
                else
                {
                    progressDialog.setTitle("Retrieving Buildings...");
                }

                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.show();

                APIClientInterface apiInterface = APIClient.getClient2().create(APIClientInterface.class);

                Call<BuildingInfoTableModel> callLogin = apiInterface.GETAllBuildingsInventoryYear(0, AccountCode, District,
                        Region, City, Province, Barangay, StructureType, BuildingAge, OccupancyType, ModuleName, SearchKeyword, tableActionID, page);

                callLogin.enqueue(new Callback<BuildingInfoTableModel>()
                {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(@NonNull Call<BuildingInfoTableModel> call, @NonNull Response<BuildingInfoTableModel> response)
                    {
                        buildingInventoryYearDataModelList.clear();

                        if (response.isSuccessful())
                        {
                            if (response.body() != null)
                            {
                                progressDialog.dismiss();
                                buildingInventoryYearDataModelList = response.body().getBuildingInventoryYearDM();

                                if (Option.equalsIgnoreCase("Normal View"))
                                {
                                    initSelectBuilding();
                                }
                            }
                            else
                            {
                                String Logs = "GET All Building: Server Response Null";

                                Log.e(TAG, Logs);
                                progressDialog.dismiss();

                                Toast.makeText(getActivity(), "No building is available..", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            String Logs = "GET All Building Failed: " + convertingResponseError(response.errorBody());

                            Log.e(TAG, Logs);
                            progressDialog.dismiss();

                            Toast.makeText(getActivity(), "No building is available.", Toast.LENGTH_SHORT).show();
                        }

                        if (Option.equalsIgnoreCase("Filter View"))
                        {
                            Log.e(TAG, "notifyDataSetChanged - " + buildingInventoryYearDataModelList.size());

                            initFilterBuildingList("");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<BuildingInfoTableModel> call, @NonNull Throwable t)
                    {
                        String Logs = "GET All Building Failure: " + t.getMessage();

                        Log.e(TAG, Logs);
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "No building is available. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "ERROR 3: " +  e.getMessage());

            progressDialog.dismiss();
            Toast.makeText(getActivity(), "No building is available. Please try again.", Toast.LENGTH_SHORT).show();
        }
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

    private void initFilterBuildingList(String text)
    {
        ArrayList<BuildingInventoryYearDataModel> filteredList = new ArrayList<>();

        for (BuildingInventoryYearDataModel item : buildingInventoryYearDataModelList)
        {
            if (item.getBuildingName().toLowerCase().contains(text.toLowerCase()))
            {
                filteredList.add(item);
            }
            else if (item.getLocation().toLowerCase().contains(text.toLowerCase()))
            {
                filteredList.add(item);
            }
        }
        rvAdapterBuildingInventoryYearList.filterList(filteredList);
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

                    if (ActivityCompat.checkSelfPermission(requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                            (requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
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



    @Override
    public void onResume()
    {
        super.onResume();

        initDisplayAdminSignature();
    }

    @SuppressLint("SetTextI18n")
    private void initCallImageSaved()
    {
        try
        {
            String ImageName_Building_Image = "Building Image";
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
        }
        catch (Exception e)
        {
            Log.e(TAG, "ERROR 2: " +  e.toString());
        }
    }

    private void initDisplayAdminSignature()
    {
        try
        {
            Cursor cursor2 = RepositoryInspectorSignature.realAllData(getContext(),
                    "New RVS Signature", AssetIDTemp);

            if (cursor2.getCount()!=0)
            {
                if (cursor2.moveToFirst())
                {
                    String SignaturePath = cursor2.getString(cursor2.getColumnIndex("SignaturePath"));

                    File file = new File(SignaturePath);

                    if (file.exists())
                    {
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        Bitmap bitmap = BitmapFactory.decodeFile(SignaturePath, bmOptions);
                        //bitmap = initImageRotateNormal(file, bitmap);

                        bmInspectorSignature = bitmap;

                        iv_remove_building_signature.setVisibility(View.VISIBLE);
                        Glide.with(requireContext()).load(bitmap).into(iv_building_signature);

                        iv_remove_building_signature.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
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
                                                UserAccount.UserAccountID, "0", "New RVS Signature");

                                        boolean isDelete = file.delete(); //Delete the image.

                                        bmInspectorSignature = null;

                                        initDisplayAdminSignature();
                                    }
                                });
                                ADSettings.show();
                            }
                        });
                    }
                    else
                    {
                        iv_remove_building_signature.setVisibility(View.GONE);
                        iv_building_signature.setImageBitmap(null);

                        bmInspectorSignature = null;
                    }
                }
            }
            else
            {
                iv_remove_building_signature.setVisibility(View.GONE);
                iv_building_signature.setImageBitmap(null);

                bmInspectorSignature = null;
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "ERROR 1: " + e.toString());
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        initRemoveDisplayAdminSignature();
    }

    private void initRemoveDisplayAdminSignature()
    {
        try
        {
            Log.e(TAG, "ON-DESTROY CALLED");

            if (!isSignatureDisplay)
            {
                Cursor cursor = RepositoryInspectorSignature.realAllData(getContext(),
                        "New RVS Signature", "0");

                if (cursor.getCount()!=0)
                {
                    if (cursor.moveToFirst())
                    {
                        String SignaturePath = cursor.getString(cursor.getColumnIndex("SignaturePath"));

                        File file = new File(SignaturePath);

                        if (file.exists())
                        {
                            RepositoryInspectorSignature.removeInspectorSignature(getContext(),
                                    UserAccount.UserAccountID, "0", "New RVS Signature");

                            boolean isDelete = file.delete(); //Delete the image.

                            initDisplayAdminSignature();
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

    public void loadMarker(String yLatitude, String xLongitude)
    {
        LatLng newLatLng = new LatLng(Double.parseDouble(yLatitude), Double.parseDouble(xLongitude));

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(newLatLng);

        mMap.clear();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLatLng, DEFAULT_ZOOM));
        mMap.addMarker(markerOptions);
    }


    private void initSpinnersListeners()
    {
        try
        {
            //region Wood Frame Scoring

            final int[] iWoodFrameScoring =
            {
                    spnr_wf_vertical_irregularity.getSelectedItemPosition(),
                    spnr_wf_plan_irregularity.getSelectedItemPosition(),
                    spnr_wf_pre_code.getSelectedItemPosition(),
                    spnr_wf_post_benchmark.getSelectedItemPosition(),
                    spnr_wf_soil_type_c2.getSelectedItemPosition(),
                    spnr_wf_soil_type_d3.getSelectedItemPosition(),
                    spnr_wf_soil_type_e4.getSelectedItemPosition()
            };

            spnr_wf_vertical_irregularity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                {
                    if (iWoodFrameScoring[0] != i)
                    {
                        WoodFrameFinalScore();
                    }
                    iWoodFrameScoring[0] = i;
                }
                public void onNothingSelected(AdapterView<?> adapterView)
                {

                }
            });

            spnr_wf_plan_irregularity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                {
                    if (iWoodFrameScoring[1] != i)
                    {
                        WoodFrameFinalScore();
                    }
                    iWoodFrameScoring[1] = i;
                }
                public void onNothingSelected(AdapterView<?> adapterView)
                {

                }
            });

            spnr_wf_pre_code.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                {
                    if (iWoodFrameScoring[2] != i)
                    {
                        WoodFrameFinalScore();
                    }
                    iWoodFrameScoring[2] = i;
                }
                public void onNothingSelected(AdapterView<?> adapterView)
                {

                }
            });

            spnr_wf_post_benchmark.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                {
                    if (iWoodFrameScoring[3] != i)
                    {
                        WoodFrameFinalScore();
                    }
                    iWoodFrameScoring[3] = i;
                }
                public void onNothingSelected(AdapterView<?> adapterView)
                {

                }
            });

            spnr_wf_soil_type_c2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                {
                    if (iWoodFrameScoring[4] != i)
                    {
                        WoodFrameFinalScore();
                    }
                    iWoodFrameScoring[4] = i;
                }
                public void onNothingSelected(AdapterView<?> adapterView)
                {

                }
            });

            spnr_wf_soil_type_d3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                {
                    if (iWoodFrameScoring[5] != i)
                    {
                        WoodFrameFinalScore();
                    }
                    iWoodFrameScoring[5] = i;
                }
                public void onNothingSelected(AdapterView<?> adapterView)
                {

                }
            });

            spnr_wf_soil_type_e4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                {
                    if (iWoodFrameScoring[6] != i)
                    {
                        WoodFrameFinalScore();
                    }
                    iWoodFrameScoring[6] = i;
                }
                public void onNothingSelected(AdapterView<?> adapterView)
                {

                }
            });

            //endregion

            //region Steel Frame Scoring

            final int[] iSteelFrameScoring =
                    {
                            spnr_sf_vertical_irregularity.getSelectedItemPosition(),
                            spnr_sf_plan_irregularity.getSelectedItemPosition(),
                            spnr_sf_pre_code.getSelectedItemPosition(),
                            spnr_sf_post_benchmark.getSelectedItemPosition(),
                            spnr_sf_soil_type_c2.getSelectedItemPosition(),
                            spnr_sf_soil_type_d3.getSelectedItemPosition(),
                            spnr_sf_soil_type_e4.getSelectedItemPosition()
                    };

            spnr_sf_vertical_irregularity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                {
                    if (iSteelFrameScoring[0] != i)
                    {
                        SteelFrameFinalScore();
                    }
                    iSteelFrameScoring[0] = i;
                }
                public void onNothingSelected(AdapterView<?> adapterView)
                {

                }
            });

            spnr_sf_plan_irregularity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                {
                    if (iSteelFrameScoring[1] != i)
                    {
                        SteelFrameFinalScore();
                    }
                    iSteelFrameScoring[1] = i;
                }
                public void onNothingSelected(AdapterView<?> adapterView)
                {

                }
            });

            spnr_sf_pre_code.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                {
                    if (iSteelFrameScoring[2] != i)
                    {
                        SteelFrameFinalScore();
                    }
                    iSteelFrameScoring[2] = i;
                }
                public void onNothingSelected(AdapterView<?> adapterView)
                {

                }
            });

            spnr_sf_post_benchmark.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                {
                    if (iSteelFrameScoring[3] != i)
                    {
                        SteelFrameFinalScore();
                    }
                    iSteelFrameScoring[3] = i;
                }
                public void onNothingSelected(AdapterView<?> adapterView)
                {

                }
            });

            spnr_sf_soil_type_c2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                {
                    if (iSteelFrameScoring[4] != i)
                    {
                        SteelFrameFinalScore();
                    }
                    iSteelFrameScoring[4] = i;
                }
                public void onNothingSelected(AdapterView<?> adapterView)
                {

                }
            });

            spnr_sf_soil_type_d3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                {
                    if (iSteelFrameScoring[5] != i)
                    {
                        SteelFrameFinalScore();
                    }
                    iSteelFrameScoring[5] = i;
                }
                public void onNothingSelected(AdapterView<?> adapterView)
                {

                }
            });

            spnr_sf_soil_type_e4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                {
                    if (iSteelFrameScoring[6] != i)
                    {
                        SteelFrameFinalScore();
                    }
                    iSteelFrameScoring[6] = i;
                }
                public void onNothingSelected(AdapterView<?> adapterView)
                {

                }
            });

            //endregion

            //region Steel Frame Scoring

            final int[] iConcreteFrameScoring =
                    {
                            spnr_cf_vertical_irregularity.getSelectedItemPosition(),
                            spnr_cf_plan_irregularity.getSelectedItemPosition(),
                            spnr_cf_pre_code.getSelectedItemPosition(),
                            spnr_cf_post_benchmark.getSelectedItemPosition(),
                            spnr_cf_soil_type_c2.getSelectedItemPosition(),
                            spnr_cf_soil_type_d3.getSelectedItemPosition(),
                            spnr_cf_soil_type_e4.getSelectedItemPosition()
                    };

            spnr_cf_vertical_irregularity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                {
                    if (iConcreteFrameScoring[0] != i)
                    {
                        ConcreteFrameFinalScore();
                    }
                    iConcreteFrameScoring[0] = i;
                }
                public void onNothingSelected(AdapterView<?> adapterView)
                {

                }
            });

            spnr_cf_plan_irregularity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                {
                    if (iConcreteFrameScoring[1] != i)
                    {
                        ConcreteFrameFinalScore();
                    }
                    iConcreteFrameScoring[1] = i;
                }
                public void onNothingSelected(AdapterView<?> adapterView)
                {

                }
            });

            spnr_cf_pre_code.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                {
                    if (iConcreteFrameScoring[2] != i)
                    {
                        ConcreteFrameFinalScore();
                    }
                    iConcreteFrameScoring[2] = i;
                }
                public void onNothingSelected(AdapterView<?> adapterView)
                {

                }
            });

            spnr_cf_post_benchmark.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                {
                    if (iConcreteFrameScoring[3] != i)
                    {
                        ConcreteFrameFinalScore();
                    }
                    iConcreteFrameScoring[3] = i;
                }
                public void onNothingSelected(AdapterView<?> adapterView)
                {

                }
            });

            spnr_cf_soil_type_c2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                {
                    if (iConcreteFrameScoring[4] != i)
                    {
                        ConcreteFrameFinalScore();
                    }
                    iConcreteFrameScoring[4] = i;
                }
                public void onNothingSelected(AdapterView<?> adapterView)
                {

                }
            });

            spnr_cf_soil_type_d3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                {
                    if (iConcreteFrameScoring[5] != i)
                    {
                        ConcreteFrameFinalScore();
                    }
                    iConcreteFrameScoring[5] = i;
                }
                public void onNothingSelected(AdapterView<?> adapterView)
                {

                }
            });

            spnr_cf_soil_type_e4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                {
                    if (iConcreteFrameScoring[6] != i)
                    {
                        ConcreteFrameFinalScore();
                    }
                    iConcreteFrameScoring[6] = i;
                }
                public void onNothingSelected(AdapterView<?> adapterView)
                {

                }
            });

            //endregion

        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    public void WoodFrameFinalScore()
    {
        try
        {
            double dVerticalIrregularity;
            double dPlanIrregularity;
            double dPreCode;
            double dPostBenchmark;
            double dSoilTypeC2;
            double dSoilTypeD2;
            double dSoilTypeE4;
            double WoodFrameFinalScore = 0.0;

            if (spnr_wf_vertical_irregularity.getSelectedItem().toString() != null)
            {
                dVerticalIrregularity = Double.parseDouble(spnr_wf_vertical_irregularity.getSelectedItem().toString());

                WoodFrameFinalScore = WoodFrameFinalScore + dVerticalIrregularity;
            }
            if (spnr_wf_plan_irregularity.getSelectedItem().toString() != null)
            {
                dPlanIrregularity = Double.parseDouble(spnr_wf_plan_irregularity.getSelectedItem().toString());

                WoodFrameFinalScore = WoodFrameFinalScore + dPlanIrregularity;
            }
            if (spnr_wf_pre_code.getSelectedItem().toString() != null)
            {
                dPreCode = Double.parseDouble(spnr_wf_pre_code.getSelectedItem().toString());

                WoodFrameFinalScore = WoodFrameFinalScore + dPreCode;
            }
            if (spnr_wf_post_benchmark.getSelectedItem().toString() != null)
            {
                dPostBenchmark = Double.parseDouble(spnr_wf_post_benchmark.getSelectedItem().toString());

                WoodFrameFinalScore = WoodFrameFinalScore + dPostBenchmark;
            }
            if (spnr_wf_soil_type_c2.getSelectedItem().toString() != null)
            {
                dSoilTypeC2 = Double.parseDouble(spnr_wf_soil_type_c2.getSelectedItem().toString());

                WoodFrameFinalScore = WoodFrameFinalScore + dSoilTypeC2;
            }
            if (spnr_wf_soil_type_d3.getSelectedItem().toString() != null)
            {
                dSoilTypeD2 = Double.parseDouble(spnr_wf_soil_type_d3.getSelectedItem().toString());

                WoodFrameFinalScore = WoodFrameFinalScore + dSoilTypeD2;
            }
            if (spnr_wf_soil_type_e4.getSelectedItem().toString() != null)
            {
                dSoilTypeE4 = Double.parseDouble(spnr_wf_soil_type_e4.getSelectedItem().toString());

                WoodFrameFinalScore = WoodFrameFinalScore + dSoilTypeE4;
            }

            DecimalFormat df = new DecimalFormat("0.00");
            df.setRoundingMode(RoundingMode.UP);

            tv_final_score_wf.setText(df.format(WoodFrameFinalScore));
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    public void SteelFrameFinalScore()
    {
        try
        {
            double dVerticalIrregularity;
            double dPlanIrregularity;
            double dPreCode;
            double dPostBenchmark;
            double dSoilTypeC2;
            double dSoilTypeD3;
            double dSoilTypeE2;
            double SteelFrameFinalScore = 0.0;

            if (spnr_sf_vertical_irregularity.getSelectedItem().toString() != null)
            {
                dVerticalIrregularity = Double.parseDouble(spnr_sf_vertical_irregularity.getSelectedItem().toString());

                SteelFrameFinalScore = SteelFrameFinalScore + dVerticalIrregularity;
            }
            if (spnr_sf_plan_irregularity.getSelectedItem().toString() != null)
            {
                dPlanIrregularity = Double.parseDouble(spnr_sf_plan_irregularity.getSelectedItem().toString());

                SteelFrameFinalScore = SteelFrameFinalScore + dPlanIrregularity;
            }
            if (spnr_sf_pre_code.getSelectedItem().toString() != null)
            {
                dPreCode = Double.parseDouble(spnr_sf_pre_code.getSelectedItem().toString());

                SteelFrameFinalScore = SteelFrameFinalScore + dPreCode;
            }
            if (spnr_sf_post_benchmark.getSelectedItem().toString() != null)
            {
                dPostBenchmark = Double.parseDouble(spnr_sf_post_benchmark.getSelectedItem().toString());

                SteelFrameFinalScore = SteelFrameFinalScore + dPostBenchmark;
            }
            if (spnr_sf_soil_type_c2.getSelectedItem().toString() != null)
            {
                dSoilTypeC2 = Double.parseDouble(spnr_sf_soil_type_c2.getSelectedItem().toString());

                SteelFrameFinalScore = SteelFrameFinalScore + dSoilTypeC2;
            }
            if (spnr_sf_soil_type_d3.getSelectedItem().toString() != null)
            {
                dSoilTypeD3 = Double.parseDouble(spnr_sf_soil_type_d3.getSelectedItem().toString());

                SteelFrameFinalScore = SteelFrameFinalScore + dSoilTypeD3;
            }
            if (spnr_sf_soil_type_e4.getSelectedItem().toString() != null)
            {
                dSoilTypeE2 = Double.parseDouble(spnr_sf_soil_type_e4.getSelectedItem().toString());

                SteelFrameFinalScore = SteelFrameFinalScore + dSoilTypeE2;
            }

            DecimalFormat df = new DecimalFormat("0.00");
            df.setRoundingMode(RoundingMode.UP);

            tv_final_score_st.setText(df.format(SteelFrameFinalScore));
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    public void ConcreteFrameFinalScore()
    {
        try
        {
            double dVerticalIrregularity;
            double dPlanIrregularity;
            double dPreCode;
            double dPostBenchmark;
            double dSoilTypeC2;
            double dSoilTypeD2;
            double dSoilTypeE2;
            double ConcreteFrameFinalScore = 0.0;

            if (spnr_cf_vertical_irregularity.getSelectedItem().toString() != null)
            {
                dVerticalIrregularity = Double.parseDouble(spnr_cf_vertical_irregularity.getSelectedItem().toString());

                ConcreteFrameFinalScore = ConcreteFrameFinalScore + dVerticalIrregularity;
            }
            if (spnr_cf_plan_irregularity.getSelectedItem().toString() != null)
            {
                dPlanIrregularity = Double.parseDouble(spnr_cf_plan_irregularity.getSelectedItem().toString());

                ConcreteFrameFinalScore = ConcreteFrameFinalScore + dPlanIrregularity;
            }
            if (spnr_cf_pre_code.getSelectedItem().toString() != null)
            {
                dPreCode = Double.parseDouble(spnr_cf_pre_code.getSelectedItem().toString());

                ConcreteFrameFinalScore = ConcreteFrameFinalScore + dPreCode;
            }
            if (spnr_cf_post_benchmark.getSelectedItem().toString() != null)
            {
                dPostBenchmark = Double.parseDouble(spnr_cf_post_benchmark.getSelectedItem().toString());

                ConcreteFrameFinalScore = ConcreteFrameFinalScore + dPostBenchmark;
            }
            if (spnr_cf_soil_type_c2.getSelectedItem().toString() != null)
            {
                dSoilTypeC2 = Double.parseDouble(spnr_cf_soil_type_c2.getSelectedItem().toString());

                ConcreteFrameFinalScore = ConcreteFrameFinalScore + dSoilTypeC2;
            }
            if (spnr_cf_soil_type_d3.getSelectedItem().toString() != null)
            {
                dSoilTypeD2 = Double.parseDouble(spnr_cf_soil_type_d3.getSelectedItem().toString());

                ConcreteFrameFinalScore = ConcreteFrameFinalScore + dSoilTypeD2;
            }
            if (spnr_cf_soil_type_e4.getSelectedItem().toString() != null)
            {
                dSoilTypeE2 = Double.parseDouble(spnr_cf_soil_type_e4.getSelectedItem().toString());

                ConcreteFrameFinalScore = ConcreteFrameFinalScore + dSoilTypeE2;
            }

            DecimalFormat df = new DecimalFormat("0.00");
            df.setRoundingMode(RoundingMode.UP);

            tv_final_score_cf.setText(df.format(ConcreteFrameFinalScore));
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
            @SuppressLint("SimpleDateFormat") DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            @SuppressLint("SimpleDateFormat") DateFormat outputFormat  = new SimpleDateFormat("MM/dd/yyyy");

            try
            {
                Date convertedDate = inputFormat.parse(strDate);

                if (convertedDate != null)
                {
                    datetime = outputFormat.format(convertedDate);
                }
                else
                {
                    datetime = "";
                }
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
}
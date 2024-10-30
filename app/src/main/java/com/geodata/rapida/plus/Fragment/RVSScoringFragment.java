package com.geodata.rapida.plus.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.geodata.rapida.plus.Activity.AttachOtherImagesActivity;
import com.geodata.rapida.plus.Activity.BuildingScoreActivity;
import com.geodata.rapida.plus.Activity.CreateSignatureActivity;
import com.geodata.rapida.plus.Activity.PreviewReportRVSScoringActivity;
import com.geodata.rapida.plus.Activity.SketchPadActivity;
import com.geodata.rapida.plus.Adapter.RVAdapterBuildingImages;
import com.geodata.rapida.plus.Adapter.RVAdapterFallingHazards;
import com.geodata.rapida.plus.Adapter.RVAdapterImages;
import com.geodata.rapida.plus.Adapter.RVAdapterOccupancies;
import com.geodata.rapida.plus.Adapter.RVAdapterScreenerSignature;
import com.geodata.rapida.plus.Adapter.RVAdapterSketchImages;
import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.Retrofit.Model.AssignedInspectorsListModel;
import com.geodata.rapida.plus.Retrofit.Model.FallingHazardsModel;
import com.geodata.rapida.plus.Retrofit.Model.OccupanciesModel;
import com.geodata.rapida.plus.SQLite.Class.ImagesClass;
import com.geodata.rapida.plus.SQLite.Class.InspectorSignatureClass;
import com.geodata.rapida.plus.SQLite.Class.RVSSaveDraftDataClass;
import com.geodata.rapida.plus.SQLite.Class.SelectedFallingHazardsClass;
import com.geodata.rapida.plus.SQLite.Class.SelectedOccupancyClass;
import com.geodata.rapida.plus.SQLite.Class.SketchImagesClass;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryFallingHazards;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryFinalBuildingScores;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryImages;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryInspectorSignature;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryNoOfPersons;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryOccupancies;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryOnlineAssignedInspectors;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryOnlineBuildingInformation;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryOnlineBuildingOccupancyList;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryOnlineMissionOrders;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryRVSSaveAsDraft;
import com.geodata.rapida.plus.SQLite.Repository.RepositorySelectedFallingHazards;
import com.geodata.rapida.plus.SQLite.Repository.RepositorySelectedOccupancy;
import com.geodata.rapida.plus.SQLite.Repository.RepositorySketchImages;
import com.geodata.rapida.plus.SQLite.Repository.RepositorySoilTypes;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryTempBuildingScores;
import com.geodata.rapida.plus.Tools.MyExceptionHandler;
import com.geodata.rapida.plus.Tools.UserAccount;
import com.itextpdf.text.BadElementException;
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
import com.tooltip.Tooltip;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class RVSScoringFragment extends Fragment
{
    private static final String TAG = RVSScoringFragment.class.getSimpleName();

    TextView tv_category, tv_bold_recommendations;

    ImageView iv_camera, iv_gallery, iv_sketch, iv_sketch_camera, iv_sketch_gallery, iv_camera_2, iv_gallery_2,
            iv_admin_signature, iv_remove_admin_signature;

    Button btn_W1, btn_W2, btn_S1, btn_S2, btn_S3, btn_S4, btn_S5,
            btn_C1, btn_C2, btn_C3, btn_PC1, btn_PC2, btn_RM1,btn_RM2, btn_URM, btn_add_admin_signature;


    boolean W1, W2, S1, S2, S3, S4, S5, C1, C2, C3, PC1, PC2, RM1, RM2, URM;

    EditText edt_comment, edt_backgroundInformation, edt_findingsObservations, edt_Recommendations,
            edt_focus, edt_BuildingAdmin, edt_BuildingAdmin_Position;

    Button btn_compute_final_score, btn_save_draft, btn_preview;

    List<ImagesClass> imagesClassList1, imagesClassList2;
    List<SketchImagesClass> sketchImagesClassList;
    List<AssignedInspectorsListModel> assignedInspectorsListModelList;

    RecyclerView recyclerView1, recyclerView2, recyclerView3,
            rv_list_occupancies, rv_list_falling_hazards,
            rv_list_assignInspector;

    RecyclerView.LayoutManager layoutManager, layoutManager2;

    RVAdapterBuildingImages rvAdapterBuildingImages;
    RVAdapterSketchImages rvAdapterSketchImages;
    RVAdapterImages rvAdapterImages;
    RVAdapterScreenerSignature rvAdapterScreenerSignature;

    float borderwidth = 2f;
    int request_Code = 101, request_Code_2 = 102,
        request_Code_3 = 103, request_Code_4 = 104,
       selectedButtonCount = 0,
       IMAGE_PICK_CAMERA_SIGNATURE_1 = 105, IMAGE_PICK_GALLERY_SIGNATURE_1 = 106,
       IMAGE_PICK_CAMERA_SIGNATURE_2 = 107, IMAGE_PICK_GALLERY_SIGNATURE_2 = 108;

    LinearLayout ll_loading,ll_radio_container_noOfPersons, ll_radio_container_SoilTypes;
    RelativeLayout rl_admin_signature;

    RadioGroup rg_NoOfPersons, rg_SoilType, rg_DetailedEvaluationRequired;
    RadioButton rb_NoOfPersons, rb_SoilType, rb_detailedValue_yes, rb_detailedValue_no;

    List<OccupanciesModel> occupanciesModelList;
    RVAdapterOccupancies rvAdapterOccupancies;

    List<FallingHazardsModel> fallingHazardsModelList;
    RVAdapterFallingHazards rvAdapterFallingHazards;

    View view;

    String MissionOrderNo, AssetID, PdfFolderName, MissionOrderID, SeismicityRegion, NoOfStories, NoOfPersons, SoilType, DateBuilt,
            PreselectBuildingType, OwnerName, Position, newAdminName, newAdminPosition, DetailedEvaluation, SelectedBuildingType,
            ImageName, ImageExtension, ImagePath, sDateNow, CameraFileName;

    ScrollView scrollView;

    ProgressDialog pDialog;

    Boolean isAdminSignature = false, isInspectorSignature = false;

    Uri imageUri;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(getActivity())); //Getting Crash - Restart

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_rvs_scoring, container, false);

        initViews();

        return  view;
    }

    private void initViews()
    {
        if (haveNetworkConnection(requireContext()))
        {
            new RVSScoringFragment.initSetTimeAndDate().execute();
        }
        else
        {
            initSetDateTimeOffline();
        }

        pDialog = new ProgressDialog(getActivity());

        PdfFolderName =  "SRI" + "/" + "RVS Scoring";

        initCreateImageFolder();

        initCreateSketchFolder();

        tv_category       =  view.findViewById(R.id.tv_category);
        tv_bold_recommendations  =  view.findViewById(R.id.tv_bold_recommendations);
        try
        {
            Bundle extras = requireActivity().getIntent().getExtras();

            if(extras != null)
            {
                MissionOrderNo       = extras.getString("MissionOrderNo");
                MissionOrderID       = extras.getString("MissionOrderID");
                AssetID              = extras.getString("AssetID");
                SeismicityRegion     = extras.getString("SeismicityRegion");

                tv_category.setText(SeismicityRegion);

                Log.e(TAG, " MissionOrderID:" + MissionOrderID);

            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        iv_camera          =  view.findViewById(R.id.iv_camera);
        iv_gallery         =  view.findViewById(R.id.iv_gallery);
        iv_sketch          =  view.findViewById(R.id.iv_sketch);
        iv_sketch_camera  =  view.findViewById(R.id.iv_sketch_camera);
        iv_sketch_gallery  =  view.findViewById(R.id.iv_sketch_gallery);
        iv_camera_2        =  view.findViewById(R.id.iv_camera_2);
        iv_gallery_2       =  view.findViewById(R.id.iv_gallery_2);
        iv_admin_signature        =  view.findViewById(R.id.iv_admin_signature);
        iv_remove_admin_signature = view.findViewById(R.id.iv_remove_building_signature);

        btn_W1 =  view.findViewById(R.id.btn_W1);
        btn_W2 =  view.findViewById(R.id.btn_W2);

        btn_S1 =  view.findViewById(R.id.btn_S1);
        btn_S2 =  view.findViewById(R.id.btn_S2);
        btn_S3 =  view.findViewById(R.id.btn_S3);
        btn_S4 =  view.findViewById(R.id.btn_S4);
        btn_S5 =  view.findViewById(R.id.btn_S5);

        btn_C1 =  view.findViewById(R.id.btn_C1);
        btn_C2 =  view.findViewById(R.id.btn_C2);
        btn_C3 =  view.findViewById(R.id.btn_C3);

        btn_PC1 =  view.findViewById(R.id.btn_PC1);
        btn_PC2 =  view.findViewById(R.id.btn_PC2);

        btn_RM1 =  view.findViewById(R.id.btn_RM1);
        btn_RM2 =  view.findViewById(R.id.btn_RM2);

        btn_URM =  view.findViewById(R.id.btn_URM);
        btn_add_admin_signature  =  view.findViewById(R.id.btn_add_admin_signature);

        ll_loading                     = view.findViewById(R.id.ll_loading);
        ll_radio_container_noOfPersons = view.findViewById(R.id.ll_radio_container_noOfPersons);
        ll_radio_container_SoilTypes   = view.findViewById(R.id.ll_radio_container_SoilTypes);
        rl_admin_signature             = view.findViewById(R.id.rl_admin_signature);

        edt_comment                  = view.findViewById(R.id.edt_comment);
        edt_backgroundInformation    = view.findViewById(R.id.edt_backgroundInformation);
        edt_findingsObservations     = view.findViewById(R.id.edt_findingsObservations);
        edt_Recommendations          = view.findViewById(R.id.edt_Recommendations);
        edt_focus                    = view.findViewById(R.id.edt_focus);
        edt_BuildingAdmin            =  view.findViewById(R.id.edt_BuildingAdmin);
        edt_BuildingAdmin_Position   =  view.findViewById(R.id.edt_BuildingAdmin_Position);

        btn_compute_final_score = view.findViewById(R.id.btn_compute_final_score);
        btn_compute_final_score.setEnabled(false);

        btn_save_draft = view.findViewById(R.id.btn_save_draft);
        btn_preview     = view.findViewById(R.id.btn_preview);

        recyclerView1 = view.findViewById(R.id.rv_list);
        recyclerView2 = view.findViewById(R.id.rv_sketch_list);
        recyclerView3 = view.findViewById(R.id.rv_other_images_attachment);

        rv_list_occupancies     = view.findViewById(R.id.rv_list_occupancies);
        rv_list_falling_hazards = view.findViewById(R.id.rv_list_falling_hazards);
        rv_list_assignInspector = view.findViewById(R.id.rv_list_assignInspector);

        rg_DetailedEvaluationRequired = view.findViewById(R.id.rg_DetailedEvaluationRequired);
        rb_detailedValue_yes          = view.findViewById(R.id.rb_detailedValue_yes);
        rb_detailedValue_no           = view.findViewById(R.id.rb_detailedValue_no);

        scrollView = view.findViewById(R.id.scrollView);

        initListeners();

        initSetAllDisplayData();
    }

    private void initSetAllDisplayData()
    {
        try
        {
            initGetBuildingInformation();

            //Set Image List
            imagesClassList1 = new ArrayList<>();
            recyclerView1.setHasFixedSize(true);
            GridLayoutManager gridLayoutManager  = new GridLayoutManager(getContext(), 4);
            recyclerView1.setLayoutManager(gridLayoutManager);
            rvAdapterBuildingImages = new RVAdapterBuildingImages(getContext(), imagesClassList1, iv_camera, iv_gallery);
            recyclerView1.setAdapter(rvAdapterBuildingImages);

            //Display List of - Building Image
            initSetBuildingImageList();

            //Set Sketch Image List
            sketchImagesClassList = new ArrayList<>();
            recyclerView2.setHasFixedSize(true);
            GridLayoutManager gridLayoutManager2 = new GridLayoutManager(getContext(), 4);
            recyclerView2.setLayoutManager(gridLayoutManager2);
            rvAdapterSketchImages   = new RVAdapterSketchImages(getContext(), sketchImagesClassList, iv_sketch, iv_sketch_camera, iv_sketch_gallery);
            recyclerView2.setAdapter(rvAdapterSketchImages);

            //Display List of - Sketch Image
            initSetSketchImageList();

            //Occupancy - List
            initSetDataOccupancies();

            initSetDataFromSavedDraft();

            //Data Falling Hazards - List
            initSetDataFallingHazards();

            //Set Other Image List
            imagesClassList2 = new ArrayList<>();
            recyclerView3.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView3.setLayoutManager(layoutManager);
            rvAdapterImages = new RVAdapterImages(getContext(), imagesClassList2);
            recyclerView3.setAdapter(rvAdapterImages);

            //Other Image - List
            initSetOtherImagesAttachmentList();

            assignedInspectorsListModelList = new ArrayList<>();
            rv_list_assignInspector.setHasFixedSize(true);
            layoutManager2 = new LinearLayoutManager(getContext());
            rv_list_assignInspector.setLayoutManager(layoutManager2);
            rvAdapterScreenerSignature = new RVAdapterScreenerSignature(getContext(), assignedInspectorsListModelList);
            rv_list_assignInspector.setAdapter(rvAdapterScreenerSignature);

            initStartToolTip();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initGetBuildingInformation()
    {
        try
        {
            Cursor cursor = RepositoryOnlineBuildingInformation.realAllData2(getContext(), String.valueOf(UserAccount.employeeID), MissionOrderID);

            if (cursor.getCount()!=0)
            {
                if (cursor.moveToFirst())
                {
                    NoOfStories  = cursor.getString(cursor.getColumnIndex("NoOfFloors"));
                    NoOfPersons  = cursor.getString(cursor.getColumnIndex("BuildingNoOfPersons"));
                    SoilType     = cursor.getString(cursor.getColumnIndex("BuildingSoilType"));
                    DateBuilt    = cursor.getString(cursor.getColumnIndex("DateFinished"));

                    OwnerName  = cursor.getString(cursor.getColumnIndex("OwnerName")) != null ?
                                 cursor.getString(cursor.getColumnIndex("OwnerName")):"";

                    Position  = cursor.getString(cursor.getColumnIndex("Position")) != null ?
                                cursor.getString(cursor.getColumnIndex("Position")):"";

                    if (cursor.getString(cursor.getColumnIndex("StructureType")) != null)
                    {
                        PreselectBuildingType = cursor.getString(cursor.getColumnIndex("StructureType"));
                    }
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initSetBuildingImageList()
    {
        try
        {
            Cursor cursor = RepositoryImages.realAllData(getContext(), MissionOrderID,
                    "Building Image", SeismicityRegion);

            if (cursor.getCount()!=0)
            {
                if (cursor.moveToFirst())
                {
                    imagesClassList1.clear();

                    do
                    {
                        ImagesClass imagesClass = new ImagesClass();

                        imagesClass.setID(cursor.getString(cursor.getColumnIndex("ID")));
                        imagesClass.setImageType(cursor.getString(cursor.getColumnIndex("ImageType")));
                        imagesClass.setImageID(cursor.getString(cursor.getColumnIndex("ImageID")));
                        imagesClass.setImageName(cursor.getString(cursor.getColumnIndex("ImageName")));
                        imagesClass.setImagePath(cursor.getString(cursor.getColumnIndex("ImagePath")));
                        imagesClass.setImageExtension(cursor.getString(cursor.getColumnIndex("ImageExtension")));
                        imagesClass.setDtAdded(cursor.getString(cursor.getColumnIndex("DtAdded")));
                        imagesClass.setIsActive(cursor.getString(cursor.getColumnIndex("isActive")));
                        imagesClass.setIsSync(cursor.getString(cursor.getColumnIndex("isSync")));

                        imagesClassList1.add(imagesClass);
                    }
                    while (cursor.moveToNext());

                    rvAdapterBuildingImages.notifyDataSetChanged();


                    if (imagesClassList1.size() != 0)
                    {
                        iv_camera.setEnabled(false);
                        iv_gallery.setEnabled(false);

                        initEnableDisableButtons(true, null, iv_camera, iv_gallery, null);
                    }
                    else
                    {
                        iv_camera.setEnabled(true);
                        iv_gallery.setEnabled(true);

                        initEnableDisableButtons(false, null, iv_camera, iv_gallery, null);
                    }
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initSetSketchImageList()
    {
        try
        {
            Cursor cursor = RepositorySketchImages.realAllData(getContext(), MissionOrderID, SeismicityRegion);

            if (cursor.getCount()!=0)
            {
                if (cursor.moveToFirst())
                {
                    sketchImagesClassList.clear();

                    do
                    {
                        SketchImagesClass sketchImagesClass = new SketchImagesClass();

                        sketchImagesClass.setID(cursor.getString(cursor.getColumnIndex("ID")));
                        sketchImagesClass.setSketchID(cursor.getString(cursor.getColumnIndex("SketchID")));
                        sketchImagesClass.setSketchName(cursor.getString(cursor.getColumnIndex("SketchName")));
                        sketchImagesClass.setSketchPath(cursor.getString(cursor.getColumnIndex("SketchPath")));
                        sketchImagesClass.setSketchExtension(cursor.getString(cursor.getColumnIndex("SketchExtension")));
                        sketchImagesClass.setDtAdded(cursor.getString(cursor.getColumnIndex("DtAdded")));
                        sketchImagesClass.setIsActive(cursor.getString(cursor.getColumnIndex("isActive")));
                        sketchImagesClass.setIsSync(cursor.getString(cursor.getColumnIndex("isSync")));

                        sketchImagesClassList.add(sketchImagesClass);
                    }
                    while (cursor.moveToNext());

                    if (sketchImagesClassList.size() != 0)
                    {
                        iv_sketch.setEnabled(false);
                        iv_sketch_camera.setEnabled(false);
                        iv_sketch_gallery.setEnabled(false);

                        initEnableDisableButtons(true, null, iv_sketch, iv_sketch_camera, iv_sketch_gallery);
                    }
                    else
                    {
                        iv_sketch.setEnabled(true);
                        iv_sketch_camera.setEnabled(true);
                        iv_sketch_gallery.setEnabled(true);

                        initEnableDisableButtons(false, null, iv_sketch, iv_sketch_camera, iv_sketch_gallery);
                    }
                }
                rvAdapterSketchImages.notifyDataSetChanged();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initSetDataOccupancies()
    {
        try
        {
            occupanciesModelList = new ArrayList<>();
            rv_list_occupancies.setHasFixedSize(true);
            GridLayoutManager gridLayoutManager3 = new GridLayoutManager(getContext(), 3);
            rv_list_occupancies.setLayoutManager(gridLayoutManager3);
            rvAdapterOccupancies = new RVAdapterOccupancies(getContext(), occupanciesModelList, MissionOrderID, SeismicityRegion);
            rv_list_occupancies.setAdapter(rvAdapterOccupancies);

            Cursor cursor = RepositoryOccupancies.realAllData(getContext());

            if (cursor.getCount()!=0)
            {
                if (cursor.moveToFirst())
                {
                    occupanciesModelList.clear();

                    do
                    {
                        OccupanciesModel  occupanciesModel = new OccupanciesModel();

                        String UseOfCharacterOccupancyID = cursor.getString(cursor.getColumnIndex("UseOfCharacterOccupancyID"));

                        occupanciesModel.setUseOfCharacterOccupancyID(Integer.parseInt(UseOfCharacterOccupancyID));
                        occupanciesModel.setDescription(cursor.getString(cursor.getColumnIndex("Description")));

                        occupanciesModelList.add(occupanciesModel);
                    }
                    while (cursor.moveToNext());
                }

                cursor.close();

                rvAdapterOccupancies.notifyDataSetChanged();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "TEST1" + e.toString());
        }
    }

    private void initSetDataFromSavedDraft()
    {
        try
        {
            String UserAccountID = UserAccount.UserAccountID;

            Cursor cursor = RepositoryRVSSaveAsDraft.realAllData2(getContext(), UserAccountID, MissionOrderID, SeismicityRegion);

            if (cursor.getCount()!=0)
            {
                if (cursor.moveToFirst())
                {
                    NoOfPersons        = cursor.getString(cursor.getColumnIndex("NoOfPersons"));
                    SoilType           = cursor.getString(cursor.getColumnIndex("SoilType"));

                    String Comments    = cursor.getString(cursor.getColumnIndex("Comments"));

                    DetailedEvaluation = cursor.getString(cursor.getColumnIndex("DetailedEvaluation")) != null ?
                                         cursor.getString(cursor.getColumnIndex("DetailedEvaluation")):"";

                    String BackgroundInformation   = cursor.getString(cursor.getColumnIndex("BackgroundInformation"));
                    String FindingsObservations    = cursor.getString(cursor.getColumnIndex("FindingsObservations"));
                    String CommentsRecommendations = cursor.getString(cursor.getColumnIndex("CommentsRecommendations"));

                    if (DetailedEvaluation.equalsIgnoreCase("yes"))
                    {
                        rb_detailedValue_yes.setChecked(true);

                    }
                    else if (DetailedEvaluation.equalsIgnoreCase("no"))
                    {
                        rb_detailedValue_no.setChecked(true);
                    }

                    edt_comment.setText(Comments);
                    edt_backgroundInformation.setText(BackgroundInformation);
                    edt_findingsObservations.setText(FindingsObservations);
                    edt_Recommendations.setText(CommentsRecommendations);

                    newAdminName  = cursor.getString(cursor.getColumnIndex("AdminName")) != null ?
                                    cursor.getString(cursor.getColumnIndex("AdminName")):"";

                    newAdminPosition  = cursor.getString(cursor.getColumnIndex("AdminPosition")) != null ?
                                        cursor.getString(cursor.getColumnIndex("AdminPosition")):"";

                    if (!newAdminName.equals("") && !newAdminPosition.equals(""))
                    {
                        edt_BuildingAdmin.setText(newAdminName);
                        edt_BuildingAdmin_Position.setText(newAdminPosition);
                    }
                    else
                    {
                        edt_BuildingAdmin.setText(OwnerName);
                        edt_BuildingAdmin_Position.setText(Position);
                    }
                }
            }
            else
            {
                edt_BuildingAdmin.setText(OwnerName);
                edt_BuildingAdmin_Position.setText(Position);
            }

            //Data No Of Person - List
            initSetDataNoOfPersons();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    private void initSetDataNoOfPersons()
    {
        try
        {
            rg_NoOfPersons = new RadioGroup(getContext());
            rg_NoOfPersons.setOrientation(LinearLayout.VERTICAL);
            rg_NoOfPersons.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

            RadioGroup.LayoutParams layoutParams;

            Cursor cursor = RepositoryNoOfPersons.realAllData(getContext());

            if (cursor.getCount()!=0)
            {
                if (cursor.moveToFirst())
                {
                    ColorStateList colorStateList = new ColorStateList(
                            new int[][]{
                                    new int[]{-android.R.attr.state_checked},
                                    new int[]{android.R.attr.state_checked}
                            },
                            new int[]{

                                    Color.DKGRAY, Color.DKGRAY, // Color.rgb (242,81,112),
                            }
                    );

                    do
                    {
                        String text = cursor.getString(cursor.getColumnIndex("NoOfPersons"));

                        rb_NoOfPersons = new RadioButton(getContext());
                        rb_NoOfPersons.setText(text);
                        rb_NoOfPersons.setButtonTintList(colorStateList);
                        rb_NoOfPersons.setPadding(5,0,0,0);

                        layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.MATCH_PARENT);
                        //layoutParams.setMargins(0, 0,0,0);

                        rg_NoOfPersons.addView(rb_NoOfPersons, layoutParams);

                        if (NoOfPersons != null && NoOfPersons.equals(text))
                        {
                            rb_NoOfPersons.setChecked(true);
                        }
                    }
                    while (cursor.moveToNext());
                }
            }

            ll_radio_container_noOfPersons.addView(rg_NoOfPersons);

            //Data Soil Types - List
            initSetDataSoilTypes();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    @SuppressLint({"UseCompatLoadingForColorStateLists", "SetTextI18n"})
    private void initSetDataSoilTypes()
    {
        try
        {
            rg_SoilType = new RadioGroup(getContext());
            rg_SoilType.setOrientation(LinearLayout.VERTICAL);
            rg_SoilType.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

            RadioGroup.LayoutParams layoutParams;

            Cursor cursor = RepositorySoilTypes.realAllData(getContext());

            if (cursor.getCount()!=0)
            {
                if (cursor.moveToFirst())
                {
                    ColorStateList colorStateList = new ColorStateList(
                            new int[][]{
                                    new int[]{-android.R.attr.state_checked},
                                    new int[]{android.R.attr.state_checked}
                            },
                            new int[]{

                                    Color.DKGRAY, Color.DKGRAY, // Color.rgb (242,81,112),
                            }
                    );

                    do
                    {
                        String BuildingSoilTypeCode = cursor.getString(cursor.getColumnIndex("BuildingSoilTypeCode"));
                        String text = BuildingSoilTypeCode + ". " + cursor.getString(cursor.getColumnIndex("BuildingSoilTypeDesc"));

                        String txtValidate = BuildingSoilTypeCode + " - " + cursor.getString(cursor.getColumnIndex("BuildingSoilTypeDesc"));


                        rb_SoilType = new RadioButton(getContext());
                        rb_SoilType.setText(text);
                        rb_SoilType.setButtonTintList(colorStateList);
                        rb_SoilType.setPadding(5,0,0,0);

                        layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.MATCH_PARENT);
                        //layoutParams.setMargins(0, 0,0,0);

                        rg_SoilType.addView(rb_SoilType, layoutParams);

                        if (SoilType!= null && SoilType.equals(txtValidate))
                        {
                            rb_SoilType.setChecked(true);
                        }
                        else if (SoilType!= null && SoilType.equals(text))
                        {
                            rb_SoilType.setChecked(true);
                        }
                    }
                    while (cursor.moveToNext());
                }
            }

            ll_radio_container_SoilTypes.addView(rg_SoilType);

        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initSetDataFallingHazards()
    {
        try
        {
            fallingHazardsModelList = new ArrayList<>();
            GridLayoutManager gridLayoutManager4 = new GridLayoutManager(getContext(), 2);
            rv_list_falling_hazards.setLayoutManager(gridLayoutManager4);
            rvAdapterFallingHazards = new RVAdapterFallingHazards(getContext(), fallingHazardsModelList, MissionOrderID, SeismicityRegion);
            rv_list_falling_hazards.setAdapter(rvAdapterFallingHazards);

            Cursor cursor = RepositoryFallingHazards.realAllData(getContext());

            if (cursor.getCount()!=0)
            {
                if (cursor.moveToFirst())
                {
                    fallingHazardsModelList.clear();

                    do
                    {
                        FallingHazardsModel  fallingHazardsModel = new FallingHazardsModel();

                        String FallingHazardID = cursor.getString(cursor.getColumnIndex("FallingHazardID"));
                        fallingHazardsModel.setFallingHazardID(Integer.parseInt(FallingHazardID));
                        fallingHazardsModel.setFallingHazardDesc(cursor.getString(cursor.getColumnIndex("FallingHazardDesc")));

                        fallingHazardsModelList.add(fallingHazardsModel);
                    }
                    while (cursor.moveToNext());
                }

                cursor.close();

                rvAdapterFallingHazards.notifyDataSetChanged();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "TEST2" + e.toString());
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initSetOtherImagesAttachmentList()
    {
        try
        {
            Cursor cursor = RepositoryImages.realAllData(getContext(), MissionOrderID,
                    "Other Image", SeismicityRegion);

            if (cursor.getCount()!=0)
            {
                if (cursor.moveToFirst())
                {
                    imagesClassList2.clear();

                    do
                    {
                        ImagesClass imagesClass = new ImagesClass();

                        imagesClass.setID(cursor.getString(cursor.getColumnIndex("ID")));
                        imagesClass.setImageID(cursor.getString(cursor.getColumnIndex("ImageID")));
                        imagesClass.setImageType(cursor.getString(cursor.getColumnIndex("ImageType")));
                        imagesClass.setImageName(cursor.getString(cursor.getColumnIndex("ImageName")));
                        imagesClass.setImagePath(cursor.getString(cursor.getColumnIndex("ImagePath")));
                        imagesClass.setImageExtension(cursor.getString(cursor.getColumnIndex("ImageExtension")));
                        imagesClass.setDtAdded(cursor.getString(cursor.getColumnIndex("DtAdded")));
                        imagesClass.setDescription(cursor.getString(cursor.getColumnIndex("Description")));
                        imagesClass.setIsActive(cursor.getString(cursor.getColumnIndex("isActive")));
                        imagesClass.setIsSync(cursor.getString(cursor.getColumnIndex("isSync")));

                        imagesClassList2.add(imagesClass);
                    }
                    while (cursor.moveToNext());
                }
                rvAdapterImages.notifyDataSetChanged();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "TEST3" + e.toString());
        }
    }




    private void initListeners()
    {
        iv_camera.setOnClickListener(v ->
        {
            Intent intent = new Intent(getActivity(), AttachOtherImagesActivity.class);
            intent.putExtra("MissionOrderID", MissionOrderID);
            intent.putExtra("Option", "CAMERA");
            intent.putExtra("SeismicityRegion", SeismicityRegion);
            intent.putExtra("ImageType", "Building Image");
            startActivityForResult(intent, request_Code);
        });

        iv_gallery.setOnClickListener(v ->
        {
            Intent intent = new Intent(getActivity(), AttachOtherImagesActivity.class);
            intent.putExtra("MissionOrderID", MissionOrderID);
            intent.putExtra("Option", "GALLERY");
            intent.putExtra("SeismicityRegion", SeismicityRegion);
            intent.putExtra("ImageType", "Building Image");
            startActivityForResult(intent, request_Code);
        });

        iv_camera_2.setOnClickListener(v ->
        {
            Intent intent = new Intent(getActivity(), AttachOtherImagesActivity.class);
            intent.putExtra("MissionOrderID", MissionOrderID);
            intent.putExtra("Option", "CAMERA");
            intent.putExtra("SeismicityRegion", SeismicityRegion);
            intent.putExtra("ImageType", "Other Image");
            startActivityForResult(intent, request_Code_3);
        });

        iv_gallery_2.setOnClickListener(v ->
        {
            Intent intent = new Intent(getActivity(), AttachOtherImagesActivity.class);
            intent.putExtra("MissionOrderID", MissionOrderID);
            intent.putExtra("Option", "GALLERY");
            intent.putExtra("SeismicityRegion", SeismicityRegion);
            intent.putExtra("ImageType", "Other Image");
            startActivityForResult(intent, request_Code_3);
        });

        iv_sketch.setOnClickListener(v ->
        {
            Intent intent = new Intent(getActivity(), SketchPadActivity.class);
            intent.putExtra("MissionOrderID", MissionOrderID);
            intent.putExtra("Option", "Sketch Draw");
            intent.putExtra("SeismicityRegion", SeismicityRegion);
            startActivityForResult(intent, request_Code_2);
        });

        iv_sketch_camera.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), SketchPadActivity.class);
                intent.putExtra("MissionOrderID", MissionOrderID);
                intent.putExtra("Option", "Sketch Capture");
                intent.putExtra("SeismicityRegion", SeismicityRegion);
                startActivityForResult(intent, request_Code_2);
            }
        });

        iv_sketch_gallery.setOnClickListener(v ->
        {
            Intent intent = new Intent(getActivity(), SketchPadActivity.class);
            intent.putExtra("MissionOrderID", MissionOrderID);
            intent.putExtra("Option", "Sketch Upload");
            intent.putExtra("SeismicityRegion", SeismicityRegion);
            startActivityForResult(intent, request_Code_2);
        });


        btn_save_draft.setOnClickListener(new View.OnClickListener()
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
                            case DialogInterface.BUTTON_POSITIVE:

                                initSaveAsDraft();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Are you sure  to save as draft the report ?");
                builder.setCancelable(false);
                builder.setNegativeButton("CLOSE", onClickListener);
                builder.setPositiveButton("YES", onClickListener);
                builder.show();
            }
        });

        btn_preview.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (initValidationOfPreview())
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
            }
        });

        initBuildingListeners();

        tv_bold_recommendations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //initButtonBold(edt_Recommendations);

                Toast.makeText(getContext(), "On-going.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean initValidationOfPreview()
    {
        try
        {
            if (iv_camera.isEnabled() && iv_gallery.isEnabled())
            {
                Toast.makeText(getContext(), "Building Image is required.", Toast.LENGTH_SHORT).show();
            }
            else if (iv_sketch.isEnabled() && iv_sketch_camera.isEnabled() && iv_sketch_gallery.isEnabled())
            {
                Toast.makeText(getContext(), "Sketch is required.", Toast.LENGTH_SHORT).show();
            }
            else if (initValidateOccupancySelect() == 0)
            {
                Toast.makeText(getContext(), "Occupancy is required.", Toast.LENGTH_SHORT).show();
            }
            else if (initValidateNoOfPersonsSelect() == 0)
            {
                Toast.makeText(getContext(), "No. of Persons is required.", Toast.LENGTH_SHORT).show();
            }
            else if (edt_comment.getText().toString().isEmpty())
            {
                Toast.makeText(getContext(), "Comments is required.", Toast.LENGTH_SHORT).show();
            }
       /*     else if (edt_backgroundInformation.getText().toString().isEmpty())
            {
                Toast.makeText(getContext(), "Background Information is required.", Toast.LENGTH_SHORT).show();
            }
            else if (edt_findingsObservations.getText().toString().isEmpty())
            {
                Toast.makeText(getContext(), "Findings/Observations is required.", Toast.LENGTH_SHORT).show();
            }
            else if (edt_Recommendations.getText().toString().isEmpty())
            {
                Toast.makeText(getContext(), "Recommendations is required.", Toast.LENGTH_SHORT).show();
            }*/
            else if (!isInspectorSignature)
            {
                Toast.makeText(getContext(), "Inspector signature is required.", Toast.LENGTH_SHORT).show();
            }
        /*    else if (!isAdminSignature)
            {
                Toast.makeText(getContext(), "Building Admin signature is required.", Toast.LENGTH_SHORT).show();
            }*/
            else
            {
                return true;
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        return false;
    }

    private int initValidateOccupancySelect()
    {
        int OccupancySelected = 0;

        int iOccupanciesCount = occupanciesModelList.size();

        for(int i=0; i < iOccupanciesCount ; i++)
        {
            OccupanciesModel occupanciesModel = occupanciesModelList.get(i);

            if (occupanciesModel.getIsActive().equals("1"))
            {
                OccupancySelected = OccupancySelected + 1;
            }
        }

        return OccupancySelected;
    }

    private int initValidateNoOfPersonsSelect()
    {
        int NoOfPersonsSelected = 0;

        int selectedIdNoOfPersons = rg_NoOfPersons.getCheckedRadioButtonId();

        if (selectedIdNoOfPersons != -1)
        {
            rb_NoOfPersons = view.findViewById(selectedIdNoOfPersons);

            NoOfPersonsSelected = NoOfPersonsSelected + 1;
        }

        return NoOfPersonsSelected;
    }




    private void initSaveAsDraft()
    {
        try
        {
            String UserAccountID = UserAccount.UserAccountID;

            String NoOfPersons= "", SoilType = "", DetailedEvaluationRequired = "";

            int selectedIdNoOfPersons = rg_NoOfPersons.getCheckedRadioButtonId();

            if (selectedIdNoOfPersons != -1)
            {
                rb_NoOfPersons = view.findViewById(selectedIdNoOfPersons);

                NoOfPersons  = rb_NoOfPersons.getText().toString();
            }

            int selectedIdSoilType = rg_SoilType.getCheckedRadioButtonId();

            if (selectedIdSoilType != -1)
            {
                rb_SoilType = view.findViewById(selectedIdSoilType);

                SoilType = rb_SoilType.getText().toString();

                Log.e(TAG, "SoilType: " + SoilType);
            }

            if (rb_detailedValue_yes.isChecked())
            {
                DetailedEvaluationRequired = rb_detailedValue_yes.getText().toString();
            }
            else if (rb_detailedValue_no.isChecked())
            {
                DetailedEvaluationRequired = rb_detailedValue_no.getText().toString();
            }

            String Comments                = !edt_comment.getText().toString().equals("") ? edt_comment.getText().toString():"";
            String BackgroundInformation   = !edt_backgroundInformation.getText().toString().equals("") ? edt_backgroundInformation.getText().toString():"";
            String FindingsObservations    = !edt_findingsObservations.getText().toString().equals("") ? edt_findingsObservations.getText().toString():"";
            String Recommendations         = !edt_Recommendations.getText().toString().equals("") ? edt_Recommendations.getText().toString():"";

            //Selecting Occupancy - Saving
            int iOccupanciesCount = occupanciesModelList.size();

            for(int i=0; i < iOccupanciesCount ; i++)
            {
                OccupanciesModel occupanciesModel = occupanciesModelList.get(i);

                if (occupanciesModel.getIsActive().equals("1"))
                {
                    String UseOfCharacterOccupancyID = String.valueOf(occupanciesModel.getUseOfCharacterOccupancyID());

                    SelectedOccupancyClass selectedOccupancyClass = new SelectedOccupancyClass();

                    selectedOccupancyClass.setUserAccountID(UserAccount.UserAccountID);
                    selectedOccupancyClass.setMissionOrderID(MissionOrderID);
                    selectedOccupancyClass.setCategory(SeismicityRegion);
                    selectedOccupancyClass.setUseOfCharacterOccupancyID(UseOfCharacterOccupancyID);

                    Cursor cursor = RepositorySelectedOccupancy.realAllData2(getContext(),
                            UserAccountID, MissionOrderID, SeismicityRegion, UseOfCharacterOccupancyID);

                    if (cursor.getCount()!=0)
                    {
                        cursor.moveToFirst();

                        String ID = cursor.getString(cursor.getColumnIndex("ID"));

                        RepositorySelectedOccupancy.updateSelectedOccupancy(getContext(), selectedOccupancyClass, ID);
                    }
                    else
                    {
                        RepositorySelectedOccupancy.saveSelectedOccupancy(getContext(), selectedOccupancyClass);
                    }
                }
                else
                {
                    Cursor cursor =  RepositorySelectedOccupancy.realAllData2(getContext(),
                            UserAccountID, MissionOrderID, SeismicityRegion, String.valueOf(occupanciesModel.getUseOfCharacterOccupancyID()));

                    if (cursor.getCount()!=0)
                    {
                        if (cursor.moveToFirst())
                        {
                            String ID = cursor.getString(cursor.getColumnIndex("ID"));

                            RepositorySelectedOccupancy.removeSelectedOccupancy(getContext(), ID);
                        }
                    }

                    Cursor cursor2 = RepositoryOnlineBuildingOccupancyList.realAllData(getContext(),
                            UserAccountID, MissionOrderID, occupanciesModel.getDescription());

                    if (cursor2.getCount()!=0)
                    {
                        if (cursor2.moveToFirst())
                        {
                            String ID = cursor2.getString(cursor2.getColumnIndex("ID"));

                            RepositoryOnlineBuildingOccupancyList.removeSelectedOccupancy(getContext(), ID);
                        }
                    }
                }
            }

            //Selecting Falling Hazards - Saving
            int iFallingHazardsCount = fallingHazardsModelList.size();

            for(int i=0; i < iFallingHazardsCount ; i++)
            {
                FallingHazardsModel fallingHazardsModel = fallingHazardsModelList.get(i);

                if (fallingHazardsModel.getIsActive().equals("1"))
                {
                    String Description = fallingHazardsModel.getFallingHazardDesc();
                    String OthersField = fallingHazardsModel.getOthersField();

                    SelectedFallingHazardsClass selectedFallingHazardsClass = new SelectedFallingHazardsClass();

                    selectedFallingHazardsClass.setUserAccountID(UserAccount.UserAccountID);
                    selectedFallingHazardsClass.setMissionOrderID(MissionOrderID);
                    selectedFallingHazardsClass.setCategory(SeismicityRegion);
                    selectedFallingHazardsClass.setFallingHazardDesc(Description);
                    selectedFallingHazardsClass.setOthersField(OthersField);

                    Cursor cursor =  RepositorySelectedFallingHazards.realAllData2(getContext(),
                            UserAccountID, MissionOrderID, SeismicityRegion, Description);

                    if (cursor.getCount()!=0)
                    {
                        cursor.moveToFirst();

                        String ID = cursor.getString(cursor.getColumnIndex("ID"));

                        RepositorySelectedFallingHazards.updateSelectedFallingHazards(getContext(), selectedFallingHazardsClass, ID);

                        Log.e(TAG, "FALLING HAZARD SAVING: 1");
                    }
                    else
                    {
                        RepositorySelectedFallingHazards.saveSelectedFallingHazards(getContext(), selectedFallingHazardsClass);

                        Log.e(TAG, "FALLING HAZARD SAVING: 2");
                    }
                }
                else
                {
                    Cursor cursor =  RepositorySelectedFallingHazards.realAllData2(getContext(),
                            UserAccountID, MissionOrderID, SeismicityRegion, fallingHazardsModel.getFallingHazardDesc());

                    if (cursor.getCount()!=0)
                    {
                        cursor.moveToFirst();

                        String ID = cursor.getString(cursor.getColumnIndex("ID"));

                        RepositorySelectedFallingHazards.removeSelectedFallingHazards(getContext(), ID);

                        Log.e(TAG, "FALLING HAZARD SAVING: 3");
                    }
                }
            }


            String newAdminName = edt_BuildingAdmin.getText().toString() !=null ?
                                  edt_BuildingAdmin.getText().toString():"";

            String newAdminPosition = edt_BuildingAdmin_Position.getText().toString() !=null ?
                                      edt_BuildingAdmin_Position.getText().toString():"";

            RVSSaveDraftDataClass rvsSaveDraftDataClass = new RVSSaveDraftDataClass();

            rvsSaveDraftDataClass.setScreenerID(UserAccountID);
            rvsSaveDraftDataClass.setMissionOrderID(MissionOrderID);
            rvsSaveDraftDataClass.setCategory(SeismicityRegion);
            rvsSaveDraftDataClass.setNoOfPersons(NoOfPersons);
            rvsSaveDraftDataClass.setSoilType(SoilType);
            rvsSaveDraftDataClass.setComments(Comments);
            rvsSaveDraftDataClass.setDetailedEvaluation(DetailedEvaluationRequired);
            rvsSaveDraftDataClass.setBackgroundInformation(BackgroundInformation);
            rvsSaveDraftDataClass.setFindingsObservations(FindingsObservations);
            rvsSaveDraftDataClass.setCommentsRecommendations(Recommendations);
            rvsSaveDraftDataClass.setAdminName(newAdminName);
            rvsSaveDraftDataClass.setAdminPosition(newAdminPosition);

            Cursor cursor = RepositoryRVSSaveAsDraft.realAllData2(getContext(), UserAccountID, MissionOrderID, SeismicityRegion);

            if (cursor.getCount()!=0)
            {
                cursor.moveToFirst();

                String ID = cursor.getString(cursor.getColumnIndex("ID"));

                RepositoryRVSSaveAsDraft.updateRVSData(getContext(), rvsSaveDraftDataClass, ID);
            }
            else
            {
                RepositoryRVSSaveAsDraft.saveRVSData(getContext(), rvsSaveDraftDataClass);
            }

            Toast.makeText(getContext(), "Successfully Saved.", Toast.LENGTH_SHORT).show();

            requireActivity().finish();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
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
            Log.e(TAG, "DASDASDASDASDASDAS");

            String FILE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + PdfFolderName + "/" + "Seismic Report" + "_" + MissionOrderNo + "." + "pdf";

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

            Intent intent = new Intent(getActivity(), PreviewReportRVSScoringActivity.class);
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
        Phrase phrase;
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
        Font catFont3                  = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD);
        Font catFont4                  = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Font catFont5                  = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD | Font.ITALIC);

        try
        {
            String ScreenerID    = String.valueOf(UserAccount.employeeID);
            String UserAccountID = UserAccount.UserAccountID;

            String BuildingName="", NoOfPersons="", NoOfStories="", Address="", FloorArea="",ZIP="", DateBuilt ="", Occupancy = "",
                    DateReported = "", LatitudeY="", LongitudeX="", Altitude="";

            Cursor cursor = RepositoryOnlineBuildingInformation.realAllData2(getContext(), ScreenerID, MissionOrderID);

            if (cursor.getCount()!=0)
            {
                if (cursor.moveToFirst())
                {
                    BuildingName = cursor.getString(cursor.getColumnIndex("BuildingName"));

                    NoOfStories   = cursor.getString(cursor.getColumnIndex("NoOfFloors"));
                    Address       = cursor.getString(cursor.getColumnIndex("Location"));
                    FloorArea     = cursor.getString(cursor.getColumnIndex("FloorArea"));
                    ZIP           = cursor.getString(cursor.getColumnIndex("ZipCode"));
                    DateBuilt     = cursor.getString(cursor.getColumnIndex("DateFinished"));
                    DateReported  = cursor.getString(cursor.getColumnIndex("DateReported"));
                    LatitudeY     = cursor.getString(cursor.getColumnIndex("Lat"));
                    LongitudeX    = cursor.getString(cursor.getColumnIndex("Long"));
                    Altitude      = cursor.getString(cursor.getColumnIndex("Altitude"));

                    if (cursor.getString(cursor.getColumnIndex("Occupancy")) != null)
                    {
                        Occupancy = cursor.getString(cursor.getColumnIndex("Occupancy"));
                    }
                }
            }

            String DetailedEvaluation = "";

            if (rb_detailedValue_yes.isChecked())
            {
                DetailedEvaluation = rb_detailedValue_yes.getText().toString();
            }
            else if (rb_detailedValue_no.isChecked())
            {
                DetailedEvaluation = rb_detailedValue_no.getText().toString();
            }

            Cursor cursor04 = RepositoryRVSSaveAsDraft.realAllData2(getContext(), UserAccountID, MissionOrderID, SeismicityRegion);

            if (cursor04.getCount() !=0)
            {
                if(cursor04.moveToFirst())
                {
                    if (!cursor04.getString(cursor04.getColumnIndex("DetailedEvaluation")).equals(""))
                    {
                        DetailedEvaluation = cursor04.getString(cursor04.getColumnIndex("DetailedEvaluation"));
                    }

                    int selectedIdNoOfPersons = rg_NoOfPersons.getCheckedRadioButtonId();

                    if (selectedIdNoOfPersons != -1)
                    {
                        rb_NoOfPersons   = view.findViewById(selectedIdNoOfPersons);
                        NoOfPersons      = rb_NoOfPersons.getText().toString();
                    }
                    else
                    {
                        NoOfPersons = cursor04.getString(cursor04.getColumnIndex("NoOfPersons"));
                    }
                }
            }
            else
            {
                int selectedIdNoOfPersons = rg_NoOfPersons.getCheckedRadioButtonId();

                if (selectedIdNoOfPersons != -1)
                {
                    rb_NoOfPersons   = view.findViewById(selectedIdNoOfPersons);
                    NoOfPersons      = rb_NoOfPersons.getText().toString();
                }
            }

            //CHECK Image
            Drawable dCheck = ContextCompat.getDrawable(requireContext(), R.drawable.check_png);
            BitmapDrawable bdCheck = ((BitmapDrawable) dCheck);
            assert bdCheck != null;
            Bitmap bCheck = bdCheck.getBitmap();
            ByteArrayOutputStream stream3 = new ByteArrayOutputStream();
            bCheck.compress(Bitmap.CompressFormat.PNG, 100, stream3);

            Image imageCheck = Image.getInstance(stream3.toByteArray());
            imageCheck.scaleAbsolute(10f, 10f);

            //UNCHECK Image
            Drawable dUncheck = ContextCompat.getDrawable(requireContext(), R.drawable.uncheck_png);
            BitmapDrawable bdUncheck = ((BitmapDrawable) dUncheck);
            assert bdUncheck != null;
            Bitmap bUncheck = bdUncheck.getBitmap();
            ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
            bUncheck.compress(Bitmap.CompressFormat.PNG, 100, stream2);

            Image imageUncheck = Image.getInstance(stream2.toByteArray());
            imageUncheck.scaleAbsolute(10f, 10f);


            //region HEADER
            try
            {
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

                Image LogoLeft = Image.getInstance(stream.toByteArray());
                LogoLeft.scaleAbsolute(81f, 80f);

                cells = new PdfPCell();
                cells.addElement(LogoLeft);
                cells.setBackgroundColor(BaseColor.WHITE);
                cells.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cells.setBorder(Rectangle.NO_BORDER);
                cells.setColspan(1);
                table.addCell(cells);

                Phrase headerPhrase = new Phrase(new Chunk("Republic of the Philippines", catFont));
                headerPhrase.add(new Phrase("\n" + "(AGENCY)", catFont));
                headerPhrase.add(new Phrase("\n" + "(Location)", catFont));
                headerPhrase.add(new Phrase("\n\n" + "FEMA-154 Seismic Resiliency Inspection", catFont2));

                cells = new PdfPCell(headerPhrase);
                cells.setBackgroundColor(BaseColor.WHITE);
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setBorder(Rectangle.NO_BORDER);
                cells.setColspan(1);
                table.addCell(cells);

                cells = new PdfPCell();
                cells.addElement(LogoLeft);
                cells.setBackgroundColor(BaseColor.WHITE);
                cells.setHorizontalAlignment(Element.ALIGN_LEFT);
                cells.setBorder(Rectangle.NO_BORDER);
                cells.setColspan(1);
                table.addCell(cells);


                document.add(table);
            }
            catch (Exception e)
            {
                Log.e(TAG, "Header: " + e.toString());
            }
            //endregion HEADER

            //region BUILDING INFORMATION
            try
            {
                table = new PdfPTable(2);
                table.setWidthPercentage(100);

                //INSPECTION
                cells = new PdfPCell(new Phrase("INSPECTION", header2));
                cells.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.RIGHT);
                cells.setPaddingTop(5);
                cells.setPaddingLeft(5);
                cells.setColspan(2);
                table.addCell(cells);


                //SCREENER
                table2 = new PdfPTable(50);
                table2.setWidthPercentage(100);

                //ADDRESS
                phrase = new Phrase("Inspector: ", header1);
                phrase.setFont(smallNormal);
                pCell = new PdfPCell(phrase);
                pCell.setColspan(10);
                pCell.setBorder(Rectangle.NO_BORDER);
                pCell.setPaddingTop(5);
                table2.addCell(pCell);

                Paragraph newParagraph = new Paragraph();
                Cursor cursor2 = RepositoryOnlineAssignedInspectors.realAllData2(getContext(), ScreenerID, MissionOrderID);

                if (cursor2.getCount()!=0)
                {
                    if (cursor2.moveToFirst())
                    {
                        do
                        {
                            String Inspector = cursor2.getString(cursor2.getColumnIndex("Inspector"));

                            newParagraph.add(new Chunk(Inspector + "\n", header1));

                            /*boolean isTL = (cursor2.getString(cursor2.getColumnIndexOrThrow("isTL"))).equals("1");

                            if (isTL)
                            {
                                newParagraph.add(new Chunk(Inspector, header1));
                                newParagraph.add(new Chunk( " [TL]\n", header2));
                            }
                            else
                            {
                                newParagraph.add(new Chunk(Inspector + "\n", header1));
                            }*/
                        }
                        while (cursor2.moveToNext());
                    }
                    cursor2.close();
                }

                pCell = new PdfPCell(newParagraph);
                pCell.setColspan(90);
                pCell.setBorder(Rectangle.NO_BORDER);
                pCell.setPaddingTop(5);
                table2.addCell(pCell);
                cells.addElement(table2);

                cells.setColspan(1);
                cells.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
                cells.setPaddingLeft(20);
                cells.setPaddingBottom(10);
                table.addCell(cells);


                //DATE
                phrase = new Phrase(new Chunk(" " + "Inspected Date: ", header1));
                phrase.setFont(smallNormal);

                if (!DateReported.equals(""))
                {
                    phrase.add(DateReported);
                }
                else
                {
                    long timestampMilliseconds = System.currentTimeMillis();
                    SimpleDateFormat DateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

                    DateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
                    String actualDate = DateFormat.format(new Date(timestampMilliseconds));

                    phrase.add(actualDate);
                }

                cells = new PdfPCell(phrase);
                cells.setColspan(1);
                cells.setBorder(Rectangle.RIGHT | Rectangle.BOTTOM);
                cells.setPaddingRight(20);
                cells.setPaddingBottom(10);
                cells.setPaddingTop(8);
                table.addCell(cells);


                cells = new PdfPCell(new Phrase(new Chunk("BUILDING INFORMATION", header2)));
                cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
                cells.setPaddingTop(5);
                cells.setPaddingLeft(5);
                cells.setPaddingBottom(5);
                cells.setColspan(2);
                table.addCell(cells);


                cells = new PdfPCell();
                table2 = new PdfPTable(100);
                table2.setWidthPercentage(100);

                //BUILDING NAME
                pCell = new PdfPCell(new Phrase("Building Name: ", header1));
                pCell.setColspan(30);
                pCell.setBorder(Rectangle.NO_BORDER);
                table2.addCell(pCell);

                pCell = new PdfPCell(new Phrase(BuildingName, header1));
                pCell.setColspan(70);
                pCell.setBorder(Rectangle.NO_BORDER);
                table2.addCell(pCell);
                cells.addElement(table2);


                //NO. OF STORIES
                phrase = new Phrase();
                phrase.add(new Chunk(" " + "No. of Stories: ", header1));
                phrase.add(new Chunk(NoOfStories, smallNormal));
                cells.addElement(phrase);

                //FLOOR AREA
                phrase = new Phrase();
                phrase.add(new Chunk(" " + "Floor Area (sq.m): ", header1));
                phrase.add(new Chunk(FloorArea, smallNormal));
                cells.addElement(phrase);

                //YEAR BUILT
                phrase = new Phrase();
                phrase.add(new Chunk(" " + "Year Built: ", header1));

                if (DateBuilt.contains("T"))
                {
                    String[] YearBuilt =  DateBuilt.split("T");

                    phrase.add(new Chunk(FormatDate(YearBuilt[0]), header1));
                }

                cells.addElement(phrase);

                //ZIP
                phrase = new Phrase();
                phrase.add(new Chunk(" " + "Zip Code: ", header1));
                phrase.add(new Chunk(ZIP, smallNormal));

                cells.addElement(phrase);


                phrase = new Phrase();
                phrase.add(new Chunk(" " + "Altitude: 40 meters", header1));
                phrase.add(new Chunk(Altitude, smallNormal));

                cells.addElement(phrase);


                cells.setColspan(1);
                cells.setBorder(Rectangle.LEFT);
                cells.setPaddingLeft(20);
                cells.setPaddingBottom(10);
                table.addCell(cells);


                //ZIP
                cells = new PdfPCell();
                table2 = new PdfPTable(50);
                table2.setWidthPercentage(100);

                //OCCUPANCY
                pCell = new PdfPCell(new Phrase("Occupancy:", header1));
                pCell.setColspan(12);
                pCell.setBorder(Rectangle.NO_BORDER);
                pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table2.addCell(pCell);

                int iOccupancyCount = occupanciesModelList.size();

                Log.e(TAG, "iOccupancyCount: " + iOccupancyCount + "\n"+ "Occupancy: " + Occupancy);

                String sOccupancy = "";

                for(int i=0; i < iOccupancyCount ; i++)
                {
                    OccupanciesModel occupanciesModel = occupanciesModelList.get(i);

                    if (occupanciesModel.getIsActive().equals("1"))
                    {
                        if (Occupancy != null || Occupancy.equals(""))
                        {
                            sOccupancy = Occupancy + ", " + occupanciesModel.getDescription();
                        }
                    }
                }

                if (Occupancy.equals(""))
                {
                    Occupancy = sOccupancy;
                }

                pCell = new PdfPCell(new Phrase(Occupancy, smallNormal));
                pCell.setColspan(38);
                pCell.setBorder(Rectangle.NO_BORDER);
                pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table2.addCell(pCell);
                cells.addElement(table2);


                //NO. OF PERSONS
                phrase = new Phrase();
                phrase.add(new Chunk(" " + "No. of Persons: ", header1));
                phrase.add(new Chunk(NoOfPersons, header1));
                cells.addElement(phrase);


                table2 = new PdfPTable(50);
                table2.setWidthPercentage(100);

                //ADDRESS
                phrase = new Phrase("Address:", header1);
                pCell = new PdfPCell(phrase);
                pCell.setColspan(9);
                pCell.setBorder(Rectangle.NO_BORDER);
                pCell.setPaddingTop(5);
                table2.addCell(pCell);

                phrase = new Phrase(Address, header1);
                pCell = new PdfPCell(phrase);
                pCell.setColspan(90);
                pCell.setBorder(Rectangle.NO_BORDER);
                pCell.setPaddingTop(5);
                table2.addCell(pCell);

                cells.addElement(table2);
                cells.setColspan(1);
                cells.setBorder(Rectangle.RIGHT);
                cells.setPaddingRight(20);
                cells.setPaddingBottom(10);


                cells.addElement(new Phrase("Latitude (Y): " + LatitudeY));
                cells.setColspan(1);
                cells.setBorder(Rectangle.RIGHT);
                cells.setPaddingRight(20);
                cells.setPaddingBottom(10);

                cells.addElement(new Phrase("Longitude (X):" + LongitudeX));
                cells.setColspan(1);
                cells.setBorder(Rectangle.RIGHT);
                cells.setPaddingRight(20);
                cells.setPaddingBottom(10);

                table.addCell(cells);

                document.add(table);
            }
            catch (Exception e)
            {
                Log.e(TAG, "Building Information: " + e.toString());
            }
            //endregion

            //region SKETCH & BUILDING IMAGE
            try
            {
                table = new PdfPTable(100);
                table.setWidthPercentage(100);

                cells = new PdfPCell(new Phrase(""));
                cells.setHorizontalAlignment(Element.ALIGN_LEFT);
                cells.setBorder(Rectangle.LEFT);
                cells.setColspan(2);
                table.addCell(cells);


                int iSketchImagesCount = sketchImagesClassList.size();

                if (iSketchImagesCount != 0)
                {
                    for(int i=0; i < iSketchImagesCount ; i++)
                    {
                        SketchImagesClass sketchImagesClass = sketchImagesClassList.get(i);

                        File file = new File(sketchImagesClass.getSketchPath());

                        if (file.exists())
                        {
                            try
                            {
                                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                                Bitmap bitmapBuildingImage = BitmapFactory.decodeFile(sketchImagesClass.getSketchPath(), bmOptions);

                                ByteArrayOutputStream byteArrayOutputStreamCam1 = new ByteArrayOutputStream();
                                getResizedBitmap(bitmapBuildingImage, 500).compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStreamCam1);

                                Image imageCam1 = Image.getInstance(byteArrayOutputStreamCam1.toByteArray());
                                imageCam1.setAlignment(Image.ALIGN_CENTER);
                                imageCam1.scaleAbsolute(260f, 250f);

                                cells = new PdfPCell(imageCam1);
                                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                                cells.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM |Rectangle.RIGHT);
                                cells.setColspan(48);
                                cells.setPadding(5);
                               /* cells.setPaddingTop(130);
                                cells.setPaddingBottom(130);*/
                                cells.setBorderWidth(borderwidth);
                                cells.setBorderColor(BaseColor.BLACK);
                                table.addCell(cells);
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, e.toString());
                            }
                        }
                        else
                        {
                            cells = new PdfPCell(new Phrase("SKETCH OF\nTHE BUILDING", smallBold));
                            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cells.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM |Rectangle.RIGHT);
                            cells.setColspan(48);
                            cells.setPaddingTop(130);
                            cells.setPaddingBottom(130);
                            cells.setBorderWidth(borderwidth);
                            cells.setBorderColor(BaseColor.BLACK);
                            table.addCell(cells);
                        }
                    }
                }
                else
                {
                    cells = new PdfPCell(new Phrase("SKETCH OF\nTHE BUILDING", smallBold));
                    cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cells.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM |Rectangle.RIGHT);
                    cells.setColspan(48);
                    cells.setPaddingTop(130);
                    cells.setPaddingBottom(130);
                    cells.setBorderWidth(borderwidth);
                    cells.setBorderColor(BaseColor.BLACK);
                    table.addCell(cells);
                }


                int iImageBuildingCount = imagesClassList1.size();

                if (iImageBuildingCount!=0)
                {
                    for(int i=0; i < iImageBuildingCount ; i++)
                    {
                        ImagesClass imagesClass = imagesClassList1.get(i);

                        File file = new File(imagesClass.getImagePath());

                        if (file.exists())
                        {
                            try
                            {
                                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                                Bitmap bitmapBuildingImage = BitmapFactory.decodeFile(imagesClass.getImagePath(), bmOptions);

                                ByteArrayOutputStream byteArrayOutputStreamCam1 = new ByteArrayOutputStream();
                                getResizedBitmap(bitmapBuildingImage, 500).compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStreamCam1);

                                Image imageCam1 =Image.getInstance(byteArrayOutputStreamCam1.toByteArray());
                                imageCam1.setAlignment(Image.ALIGN_CENTER);
                                imageCam1.scaleAbsolute(260f, 260f);

                                cells = new PdfPCell(imageCam1);
                                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                                cells.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
                                cells.setPadding(5);
                                cells.setColspan(47);
                                cells.setBorderWidth(borderwidth);
                                cells.setBorderColor(BaseColor.BLACK);
                                table.addCell(cells);
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, e.toString());
                            }
                        }
                        else
                        {
                            cells = new PdfPCell(new Phrase("BUILDING IMAGE", smallBold));
                            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cells.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
                            cells.setColspan(47);
                            cells.setBorderWidth(borderwidth);
                            cells.setPaddingTop(130);
                            cells.setPaddingBottom(130);
                            cells.setBorderColor(BaseColor.BLACK);
                            table.addCell(cells);
                        }
                    }
                }
                else
                {
                    cells = new PdfPCell(new Phrase("BUILDING IMAGE", smallBold));
                    cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cells.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
                    cells.setColspan(47);
                    cells.setBorderWidth(borderwidth);
                    cells.setPaddingTop(130);
                    cells.setPaddingBottom(130);
                    cells.setBorderColor(BaseColor.BLACK);
                    table.addCell(cells);
                }


                cells = new PdfPCell(new Phrase(""));
                cells.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cells.setBorder(Rectangle.RIGHT);
                cells.setColspan(3);
                table.addCell(cells);

                //SPACING
                cells = new PdfPCell(new Phrase("\n"));
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setBorder(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.RIGHT);
                cells.setColspan(100);
                table.addCell(cells);

                document.add(table);
            }
            catch (Exception e)
            {
                Log.e(TAG, "SKETCH & BUILDING IMAGE: "  + e.toString());
            }
            //endregion

            //region SOIL TYPE & FALLING HAZARDS
            try
            {
                table = new PdfPTable(2);
                table.setWidthPercentage(100);

                //Soil Type
                phrase = new Phrase();
                phrase.add(new Chunk("Soil Type: ", header2));

                phrase.setFont(smallNormal);

                Cursor cursor3 = RepositorySoilTypes.realAllData(getContext());

                if (cursor3.getCount()!=0)
                {
                    if (cursor3.moveToFirst())
                    {
                        String SoilType1 = "";

                        int selectedIdSoilType = rg_SoilType.getCheckedRadioButtonId();

                        if (selectedIdSoilType != -1)
                        {
                            rb_SoilType = view.findViewById(selectedIdSoilType);
                            SoilType1   = rb_SoilType.getText().toString();
                        }

                        do
                        {
                            String BuildingSoilTypeCode = cursor3.getString(cursor3.getColumnIndex("BuildingSoilTypeCode"));
                            String BuildingSoilTypeDesc = cursor3.getString(cursor3.getColumnIndex("BuildingSoilTypeDesc"));
                            String SoilType2 = BuildingSoilTypeCode + ". " + BuildingSoilTypeDesc;

                            phrase.add("\n");
                            if (SoilType2.equals(SoilType1))
                            {
                                phrase.add(new Chunk(imageCheck, 3, -1));
                            }
                            else
                            {
                                phrase.add(new Chunk(imageUncheck, 3, -1));
                            }
                            phrase.add("   ");
                            phrase.add(new Chunk(BuildingSoilTypeCode, header2));
                            phrase.add(" - " + BuildingSoilTypeDesc);
                        }
                        while (cursor3.moveToNext());
                    }
                }

                cells = new PdfPCell();
                cells.addElement(phrase);
                cells.setColspan(1);
                cells.setBorder(Rectangle.LEFT | Rectangle.TOP);
                cells.setPaddingLeft(5);
                cells.setPaddingBottom(10);
                table.addCell(cells);


                //Falling Hazards
                phrase = new Phrase();
                phrase.add(new Chunk("Falling Hazards: ", header2));

                phrase.setFont(smallNormal);

                int iFallingHazardsCount = fallingHazardsModelList.size();

                for(int i=0; i < iFallingHazardsCount ; i++)
                {
                    FallingHazardsModel fallingHazardsModel = fallingHazardsModelList.get(i);

                    String Description = fallingHazardsModel.getFallingHazardDesc();
                    String OthersField1 = fallingHazardsModel.getOthersField();

                    phrase.add("\n");

                    Cursor c = RepositorySelectedFallingHazards.realAllData2(getContext(), UserAccountID,
                            MissionOrderID, SeismicityRegion, Description);

                    if (c.getCount()!=0)
                    {
                        if (c.moveToFirst())
                        {
                            String FallingHazardDesc = c.getString(c.getColumnIndex("FallingHazardDesc"));
                            String OthersField       = c.getString(c.getColumnIndex("OthersField"));

                            if (FallingHazardDesc.equalsIgnoreCase(Description))
                            {
                                phrase.add(new Chunk(imageCheck, 3, -1));

                                if (FallingHazardDesc.equalsIgnoreCase("Others"))
                                {
                                    phrase.add("   " + Description + " : ");
                                    phrase.add(new Chunk(OthersField, smallNormal2));
                                }
                                else
                                {
                                    phrase.add("   " + Description);
                                }
                            }
                            else
                            {
                                phrase.add(new Chunk(imageUncheck, 3, -1));
                                phrase.add("   " + Description + " ");
                            }
                        }
                    }
                    else
                    {
                        if (fallingHazardsModel.getIsActive().equals("1"))
                        {
                            phrase.add(new Chunk(imageCheck, 3, -1));
                        }
                        else
                        {
                            phrase.add(new Chunk(imageUncheck, 3, -1));
                        }

                        phrase.add("   " + Description + "  ");
                        phrase.add(new Chunk(OthersField1, smallNormal2));
                    }
                }

                cells = new PdfPCell();
                cells.addElement(phrase);
                cells.setHorizontalAlignment(Element.ALIGN_LEFT);
                cells.setBorder(Rectangle.RIGHT | Rectangle.TOP);
                cells.setColspan(1);
                cells.setPaddingBottom(10);
                table.addCell(cells);


                cells = new PdfPCell(new Phrase(""));
                cells.setBorder(Rectangle.BOTTOM);
                cells.setColspan(2);
                table.addCell(cells);

                document.add(table);
            }
            catch (Exception e)
            {
                Log.e(TAG, "SOIL TYPE & FALLING HAZARDS: "  + e.toString());
            }
            //endregion

            //region BASIC SCORE, MODIFIERS, AND FINAL SCORE, S
            try
            {
                document.newPage();

                table = new PdfPTable(3);
                table.setWidthPercentage(100);

                phrase = new Phrase();
                phrase.add(new Chunk("BASIC SCORE, MODIFIERS, AND FINAL SCORE, ", catFont4));
                phrase.add(new Chunk("S", catFont5));

                cells = new PdfPCell(phrase);
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setBorder(Rectangle.BOX);
                cells.setColspan(3);
                cells.setPaddingTop(20);
                cells.setPaddingBottom(20);
                table.addCell(cells);


                String FinalScore1 = "", FinalScore2 = "";

                Cursor cursor1 = RepositoryFinalBuildingScores.realAllData3(getContext(), UserAccountID, MissionOrderID);

                if (cursor1.getCount()!=0)
                {
                    if (cursor1.moveToFirst())
                    {
                        if (cursor1.getString(cursor1.getColumnIndex("Category1")) != null)
                        {
                            String Category1     = cursor1.getString(cursor1.getColumnIndex("Category1"));
                            String BuildingType1 = cursor1.getString(cursor1.getColumnIndex("BuildingType1"));
                            FinalScore1          = cursor1.getString(cursor1.getColumnIndex("FinalScore1"));

                            String Category2 = "", BuildingType2 = "";

                            if (cursor1.getString(cursor1.getColumnIndex("Category2")) != null)
                            {
                                Category2     = cursor1.getString(cursor1.getColumnIndex("Category2"));
                                BuildingType2 = cursor1.getString(cursor1.getColumnIndex("BuildingType2"));
                                FinalScore2   = cursor1.getString(cursor1.getColumnIndex("FinalScore2"));
                            }

                            //MODIFIERS
                            table2 = new PdfPTable(1);
                            table2.setWidthPercentage(100);

                            pCell = new PdfPCell(new Phrase("Seismicity Region", header2));
                            pCell.setColspan(1);
                            pCell.setBorder(Rectangle.NO_BORDER);
                            pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            pCell.setPaddingTop(15);
                            table2.addCell(pCell);

                            pCell = new PdfPCell(new Phrase("Building Type", header2));
                            pCell.setColspan(1);
                            pCell.setBorder(Rectangle.NO_BORDER);
                            pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            pCell.setPaddingTop(15);
                            table2.addCell(pCell);

                            cells = new PdfPCell();
                            cells.addElement(table2);
                            cells.setColspan(1);
                            cells.setBorder(Rectangle.LEFT);
                            pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cells.setPaddingTop(20);
                            cells.setPaddingLeft(5);
                            table.addCell(cells);


                            //BASIC SCORE 1
                            table2 = new PdfPTable(1);
                            table2.setWidthPercentage(100);

                            pCell = new PdfPCell(new Phrase(Category1, smallNormal));
                            pCell.setColspan(1);
                            pCell.setBorder(Rectangle.BOTTOM);
                            pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            pCell.setPaddingTop(15);
                            table2.addCell(pCell);

                            pCell = new PdfPCell(new Phrase(BuildingType1, smallNormal));
                            pCell.setColspan(1);
                            pCell.setBorder(Rectangle.BOTTOM);
                            pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            pCell.setPaddingTop(15);
                            table2.addCell(pCell);

                            cells = new PdfPCell();
                            cells.addElement(table2);
                            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cells.setBorder(Rectangle.NO_BORDER);
                            cells.setColspan(1);
                            cells.setPaddingLeft(10);
                            cells.setPaddingRight(10);
                            cells.setPaddingTop(20);
                            table.addCell(cells);


                            //BASIC SCORE 2
                            table2 = new PdfPTable(1);
                            table2.setWidthPercentage(100);

                            if (!Category2.equals(""))
                            {
                                pCell = new PdfPCell(new Phrase(Category2, smallNormal));
                                pCell.setColspan(1);
                                pCell.setBorder(Rectangle.BOTTOM);
                            }
                            else
                            {
                                pCell = new PdfPCell(new Phrase("", smallNormal));
                                pCell.setColspan(1);
                                pCell.setBorder(Rectangle.NO_BORDER);
                            }

                            pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            pCell.setPaddingTop(15);
                            table2.addCell(pCell);

                            if (!BuildingType2.equals(""))
                            {
                                pCell = new PdfPCell(new Phrase(BuildingType2, smallNormal));
                                pCell.setColspan(1);
                                pCell.setBorder(Rectangle.BOTTOM);
                            }
                            else
                            {
                                pCell = new PdfPCell(new Phrase("", smallNormal));
                                pCell.setColspan(1);
                                pCell.setBorder(Rectangle.NO_BORDER);
                            }

                            pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            pCell.setPaddingTop(15);
                            table2.addCell(pCell);

                            cells = new PdfPCell();
                            cells.addElement(table2);
                            cells.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                            cells.setBorder(Rectangle.RIGHT);
                            cells.setColspan(1);
                            cells.setPaddingLeft(10);
                            cells.setPaddingRight(10);
                            cells.setPaddingTop(20);
                            table.addCell(cells);

                            Cursor cursor4 = RepositoryTempBuildingScores.selectBuildingScores4(getContext(), UserAccount.UserAccountID, MissionOrderID,
                                    Category1, BuildingType1, BuildingType2);

                            if (cursor4.getCount()!=0)
                            {
                                cursor4.moveToFirst();

                                do
                                {
                                    String Modifiers      = cursor4.getString(cursor4.getColumnIndex("Modifiers"));
                                    String BuildingScore1 = cursor4.getString(cursor4.getColumnIndex("BuildingScore1"));

                                    //MODIFIERS
                                    table2 = new PdfPTable(1);
                                    table2.setWidthPercentage(100);

                                    pCell = new PdfPCell(new Phrase(Modifiers, header2));
                                    pCell.setColspan(1);
                                    pCell.setBorder(Rectangle.NO_BORDER);
                                    pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                    pCell.setPaddingTop(10);
                                    table2.addCell(pCell);

                                    cells = new PdfPCell();
                                    cells.addElement(table2);
                                    cells.setColspan(1);
                                    cells.setBorder(Rectangle.LEFT);
                                    pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                    cells.setPaddingLeft(5);
                                    cells.setPaddingBottom(5);
                                    table.addCell(cells);


                                    //BUILDING SCORE 1
                                    table2 = new PdfPTable(1);
                                    table2.setWidthPercentage(100);

                                    Log.e(TAG, "BuildingScore1: " + BuildingScore1 +"-");

                                    if (BuildingScore1 != null)
                                    {
                                        pCell = new PdfPCell(new Phrase(BuildingScore1, smallNormal));
                                        pCell.setColspan(1);
                                    }
                                    else
                                    {
                                        pCell = new PdfPCell(new Phrase(" ", smallNormal));
                                        pCell.setColspan(1);
                                    }
                                    if (!BuildingType1.equals(""))
                                    {
                                        pCell.setBorder(Rectangle.BOTTOM);
                                    }
                                    else
                                    {
                                        pCell.setBorder(Rectangle.NO_BORDER);
                                    }
                                    pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    pCell.setPaddingTop(10);
                                    table2.addCell(pCell);


                                    cells = new PdfPCell();
                                    cells.addElement(table2);
                                    cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    cells.setBorder(Rectangle.NO_BORDER);
                                    cells.setColspan(1);
                                    cells.setPaddingLeft(10);
                                    cells.setPaddingRight(10);
                                    cells.setPaddingBottom(5);
                                    table.addCell(cells);


                                    //BUILDING SCORE 2
                                    table2 = new PdfPTable(1);
                                    table2.setWidthPercentage(100);

                                    if (!BuildingType2.equals(""))
                                    {
                                        if (cursor4.getString(cursor4.getColumnIndex("BuildingScore2")) != null)
                                        {
                                            String BuildingScore2 = cursor4.getString(cursor4.getColumnIndex("BuildingScore2"));
                                            pCell = new PdfPCell(new Phrase(BuildingScore2, smallNormal));
                                        }
                                        else
                                        {
                                            pCell = new PdfPCell(new Phrase(" ", smallNormal));
                                        }
                                        pCell.setColspan(1);
                                        pCell.setBorder(Rectangle.BOTTOM);
                                    }
                                    else
                                    {
                                        pCell = new PdfPCell(new Phrase(" ", smallNormal));
                                        pCell.setColspan(1);
                                        pCell.setBorder(Rectangle.NO_BORDER);
                                    }
                                    pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    pCell.setPaddingTop(10);
                                    table2.addCell(pCell);


                                    cells = new PdfPCell();
                                    cells.addElement(table2);
                                    cells.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                                    cells.setBorder(Rectangle.RIGHT);
                                    cells.setColspan(1);
                                    cells.setPaddingLeft(10);
                                    cells.setPaddingRight(10);
                                    cells.setPaddingBottom(5);
                                    table.addCell(cells);

                                }
                                while (cursor4.moveToNext());
                            }
                        }
                    }
                }

                cells = new PdfPCell(new Phrase("\n"));
                cells.setBorder(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.RIGHT);
                cells.setColspan(3);
                table.addCell(cells);


                //FINAL SCORE, S
                phrase = new Phrase();
                phrase.add(new Chunk("FINAL SCORE, ", header2));
                phrase.add(new Chunk("S", header4));

                cells = new PdfPCell();
                cells.addElement(phrase);
                cells.setColspan(3);
                cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
                cells.setPaddingTop(10);
                cells.setPaddingLeft(10);
                table.addCell(cells);


                cells = new PdfPCell();
                cells.addElement(new Phrase("(If less than 2.0, building may be\n" + "vulnerable to Seismic Hazard)", header2));
                cells.setColspan(1);
                cells.setBorder(Rectangle.LEFT);
                cells.setPaddingTop(5);
                cells.setPaddingLeft(10);
                cells.setPaddingBottom(10);
                table.addCell(cells);

                //Score 1
                paragraph = new Paragraph(FinalScore1);
                paragraph.setFont(header2);
                paragraph.setAlignment(Element.ALIGN_CENTER);

                cells = new PdfPCell();
                cells.addElement(paragraph);
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setBorder(Rectangle.NO_BORDER);
                cells.setColspan(1);
                cells.setPaddingBottom(10);
                table.addCell(cells);

                //Score 2
                paragraph = new Paragraph(FinalScore2);
                paragraph.setFont(header2);
                paragraph.setAlignment(Element.ALIGN_CENTER);

                cells = new PdfPCell();
                cells.addElement(paragraph);
                cells.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cells.setBorder(Rectangle.RIGHT);
                cells.setColspan(1);
                cells.setPaddingBottom(10);
                table.addCell(cells);

                cells = new PdfPCell(new Phrase(""));
                cells.setBorder(Rectangle.BOTTOM);
                cells.setColspan(3);
                table.addCell(cells);

                document.add(table);
            }
            catch (Exception e)
            {
                Log.e(TAG, "BASIC SCORE, MODIFIERS, AND FINAL SCORE, S: " + e.toString());
            }
            //endregion

            //region COMMENTS
            try
            {
                table = new PdfPTable(6);
                table.setWidthPercentage(100);

                cells = new PdfPCell();
                cells.addElement(new Phrase("COMMENTS: ", header2));
                cells.setColspan(4);
                cells.setPaddingTop(5);
                cells.setPaddingLeft(10);
                cells.setBorder(Rectangle.LEFT);
                table.addCell(cells);

                cells = new PdfPCell(new Phrase(""));
                cells.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
                cells.setColspan(2);
                cells.setPaddingTop(5);
                table.addCell(cells);


                //OUTPUT
                cells = new PdfPCell();
                cells.addElement(new Phrase("", header1));
                cells.setColspan(1);
                cells.setBorder(Rectangle.LEFT);
                cells.setPaddingBottom(20);
                table.addCell(cells);

                String Comments = !edt_comment.getText().toString().equals("") ?
                                    edt_comment.getText().toString():"";

                cells = new PdfPCell(new Phrase(Comments, header1));
                cells.setHorizontalAlignment(Element.ALIGN_LEFT);
                cells.setBorder(Rectangle.NO_BORDER);
                cells.setColspan(3);
                cells.setPaddingBottom(20);
                table.addCell(cells);


                //Modifiers
                cells = new PdfPCell();

                paragraph = new Paragraph("DETAILED Evaluation\n" + "Required\n\n");
                paragraph.setFont(header2);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cells.addElement(paragraph);

                paragraph = new Paragraph(DetailedEvaluation);
                paragraph.setFont(header1);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                cells.addElement(paragraph);

                cells.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cells.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
                cells.setColspan(2);
                cells.setPaddingBottom(20);
                table.addCell(cells);


                cells = new PdfPCell(new Phrase(""));
                cells.setBorder(Rectangle.BOTTOM);
                cells.setColspan(6);
                cells.setPaddingBottom(10);
                table.addCell(cells);

                document.add(table);
            }
            catch (Exception e)
            {
                Log.e(TAG, "COMMENTS: " + e.toString());
            }
            //endregion

            //region SECOND PAGE
            try
            {
                table = new PdfPTable(4);
                table.setWidthPercentage(100);

                phrase = new Phrase();
                phrase.add("*=Estimated, subjective, unreliable data");
                phrase.add("\n");
                phrase.add("DNK = Do not Know");

                paragraph = new Paragraph();
                paragraph.add(phrase);
                paragraph.setFont(small_font);
                paragraph.setAlignment(Element.ALIGN_LEFT);

                cells = new PdfPCell();
                cells.addElement(paragraph);
                cells.setHorizontalAlignment(Element.ALIGN_LEFT);
                cells.setBorder(Rectangle.NO_BORDER);
                cells.setColspan(1);
                cells.setPaddingBottom(20);
                cells.setPaddingLeft(30);
                table.addCell(cells);


                phrase = new Phrase();
                phrase.add("BR Braced Frame");
                phrase.add("\n");
                phrase.add("FD Flexible Diaphragm");
                phrase.add("\n");
                phrase.add("LM Light Metal");

                paragraph = new Paragraph();
                paragraph.add(phrase);
                paragraph.setFont(small_font);
                paragraph.setAlignment(Element.ALIGN_LEFT);

                cells = new PdfPCell();
                cells.addElement(paragraph);
                cells.setHorizontalAlignment(Element.ALIGN_LEFT);
                cells.setBorder(Rectangle.NO_BORDER);
                cells.setColspan(1);
                cells.setPaddingBottom(10);
                cells.setPaddingLeft(50);
                table.addCell(cells);


                phrase = new Phrase();
                phrase.add("MRF Moment Resisting Frame");
                phrase.add("\n");
                phrase.add("RC Reinforced Concrete");
                phrase.add("\n");
                phrase.add("RD Rigid Diaphragm");

                paragraph = new Paragraph();
                paragraph.add(phrase);
                paragraph.setFont(small_font);
                paragraph.setAlignment(Element.ALIGN_LEFT);

                cells = new PdfPCell();
                cells.addElement(paragraph);
                cells.setHorizontalAlignment(Element.ALIGN_LEFT);
                cells.setBorder(Rectangle.NO_BORDER);
                cells.setColspan(1);
                cells.setPaddingBottom(10);
                cells.setPaddingLeft(40);
                table.addCell(cells);


                phrase = new Phrase();
                phrase.add("SW Shear wall");
                phrase.add("\n");
                phrase.add("TU Tilt up");
                phrase.add("\n");
                phrase.add("URM INF");

                paragraph = new Paragraph();
                paragraph.add(phrase);
                paragraph.setFont(small_font);
                paragraph.setAlignment(Element.ALIGN_LEFT);

                cells = new PdfPCell();
                cells.addElement(paragraph);
                cells.setHorizontalAlignment(Element.ALIGN_LEFT);
                cells.setBorder(Rectangle.NO_BORDER);
                cells.setColspan(1);
                cells.setPaddingBottom(10);
                cells.setPaddingLeft(40);
                table.addCell(cells);

                document.add(table);
            }
            catch (Exception e)
            {
                Log.e(TAG, "SECOND PAGE: " + e.toString());
            }
            //endregion

            //region BACKGROUND INFORMATION & FINDINGS / OBSERVATIONS
/*            try
            {
                document.newPage();

                table = new PdfPTable(1);
                table.setWidthPercentage(100);


                cells = new PdfPCell();
                cells.addElement(new Phrase("I. BACKGROUND INFORMATION", catFont2));
                cells.setColspan(1);
                cells.setBorder(Rectangle.NO_BORDER);
                cells.setHorizontalAlignment(Element.ALIGN_LEFT);
                cells.setPaddingTop(20);
                cells.setPaddingLeft(70);
                table.addCell(cells);

                String BackgroundInformation = !edt_backgroundInformation.getText().toString().equals("") ?
                        edt_backgroundInformation.getText().toString():"";

                cells = new PdfPCell();
                cells.addElement(new Phrase(BackgroundInformation, catFont));
                cells.setColspan(1);
                cells.setBorder(Rectangle.NO_BORDER);
                cells.setHorizontalAlignment(Element.ALIGN_LEFT);
                cells.setPaddingTop(20);
                cells.setPaddingLeft(100);
                cells.setPaddingRight(30);
                table.addCell(cells);


                cells = new PdfPCell();
                cells.addElement(new Phrase("II. FINDINGS / OBSERVATIONS", catFont2));
                cells.setColspan(1);
                cells.setBorder(Rectangle.NO_BORDER);
                cells.setHorizontalAlignment(Element.ALIGN_LEFT);
                cells.setPaddingTop(40);
                cells.setPaddingLeft(70);
                table.addCell(cells);

                String FindingsObservations = !edt_findingsObservations.getText().toString().equals("") ?
                        edt_findingsObservations.getText().toString():"";

                cells = new PdfPCell();
                cells.addElement(new Phrase(FindingsObservations, catFont));
                cells.setColspan(1);
                cells.setBorder(Rectangle.NO_BORDER);
                cells.setHorizontalAlignment(Element.ALIGN_LEFT);
                cells.setPaddingTop(20);
                cells.setPaddingLeft(100);
                cells.setPaddingRight(30);
                table.addCell(cells);

                document.add(table);
                document.newPage();
            }
            catch (Exception e)
            {
                Log.e(TAG, "BACKGROUND INFORMATION & FINDINGS / OBSERVATIONS: " + e.toString());
            }*/
            //endregion

            //region RECOMMENDATIONS & SIGNATURES
            try
            {
                document.newPage();

                table = new PdfPTable(1);
                table.setWidthPercentage(100);

/*
                cells = new PdfPCell();
                cells.addElement(new Phrase("III. RECOMMENDATIONS", catFont2));
                cells.setColspan(1);
                cells.setBorder(Rectangle.NO_BORDER);
                cells.setHorizontalAlignment(Element.ALIGN_LEFT);
                cells.setPaddingTop(20);
                cells.setPaddingLeft(70);
                table.addCell(cells);

                String CommentsRecommendations = !edt_Recommendations.getText().toString().equals("") ?
                        edt_Recommendations.getText().toString():"";

                cells = new PdfPCell();
                cells.addElement(new Phrase(CommentsRecommendations, catFont));
                cells.setColspan(1);
                cells.setBorder(Rectangle.NO_BORDER);
                cells.setHorizontalAlignment(Element.ALIGN_LEFT);
                cells.setPaddingTop(10);
                cells.setPaddingLeft(100);
                cells.setPaddingRight(30);
                table.addCell(cells);
*/


                cells = new PdfPCell();
                cells.addElement(new Phrase("Attached are the photographs taken during the time of inspection for ready reference." +
                        "\n" + "Prepared by:" , catFont));
                cells.setColspan(1);
                cells.setBorder(Rectangle.NO_BORDER);
                cells.setHorizontalAlignment(Element.ALIGN_LEFT);
                cells.setPaddingTop(50);
                cells.setPaddingLeft(70);
                table.addCell(cells);


                int iScreenerCount = assignedInspectorsListModelList.size();

                for(int i=0; i < iScreenerCount ; i++)
                {
                    AssignedInspectorsListModel assignedInspectorsListModel = assignedInspectorsListModelList.get(i);

                    cells = new PdfPCell();

                    Cursor cursor1 = RepositoryInspectorSignature.realAllData(getContext(),
                            assignedInspectorsListModel.getID(), assignedInspectorsListModel.getMissionOrderID());

                    if (cursor1.getCount()!=0)
                    {
                        if (cursor1.moveToFirst())
                        {
                            String SignaturePath = cursor1.getString(cursor1.getColumnIndex("SignaturePath"));

                            File file = new File(SignaturePath);

                            if (file.exists())
                            {
                                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                                Bitmap bitmap = BitmapFactory.decodeFile(SignaturePath, bmOptions);
                                //bitmap = initImageRotateNormal(file, bitmap);

                                table2 = new PdfPTable(100);
                                table2.setWidthPercentage(100);

                                ByteArrayOutputStream byteArrayOutputStreamCam1 = new ByteArrayOutputStream();
                                getResizedBitmap(bitmap, 500).compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStreamCam1);

                                Image imageCam1 = Image.getInstance(byteArrayOutputStreamCam1.toByteArray());
                                imageCam1.setAlignment(Image.ALIGN_LEFT);
                                imageCam1.scaleAbsolute(150f, 10f);

                                pCell = new PdfPCell();
                                pCell.addElement(imageCam1);
                                pCell.setColspan(30);
                                pCell.setBorderWidth(2f);
                                pCell.setBorderColor(BaseColor.BLACK);
                                pCell.setBorder(Rectangle.NO_BORDER);
                                table2.addCell(pCell);

                                pCell = new PdfPCell(new Phrase(""));
                                pCell.setColspan(70);
                                pCell.setBorder(Rectangle.NO_BORDER);
                                table2.addCell(pCell);

                                pCell = new PdfPCell();
                                pCell.addElement(new Phrase(assignedInspectorsListModel.getInspector(), catFont2));
                                pCell.setColspan(30);
                                pCell.setBorderWidth(1f);
                                pCell.setBorderColor(BaseColor.BLACK);
                                pCell.setBorder(Rectangle.BOTTOM);
                                table2.addCell(pCell);

                                pCell = new PdfPCell(new Phrase(""));
                                pCell.setColspan(70);
                                pCell.setBorder(Rectangle.NO_BORDER);
                                table2.addCell(pCell);


                                cells.addElement(table2);
                            }
                        }
                    }
                    else
                    {
                        cells.addElement(new Phrase("_____________________", catFont4));
                    }

                    cells.addElement(new Phrase(assignedInspectorsListModel.getPosition(), header1));
                    cells.setColspan(1);
                    cells.setBorder(Rectangle.NO_BORDER);
                    cells.setPaddingLeft(70);
                    cells.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(cells);
                }



                cells = new PdfPCell();
                cells.addElement(new Phrase("\n\nNoted by:" + "\n", header1));

                Cursor cursorAdminSignature = RepositoryInspectorSignature.realAllData(getContext(),
                        "Building Admin", MissionOrderID);

                if (cursorAdminSignature.getCount()!=0)
                {
                    if (cursorAdminSignature.moveToFirst())
                    {
                        String SignaturePath = cursorAdminSignature.getString(cursorAdminSignature.getColumnIndex("SignaturePath"));

                        File file = new File(SignaturePath);

                        if (file.exists())
                        {
                            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                            Bitmap bitmap = BitmapFactory.decodeFile(SignaturePath, bmOptions);
                            //bitmap = initImageRotateNormal(file, bitmap);

                            table2 = new PdfPTable(100);
                            table2.setWidthPercentage(100);

                            ByteArrayOutputStream byteArrayOutputStreamCam1 = new ByteArrayOutputStream();
                            getResizedBitmap(bitmap, 500).compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStreamCam1);

                            Image imageCam1 = Image.getInstance(byteArrayOutputStreamCam1.toByteArray());
                            imageCam1.setAlignment(Image.ALIGN_LEFT);
                            imageCam1.scaleAbsolute(150f, 10f);

                            pCell = new PdfPCell();
                            pCell.addElement(imageCam1);
                            pCell.setColspan(30);
                            pCell.setBorderWidth(2f);
                            pCell.setBorderColor(BaseColor.BLACK);
                            pCell.setBorder(Rectangle.NO_BORDER);
                            table2.addCell(pCell);

                            pCell = new PdfPCell(new Phrase(""));
                            pCell.setColspan(70);
                            pCell.setBorder(Rectangle.NO_BORDER);
                            table2.addCell(pCell);

                            String newOwnerName = edt_BuildingAdmin.getText().toString() !=null ?
                                    edt_BuildingAdmin.getText().toString():"";

                            pCell = new PdfPCell();
                            pCell.addElement(new Phrase(newOwnerName, catFont2));
                            pCell.setColspan(30);
                            pCell.setBorderWidth(1f);
                            pCell.setBorderColor(BaseColor.BLACK);
                            pCell.setBorder(Rectangle.BOTTOM);
                            table2.addCell(pCell);

                            pCell = new PdfPCell(new Phrase(""));
                            pCell.setColspan(70);
                            pCell.setBorder(Rectangle.NO_BORDER);
                            table2.addCell(pCell);


                            cells.addElement(table2);
                        }
                        else
                        {
                            cells.addElement(new Phrase("_____________________", catFont4));
                        }
                    }
                }
                else
                {
                    cells.addElement(new Phrase("_____________________", catFont4));
                }

                String newPosition = edt_BuildingAdmin_Position.getText().toString() !=null ?
                        edt_BuildingAdmin_Position.getText().toString():"";

                cells.addElement(new Phrase(newPosition, header1));
                cells.setColspan(1);
                cells.setBorder(Rectangle.NO_BORDER);
                cells.setHorizontalAlignment(Element.ALIGN_LEFT);
                cells.setPaddingLeft(70);
                table.addCell(cells);

                document.add(table);
            }
            catch (Exception e)
            {
                Log.e(TAG, "RECOMMENDATIONS & SIGNATURES: " + e.toString());
            }
            //endregion

            //region OTHER IMAGES
            int iImageBuildingCount2 = imagesClassList2.size();

            if (iImageBuildingCount2!=0)
            {
                document.newPage();

                table = new PdfPTable(100);
                table.setWidthPercentage(100);

                cells = new PdfPCell(new Phrase("OTHER IMAGES", catFont4));
                cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                cells.setBorder(Rectangle.NO_BORDER);
                cells.setColspan(100);
                cells.setPaddingTop(20);
                cells.setPaddingBottom(20);
                table.addCell(cells);

                for(int i=0; i < iImageBuildingCount2 ; i++)
                {
                    ImagesClass imagesClass = imagesClassList2.get(i);

                    File file = new File(imagesClass.getImagePath());

                    if (file.exists())
                    {
                        try
                        {
                            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                            Bitmap bitmapBuildingImage = BitmapFactory.decodeFile(imagesClass.getImagePath(), bmOptions);

                            ByteArrayOutputStream byteArrayOutputStreamCam1 = new ByteArrayOutputStream();
                            getResizedBitmap(bitmapBuildingImage, 500).compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStreamCam1);

                            Image imageCam1 =Image.getInstance(byteArrayOutputStreamCam1.toByteArray());
                            imageCam1.setAlignment(Image.LEFT);
                            imageCam1.scaleAbsolute(175f, 150f);

                            //1
                            cells = new PdfPCell(new Phrase("", smallBold));
                            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cells.setBorder(Rectangle.NO_BORDER);
                            cells.setColspan(5);
                            table.addCell(cells);

                            cells = new PdfPCell();

                            table2 = new PdfPTable(100);
                            table2.setWidthPercentage(100);

                            pCell = new PdfPCell(imageCam1);
                            pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            pCell.setBorder(Rectangle.BOTTOM);
                            pCell.setColspan(35);
                            pCell.setPaddingTop(20);
                            pCell.setPaddingBottom(10);
                            pCell.setBorderWidth(1f);
                            pCell.setBorderColor(BaseColor.BLACK);
                            table2.addCell(pCell);

                            pCell = new PdfPCell();
                            pCell.addElement(new Phrase("Description: ", smallBold));
                            pCell.addElement(new Phrase(imagesClass.getDescription(), smallNormal));
                            pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            pCell.setBorder(Rectangle.BOTTOM);
                            pCell.setColspan(65);
                            pCell.setPaddingTop(20);
                            pCell.setPaddingLeft(5);
                            pCell.setPaddingBottom(10);
                            pCell.setBorderWidth(1f);
                            pCell.setBorderColor(BaseColor.BLACK);
                            table2.addCell(pCell);

                            table2.completeRow();
                            cells.addElement(table2);

                            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cells.setBorder(Rectangle.NO_BORDER);
                            cells.setColspan(90);
                            cells.setBorderWidth(1f);
                            cells.setBorderColor(BaseColor.BLACK);
                            table.addCell(cells);

                            cells = new PdfPCell(new Phrase("", smallBold));
                            cells.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cells.setBorder(Rectangle.NO_BORDER);
                            cells.setColspan(5);
                            table.addCell(cells);

                        }
                        catch (Exception e)
                        {
                            Log.e(TAG, e.toString());
                        }
                    }
                }

                document.add(table);
            }
            //endregion
        }
        catch (IOException | BadElementException e)
        {
            Log.e(TAG, "error here....: " + e.toString());

            pDialog.dismiss();
        }
    }

    private void ShowPreviewLayout()
    {
        ImageView iv_close;

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_layout_rvsscoring_preview);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        //window.getAttributes().windowAnimations = R.style.DialogAnimation;

        iv_close = dialog.findViewById(R.id.iv_close);

        iv_close.setOnClickListener(v ->
                dialog.dismiss()
        );

        dialog.setCancelable(true);
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private void initBuildingListeners()
    {
        btn_W1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                W1 = initValidateButton(W1, btn_W1);
            }
        });

        btn_W2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                W2 = initValidateButton(W2, btn_W2);
            }
        });

        btn_S1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                S1 = initValidateButton(S1, btn_S1);
            }
        });

        btn_S2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                S2 = initValidateButton(S2, btn_S2);
            }
        });

        btn_S3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                S3 = initValidateButton(S3, btn_S3);
            }
        });

        btn_S4.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                S4 = initValidateButton(S4, btn_S4);
            }
        });

        btn_S5.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                S5 = initValidateButton(S5, btn_S5);
            }
        });

        btn_C1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                C1 = initValidateButton(C1, btn_C1);
            }
        });

        btn_C2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                C2 = initValidateButton(C2, btn_C2);
            }
        });

        btn_C3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                C3 = initValidateButton(C3, btn_C3);
            }
        });

        btn_PC1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PC1 = initValidateButton(PC1, btn_PC1);
            }
        });

        btn_PC2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PC2 = initValidateButton(PC2, btn_PC2);
            }
        });

        btn_RM1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                RM1 = initValidateButton(RM1, btn_RM1);
            }
        });

        btn_RM2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                RM2 = initValidateButton(RM2, btn_RM2);
            }
        });

        btn_URM.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                URM =  initValidateButton(URM, btn_URM);
            }
        });

        btn_compute_final_score.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    if (selectedButtonCount == 0)
                    {
                        Toast.makeText(getContext(), "Invalid!", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        String Category     = tv_category.getText().toString();
                        String BuildingType = initComputeFinalScore();

                        Intent intent = new Intent(getActivity(), BuildingScoreActivity.class);
                        intent.putExtra("MissionOrderID", MissionOrderID);
                        intent.putExtra("Category", Category);
                        intent.putExtra("BuildingType", BuildingType);
                        intent.putExtra("NoOfStories", NoOfStories);
                        intent.putExtra("SoilType",SoilType);
                        intent.putExtra("DateBuilt",DateBuilt);
                        intent.putExtra("SelectedBuildingType", SelectedBuildingType);
                        startActivityForResult(intent, request_Code_4);
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, e.toString());
                }
            }
        });

        btn_add_admin_signature.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                initCloseKeyboard();

                edt_focus.requestFocus();
                edt_focus.clearFocus();

                final CharSequence[] options = {"Create Signature", "Capture Signature", "Attach Signature"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                            intent.putExtra("ID", "Building Admin");
                            intent.putExtra("MissionOrderID", MissionOrderID);
                            ((Activity) requireContext()).startActivityForResult(intent, 105);
                        }
                        else if (options[item].equals("Capture Signature"))
                        {
                            ImageName = "Created Signature" + "-" + sDateNow;

                            initGetPictureFromCamera(IMAGE_PICK_CAMERA_SIGNATURE_2);
                        }
                        else if (options[item].equals("Attach Signature"))
                        {
                            ImageName = "Created Signature" + "-" + sDateNow;

                            initGetPictureGallery(IMAGE_PICK_GALLERY_SIGNATURE_2);
                        }
                    }
                });
                builder.show();
            }
        });
    }

    private String initComputeFinalScore()
    {
        String result = null;

        if (W1)
        {
            result = "W1";
            SelectedBuildingType = "W1";
        }
        if (W2)
        {
            if (result != null)
            {
                result = result + "," + "W2";
                SelectedBuildingType = SelectedBuildingType + "," + "W2";
            }
            else
            {
                result = "W2";
                SelectedBuildingType = "W2";
            }
        }

        if (S1)
        {
            if (result != null)
            {
                result =  result + "," + "S1 (MRF)";
                SelectedBuildingType = SelectedBuildingType + "," + "S1";
            }
            else
            {
                result = "S1 (MRF)";
                SelectedBuildingType = "S1";
            }
        }
        if (S2)
        {
            if (result != null)
            {
                result = result + "," + "S2 (BR)";
                SelectedBuildingType = SelectedBuildingType + "," + "S2";
            }
            else
            {
                result = "S2 (BR)";
                SelectedBuildingType = "S2";
            }
        }
        if (S3)
        {
            if (result != null)
            {
                result = result + "," + "S3 (LM)";
                SelectedBuildingType = SelectedBuildingType + "," + "S3";
            }
            else
            {
                result =  "S3 (LM)";
                SelectedBuildingType = "S3";
            }
        }
        if (S4)
        {
            if (result != null)
            {
                result = result + "," +  "S4 (RC SW)";
                SelectedBuildingType = SelectedBuildingType + "," + "S4";
            }
            else
            {
                result = "S4 (RC SW)";
                SelectedBuildingType = "S4";
            }
        }
        if (S5)
        {
            if (result != null)
            {
                result = result + "," + "S5 (URM INF)";
                SelectedBuildingType = SelectedBuildingType + "," + "S5";
            }
            else
            {
                result = "S5 (URM INF)";
                SelectedBuildingType = "S5";
            }
        }

        if (C1)
        {
            if (result != null)
            {
                result = result + "," + "C1 (MRF)";
                SelectedBuildingType = SelectedBuildingType + "," + "C1";
            }
            else
            {
                result =  "C1 (MRF)";
                SelectedBuildingType = "C1";
            }
        }
        if (C2)
        {
            if (result != null)
            {
                result = result + "," +  "C2 (SW)";
                SelectedBuildingType = SelectedBuildingType + "," + "C2";
            }
            else
            {
                result =  "C2 (SW)";
                SelectedBuildingType = "C2";
            }
        }
        if (C3)
        {
            if (result != null)
            {
                result = result + "," + "C3 (URM INF)";
                SelectedBuildingType = SelectedBuildingType + "," + "C3";
            }
            else
            {
                result = "C3 (URM INF)";
                SelectedBuildingType = "C3";
            }
        }

        if (PC1)
        {
            if (result != null)
            {
                result = result + "," + "PC1 (TU)";
                SelectedBuildingType = SelectedBuildingType + "," + "PC1";
            }
            else
            {
                result = "PC1 (TU)";
                SelectedBuildingType = "PC1";
            }
        }
        if (PC2)
        {
            if (result != null)
            {
                result = result + "," + "PC2";
                SelectedBuildingType = SelectedBuildingType + "," + "PC2";
            }
            else
            {
                result = "PC2";
                SelectedBuildingType = "PC2";
            }
        }

        if (RM1)
        {
            if (result != null)
            {
                result = result + "," + "RM1 (FD)";
                SelectedBuildingType = SelectedBuildingType + "," + "RM1";
            }
            else
            {
                result = "RM1 (FD)";
                SelectedBuildingType = "RM1";
            }
        }
        if (RM2)
        {
            if (result != null)
            {
                result = result + "," + "RM2 (RD)";
                SelectedBuildingType = SelectedBuildingType + "," + "RM2";
            }
            else
            {
                result = "RM2 (RD)";
                SelectedBuildingType = "RM2";
            }
        }

        if (URM)
        {
            if (result != null)
            {
                result = result + "," + "URM";
                SelectedBuildingType = SelectedBuildingType + "," + "URM";
            }
            else
            {
                result = "URM";
                SelectedBuildingType = "URM";
            }
        }
        return result;
    }




    private boolean initValidateButton(boolean condition, Button button)
    {
        if (selectedButtonCount == 2)
        {
            if (condition)
            {
                return initUnselectButton(button);
            }
            else
            {
                Toast.makeText(getContext(), "You can only select a maximum of 2 Building Types.", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            if (condition)
            {
                return initUnselectButton(button);
            }
            else
            {
                return initSelectButton(button);
            }
        }
        return  false;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private boolean initSelectButton(Button button1)
    {
        try
        {
            selectedButtonCount = selectedButtonCount + 1;

            if (selectedButtonCount == 1)
            {
                btn_compute_final_score.setEnabled(true);

                initEnableDisableButtons(false, btn_compute_final_score, null, null, null);
            }

            button1.setTextColor(Color.WHITE);
            button1.setBackground(requireContext().getDrawable(R.drawable.custom_blue_background_round_active_20));
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
        return true;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private boolean initUnselectButton(Button button1)
    {
        try
        {
            if (selectedButtonCount != 0)
            {
                selectedButtonCount = selectedButtonCount - 1;

                if (selectedButtonCount == 0)
                {
                    btn_compute_final_score.setEnabled(false);
                    initEnableDisableButtons(true, btn_compute_final_score, null, null, null);
                }

                button1.setTextColor(Color.BLACK);
                button1.setBackground(requireContext().getDrawable(R.drawable.custom_gray_background_round_20));
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
        return false;
    }

    public static void initEnableDisableButtons(boolean status, Button button, ImageView imageView, ImageView imageView2, ImageView imageView3)
    {
        if (status)
        {
            AlphaAnimation alpha = new AlphaAnimation(0.5F, 0.5F);
            alpha.setDuration(0);
            alpha.setFillAfter(true);

            if (button != null)
            {
                button.startAnimation(alpha);
            }

            if (imageView!= null)
            {
                imageView.startAnimation(alpha);
            }

            if (imageView2!=null)
            {
                imageView2.startAnimation(alpha);
            }

            if (imageView3!=null)
            {
                imageView3.startAnimation(alpha);
            }
        }
        else
        {
            if (button != null)
            {
                button.clearAnimation();
            }

            if (imageView!= null)
            {
                imageView.clearAnimation();
            }

            if (imageView2!=null)
            {
                imageView2.clearAnimation();
            }

            if (imageView3!=null)
            {
                imageView3.clearAnimation();
            }
        }
    }




    /*BACKEND*/
    @Override
    public void onResume()
    {
        super.onResume();

        initGetBuildingInformation();

        initSetBackgroundBuildingType(SeismicityRegion);

        if (!btn_compute_final_score.isEnabled())
        {
            initEnableDisableButtons(true, btn_compute_final_score, null, null, null);
        }

        if (!iv_camera.isEnabled() && !iv_gallery.isEnabled())
        {
            initEnableDisableButtons(true, null, iv_camera, iv_gallery, null);
        }

        if (!iv_sketch.isEnabled() && !iv_sketch_camera.isEnabled() && !iv_sketch_gallery.isEnabled())
        {
            initEnableDisableButtons(true, null, iv_sketch, iv_sketch_camera, iv_sketch_gallery);
        }

        initSetDataAssignedInspectorSignature();

        initDisplayAdminSignature();

        if (pDialog.isShowing())
        {
            pDialog.dismiss();
        }
    }

    @SuppressLint("SetTextI18n")
    private void initSetBackgroundBuildingType(String Category)
    {
        try
        {
            boolean iW1 = false, iW2 = false,
                    iS1 = false, iS2 = false, iS3 = false, iS4 = false, iS5 = false,
                    iC1= false , iC2= false , iC3= false ,
                    iPC1= false, iPC2= false,
                    iRM1= false, iRM2= false,
                    iURM= false;

            W1 = false;
            W2 = false;
            S1  = false;
            S2  = false;
            S3  = false;
            S4  = false;
            S5  = false;
            C1 = false;
            C2 = false;
            C3 = false;
            PC1 = false;
            PC2 = false;
            RM1 = false;
            RM2 = false;
            URM= false;

            selectedButtonCount = 0;

            switch (Category.toLowerCase())
            {
                case "high":
                    Category = "High Seismicity";
                    break;
                case "moderate":
                case "medium":
                    Category = "Moderate Seismicity";
                    break;
                case "low":
                case "very low":
                    Category = "Low Seismicity";
                    break;

            }

            Cursor cursor = RepositoryFinalBuildingScores.realAllData(getContext(), UserAccount.UserAccountID, MissionOrderID, Category);

            if (cursor.getCount()!=0)
            {
                if (cursor.moveToFirst())
                {
                    if (cursor.getString(cursor.getColumnIndex("BuildingType")) != null)
                    {
                        double iFinalScore1 = 0.0, iFinalScore2 = 0.0;

                        do
                        {
                            String FinalScore   = cursor.getString(cursor.getColumnIndex("FinalScore"));
                            String BuildingType = cursor.getString(cursor.getColumnIndex("BuildingType"));

                            if (iFinalScore1 == 0)
                            {
                                iFinalScore1 = Double.parseDouble(FinalScore);
                            }
                            else
                            {
                                iFinalScore2 = Double.parseDouble(FinalScore);
                            }

                            switch (BuildingType)
                            {
                                case "W1":
                                    btn_W1.setText(BuildingType + "         " + FinalScore);
                                    iW1 = true;

                                    if(!W1)
                                    {
                                        W1 = true;
                                        selectedButtonCount = selectedButtonCount + 1;
                                        btn_W1.setBackgroundResource(R.drawable.custom_blue_background_round_active_20);
                                        btn_W1.setTextColor(Color.WHITE);
                                    }
                                    break;
                                case "W2":
                                    btn_W2.setText(BuildingType + "         " + FinalScore);
                                    iW2 = true;
                                    if (!W2)
                                    {
                                        W2 = true;
                                        selectedButtonCount = selectedButtonCount + 1;
                                        btn_W2.setBackgroundResource(R.drawable.custom_blue_background_round_active_20);
                                        btn_W2.setTextColor(Color.WHITE);
                                    }
                                    break;
                                case "S1 (MRF)":
                                    btn_S1.setText(BuildingType + "         " + FinalScore);
                                    iS1 = true;

                                    if (!S1)
                                    {
                                        S1 = true;
                                        selectedButtonCount = selectedButtonCount + 1;
                                        btn_S1.setBackgroundResource(R.drawable.custom_blue_background_round_active_20);
                                        btn_S1.setTextColor(Color.WHITE);
                                    }
                                    break;
                                case "S2 (BR)":
                                    btn_S2.setText(BuildingType + "         " + FinalScore);
                                    iS2 = true;


                                    if (!S2)
                                    {
                                        S2 = true;
                                        selectedButtonCount = selectedButtonCount + 1;
                                        btn_S2.setBackgroundResource(R.drawable.custom_blue_background_round_active_20);
                                        btn_S2.setTextColor(Color.WHITE);
                                    }
                                    break;
                                case "S3 (LM)":
                                    btn_S3.setText(BuildingType + "         " + FinalScore);
                                    iS3 = true;

                                    if (!S3)
                                    {
                                        S3 = true;
                                        selectedButtonCount = selectedButtonCount + 1;
                                        btn_S3.setBackgroundResource(R.drawable.custom_blue_background_round_active_20);
                                        btn_S3.setTextColor(Color.WHITE);
                                    }
                                    break;
                                case "S4 (RC SW)":
                                    btn_S4.setText(BuildingType + "         " + FinalScore);
                                    iS4 = true;
                                    if (!S4)
                                    {
                                        S4 = true;
                                        selectedButtonCount = selectedButtonCount + 1;
                                        btn_S4.setBackgroundResource(R.drawable.custom_blue_background_round_active_20);
                                        btn_S4.setTextColor(Color.WHITE);
                                    }
                                    break;
                                case "S5 (URM INF)":
                                    btn_S5.setText(BuildingType + "         " + FinalScore);
                                    iS5 = true;

                                    if (!S5)
                                    {
                                        S5 = true;
                                        selectedButtonCount = selectedButtonCount + 1;
                                        btn_S5.setBackgroundResource(R.drawable.custom_blue_background_round_active_20);
                                        btn_S5.setTextColor(Color.WHITE);
                                    }
                                    break;
                                case "C1 (MRF)":
                                    btn_C1.setText(BuildingType + "         " + FinalScore);
                                    iC1 = true;

                                    if (!C1)
                                    {
                                        C1 = true;
                                        selectedButtonCount = selectedButtonCount + 1;
                                        btn_C1.setBackgroundResource(R.drawable.custom_blue_background_round_active_20);
                                        btn_C1.setTextColor(Color.WHITE);
                                    }
                                    break;
                                case "C2 (SW)":
                                    btn_C2.setText(BuildingType + "         " + FinalScore);
                                    iC2 = true;

                                    if (!C2)
                                    {
                                        C2 = true;
                                        selectedButtonCount = selectedButtonCount + 1;
                                        btn_C2.setBackgroundResource(R.drawable.custom_blue_background_round_active_20);
                                        btn_C2.setTextColor(Color.WHITE);
                                    }
                                    break;
                                case "C3 (URM INF)":
                                    btn_C3.setText(BuildingType + "         " + FinalScore);
                                    iC3 = true;

                                    if (!C3)
                                    {
                                        C3 = true;
                                        selectedButtonCount = selectedButtonCount + 1;
                                        btn_C3.setBackgroundResource(R.drawable.custom_blue_background_round_active_20);
                                        btn_C3.setTextColor(Color.WHITE);
                                    }
                                    break;
                                case "PC1 (TU)":
                                    btn_PC1.setText(BuildingType + "         " + FinalScore);
                                    iPC1 = true;

                                    if (!PC1)
                                    {
                                        PC1 = true;
                                        selectedButtonCount = selectedButtonCount + 1;
                                        btn_PC1.setBackgroundResource(R.drawable.custom_blue_background_round_active_20);
                                        btn_PC1.setTextColor(Color.WHITE);
                                    }
                                    break;
                                case "PC2":
                                    btn_PC2.setText(BuildingType + "         " + FinalScore);
                                    iPC2 = true;

                                    if (!PC2)
                                    {
                                        PC2 = true;
                                        selectedButtonCount = selectedButtonCount + 1;
                                        btn_PC2.setBackgroundResource(R.drawable.custom_blue_background_round_active_20);
                                        btn_PC2.setTextColor(Color.WHITE);
                                    }
                                    break;
                                case "RM1 (FD)":
                                    btn_RM1.setText(BuildingType + "         " + FinalScore);
                                    iRM1 = true;

                                    if (!RM1)
                                    {
                                        RM1 = true;
                                        selectedButtonCount = selectedButtonCount + 1;
                                        btn_RM1.setBackgroundResource(R.drawable.custom_blue_background_round_active_20);
                                        btn_RM1.setTextColor(Color.WHITE);
                                    }
                                    break;
                                case "RM2 (RD)":
                                    btn_RM2.setText(BuildingType + "         " + FinalScore);
                                    iRM2 = true;

                                    if (!RM2)
                                    {
                                        RM2 = true;
                                        selectedButtonCount = selectedButtonCount + 1;
                                        btn_RM2.setBackgroundResource(R.drawable.custom_blue_background_round_active_20);
                                        btn_RM2.setTextColor(Color.WHITE);
                                    }
                                    break;
                                case "URM":
                                    btn_URM.setText(BuildingType + "         " + FinalScore);
                                    iURM = true;

                                    if (!URM)
                                    {
                                        URM = true;
                                        selectedButtonCount = selectedButtonCount + 1;
                                        btn_URM.setBackgroundResource(R.drawable.custom_blue_background_round_active_20);
                                        btn_URM.setTextColor(Color.WHITE);
                                    }
                                    break;
                            }
                        }
                        while (cursor.moveToNext());

                        if (selectedButtonCount != 0)
                        {
                            btn_compute_final_score.setEnabled(true);
                        }

                        if (DetailedEvaluation == null || DetailedEvaluation.equals(""))
                        {
                            Log.e(TAG, "iFinalScore1: " + iFinalScore1 + " - " + "iFinalScore2: " + iFinalScore2);

                            if (iFinalScore1 <= iFinalScore2)
                            {
                                double FinalScore;

                                if (iFinalScore1 == 0.0)
                                {
                                    FinalScore = iFinalScore2;
                                }
                                else
                                {
                                    FinalScore = iFinalScore1;
                                }

                                if (FinalScore >= 2.0)
                                {
                                    rb_detailedValue_no.setChecked(true);
                                }
                                else
                                {
                                    rb_detailedValue_yes.setChecked(true);

                                    edt_comment.setText("A Detailed Evaluation is required to determine its seismic vulnerability and potential risks in the event of an earthquake.");
                                }

                                Log.e(TAG, "FinalScore2: " + FinalScore);
                            }
                            else if (iFinalScore1 >= iFinalScore2)
                            {
                                double FinalScore;

                                if (iFinalScore2 == 0.0)
                                {
                                    FinalScore = iFinalScore1;
                                }
                                else
                                {
                                    FinalScore = iFinalScore2;
                                }

                                if (FinalScore >= 2.0)
                                {
                                    rb_detailedValue_no.setChecked(true);
                                }
                                else
                                {
                                    rb_detailedValue_yes.setChecked(true);

                                    edt_comment.setText("A Detailed Evaluation is required to determine its seismic vulnerability and potential risks in the event of an earthquake..");
                                }


                                Log.e(TAG, "FinalScore2: " + FinalScore);
                            }
                        }
                    }
                }
            }
            else
            {
                //region Pre-Select from Building Information
                if (PreselectBuildingType != null && !PreselectBuildingType.equals(""))
                {
                    String sPreselectBuildingType1 = "", sPreselectBuildingType2 = "";

                    if (PreselectBuildingType.contains(","))
                    {
                        String[] output = PreselectBuildingType.split(",");

                        String output1 = output[0];
                        String[] finalOutput1 = output1.split("-");
                        sPreselectBuildingType1 = finalOutput1[0].replace(" ", "");

                        String output2 = output[1];
                        String[] finalOutput2 = output2.split("-");
                        sPreselectBuildingType2 = finalOutput2[0].replace(" ", "");
                    }
                    else
                    {
                        if (PreselectBuildingType.contains("-"))
                        {
                            String[] output = PreselectBuildingType.split("-");

                            sPreselectBuildingType1 = output[0].replace(" ", "");
                        }
                    }

                    if (sPreselectBuildingType1.equals("W1") || sPreselectBuildingType2.equals("W1"))
                    {
                        if (!W1)
                        {
                            iW1 = true;
                            W1 = true;
                            btn_W1.setBackgroundResource(R.drawable.custom_blue_background_round_active_20);
                            btn_W1.setTextColor(Color.WHITE);

                            selectedButtonCount = selectedButtonCount + 1;
                        }
                    }
                    else if (sPreselectBuildingType1.equals("W2") || sPreselectBuildingType2.equals("W2"))
                    {
                        if (!W2)
                        {
                            iW2 = true;
                            W2 = true;
                            btn_W2.setBackgroundResource(R.drawable.custom_blue_background_round_active_20);
                            btn_W2.setTextColor(Color.WHITE);

                            selectedButtonCount = selectedButtonCount + 1;
                        }
                    }
                    else if ((sPreselectBuildingType1.equals("S1") || sPreselectBuildingType1.equals("S1 (MRF)")) ||
                            (sPreselectBuildingType2.equals("S1") || sPreselectBuildingType2.equals("S1 (MRF)")))

                    {
                        if (!S1)
                        {
                            iS1 = true;
                            S1 = true;
                            btn_S1.setBackgroundResource(R.drawable.custom_blue_background_round_active_20);
                            btn_S1.setTextColor(Color.WHITE);

                            selectedButtonCount = selectedButtonCount + 1;
                        }
                    }
                    else if ((sPreselectBuildingType1.equals("S2") || sPreselectBuildingType1.equals("S2 (BR)")) ||
                            (sPreselectBuildingType2.equals("S2")  || sPreselectBuildingType2.equals("S2 (BR)")))

                    {
                        if (!S2)
                        {
                            iS2 = true;
                            S2 = true;
                            btn_S2.setBackgroundResource(R.drawable.custom_blue_background_round_active_20);
                            btn_S2.setTextColor(Color.WHITE);

                            selectedButtonCount = selectedButtonCount + 1;
                        }
                    }
                    else if ((sPreselectBuildingType1.equals("S3") || sPreselectBuildingType1.equals("S3 (LM)")) ||
                            (sPreselectBuildingType2.equals("S3")  || sPreselectBuildingType2.equals("S3 (LM)")))

                    {
                        if (!S3)
                        {
                            iS3 = true;
                            S3 = true;
                            btn_S3.setBackgroundResource(R.drawable.custom_blue_background_round_active_20);
                            btn_S3.setTextColor(Color.WHITE);

                            selectedButtonCount = selectedButtonCount + 1;
                        }
                    }
                    else if ((sPreselectBuildingType1.equals("S4") || sPreselectBuildingType1.equals("S4 (RC SW)")) ||
                            (sPreselectBuildingType2.equals("S4")  || sPreselectBuildingType2.equals("S4 (RC SW)")))

                    {
                        if (!S4)
                        {
                            iS4 = true;
                            S4 = true;
                            btn_S4.setBackgroundResource(R.drawable.custom_blue_background_round_active_20);
                            btn_S4.setTextColor(Color.WHITE);

                            selectedButtonCount = selectedButtonCount + 1;
                        }
                    }
                    else if ((sPreselectBuildingType1.equals("S5") || sPreselectBuildingType1.equals("S5 (URM INF)")) ||
                            (sPreselectBuildingType2.equals("S5")  || sPreselectBuildingType2.equals("S5 (URM INF)")))

                    {
                        if (!S5)
                        {
                            iS5 = true;
                            S5 = true;
                            btn_S5.setBackgroundResource(R.drawable.custom_blue_background_round_active_20);
                            btn_S5.setTextColor(Color.WHITE);

                            selectedButtonCount = selectedButtonCount + 1;
                        }
                    }
                    else if ((sPreselectBuildingType1.equals("C1") || sPreselectBuildingType1.equals("C1 (MRF)")) ||
                            (sPreselectBuildingType2.equals("C1")  || sPreselectBuildingType2.equals("C1 (MRF)")))

                    {
                        Log.e(TAG, "THIS IS WORKING---21312");

                        if (!C1)
                        {
                            iC1 = true;
                            C1 = true;
                            btn_C1.setBackgroundResource(R.drawable.custom_blue_background_round_active_20);
                            btn_C1.setTextColor(Color.WHITE);

                            selectedButtonCount = selectedButtonCount + 1;
                        }
                    }
                    else if ((sPreselectBuildingType1.equals("C2") || sPreselectBuildingType1.equals("C2 (SW)")) ||
                            (sPreselectBuildingType2.equals("C2")  || sPreselectBuildingType2.equals("C2 (SW)")))

                    {
                        Log.e(TAG, "THIS IS WORKING BY MJ.....");

                        if (!C2)
                        {
                            iC2 = true;
                            C2 = true;
                            btn_C2.setBackgroundResource(R.drawable.custom_blue_background_round_active_20);
                            btn_C2.setTextColor(Color.WHITE);

                            selectedButtonCount = selectedButtonCount + 1;
                        }
                    }
                    else if ((sPreselectBuildingType1.equals("C3") || sPreselectBuildingType1.equals("C3 (URM INF)")) ||
                            (sPreselectBuildingType2.equals("C3")  || sPreselectBuildingType2.equals("C3 (URM INF)")))

                    {
                        if (!C3)
                        {
                            iC3 = true;
                            C3 = true;
                            btn_C3.setBackgroundResource(R.drawable.custom_blue_background_round_active_20);
                            btn_C3.setTextColor(Color.WHITE);

                            selectedButtonCount = selectedButtonCount + 1;
                        }
                    }
                    else if ((sPreselectBuildingType1.equals("PC1") || sPreselectBuildingType1.equals("PC1 (TU)")) ||
                            (sPreselectBuildingType2.equals("PC1")  || sPreselectBuildingType2.equals("PC1 (TU)")))

                    {
                        if (!PC1)
                        {
                            iPC1 = true;
                            PC1 = true;
                            btn_PC1.setBackgroundResource(R.drawable.custom_blue_background_round_active_20);
                            btn_PC1.setTextColor(Color.WHITE);

                            selectedButtonCount = selectedButtonCount + 1;
                        }
                    }
                    else if ((sPreselectBuildingType1.equals("PC2") ||  sPreselectBuildingType2.equals("PC2")))

                    {
                        if (!PC2)
                        {
                            iPC2 = true;
                            PC2 = true;
                            btn_PC2.setBackgroundResource(R.drawable.custom_blue_background_round_active_20);
                            btn_PC2.setTextColor(Color.WHITE);

                            selectedButtonCount = selectedButtonCount + 1;
                        }
                    }
                    else if ((sPreselectBuildingType1.equals("RM1") || sPreselectBuildingType1.equals("RM1 (FD)")) ||
                            (sPreselectBuildingType2.equals("RM1")  || sPreselectBuildingType2.equals("RM2 (FD)")))

                    {
                        if (!RM1)
                        {
                            iRM1 = true;
                            RM1 = true;
                            btn_RM1.setBackgroundResource(R.drawable.custom_blue_background_round_active_20);
                            btn_RM1.setTextColor(Color.WHITE);

                            selectedButtonCount = selectedButtonCount + 1;
                        }
                    }
                    else if ((sPreselectBuildingType1.equals("RM2") || sPreselectBuildingType1.equals("RM2 (RD)")) ||
                            (sPreselectBuildingType2.equals("RM2")  || sPreselectBuildingType2.equals("RM2 (RD)")))
                    {
                        if (!RM2)
                        {
                            iRM2 = true;
                            RM2 = true;
                            btn_RM2.setBackgroundResource(R.drawable.custom_blue_background_round_active_20);
                            btn_RM2.setTextColor(Color.WHITE);

                            selectedButtonCount = selectedButtonCount + 1;
                        }
                    }
                    else if ((sPreselectBuildingType1.equals("URM") || sPreselectBuildingType2.equals("URM")))
                    {
                        if (!URM)
                        {
                            iURM = true;
                            URM = true;
                            btn_URM.setBackgroundResource(R.drawable.custom_blue_background_round_active_20);
                            btn_URM.setTextColor(Color.WHITE);

                            selectedButtonCount = selectedButtonCount + 1;
                        }
                    }

                    if (selectedButtonCount != 0)
                    {
                        btn_compute_final_score.setEnabled(true);
                    }
                }
                //endregion
            }

            //region Return Normal
            if (!iW1)
            {
                btn_W1.setText("W1");
                btn_W1.setTextColor(Color.BLACK);
                btn_W1.setBackgroundResource(R.drawable.custom_gray_background_round_20);
            }

            if (!iW2)
            {
                btn_W2.setText("W2");
                btn_W2.setBackgroundResource(R.drawable.custom_gray_background_round_20);
                btn_W2.setTextColor(Color.BLACK);
            }

            if (!iS1)
            {
                btn_S1.setText("S1 (MRF)");
                btn_S1.setBackgroundResource(R.drawable.custom_gray_background_round_20);
                btn_S1.setTextColor(Color.BLACK);
            }

            if (!iS2)
            {
                btn_S2.setText("S2 (BR)");
                btn_S2.setBackgroundResource(R.drawable.custom_gray_background_round_20);
                btn_S2.setTextColor(Color.BLACK);
            }

            if (!iS3)
            {
                btn_S3.setText("S3 (LM)");
                btn_S3.setBackgroundResource(R.drawable.custom_gray_background_round_20);
                btn_S3.setTextColor(Color.BLACK);
            }

            if (!iS4)
            {
                btn_S4.setText("S4 (RC SW)");
                btn_S4.setBackgroundResource(R.drawable.custom_gray_background_round_20);
                btn_S4.setTextColor(Color.BLACK);
            }

            if (!iS5)
            {
                btn_S5.setText("S5 (URM INF)");
                btn_S5.setBackgroundResource(R.drawable.custom_gray_background_round_20);
                btn_S5.setTextColor(Color.BLACK);
            }

            if (!iC1)
            {
                btn_C1.setText("C1 (MRF)");
                btn_C1.setBackgroundResource(R.drawable.custom_gray_background_round_20);
                btn_C1.setTextColor(Color.BLACK);
            }

            if (!iC2)
            {
                btn_C2.setText("C2 (SW)");
                btn_C2.setBackgroundResource(R.drawable.custom_gray_background_round_20);
                btn_C2.setTextColor(Color.BLACK);
            }

            if (!iC3)
            {
                btn_C3.setText("C3 (URM INF)");
                btn_C3.setBackgroundResource(R.drawable.custom_gray_background_round_20);
                btn_C3.setTextColor(Color.BLACK);
            }

            if (!iPC1)
            {
                btn_PC1.setText("PC1 (TU)");
                btn_PC1.setBackgroundResource(R.drawable.custom_gray_background_round_20);
                btn_PC1.setTextColor(Color.BLACK);
            }

            if (!iPC2)
            {
                btn_PC2.setText("PC2");
                btn_PC2.setBackgroundResource(R.drawable.custom_gray_background_round_20);
                btn_PC2.setTextColor(Color.BLACK);
            }

            if (!iRM1)
            {
                btn_RM1.setText("RM1 (FD)");
                btn_RM1.setBackgroundResource(R.drawable.custom_gray_background_round_20);
                btn_RM1.setTextColor(Color.BLACK);
            }

            if (!iRM2)
            {
                btn_RM2.setText("RM2 (RD)");
                btn_RM2.setBackgroundResource(R.drawable.custom_gray_background_round_20);
                btn_RM2.setTextColor(Color.BLACK);
            }

            if (!iURM)
            {
                btn_URM.setText("URM");
                btn_URM.setBackgroundResource(R.drawable.custom_gray_background_round_20);
                btn_URM.setTextColor(Color.BLACK);
            }
            //endregion

        }
        catch (Exception e)
        {
            Log.e(TAG, "ITO ANG ERROR: " + e.toString());
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initSetDataAssignedInspectorSignature()
    {
        try
        {
            String ScreenerID    = String.valueOf(UserAccount.employeeID);

            Cursor cursor = RepositoryOnlineAssignedInspectors.realAllData2(getContext(), ScreenerID, MissionOrderID);

            if (cursor.getCount()!=0)
            {
                int iAssignedSignatureCount = cursor.getCount();
                int isAssignedSignatured = 0;

                if (cursor.moveToFirst())
                {
                    assignedInspectorsListModelList.clear();

                    do
                    {
                        AssignedInspectorsListModel  assignedInspectorsListModel = new AssignedInspectorsListModel();

                        assignedInspectorsListModel.setID(cursor.getString(cursor.getColumnIndex("ID")));
                        assignedInspectorsListModel.setScreenerID(cursor.getString(cursor.getColumnIndex("ScreenerID")));
                        assignedInspectorsListModel.setMissionOrderID(cursor.getString(cursor.getColumnIndex("MissionOrderID")));
                        assignedInspectorsListModel.setInspector(cursor.getString(cursor.getColumnIndex("Inspector")));
                        assignedInspectorsListModel.setPosition(cursor.getString(cursor.getColumnIndex("Position")));

                        assignedInspectorsListModelList.add(assignedInspectorsListModel);

                        Cursor cursor2 = RepositoryInspectorSignature.realAllData(getContext(),
                                assignedInspectorsListModel.getID(), assignedInspectorsListModel.getMissionOrderID());

                        if (cursor2.getCount()!=0)
                        {
                            if (cursor2.moveToFirst())
                            {
                                String SignaturePath = cursor2.getString(cursor2.getColumnIndex("SignaturePath"));

                                File file = new File(SignaturePath);

                                if (file.exists())
                                {
                                    isAssignedSignatured = isAssignedSignatured + 1;
                                }
                            }
                        }
                    }
                    while (cursor.moveToNext());
                }

                cursor.close();

                rvAdapterScreenerSignature.notifyDataSetChanged();

                if (iAssignedSignatureCount == isAssignedSignatured)
                {
                    isInspectorSignature = true;
                }
                else
                {
                    isInspectorSignature = false;
                }
            }
            else
            {
                isInspectorSignature = false;
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initDisplayAdminSignature()
    {
        try
        {
            Cursor cursor = RepositoryInspectorSignature.realAllData(getContext(),
                    "Building Admin", MissionOrderID);

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

                        btn_add_admin_signature.setVisibility(View.GONE);
                        rl_admin_signature.setVisibility(View.VISIBLE);
                        Glide.with(requireContext()).load(bitmap).into(iv_admin_signature);

                        isAdminSignature = true;

                        iv_remove_admin_signature.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                final AlertDialog.Builder ADSettings = new AlertDialog.Builder(getContext());
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
                                                UserAccount.UserAccountID, MissionOrderID, "Building Admin");

                                        file.delete(); //Delete the image.

                                        initDisplayAdminSignature();
                                    }
                                });
                                ADSettings.show();
                            }
                        });
                    }
                    else
                    {
                        btn_add_admin_signature.setVisibility(View.VISIBLE);
                        rl_admin_signature.setVisibility(View.GONE);
                        isAdminSignature = false;
                    }
                }
            }
            else
            {
                btn_add_admin_signature.setVisibility(View.VISIBLE);
                rl_admin_signature.setVisibility(View.GONE);
                isAdminSignature = false;
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }




    private void initCreateImageFolder()
    {
        try
        {
            String SaveFolderName = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/SRI/" + ".Attachments";

            File fImageDirectory = new File(SaveFolderName);

            // Create a new folder if no folder name exist
            if (!fImageDirectory.exists())
            {
                fImageDirectory.mkdirs();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initCreateSketchFolder()
    {
        try
        {
            String SaveFolderName = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/SRI/" + ".Sketches";

            File fImageDirectory = new File(SaveFolderName);

            // Create a new folder if no folder name exist
            if (!fImageDirectory.exists())
            {
                fImageDirectory.mkdirs();
            }
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
            String SaveFolderName = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/SRI/" + ".Signatures";

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
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, resultCode);
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

        try
        {
            if (requestCode == request_Code && data != null)
            {
                String Result = data.getStringExtra("Result");

                if (Result!= null && Result.equalsIgnoreCase("Success"))
                {
                    initSetBuildingImageList();

                    Toast.makeText(getContext(), "Successfully added the Building Image.", Toast.LENGTH_SHORT).show();
                }
            }
            else if (requestCode == request_Code_2 && data != null)
            {
                String Result = data.getStringExtra("Result");

                if (Result!= null && Result.equalsIgnoreCase("Success"))
                {
                    initSetSketchImageList();

                    Toast.makeText(getContext(), "Successfully added the Sketched.", Toast.LENGTH_SHORT).show();
                }
            }
            else if (requestCode == request_Code_3 && data != null)
            {
                String Result = data.getStringExtra("Result");

                if (Result!= null && Result.equalsIgnoreCase("Success"))
                {
                    initSetOtherImagesAttachmentList();

                    Toast.makeText(getContext(), "Successfully added the other image.", Toast.LENGTH_SHORT).show();
                }
            }

            //
            else if (requestCode == IMAGE_PICK_CAMERA_SIGNATURE_2)
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
                            initSaveSignaturePhoto();
                        }
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, e.toString());
                }
            }
            else if (requestCode == IMAGE_PICK_GALLERY_SIGNATURE_2)
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
                    String SaveFolderName = Environment.getExternalStorageDirectory().getAbsolutePath()
                            + "/SRI/" + ".Signatures";

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
            inspectorSignatureClass.setSignatureID("Building Admin");

            inspectorSignatureClass.setSignatureName(ImageName);
            inspectorSignatureClass.setSignatureExtension(ImageExtension);
            inspectorSignatureClass.setSignaturePath(ImagePath);
            inspectorSignatureClass.setDtAdded(dateAdded);

            Cursor cursor = RepositoryInspectorSignature.realAllData2(getContext(), UserAccount.UserAccountID, MissionOrderID, "Building Admin");

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

            initDisplayAdminSignature();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }



    private void initStartToolTip()
    {
        try
        {
            btn_W1.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    String message = "Light Wood frame single-or\nmultiple- family dwellings of\none or more stories in height";

                    initShowToolTip(v, message);

                    return false;
                }
            });

            btn_W2.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    String message = "Light wood-frame buildings\nlarger than 5,000 square feet";

                    initShowToolTip(v, message);

                    return false;
                }
            });

            btn_S1.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    String message = "Steel moment-resisting\nframe buildings";

                    initShowToolTip(v, message);

                    return false;
                }
            });

            btn_S2.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    String message = "Braced steel frame buildings";

                    initShowToolTip(v, message);

                    return false;
                }
            });

            btn_S3.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    String message = "Light metal buildings";

                    initShowToolTip(v, message);

                    return false;
                }
            });

            btn_S4.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    String message = "Steel frame buildings with\ncast-in-place concrete shear walls";

                    initShowToolTip(v, message);

                    return false;
                }
            });

            btn_S5.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    String message = "Steel frame buildings with\nunreinforced masonry infill walls";

                    initShowToolTip(v, message);

                    return false;
                }
            });

            btn_C1.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    String message = "Concrete moment-resisting\nframe buildings";

                    initShowToolTip(v, message);

                    return false;
                }
            });

            btn_C2.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    String message = "Concrete shear-wall buildings";

                    initShowToolTip(v, message);

                    return false;
                }
            });

            btn_C3.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    String message = "Concrete frame buildings with\nunreinforced masonry infill walls";

                    initShowToolTip(v, message);

                    return false;
                }
            });

            btn_PC1.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    String message = "Tilt-up buildings";

                    initShowToolTip(v, message);

                    return false;
                }
            });

            btn_PC2.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    String message = "Precast concrete frame buildings";

                    initShowToolTip(v, message);

                    return false;
                }
            });

            btn_RM1.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    String message = " Reinforced masonry buildings with\nflexible floor and roof diaphragms";

                    initShowToolTip(v, message);

                    return false;
                }
            });

            btn_RM2.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    String message = "Reinforced masonry buildings with\nrigid floor and roof diaphragms";

                    initShowToolTip(v, message);

                    return false;
                }
            });

            btn_URM.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    String message = "Unreinforced masonry\nbearing-wall buildings";

                    initShowToolTip(v, message);

                    return false;
                }
            });
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initShowToolTip(View view, String message)
    {
         /*  new SimpleTooltip.Builder(getContext())
                .anchorView(view)
                .text(null)
                .textColor(Color.WHITE)
                .gravity(Gravity.TOP)
                .focusable(true)
                .animated(true)
                .transparentOverlay(false)
                .highlightShape(OverlayView.HIGHLIGHT_SHAPE_RECTANGULAR)
                .overlayOffset(0)
                .build()
                .show();*/

        new Tooltip.Builder(view, R.style.Tooltip2)
                .setText(message)
                .setGravity(Gravity.TOP)
                .setCancelable(true)
                .setDismissOnClick(true)
                .show();
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
            Log.e(TAG, e.toString());
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

    private void initCloseKeyboard()
    {
        try
        {
            View view = requireActivity().getCurrentFocus();

            if (view != null)
            {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Closing error: " + e.toString());
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize)
    {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;

        if (bitmapRatio > 1)
        {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        }
        else
        {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
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


    private void initButtonBold(EditText editText)
    {
        Spannable spannableString = new SpannableStringBuilder(editText.getText());

        spannableString.setSpan(new StyleSpan(Typeface.BOLD),
                editText.getSelectionStart(),
                editText.getSelectionEnd(), 0);

        editText.setText(spannableString);
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
                datetime = outputFormat.format(Objects.requireNonNull(convertedDate));
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
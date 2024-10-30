package com.geodata.rapida.plus.Fragment;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geodata.rapida.plus.Adapter.RVAdapterAssignedInspector;
import com.geodata.rapida.plus.Adapter.RVAdapterMissionOrderAttachment;
import com.geodata.rapida.plus.R;
import com.geodata.rapida.plus.Retrofit.Model.AssignedInspectorsListModel;
import com.geodata.rapida.plus.Retrofit.Model.MOFileAttachmentsList;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryOnlineAssignedInspectors;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryOnlineMOFileAttachmentsList;
import com.geodata.rapida.plus.SQLite.Repository.RepositoryOnlineMissionOrders;
import com.geodata.rapida.plus.Tools.MyExceptionHandler;
import com.geodata.rapida.plus.Tools.UserAccount;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MissionOrderFragment extends Fragment
{
    private static final String TAG = MissionOrderFragment.class.getSimpleName();

    View view;

    TextView tv_MissionOrderNo, tv_dateIssued, tv_BuildingName,
             tv_BuildingAdmin, tv_BuildingAdmin2, tv_BuildingAdmin2_Position, tv_Address, tv_DateAndLocation,
             tv_seismicity_region, tv_screening_date, tv_ScreeningType,
             tv_ReasonForInspector, tv_DateIssued, tv_statusPending,
             tv_no_previous_report, tv_no_mission_attachment, tv_Remarks, tv_DateReported;

    ImageView iv_admin_signature;

    LinearLayout ll_admin_signature, ll_mission_order_attachment, ll_dateReported;

    List<AssignedInspectorsListModel> assignedInspectorsListModels;
    List<MOFileAttachmentsList>  PreviousReportAttachmentList, MissionOrderAttachmentList;

    String MissionOrderID, SeismicityRegion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(getActivity())); //Getting Crash - Restart

        view = inflater.inflate(R.layout.fragment_mission_order, container, false);

        initViews();

        return view;
    }

    private void initViews()
    {
        try
        {
            Bundle extras = requireActivity().getIntent().getExtras();

            if(extras != null)
            {
                MissionOrderID   = extras.getString("MissionOrderID");

                SeismicityRegion = extras.getString("SeismicityRegion");
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        tv_MissionOrderNo   = view.findViewById(R.id.tv_MissionOrderNo);
        tv_dateIssued       = view.findViewById(R.id.tv_dateIssued);
        tv_BuildingName     = view.findViewById(R.id.tv_BuildingName);
        tv_BuildingAdmin    = view.findViewById(R.id.tv_BuildingAdmin);
        tv_BuildingAdmin2   = view.findViewById(R.id.tv_BuildingAdmin2);
        tv_BuildingAdmin2_Position = view.findViewById(R.id.tv_BuildingAdmin2_Position);
        tv_Address          = view.findViewById(R.id.tv_Address);
        tv_DateAndLocation  = view.findViewById(R.id.tv_DateAndLocation);
        tv_seismicity_region   = view.findViewById(R.id.tv_seismicity_region);
        tv_screening_date      = view.findViewById(R.id.tv_screening_date);
        tv_ScreeningType       = view.findViewById(R.id.tv_ScreeningType);
        tv_ReasonForInspector  = view.findViewById(R.id.tv_ReasonForInspector);
        tv_DateIssued          = view.findViewById(R.id.tv_DateIssued);
        tv_statusPending       = view.findViewById(R.id.tv_statusPending);
        tv_no_previous_report  = view.findViewById(R.id.tv_no_previous_report);
        tv_no_mission_attachment = view.findViewById(R.id.tv_no_mission_attachment);
        tv_Remarks               = view.findViewById(R.id.tv_Remarks);
        tv_DateReported          = view.findViewById(R.id.tv_DateReported);

        ll_admin_signature           = view.findViewById(R.id.ll_admin_signature);
        ll_mission_order_attachment  = view.findViewById(R.id.ll_mission_order_attachment);
        ll_dateReported              = view.findViewById(R.id.ll_dateReported);

        iv_admin_signature  = view.findViewById(R.id.iv_admin_signature);

        initSetData();
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void initSetData()
    {
        try
        {
            String ScreenerID = String.valueOf(UserAccount.employeeID);

            Cursor cursor = RepositoryOnlineMissionOrders.realAllData4(getContext(), ScreenerID, MissionOrderID);

            if (cursor.getCount()!=0)
            {
                if (cursor.moveToFirst())
                {
                    if (cursor.getString(cursor.getColumnIndex("DateIssued")).contains("T"))
                    {
                        String[] DateIssued =  cursor.getString(cursor.getColumnIndex("DateIssued")).split("T");
                        tv_dateIssued.setText(FormatDate(DateIssued[0]));
                        tv_DateIssued.setText(FormatDate(DateIssued[0]));
                    }

                    tv_MissionOrderNo.setText(cursor.getString(cursor.getColumnIndex("MissionOrderNo")));
                    tv_BuildingName.setText(cursor.getString(cursor.getColumnIndex("BuildingName")));

                    tv_BuildingAdmin.setText(cursor.getString(cursor.getColumnIndex("OwnerName")));

                    tv_BuildingAdmin2.setText(cursor.getString(cursor.getColumnIndex("ApprovedBy")));
                   // tv_BuildingAdmin2_Position.setText(cursor.getString(cursor.getColumnIndex("")));

                    tv_Address.setText(cursor.getString(cursor.getColumnIndex("Location")));

                    tv_screening_date.setText(cursor.getString(cursor.getColumnIndex("ScreeningSchedule")));

                    tv_DateAndLocation.setText(tv_DateAndLocation.getText().toString().replace("($Date$)", FormatDate(tv_screening_date.getText().toString())));
                    tv_DateAndLocation.setText(tv_DateAndLocation.getText().toString().replace("($Location$)", tv_Address.getText().toString()));

                    tv_seismicity_region.setText(SeismicityRegion);

                    String ScreeningType = cursor.getString(cursor.getColumnIndex("ScreeningType"));
                    tv_ScreeningType.setText(ScreeningType);

                    String ReasonForScreening = cursor.getString(cursor.getColumnIndex("ReasonForScreening"));
                    tv_ReasonForInspector.setText(ReasonForScreening);

                    if (!cursor.getString(cursor.getColumnIndex("Remarks")).equals(""))
                    {
                        tv_Remarks.setText(cursor.getString(cursor.getColumnIndex("Remarks")));
                    }
                    else
                    {
                        tv_Remarks.setTextColor(Color.parseColor("#808080"));
                        tv_Remarks.setText("None");
                    }

                    if (!cursor.getString(cursor.getColumnIndex("DateReported")).equals(""))
                    {
                        ll_dateReported.setVisibility(View.VISIBLE);

                        String[] DateReported =  cursor.getString(cursor.getColumnIndex("DateReported")).split("T");
                        tv_DateReported.setText(FormatDate(DateReported[0]));
                    }

                    String InspectionStatus = cursor.getString(cursor.getColumnIndex("InspectionStatus"));
                    tv_statusPending.setText(InspectionStatus);

                    if (!InspectionStatus.equals("") && InspectionStatus.equalsIgnoreCase("complete"))
                    {
                        tv_statusPending.setBackground(requireContext().getDrawable(R.drawable.custom_green_background));
                    }
                    else
                    {
                        tv_statusPending.setBackground(requireContext().getDrawable(R.drawable.custom_red_background));
                    }

                    initInspectorList(cursor.getString(cursor.getColumnIndex("MissionOrderID")));

                    initPreviousReportAttachments(cursor.getString(cursor.getColumnIndex("MissionOrderID")));
                    initMissionOrderAttachments(cursor.getString(cursor.getColumnIndex("MissionOrderID")));

                    if (cursor.getString(cursor.getColumnIndex("SignaturePath")) != null)
                    {
                        ll_admin_signature.setVisibility(View.VISIBLE);

                        String SaveFolderName = Environment.getExternalStorageDirectory().getAbsolutePath()
                                + "/SRI/" + ".Signatures";

                        String SignatureName = "MO-" + MissionOrderID + "-" + "BOD Signature";

                        String SignatureExtension = "png";

                        String SignaturePath = SaveFolderName + "/" + SignatureName + "." + SignatureExtension;

                        File file = new File(SignaturePath);

                        if (file.exists())
                        {
                            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                            Bitmap bitmap = BitmapFactory.decodeFile(SignaturePath, bmOptions);
                           // bitmap = initImageRotateNormal(file, bitmap);

                            iv_admin_signature.setImageBitmap(bitmap);
                        }
                        else
                        {
                            ll_admin_signature.setVisibility(View.GONE);
                        }
                    }
                    else
                    {
                        ll_admin_signature.setVisibility(View.GONE);
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
    private void initInspectorList(String MissionOrderID)
    {
        try
        {
            assignedInspectorsListModels = new ArrayList<>();

            RecyclerView rv_assign_inspector_list = view.findViewById(R.id.rv_assign_inspector_list);
            rv_assign_inspector_list.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            rv_assign_inspector_list.setLayoutManager(layoutManager);

            RVAdapterAssignedInspector rvAdapterAssignedInspector = new RVAdapterAssignedInspector(getContext(), assignedInspectorsListModels);
            rv_assign_inspector_list.setAdapter(rvAdapterAssignedInspector);

            String ScreenerID = String.valueOf(UserAccount.employeeID);

            Cursor cursor = RepositoryOnlineAssignedInspectors.realAllData2(getContext(), ScreenerID, MissionOrderID);

            if (cursor.getCount()!=0)
            {
                if (cursor.moveToFirst())
                {
                    assignedInspectorsListModels.clear();

                    do
                    {
                        AssignedInspectorsListModel assignedInspectorsListModel = new AssignedInspectorsListModel();

                        assignedInspectorsListModel.setID(cursor.getString(cursor.getColumnIndex("ID")));
                        assignedInspectorsListModel.setScreenerID(cursor.getString(cursor.getColumnIndex("ScreenerID")));
                        assignedInspectorsListModel.setMissionOrderID(cursor.getString(cursor.getColumnIndex("MissionOrderID")));
                        assignedInspectorsListModel.setInspector(cursor.getString(cursor.getColumnIndex("Inspector")));
                        boolean isTL = (cursor.getString(cursor.getColumnIndexOrThrow("isTL"))).equals("1");
                        assignedInspectorsListModel.setTL(isTL);
                        assignedInspectorsListModels.add(assignedInspectorsListModel);
                    }
                    while (cursor.moveToNext());
                }

                cursor.close();

                rvAdapterAssignedInspector.notifyDataSetChanged();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initPreviousReportAttachments(String MissionOrderID)
    {
        try
        {
            PreviousReportAttachmentList = new ArrayList<>();

            RecyclerView rv_previous_reports = view.findViewById(R.id.rv_previous_reports);
            rv_previous_reports.setHasFixedSize(true);

            RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(getContext());
            rv_previous_reports.setLayoutManager(layoutManager2);

            RVAdapterMissionOrderAttachment rvAdapterMissionOrderAttachment = new RVAdapterMissionOrderAttachment(getContext(), PreviousReportAttachmentList);
            rv_previous_reports.setAdapter(rvAdapterMissionOrderAttachment);

            String ScreenerID = String.valueOf(UserAccount.employeeID);

            Cursor cursor = RepositoryOnlineMOFileAttachmentsList.realAllData3(getContext(), ScreenerID, MissionOrderID);

            if (cursor.getCount()!=0)
            {
                if (cursor.moveToFirst())
                {
                    tv_no_previous_report.setVisibility(View.GONE);

                    PreviousReportAttachmentList.clear();

                    do
                    {
                        MOFileAttachmentsList moFileAttachmentsList = new MOFileAttachmentsList();

                        moFileAttachmentsList.setID(cursor.getString(cursor.getColumnIndex("ID")));
                        moFileAttachmentsList.setScreenerID(cursor.getString(cursor.getColumnIndex("ScreenerID")));
                        moFileAttachmentsList.setMissionOrderID(cursor.getString(cursor.getColumnIndex("MissionOrderID")));
                        moFileAttachmentsList.setMOAttachmentFilePath(cursor.getString(cursor.getColumnIndex("MOAttachmentFilePath")));
                        moFileAttachmentsList.setFileName(cursor.getString(cursor.getColumnIndex("FileName")));

                        PreviousReportAttachmentList.add(moFileAttachmentsList);
                    }
                    while (cursor.moveToNext());
                }

                cursor.close();

                rvAdapterMissionOrderAttachment.notifyDataSetChanged();
            }
            else
            {
                tv_no_previous_report.setVisibility(View.VISIBLE);
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initMissionOrderAttachments(String MissionOrderID)
    {
        try
        {
            MissionOrderAttachmentList = new ArrayList<>();

            RecyclerView rv_missionOrder_Attachments = view.findViewById(R.id.rv_missionOrder_Attachments);
            rv_missionOrder_Attachments.setHasFixedSize(true);

            RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(getContext());
            rv_missionOrder_Attachments.setLayoutManager(layoutManager2);

            RVAdapterMissionOrderAttachment rvAdapterMissionOrderAttachment = new RVAdapterMissionOrderAttachment(getContext(), MissionOrderAttachmentList);
            rv_missionOrder_Attachments.setAdapter(rvAdapterMissionOrderAttachment);

            String ScreenerID = String.valueOf(UserAccount.employeeID);

            Cursor cursor = RepositoryOnlineMOFileAttachmentsList.realAllData2(getContext(), ScreenerID, MissionOrderID);

            Log.e(TAG, "ScreenerID: " + ScreenerID + " MissionOrderID:" + MissionOrderID);

            if (cursor.getCount()!=0)
            {
                if (cursor.moveToFirst())
                {
                    tv_no_mission_attachment.setVisibility(View.GONE);

                    MissionOrderAttachmentList.clear();

                    do
                    {
                        MOFileAttachmentsList moFileAttachmentsList = new MOFileAttachmentsList();

                        moFileAttachmentsList.setID(cursor.getString(cursor.getColumnIndex("ID")));
                        moFileAttachmentsList.setScreenerID(cursor.getString(cursor.getColumnIndex("ScreenerID")));
                        moFileAttachmentsList.setMissionOrderID(cursor.getString(cursor.getColumnIndex("MissionOrderID")));
                        moFileAttachmentsList.setMOAttachmentFilePath(cursor.getString(cursor.getColumnIndex("MOAttachmentFilePath")));
                        moFileAttachmentsList.setFileName(cursor.getString(cursor.getColumnIndex("FileName")));

                        MissionOrderAttachmentList.add(moFileAttachmentsList);
                    }
                    while (cursor.moveToNext());
                }

                cursor.close();

                rvAdapterMissionOrderAttachment.notifyDataSetChanged();
            }
            else
            {
                tv_no_mission_attachment.setVisibility(View.VISIBLE);
            }
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
}
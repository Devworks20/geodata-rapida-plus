package com.geodata.rapida.plus.SQLite.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.geodata.rapida.plus.Retrofit.Model.MissionOrdersModel;
import com.geodata.rapida.plus.SQLite.Main.SQLiteDbContext;

public class RepositoryOnlineMissionOrders
{
    private static final String TAG = RepositoryOnlineMissionOrders.class.getSimpleName();

    private static final String[] allColumns =
            {
                    "ID",
                    "ScreenerID",
                    "MissionOrderType",
                    "ApprovedBy",
                    "ApprovedByID",
                    "AssetID",
                    "DateIssued",
                    "DateReported",
                    "EndorsedForApproval",
                    "EndorsedForApprovalID",
                    "InspectionStatus",
                    "InventoryYear",
                    "MissionOrderID",
                    "MissionOrderNo",
                    "ReasonForScreening",
                    "Remarks",
                    "ScreeningSchedule",
                    "ScreeningType",
                    "SignaturePath",
                    "ReportPath",
                    "isActive",
                    "dtAdded"
            };

    public static ContentValues setMissionOrdersValues(MissionOrdersModel missionOrdersModel)
    {
        ContentValues cValues = new ContentValues();

        cValues.put("ScreenerID",            missionOrdersModel.getScreenerID());
        cValues.put("MissionOrderType",      missionOrdersModel.getMissionOrderType());

        cValues.put("ApprovedBy",            missionOrdersModel.getApprovedBy());
        cValues.put("ApprovedByID",          missionOrdersModel.getApprovedByID());
        cValues.put("AssetID",               missionOrdersModel.getAssetID());
        cValues.put("DateIssued",            missionOrdersModel.getDateIssued());
        cValues.put("DateReported",          missionOrdersModel.getDateReported());
        cValues.put("EndorsedForApproval",   missionOrdersModel.getEndorsedForApproval());
        cValues.put("EndorsedForApprovalID", missionOrdersModel.getEndorsedForApprovalID());
        cValues.put("InspectionStatus",      missionOrdersModel.getInspectionStatus());
        cValues.put("InventoryYear",         missionOrdersModel.getInventoryYear());
        cValues.put("MissionOrderID",        missionOrdersModel.getMissionOrderID());
        cValues.put("MissionOrderNo",        missionOrdersModel.getMissionOrderNo());
        cValues.put("ReasonForScreening",    missionOrdersModel.getReasonForScreening());
        cValues.put("Remarks",               missionOrdersModel.getRemarks());
        cValues.put("ScreeningSchedule",     missionOrdersModel.getScreeningSchedule());
        cValues.put("ScreeningType",         missionOrdersModel.getScreeningType());
        cValues.put("SignaturePath",         missionOrdersModel.getSignaturePath());

        cValues.put("isActive",        missionOrdersModel.getIsActive());
        cValues.put("dtAdded",         missionOrdersModel.getDtAdded());

        return  cValues;
    }

    public static void saveMissionOrders(Context context, MissionOrdersModel missionOrdersModel)
    {
        ContentValues cValues = setMissionOrdersValues(missionOrdersModel);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.insert(SQLiteDbContext.TABLE_MISSION_ORDERS, null, cValues);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static Cursor realAllData(Context context, String ScreenerID, String MissionOrderNo)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_MISSION_ORDERS, allColumns,
                "ScreenerID=? AND MissionOrderNo=?", new String[]{ScreenerID, MissionOrderNo}, null, null, null);
    }

    public static void updateTheMissionOrderStatus(Context context, MissionOrdersModel missionOrdersModel)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues cValues = setMissionOrdersValues(missionOrdersModel);

        try
        {
            db.update(SQLiteDbContext.TABLE_MISSION_ORDERS, cValues, "ScreenerID=? AND MissionOrderID=?",
                    new String[]{missionOrdersModel.getScreenerID(), String.valueOf(missionOrdersModel.getMissionOrderID())});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }


    public static void updateReportPathOfMissionOrder(Context context, String ScreenerID, String MissionOrderID, String ReportPath)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues cValues = new ContentValues();
        cValues.put("ReportPath", ReportPath);

        try
        {
            db.update(SQLiteDbContext.TABLE_MISSION_ORDERS, cValues, "ScreenerID=? AND MissionOrderID=?",
                    new String[]{ScreenerID, String.valueOf(MissionOrderID)});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static Cursor realAllData2(Context context, String ScreenerID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        String query = "SELECT "                         +
                "A.ID, "                                 +
                "A.ScreenerID, "                         +
                "A.MissionOrderType, "                   +
                "A.ApprovedBy, "                         +
                "A.ApprovedByID, "                       +
                "A.AssetID, "                            +
                "A.DateIssued, "                         +
                "A.DateReported, "                       +
                "A.EndorsedForApproval, "                +
                "A.EndorsedForApprovalID, "              +
                "A.InspectionStatus, "                   +
                "A.InventoryYear, "                      +
                "A.MissionOrderID, "                     +
                "A.MissionOrderNo, "                     +
                "A.ReasonForScreening, "                 +
                "A.Remarks, "                            +
                "A.ScreeningSchedule, "                  +
                "A.ScreeningType, "                      +
                "A.SignaturePath,"                       +
                "A.ReportPath,"                          +
                "A.isActive,"                            +
                "A.dtAdded,"                             +
                "B.Seismicity, "                         +
                "B.BuildingName, "                       +
                "B.Location, "                           +
                "B.OwnerName "                           +
                "FROM "                                  +
                SQLiteDbContext.TABLE_MISSION_ORDERS + " AS A " +
                "LEFT JOIN " +
                SQLiteDbContext.TABLE_BUILDING_INFORMATION + " AS B " +
                "ON A.MissionOrderID=B.MissionOrderID " +
                "WHERE A.ScreenerID=?";

        return db.rawQuery(query, new String[]{ScreenerID});

        //return db.query(SQLiteDbContext.TABLE_MISSION_ORDERS, allColumns, null,null, null, null, null);
    }

    public static Cursor realAllData3(Context context, String ScreenerID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

     /*   String query = "SELECT "                         +
                "A.ID, "                                 +
                "A.ScreenerID, "                         +
                "A.MissionOrderType, "                   +
                "A.ApprovedBy, "                         +
                "A.ApprovedByID, "                       +
                "A.AssetID, "                            +
                "A.DateIssued, "                         +
                "A.DateReported, "                       +
                "A.EndorsedForApproval, "                +
                "A.EndorsedForApprovalID, "              +
                "A.InspectionStatus, "                   +
                "A.InventoryYear, "                      +
                "A.MissionOrderID, "                     +
                "A.MissionOrderNo, "                     +
                "A.ReasonForScreening, "                 +
                "A.Remarks, "                            +
                "A.ScreeningSchedule, "                  +
                "A.ScreeningType, "                      +
                "A.SignaturePath,"                       +
                "A.ReportPath,"                          +
                "A.isActive,"                            +
                "A.dtAdded,"                             +
                "B.Seismicity, "                         +
                "B.BuildingName, "                       +
                "B.Location, "                           +
                "B.OwnerName "                           +
                "FROM "                                  +
                SQLiteDbContext.TABLE_MISSION_ORDERS + " AS A " +
                "LEFT JOIN " +
                SQLiteDbContext.TABLE_BUILDING_INFORMATION + " AS B " +
                "ON A.MissionOrderID=B.MissionOrderID " +
                "WHERE A.ScreenerID=? AND A.MissionOrderType=?";

        return db.rawQuery(query, new String[]{ScreenerID, "EDI"});*/

        String query = "SELECT "                         +
                "A.ID, "                                 +
                "A.ScreenerID, "                         +
                "A.MissionOrderType, "                   +
                "A.ApprovedBy, "                         +
                "A.ApprovedByID, "                       +
                "A.AssetID, "                            +
                "A.DateIssued, "                         +
                "A.DateReported, "                       +
                "A.EndorsedForApproval, "                +
                "A.EndorsedForApprovalID, "              +
                "A.InspectionStatus, "                   +
                "A.InventoryYear, "                      +
                "A.MissionOrderID, "                     +
                "A.MissionOrderNo, "                     +
                "A.ReasonForScreening, "                 +
                "A.Remarks, "                            +
                "A.ScreeningSchedule, "                  +
                "A.ScreeningType, "                      +
                "A.SignaturePath,"                       +
                "A.ReportPath,"                          +
                "A.isActive,"                            +
                "A.dtAdded,"                             +
                "B.Seismicity, "                         +
                "B.BuildingName, "                       +
                "B.Location, "                           +
                "B.OwnerName "                           +
                "FROM "                                  +
                SQLiteDbContext.TABLE_MISSION_ORDERS + " AS A "       +
                "LEFT JOIN "                                          +
                SQLiteDbContext.TABLE_BUILDING_INFORMATION + " AS B " +
                "ON A.MissionOrderID=B.MissionOrderID "               +
                "WHERE A.ScreenerID=?";

        return db.rawQuery(query, new String[]{ScreenerID});
    }

    public static Cursor realAllData4(Context context, String ScreenerID, String MissionOrderID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        String query = "SELECT "                         +
                "A.ID, "                                 +
                "A.ScreenerID, "                         +
                "A.MissionOrderType, "                   +
                "A.ApprovedBy, "                         +
                "A.ApprovedByID, "                       +
                "A.AssetID, "                            +
                "A.DateIssued, "                         +
                "A.DateReported, "                       +
                "A.EndorsedForApproval, "                +
                "A.EndorsedForApprovalID, "              +
                "A.InspectionStatus, "                   +
                "A.InventoryYear, "                      +
                "A.MissionOrderID, "                     +
                "A.MissionOrderNo, "                     +
                "A.ReasonForScreening, "                 +
                "A.Remarks, "                            +
                "A.ScreeningSchedule, "                  +
                "A.ScreeningType, "                      +
                "A.SignaturePath,"                       +
                "A.ReportPath,"                          +
                "A.isActive,"                            +
                "A.dtAdded,"                             +
                "B.Seismicity, "                         +
                "B.BuildingName, "                       +
                "B.Location, "                           +
                "B.ContactNo, "                          +
                "B.OwnerName "                           +
                "FROM "                                  +
                SQLiteDbContext.TABLE_MISSION_ORDERS + " AS A "       +
                "LEFT JOIN "                                          +
                SQLiteDbContext.TABLE_BUILDING_INFORMATION + " AS B " +
                "ON A.MissionOrderID=B.MissionOrderID "               +
                "WHERE A.ScreenerID=? AND A.MissionOrderID=?";

        return db.rawQuery(query, new String[]{ScreenerID, MissionOrderID});

        //return db.query(SQLiteDbContext.TABLE_MISSION_ORDERS, allColumns, null,null, null, null, null);
    }

    public static Cursor realAllData5(Context context, String ScreenerID, String MissionOrderID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        String query = "SELECT "                         +
                "A.ID, "                                 +
                "A.ScreenerID, "                         +
                "A.MissionOrderType, "                   +
                "A.ApprovedBy, "                         +
                "A.ApprovedByID, "                       +
                "A.AssetID, "                            +
                "A.DateIssued, "                         +
                "A.DateReported, "                       +
                "A.EndorsedForApproval, "                +
                "A.EndorsedForApprovalID, "              +
                "A.InspectionStatus, "                   +
                "A.InventoryYear, "                      +
                "A.MissionOrderID, "                     +
                "A.MissionOrderNo, "                     +
                "A.ReasonForScreening, "                 +
                "A.Remarks, "                            +
                "A.ScreeningSchedule, "                  +
                "A.ScreeningType, "                      +
                "A.SignaturePath,"                       +
                "A.ReportPath,"                          +
                "A.isActive,"                            +
                "A.dtAdded,"                             +
                "B.Seismicity, "                         +
                "B.BuildingName, "                       +
                "B.Location, "                           +
                "B.ContactNo, "                          +
                "B.StructureType, "                      +
                "B.OwnerName, "                          +
                "GROUP_CONCAT(DISTINCT C.Occupancy) AS Occupancy "    +
                "FROM "                                               +
                SQLiteDbContext.TABLE_MISSION_ORDERS + " AS A "       +
                "LEFT JOIN "                                          +
                SQLiteDbContext.TABLE_BUILDING_INFORMATION + " AS B " +
                "ON A.MissionOrderID=B.MissionOrderID "               +
                "LEFT JOIN "                                          +
                SQLiteDbContext.TABLE_OCCUPANCY_LIST + " AS C "       +
                "ON A.MissionOrderID=C.MissionOrderID "               +
                "WHERE A.ScreenerID=? AND A.MissionOrderID=?";

        return db.rawQuery(query, new String[]{ScreenerID, MissionOrderID});

        //return db.query(SQLiteDbContext.TABLE_MISSION_ORDERS, allColumns, null,null, null, null, null);
    }

    public static void updateTheMissionOrderStatus(Context context, String ScreenerID, String MissionOrderID, String DateReported, String Status)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("DateReported",  DateReported);
        cv.put("InspectionStatus", Status);

        try
        {
            db.update(SQLiteDbContext.TABLE_MISSION_ORDERS, cv, "ScreenerID=? AND MissionOrderID=?", new String[]{ScreenerID, MissionOrderID});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

}

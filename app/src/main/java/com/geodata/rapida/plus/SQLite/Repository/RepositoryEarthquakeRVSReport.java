package com.geodata.rapida.plus.SQLite.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.geodata.rapida.plus.Retrofit.Model.EarthquakeRVSReportModel;
import com.geodata.rapida.plus.SQLite.Class.InspectorSignatureClass;
import com.geodata.rapida.plus.SQLite.Main.SQLiteDbContext;

public class RepositoryEarthquakeRVSReport
{
    private static final String TAG = RepositoryEarthquakeRVSReport.class.getSimpleName();

    private static final String[] allColumns =
            {
                    "ID",
                    "UserAccountID",
                    "EarthquakeRVSReportID",
                    "BuildingName",
                    "ConcreteFinalScore",
                    "EarthquakeRVSReportPdfPath",
                    "FaultDistance",
                    "FinalScore",
                    "NearestFault",
                    "ScreeningDate",
                    "Seismicity",
                    "SteelFinalScore"
            };

    public static ContentValues setValues(EarthquakeRVSReportModel earthquakeRVSReportModel)
    {
        ContentValues cValues = new ContentValues();

        cValues.put("UserAccountID",              earthquakeRVSReportModel.getUserAccountID());
        cValues.put("EarthquakeRVSReportID",      earthquakeRVSReportModel.getEarthquakeRVSReportID());
        cValues.put("BuildingName",               earthquakeRVSReportModel.getBuildingName());
        cValues.put("ConcreteFinalScore",         earthquakeRVSReportModel.getConcreteFinalScore());
        cValues.put("EarthquakeRVSReportPdfPath", earthquakeRVSReportModel.getEarthquakeRVSReportPdfPath());
        cValues.put("FaultDistance",              earthquakeRVSReportModel.getFaultDistance());
        cValues.put("FinalScore",                 earthquakeRVSReportModel.getFinalScore());
        cValues.put("NearestFault",               earthquakeRVSReportModel.getNearestFault());
        cValues.put("ScreeningDate",              earthquakeRVSReportModel.getScreeningDate());
        cValues.put("Seismicity",                 earthquakeRVSReportModel.getSeismicity());
        cValues.put("SteelFinalScore",            earthquakeRVSReportModel.getSteelFinalScore());

        return  cValues;
    }


    public static void saveEarthquakeRVSReport(Context context, EarthquakeRVSReportModel earthquakeRVSReportModel)
    {
        ContentValues cValues = setValues(earthquakeRVSReportModel);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.insert(SQLiteDbContext.TABLE_EARTHQUAKE_RVS_REPORT, null, cValues);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void updateEarthquakeRVSReport(Context context, String EarthquakeRVSReportID, EarthquakeRVSReportModel earthquakeRVSReportModel)
    {
        ContentValues cValues = new ContentValues();

        cValues.put("EarthquakeRVSReportPdfPath", earthquakeRVSReportModel.getEarthquakeRVSReportPdfPath());
        cValues.put("FaultDistance",              earthquakeRVSReportModel.getFaultDistance());
        cValues.put("NearestFault",               earthquakeRVSReportModel.getNearestFault());
        cValues.put("ScreeningDate",              earthquakeRVSReportModel.getScreeningDate());
        cValues.put("Seismicity",                 earthquakeRVSReportModel.getSeismicity());

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.update(SQLiteDbContext.TABLE_EARTHQUAKE_RVS_REPORT, cValues,
                    "EarthquakeRVSReportID=?",new String[]{EarthquakeRVSReportID});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static Cursor realAllData(Context context)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_EARTHQUAKE_RVS_REPORT, allColumns,
                null, null, null, null, null);
    }

    public static Cursor realAllData2(Context context, String UserAccountID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_EARTHQUAKE_RVS_REPORT, allColumns,
                "UserAccountID=?", new String[]{UserAccountID}, null, null, null);
    }

    public static Cursor realAllData3(Context context, String EarthquakeRVSReportID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_EARTHQUAKE_RVS_REPORT, allColumns,
                "EarthquakeRVSReportID=?", new String[]{EarthquakeRVSReportID}, null, null, null);
    }

}

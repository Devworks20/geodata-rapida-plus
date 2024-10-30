package com.geodata.rapida.plus.SQLite.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.geodata.rapida.plus.SQLite.Class.RVSSaveDraftDataClass;
import com.geodata.rapida.plus.SQLite.Class.SelectedFallingHazardsClass;
import com.geodata.rapida.plus.SQLite.Main.SQLiteDbContext;

public class RepositoryRVSSaveAsDraft
{
    private static final String TAG = RepositoryRVSSaveAsDraft.class.getSimpleName();

    private static final String[] allColumns =
            {
                    "ID",
                    "ScreenerID",
                    "MissionOrderID",
                    "Category",
                    "NoOfPersons",
                    "SoilType",
                    "Comments",
                    "DetailedEvaluation",
                    "BackgroundInformation",
                    "FindingsObservations",
                    "CommentsRecommendations",
                    "AdminName",
                    "AdminPosition"
            };

    public static ContentValues setAccountValues(RVSSaveDraftDataClass rvsSaveDraftDataClass)
    {
        ContentValues cValues = new ContentValues();

        cValues.put("ScreenerID",              rvsSaveDraftDataClass.getScreenerID());
        cValues.put("MissionOrderID",          rvsSaveDraftDataClass.getMissionOrderID());
        cValues.put("Category",                rvsSaveDraftDataClass.getCategory());
        cValues.put("NoOfPersons",             rvsSaveDraftDataClass.getNoOfPersons());
        cValues.put("SoilType",                rvsSaveDraftDataClass.getSoilType());
        cValues.put("Comments",                rvsSaveDraftDataClass.getComments());
        cValues.put("DetailedEvaluation",      rvsSaveDraftDataClass.getDetailedEvaluation());
        cValues.put("BackgroundInformation",   rvsSaveDraftDataClass.getBackgroundInformation());
        cValues.put("FindingsObservations",    rvsSaveDraftDataClass.getFindingsObservations());
        cValues.put("CommentsRecommendations", rvsSaveDraftDataClass.getCommentsRecommendations());
        cValues.put("AdminName",               rvsSaveDraftDataClass.getAdminName());
        cValues.put("AdminPosition",           rvsSaveDraftDataClass.getAdminPosition());

        return  cValues;
    }

    public static void saveRVSData(Context context, RVSSaveDraftDataClass rvsSaveDraftDataClass)
    {
        ContentValues cValues = setAccountValues(rvsSaveDraftDataClass);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.insert(SQLiteDbContext.TABLE_RVS_SCORING, null, cValues);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void updateRVSData(Context context, RVSSaveDraftDataClass rvsSaveDraftDataClass, String ID)
    {
        ContentValues cValues = setAccountValues(rvsSaveDraftDataClass);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.update(SQLiteDbContext.TABLE_RVS_SCORING, cValues, "ID=?",new String[]{ID});
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

        return db.query(SQLiteDbContext.TABLE_RVS_SCORING, allColumns, null, null, null, null, null);
    }

    public static Cursor realAllData2(Context context, String UserAccountID, String MissionOrderID, String Category)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_RVS_SCORING, allColumns,
                "ScreenerID=? AND MissionOrderID=? AND Category=?",
                new String[]{UserAccountID, MissionOrderID, Category}, null, null, null);
    }

}

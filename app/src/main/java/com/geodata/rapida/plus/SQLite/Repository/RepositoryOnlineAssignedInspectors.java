package com.geodata.rapida.plus.SQLite.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.geodata.rapida.plus.Retrofit.Model.AssignedInspectorsListModel;
import com.geodata.rapida.plus.SQLite.Main.SQLiteDbContext;

public class RepositoryOnlineAssignedInspectors
{
    private static final String TAG = RepositoryOnlineAssignedInspectors.class.getSimpleName();

    private static final String[] allColumns =
            {
                    "ID",
                    "ScreenerID",
                    "MissionOrderID",
                    "Inspector",
                    "Position",
                    "isTL"
            };

    public static ContentValues setAssignedInspectorsValues(AssignedInspectorsListModel assignedInspectorsListModel)
    {
        ContentValues cValues = new ContentValues();

        cValues.put("ScreenerID",     assignedInspectorsListModel.getScreenerID());
        cValues.put("MissionOrderID", assignedInspectorsListModel.getMissionOrderID());
        cValues.put("Inspector",      assignedInspectorsListModel.getInspector());
        cValues.put("Position",       assignedInspectorsListModel.getPosition());
        cValues.put("isTL",           assignedInspectorsListModel.getTL());

        return  cValues;
    }

    public static void saveAssignedInspectors(Context context, AssignedInspectorsListModel assignedInspectorsListModel)
    {
        ContentValues cValues = setAssignedInspectorsValues(assignedInspectorsListModel);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.insert(SQLiteDbContext.TABLE_ASSIGNED_INSPECTORS, null, cValues);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void updateAssignedInspectors(Context context, String ID, AssignedInspectorsListModel assignedInspectorsListModel)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("Position", assignedInspectorsListModel.getPosition());
        cv.put("isTL",     assignedInspectorsListModel.getTL());

        try
        {
            db.update(SQLiteDbContext.TABLE_ASSIGNED_INSPECTORS, cv, "ID=?", new String[]{ID});
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

        return db.query(SQLiteDbContext.TABLE_ASSIGNED_INSPECTORS, allColumns, null, null, null, null, null);
    }

    public static Cursor realAllData2(Context context, String ScreenerID, String MissionOrderID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_ASSIGNED_INSPECTORS, allColumns,
                "ScreenerID=? AND MissionOrderID=? ", new String[]{ScreenerID, MissionOrderID}, null, null, null);
    }

    public static Cursor realAllData3(Context context, String ScreenerID, String MissionOrderID, String Inspector)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_ASSIGNED_INSPECTORS, allColumns,
                "MissionOrderID=? AND ScreenerID=? AND Inspector=? AND isTL=?",
                new String[]{ScreenerID, MissionOrderID, Inspector, "0"}, null, null, null);
    }

    public static Cursor realAllData4(Context context, String MissionOrderID,String Inspector)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_ASSIGNED_INSPECTORS, allColumns,
                "MissionOrderID=? AND Inspector=?",
                new String[]{MissionOrderID, Inspector}, null, null, null);
    }

    public static Cursor realAllData5(Context context, String Inspector)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_ASSIGNED_INSPECTORS, allColumns,
                "Inspector=? AND isTL=?",
                new String[]{Inspector, "1"}, null, null, null);
    }

}

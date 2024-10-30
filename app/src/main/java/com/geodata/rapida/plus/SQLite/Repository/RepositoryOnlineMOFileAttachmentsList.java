package com.geodata.rapida.plus.SQLite.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.geodata.rapida.plus.Retrofit.Model.AssignedInspectorsListModel;
import com.geodata.rapida.plus.Retrofit.Model.BuildingOccupancyListModel;
import com.geodata.rapida.plus.Retrofit.Model.MOFileAttachmentsList;
import com.geodata.rapida.plus.SQLite.Main.SQLiteDbContext;

public class RepositoryOnlineMOFileAttachmentsList
{
    private static final String TAG = RepositoryOnlineMOFileAttachmentsList.class.getSimpleName();

    private static final String[] allColumns =
            {
                    "ID",
                    "ScreenerID",
                    "MissionOrderID",
                    "MOAttachmentFilePath",
                    "isPreviousReport",
                    "FileName"
            };

    public static ContentValues setAssignedInspectorsValues(MOFileAttachmentsList moFileAttachmentsList)
    {
        ContentValues cValues = new ContentValues();

        cValues.put("ScreenerID",           moFileAttachmentsList.getScreenerID());
        cValues.put("MissionOrderID",       moFileAttachmentsList.getMissionOrderID());
        cValues.put("MOAttachmentFilePath", moFileAttachmentsList.getMOAttachmentFilePath());
        cValues.put("isPreviousReport",     moFileAttachmentsList.getPreviousReport());
        cValues.put("FileName",             moFileAttachmentsList.getFileName());

        return  cValues;
    }

    public static void saveMOFileAttachment(Context context, MOFileAttachmentsList moFileAttachmentsList)
    {
        ContentValues cValues = setAssignedInspectorsValues(moFileAttachmentsList);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.insert(SQLiteDbContext.TABLE_MO_FILE_ATTACHMENTS, null, cValues);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void updateMOFileAttachment(Context context, MOFileAttachmentsList moFileAttachmentsList)
    {
        ContentValues cValues = new ContentValues();

        cValues.put("MOAttachmentFilePath", moFileAttachmentsList.getMOAttachmentFilePath());
        cValues.put("isPreviousReport",     moFileAttachmentsList.getPreviousReport());
        cValues.put("isPreviousReport",     moFileAttachmentsList.getFileName());

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.update(SQLiteDbContext.TABLE_MO_FILE_ATTACHMENTS, cValues,
                    "ScreenerID=? AND MissionOrderID=?",
                    new String[]{moFileAttachmentsList.getScreenerID(), moFileAttachmentsList.getMissionOrderID()});
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

        return db.query(SQLiteDbContext.TABLE_MO_FILE_ATTACHMENTS, allColumns, null, null, null, null, null);
    }

    public static Cursor realAllData2(Context context, String ScreenerID, String MissionOrderID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_MO_FILE_ATTACHMENTS, allColumns,
                "ScreenerID=? AND MissionOrderID=? AND isPreviousReport=?",
                new String[]{ScreenerID, MissionOrderID, "0"}, null, null, null);
    }

    public static Cursor realAllData3(Context context, String ScreenerID, String MissionOrderID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_MO_FILE_ATTACHMENTS, allColumns,
                "ScreenerID=? AND MissionOrderID=? AND isPreviousReport=?",
                new String[]{ScreenerID, MissionOrderID, "1"}, null, null, null);
    }

    public static Cursor realAllData4(Context context, MOFileAttachmentsList moFileAttachmentsList)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_MO_FILE_ATTACHMENTS, allColumns,
                "ScreenerID=? AND MissionOrderID=? AND FileName=?",
                new String[]{moFileAttachmentsList.getScreenerID(),
                        moFileAttachmentsList.getMissionOrderID(),
                        moFileAttachmentsList.getFileName()}, null, null, null);
    }

}

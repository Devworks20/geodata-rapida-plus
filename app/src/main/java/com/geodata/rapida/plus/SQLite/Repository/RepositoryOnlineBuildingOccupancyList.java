package com.geodata.rapida.plus.SQLite.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.geodata.rapida.plus.Retrofit.Model.BuildingOccupancyListModel;
import com.geodata.rapida.plus.Retrofit.Model.OccupanciesModel;
import com.geodata.rapida.plus.SQLite.Main.SQLiteDbContext;

public class RepositoryOnlineBuildingOccupancyList
{
    private static final String TAG = RepositoryOnlineBuildingOccupancyList.class.getSimpleName();

    private static final String[] allColumns =
            {
                    "ID",
                    "ScreenerID",
                    "MissionOrderID",
                    "Occupancy"
            };

    public static ContentValues setBuildingOccupancyListValues(BuildingOccupancyListModel buildingOccupancyListModel)
    {
        ContentValues cValues = new ContentValues();

        cValues.put("ScreenerID",     buildingOccupancyListModel.getScreenerID());
        cValues.put("MissionOrderID", buildingOccupancyListModel.getMissionOrderID());
        cValues.put("Occupancy",      buildingOccupancyListModel.getOccupancy());

        return  cValues;
    }

    public static Cursor realAllData(Context context, String ScreenerID, String MissionOrderID, String Occupancy)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_OCCUPANCY_LIST, allColumns,
                "ScreenerID=? AND MissionOrderID=? AND Occupancy=?",
                new String[]{ScreenerID, MissionOrderID, Occupancy}, null, null, null);
    }

    public static void saveBuildingOccupancyList(Context context, BuildingOccupancyListModel buildingOccupancyListModel)
    {
        ContentValues cValues = setBuildingOccupancyListValues(buildingOccupancyListModel);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.insert(SQLiteDbContext.TABLE_OCCUPANCY_LIST, null, cValues);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void updateBuildingOccupancyList(Context context, BuildingOccupancyListModel buildingOccupancyListModel)
    {
        ContentValues cValues = new ContentValues();

        cValues.put("Occupancy", buildingOccupancyListModel.getOccupancy());

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.update(SQLiteDbContext.TABLE_OCCUPANCY_LIST, cValues,
                    "ScreenerID=? AND MissionOrderID=?",
                    new String[]{buildingOccupancyListModel.getScreenerID(), buildingOccupancyListModel.getMissionOrderID()});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void removeSelectedOccupancy(Context context, String ID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.delete(SQLiteDbContext.TABLE_OCCUPANCY_LIST, "ID=?",new String[]{ID});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

}

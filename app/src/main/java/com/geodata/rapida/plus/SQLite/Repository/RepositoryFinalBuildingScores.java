package com.geodata.rapida.plus.SQLite.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.geodata.rapida.plus.SQLite.Class.FinalBuildingScoresClass;
import com.geodata.rapida.plus.SQLite.Main.SQLiteDbContext;

public class RepositoryFinalBuildingScores
{
    private static final String TAG = RepositoryFinalBuildingScores.class.getSimpleName();

    private static final String[] allColumns =
            {
                    "ID",
                    "UserAccountID",
                    "MissionOrderID",
                    "Category",
                    "BuildingScoreNo",
                    "BuildingType",
                    "FinalScore",
                    "isActive"
            };

    public static ContentValues setAccountValues(FinalBuildingScoresClass finalBuildingScoresClass)
    {
        ContentValues cValues = new ContentValues();

        cValues.put("UserAccountID",   finalBuildingScoresClass.getUserAccountID());
        cValues.put("MissionOrderID",  finalBuildingScoresClass.getMissionOrderID());
        cValues.put("Category",        finalBuildingScoresClass.getCategory());
        cValues.put("BuildingScoreNo", finalBuildingScoresClass.getBuildingScoreNo());
        cValues.put("BuildingType",    finalBuildingScoresClass.getBuildingType());
        cValues.put("FinalScore",      finalBuildingScoresClass.getFinalScore());
        cValues.put("isActive",        finalBuildingScoresClass.getIsActive());

        return  cValues;
    }

    public static void saveFinalBuildingScore(Context context, FinalBuildingScoresClass finalBuildingScoresClass)
    {
        ContentValues cValues = setAccountValues(finalBuildingScoresClass);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.insert(SQLiteDbContext.TABLE_FINAL_BUILDING_SCORES, null, cValues);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static Cursor realAllData(Context context, String UserAccountID, String MissionOrderID, String Category)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_FINAL_BUILDING_SCORES, allColumns,
                "UserAccountID=? AND MissionOrderID=? AND Category=?",
                new String[]{UserAccountID, MissionOrderID, Category},null, null, null);
    }

    public static Cursor realAllData2(Context context, String UserAccountID, String MissionOrderID, String BuildingScoreNo, String BuildingType)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_FINAL_BUILDING_SCORES, allColumns,
                "UserAccountID=? AND MissionOrderID=? AND BuildingScoreNo=? AND BuildingType=? ",
                new String[]{UserAccountID, MissionOrderID, BuildingScoreNo, BuildingType},null, null, null);
    }

    public static Cursor realAllData3(Context context, String UserAccountID, String MissionOrderID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        String[] allColumns =
                {
                        "ID",
                        "MAX(CASE WHEN BuildingScoreNo = '1' THEN Category END) AS Category1",
                        "MAX(CASE WHEN BuildingScoreNo = '1' THEN BuildingType END) AS BuildingType1",
                        "MAX(CASE WHEN BuildingScoreNo = '1' THEN FinalScore END) AS FinalScore1",
                        "MAX(CASE WHEN BuildingScoreNo = '2' THEN Category END) AS Category2",
                        "MAX(CASE WHEN BuildingScoreNo = '2' THEN BuildingType END) AS BuildingType2",
                        "MAX(CASE WHEN BuildingScoreNo = '2' THEN FinalScore END) AS FinalScore2 "
                };

        return db.query(SQLiteDbContext.TABLE_FINAL_BUILDING_SCORES, allColumns,
                "UserAccountID=? AND MissionOrderID=?", new String[]{UserAccountID, MissionOrderID}, null, null, "ID");
    }

    public static Cursor realAllData4(Context context, String UserAccountID, String MissionOrderID, String BuildingScoreNo)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_FINAL_BUILDING_SCORES, allColumns,
                "UserAccountID=? AND MissionOrderID=? AND BuildingScoreNo=?",
                new String[]{UserAccountID, MissionOrderID, BuildingScoreNo},null, null, null);
    }

    public static void updateFinalBuildingScore(Context context, String ID, FinalBuildingScoresClass finalBuildingScoresClass)
    {
        ContentValues cValues = setAccountValues(finalBuildingScoresClass);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.update(SQLiteDbContext.TABLE_FINAL_BUILDING_SCORES, cValues, "ID=?", new String[]{ID});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void removeBuildingFinalScores(Context context, String ID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.delete(SQLiteDbContext.TABLE_FINAL_BUILDING_SCORES, "ID=?", new String[]{ID});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

}

package com.geodata.rapida.plus.SQLite.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.geodata.rapida.plus.SQLite.Class.AddBuildingScoresClass;
import com.geodata.rapida.plus.SQLite.Class.TempBuildingScoresClass;
import com.geodata.rapida.plus.SQLite.Main.SQLiteDbContext;

public class RepositoryTempBuildingScores
{
    private static final String TAG = RepositoryTempBuildingScores.class.getSimpleName();

    private static final String[] allColumns =
            {
                    "ID",
                    "UserAccountID",
                    "MissionOrderID",
                    "Category",
                    "BuildingID",
                    "BuildingType",
                    "Modifiers",
                    "Scores",
                    "isActive"
            };

    public static ContentValues setAccountValues(TempBuildingScoresClass tempBuildingScoresClass)
    {
        ContentValues cValues = new ContentValues();

        cValues.put("UserAccountID",  tempBuildingScoresClass.getUserAccountID());
        cValues.put("MissionOrderID", tempBuildingScoresClass.getMissionOrderID());
        cValues.put("Category",       tempBuildingScoresClass.getCategory());
        cValues.put("BuildingID",     tempBuildingScoresClass.getBuildingID());
        cValues.put("BuildingType",   tempBuildingScoresClass.getBuildingType());
        cValues.put("Modifiers",      tempBuildingScoresClass.getModifiers());
        cValues.put("Scores",         tempBuildingScoresClass.getScores());
        cValues.put("isActive",       tempBuildingScoresClass.getIsActive());

        return  cValues;
    }

    public static void saveBuildingScores(Context context, TempBuildingScoresClass tempBuildingScoresClass)
    {
        ContentValues cValues = setAccountValues(tempBuildingScoresClass);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.insert(SQLiteDbContext.TABLE_TEMP_BUILDING_SCORES, null, cValues);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static Cursor selectBuildingScores(Context context, String UserAccountID, String MissionOrderID, String Category)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_TEMP_BUILDING_SCORES, allColumns,
                "UserAccountID=? AND MissionOrderID=? AND Category=?",
                new String[]{UserAccountID, MissionOrderID, Category}, null, null, null);
    }

    public static Cursor selectBuildingScores2(Context context, String UserAccountID, String MissionOrderID, String BuildingType)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_TEMP_BUILDING_SCORES, allColumns,
                "UserAccountID=? AND MissionOrderID=? AND BuildingType=?",
                new String[]{UserAccountID, MissionOrderID, BuildingType}, null, null, null);
    }

    public static Cursor selectBuildingScores3(Context context,  String UserAccountID, String MissionOrderID,
                                               String Category, String BuildingType, String BuildingType2)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        if (BuildingType2.equals(""))
        {
            String[] allColumns =
                    {
                            "Modifiers",
                            "MAX(CASE WHEN BuildingType = '" + BuildingType + "' THEN isActive END) AS isActive1",
                            "MAX(CASE WHEN BuildingType = '" + BuildingType + "' THEN ID END) AS BuildingID1",
                            "MAX(CASE WHEN BuildingType = '" + BuildingType + "' THEN Scores END) AS BuildingScore1"
                    };


            return db.query(SQLiteDbContext.TABLE_TEMP_BUILDING_SCORES, allColumns,
                    "Category=? AND UserAccountID=? AND MissionOrderID=?",
                    new String[]{Category, UserAccountID, MissionOrderID}, "Modifiers", null, "ID");

        }
        else
        {
            String[] allColumns2 =
                    {
                            "Modifiers",
                            "MAX(CASE WHEN BuildingType = '" + BuildingType + "' THEN isActive END) AS isActive1",
                            "MAX(CASE WHEN BuildingType = '" + BuildingType + "' THEN ID END) AS BuildingID1",
                            "MAX(CASE WHEN BuildingType = '" + BuildingType + "' THEN Scores END) AS BuildingScore1",
                            "MAX(CASE WHEN BuildingType = '" + BuildingType2 + "' THEN isActive END) AS isActive2",
                            "MAX(CASE WHEN BuildingType = '" + BuildingType2 + "' THEN ID END) AS BuildingID2",
                            "MAX(CASE WHEN BuildingType = '" + BuildingType2 + "' THEN Scores END) AS BuildingScore2"
                    };

            return db.query(SQLiteDbContext.TABLE_TEMP_BUILDING_SCORES, allColumns2,
                    "Category=? AND UserAccountID=? AND MissionOrderID=?",
                    new String[]{Category, UserAccountID, MissionOrderID}, "Modifiers", null, "ID");

        }
    }

    public static Cursor selectBuildingScores4(Context context,  String UserAccountID, String MissionOrderID,
                                               String Category, String BuildingType, String BuildingType2)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        if (BuildingType2.equals(""))
        {
            Log.e(TAG, "THIS CALLED 1");

            String[] allColumns =
                    {
                            "Modifiers",
                            "MAX(CASE WHEN BuildingType = '" + BuildingType + "' THEN isActive END) AS isActive1",
                            "MAX(CASE WHEN BuildingType = '" + BuildingType + "' THEN ID END) AS BuildingID1",
                            "MAX(CASE WHEN BuildingType = '" + BuildingType + "' THEN Scores END) AS BuildingScore1",
                    };


            return db.query(SQLiteDbContext.TABLE_TEMP_BUILDING_SCORES, allColumns,
                    "isActive=? AND Category=? AND UserAccountID=? AND MissionOrderID=?",
                    new String[]{"1", Category, UserAccountID, MissionOrderID}, "Modifiers", null, "ID");

        }
        else
        {
            String[] allColumns2 =
                    {
                            "Modifiers",
                            "MAX(CASE WHEN BuildingType = '" + BuildingType + "' THEN isActive END) AS isActive1",
                            "MAX(CASE WHEN BuildingType = '" + BuildingType + "' THEN ID END) AS BuildingID1",
                            "MAX(CASE WHEN BuildingType = '" + BuildingType + "' THEN Scores END) AS BuildingScore1",
                            "MAX(CASE WHEN BuildingType = '" + BuildingType2 + "' THEN isActive END) AS isActive2",
                            "MAX(CASE WHEN BuildingType = '" + BuildingType2 + "' THEN ID END) AS BuildingID2",
                            "MAX(CASE WHEN BuildingType = '" + BuildingType2 + "' THEN Scores END) AS BuildingScore2"
                    };

            return db.query(SQLiteDbContext.TABLE_TEMP_BUILDING_SCORES, allColumns2,
                    "isActive=? AND Category=? AND UserAccountID=? AND MissionOrderID=?",
                    new String[]{"1", Category, UserAccountID, MissionOrderID}, "Modifiers", null, "ID");

        }
    }

    public static void updateBuildingScore(Context context, String UserAccountID, String MissionOrderID, String BuildingID, String isActive)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("isActive", isActive);

        try
        {
            db.update(SQLiteDbContext.TABLE_TEMP_BUILDING_SCORES, cv, "UserAccountID=? AND MissionOrderID=? AND BuildingID=?",
                    new String[]{UserAccountID, MissionOrderID, BuildingID});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void removesBuildingScore(Context context, String UserAccountID, String MissionOrderID, String Category)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.delete(SQLiteDbContext.TABLE_TEMP_BUILDING_SCORES,
                    "UserAccountID=? AND MissionOrderID=? AND Category=?", new String[]{UserAccountID, MissionOrderID, Category});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

}

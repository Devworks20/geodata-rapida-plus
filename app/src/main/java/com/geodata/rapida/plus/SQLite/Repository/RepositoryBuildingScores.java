package com.geodata.rapida.plus.SQLite.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.geodata.rapida.plus.SQLite.Class.AddBuildingScoresClass;
import com.geodata.rapida.plus.SQLite.Main.SQLiteDbContext;

public class RepositoryBuildingScores
{
    private static final String TAG = RepositoryBuildingScores.class.getSimpleName();

    private static final String[] allColumns =
            {
                    "ID",
                    "Category",
                    "BuildingType",
                    "Modifiers",
                    "Scores",
                    "isActive"
            };

    public static ContentValues setAccountValues(AddBuildingScoresClass addBuildingScoresClass)
    {
        ContentValues cValues = new ContentValues();

        cValues.put("Category",     addBuildingScoresClass.getCategory());
        cValues.put("BuildingType", addBuildingScoresClass.getBuildingType());
        cValues.put("Modifiers",    addBuildingScoresClass.getModifiers());
        cValues.put("Scores",       addBuildingScoresClass.getScores());
        cValues.put("isActive",     addBuildingScoresClass.getIsActive());

        return  cValues;
    }

    public static void saveBuildingScores(Context context, AddBuildingScoresClass addBuildingScoresClass)
    {
        ContentValues cValues = setAccountValues(addBuildingScoresClass);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.insert(SQLiteDbContext.TABLE_BUILDING_SCORES, null, cValues);
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

        return db.query(SQLiteDbContext.TABLE_BUILDING_SCORES, allColumns, null, null, null, null, null);
    }

    public static Cursor selectBuildingScores(Context context, String Category, String BuildingType, String BuildingType2)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        String[] allColumns;

        if (BuildingType2.equals(""))
        {
            allColumns = new String[]
                    {
                            "Modifiers",
                            "MAX(CASE WHEN BuildingType = '" + BuildingType + "' THEN isActive END) AS isActive1",
                            "MAX(CASE WHEN BuildingType = '" + BuildingType + "' THEN ID END) AS BuildingID1",
                            "MAX(CASE WHEN BuildingType = '" + BuildingType + "' THEN Scores END) AS BuildingScore1"
                    };
        }
        else
        {
            allColumns = new String[]
                    {
                            "Modifiers",
                            "MAX(CASE WHEN BuildingType = '" + BuildingType + "' THEN isActive END) AS isActive1",
                            "MAX(CASE WHEN BuildingType = '" + BuildingType + "' THEN ID END) AS BuildingID1",
                            "MAX(CASE WHEN BuildingType = '" + BuildingType + "' THEN Scores END) AS BuildingScore1",
                            "MAX(CASE WHEN BuildingType = '" + BuildingType2 + "' THEN isActive END) AS isActive2",
                            "MAX(CASE WHEN BuildingType = '" + BuildingType2 + "' THEN ID END) AS BuildingID2",
                            "MAX(CASE WHEN BuildingType = '" + BuildingType2 + "' THEN Scores END) AS BuildingScore2"
                    };
        }

        return db.query(SQLiteDbContext.TABLE_BUILDING_SCORES, allColumns,
                "Category=?", new String[]{Category}, "Modifiers", null, "ID");

    }

    public static Cursor selectBuildingScores2(Context context, String Category, String BuildingType, String BuildingType2)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        String[] allColumns =
                {
                        "Modifiers",
                        "MAX(CASE WHEN BuildingType = '" + BuildingType + "' THEN Scores END) AS BuildingScore1",
                        "MAX(CASE WHEN BuildingType = '" + BuildingType2 + "' THEN Scores END) AS BuildingScore2"
                };

        return db.query(SQLiteDbContext.TABLE_BUILDING_SCORES, allColumns,
                "Category=? AND isActive=?", new String[]{Category, "1"}, "Modifiers", null, "Modifiers");
    }

    public static Cursor selectBuildingScores3(Context context, String Category, String BuildingType, String Modifiers)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_BUILDING_SCORES, allColumns,
                "Category=? AND BuildingType=? AND Modifiers=?",
                new String[]{Category, BuildingType, Modifiers}, null, null, null);
    }

    public static void updateStatusBuildingScore1(Context context, String BuildingID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("isActive",  "1");

        try
        {
            db.update(SQLiteDbContext.TABLE_BUILDING_SCORES, cv, "ID=?", new String[]{BuildingID});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void removesBuildingScore(Context context, String BuildingID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("isActive",  "0");

        try
        {
            db.update(SQLiteDbContext.TABLE_BUILDING_SCORES, cv, "ID=?", new String[]{BuildingID});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

}

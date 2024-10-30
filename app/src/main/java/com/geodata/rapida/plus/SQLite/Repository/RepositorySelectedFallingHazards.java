package com.geodata.rapida.plus.SQLite.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.geodata.rapida.plus.SQLite.Class.SelectedFallingHazardsClass;
import com.geodata.rapida.plus.SQLite.Main.SQLiteDbContext;

public class RepositorySelectedFallingHazards
{
    private static final String TAG = RepositorySelectedFallingHazards.class.getSimpleName();

    private static final String[] allColumns =
            {
                    "ID",
                    "UserAccountID",
                    "MissionOrderID",
                    "Category",
                    "FallingHazardDesc",
                    "OthersField"
            };

    public static ContentValues setAccountValues(SelectedFallingHazardsClass selectedFallingHazardsClass)
    {
        ContentValues cValues = new ContentValues();

        cValues.put("UserAccountID",     selectedFallingHazardsClass.getUserAccountID());
        cValues.put("MissionOrderID",    selectedFallingHazardsClass.getMissionOrderID());
        cValues.put("Category",          selectedFallingHazardsClass.getCategory());
        cValues.put("FallingHazardDesc", selectedFallingHazardsClass.getFallingHazardDesc());
        cValues.put("OthersField",       selectedFallingHazardsClass.getOthersField());

        return  cValues;
    }

    public static void saveSelectedFallingHazards(Context context, SelectedFallingHazardsClass selectedFallingHazardsClass)
    {
        ContentValues cValues = setAccountValues(selectedFallingHazardsClass);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.insert(SQLiteDbContext.TABLE_SELECTED_FALLING_HAZARDS, null, cValues);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void updateSelectedFallingHazards(Context context, SelectedFallingHazardsClass selectedFallingHazardsClass, String ID)
    {
        ContentValues cValues = setAccountValues(selectedFallingHazardsClass);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.update(SQLiteDbContext.TABLE_SELECTED_FALLING_HAZARDS, cValues, "ID=?",new String[]{ID});
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

        return db.query(SQLiteDbContext.TABLE_SELECTED_FALLING_HAZARDS, allColumns,
                null, null, null, null, null);
    }

    public static Cursor realAllData2(Context context, String UserAccountID, String MissionOrderID, String Category, String FallingHazardDesc)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_SELECTED_FALLING_HAZARDS, allColumns,
                "UserAccountID=? AND MissionOrderID=? AND Category=? AND FallingHazardDesc=? COLLATE NOCASE",
                new String[]{UserAccountID, MissionOrderID, Category, FallingHazardDesc}, null, null, null);
    }

    public static void removeSelectedFallingHazards(Context context, String ID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.delete(SQLiteDbContext.TABLE_SELECTED_FALLING_HAZARDS, "ID=?",new String[]{ID});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

}

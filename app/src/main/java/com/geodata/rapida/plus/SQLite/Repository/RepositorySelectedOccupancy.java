package com.geodata.rapida.plus.SQLite.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.geodata.rapida.plus.SQLite.Class.SelectedOccupancyClass;
import com.geodata.rapida.plus.SQLite.Class.UserAccountClass;
import com.geodata.rapida.plus.SQLite.Main.SQLiteDbContext;

public class RepositorySelectedOccupancy
{
    private static final String TAG = RepositorySelectedOccupancy.class.getSimpleName();

    private static final String[] allColumns =
            {
                    "ID",
                    "UserAccountID",
                    "MissionOrderID",
                    "Category",
                    "UseOfCharacterOccupancyID"
            };

    public static ContentValues setAccountValues(SelectedOccupancyClass selectedOccupancyClass)
    {
        ContentValues cValues = new ContentValues();

        cValues.put("UserAccountID",             selectedOccupancyClass.getUserAccountID());
        cValues.put("MissionOrderID",            selectedOccupancyClass.getMissionOrderID());
        cValues.put("Category",                  selectedOccupancyClass.getCategory());
        cValues.put("UseOfCharacterOccupancyID", selectedOccupancyClass.getUseOfCharacterOccupancyID());

        return  cValues;
    }

    public static void saveSelectedOccupancy(Context context, SelectedOccupancyClass selectedOccupancyClass)
    {
        ContentValues cValues = setAccountValues(selectedOccupancyClass);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.insert(SQLiteDbContext.TABLE_SELECTED_OCCUPANCIES, null, cValues);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void updateSelectedOccupancy(Context context, SelectedOccupancyClass selectedOccupancyClass, String ID)
    {
        ContentValues cValues = setAccountValues(selectedOccupancyClass);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.update(SQLiteDbContext.TABLE_SELECTED_OCCUPANCIES, cValues, "ID=?",new String[]{ID});
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

        return db.query(SQLiteDbContext.TABLE_SELECTED_OCCUPANCIES, allColumns,
                null, null, null, null, null);
    }

    public static Cursor realAllData2(Context context, String UserAccountID, String MissionOrderID, String Category, String UseOfCharacterOccupancyID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_SELECTED_OCCUPANCIES, allColumns,
                "UserAccountID=? AND MissionOrderID=? AND Category=? AND UseOfCharacterOccupancyID=?",
                new String[]{UserAccountID, MissionOrderID, Category, UseOfCharacterOccupancyID}, null, null, null);
    }

    public static void removeSelectedOccupancy(Context context, String ID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.delete(SQLiteDbContext.TABLE_SELECTED_OCCUPANCIES, "ID=?",new String[]{ID});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

}

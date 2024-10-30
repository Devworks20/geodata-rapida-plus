package com.geodata.rapida.plus.SQLite.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.geodata.rapida.plus.Retrofit.Model.FallingHazardsModel;
import com.geodata.rapida.plus.Retrofit.Model.SoilTypesModel;
import com.geodata.rapida.plus.SQLite.Main.SQLiteDbContext;

public class RepositoryFallingHazards
{
    private static final String TAG = RepositoryFallingHazards.class.getSimpleName();

    private static final String[] allColumns =
            {
                    "ID",
                    "FallingHazardID",
                    "FallingHazardDesc"
            };

    public static ContentValues setAccountValues(FallingHazardsModel fallingHazardsModel)
    {
        ContentValues cValues = new ContentValues();

        cValues.put("FallingHazardID",   fallingHazardsModel.getFallingHazardID());
        cValues.put("FallingHazardDesc", fallingHazardsModel.getFallingHazardDesc());

        return  cValues;
    }

    public static void saveFallingHazards(Context context, FallingHazardsModel fallingHazardsModel)
    {
        ContentValues cValues = setAccountValues(fallingHazardsModel);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.insert(SQLiteDbContext.TABLE_FALLING_HAZARDS, null, cValues);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void updateFallingHazards(Context context, String FallingHazardID, FallingHazardsModel fallingHazardsModel)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("FallingHazardDesc", fallingHazardsModel.getFallingHazardDesc());

        try
        {
            db.update(SQLiteDbContext.TABLE_FALLING_HAZARDS, cv,
                    "FallingHazardID=?", new String[]{FallingHazardID});
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

        return db.query(SQLiteDbContext.TABLE_FALLING_HAZARDS, allColumns,
                null, null, null, null, null);
    }

    public static Cursor realAllData2(Context context, String FallingHazardID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_FALLING_HAZARDS, allColumns,
                "FallingHazardID=?", new String[]{FallingHazardID}, null, null, null);
    }

}

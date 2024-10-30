package com.geodata.rapida.plus.SQLite.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.geodata.rapida.plus.Retrofit.Model.OccupanciesModel;
import com.geodata.rapida.plus.SQLite.Main.SQLiteDbContext;

public class RepositoryOccupancies
{
    private static final String TAG = RepositoryOccupancies.class.getSimpleName();

    private static final String[] allColumns =
            {
                    "ID",
                    "UseOfCharacterOccupancyID",
                    "Description"
            };

    public static ContentValues setAccountValues(OccupanciesModel occupanciesModel)
    {
        ContentValues cValues = new ContentValues();

        cValues.put("UseOfCharacterOccupancyID", occupanciesModel.getUseOfCharacterOccupancyID());
        cValues.put("Description",               occupanciesModel.getDescription());

        return  cValues;
    }

    public static void saveOccupancies(Context context, OccupanciesModel occupanciesModel)
    {
        ContentValues cValues = setAccountValues(occupanciesModel);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.insert(SQLiteDbContext.TABLE_OCCUPANCIES, null, cValues);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void updateOccupancies(Context context, String UseOfCharacterOccupancyID, OccupanciesModel occupanciesModel)
    {
        ContentValues cValues = new ContentValues();

        cValues.put("Description", occupanciesModel.getDescription());

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.update(SQLiteDbContext.TABLE_OCCUPANCIES, cValues,
                    "UseOfCharacterOccupancyID=?",new String[]{UseOfCharacterOccupancyID});
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

        return db.query(SQLiteDbContext.TABLE_OCCUPANCIES, allColumns,
                null, null, null, null, null);
    }

    public static Cursor realAllData2(Context context, String UseOfCharacterOccupancyID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_OCCUPANCIES, allColumns,
                "UseOfCharacterOccupancyID=?", new String[]{UseOfCharacterOccupancyID}, null, null, null);
    }

}

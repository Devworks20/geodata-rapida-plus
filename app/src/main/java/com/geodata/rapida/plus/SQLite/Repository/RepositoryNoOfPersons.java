package com.geodata.rapida.plus.SQLite.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.geodata.rapida.plus.Retrofit.Model.AssignedInspectorsListModel;
import com.geodata.rapida.plus.Retrofit.Model.NoOfPersonsModel;
import com.geodata.rapida.plus.SQLite.Main.SQLiteDbContext;

public class RepositoryNoOfPersons
{
    private static final String TAG = RepositoryNoOfPersons.class.getSimpleName();

    private static final String[] allColumns =
            {
                    "ID",
                    "NoOfPersonsID",
                    "NoOfPersons"
            };

    public static ContentValues setAccountValues(NoOfPersonsModel noOfPersonsModel)
    {
        ContentValues cValues = new ContentValues();

        cValues.put("NoOfPersonsID", noOfPersonsModel.getNoOfPersonsID());
        cValues.put("NoOfPersons",   noOfPersonsModel.getNoOfPersons());

        return  cValues;
    }

    public static void saveNoOfPersons(Context context, NoOfPersonsModel noOfPersonsModel)
    {
        ContentValues cValues = setAccountValues(noOfPersonsModel);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.insert(SQLiteDbContext.TABLE_NO_OF_PERSONS, null, cValues);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void updateNoOfPersons(Context context, String NoOfPersonsID, NoOfPersonsModel noOfPersonsModel)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("NoOfPersons", noOfPersonsModel.getNoOfPersons());

        try
        {
            db.update(SQLiteDbContext.TABLE_NO_OF_PERSONS, cv, "NoOfPersonsID=?", new String[]{NoOfPersonsID});
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

        return db.query(SQLiteDbContext.TABLE_NO_OF_PERSONS, allColumns,
                null, null, null, null, null);
    }

    public static Cursor realAllData2(Context context, String NoOfPersonsID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_NO_OF_PERSONS, allColumns,
                "NoOfPersonsID=?", new String[]{NoOfPersonsID}, null, null, null);
    }

}

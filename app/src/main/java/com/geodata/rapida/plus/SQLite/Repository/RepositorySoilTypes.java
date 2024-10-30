package com.geodata.rapida.plus.SQLite.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.geodata.rapida.plus.Retrofit.Model.NoOfPersonsModel;
import com.geodata.rapida.plus.Retrofit.Model.OccupanciesModel;
import com.geodata.rapida.plus.Retrofit.Model.SoilTypesModel;
import com.geodata.rapida.plus.SQLite.Main.SQLiteDbContext;

public class RepositorySoilTypes
{
    private static final String TAG = RepositorySoilTypes.class.getSimpleName();

    private static final String[] allColumns =
            {
                    "ID",
                    "BuildingSoilTypeID",
                    "BuildingSoilTypeCode",
                    "BuildingSoilTypeDesc"
            };

    public static ContentValues setAccountValues(SoilTypesModel soilTypesModel)
    {
        ContentValues cValues = new ContentValues();

        cValues.put("BuildingSoilTypeID",   soilTypesModel.getBuildingSoilTypeID());
        cValues.put("BuildingSoilTypeCode", soilTypesModel.getBuildingSoilTypeCode());
        cValues.put("BuildingSoilTypeDesc", soilTypesModel.getBuildingSoilTypeDesc());

        return  cValues;
    }

    public static void saveSoilTypes(Context context, SoilTypesModel soilTypesModel)
    {
        ContentValues cValues = setAccountValues(soilTypesModel);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.insert(SQLiteDbContext.TABLE_SOIL_TYPES, null, cValues);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void updateSoilTypes(Context context, String BuildingSoilTypeID, SoilTypesModel soilTypesModel)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("BuildingSoilTypeCode", soilTypesModel.getBuildingSoilTypeCode());
        cv.put("BuildingSoilTypeDesc", soilTypesModel.getBuildingSoilTypeDesc());

        try
        {
            db.update(SQLiteDbContext.TABLE_SOIL_TYPES, cv,
                    "BuildingSoilTypeID=?", new String[]{BuildingSoilTypeID});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static Cursor realAllData(Context context)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_SOIL_TYPES, allColumns,
                null, null, null, null, null);
    }

    public static Cursor realAllData2(Context context, String BuildingSoilTypeID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_SOIL_TYPES, allColumns,
                "BuildingSoilTypeID=?", new String[]{BuildingSoilTypeID}, null, null, null);
    }

}

package com.geodata.rapida.plus.SQLite.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.geodata.rapida.plus.Retrofit.Model.BuildingTypeModel;
import com.geodata.rapida.plus.SQLite.Main.SQLiteDbContext;

public class RepositoryBuildingType
{
    private static final String TAG = RepositoryBuildingType.class.getSimpleName();

    private static final String[] allColumns =
            {
                    "ID",
                    "StructureTypeID",
                    "Code",
                    "BuildingTypeName",
                    "PreCode",
                    "PostBenchmark"
            };

    public static ContentValues setAccountValues(BuildingTypeModel buildingTypeModel)
    {
        ContentValues cValues = new ContentValues();

        cValues.put("StructureTypeID",   buildingTypeModel.getStructureTypeID());
        cValues.put("Code",              buildingTypeModel.getCode());
        cValues.put("BuildingTypeName",  buildingTypeModel.getBuildingTypeName());
        cValues.put("PreCode",           buildingTypeModel.getPreCode());
        cValues.put("PostBenchmark",     buildingTypeModel.getPostBenchmark());

        return  cValues;
    }

    public static void saveBuildingType(Context context, BuildingTypeModel buildingTypeModel)
    {
        ContentValues cValues = setAccountValues(buildingTypeModel);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.insert(SQLiteDbContext.TABLE_BUILDING_TYPES, null, cValues);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void updateBuildingType(Context context, BuildingTypeModel buildingTypeModel)
    {
        ContentValues cValues = new ContentValues();

        cValues.put("BuildingTypeName",  buildingTypeModel.getBuildingTypeName());
        cValues.put("Code",              buildingTypeModel.getCode());
        cValues.put("PostBenchmark",     buildingTypeModel.getPostBenchmark());
        cValues.put("PreCode",           buildingTypeModel.getPreCode());

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.update(SQLiteDbContext.TABLE_BUILDING_TYPES, cValues,
                    "StructureTypeID=?",new String[]{buildingTypeModel.getStructureTypeID()});
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

        return db.query(SQLiteDbContext.TABLE_BUILDING_TYPES, allColumns, null, null, null, null, null);
    }

    public static Cursor realAllData2(Context context, BuildingTypeModel buildingTypeModel)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_BUILDING_TYPES, allColumns,
                "StructureTypeID=?", new String[]{buildingTypeModel.getStructureTypeID()},null, null, null);
    }

    public static Cursor realAllData3(Context context, String Code)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_BUILDING_TYPES, allColumns,
                "Code=?", new String[]{Code},null, null, null);
    }

}

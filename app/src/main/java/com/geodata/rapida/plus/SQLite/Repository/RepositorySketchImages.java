package com.geodata.rapida.plus.SQLite.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.geodata.rapida.plus.SQLite.Class.SketchImagesClass;
import com.geodata.rapida.plus.SQLite.Main.SQLiteDbContext;

public class RepositorySketchImages
{
    private static final String TAG = RepositorySketchImages.class.getSimpleName();

    private static final String[] allColumns =
            {
                    "ID",
                    "UserAccountID",
                    "Category",
                    "SketchID",
                    "SketchName",
                    "SketchPath",
                    "SketchExtension",
                    "DtAdded",
                    "isActive",
                    "isSync"
            };

    public static ContentValues setAccountValues(SketchImagesClass sketchImagesClass)
    {
        ContentValues cValues = new ContentValues();

        cValues.put("UserAccountID",   sketchImagesClass.getUserAccountID());
        cValues.put("Category",        sketchImagesClass.getCategory());
        cValues.put("SketchID",        sketchImagesClass.getSketchID());
        cValues.put("SketchName",      sketchImagesClass.getSketchName());
        cValues.put("SketchPath",      sketchImagesClass.getSketchPath());
        cValues.put("SketchExtension", sketchImagesClass.getSketchExtension());
        cValues.put("DtAdded",         sketchImagesClass.getDtAdded());
        cValues.put("isActive",        sketchImagesClass.getIsActive());
        cValues.put("isSync",          sketchImagesClass.getIsSync());

        return  cValues;
    }

    public static void savePhoto(Context context, SketchImagesClass sketchImagesClass)
    {
        ContentValues cValues = setAccountValues(sketchImagesClass);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.insert(SQLiteDbContext.TABLE_SKETCH_IMAGES, null, cValues);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static Cursor realAllData(Context context, String SketchID, String Category)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_SKETCH_IMAGES, allColumns,
                "SketchID=? AND isSync=? AND Category=?", new String[]{SketchID, "0", Category}, null, null, null);
    }

    public static void removePhoto(Context context, String ID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.delete(SQLiteDbContext.TABLE_SKETCH_IMAGES, "ID=?", new String[]{ID});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

}

package com.geodata.rapida.plus.SQLite.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.geodata.rapida.plus.SQLite.Class.ImagesClass;
import com.geodata.rapida.plus.SQLite.Main.SQLiteDbContext;

public class RepositoryImages
{
    private static final String TAG = RepositoryImages.class.getSimpleName();

    private static final String[] allColumns =
            {
                    "ID",
                    "UserAccountID",
                    "Category",
                    "ImageID",
                    "ImageType",
                    "ImageName",
                    "ImagePath",
                    "ImageExtension",
                    "DtAdded",
                    "Description",
                    "isActive",
                    "isSync"
            };

    public static ContentValues setAccountValues(ImagesClass ImagesClass)
    {
        ContentValues cValues = new ContentValues();

        cValues.put("UserAccountID",  ImagesClass.getUserAccountID());
        cValues.put("Category",       ImagesClass.getCategory());
        cValues.put("ImageID",        ImagesClass.getImageID());
        cValues.put("ImageType",      ImagesClass.getImageType());
        cValues.put("ImageName",      ImagesClass.getImageName());
        cValues.put("ImagePath",      ImagesClass.getImagePath());
        cValues.put("ImageExtension", ImagesClass.getImageExtension());
        cValues.put("DtAdded",        ImagesClass.getDtAdded());
        cValues.put("Description",    ImagesClass.getDescription());
        cValues.put("isActive",       ImagesClass.getIsActive());
        cValues.put("isSync",         ImagesClass.getIsSync());

        return  cValues;
    }

    public static void savePhoto(Context context, ImagesClass ImagesClass)
    {
        ContentValues cValues = setAccountValues(ImagesClass);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.insert(SQLiteDbContext.TABLE_IMAGES, null, cValues);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static Cursor realAllData(Context context, String ImageID, String ImageType, String Category)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_IMAGES, allColumns,
                "ImageID=? AND isSync=? AND ImageType=? AND Category=?",
                new String[]{ImageID, "0", ImageType, Category}, null, null, null);
    }

    public static void updateImageDescription(Context context, String ID, String Description)
    {
        ContentValues cv = new ContentValues();
        cv.put("Description", Description);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.update(SQLiteDbContext.TABLE_IMAGES, cv, "ID=?",new String[]{ID});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void removePhoto(Context context, String ID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.delete(SQLiteDbContext.TABLE_IMAGES, "ID=?", new String[]{ID});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

}

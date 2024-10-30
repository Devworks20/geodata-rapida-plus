package com.geodata.rapida.plus.SQLite.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.geodata.rapida.plus.SQLite.Class.UserAccountClass;
import com.geodata.rapida.plus.SQLite.Main.SQLiteDbContext;

public class RepositoryUserAccount
{
    private static final String TAG = RepositoryUserAccount.class.getSimpleName();

    private static final String[] allColumns =
            {
                    "ID",
                    "EmployeeID",
                    "UserAccountID",
                    "Username",
                    "Password",
                    "RoleName",
                    "CompleteName",
                    "AppID",
                    "Position",
                    "DtAdded",
                    "isActive"
            };

    public static ContentValues setAccountValues(UserAccountClass userAccountClass)
    {
        ContentValues cValues = new ContentValues();

        cValues.put("EmployeeID",    userAccountClass.getEmployeeID());
        cValues.put("UserAccountID", userAccountClass.getUserAccountID());
        cValues.put("Username",      userAccountClass.getUsername());
        cValues.put("Password",      userAccountClass.getPassword());
        cValues.put("RoleName",      userAccountClass.getRoleName());
        cValues.put("CompleteName",  userAccountClass.getCompleteName());
        cValues.put("AppID",         userAccountClass.getAppID());
        cValues.put("Position",      userAccountClass.getPosition());
        cValues.put("DtAdded",       userAccountClass.getDtAdded());
        cValues.put("isActive",      userAccountClass.getIsActive());

        return  cValues;
    }

    public static void saveAccount(Context context, UserAccountClass sketchImagesClass)
    {
        ContentValues cValues = setAccountValues(sketchImagesClass);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.insert(SQLiteDbContext.TABLE_USER_ACCOUNT, null, cValues);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void updateUser(Context context, UserAccountClass sketchImagesClass, String EmployeeID)
    {
        ContentValues cValues = setAccountValues(sketchImagesClass);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.update(SQLiteDbContext.TABLE_USER_ACCOUNT, cValues, "EmployeeID=?",new String[]{EmployeeID});
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

        return db.query(SQLiteDbContext.TABLE_USER_ACCOUNT, allColumns, null, null, null, null, null);
    }

    public static Cursor realAllData2(Context context, String EmployeeID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_USER_ACCOUNT, allColumns, "EmployeeID=?",new String[]{EmployeeID}, null, null, null);
    }

    public static Cursor realAllData3(Context context, String Username)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_USER_ACCOUNT, allColumns, "Username=?" ,new String[]{Username}, null, null, null);

    }

    public static void removeUser(Context context)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.delete(SQLiteDbContext.TABLE_USER_ACCOUNT, null, null);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

}

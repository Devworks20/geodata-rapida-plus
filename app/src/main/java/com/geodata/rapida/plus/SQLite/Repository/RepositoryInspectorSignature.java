package com.geodata.rapida.plus.SQLite.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.geodata.rapida.plus.SQLite.Class.InspectorSignatureClass;
import com.geodata.rapida.plus.SQLite.Class.RVSSaveDraftDataClass;
import com.geodata.rapida.plus.SQLite.Main.SQLiteDbContext;

public class RepositoryInspectorSignature
{
    private static final String TAG = RepositoryInspectorSignature.class.getSimpleName();

    private static final String[] allColumns =
            {
                    "ID",
                    "UserAccountID",
                    "MissionOrderID",
                    "SignatureID",
                    "SignatureName",
                    "SignaturePath",
                    "SignatureExtension",
                    "DtAdded",
                    "isActive",
                    "isSync"
            };

    public static ContentValues setAccountValues(InspectorSignatureClass inspectorSignatureClass)
    {
        ContentValues cValues = new ContentValues();

        cValues.put("UserAccountID",      inspectorSignatureClass.getUserAccountID());
        cValues.put("MissionOrderID",     inspectorSignatureClass.getMissionOrderID());
        cValues.put("SignatureID",        inspectorSignatureClass.getSignatureID());
        cValues.put("SignatureName",      inspectorSignatureClass.getSignatureName());
        cValues.put("SignaturePath",      inspectorSignatureClass.getSignaturePath());
        cValues.put("SignatureExtension", inspectorSignatureClass.getSignatureExtension());
        cValues.put("DtAdded",            inspectorSignatureClass.getDtAdded());
        cValues.put("isActive",           inspectorSignatureClass.getIsActive());
        cValues.put("isSync",             inspectorSignatureClass.getIsSync());

        return  cValues;
    }

    public static void saveInspectorSignature(Context context, InspectorSignatureClass inspectorSignatureClass)
    {
        ContentValues cValues = setAccountValues(inspectorSignatureClass);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.insert(SQLiteDbContext.TABLE_INSPECTOR_SIGNATURE, null, cValues);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void updateInspectorSignature(Context context, String ID, InspectorSignatureClass inspectorSignatureClass)
    {
        ContentValues cValues = setAccountValues(inspectorSignatureClass);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.update(SQLiteDbContext.TABLE_INSPECTOR_SIGNATURE, cValues, "ID=?",new String[]{ID});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static Cursor realAllData(Context context, String SignatureID, String MissionOrderID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_INSPECTOR_SIGNATURE, allColumns,
                "SignatureID=? AND MissionOrderID=? AND isSync=?",
                new String[]{SignatureID,MissionOrderID, "0"}, null, null, null);

    }

    public static Cursor realAllData2(Context context, String UserAccountID, String MissionOrderID, String SignatureID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_INSPECTOR_SIGNATURE, allColumns,
                "UserAccountID=? AND MissionOrderID=? AND SignatureID=?",
                new String[]{UserAccountID, MissionOrderID, SignatureID}, null, null, null);
    }

    public static void removeInspectorSignature(Context context, String UserAccountID, String MissionOrderID, String SignatureID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.delete(SQLiteDbContext.TABLE_INSPECTOR_SIGNATURE,
                    "UserAccountID=? AND MissionOrderID=? AND SignatureID=?",
                    new String[]{UserAccountID, MissionOrderID, SignatureID});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

}

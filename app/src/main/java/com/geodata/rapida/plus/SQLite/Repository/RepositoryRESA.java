package com.geodata.rapida.plus.SQLite.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.geodata.rapida.plus.SQLite.Class.RESAClass;
import com.geodata.rapida.plus.SQLite.Main.SQLiteDbContext;

public class RepositoryRESA
{
    private static final String TAG = RepositoryRESA.class.getSimpleName();

    private static final String[] allColumns =
            {
                    "ID",
                    "ScreenerID",
                    "MissionOrderID",
                    "Affiliation",
                    "SetDate",
                    "SetTime",
                    "BuildingName",
                    "BuildingAddress",
                    "BuildingContact",
                    "NoOfStoreyAboveGround",
                    "NoOfStoreyBelowGround",
                    "TypeOfConstruction",
                    "PrimaryOccupancy",
                    "ApproxFootPrintAreaSM",
                    "NoOfResidentialUnits",
                    "NoOfCommercialUnits",
                    "BuildingOffFoundationType",
                    "StoryLeaningType",
                    "OtherStructuralDamageType",
                    "OtherFallingHazardType",
                    "CrackingType",
                    "OtherOptionalType",
                    "Comments",
                    "EstimatedBuildingDamage",
                    "PreviousPostingEstimatedDamage",
                    "PreviousPostingDate",
                    "ColorPlacard",
                    "FurtherActionsBarricades",
                    "FurtherActionsDetailedEvaluationRecommended",
                    "FurtherActionsOtherRecommended",
                    "FurtherActionComments",
                    "FurtherActionEstimatedDamage",
                    "FurtherActionRecommendations",
                    "SecondComments",
                    "InspectedBy"
            };

    public static ContentValues setDataValues(RESAClass resaClass)
    {
        ContentValues cValues = new ContentValues();

        cValues.put("ScreenerID",                     resaClass.getScreenerID());
        cValues.put("MissionOrderID",                 resaClass.getMissionOrderID());
        cValues.put("Affiliation",                    resaClass.getAffiliation());
        cValues.put("SetDate",                        resaClass.getSetDate());
        cValues.put("SetTime",                        resaClass.getSetTime());
        cValues.put("BuildingName",                   resaClass.getBuildingName());
        cValues.put("BuildingAddress",                resaClass.getBuildingAddress());
        cValues.put("BuildingContact",                resaClass.getBuildingContact());
        cValues.put("NoOfStoreyAboveGround",          resaClass.getNoOfStoreyAboveGround());
        cValues.put("NoOfStoreyBelowGround",          resaClass.getNoOfStoreyBelowGround());
        cValues.put("TypeOfConstruction",             resaClass.getTypeOfConstruction());
        cValues.put("PrimaryOccupancy",               resaClass.getPrimaryOccupancy());
        cValues.put("ApproxFootPrintAreaSM",          resaClass.getApproxFootPrintAreaSM());
        cValues.put("NoOfResidentialUnits",           resaClass.getNoOfResidentialUnits());
        cValues.put("NoOfCommercialUnits",            resaClass.getNoOfCommercialUnits());
        cValues.put("BuildingOffFoundationType",      resaClass.getBuildingOffFoundationType());
        cValues.put("StoryLeaningType",               resaClass.getStoryLeaningType());
        cValues.put("OtherStructuralDamageType",      resaClass.getOtherStructuralDamageType());
        cValues.put("OtherFallingHazardType",         resaClass.getOtherFallingHazardType());
        cValues.put("CrackingType",                   resaClass.getCrackingType());
        cValues.put("OtherOptionalType",              resaClass.getOtherOptionalType());
        cValues.put("Comments",                       resaClass.getComments());
        cValues.put("EstimatedBuildingDamage",        resaClass.getEstimatedBuildingDamage());
        cValues.put("PreviousPostingEstimatedDamage", resaClass.getPreviousPostingEstimatedDamage());
        cValues.put("PreviousPostingDate",            resaClass.getPreviousPostingDate());
        cValues.put("ColorPlacard",                   resaClass.getColorPlacard());
        cValues.put("FurtherActionsBarricades",      resaClass.getFurtherActionsBarricades());
        cValues.put("FurtherActionsDetailedEvaluationRecommended", resaClass.getFurtherActionsDetailedEvaluationRecommended());
        cValues.put("FurtherActionsOtherRecommended", resaClass.getFurtherActionsOtherRecommended());
        cValues.put("FurtherActionComments",          resaClass.getFurtherActionComments());
        cValues.put("FurtherActionEstimatedDamage",   resaClass.getFurtherActionEstimatedDamage());
        cValues.put("FurtherActionRecommendations",   resaClass.getFurtherActionRecommendations());
        cValues.put("SecondComments",                 resaClass.getSecondComments());
        cValues.put("InspectedBy",                    resaClass.getInspectedBy());

        return  cValues;
    }

    public static Cursor realAllData(Context context, String ScreenerID, String MissionOrderID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_RESA, allColumns,
                "ScreenerID=? AND MissionOrderID=?",new String[]{ScreenerID, MissionOrderID}, null, null, null);
    }

    public static void saveRESA(Context context, RESAClass resaClass)
    {
        ContentValues cValues = setDataValues(resaClass);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.insert(SQLiteDbContext.TABLE_RESA, null, cValues);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void updateRESA(Context context, String ScreenerID, String MissionOrderID, RESAClass resaClass)
    {
        ContentValues cValues = setDataValues(resaClass);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.update(SQLiteDbContext.TABLE_RESA, cValues,
                    "ScreenerID=? AND MissionOrderID=?",new String[]{ScreenerID, MissionOrderID});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

}

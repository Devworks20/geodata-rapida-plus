package com.geodata.rapida.plus.SQLite.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.geodata.rapida.plus.SQLite.Class.DESAClass;
import com.geodata.rapida.plus.SQLite.Class.RESAClass;
import com.geodata.rapida.plus.SQLite.Main.SQLiteDbContext;

public class RepositoryDESA
{
    private static final String TAG = RepositoryDESA.class.getSimpleName();

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
                    "CollapseType",
                    "CollapseComment",
                    "BuildingStoryLeaningType",
                    "BuildingStoryLeaningComment",
                    "OverAllHazardsOtherType",
                    "FoundationType",
                    "FoundationComment",
                    "RoofFloorVLType",
                    "RoofFloorVLComment",
                    "CPCType",
                    "CPCComment",
                    "DiaphragmsHBType",
                    "DiaphragmsHBComment",
                    "WallsVBType",
                    "WallsVBComment",
                    "PrecastConnectionsType",
                    "PrecastConnectionsComment",
                    "ParapetsOrnamentationType",
                    "ParapetsOrnamentationComment",
                    "CladdingGlazingType",
                    "CladdingGlazingComment",
                    "CeilingLightFixturesType",
                    "CeilingLightFixturesComment",
                    "InteriorWallsPartitionsType",
                    "InteriorWallsPartitionsComment",
                    "ElevatorsType",
                    "ElevatorsComment",
                    "StairsExitType",
                    "StairsExitComment",
                    "ElectricGasType",
                    "ElectricGasComment",
                    "NonstructuralHazardOtherType",
                    "SlopeFailureDebrisType",
                    "SlopeFailureDebrisComment",
                    "GroundMovementFissuresType",
                    "GroundMovementFissuresComment",
                    "GeotechnicalHazardOther",
                    "EstimatedBuildingDamage",
                    "PreviousPostingEstimatedDamage",
                    "PreviousPostingDate",
                    "ColorPlacard",
                    "FurtherActionsBarricades",
                    "FurtherActionsEngineeringEvaluationRecommended",
                    "FurtherActionsOtherRecommendation",
                    "BarricadesComment",
                    "EngineeringEvaluationRecommendedType",
                    "RecommendationsType",
                    "Comments",
                    "InspectedBy"
            };

    public static ContentValues setDataValues(DESAClass desaClass)
    {
        ContentValues cValues = new ContentValues();

        cValues.put("ScreenerID",                            desaClass.getScreenerID());
        cValues.put("MissionOrderID",                        desaClass.getMissionOrderID());
        cValues.put("Affiliation",                           desaClass.getAffiliation());
        cValues.put("SetDate",                               desaClass.getSetDate());
        cValues.put("SetTime",                               desaClass.getSetTime());
        cValues.put("BuildingName",                          desaClass.getBuildingName());
        cValues.put("BuildingAddress",                       desaClass.getBuildingAddress());
        cValues.put("BuildingContact",                       desaClass.getBuildingContact());
        cValues.put("NoOfStoreyAboveGround",                 desaClass.getNoOfStoreyAboveGround());
        cValues.put("NoOfStoreyBelowGround",                 desaClass.getNoOfStoreyBelowGround());
        cValues.put("TypeOfConstruction",                    desaClass.getTypeOfConstruction());
        cValues.put("PrimaryOccupancy",                      desaClass.getPrimaryOccupancy());
        cValues.put("ApproxFootPrintAreaSM",                 desaClass.getApproxFootPrintAreaSM());
        cValues.put("NoOfResidentialUnits",                  desaClass.getNoOfResidentialUnits());
        cValues.put("NoOfCommercialUnits",                   desaClass.getNoOfCommercialUnits());
        cValues.put("CollapseType",                          desaClass.getCollapseType());
        cValues.put("CollapseComment",                       desaClass.getCollapseComment());
        cValues.put("BuildingStoryLeaningType" ,             desaClass.getBuildingStoryLeaningType());
        cValues.put("BuildingStoryLeaningComment" ,          desaClass.getBuildingStoryLeaningComment());
        cValues.put("OverAllHazardsOtherType" ,              desaClass.getOverAllHazardsOtherType());
        cValues.put("FoundationType" ,                       desaClass.getFoundationType());
        cValues.put("FoundationComment" ,                    desaClass.getFoundationComment());
        cValues.put("RoofFloorVLType" ,                      desaClass.getRoofFloorVLType());
        cValues.put("RoofFloorVLComment",                    desaClass.getRoofFloorVLComment());
        cValues.put("CPCType",                               desaClass.getCPCType());
        cValues.put("CPCComment",                            desaClass.getCPCComment());
        cValues.put("DiaphragmsHBType",                      desaClass.getDiaphragmsHBType());
        cValues.put("DiaphragmsHBComment",                   desaClass.getDiaphragmsHBComment());
        cValues.put("WallsVBType",                           desaClass.getWallsVBType());
        cValues.put("WallsVBComment",                        desaClass.getWallsVBComment());
        cValues.put("PrecastConnectionsType" ,               desaClass.getPrecastConnectionsType());
        cValues.put("PrecastConnectionsComment",             desaClass.getPrecastConnectionsComment());
        cValues.put("ParapetsOrnamentationType",             desaClass.getParapetsOrnamentationType());
        cValues.put("ParapetsOrnamentationComment",          desaClass.getParapetsOrnamentationComment());
        cValues.put("CladdingGlazingType",                   desaClass.getCladdingGlazingType());
        cValues.put("CladdingGlazingComment",                desaClass.getCladdingGlazingComment());
        cValues.put("CeilingLightFixturesType",              desaClass.getCeilingLightFixturesType());
        cValues.put("CeilingLightFixturesComment",           desaClass.getCeilingLightFixturesComment());
        cValues.put("InteriorWallsPartitionsType",           desaClass.getInteriorWallsPartitionsType());
        cValues.put("InteriorWallsPartitionsComment",        desaClass.getInteriorWallsPartitionsComment());
        cValues.put("ElevatorsType",                         desaClass.getElevatorsType());
        cValues.put("ElevatorsComment",                      desaClass.getElevatorsComment());
        cValues.put("StairsExitType",                        desaClass.getStairsExitType());
        cValues.put("StairsExitComment",                     desaClass.getStairsExitComment());
        cValues.put("ElectricGasType",                       desaClass.getElectricGasType());
        cValues.put("ElectricGasComment",                    desaClass.getElectricGasComment());
        cValues.put("NonstructuralHazardOtherType",          desaClass.getNonstructuralHazardOtherType());
        cValues.put("SlopeFailureDebrisType",                desaClass.getSlopeFailureDebrisType());
        cValues.put("SlopeFailureDebrisComment" ,            desaClass.getSlopeFailureDebrisComment());
        cValues.put("GroundMovementFissuresType" ,           desaClass.getGroundMovementFissuresType());
        cValues.put("GroundMovementFissuresComment" ,        desaClass.getGroundMovementFissuresComment());
        cValues.put("GeotechnicalHazardOther",               desaClass.getGeotechnicalHazardOther());
        cValues.put("EstimatedBuildingDamage",               desaClass.getEstimatedBuildingDamage());
        cValues.put("PreviousPostingEstimatedDamage",        desaClass.getPreviousPostingEstimatedDamage());
        cValues.put("PreviousPostingDate",                   desaClass.getPreviousPostingDate());
        cValues.put("ColorPlacard",                          desaClass.getColorPlacard());
        cValues.put("FurtherActionsBarricades",                       desaClass.getFurtherActionsBarricades());
        cValues.put("FurtherActionsEngineeringEvaluationRecommended", desaClass.getFurtherActionsEngineeringEvaluationRecommended());
        cValues.put("FurtherActionsOtherRecommendation",              desaClass.getFurtherActionsOtherRecommendation());
        cValues.put("BarricadesComment",                     desaClass.getBarricadesComment());
        cValues.put("EngineeringEvaluationRecommendedType",  desaClass.getEngineeringEvaluationRecommendedType());
        cValues.put("RecommendationsType",                   desaClass.getRecommendationsType());
        cValues.put("Comments",                              desaClass.getComments());
        cValues.put("InspectedBy",                           desaClass.getInspectedBy());

        return  cValues;
    }

    public static Cursor realAllData(Context context, String ScreenerID, String MissionOrderID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_DESA, allColumns,
                "ScreenerID=? AND MissionOrderID=?",new String[]{ScreenerID, MissionOrderID}, null, null, null);
    }

    public static void saveDESA(Context context, DESAClass desaClass)
    {
        ContentValues cValues = setDataValues(desaClass);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.insert(SQLiteDbContext.TABLE_DESA, null, cValues);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void updateDESA(Context context, String ScreenerID, String MissionOrderID, DESAClass desaClass)
    {
        ContentValues cValues = setDataValues(desaClass);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.update(SQLiteDbContext.TABLE_DESA, cValues,
                    "ScreenerID=? AND MissionOrderID=?",new String[]{ScreenerID, MissionOrderID});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

}

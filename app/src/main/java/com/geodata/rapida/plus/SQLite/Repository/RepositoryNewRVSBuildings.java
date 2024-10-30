package com.geodata.rapida.plus.SQLite.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.geodata.rapida.plus.Retrofit.Model.BuildingListOfEDIModel;
import com.geodata.rapida.plus.SQLite.Main.SQLiteDbContext;

public class RepositoryNewRVSBuildings
{
    private static final String TAG = RepositoryNewRVSBuildings.class.getSimpleName();

    private static final String[] allColumns =
            {
                    "ID",
                    "ScreenerID",
                    "AssetInfoBuildingID",
                    "AssetID",
                    "ASSET_ID",
                    "AssetCatID",
                    "BuildingCode",
                    "AssetCode",
                    "BuildingLandPin",
                    "TCTNo",
                    "Cost",
                    "NavAssetCatID",
                    "BuildingClassification",
                    "BuildingPhoto",
                    "InventoryYear",
                    "AccountCode",
                    "BuildingName",
                    "BIN",
                    "Agency",
                    "DateBuilt",
                    "BuildingAge",
                    "BookValue",
                    "AssessedValue",
                    "Groups",
                    "BldgOwnershipType",
                    "SoilType",
                    "NoOFPersons",
                    "StructuralTypeDesc",
                    "StructuralTypeCode",
                    "FloorArea",
                    "Height",
                    "NoOfStories",
                    "OpenSpace",
                    "AveAreaPerFloor",
                    "CostPerSqm",
                    "NoOfUnits" ,
                    "OwnerName",
                    "Position",
                    "ContactNo",
                    "Email",
                    "BuildingPhotoPath",
                    "SeismicityRegion",
                    "LotOwnType",
                    "LotArea",
                    "BIRZonalValue",
                    "BuildingCondition",
                    "ValueOfRepair",
                    "Remarks",
                    "Region",
                    "Province",
                    "City",
                    "District",
                    "DistrictOffice",
                    "Barangay",
                    "BlockNo",
                    "Street",
                    "SubdCompVill",
                    "Purok",
                    "Sitio",
                    "Zip",
                    "Latitude",
                    "Longitude",
                    "InspectedBy",
                    "ConstructionStatus",
                    "VisitNo",
                    "StoreyNo",
                    "Residential",
                    "BldgPermitNo",
                    "CompanyName",
                    "PresidentName",
                    "isWoodFrame",
                    "isSteelFrame",
                    "isConcreteFrame",
                    "BasicScoreWF",
                    "BasicScoreCF",
                    "BasicScoreSF",
                    "VerticalIrregularityWF",
                    "VerticalIrregularitySF",
                    "VerticalIrregularityCF",
                    "PlanIrregularityWF",
                    "PlanIrregularitySF",
                    "PlanIrregularityCF",
                    "PreCodeWF",
                    "PreCodeSF",
                    "PreCodeCF",
                    "PostBenchmarkWF",
                    "PostBenchmarkSF",
                    "PostBenchmarkCF",
                    "SoilTypeC2WF",
                    "SoilTypeC2SF",
                    "SoilTypeC2CF",
                    "SoilTypeD3WF",
                    "SoilTypeD3SF",
                    "SoilTypeD3CF",
                    "SoilTypeE4WF",
                    "SoilTypeE4SF",
                    "SoilTypeE4CF",
                    "isPreCode",
                    "isPostBenchmark",
                    "isSoilTypeC2" ,
                    "isSoilTypeD3",
                    "isSoilTypeE4",
                    "WoodFrameFinalScore ",
                    "SteelFrameFinalScore",
                    "ConcreteFrameFinalScore",
                    "StructuralComponents",
                    "NonStructuralComponents",
                    "AncillaryAuxiliaryComponents",
                    "BuildingPhotoBase64",
                    "isSynced"
            };

    public static ContentValues setAccountValues(BuildingListOfEDIModel buildingListOfEDIModel)
    {
        ContentValues cValues = new ContentValues();

        cValues.put("ScreenerID",                   buildingListOfEDIModel.getScreenerID());
        cValues.put("AssetInfoBuildingID",          buildingListOfEDIModel.getAssetInfoBuildingID());
        cValues.put("AssetID",                      buildingListOfEDIModel.getAssetID());
        cValues.put("ASSET_ID",                     buildingListOfEDIModel.getASSET_ID());
        cValues.put("AssetCatID",                   buildingListOfEDIModel.getAssetCatID());
        cValues.put("BuildingCode",                 buildingListOfEDIModel.getBuildingCode());
        cValues.put("AssetCode",                    buildingListOfEDIModel.getAssetCode());
        cValues.put("BuildingLandPin",              buildingListOfEDIModel.getBuildingLandPin());
        cValues.put("TCTNo",                        buildingListOfEDIModel.getTCTNo());
        cValues.put("Cost",                         buildingListOfEDIModel.getCost());
        cValues.put("NavAssetCatID",                buildingListOfEDIModel.getNavAssetCatID());
        cValues.put("BuildingClassification",       buildingListOfEDIModel.getBuildingClassification());
        cValues.put("BuildingPhoto",                buildingListOfEDIModel.getBuildingPhoto());
        cValues.put("InventoryYear",                buildingListOfEDIModel.getInventoryYear());
        cValues.put("AccountCode",                  buildingListOfEDIModel.getAccountCode());
        cValues.put("BuildingName",                 buildingListOfEDIModel.getBuildingName());
        cValues.put("BIN",                          buildingListOfEDIModel.getBIN());
        cValues.put("Agency",                       buildingListOfEDIModel.getAgency());
        cValues.put("DateBuilt",                    buildingListOfEDIModel.getDateBuilt());
        cValues.put("BuildingAge",                  buildingListOfEDIModel.getBuildingAge());
        cValues.put("BookValue",                    buildingListOfEDIModel.getBookValue());
        cValues.put("AssessedValue",                buildingListOfEDIModel.getAssessedValue());
        cValues.put("Groups",                       buildingListOfEDIModel.getGroups());
        cValues.put("BldgOwnershipType",            buildingListOfEDIModel.getBldgOwnershipType());
        cValues.put("SoilType",                     buildingListOfEDIModel.getSoilType());
        cValues.put("NoOFPersons",                  buildingListOfEDIModel.getNoOFPersons());
        cValues.put("StructuralTypeDesc",           buildingListOfEDIModel.getStructuralTypeDesc());
        cValues.put("StructuralTypeCode",           buildingListOfEDIModel.getStructuralTypeCode());
        cValues.put("FloorArea",                    buildingListOfEDIModel.getFloorArea());
        cValues.put("Height",                       buildingListOfEDIModel.getHeight());
        cValues.put("NoOfStories",                  buildingListOfEDIModel.getNoOfStories());
        cValues.put("OpenSpace",                    buildingListOfEDIModel.getOpenSpace());
        cValues.put("AveAreaPerFloor",              buildingListOfEDIModel.getAveAreaPerFloor());
        cValues.put("CostPerSqm",                   buildingListOfEDIModel.getCostPerSqm());
        cValues.put("NoOfUnits",                    buildingListOfEDIModel.getNoOfUnits());
        cValues.put("OwnerName",                    buildingListOfEDIModel.getOwnerName());
        cValues.put("Position",                     buildingListOfEDIModel.getPosition());
        cValues.put("ContactNo",                    buildingListOfEDIModel.getContactNo());
        cValues.put("Email",                        buildingListOfEDIModel.getEmail());
        cValues.put("BuildingPhotoPath",            buildingListOfEDIModel.getBuildingPhotoPath());
        cValues.put("SeismicityRegion",             buildingListOfEDIModel.getSeismicityRegion());
        cValues.put("LotOwnType",                   buildingListOfEDIModel.getLotOwnType());
        cValues.put("LotArea",                      buildingListOfEDIModel.getLotArea());
        cValues.put("BIRZonalValue",                buildingListOfEDIModel.getBIRZonalValue());
        cValues.put("BuildingCondition",            buildingListOfEDIModel.getBuildingCondition());
        cValues.put("ValueOfRepair",                buildingListOfEDIModel.getValueOfRepair());
        cValues.put("Remarks",                      buildingListOfEDIModel.getRemarks());
        cValues.put("Region",                       buildingListOfEDIModel.getRegion());
        cValues.put("Province",                     buildingListOfEDIModel.getProvince());
        cValues.put("City",                         buildingListOfEDIModel.getCity());
        cValues.put("District",                     buildingListOfEDIModel.getDistrict());
        cValues.put("DistrictOffice",               buildingListOfEDIModel.getDistrictOffice());
        cValues.put("Barangay",                     buildingListOfEDIModel.getBarangay());
        cValues.put("BlockNo",                      buildingListOfEDIModel.getBlockNo());
        cValues.put("Street",                       buildingListOfEDIModel.getStreet());
        cValues.put("SubdCompVill",                 buildingListOfEDIModel.getSubdCompVill());
        cValues.put("Purok",                        buildingListOfEDIModel.getPurok());
        cValues.put("Sitio",                        buildingListOfEDIModel.getSitio());
        cValues.put("Zip",                          buildingListOfEDIModel.getZip());
        cValues.put("Latitude",                     buildingListOfEDIModel.getLatitude());
        cValues.put("Longitude",                    buildingListOfEDIModel.getLongitude());
        cValues.put("InspectedBy",                  buildingListOfEDIModel.getInspectedBy());
        cValues.put("ConstructionStatus",           buildingListOfEDIModel.getConstructionStatus());
        cValues.put("VisitNo",                      buildingListOfEDIModel.getVisitNo());
        cValues.put("StoreyNo",                     buildingListOfEDIModel.getStoreyNo());
        cValues.put("Residential",                  buildingListOfEDIModel.getResidential());
        cValues.put("BldgPermitNo",                 buildingListOfEDIModel.getBldgPermitNo());
        cValues.put("OwnerName",                    buildingListOfEDIModel.getOwnerName());
        cValues.put("CompanyName",                  buildingListOfEDIModel.getCompanyName());
        cValues.put("PresidentName",                buildingListOfEDIModel.getPresidentName());
        cValues.put("isWoodFrame",                  buildingListOfEDIModel.getIsWoodFrame());
        cValues.put("isSteelFrame",                 buildingListOfEDIModel.getIsSteelFrame());
        cValues.put("isConcreteFrame",              buildingListOfEDIModel.getIsConcreteFrame ());
        cValues.put("BasicScoreWF",                 buildingListOfEDIModel.getBasicScoreWF());
        cValues.put("BasicScoreSF",                 buildingListOfEDIModel.getBasicScoreSF());
        cValues.put("BasicScoreCF",                 buildingListOfEDIModel.getBasicScoreCF());
        cValues.put("VerticalIrregularityWF",       buildingListOfEDIModel.getVerticalIrregularityWF());
        cValues.put("VerticalIrregularitySF",       buildingListOfEDIModel.getVerticalIrregularitySF());
        cValues.put("VerticalIrregularityCF",       buildingListOfEDIModel.getVerticalIrregularityCF());
        cValues.put("PlanIrregularityWF",           buildingListOfEDIModel.getPlanIrregularityWF());
        cValues.put("PlanIrregularitySF",           buildingListOfEDIModel.getPlanIrregularitySF());
        cValues.put("PlanIrregularityCF",           buildingListOfEDIModel.getPlanIrregularityCF());
        cValues.put("PreCodeWF",                    buildingListOfEDIModel.getPreCodeWF());
        cValues.put("PreCodeSF",                    buildingListOfEDIModel.getPreCodeSF());
        cValues.put("PreCodeCF",                    buildingListOfEDIModel.getPreCodeCF());
        cValues.put("PostBenchmarkWF",              buildingListOfEDIModel.getPostBenchmarkWF());
        cValues.put("PostBenchmarkSF",              buildingListOfEDIModel.getPostBenchmarkSF());
        cValues.put("PostBenchmarkCF",              buildingListOfEDIModel.getPostBenchmarkCF());
        cValues.put("SoilTypeC2WF",                 buildingListOfEDIModel.getSoilTypeC2WF());
        cValues.put("SoilTypeC2SF",                 buildingListOfEDIModel.getSoilTypeC2SF());
        cValues.put("SoilTypeC2CF",                 buildingListOfEDIModel.getSoilTypeC2CF());
        cValues.put("SoilTypeD3WF",                 buildingListOfEDIModel.getSoilTypeD3WF());
        cValues.put("SoilTypeD3SF",                 buildingListOfEDIModel.getSoilTypeD3SF());
        cValues.put("SoilTypeD3CF",                 buildingListOfEDIModel.getSoilTypeD3CF());
        cValues.put("SoilTypeE4WF",                 buildingListOfEDIModel.getSoilTypeE4WF());
        cValues.put("SoilTypeE4SF",                 buildingListOfEDIModel.getSoilTypeE4SF());
        cValues.put("SoilTypeE4CF",                 buildingListOfEDIModel.getSoilTypeE4CF());
        cValues.put("isPreCode      ",              buildingListOfEDIModel.getIsPreCode());
        cValues.put("isPostBenchmark",              buildingListOfEDIModel.getIsPostBenchmark());
        cValues.put("isSoilTypeC2",                 buildingListOfEDIModel.getIsSoilTypeC2());
        cValues.put("isSoilTypeD3",                 buildingListOfEDIModel.getIsSoilTypeD3());
        cValues.put("isSoilTypeE4",                 buildingListOfEDIModel.getIsSoilTypeE4());
        cValues.put("WoodFrameFinalScore",          buildingListOfEDIModel.getWoodFrameFinalScore());
        cValues.put("SteelFrameFinalScore",         buildingListOfEDIModel.getSteelFrameFinalScore());
        cValues.put("ConcreteFrameFinalScore",      buildingListOfEDIModel.getConcreteFrameFinalScore());
        cValues.put("StructuralComponents",         buildingListOfEDIModel.getStructuralComponents());
        cValues.put("NonStructuralComponents",      buildingListOfEDIModel.getNonStructuralComponents());
        cValues.put("AncillaryAuxiliaryComponents", buildingListOfEDIModel.getAncillaryAuxiliaryComponents());
        cValues.put("BuildingPhotoBase64",          buildingListOfEDIModel.getBuildingPhotoBase64());
        cValues.put("dtAdded",                      buildingListOfEDIModel.getDtAdded());
        cValues.put("isSynced",                     buildingListOfEDIModel.getIsSynced());

        return  cValues;
    }

    public static void saveNewRVSBuilding(Context context, BuildingListOfEDIModel buildingListOfEDIModel)
    {
        ContentValues cValues = setAccountValues(buildingListOfEDIModel);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.insert(SQLiteDbContext.TABLE_NEW_RVS, null, cValues);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void updateNewRVSBuildingData(Context context, BuildingListOfEDIModel buildingListOfEDIModel, String ID)
    {
        ContentValues cValues = setAccountValues(buildingListOfEDIModel);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.update(SQLiteDbContext.TABLE_NEW_RVS,  cValues, "ID=?", new String[]{ID});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void updateNewRVSBuildingStatus(Context context, String AssetID)
    {
        ContentValues cValues = new ContentValues();
        cValues.put("isSynced", "1");

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.update(SQLiteDbContext.TABLE_NEW_RVS,  cValues, "AssetID=? AND isSynced=?", new String[]{AssetID, "0"});
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static Cursor retrieveData(Context context, String ScreenerID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_NEW_RVS, allColumns, "ScreenerID=? AND AssetInfoBuildingID=? AND isSynced=?",
                new String[]{ScreenerID, "", "0"}, null, null, null);
    }

    public static Cursor retrieveData2(Context context, String ScreenerID, String AssetInfoBuildingID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_NEW_RVS, allColumns, "ScreenerID=? AND AssetInfoBuildingID=? AND isSynced=?",
                new String[]{ScreenerID, AssetInfoBuildingID, "0"}, null, null, null);
    }

    public static Cursor retrieveData3(Context context, String ScreenerID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_NEW_RVS, allColumns, "ScreenerID=? AND isSynced=?",
                new String[]{ScreenerID, "0"}, null, null, null);
    }

}

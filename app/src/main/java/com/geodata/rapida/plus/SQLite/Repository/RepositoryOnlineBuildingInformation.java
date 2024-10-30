package com.geodata.rapida.plus.SQLite.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.geodata.rapida.plus.Retrofit.Model.BuildingInformationModel;
import com.geodata.rapida.plus.Retrofit.Model.MissionOrdersModel;
import com.geodata.rapida.plus.SQLite.Class.RVSSaveDraftDataClass;
import com.geodata.rapida.plus.SQLite.Main.SQLiteDbContext;

public class RepositoryOnlineBuildingInformation
{
    private static final String TAG = RepositoryOnlineBuildingInformation.class.getSimpleName();

    private static final String[] allColumns =
            {
                    "ID",
                    "ScreenerID",
                    "MissionOrderID",
                    "AccountCode",
                    "AccountCodeID",
                    "Age",
                    "Agency",
                    "AgencyID",
                    "Area_Purok_Sitio",
                    "AssessedValue",
                    "AssetID",
                    "AveAreaPerFloor",
                    "BIN",
                    "BIRZonalValue",
                    "BldgConditionID",
                    "BldgGroup",
                    "BldgGroupID",
                    "BldgOwnershipTypeID",
                    "Bldg_Rm_Flr",
                    "BookValue",
                    "BuildingCode",
                    "BuildingConditionReport",
                    "BuildingDisplayID",
                    "BuildingInfoID",
                    "BuildingInventoryYearID",
                    "BuildingLandPin",
                    "BuildingName",
                    "BuildingNoOfPersons",
                    "BuildingOwnershipType",
                    "BuildingSoilType",
                    "ContactNo",
                    "Cost",
                    "CostPerSqm",
                    "DPWHOB_ID",
                    "DateFinished",
                    "DateSaved",
                    "DateUpdated",
                    "DistrictOffice",
                    "DistrictOfficeID",
                    "Email",
                    "FaultDistance",
                    "FilesUrl",
                    "FloorArea",
                    "GOB_ID",
                    "Height",
                    "ImageFile",
                    "ImageUrl",
                    "InventoryYear",
                    "Lat",
                    "LifeSpanOfBuilding",
                    "Location",
                    "Long",
                    "Altitude",
                    "LotArea",
                    "LotOwnershipType",
                    "LotOwnershipTypeID",
                    "NearestFault",
                    "NoOfFloors",
                    "NoOfPersonsID",
                    "NoOfUnits",
                    "Occupancies",
                    "OjectID",
                    "OpenSpace",
                    "OwnerName",
                    "OwnershipType",
                    "Position",
                    "Remarks",
                    "Seismicity",
                    "SoilTypeID",
                    "StructureType",
                    "StructureTypeID",
                    "TCTNo",
                    "ValueOfRepair",
                    "ZipCode",
                    "brgyCode",
                    "citymunCode",
                    "gisBarangay",
                    "gisCity",
                    "gisProvince",
                    "gisRegion",
                    "isForInspection",
                    "provCode",
                    "regCode"
            };

    public static ContentValues setBuildingInformationValues(BuildingInformationModel buildingInformationModel)
    {
        ContentValues cValues = new ContentValues();

         cValues.put("ScreenerID",              buildingInformationModel.getScreenerID());
         cValues.put("MissionOrderID",          buildingInformationModel.getMissionOrderID());

         cValues.put("AccountCode",             buildingInformationModel.getAccountCode().getAccountCode1()); //AccountCode1

         cValues.put("AccountCodeID",           buildingInformationModel.getAccountCodeID());
         cValues.put("Age",                     buildingInformationModel.getAge());

         cValues.put("Agency",                  buildingInformationModel.getAgency().getAgencyName()); //AgencyName

         cValues.put("AgencyID",                buildingInformationModel.getAgencyID());
         cValues.put("Area_Purok_Sitio",        buildingInformationModel.getArea_Purok_Sitio());
         cValues.put("AssessedValue",           buildingInformationModel.getAssessedValue());
         cValues.put("AssetID",                 buildingInformationModel.getAssetID());
         cValues.put("AveAreaPerFloor",         buildingInformationModel.getAveAreaPerFloor());
         cValues.put("BIN",                     buildingInformationModel.getBIN());
         cValues.put("BIRZonalValue",           buildingInformationModel.getBIRZonalValue());
         cValues.put("BldgConditionID",         buildingInformationModel.getBldgConditionID());

         cValues.put("BldgGroup",               buildingInformationModel.getBldgGroup().getBldgGroupDesc()); //BldgGroupDesc

         cValues.put("BldgGroupID",             buildingInformationModel.getBldgGroupID());
         cValues.put("BldgOwnershipTypeID",     buildingInformationModel.getBldgOwnershipTypeID());
         cValues.put("Bldg_Rm_Flr",             buildingInformationModel.getBldg_Rm_Flr());
         cValues.put("BookValue",               buildingInformationModel.getBookValue());
         cValues.put("BuildingCode",            buildingInformationModel.getBuildingCode());

         cValues.put("BuildingConditionReport", buildingInformationModel.getBuildingConditionReport().getBldgConditionName()); //BldgConditionName


         cValues.put("BuildingDisplayID",       buildingInformationModel.getBuildingDisplayID());
         cValues.put("BuildingInfoID",          buildingInformationModel.getBuildingInfoID());
         cValues.put("BuildingInventoryYearID", buildingInformationModel.getBuildingInventoryYearID());
         cValues.put("BuildingLandPin",         buildingInformationModel.getBuildingLandPin());
         cValues.put("BuildingName",            buildingInformationModel.getBuildingName());

         cValues.put("BuildingNoOfPersons",     buildingInformationModel.getBuildingNoOfPersons().getNoOfPersons()); //NoOfPersons
         cValues.put("BuildingOwnershipType",   buildingInformationModel.getBuildingOwnershipType().getBuildingOwnershipTypeDesc()); //BuildingOwnershipTypeDesc
         cValues.put("BuildingSoilType",        buildingInformationModel.getBuildingSoilType().getBuildingSoilTypeDesc()); //BuildingSoilTypeDesc

         cValues.put("ContactNo",               buildingInformationModel.getContactNo());
         cValues.put("Cost",                    buildingInformationModel.getCost());
         cValues.put("CostPerSqm",              buildingInformationModel.getCostPerSqm());
         cValues.put("DPWHOB_ID",               buildingInformationModel.getDPWHOB_ID());
         cValues.put("DateFinished",            buildingInformationModel.getDateFinished());
         cValues.put("DateSaved",               buildingInformationModel.getDateSaved());
         cValues.put("DateUpdated",             buildingInformationModel.getDateUpdated());

         cValues.put("DistrictOffice",          buildingInformationModel.getDistrictOffice().getDistrictOffice1()); //DistrictOffice1

         cValues.put("DistrictOfficeID",        buildingInformationModel.getDistrictOfficeID());
         cValues.put("Email",                   buildingInformationModel.getEmail());
         cValues.put("FaultDistance",           buildingInformationModel.getFaultDistance());
         cValues.put("FilesUrl",                buildingInformationModel.getFilesUrl());
         cValues.put("FloorArea",               buildingInformationModel.getFloorArea());
         cValues.put("GOB_ID",                  buildingInformationModel.getGOB_ID());
         cValues.put("Height",                  buildingInformationModel.getHeight());
         cValues.put("ImageFile",               buildingInformationModel.getImageFile());
         cValues.put("ImageUrl",                buildingInformationModel.getImageUrl());
         cValues.put("InventoryYear",           buildingInformationModel.getInventoryYear());
         cValues.put("Lat",                     buildingInformationModel.getLat());
         cValues.put("LifeSpanOfBuilding",      buildingInformationModel.getLifeSpanOfBuilding());
         cValues.put("Location",                buildingInformationModel.getLocation());
         cValues.put("Long",                    buildingInformationModel.getLong());
         cValues.put("Altitude",                buildingInformationModel.getAltitude());

         cValues.put("LotArea",                 buildingInformationModel.getLotArea());

         cValues.put("LotOwnershipType",        buildingInformationModel.getLotOwnershipType().getLotOwnershipTypeDesc()); //LotOwnershipTypeDesc

         cValues.put("LotOwnershipTypeID",      buildingInformationModel.getLotOwnershipTypeID());
         cValues.put("NearestFault",            buildingInformationModel.getNearestFault());
         cValues.put("NoOfFloors",              buildingInformationModel.getNoOfFloors());
         cValues.put("NoOfPersonsID",           buildingInformationModel.getNoOfPersonsID());
         cValues.put("NoOfUnits",               buildingInformationModel.getNoOfUnits());
         cValues.put("Occupancies",             buildingInformationModel.getOccupancies());
         cValues.put("OjectID",                 buildingInformationModel.getOjectID());
         cValues.put("OpenSpace",               buildingInformationModel.getOpenSpace());
         cValues.put("OwnerName",               buildingInformationModel.getOwnerName());
         cValues.put("OwnershipType",           buildingInformationModel.getOwnershipType());
         cValues.put("Position",                buildingInformationModel.getPosition());
         cValues.put("Remarks",                 buildingInformationModel.getRemarks());
         cValues.put("Seismicity",              buildingInformationModel.getSeismicity());
         cValues.put("SoilTypeID",              buildingInformationModel.getSoilTypeID());

         cValues.put("StructureType",           buildingInformationModel.getStructureType().getDescription()); //Description

         cValues.put("StructureTypeID",         buildingInformationModel.getStructureTypeID());
         cValues.put("TCTNo",                   buildingInformationModel.getTCTNo());
         cValues.put("ValueOfRepair",           buildingInformationModel.getValueOfRepair());
         cValues.put("ZipCode",                 buildingInformationModel.getZipCode());
         cValues.put("brgyCode",                buildingInformationModel.getBrgyCode());
         cValues.put("citymunCode",             buildingInformationModel.getCitymunCode());

         cValues.put("gisBarangay",             buildingInformationModel.getGisBarangay().getBrgyDesc()); //
         cValues.put("gisCity",                 buildingInformationModel.getGisBarangay().getBrgyDesc()); //
         cValues.put("gisProvince",             buildingInformationModel.getGisProvince().getProvDesc()); //
         cValues.put("gisRegion",               buildingInformationModel.getGisRegion().getRegDesc()); //

         cValues.put("isForInspection",         buildingInformationModel.getIsForInspection());
         cValues.put("provCode",                buildingInformationModel.getProvCode());
         cValues.put("regCode",                 buildingInformationModel.getRegCode());

        return  cValues;
    }

    public static void saveBuildingInformation(Context context, BuildingInformationModel buildingInformationModel)
    {
        ContentValues cValues = setBuildingInformationValues(buildingInformationModel);

        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try
        {
            db.insert(SQLiteDbContext.TABLE_BUILDING_INFORMATION, null, cValues);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        db.close();
    }

    public static void updateBuildingInformation(Context context, BuildingInformationModel buildingInformationModel)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues cValues = setBuildingInformationValues(buildingInformationModel);

        try
        {
            db.update(SQLiteDbContext.TABLE_BUILDING_INFORMATION, cValues, "ScreenerID=? AND MissionOrderID=?",
                    new String[]{buildingInformationModel.getScreenerID(), String.valueOf(buildingInformationModel.getMissionOrderID())});
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

        return db.query(SQLiteDbContext.TABLE_BUILDING_INFORMATION, allColumns,
                null, null, null, null, null);
    }

    public static Cursor realAllData3(Context context, String ScreenerID, String MissionOrderID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(SQLiteDbContext.TABLE_BUILDING_INFORMATION, allColumns,
                "ScreenerID=? AND MissionOrderID=?",new String[]{ScreenerID, MissionOrderID}, null, null, null);
    }

    public static Cursor realAllData2(Context context, String ScreenerID, String MissionOrderID)
    {
        SQLiteDbContext helper = new SQLiteDbContext(context); SQLiteDatabase db = helper.getReadableDatabase();

        String query = "SELECT "                                        +
                "A.ID, "                                                +
                "A.ScreenerID, "                                        +
                "A.MissionOrderID, "                                    +
                "A.AccountCode, "                                       +
                "A.AccountCodeID, "                                     +
                "A.Age, "                                               +
                "A.Agency, "                                            +
                "A.AgencyID, "                                          +
                "A.Area_Purok_Sitio, "                                  +
                "A.AssessedValue, "                                     +
                "A.AssetID, "                                           +
                "A.AveAreaPerFloor, "                                   +
                "A.BIN, "                                               +
                "A.BIRZonalValue, "                                     +
                "A.BldgConditionID, "                                   +
                "A.BldgGroup, "                                         +
                "A.BldgGroupID, "                                       +
                "A.BldgOwnershipTypeID, "                               +
                "A.Bldg_Rm_Flr, "                                       +
                "A.BookValue, "                                         +
                "A.BuildingCode, "                                      +
                "A.BuildingConditionReport, "                           +
                "A.BuildingDisplayID, "                                 +
                "A.BuildingInfoID, "                                    +
                "A.BuildingInventoryYearID, "                           +
                "A.BuildingLandPin, "                                   +
                "A.BuildingName, "                                      +
                "A.BuildingNoOfPersons, "                               +
                "A.BuildingOwnershipType, "                             +
                "A.BuildingSoilType, "                                  +
                "A.ContactNo, "                                         +
                "A.Cost, "                                              +
                "A.CostPerSqm, "                                        +
                "A.DPWHOB_ID, "                                         +
                "A.DateFinished, "                                      +
                "A.DateSaved, "                                         +
                "A.DateUpdated, "                                       +
                "A.DistrictOffice, "                                    +
                "A.DistrictOfficeID, "                                  +
                "A.Email, "                                             +
                "A.FaultDistance, "                                     +
                "A.FilesUrl, "                                          +
                "A.FloorArea, "                                         +
                "A.GOB_ID, "                                            +
                "A.Height, "                                            +
                "A.ImageFile, "                                         +
                "A.ImageUrl, "                                          +
                "A.InventoryYear, "                                     +
                "A.Lat, "                                               +
                "A.LifeSpanOfBuilding, "                                +
                "A.Location, "                                          +
                "A.Long, "                                              +
                "A.Altitude, "                                          +
                "A.LotArea, "                                           +
                "A.LotOwnershipType, "                                  +
                "A.LotOwnershipTypeID, "                                +
                "A.NearestFault, "                                      +
                "A.NoOfFloors, "                                        +
                "A.NoOfPersonsID, "                                     +
                "A.NoOfUnits, "                                         +
                "A.Occupancies, "                                       +
                "A.OjectID, "                                           +
                "A.OpenSpace, "                                         +
                "A.OwnerName, "                                         +
                "A.OwnershipType, "                                     +
                "A.Position, "                                          +
                "A.Remarks, "                                           +
                "A.Seismicity, "                                        +
                "A.SoilTypeID, "                                        +
                "A.StructureType, "                                     +
                "A.StructureTypeID, "                                   +
                "A.TCTNo, "                                             +
                "A.ValueOfRepair, "                                     +
                "A.ZipCode, "                                           +
                "A.brgyCode, "                                          +
                "A.citymunCode, "                                       +
                "A.gisBarangay, "                                       +
                "A.gisCity, "                                           +
                "A.gisProvince, "                                       +
                "A.gisRegion, "                                         +
                "A.isForInspection, "                                   +
                "A.provCode, "                                          +
                "A.regCode, "                                           +
                "GROUP_CONCAT(DISTINCT B.Occupancy) AS Occupancy, "     +
                "C.DateReported, "                                      +
                "C.InspectionStatus, "                                  +
                "C.ReasonForScreening, "                                +
                "C.ApprovedBy "                                         +
                "FROM "                                                 +
                SQLiteDbContext.TABLE_BUILDING_INFORMATION + " AS A "   +
                "LEFT JOIN "                                            +
                SQLiteDbContext.TABLE_OCCUPANCY_LIST + " AS B "         +
                "ON A.MissionOrderID=B.MissionOrderID "                 +
                "LEFT JOIN "                                            +
                SQLiteDbContext.TABLE_MISSION_ORDERS + " AS C "         +
                "ON A.MissionOrderID=C.MissionOrderID "                 +
                "WHERE A.ScreenerID=? AND A.MissionOrderID=?";

        return db.rawQuery(query, new String[]{ScreenerID, MissionOrderID});

        //return db.query(SQLiteDbContext.TABLE_OCCUPANCY_LIST, allColumns, null, null, null, null, null);
    }

}

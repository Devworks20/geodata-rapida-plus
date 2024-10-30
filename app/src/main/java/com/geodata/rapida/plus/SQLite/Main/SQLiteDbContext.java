package com.geodata.rapida.plus.SQLite.Main;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDbContext extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "RapidA+.db";

    public static final int DATABASE_VERSION = 12;

    public static final String TABLE_USER_ACCOUNT               = "tblUserAccount";
    public static final String TABLE_IMAGES                     = "tblImages";
    public static final String TABLE_SKETCH_IMAGES              = "tblSketchImages";
    public static final String TABLE_BUILDING_SCORES            = "tblBuildingScores";
    public static final String TABLE_TEMP_BUILDING_SCORES       = "tblTempBuildingScores";
    public static final String TABLE_FINAL_BUILDING_SCORES      = "tblFinalBuildingScores";

    public static final String TABLE_OCCUPANCIES                = "tblOccupancies";
    public static final String TABLE_SELECTED_OCCUPANCIES       = "tblSelectedOccupancies";

    public static final String TABLE_NO_OF_PERSONS              = "tblNoOfPersons";
    public static final String TABLE_SOIL_TYPES                 = "tblSoilTypes";
    public static final String TABLE_FALLING_HAZARDS            = "tblFallingHazards";
    public static final String TABLE_SELECTED_FALLING_HAZARDS   = "tblSelectedFallingHazards";

    public static final String TABLE_MISSION_ORDERS             = "tblMissionOrders";
    public static final String TABLE_ASSIGNED_INSPECTORS        = "tblAssignedInspectors";
    public static final String TABLE_MO_FILE_ATTACHMENTS        = "tblMOFileAttachments";
    public static final String TABLE_INSPECTOR_SIGNATURE        = "tblInspectorSignature";
    public static final String TABLE_BUILDING_INFORMATION       = "tblBuildingInformation";
    public static final String TABLE_OCCUPANCY_LIST             = "tblOccupancyList";
    public static final String TABLE_RVS_SCORING                = "tblRVSScoring";

    public static final String TABLE_NEW_RVS                    = "tblNewRVS";
    public static final String TABLE_RESA                       = "tblRESA";
    public static final String TABLE_DESA                       = "tblDESA";

    public static final String TABLE_EARTHQUAKE_RVS_REPORT      = "tblEarthquakeRVSReport";

    public static final String TABLE_BUILDING_TYPES             = "tblBuildingTypes";


    public SQLiteDbContext(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public SQLiteDbContext(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE_USER_ACCOUNT);
        db.execSQL(CREATE_TABLE_IMAGES);
        db.execSQL(CREATE_TABLE_SKETCH_IMAGES);
        db.execSQL(CREATE_TABLE_BUILDING_SCORES);
        db.execSQL(CREATE_TEMP_TABLE_BUILDING_SCORES);
        db.execSQL(CREATE_TABLE_FINAL_BUILDING_SCORES);

        db.execSQL(CREATE_TABLE_OCCUPANCIES);
        db.execSQL(CREATE_TABLE_SELECTED_OCCUPANCIES);
        db.execSQL(CREATE_TABLE_NO_OF_PERSONS);
        db.execSQL(CREATE_TABLE_SOIL_TYPES);
        db.execSQL(CREATE_TABLE_FALLING_HAZARDS);
        db.execSQL(CREATE_TABLE_SELECTED_FALLING_HAZARDS);

        db.execSQL(CREATE_TABLE_MISSION_ORDERS);
        db.execSQL(CREATE_TABLE_ASSIGNED_INSPECTORS);
        db.execSQL(CREATE_TABLE_MO_FILE_ATTACHMENTS);
        db.execSQL(CREATE_TABLE_INSPECTOR_SIGNATURE);
        db.execSQL(CREATE_TABLE_BUILDING_INFORMATION);
        db.execSQL(CREATE_TABLE_OCCUPANCY_LIST);
        db.execSQL(CREATE_TABLE_RVS_SCORING);

        db.execSQL(CREATE_TABLE_NEW_RVS);
        db.execSQL(CREATE_TABLE_RESA);
        db.execSQL(CREATE_TABLE_DESA);
        db.execSQL(CREATE_TABLE_EARTHQUAKE_RVS_REPORT);
        db.execSQL(CREATE_TABLE_BUILDING_TYPES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_ACCOUNT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SKETCH_IMAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUILDING_SCORES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEMP_BUILDING_SCORES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FINAL_BUILDING_SCORES);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OCCUPANCIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SELECTED_OCCUPANCIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NO_OF_PERSONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SOIL_TYPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FALLING_HAZARDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SELECTED_FALLING_HAZARDS);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MISSION_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSIGNED_INSPECTORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSPECTOR_SIGNATURE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUILDING_INFORMATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OCCUPANCY_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RVS_SCORING);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEW_RVS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DESA);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EARTHQUAKE_RVS_REPORT);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUILDING_TYPES);

        onCreate(db);
    }


    private static final String CREATE_TABLE_USER_ACCOUNT =
            "CREATE TABLE IF NOT EXISTS " + TABLE_USER_ACCOUNT             +
                    "("                                      +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "EmployeeID VARCHAR, "                   +
                    "UserAccountID VARCHAR, "                +
                    "Username VARCHAR, "                     +
                    "Password VARCHAR, "                     +
                    "RoleName VARCHAR, "                     +
                    "CompleteName VARCHAR, "                 +
                    "AppID VARCHAR, "                        +
                    "Position VARCHAR, "                     +
                    "DtAdded VARCHAR, "                      +
                    "isActive VARCHAR "                      +
                    ")";

    private static final String CREATE_TABLE_IMAGES =
            "CREATE TABLE IF NOT EXISTS " + TABLE_IMAGES                   +
                    "("                                      +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "UserAccountID VARCHAR, "                +
                    "Category VARCHAR, "                     +
                    "ImageID VARCHAR, "                      +
                    "ImageType VARCHAR, "                    +
                    "ImageName VARCHAR, "                    +
                    "ImagePath VARCHAR, "                    +
                    "ImageExtension VARCHAR, "               +
                    "DtAdded VARCHAR, "                      +
                    "Description VARCHAR, "                  +
                    "isActive VARCHAR, "                     +
                    "isSync VARCHAR "                        +
                    ")";

    private static final String CREATE_TABLE_SKETCH_IMAGES =
            "CREATE TABLE IF NOT EXISTS " + TABLE_SKETCH_IMAGES            +
                    "("                                      +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "UserAccountID VARCHAR, "                +
                    "Category VARCHAR, "                     +
                    "SketchID VARCHAR, "                     +
                    "SketchName VARCHAR, "                   +
                    "SketchPath VARCHAR, "                   +
                    "SketchExtension VARCHAR, "              +
                    "DtAdded VARCHAR, "                      +
                    "isActive VARCHAR, "                     +
                    "isSync VARCHAR "                        +
                    ")";

    private static final String CREATE_TABLE_BUILDING_SCORES =
            "CREATE TABLE IF NOT EXISTS " + TABLE_BUILDING_SCORES          +
                    "("                                      +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Category VARCHAR, "                     +
                    "BuildingType VARCHAR, "                 +
                    "Modifiers VARCHAR, "                    +
                    "Scores VARCHAR, "                       +
                    "isActive VARCHAR "                      +
                    ")";

    private static final String CREATE_TEMP_TABLE_BUILDING_SCORES =
            "CREATE TABLE IF NOT EXISTS " + TABLE_TEMP_BUILDING_SCORES     +
                    "("                                      +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "UserAccountID VARCHAR, "                +
                    "MissionOrderID VARCHAR, "               +
                    "Category VARCHAR, "                     +
                    "BuildingID VARCHAR, "                   +
                    "BuildingType VARCHAR, "                 +
                    "Modifiers VARCHAR, "                    +
                    "Scores VARCHAR, "                       +
                    "isActive VARCHAR "                      +
                    ")";

    private static final String CREATE_TABLE_FINAL_BUILDING_SCORES =
            "CREATE TABLE IF NOT EXISTS " + TABLE_FINAL_BUILDING_SCORES    +
                    "("                                      +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "UserAccountID VARCHAR, "                +
                    "MissionOrderID VARCHAR, "               +
                    "Category VARCHAR, "                     +
                    "BuildingScoreNo VARCHAR, "              +
                    "BuildingType VARCHAR, "                 +
                    "FinalScore VARCHAR, "                   +
                    "isActive VARCHAR  "                     +
                    ")";

    private static final String CREATE_TABLE_OCCUPANCIES =
            "CREATE TABLE IF NOT EXISTS " + TABLE_OCCUPANCIES              +
                    "("                                      +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "UseOfCharacterOccupancyID VARCHAR, "    +
                    "Description VARCHAR  "                  +
                    ")";

    private static final String CREATE_TABLE_SELECTED_OCCUPANCIES =
            "CREATE TABLE IF NOT EXISTS " + TABLE_SELECTED_OCCUPANCIES     +
                    "("                                      +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "UserAccountID VARCHAR, "                +
                    "MissionOrderID VARCHAR, "               +
                    "Category VARCHAR, "                     +
                    "UseOfCharacterOccupancyID VARCHAR  "    +
                    ")";


    private static final String CREATE_TABLE_NO_OF_PERSONS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NO_OF_PERSONS            +
                    "("                                      +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "NoOfPersonsID VARCHAR, "                +
                    "NoOfPersons VARCHAR  "                  +
                    ")";

    private static final String CREATE_TABLE_SOIL_TYPES =
            "CREATE TABLE IF NOT EXISTS " + TABLE_SOIL_TYPES                +
                    "("                                      +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "BuildingSoilTypeID VARCHAR, "           +
                    "BuildingSoilTypeCode VARCHAR, "         +
                    "BuildingSoilTypeDesc VARCHAR  "         +
                    ")";

    private static final String CREATE_TABLE_FALLING_HAZARDS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_FALLING_HAZARDS          +
                    "("                                      +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "FallingHazardID VARCHAR, "              +
                    "FallingHazardDesc VARCHAR  "            +
                    ")";

    private static final String CREATE_TABLE_SELECTED_FALLING_HAZARDS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_SELECTED_FALLING_HAZARDS +
                    "("                                      +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "UserAccountID VARCHAR, "                +
                    "MissionOrderID VARCHAR, "               +
                    "Category VARCHAR, "                     +
                    "FallingHazardDesc VARCHAR,  "           +
                    "OthersField VARCHAR  "                  +
                    ")";

    private static final String CREATE_TABLE_MISSION_ORDERS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_MISSION_ORDERS           +
                    "("                                      +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "ScreenerID VARCHAR, "                   +
                    "MissionOrderType VARCHAR, "             +
                    "ApprovedBy VARCHAR, "                   +
                    "ApprovedByID VARCHAR, "                 +
                    "AssetID VARCHAR, "                      +
                    "DateIssued VARCHAR,  "                  +
                    "DateReported VARCHAR,  "                +
                    "EndorsedForApproval VARCHAR,  "         +
                    "EndorsedForApprovalID VARCHAR,  "       +
                    "InspectionStatus VARCHAR,  "            +
                    "InventoryYear VARCHAR,  "               +
                    "MissionOrderID VARCHAR, "               +
                    "MissionOrderNo VARCHAR, "               +
                    "ReasonForScreening VARCHAR, "           +
                    "Remarks VARCHAR, "                      +
                    "ScreeningSchedule VARCHAR,  "           +
                    "ScreeningType VARCHAR,  "               +
                    "SignaturePath VARCHAR, "                +
                    "ReportPath VARCHAR, "                   +
                    "isActive VARCHAR, "                     +
                    "dtAdded VARCHAR "                       +
                    ")";


    private static final String CREATE_TABLE_ASSIGNED_INSPECTORS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_ASSIGNED_INSPECTORS      +
                    "("                                      +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "ScreenerID VARCHAR, "                   +
                    "MissionOrderID VARCHAR, "               +
                    "Inspector VARCHAR, "                    +
                    "Position VARCHAR, "                     +
                    "isTL VARCHAR "                          +
                    ")";

    private static final String CREATE_TABLE_MO_FILE_ATTACHMENTS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_MO_FILE_ATTACHMENTS      +
                    "("                                      +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "ScreenerID VARCHAR, "                   +
                    "MissionOrderID VARCHAR, "               +
                    "MOAttachmentFilePath VARCHAR, "         +
                    "FileName VARCHAR, "                     +
                    "isPreviousReport BOOLEAN "              +
                    ")";

    private static final String CREATE_TABLE_INSPECTOR_SIGNATURE  =
            "CREATE TABLE IF NOT EXISTS " + TABLE_INSPECTOR_SIGNATURE      +
                    "("                                      +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "UserAccountID VARCHAR, "                +
                    "MissionOrderID VARCHAR, "               +
                    "SignatureID VARCHAR, "                  +
                    "SignatureName VARCHAR, "                +
                    "SignaturePath VARCHAR, "                +
                    "SignatureExtension VARCHAR, "           +
                    "DtAdded VARCHAR, "                      +
                    "isActive VARCHAR, "                     +
                    "isSync VARCHAR "                        +
                    ")";

    private static final String CREATE_TABLE_BUILDING_INFORMATION =
            "CREATE TABLE IF NOT EXISTS " + TABLE_BUILDING_INFORMATION     +
                    "("                                      +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "ScreenerID VARCHAR, "              +
                    "MissionOrderID VARCHAR, "          +
                    "AccountCode VARCHAR, "             +
                    "AccountCodeID VARCHAR, "           +
                    "Age VARCHAR, "                     +
                    "Agency VARCHAR, "                  +
                    "AgencyID VARCHAR, "                +
                    "Area_Purok_Sitio VARCHAR, "       +
                    "AssessedValue VARCHAR, "           +
                    "AssetID VARCHAR, "                 +
                    "AveAreaPerFloor  VARCHAR, "        +
                    "BIN VARCHAR, "                     +
                    "BIRZonalValue VARCHAR, "           +
                    "BldgConditionID VARCHAR,"          +
                    "BldgGroup VARCHAR,"                +
                    "BldgGroupID VARCHAR,"              +
                    "BldgOwnershipTypeID VARCHAR,"      +
                    "Bldg_Rm_Flr VARCHAR,"              +
                    "BookValue VARCHAR,"                +
                    "BuildingCode VARCHAR,"             +
                    "BuildingConditionReport VARCHAR,"  +
                    "BuildingDisplayID VARCHAR, "       +
                    "BuildingInfoID VARCHAR, "          +
                    "BuildingInventoryYearID VARCHAR, " +
                    "BuildingLandPin VARCHAR, "         +
                    "BuildingName VARCHAR, "            +
                    "BuildingNoOfPersons VARCHAR, "     +
                    "BuildingOwnershipType VARCHAR, "   +
                    "BuildingSoilType VARCHAR, "        +
                    "ContactNo VARCHAR, "               +
                    "Cost VARCHAR,"                      +
                    "CostPerSqm VARCHAR, "              +
                    "DPWHOB_ID VARCHAR, "               +
                    "DateFinished VARCHAR, "            +
                    "DateSaved VARCHAR, "               +
                    "DateUpdated VARCHAR, "             +
                    "DistrictOffice VARCHAR, "          +
                    "DistrictOfficeID VARCHAR, "        +
                    "Email VARCHAR, "                   +
                    "FaultDistance VARCHAR, "           +
                    "FilesUrl VARCHAR, "                +
                    "FloorArea VARCHAR, "               +
                    "GOB_ID VARCHAR, "                  +
                    "Height VARCHAR, "                  +
                    "ImageFile VARCHAR, "               +
                    "ImageUrl VARCHAR, "                +
                    "InventoryYear VARCHAR, "           +
                    "Lat VARCHAR, "                     +
                    "LifeSpanOfBuilding VARCHAR, "      +
                    "Location VARCHAR, "                +
                    "Long VARCHAR, "                    +
                    "Altitude VARCHAR, "                +
                    "LotArea VARCHAR, "                 +
                    "LotOwnershipType VARCHAR, "        +
                    "LotOwnershipTypeID VARCHAR, "      +
                    "NearestFault VARCHAR, "            +
                    "NoOfFloors VARCHAR, "              +
                    "NoOfPersonsID VARCHAR, "           +
                    "NoOfUnits VARCHAR, "               +
                    "Occupancies VARCHAR, "             +
                    "OjectID VARCHAR, "                 +
                    "OpenSpace VARCHAR, "               +
                    "OwnerName VARCHAR, "               +
                    "OwnershipType VARCHAR, "           +
                    "Position VARCHAR, "                +
                    "Remarks VARCHAR, "                 +
                    "Seismicity VARCHAR, "              +
                    "SoilTypeID VARCHAR, "              +
                    "StructureType VARCHAR, "           +
                    "StructureTypeID VARCHAR, "          +
                    "TCTNo VARCHAR, "                   +
                    "ValueOfRepair VARCHAR, "           +
                    "ZipCode VARCHAR, "                 +
                    "brgyCode VARCHAR, "                +
                    "citymunCode VARCHAR, "             +
                    "gisBarangay VARCHAR, "             +
                    "gisCity VARCHAR, "                 +
                    "gisProvince VARCHAR, "             +
                    "gisRegion VARCHAR, "               +
                    "isForInspection VARCHAR, "         +
                    "provCode VARCHAR, "                +
                    "regCode VARCHAR "                  +
                    ")";

    private static final String CREATE_TABLE_OCCUPANCY_LIST =
            "CREATE TABLE IF NOT EXISTS " + TABLE_OCCUPANCY_LIST           +
                    "("                                      +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "ScreenerID VARCHAR, "                   +
                    "MissionOrderID VARCHAR, "               +
                    "Occupancy VARCHAR  "                    +
                    ")";


    private static final String CREATE_TABLE_RVS_SCORING =
            "CREATE TABLE IF NOT EXISTS " + TABLE_RVS_SCORING              +
                    "("                                      +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "ScreenerID VARCHAR, "                   +
                    "MissionOrderID VARCHAR, "               +
                    "Category VARCHAR, "                     +
                    "NoOfPersons VARCHAR, "                  +
                    "SoilType VARCHAR, "                     +
                    "Comments VARCHAR, "                     +
                    "DetailedEvaluation VARCHAR, "           +
                    "BackgroundInformation VARCHAR, "        +
                    "FindingsObservations VARCHAR, "         +
                    "CommentsRecommendations VARCHAR,  "     +
                    "AdminName VARCHAR, "                    +
                    "AdminPosition VARCHAR "                 +
                    ")";


    private static final String CREATE_TABLE_NEW_RVS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NEW_RVS                  +
                    "("                                      +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "ScreenerID VARCHAR, "                   +
                    "AssetInfoBuildingID VARCHAR, "          +
                    "AssetID VARCHAR, "                      +
                    "ASSET_ID VARCHAR, "                     +
                    "AssetCatID VARCHAR, "                   +
                    "BuildingCode VARCHAR, "                 +
                    "AssetCode VARCHAR, "                    +
                    "BuildingLandPin VARCHAR, "              +
                    "TCTNo VARCHAR, "                        +
                    "Cost VARCHAR, "                         +
                    "NavAssetCatID VARCHAR, "                +
                    "BuildingClassification VARCHAR, "       +
                    "BuildingPhoto VARCHAR, "                +
                    "InventoryYear VARCHAR, "                +
                    "AccountCode VARCHAR, "                  +
                    "BuildingName VARCHAR, "                 +
                    "BIN VARCHAR, "                          +
                    "Agency VARCHAR, "                       +
                    "DateBuilt VARCHAR, "                    +
                    "BuildingAge VARCHAR, "                  +
                    "BookValue VARCHAR, "                    +
                    "AssessedValue VARCHAR, "                +
                    "Groups VARCHAR, "                       +
                    "BldgOwnershipType VARCHAR, "            +
                    "SoilType VARCHAR, "                     +
                    "NoOFPersons VARCHAR, "                  +
                    "StructuralTypeDesc VARCHAR, "           +
                    "StructuralTypeCode VARCHAR, "           +
                    "FloorArea  VARCHAR, "                   +
                    "Height VARCHAR, "                       +
                    "NoOfStories VARCHAR, "                  +
                    "OpenSpace VARCHAR, "                    +
                    "AveAreaPerFloor VARCHAR, "              +
                    "CostPerSqm VARCHAR, "                   +
                    "NoOfUnits VARCHAR, "                    +
                    "OwnerName VARCHAR, "                    +
                    "Position VARCHAR, "                     +
                    "ContactNo VARCHAR, "                    +
                    "Email VARCHAR, "                        +
                    "BuildingPhotoPath VARCHAR, "            +
                    "SeismicityRegion VARCHAR, "             +
                    "LotOwnType VARCHAR, "                   +
                    "LotArea VARCHAR, "                      +
                    "BIRZonalValue VARCHAR, "                +
                    "BuildingCondition VARCHAR, "            +
                    "ValueOfRepair VARCHAR, "                +
                    "Remarks VARCHAR, "                      +
                    "Region VARCHAR, "                       +
                    "Province VARCHAR, "                     +
                    "City VARCHAR, "                         +
                    "District VARCHAR, "                     +
                    "DistrictOffice VARCHAR, "               +
                    "Barangay VARCHAR, "                     +
                    "BlockNo VARCHAR, "                      +
                    "Street VARCHAR, "                       +
                    "SubdCompVill VARCHAR, "                 +
                    "Purok VARCHAR, "                        +
                    "Sitio VARCHAR, "                        +
                    "Zip VARCHAR, "                          +
                    "Latitude VARCHAR, "                     +
                    "Longitude VARCHAR, "                    +
                    "InspectedBy VARCHAR, "                  +
                    "ConstructionStatus VARCHAR, "           +
                    "VisitNo VARCHAR, "                      +
                    "StoreyNo VARCHAR, "                     +
                    "Residential VARCHAR, "                  +
                    "BldgPermitNo VARCHAR, "                 +
                    "CompanyName VARCHAR, "                  +
                    "PresidentName VARCHAR, "                +
                    "isWoodFrame VARCHAR, "                  +
                    "isSteelFrame VARCHAR, "                 +
                    "isConcreteFrame VARCHAR, "              +
                    "BasicScoreWF VARCHAR, "                 +
                    "BasicScoreCF VARCHAR, "                 +
                    "BasicScoreSF VARCHAR, "                 +
                    "VerticalIrregularityWF VARCHAR, "       +
                    "VerticalIrregularitySF VARCHAR, "       +
                    "VerticalIrregularityCF VARCHAR, "       +
                    "PlanIrregularityWF VARCHAR, "           +
                    "PlanIrregularitySF VARCHAR, "           +
                    "PlanIrregularityCF VARCHAR, "           +
                    "PreCodeWF VARCHAR, "                    +
                    "PreCodeSF VARCHAR, "                    +
                    "PreCodeCF VARCHAR, "                    +
                    "PostBenchmarkWF VARCHAR, "              +
                    "PostBenchmarkSF VARCHAR, "              +
                    "PostBenchmarkCF VARCHAR, "              +
                    "SoilTypeC2WF VARCHAR, "                 +
                    "SoilTypeC2SF VARCHAR, "                 +
                    "SoilTypeC2CF VARCHAR, "                 +
                    "SoilTypeD3WF VARCHAR, "                 +
                    "SoilTypeD3SF VARCHAR, "                 +
                    "SoilTypeD3CF VARCHAR, "                 +
                    "SoilTypeE4WF VARCHAR, "                 +
                    "SoilTypeE4SF VARCHAR, "                 +
                    "SoilTypeE4CF VARCHAR, "                 +
                    "isPreCode VARCHAR, "                    +
                    "isPostBenchmark VARCHAR, "              +
                    "isSoilTypeC2 VARCHAR, "                 +
                    "isSoilTypeD3 VARCHAR, "                 +
                    "isSoilTypeE4 VARCHAR, "                 +
                    "WoodFrameFinalScore VARCHAR, "          +
                    "SteelFrameFinalScore VARCHAR, "         +
                    "ConcreteFrameFinalScore VARCHAR, "      +
                    "StructuralComponents VARCHAR, "         +
                    "NonStructuralComponents VARCHAR, "      +
                    "AncillaryAuxiliaryComponents VARCHAR, " +
                    "BuildingPhotoBase64 VARCHAR, "          +
                    "dtAdded VARCHAR, "                      +
                    "isSynced VARCHAR "                      +
                    ")";



    private static final String CREATE_TABLE_RESA =
            "CREATE TABLE IF NOT EXISTS " + TABLE_RESA                       +
                    "("                                        +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, "   +
                    "ScreenerID VARCHAR, "                     +
                    "MissionOrderID VARCHAR, "                 +
                    "Affiliation VARCHAR, "                    +
                    "SetDate VARCHAR, "                        +
                    "SetTime VARCHAR, "                        +
                    "BuildingName VARCHAR, "                   +
                    "BuildingAddress VARCHAR, "                +
                    "BuildingContact VARCHAR, "                +
                    "NoOfStoreyAboveGround VARCHAR, "          +
                    "NoOfStoreyBelowGround VARCHAR, "          +
                    "TypeOfConstruction VARCHAR, "             +
                    "PrimaryOccupancy VARCHAR, "               +
                    "ApproxFootPrintAreaSM VARCHAR, "          +
                    "NoOfResidentialUnits VARCHAR, "           +
                    "NoOfCommercialUnits VARCHAR, "            +
                    "BuildingOffFoundationType VARCHAR, "      +
                    "StoryLeaningType VARCHAR, "               +
                    "OtherStructuralDamageType VARCHAR, "      +
                    "OtherFallingHazardType VARCHAR, "         +
                    "CrackingType VARCHAR, "                   +
                    "OtherOptionalType VARCHAR, "              +
                    "Comments VARCHAR, "                       +
                    "EstimatedBuildingDamage VARCHAR, "        +
                    "PreviousPostingEstimatedDamage VARCHAR, " +
                    "PreviousPostingDate VARCHAR, "            +
                    "ColorPlacard VARCHAR, "                   +
                    "FurtherActionsBarricades VARCHAR, "       +
                    "FurtherActionsDetailedEvaluationRecommended VARCHAR, " +
                    "FurtherActionsOtherRecommended VARCHAR, " +
                    "FurtherActionsType VARCHAR, "             +
                    "FurtherActionComments VARCHAR, "          +
                    "FurtherActionEstimatedDamage VARCHAR, "   +
                    "FurtherActionRecommendations VARCHAR, "   +
                    "SecondComments VARCHAR,  "                +
                    "InspectedBy VARCHAR "                     +
                    ")";

    private static final String CREATE_TABLE_DESA =
            "CREATE TABLE IF NOT EXISTS " + TABLE_DESA                       +
                    "("                                        +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, "   +
                    "ScreenerID VARCHAR, "                     +
                    "MissionOrderID VARCHAR, "                 +
                    "Affiliation VARCHAR, "                    +
                    "SetDate VARCHAR, "                        +
                    "SetTime VARCHAR, "                        +
                    "BuildingName VARCHAR, "                   +
                    "BuildingAddress VARCHAR, "                +
                    "BuildingContact VARCHAR, "                +
                    "NoOfStoreyAboveGround VARCHAR, "          +
                    "NoOfStoreyBelowGround VARCHAR, "          +
                    "TypeOfConstruction VARCHAR, "             +
                    "PrimaryOccupancy VARCHAR, "               +
                    "ApproxFootPrintAreaSM VARCHAR, "          +
                    "NoOfResidentialUnits VARCHAR, "           +
                    "NoOfCommercialUnits VARCHAR, "            +
                    "CollapseType, "                           +
                    "CollapseComment, "                        +
                    "BuildingStoryLeaningType,  "              +
                    "BuildingStoryLeaningComment, "            +
                    "OverAllHazardsOtherType, "                +
                    "FoundationType, "                         +
                    "FoundationComment, "                      +
                    "RoofFloorVLType, "                        +
                    "RoofFloorVLComment, "                     +
                    "CPCType, "                                +
                    "CPCComment, "                             +
                    "DiaphragmsHBType, "                       +
                    "DiaphragmsHBComment, "                    +
                    "WallsVBType, "                            +
                    "WallsVBComment, "                         +
                    "PrecastConnectionsType, "                 +
                    "PrecastConnectionsComment, "              +
                    "ParapetsOrnamentationType, "             +
                    "ParapetsOrnamentationComment, "          +
                    "CladdingGlazingType, "                    +
                    "CladdingGlazingComment, "                 +
                    "CeilingLightFixturesType,  "              +
                    "CeilingLightFixturesComment, "            +
                    "InteriorWallsPartitionsType, "            +
                    "InteriorWallsPartitionsComment, "         +
                    "ElevatorsType, "                          +
                    "ElevatorsComment, "                       +
                    "StairsExitType, "                         +
                    "StairsExitComment, "                      +
                    "ElectricGasType, "                        +
                    "ElectricGasComment, "                     +
                    "NonstructuralHazardOtherType, "           +
                    "SlopeFailureDebrisType, "                 +
                    "SlopeFailureDebrisComment, "              +
                    "GroundMovementFissuresType, "             +
                    "GroundMovementFissuresComment, "          +
                    "GeotechnicalHazardOther, "                +
                    "EstimatedBuildingDamage VARCHAR, "        +
                    "PreviousPostingEstimatedDamage VARCHAR, " +
                    "PreviousPostingDate VARCHAR, "            +
                    "ColorPlacard VARCHAR, "                   +
                    "FurtherActionsBarricades VARCHAR, "                       +
                    "FurtherActionsEngineeringEvaluationRecommended VARCHAR, " +
                    "FurtherActionsOtherRecommendation VARCHAR, "              +
                    "BarricadesComment VARCHAR, "                              +
                    "EngineeringEvaluationRecommendedType VARCHAR, " +
                    "RecommendationsType VARCHAR, "            +
                    "Comments VARCHAR,  "                      +
                    "InspectedBy VARCHAR "                     +
                    ")";

    private static final String CREATE_TABLE_EARTHQUAKE_RVS_REPORT =
            "CREATE TABLE IF NOT EXISTS " + TABLE_EARTHQUAKE_RVS_REPORT    +
                    "("                                      +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "UserAccountID VARCHAR, "                +
                    "EarthquakeRVSReportID VARCHAR, "        +
                    "BuildingName VARCHAR, "                 +
                    "ConcreteFinalScore VARCHAR, "           +
                    "EarthquakeRVSReportPdfPath VARCHAR, "   +
                    "FaultDistance VARCHAR, "                +
                    "FinalScore VARCHAR, "                   +
                    "NearestFault VARCHAR, "                 +
                    "ScreeningDate VARCHAR, "                +
                    "Seismicity VARCHAR,  "                  +
                    "SteelFinalScore VARCHAR  "              +
                    ")";

    private static final String CREATE_TABLE_BUILDING_TYPES =
            "CREATE TABLE IF NOT EXISTS " + TABLE_BUILDING_TYPES           +
                    "("                                      +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "StructureTypeID VARCHAR, "              +
                    "Code VARCHAR, "                         +
                    "BuildingTypeName VARCHAR, "             +
                    "PreCode VARCHAR,  "                     +
                    "PostBenchmark VARCHAR  "                +
                    ")";
}

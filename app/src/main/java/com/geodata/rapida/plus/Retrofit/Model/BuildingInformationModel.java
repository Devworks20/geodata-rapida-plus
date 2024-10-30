package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BuildingInformationModel
{
    private String ScreenerID;
    private String MissionOrderID;

    @SerializedName("AccountCode")
    @Expose
    private AccountCode AccountCode;

    @SerializedName("AccountCodeID")
    @Expose
    private String AccountCodeID;

    @SerializedName("Age")
    @Expose
    private String Age;

    @SerializedName("Agency")
    @Expose
    private Agency Agency;

    @SerializedName("AgencyID")
    @Expose
    private String AgencyID;

    @SerializedName("Area_Purok_Sitio")
    @Expose
    private String Area_Purok_Sitio;

    @SerializedName("AssessedValue")
    @Expose
    private String AssessedValue;

    @SerializedName("AssetID")
    @Expose
    private String AssetID;

    @SerializedName("AveAreaPerFloor")
    @Expose
    private String AveAreaPerFloor;

    @SerializedName("BIN")
    @Expose
    private String BIN;

    @SerializedName("BIRZonalValue")
    @Expose
    private String BIRZonalValue;

    @SerializedName("BldgConditionID")
    @Expose
    private String BldgConditionID ;

    @SerializedName("BldgGroup")
    @Expose
    private BldgGroup BldgGroup;

    @SerializedName("BldgGroupID")
    @Expose
    private String BldgGroupID;

    @SerializedName("BldgOwnershipTypeID")
    @Expose
    private String BldgOwnershipTypeID;

    @SerializedName("Bldg_Rm_Flr")
    @Expose
    private String Bldg_Rm_Flr;

    @SerializedName("BookValue")
    @Expose
    private String BookValue;

    @SerializedName("BuildingCode")
    @Expose
    private String BuildingCode;

    @SerializedName("BuildingConditionReport")
    @Expose
    private BuildingConditionReport BuildingConditionReport;

    @SerializedName("BuildingDisplayID")
    @Expose
    private String BuildingDisplayID;

    @SerializedName("BuildingInfoID")
    @Expose
    private String BuildingInfoID;

    @SerializedName("BuildingInventoryYearID")
    @Expose
    private String BuildingInventoryYearID;

    @SerializedName("BuildingLandPin")
    @Expose
    private String BuildingLandPin;

    @SerializedName("BuildingName")
    @Expose
    private String BuildingName;

    @SerializedName("BuildingNoOfPersons")
    @Expose
    private BuildingNoOfPersons BuildingNoOfPersons;

    @SerializedName("BuildingOwnershipType")
    @Expose
    private BuildingOwnershipType BuildingOwnershipType;

    @SerializedName("BuildingSoilType")
    @Expose
    private BuildingSoilType BuildingSoilType;

    @SerializedName("ContactNo")
    @Expose
    private String ContactNo;

    @SerializedName("Cost")
    @Expose
    private String Cost;

    @SerializedName("CostPerSqm")
    @Expose
    private String CostPerSqm;

    @SerializedName("DPWHOB_ID")
    @Expose
    private String DPWHOB_ID;

    @SerializedName("DateFinished")
    @Expose
    private String DateFinished;

    @SerializedName("DateSaved")
    @Expose
    private String DateSaved;

    @SerializedName("DateUpdated")
    @Expose
    private String DateUpdated;

    @SerializedName("DistrictOffice")
    @Expose
    private DistrictOffice DistrictOffice;

    @SerializedName("DistrictOfficeID")
    @Expose
    private String DistrictOfficeID;

    @SerializedName("Email")
    @Expose
    private String Email;

    @SerializedName("FaultDistance")
    @Expose
    private String FaultDistance;

    @SerializedName("FilesUrl")
    @Expose
    private String FilesUrl;

    @SerializedName("FloorArea")
    @Expose
    private String FloorArea;

    @SerializedName("GOB_ID")
    @Expose
    private String GOB_ID;

    @SerializedName("Height")
    @Expose
    private String Height;

    @SerializedName("ImageFile")
    @Expose
    private String ImageFile;

    @SerializedName("ImageUrl")
    @Expose
    private String ImageUrl;

    @SerializedName("InventoryYear")
    @Expose
    private String InventoryYear ;

    @SerializedName("Lat")
    @Expose
    private String Lat;

    @SerializedName("LifeSpanOfBuilding")
    @Expose
    private String LifeSpanOfBuilding;

    @SerializedName("Location")
    @Expose
    private String Location;

    @SerializedName("Long")
    @Expose
    private String Long;

    @SerializedName("Altitude")
    @Expose
    private String Altitude;

    @SerializedName("LotArea")
    @Expose
    private String LotArea;

    @SerializedName("LotOwnershipType")
    @Expose
    private LotOwnershipType LotOwnershipType;

    @SerializedName("LotOwnershipTypeID")
    @Expose
    private String LotOwnershipTypeID;

    @SerializedName("NearestFault")
    @Expose
    private String NearestFault;

    @SerializedName("NoOfFloors")
    @Expose
    private String NoOfFloors;

    @SerializedName("NoOfPersonsID")
    @Expose
    private String NoOfPersonsID;

    @SerializedName("NoOfUnits")
    @Expose
    private String NoOfUnits;

    @SerializedName("Occupancies")
    @Expose
    private String Occupancies;

    @SerializedName("OjectID")
    @Expose
    private String OjectID;

    @SerializedName("OpenSpace")
    @Expose
    private String OpenSpace;

    @SerializedName("OwnerName")
    @Expose
    private String OwnerName;

    @SerializedName("OwnershipType")
    @Expose
    private String OwnershipType;

    @SerializedName("Position")
    @Expose
    private String Position;

    @SerializedName("Remarks")
    @Expose
    private String Remarks;

    @SerializedName("Seismicity")
    @Expose
    private String Seismicity;

    @SerializedName("SoilTypeID")
    @Expose
    private String SoilTypeID;

    @SerializedName("StructureType")
    @Expose
    private StructureType StructureType;

    @SerializedName("StructureTypeID")
    @Expose
    private String StructureTypeID;

    @SerializedName("TCTNo")
    @Expose
    private String TCTNo;

    @SerializedName("ValueOfRepair")
    @Expose
    private String ValueOfRepair;

    @SerializedName("ZipCode")
    @Expose
    private String ZipCode;

    @SerializedName("brgyCode")
    @Expose
    private String brgyCode;

    @SerializedName("citymunCode")
    @Expose
    private String citymunCode;

    @SerializedName("gisBarangay")
    @Expose
    private gisBarangay gisBarangay;

    @SerializedName("gisCity")
    @Expose
    private gisCity gisCity;

    @SerializedName("gisProvince")
    @Expose
    private gisProvince gisProvince;

    @SerializedName("gisRegion")
    @Expose
    private gisRegion gisRegion;

    @SerializedName("isForInspection")
    @Expose
    private String isForInspection;

    @SerializedName("provCode")
    @Expose
    private String provCode;

    @SerializedName("regCode")
    @Expose
    private String regCode;


    public BuildingInformationModel()
    {
        ScreenerID              = "";
        MissionOrderID          = "";
        AccountCode             = null;
        AccountCodeID           = "";
        Age                     = "";
        Agency                  = null;
        AgencyID                = "";
        Area_Purok_Sitio        = "";
        AssessedValue           = "";
        AssetID                 = "";
        AveAreaPerFloor         = "";
        BIN                     = "";
        BIRZonalValue           = "";
        BldgConditionID         = "";
        BldgGroup               = null;
        BldgGroupID             = "";
        BldgOwnershipTypeID     = "";
        Bldg_Rm_Flr             = "";
        BookValue               = "";
        BuildingCode            = "";
        BuildingConditionReport = null;
        BuildingDisplayID       = "";
        BuildingInfoID          = "";
        BuildingInventoryYearID = "";
        BuildingLandPin         = "";
        BuildingName            = "";
        BuildingNoOfPersons     = null;
        BuildingOwnershipType   = null;
        BuildingSoilType        = null;
        ContactNo               = "";
        Cost                    = "";
        CostPerSqm              = "";
        DPWHOB_ID               = "";
        DateFinished            = "";
        DateSaved               = "";
        DateUpdated             = "";
        DistrictOffice          = null;
        DistrictOfficeID        = "";
        Email                   = "";
        FaultDistance           = "";
        FilesUrl                = "";
        FloorArea               = "";
        GOB_ID                  = "";
        Height                  = "";
        ImageFile               = "";
        ImageUrl                = "";
        InventoryYear           = "";
        Lat                     = "";
        LifeSpanOfBuilding      = "";
        Location                = "";
        Long                    = "";
        Altitude                = "";
        LotArea                 = "";
        LotOwnershipType        = null;
        LotOwnershipTypeID      = "";
        NearestFault            = "";
        NoOfFloors              = "";
        NoOfPersonsID           = "";
        NoOfUnits               = "";
        Occupancies             = "";
        OjectID                 = "";
        OpenSpace               = "";
        OwnerName               = "";
        OwnershipType           = "";
        Position                = "";
        Remarks                 = "";
        Seismicity              = "";
        SoilTypeID              = "";
        StructureType           = null;
        StructureTypeID         = "";
        TCTNo                   = "";
        ValueOfRepair           = "";
        ZipCode                 = "";
        brgyCode                = "";
        citymunCode             = "";
        gisBarangay             = null;
        gisCity                 = null;
        gisProvince             = null;
        gisRegion               = null;
        isForInspection         = "";
        provCode                = "";
        regCode                 = "";
    }




    public String getScreenerID() {
        return ScreenerID;
    }

    public void setScreenerID(String screenerID) {
        ScreenerID = screenerID;
    }

    public String getMissionOrderID() {
        return MissionOrderID;
    }

    public void setMissionOrderID(String missionOrderID) {
        MissionOrderID = missionOrderID;
    }

    public com.geodata.rapida.plus.Retrofit.Model.AccountCode getAccountCode() {
        return AccountCode;
    }

    public void setAccountCode(AccountCode accountCode) {
        AccountCode = accountCode;
    }

    public String getAccountCodeID() {
        return AccountCodeID;
    }

    public void setAccountCodeID(String accountCodeID) {
        AccountCodeID = accountCodeID;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public com.geodata.rapida.plus.Retrofit.Model.Agency getAgency() {
        return Agency;
    }

    public void setAgency(Agency agency) {
        Agency = agency;
    }

    public String getAgencyID() {
        return AgencyID;
    }

    public void setAgencyID(String agencyID) {
        AgencyID = agencyID;
    }

    public String getArea_Purok_Sitio() {
        return Area_Purok_Sitio;
    }

    public void setArea_Purok_Sitio(String area_Purok_Sitio) {
        Area_Purok_Sitio = area_Purok_Sitio;
    }

    public String getAssessedValue() {
        return AssessedValue;
    }

    public void setAssessedValue(String assessedValue) {
        AssessedValue = assessedValue;
    }

    public String getAssetID() {
        return AssetID;
    }

    public void setAssetID(String assetID) {
        AssetID = assetID;
    }

    public String getAveAreaPerFloor() {
        return AveAreaPerFloor;
    }

    public void setAveAreaPerFloor(String aveAreaPerFloor) {
        AveAreaPerFloor = aveAreaPerFloor;
    }

    public String getBIN() {
        return BIN;
    }

    public void setBIN(String BIN) {
        this.BIN = BIN;
    }

    public String getBIRZonalValue() {
        return BIRZonalValue;
    }

    public void setBIRZonalValue(String BIRZonalValue) {
        this.BIRZonalValue = BIRZonalValue;
    }

    public String getBldgConditionID() {
        return BldgConditionID;
    }

    public void setBldgConditionID(String bldgConditionID) {
        BldgConditionID = bldgConditionID;
    }

    public BldgGroup getBldgGroup() {
        return BldgGroup;
    }

    public void setBldgGroup(BldgGroup bldgGroup) {
        BldgGroup = bldgGroup;
    }

    public String getBldgGroupID() {
        return BldgGroupID;
    }

    public void setBldgGroupID(String bldgGroupID) {
        BldgGroupID = bldgGroupID;
    }

    public String getBldgOwnershipTypeID() {
        return BldgOwnershipTypeID;
    }

    public void setBldgOwnershipTypeID(String bldgOwnershipTypeID) {
        BldgOwnershipTypeID = bldgOwnershipTypeID;
    }

    public String getBldg_Rm_Flr() {
        return Bldg_Rm_Flr;
    }

    public void setBldg_Rm_Flr(String bldg_Rm_Flr) {
        Bldg_Rm_Flr = bldg_Rm_Flr;
    }

    public String getBookValue() {
        return BookValue;
    }

    public void setBookValue(String bookValue) {
        BookValue = bookValue;
    }

    public String getBuildingCode() {
        return BuildingCode;
    }

    public void setBuildingCode(String buildingCode) {
        BuildingCode = buildingCode;
    }

    public com.geodata.rapida.plus.Retrofit.Model.BuildingConditionReport getBuildingConditionReport() {
        return BuildingConditionReport;
    }

    public void setBuildingConditionReport(com.geodata.rapida.plus.Retrofit.Model.BuildingConditionReport buildingConditionReport) {
        BuildingConditionReport = buildingConditionReport;
    }

    public String getBuildingDisplayID() {
        return BuildingDisplayID;
    }

    public void setBuildingDisplayID(String buildingDisplayID) {
        BuildingDisplayID = buildingDisplayID;
    }

    public String getBuildingInfoID() {
        return BuildingInfoID;
    }

    public void setBuildingInfoID(String buildingInfoID) {
        BuildingInfoID = buildingInfoID;
    }

    public String getBuildingInventoryYearID() {
        return BuildingInventoryYearID;
    }

    public void setBuildingInventoryYearID(String buildingInventoryYearID) {
        BuildingInventoryYearID = buildingInventoryYearID;
    }

    public String getBuildingLandPin() {
        return BuildingLandPin;
    }

    public void setBuildingLandPin(String buildingLandPin) {
        BuildingLandPin = buildingLandPin;
    }

    public String getBuildingName() {
        return BuildingName;
    }

    public void setBuildingName(String buildingName) {
        BuildingName = buildingName;
    }

    public com.geodata.rapida.plus.Retrofit.Model.BuildingNoOfPersons getBuildingNoOfPersons() {
        return BuildingNoOfPersons;
    }

    public void setBuildingNoOfPersons(com.geodata.rapida.plus.Retrofit.Model.BuildingNoOfPersons buildingNoOfPersons) {
        BuildingNoOfPersons = buildingNoOfPersons;
    }

    public com.geodata.rapida.plus.Retrofit.Model.BuildingOwnershipType getBuildingOwnershipType() {
        return BuildingOwnershipType;
    }

    public void setBuildingOwnershipType(com.geodata.rapida.plus.Retrofit.Model.BuildingOwnershipType buildingOwnershipType) {
        BuildingOwnershipType = buildingOwnershipType;
    }

    public com.geodata.rapida.plus.Retrofit.Model.BuildingSoilType getBuildingSoilType() {
        return BuildingSoilType;
    }

    public void setBuildingSoilType(com.geodata.rapida.plus.Retrofit.Model.BuildingSoilType buildingSoilType) {
        BuildingSoilType = buildingSoilType;
    }

    public String getContactNo() {
        return ContactNo;
    }

    public void setContactNo(String contactNo) {
        ContactNo = contactNo;
    }

    public String getCost() {
        return Cost;
    }

    public void setCost(String cost) {
        Cost = cost;
    }

    public String getCostPerSqm() {
        return CostPerSqm;
    }

    public void setCostPerSqm(String costPerSqm) {
        CostPerSqm = costPerSqm;
    }

    public String getDPWHOB_ID() {
        return DPWHOB_ID;
    }

    public void setDPWHOB_ID(String DPWHOB_ID) {
        this.DPWHOB_ID = DPWHOB_ID;
    }

    public String getDateFinished() {
        return DateFinished;
    }

    public void setDateFinished(String dateFinished) {
        DateFinished = dateFinished;
    }

    public String getDateSaved() {
        return DateSaved;
    }

    public void setDateSaved(String dateSaved) {
        DateSaved = dateSaved;
    }

    public String getDateUpdated() {
        return DateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        DateUpdated = dateUpdated;
    }

    public DistrictOffice getDistrictOffice() {
        return DistrictOffice;
    }

    public void setDistrictOffice(DistrictOffice districtOffice) {
        DistrictOffice = districtOffice;
    }

    public String getDistrictOfficeID() {
        return DistrictOfficeID;
    }

    public void setDistrictOfficeID(String districtOfficeID) {
        DistrictOfficeID = districtOfficeID;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getFaultDistance() {
        return FaultDistance;
    }

    public void setFaultDistance(String faultDistance) {
        FaultDistance = faultDistance;
    }

    public String getFilesUrl() {
        return FilesUrl;
    }

    public void setFilesUrl(String filesUrl) {
        FilesUrl = filesUrl;
    }

    public String getFloorArea() {
        return FloorArea;
    }

    public void setFloorArea(String floorArea) {
        FloorArea = floorArea;
    }

    public String getGOB_ID() {
        return GOB_ID;
    }

    public void setGOB_ID(String GOB_ID) {
        this.GOB_ID = GOB_ID;
    }

    public String getHeight() {
        return Height;
    }

    public void setHeight(String height) {
        Height = height;
    }

    public String getImageFile() {
        return ImageFile;
    }

    public void setImageFile(String imageFile) {
        ImageFile = imageFile;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getInventoryYear() {
        return InventoryYear;
    }

    public void setInventoryYear(String inventoryYear) {
        InventoryYear = inventoryYear;
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }

    public String getLifeSpanOfBuilding() {
        return LifeSpanOfBuilding;
    }

    public void setLifeSpanOfBuilding(String lifeSpanOfBuilding) {
        LifeSpanOfBuilding = lifeSpanOfBuilding;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getLong() {
        return Long;
    }

    public String getAltitude() {
        return Altitude;
    }

    public void setAltitude(String altitude) {
        Altitude = altitude;
    }

    public void setLong(String aLong) {
        Long = aLong;
    }

    public String getLotArea() {
        return LotArea;
    }

    public void setLotArea(String lotArea) {
        LotArea = lotArea;
    }

    public com.geodata.rapida.plus.Retrofit.Model.LotOwnershipType getLotOwnershipType() {
        return LotOwnershipType;
    }

    public void setLotOwnershipType(com.geodata.rapida.plus.Retrofit.Model.LotOwnershipType lotOwnershipType) {
        LotOwnershipType = lotOwnershipType;
    }

    public String getLotOwnershipTypeID() {
        return LotOwnershipTypeID;
    }

    public void setLotOwnershipTypeID(String lotOwnershipTypeID) {
        LotOwnershipTypeID = lotOwnershipTypeID;
    }

    public String getNearestFault() {
        return NearestFault;
    }

    public void setNearestFault(String nearestFault) {
        NearestFault = nearestFault;
    }

    public String getNoOfFloors() {
        return NoOfFloors;
    }

    public void setNoOfFloors(String noOfFloors) {
        NoOfFloors = noOfFloors;
    }

    public String getNoOfPersonsID() {
        return NoOfPersonsID;
    }

    public void setNoOfPersonsID(String noOfPersonsID) {
        NoOfPersonsID = noOfPersonsID;
    }

    public String getNoOfUnits() {
        return NoOfUnits;
    }

    public void setNoOfUnits(String noOfUnits) {
        NoOfUnits = noOfUnits;
    }

    public String getOccupancies() {
        return Occupancies;
    }

    public void setOccupancies(String occupancies) {
        Occupancies = occupancies;
    }

    public String getOjectID() {
        return OjectID;
    }

    public void setOjectID(String ojectID) {
        OjectID = ojectID;
    }

    public String getOpenSpace() {
        return OpenSpace;
    }

    public void setOpenSpace(String openSpace) {
        OpenSpace = openSpace;
    }

    public String getOwnerName() {
        return OwnerName;
    }

    public void setOwnerName(String ownerName) {
        OwnerName = ownerName;
    }

    public String getOwnershipType() {
        return OwnershipType;
    }

    public void setOwnershipType(String ownershipType) {
        OwnershipType = ownershipType;
    }

    public String getPosition() {
        return Position;
    }

    public void setPosition(String position) {
        Position = position;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getSeismicity() {
        return Seismicity;
    }

    public void setSeismicity(String seismicity) {
        Seismicity = seismicity;
    }

    public String getSoilTypeID() {
        return SoilTypeID;
    }

    public void setSoilTypeID(String soilTypeID) {
        SoilTypeID = soilTypeID;
    }

    public com.geodata.rapida.plus.Retrofit.Model.StructureType getStructureType() {
        return StructureType;
    }

    public void setStructureType(com.geodata.rapida.plus.Retrofit.Model.StructureType structureType) {
        StructureType = structureType;
    }

    public String getStructureTypeID() {
        return StructureTypeID;
    }

    public void setStructureTypeID(String structureTypeID) {
        StructureTypeID = structureTypeID;
    }

    public String getTCTNo() {
        return TCTNo;
    }

    public void setTCTNo(String TCTNo) {
        this.TCTNo = TCTNo;
    }

    public String getValueOfRepair() {
        return ValueOfRepair;
    }

    public void setValueOfRepair(String valueOfRepair) {
        ValueOfRepair = valueOfRepair;
    }

    public String getZipCode() {
        return ZipCode;
    }

    public void setZipCode(String zipCode) {
        ZipCode = zipCode;
    }

    public String getBrgyCode() {
        return brgyCode;
    }

    public void setBrgyCode(String brgyCode) {
        this.brgyCode = brgyCode;
    }

    public String getCitymunCode() {
        return citymunCode;
    }

    public void setCitymunCode(String citymunCode) {
        this.citymunCode = citymunCode;
    }

    public com.geodata.rapida.plus.Retrofit.Model.gisBarangay getGisBarangay() {
        return gisBarangay;
    }

    public void setGisBarangay(com.geodata.rapida.plus.Retrofit.Model.gisBarangay gisBarangay) {
        this.gisBarangay = gisBarangay;
    }

    public com.geodata.rapida.plus.Retrofit.Model.gisCity getGisCity() {
        return gisCity;
    }

    public void setGisCity(com.geodata.rapida.plus.Retrofit.Model.gisCity gisCity) {
        this.gisCity = gisCity;
    }

    public com.geodata.rapida.plus.Retrofit.Model.gisProvince getGisProvince() {
        return gisProvince;
    }

    public void setGisProvince(com.geodata.rapida.plus.Retrofit.Model.gisProvince gisProvince) {
        this.gisProvince = gisProvince;
    }

    public com.geodata.rapida.plus.Retrofit.Model.gisRegion getGisRegion() {
        return gisRegion;
    }

    public void setGisRegion(com.geodata.rapida.plus.Retrofit.Model.gisRegion gisRegion) {
        this.gisRegion = gisRegion;
    }

    public String getIsForInspection() {
        return isForInspection;
    }

    public void setIsForInspection(String isForInspection) {
        this.isForInspection = isForInspection;
    }

    public String getProvCode() {
        return provCode;
    }

    public void setProvCode(String provCode) {
        this.provCode = provCode;
    }

    public String getRegCode() {
        return regCode;
    }

    public void setRegCode(String regCode) {
        this.regCode = regCode;
    }
}

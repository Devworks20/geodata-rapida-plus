package com.geodata.rapida.plus.Retrofit.Model;

import com.geodata.rapida.plus.SQLite.Class.buildingOccupancyList;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class BuildingListOfEDIModel implements Serializable
{
    @SerializedName("buildingOccupancyList")
    @Expose
    private List<buildingOccupancyList> buildingOccupancyList;

    @SerializedName("AssetInfoBuildingID")
    @Expose
    private String AssetInfoBuildingID;

    @SerializedName("AssetID")
    @Expose
    private String AssetID;

    @SerializedName("ASSET_ID")
    @Expose
    private String ASSET_ID;

    @SerializedName("AssetCatID")
    @Expose
    private String AssetCatID;

    @SerializedName("BuildingCode")
    @Expose
    private String BuildingCode;

    @SerializedName("AssetCode")
    @Expose
    private String AssetCode;

    @SerializedName("BuildingLandPin")
    @Expose
    private String BuildingLandPin;

    @SerializedName("TCTNo")
    @Expose
    private String TCTNo;

    @SerializedName("Cost")
    @Expose
    private String Cost;

    @SerializedName("NavAssetCatID")
    @Expose
    private String NavAssetCatID;

    @SerializedName("BuildingClassification")
    @Expose
    private String BuildingClassification;

    @SerializedName("BuildingPhoto")
    @Expose
    private String BuildingPhoto;

    @SerializedName("InventoryYear")
    @Expose
    private String InventoryYear;

    @SerializedName("AccountCode")
    @Expose
    private String AccountCode;

    @SerializedName("BuildingName")
    @Expose
    private String BuildingName;

    @SerializedName("BIN")
    @Expose
    private String BIN;

    @SerializedName("Agency")
    @Expose
    private String Agency;

    @SerializedName("DateBuilt")
    @Expose
    private String DateBuilt;

    @SerializedName("BuildingAge")
    @Expose
    private String BuildingAge;

    @SerializedName("BookValue")
    @Expose
    private String BookValue;

    @SerializedName("AssessedValue")
    @Expose
    private String AssessedValue;

    @SerializedName("Group")
    @Expose
    private String Groups;

    @SerializedName("BldgOwnershipType")
    @Expose
    private String BldgOwnershipType;

    @SerializedName("SoilType")
    @Expose
    private String SoilType;

    @SerializedName("NoOFPersons")
    @Expose
    private String NoOFPersons;

    @SerializedName("StructuralTypeDesc")
    @Expose
    private String StructuralTypeDesc;

    @SerializedName("StructuralTypeCode")
    @Expose
    private String StructuralTypeCode;

    @SerializedName("FloorArea")
    @Expose
    private String FloorArea;

    @SerializedName("Height")
    @Expose
    private String Height;

    @SerializedName("NoOfStories")
    @Expose
    private String NoOfStories;

    @SerializedName("OpenSpace")
    @Expose
    private String OpenSpace;

    @SerializedName("AveAreaPerFloor")
    @Expose
    private String AveAreaPerFloor;

    @SerializedName("CostPerSqm")
    @Expose
    private String CostPerSqm;

    @SerializedName("NoOfUnits")
    @Expose
    private String NoOfUnits;

    @SerializedName("OwnerName")
    @Expose
    private String OwnerName;

    @SerializedName("Position")
    @Expose
    private String Position;

    @SerializedName("ContactNo")
    @Expose
    private String ContactNo;

    @SerializedName("Email")
    @Expose
    private String Email;

    @SerializedName("BuildingPhotoPath")
    @Expose
    private String BuildingPhotoPath;

    @SerializedName("SeismicityRegion")
    @Expose
    private String SeismicityRegion;

    @SerializedName("LotOwnType")
    @Expose
    private String LotOwnType;

    @SerializedName("LotArea")
    @Expose
    private String LotArea;

    @SerializedName("BIRZonalValue")
    @Expose
    private String BIRZonalValue;

    @SerializedName("BuildingCondition")
    @Expose
    private String BuildingCondition;

    @SerializedName("ValueOfRepair")
    @Expose
    private String ValueOfRepair;

    @SerializedName("Remarks")
    @Expose
    private String Remarks;

    @SerializedName("Region")
    @Expose
    private String Region;

    @SerializedName("Province")
    @Expose
    private String Province;

    @SerializedName("City")
    @Expose
    private String City;

    @SerializedName("District")
    @Expose
    private String District;

    @SerializedName("DistrictOffice")
    @Expose
    private String DistrictOffice;

    @SerializedName("Barangay")
    @Expose
    private String Barangay;

    @SerializedName("BlockNo")
    @Expose
    private String BlockNo;

    @SerializedName("Street")
    @Expose
    private String Street;

    @SerializedName("SubdCompVill")
    @Expose
    private String SubdCompVill;

    @SerializedName("Purok")
    @Expose
    private String Purok;

    @SerializedName("Sitio")
    @Expose
    private String Sitio;

    @SerializedName("Zip")
    @Expose
    private String Zip;

    @SerializedName("Latitude")
    @Expose
    private String Latitude;

    @SerializedName("Longitude")
    @Expose
    private String Longitude;

    @SerializedName("BuildingFullAddress")
    @Expose
    private String BuildingFullAddress;

    private String ScreenerID;
    private String InspectedBy;

    private String ConstructionStatus;
    private String VisitNo;
    private String StoreyNo;
    private String Residential;
    private String BldgPermitNo;
    private String CompanyName;
    private String PresidentName;
    private String isWoodFrame;
    private String isSteelFrame;
    private String isConcreteFrame;

    private String BasicScoreWF;
    private String BasicScoreSF;
    private String BasicScoreCF;

    private String VerticalIrregularityWF;
    private String VerticalIrregularitySF;
    private String VerticalIrregularityCF;

    private String PlanIrregularityWF;
    private String PlanIrregularitySF;
    private String PlanIrregularityCF;

    private String PreCodeWF;
    private String PreCodeSF;
    private String PreCodeCF;

    private String PostBenchmarkWF;
    private String PostBenchmarkSF;
    private String PostBenchmarkCF;

    private String SoilTypeC2WF;
    private String SoilTypeC2SF;
    private String SoilTypeC2CF;
    private String SoilTypeD3WF;
    private String SoilTypeD3SF;
    private String SoilTypeD3CF;
    private String SoilTypeE4WF;
    private String SoilTypeE4SF;
    private String SoilTypeE4CF;
    private String isPreCode;
    private String isPostBenchmark;
    private String isSoilTypeC2;
    private String isSoilTypeD3;
    private String isSoilTypeE4;
    private String WoodFrameFinalScore;
    private String SteelFrameFinalScore;
    private String ConcreteFrameFinalScore;
    private String StructuralComponents;
    private String NonStructuralComponents;
    private String AncillaryAuxiliaryComponents;

    private String BuildingPhotoBase64;
    private String dtAdded;
    private String isSynced;

    public BuildingListOfEDIModel()
    {
        AssetInfoBuildingID         = "";
        AssetID                     = "";
        ASSET_ID                    = "";
        AssetCatID                  = "";
        BuildingCode                = "";
        AssetCode                   = "";
        BuildingLandPin             = "";
        TCTNo                       = "";
        Cost                        = "";
        NavAssetCatID               = "";
        BuildingClassification      = "";
        BuildingPhoto               = "";
        InventoryYear               = "";
        AccountCode                 = "";
        BuildingName                = "";
        BIN                         = "";
        Agency                      = "";
        DateBuilt                   = "";
        BuildingAge                 = "";
        BookValue                   = "";
        AssessedValue               = "";
        Groups = "";
        BldgOwnershipType           = "";
        SoilType                    = "";
        NoOFPersons                 = "";
        StructuralTypeDesc          = "";
        StructuralTypeCode          = "";
        FloorArea                   = "";
        Height                      = "";
        NoOfStories                 = "";
        OpenSpace                   = "";
        AveAreaPerFloor             = "";
        CostPerSqm                  = "";
        NoOfUnits                   = "";
        OwnerName                   = "";
        Position                    = "";
        ContactNo                   = "";
        Email                       = "";
        BuildingPhotoPath           = "";
        SeismicityRegion            = "";
        LotOwnType                  = "";
        LotArea                     = "";
        BIRZonalValue               = "";
        BuildingCondition           = "";
        ValueOfRepair               = "";
        Remarks                     = "";
        Region                      = "";
        Province                    = "";
        City                        = "";
        District                    = "";
        DistrictOffice              = "";
        Barangay                    = "";
        BlockNo                     = "";
        Street                      = "";
        SubdCompVill                = "";
        Purok                       = "";
        Sitio                       = "";
        Zip                         = "";
        Latitude                    = "";
        Longitude                   = "";
        BuildingFullAddress         = "";
        ScreenerID                  = "";
        ConstructionStatus          = "";
        VisitNo                     = "";
        StoreyNo                    = "";
        Residential                 = "";
        BldgPermitNo                = "";
        OwnerName                   = "";
        CompanyName                 = "";
        PresidentName               = "";
        isWoodFrame                 = "";
        isSteelFrame                = "";
        isConcreteFrame             = "";

        BasicScoreWF = "";
        BasicScoreSF = "";
        BasicScoreCF = "";

        VerticalIrregularityWF      = "";
        VerticalIrregularitySF      = "";
        VerticalIrregularityCF      = "";

        PlanIrregularityWF          = "";
        PlanIrregularitySF          = "";
        PlanIrregularityCF          = "";

        PreCodeWF                   = "";
        PreCodeSF                   = "";
        PreCodeCF                   = "";

        PostBenchmarkWF             = "";
        PostBenchmarkSF             = "";
        PostBenchmarkCF             = "";

        SoilTypeC2WF                = "";
        SoilTypeC2SF                = "";
        SoilTypeC2CF                = "";

        SoilTypeD3WF                = "";
        SoilTypeD3SF                = "";
        SoilTypeD3CF                = "";

        SoilTypeE4WF                = "";
        SoilTypeE4SF                = "";
        SoilTypeE4CF                = "";

        isPreCode                   = "";
        isPostBenchmark             = "";
        isSoilTypeC2                = "";
        isSoilTypeD3                = "";
        isSoilTypeE4                = "";
        WoodFrameFinalScore         = "";
        SteelFrameFinalScore        = "";
        ConcreteFrameFinalScore     = "";
        StructuralComponents        = "";
        NonStructuralComponents     = "";
        AncillaryAuxiliaryComponents = "";
        InspectedBy                  = "";

        BuildingPhotoBase64 = "";
        dtAdded             = "";
        isSynced            = "0";
    }

    public List<buildingOccupancyList> getBuildingOccupancyList() {
        return buildingOccupancyList;
    }

    public void setBuildingOccupancyList(List<buildingOccupancyList> buildingOccupancyList) {
        this.buildingOccupancyList = buildingOccupancyList;
    }

    public String getAssetInfoBuildingID() {
        return AssetInfoBuildingID;
    }

    public void setAssetInfoBuildingID(String assetInfoBuildingID) {
        AssetInfoBuildingID = assetInfoBuildingID;
    }

    public String getAssetID() {
        return AssetID;
    }

    public void setAssetID(String assetID) {
        AssetID = assetID;
    }

    public String getASSET_ID() {
        return ASSET_ID;
    }

    public void setASSET_ID(String ASSET_ID) {
        this.ASSET_ID = ASSET_ID;
    }

    public String getAssetCatID() {
        return AssetCatID;
    }

    public void setAssetCatID(String assetCatID) {
        AssetCatID = assetCatID;
    }

    public String getBuildingCode() {
        return BuildingCode;
    }

    public void setBuildingCode(String buildingCode) {
        BuildingCode = buildingCode;
    }

    public String getAssetCode() {
        return AssetCode;
    }

    public void setAssetCode(String assetCode) {
        AssetCode = assetCode;
    }

    public String getBuildingLandPin() {
        return BuildingLandPin;
    }

    public void setBuildingLandPin(String buildingLandPin) {
        BuildingLandPin = buildingLandPin;
    }

    public String getTCTNo() {
        return TCTNo;
    }

    public void setTCTNo(String TCTNo) {
        this.TCTNo = TCTNo;
    }

    public String getCost() {
        return Cost;
    }

    public void setCost(String cost) {
        Cost = cost;
    }

    public String getNavAssetCatID() {
        return NavAssetCatID;
    }

    public void setNavAssetCatID(String navAssetCatID) {
        NavAssetCatID = navAssetCatID;
    }

    public String getBuildingClassification() {
        return BuildingClassification;
    }

    public void setBuildingClassification(String buildingClassification) {
        BuildingClassification = buildingClassification;
    }

    public String getBuildingPhoto() {
        return BuildingPhoto;
    }

    public void setBuildingPhoto(String buildingPhoto) {
        BuildingPhoto = buildingPhoto;
    }

    public String getInventoryYear() {
        return InventoryYear;
    }

    public void setInventoryYear(String inventoryYear) {
        InventoryYear = inventoryYear;
    }

    public String getAccountCode() {
        return AccountCode;
    }

    public void setAccountCode(String accountCode) {
        AccountCode = accountCode;
    }

    public String getBuildingName() {
        return BuildingName;
    }

    public void setBuildingName(String buildingName) {
        BuildingName = buildingName;
    }

    public String getBIN() {
        return BIN;
    }

    public void setBIN(String BIN) {
        this.BIN = BIN;
    }

    public String getAgency() {
        return Agency;
    }

    public void setAgency(String agency) {
        Agency = agency;
    }

    public String getDateBuilt() {
        return DateBuilt;
    }

    public void setDateBuilt(String dateBuilt) {
        DateBuilt = dateBuilt;
    }

    public String getBuildingAge() {
        return BuildingAge;
    }

    public void setBuildingAge(String buildingAge) {
        BuildingAge = buildingAge;
    }

    public String getBookValue() {
        return BookValue;
    }

    public void setBookValue(String bookValue) {
        BookValue = bookValue;
    }

    public String getAssessedValue() {
        return AssessedValue;
    }

    public void setAssessedValue(String assessedValue) {
        AssessedValue = assessedValue;
    }

    public String getGroups() {
        return Groups;
    }

    public void setGroups(String groups) {
        Groups = groups;
    }

    public String getBldgOwnershipType() {
        return BldgOwnershipType;
    }

    public void setBldgOwnershipType(String bldgOwnershipType) {
        BldgOwnershipType = bldgOwnershipType;
    }

    public String getSoilType() {
        return SoilType;
    }

    public void setSoilType(String soilType) {
        SoilType = soilType;
    }

    public String getNoOFPersons() {
        return NoOFPersons;
    }

    public void setNoOFPersons(String noOFPersons) {
        NoOFPersons = noOFPersons;
    }

    public String getStructuralTypeDesc() {
        return StructuralTypeDesc;
    }

    public void setStructuralTypeDesc(String structuralTypeDesc) {
        StructuralTypeDesc = structuralTypeDesc;
    }

    public String getStructuralTypeCode() {
        return StructuralTypeCode;
    }

    public void setStructuralTypeCode(String structuralTypeCode) {
        StructuralTypeCode = structuralTypeCode;
    }

    public String getFloorArea() {
        return FloorArea;
    }

    public void setFloorArea(String floorArea) {
        FloorArea = floorArea;
    }

    public String getHeight() {
        return Height;
    }

    public void setHeight(String height) {
        Height = height;
    }

    public String getNoOfStories() {
        return NoOfStories;
    }

    public void setNoOfStories(String noOfStories) {
        NoOfStories = noOfStories;
    }

    public String getOpenSpace() {
        return OpenSpace;
    }

    public void setOpenSpace(String openSpace) {
        OpenSpace = openSpace;
    }

    public String getAveAreaPerFloor() {
        return AveAreaPerFloor;
    }

    public void setAveAreaPerFloor(String aveAreaPerFloor) {
        AveAreaPerFloor = aveAreaPerFloor;
    }

    public String getCostPerSqm() {
        return CostPerSqm;
    }

    public void setCostPerSqm(String costPerSqm) {
        CostPerSqm = costPerSqm;
    }

    public String getNoOfUnits() {
        return NoOfUnits;
    }

    public void setNoOfUnits(String noOfUnits) {
        NoOfUnits = noOfUnits;
    }

    public String getOwnerName() {
        return OwnerName;
    }

    public void setOwnerName(String ownerName) {
        OwnerName = ownerName;
    }

    public String getPosition() {
        return Position;
    }

    public void setPosition(String position) {
        Position = position;
    }

    public String getContactNo() {
        return ContactNo;
    }

    public void setContactNo(String contactNo) {
        ContactNo = contactNo;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getBuildingPhotoPath() {
        return BuildingPhotoPath;
    }

    public void setBuildingPhotoPath(String buildingPhotoPath) {
        BuildingPhotoPath = buildingPhotoPath;
    }

    public String getSeismicityRegion() {
        return SeismicityRegion;
    }

    public void setSeismicityRegion(String seismicityRegion) {
        SeismicityRegion = seismicityRegion;
    }

    public String getLotOwnType() {
        return LotOwnType;
    }

    public void setLotOwnType(String lotOwnType) {
        LotOwnType = lotOwnType;
    }

    public String getLotArea() {
        return LotArea;
    }

    public void setLotArea(String lotArea) {
        LotArea = lotArea;
    }

    public String getBIRZonalValue() {
        return BIRZonalValue;
    }

    public void setBIRZonalValue(String BIRZonalValue) {
        this.BIRZonalValue = BIRZonalValue;
    }

    public String getBuildingCondition() {
        return BuildingCondition;
    }

    public void setBuildingCondition(String buildingCondition) {
        BuildingCondition = buildingCondition;
    }

    public String getValueOfRepair() {
        return ValueOfRepair;
    }

    public void setValueOfRepair(String valueOfRepair) {
        ValueOfRepair = valueOfRepair;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getRegion() {
        return Region;
    }

    public void setRegion(String region) {
        Region = region;
    }

    public String getProvince() {
        return Province;
    }

    public void setProvince(String province) {
        Province = province;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public String getDistrictOffice() {
        return DistrictOffice;
    }

    public void setDistrictOffice(String districtOffice) {
        DistrictOffice = districtOffice;
    }

    public String getBarangay() {
        return Barangay;
    }

    public void setBarangay(String barangay) {
        Barangay = barangay;
    }

    public String getBlockNo() {
        return BlockNo;
    }

    public void setBlockNo(String blockNo) {
        BlockNo = blockNo;
    }

    public String getStreet() {
        return Street;
    }

    public void setStreet(String street) {
        Street = street;
    }

    public String getSubdCompVill() {
        return SubdCompVill;
    }

    public void setSubdCompVill(String subdCompVill) {
        SubdCompVill = subdCompVill;
    }

    public String getPurok() {
        return Purok;
    }

    public void setPurok(String purok) {
        Purok = purok;
    }

    public String getSitio() {
        return Sitio;
    }

    public void setSitio(String sitio) {
        Sitio = sitio;
    }

    public String getZip() {
        return Zip;
    }

    public void setZip(String zip) {
        Zip = zip;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getBuildingFullAddress() {
        return BuildingFullAddress;
    }

    public void setBuildingFullAddress(String buildingFullAddress) {
        BuildingFullAddress = buildingFullAddress;
    }

    public String getScreenerID() {
        return ScreenerID;
    }

    public void setScreenerID(String screenerID) {
        ScreenerID = screenerID;
    }

    public String getConstructionStatus() {
        return ConstructionStatus;
    }

    public void setConstructionStatus(String constructionStatus) {
        ConstructionStatus = constructionStatus;
    }

    public String getVisitNo() {
        return VisitNo;
    }

    public void setVisitNo(String visitNo) {
        VisitNo = visitNo;
    }

    public String getStoreyNo() {
        return StoreyNo;
    }

    public void setStoreyNo(String storeyNo) {
        StoreyNo = storeyNo;
    }

    public String getResidential() {
        return Residential;
    }

    public void setResidential(String residential) {
        Residential = residential;
    }

    public String getBldgPermitNo() {
        return BldgPermitNo;
    }

    public void setBldgPermitNo(String bldgPermitNo) {
        BldgPermitNo = bldgPermitNo;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public String getPresidentName() {
        return PresidentName;
    }

    public void setPresidentName(String presidentName) {
        PresidentName = presidentName;
    }

    public String getIsWoodFrame() {
        return isWoodFrame;
    }

    public void setIsWoodFrame(String isWoodFrame) {
        this.isWoodFrame = isWoodFrame;
    }

    public String getIsSteelFrame() {
        return isSteelFrame;
    }

    public void setIsSteelFrame(String isSteelFrame) {
        this.isSteelFrame = isSteelFrame;
    }

    public String getIsConcreteFrame() {
        return isConcreteFrame;
    }

    public void setIsConcreteFrame(String isConcreteFrame) {
        this.isConcreteFrame = isConcreteFrame;
    }

    public String getBasicScoreWF() {
        return BasicScoreWF;
    }

    public void setBasicScoreWF(String basicScoreWF) {
        BasicScoreWF = basicScoreWF;
    }

    public String getBasicScoreSF() {
        return BasicScoreSF;
    }

    public void setBasicScoreSF(String basicScoreSF) {
        BasicScoreSF = basicScoreSF;
    }

    public String getBasicScoreCF() {
        return BasicScoreCF;
    }

    public void setBasicScoreCF(String basicScoreCF) {
        BasicScoreCF = basicScoreCF;
    }

    public String getVerticalIrregularityWF() {
        return VerticalIrregularityWF;
    }

    public void setVerticalIrregularityWF(String verticalIrregularityWF) {
        VerticalIrregularityWF = verticalIrregularityWF;
    }

    public String getVerticalIrregularitySF() {
        return VerticalIrregularitySF;
    }

    public void setVerticalIrregularitySF(String verticalIrregularitySF) {
        VerticalIrregularitySF = verticalIrregularitySF;
    }

    public String getVerticalIrregularityCF() {
        return VerticalIrregularityCF;
    }

    public void setVerticalIrregularityCF(String verticalIrregularityCF) {
        VerticalIrregularityCF = verticalIrregularityCF;
    }

    public String getPlanIrregularityWF() {
        return PlanIrregularityWF;
    }

    public void setPlanIrregularityWF(String planIrregularityWF) {
        PlanIrregularityWF = planIrregularityWF;
    }

    public String getPlanIrregularitySF() {
        return PlanIrregularitySF;
    }

    public void setPlanIrregularitySF(String planIrregularitySF) {
        PlanIrregularitySF = planIrregularitySF;
    }

    public String getPlanIrregularityCF() {
        return PlanIrregularityCF;
    }

    public void setPlanIrregularityCF(String planIrregularityCF) {
        PlanIrregularityCF = planIrregularityCF;
    }

    public String getPreCodeWF() {
        return PreCodeWF;
    }

    public void setPreCodeWF(String preCodeWF) {
        PreCodeWF = preCodeWF;
    }

    public String getPreCodeSF() {
        return PreCodeSF;
    }

    public void setPreCodeSF(String preCodeSF) {
        PreCodeSF = preCodeSF;
    }

    public String getPreCodeCF() {
        return PreCodeCF;
    }

    public void setPreCodeCF(String preCodeCF) {
        PreCodeCF = preCodeCF;
    }

    public String getPostBenchmarkWF() {
        return PostBenchmarkWF;
    }

    public void setPostBenchmarkWF(String postBenchmarkWF) {
        PostBenchmarkWF = postBenchmarkWF;
    }

    public String getPostBenchmarkSF() {
        return PostBenchmarkSF;
    }

    public void setPostBenchmarkSF(String postBenchmarkSF) {
        PostBenchmarkSF = postBenchmarkSF;
    }

    public String getPostBenchmarkCF() {
        return PostBenchmarkCF;
    }

    public void setPostBenchmarkCF(String postBenchmarkCF) {
        PostBenchmarkCF = postBenchmarkCF;
    }

    public String getSoilTypeC2WF() {
        return SoilTypeC2WF;
    }

    public void setSoilTypeC2WF(String soilTypeC2WF) {
        SoilTypeC2WF = soilTypeC2WF;
    }

    public String getSoilTypeC2SF() {
        return SoilTypeC2SF;
    }

    public void setSoilTypeC2SF(String soilTypeC2SF) {
        SoilTypeC2SF = soilTypeC2SF;
    }

    public String getSoilTypeC2CF() {
        return SoilTypeC2CF;
    }

    public void setSoilTypeC2CF(String soilTypeC2CF) {
        SoilTypeC2CF = soilTypeC2CF;
    }

    public String getSoilTypeD3WF() {
        return SoilTypeD3WF;
    }

    public void setSoilTypeD3WF(String soilTypeD3WF) {
        SoilTypeD3WF = soilTypeD3WF;
    }

    public String getSoilTypeD3SF() {
        return SoilTypeD3SF;
    }

    public void setSoilTypeD3SF(String soilTypeD3SF) {
        SoilTypeD3SF = soilTypeD3SF;
    }

    public String getSoilTypeD3CF() {
        return SoilTypeD3CF;
    }

    public void setSoilTypeD3CF(String soilTypeD3CF) {
        SoilTypeD3CF = soilTypeD3CF;
    }

    public String getSoilTypeE4WF() {
        return SoilTypeE4WF;
    }

    public void setSoilTypeE4WF(String soilTypeE4WF) {
        SoilTypeE4WF = soilTypeE4WF;
    }

    public String getSoilTypeE4SF() {
        return SoilTypeE4SF;
    }

    public void setSoilTypeE4SF(String soilTypeE4SF) {
        SoilTypeE4SF = soilTypeE4SF;
    }

    public String getSoilTypeE4CF() {
        return SoilTypeE4CF;
    }

    public void setSoilTypeE4CF(String soilTypeE4CF) {
        SoilTypeE4CF = soilTypeE4CF;
    }

    public String getIsPreCode() {
        return isPreCode;
    }

    public void setIsPreCode(String isPreCode) {
        this.isPreCode = isPreCode;
    }

    public String getIsPostBenchmark() {
        return isPostBenchmark;
    }

    public void setIsPostBenchmark(String isPostBenchmark) {
        this.isPostBenchmark = isPostBenchmark;
    }

    public String getIsSoilTypeC2() {
        return isSoilTypeC2;
    }

    public void setIsSoilTypeC2(String isSoilTypeC2) {
        this.isSoilTypeC2 = isSoilTypeC2;
    }

    public String getIsSoilTypeD3() {
        return isSoilTypeD3;
    }

    public void setIsSoilTypeD3(String isSoilTypeD3) {
        this.isSoilTypeD3 = isSoilTypeD3;
    }

    public String getIsSoilTypeE4() {
        return isSoilTypeE4;
    }

    public void setIsSoilTypeE4(String isSoilTypeE4) {
        this.isSoilTypeE4 = isSoilTypeE4;
    }

    public String getWoodFrameFinalScore() {
        return WoodFrameFinalScore;
    }

    public void setWoodFrameFinalScore(String woodFrameFinalScore) {
        WoodFrameFinalScore = woodFrameFinalScore;
    }

    public String getSteelFrameFinalScore() {
        return SteelFrameFinalScore;
    }

    public void setSteelFrameFinalScore(String steelFrameFinalScore) {
        SteelFrameFinalScore = steelFrameFinalScore;
    }

    public String getConcreteFrameFinalScore() {
        return ConcreteFrameFinalScore;
    }

    public void setConcreteFrameFinalScore(String concreteFrameFinalScore) {
        ConcreteFrameFinalScore = concreteFrameFinalScore;
    }

    public String getStructuralComponents() {
        return StructuralComponents;
    }

    public void setStructuralComponents(String structuralComponents) {
        StructuralComponents = structuralComponents;
    }

    public String getNonStructuralComponents() {
        return NonStructuralComponents;
    }

    public void setNonStructuralComponents(String nonStructuralComponents) {
        NonStructuralComponents = nonStructuralComponents;
    }

    public String getAncillaryAuxiliaryComponents() {
        return AncillaryAuxiliaryComponents;
    }

    public void setAncillaryAuxiliaryComponents(String ancillaryAuxiliaryComponents) {
        AncillaryAuxiliaryComponents = ancillaryAuxiliaryComponents;
    }

    public String getInspectedBy() {
        return InspectedBy;
    }

    public void setInspectedBy(String inspectedBy) {
        InspectedBy = inspectedBy;
    }

    public String getBuildingPhotoBase64() {
        return BuildingPhotoBase64;
    }

    public void setBuildingPhotoBase64(String buildingPhotoBase64) {
        BuildingPhotoBase64 = buildingPhotoBase64;
    }

    public String getDtAdded() {
        return dtAdded;
    }

    public void setDtAdded(String dtAdded) {
        this.dtAdded = dtAdded;
    }

    public String getIsSynced() {
        return isSynced;
    }

    public void setIsSynced(String isSynced) {
        this.isSynced = isSynced;
    }
}

package com.geodata.rapida.plus.SQLite.Class;

public class MissionOrdersClass
{
    private String ApprovedBy;
    private String AssetID;
    private String DateIssued;
    private String DateReported;
    private String InspectionStatus;
    private String MissionOrderID;
    private String MissionOrderNo;
    private String ReasonForInspector;

    private String BuildingName;
    private String SeismicityRegion;
    private String ScreeningDate;
    private String ScreeningType;

    private String isActive;
    private String dtAdded;



    public MissionOrdersClass()
    {
        ApprovedBy          = "";
        AssetID             = "";
        DateIssued          = "";
        DateReported        = "";
        InspectionStatus    = "";
        MissionOrderID      = "";
        MissionOrderNo      = "";
        ReasonForInspector  = "";

        BuildingName     = "";
        SeismicityRegion = "";
        ScreeningDate    = "";
        ScreeningType    = "";

        isActive    = "";
        dtAdded    = "";
    }

    public String getApprovedBy() {
        return ApprovedBy;
    }

    public void setApprovedBy(String approvedBy) {
        ApprovedBy = approvedBy;
    }

    public String getAssetID() {
        return AssetID;
    }

    public void setAssetID(String assetID) {
        AssetID = assetID;
    }

    public String getDateIssued() {
        return DateIssued;
    }

    public void setDateIssued(String dateIssued) {
        DateIssued = dateIssued;
    }

    public String getDateReported() {
        return DateReported;
    }

    public void setDateReported(String dateReported) {
        DateReported = dateReported;
    }

    public String getInspectionStatus() {
        return InspectionStatus;
    }

    public void setInspectionStatus(String inspectionStatus) {
        InspectionStatus = inspectionStatus;
    }

    public String getMissionOrderID() {
        return MissionOrderID;
    }

    public void setMissionOrderID(String missionOrderID) {
        MissionOrderID = missionOrderID;
    }

    public String getMissionOrderNo() {
        return MissionOrderNo;
    }

    public void setMissionOrderNo(String missionOrderNo) {
        MissionOrderNo = missionOrderNo;
    }

    public String getReasonForInspector() {
        return ReasonForInspector;
    }

    public void setReasonForInspector(String reasonForInspector) {
        ReasonForInspector = reasonForInspector;
    }

    public String getBuildingName() {
        return BuildingName;
    }

    public void setBuildingName(String buildingName) {
        BuildingName = buildingName;
    }

    public String getSeismicityRegion() {
        return SeismicityRegion;
    }

    public void setSeismicityRegion(String seismicityRegion) {
        SeismicityRegion = seismicityRegion;
    }

    public String getScreeningDate() {
        return ScreeningDate;
    }

    public void setScreeningDate(String screeningDate) {
        ScreeningDate = screeningDate;
    }

    public String getScreeningType() {
        return ScreeningType;
    }

    public void setScreeningType(String screeningType) {
        ScreeningType = screeningType;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getDtAdded() {
        return dtAdded;
    }

    public void setDtAdded(String dtAdded) {
        this.dtAdded = dtAdded;
    }
}

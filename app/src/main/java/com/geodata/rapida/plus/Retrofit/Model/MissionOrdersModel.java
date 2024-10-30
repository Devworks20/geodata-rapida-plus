package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MissionOrdersModel
{
    private String ID;
    private String ScreenerID;
    private String MissionOrderType;
    private String ReportPath;
    private String isActive;
    private String dtAdded;

    @SerializedName("ApprovedBy")
    @Expose
    private String ApprovedBy;

    @SerializedName("ApprovedByID")
    @Expose
    private String ApprovedByID;

    @SerializedName("AssetID")
    @Expose
    private String AssetID;

    @SerializedName("DateIssued")
    @Expose
    private String DateIssued;

    @SerializedName("DateReported")
    @Expose
    private String DateReported;

    @SerializedName("EndorsedForApproval")
    @Expose
    private String EndorsedForApproval;

    @SerializedName("EndorsedForApprovalID")
    @Expose
    private String EndorsedForApprovalID;

    @SerializedName("InspectionStatus")
    @Expose
    private String InspectionStatus;

    @SerializedName("InventoryYear")
    @Expose
    private String InventoryYear;

    @SerializedName("MOFileAttachmentsList")
    @Expose
    private List<MOFileAttachmentsList> moFileAttachmentsLists;

    @SerializedName("MissionOrderID")
    @Expose
    private String MissionOrderID;

    @SerializedName("MissionOrderNo")
    @Expose
    private String MissionOrderNo;

    @SerializedName("ReasonForScreening")
    @Expose
    private String ReasonForScreening;

    @SerializedName("Remarks")
    @Expose
    private String Remarks;

    @SerializedName("Reports")
    @Expose
    private Reports Reports;

    @SerializedName("ScreeningSchedule")
    @Expose
    private String ScreeningSchedule;

    @SerializedName("ScreeningType")
    @Expose
    private String ScreeningType;

    @SerializedName("SignaturePath")
    @Expose
    private String SignaturePath;

    @SerializedName("assignedInspectorsList")
    @Expose
    private List<AssignedInspectorsListModel> AssignedInspectorsListModel;

    @SerializedName("building")
    @Expose
    private BuildingInformationModel buildingInformationModel;

    public MissionOrdersModel()
    {
        ID                          = "";
        ScreenerID                  = "";
        MissionOrderType            = "";
        ReportPath                  = "";
        ApprovedBy                  = "";
        ApprovedByID                = "";
        AssetID                     = "";
        DateIssued                  = "";
        DateReported                = "";
        EndorsedForApproval         = "";
        EndorsedForApprovalID       = "";
        InspectionStatus            = "";
        InventoryYear               = "";
        moFileAttachmentsLists      = null;
        MissionOrderID              = "";
        MissionOrderNo              = "";
        ReasonForScreening          = "";
        Remarks                     = "";
        Reports                     = null;
        ScreeningSchedule           = "";
        ScreeningType               = "";
        SignaturePath               = "";
        AssignedInspectorsListModel = null;
        buildingInformationModel    = null;
        isActive = "1";
        dtAdded = "";
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getScreenerID() {
        return ScreenerID;
    }

    public void setScreenerID(String screenerID) {
        ScreenerID = screenerID;
    }

    public String getMissionOrderType() {
        return MissionOrderType;
    }

    public void setMissionOrderType(String missionOrderType) {
        MissionOrderType = missionOrderType;
    }

    public String getReportPath() {
        return ReportPath;
    }

    public void setReportPath(String reportPath) {
        ReportPath = reportPath;
    }

    public String getApprovedBy() {
        return ApprovedBy;
    }

    public void setApprovedBy(String approvedBy) {
        ApprovedBy = approvedBy;
    }

    public String getApprovedByID() {
        return ApprovedByID;
    }

    public void setApprovedByID(String approvedByID) {
        ApprovedByID = approvedByID;
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

    public String getEndorsedForApproval() {
        return EndorsedForApproval;
    }

    public void setEndorsedForApproval(String endorsedForApproval) {
        EndorsedForApproval = endorsedForApproval;
    }

    public String getEndorsedForApprovalID() {
        return EndorsedForApprovalID;
    }

    public void setEndorsedForApprovalID(String endorsedForApprovalID) {
        EndorsedForApprovalID = endorsedForApprovalID;
    }

    public String getInspectionStatus() {
        return InspectionStatus;
    }

    public void setInspectionStatus(String inspectionStatus) {
        InspectionStatus = inspectionStatus;
    }

    public String getInventoryYear() {
        return InventoryYear;
    }

    public void setInventoryYear(String inventoryYear) {
        InventoryYear = inventoryYear;
    }

    public List<MOFileAttachmentsList> getMoFileAttachmentsLists() {
        return moFileAttachmentsLists;
    }

    public void setMoFileAttachmentsLists(List<MOFileAttachmentsList> moFileAttachmentsLists) {
        this.moFileAttachmentsLists = moFileAttachmentsLists;
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

    public String getReasonForScreening() {
        return ReasonForScreening;
    }

    public void setReasonForScreening(String reasonForScreening) {
        ReasonForScreening = reasonForScreening;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public com.geodata.rapida.plus.Retrofit.Model.Reports getReports() {
        return Reports;
    }

    public void setReports(com.geodata.rapida.plus.Retrofit.Model.Reports reports) {
        Reports = reports;
    }

    public String getScreeningSchedule() {
        return ScreeningSchedule;
    }

    public void setScreeningSchedule(String screeningSchedule) {
        ScreeningSchedule = screeningSchedule;
    }

    public String getScreeningType() {
        return ScreeningType;
    }

    public void setScreeningType(String screeningType) {
        ScreeningType = screeningType;
    }

    public String getSignaturePath() {
        return SignaturePath;
    }

    public void setSignaturePath(String signaturePath) {
        SignaturePath = signaturePath;
    }

    public List<com.geodata.rapida.plus.Retrofit.Model.AssignedInspectorsListModel> getAssignedInspectorsListModel() {
        return AssignedInspectorsListModel;
    }

    public void setAssignedInspectorsListModel(List<com.geodata.rapida.plus.Retrofit.Model.AssignedInspectorsListModel> assignedInspectorsListModel) {
        AssignedInspectorsListModel = assignedInspectorsListModel;
    }

    public BuildingInformationModel getBuildingInformationModel() {
        return buildingInformationModel;
    }

    public void setBuildingInformationModel(BuildingInformationModel buildingInformationModel) {
        this.buildingInformationModel = buildingInformationModel;
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

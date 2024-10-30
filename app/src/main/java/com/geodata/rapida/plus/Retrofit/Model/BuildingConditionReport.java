package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BuildingConditionReport
{
    @SerializedName("AssessedValue")
    @Expose
    private String AssessedValue;

    @SerializedName("BIRZonalValue")
    @Expose
    private String BIRZonalValue;

    @SerializedName("BldgConditionID")
    @Expose
    private String BldgConditionID;

    @SerializedName("BldgConditionName")
    @Expose
    private String BldgConditionName;

    @SerializedName("BldgGroupID")
    @Expose
    private String BldgGroupID;

    @SerializedName("BldgGroupName")
    @Expose
    private String BldgGroupName;

    @SerializedName("BldgOwnershipTypeID")
    @Expose
    private String BldgOwnershipTypeID;

    @SerializedName("BldgOwnershipTypeName")
    @Expose
    private String BldgOwnershipTypeName;

    @SerializedName("BookValue")
    @Expose
    private String BookValue;

    @SerializedName("BuildingConditionReportID")
    @Expose
    private String BuildingConditionReportID;

    @SerializedName("BuildingInfoID")
    @Expose
    private String BuildingInfoID;

    @SerializedName("BuildingInventoryYearID")
    @Expose
    private String BuildingInventoryYearID;

    @SerializedName("DateSaved")
    @Expose
    private String DateSaved;

    @SerializedName("DateUpdated")
    @Expose
    private String DateUpdated;

    @SerializedName("InventoryYear")
    @Expose
    private String InventoryYear;

    @SerializedName("LotArea")
    @Expose
    private String LotArea;

    @SerializedName("LotOwnershipTypeID")
    @Expose
    private String LotOwnershipTypeID;

    @SerializedName("LotOwnershipTypeName")
    @Expose
    private String LotOwnershipTypeName;

    @SerializedName("Remarks")
    @Expose
    private String Remarks;

    @SerializedName("ValueOfRepair")
    @Expose
    private String ValueOfRepair;

    public BuildingConditionReport()
    {
        AssessedValue             = "";
        BIRZonalValue             = "";
        BldgConditionID           = "";
        BldgConditionName         = "";
        BldgGroupID               = "";
        BldgGroupName             = "";
        BldgOwnershipTypeID       = "";
        BldgOwnershipTypeName     = "";
        BookValue                 = "";
        BuildingConditionReportID = "";
        BuildingInfoID            = "";
        BuildingInventoryYearID   = "";
        DateSaved                 = "";
        DateUpdated               = "";
        InventoryYear             = "";
        LotArea                   = "";
        LotOwnershipTypeID        = "";
        LotOwnershipTypeName      = "";
        Remarks                   = "";
        ValueOfRepair             = "";
    }

    public String getAssessedValue() {
        return AssessedValue;
    }

    public void setAssessedValue(String assessedValue) {
        AssessedValue = assessedValue;
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

    public String getBldgConditionName() {
        return BldgConditionName;
    }

    public void setBldgConditionName(String bldgConditionName) {
        BldgConditionName = bldgConditionName;
    }

    public String getBldgGroupID() {
        return BldgGroupID;
    }

    public void setBldgGroupID(String bldgGroupID) {
        BldgGroupID = bldgGroupID;
    }

    public String getBldgGroupName() {
        return BldgGroupName;
    }

    public void setBldgGroupName(String bldgGroupName) {
        BldgGroupName = bldgGroupName;
    }

    public String getBldgOwnershipTypeID() {
        return BldgOwnershipTypeID;
    }

    public void setBldgOwnershipTypeID(String bldgOwnershipTypeID) {
        BldgOwnershipTypeID = bldgOwnershipTypeID;
    }

    public String getBldgOwnershipTypeName() {
        return BldgOwnershipTypeName;
    }

    public void setBldgOwnershipTypeName(String bldgOwnershipTypeName) {
        BldgOwnershipTypeName = bldgOwnershipTypeName;
    }

    public String getBookValue() {
        return BookValue;
    }

    public void setBookValue(String bookValue) {
        BookValue = bookValue;
    }

    public String getBuildingConditionReportID() {
        return BuildingConditionReportID;
    }

    public void setBuildingConditionReportID(String buildingConditionReportID) {
        BuildingConditionReportID = buildingConditionReportID;
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

    public String getInventoryYear() {
        return InventoryYear;
    }

    public void setInventoryYear(String inventoryYear) {
        InventoryYear = inventoryYear;
    }

    public String getLotArea() {
        return LotArea;
    }

    public void setLotArea(String lotArea) {
        LotArea = lotArea;
    }

    public String getLotOwnershipTypeID() {
        return LotOwnershipTypeID;
    }

    public void setLotOwnershipTypeID(String lotOwnershipTypeID) {
        LotOwnershipTypeID = lotOwnershipTypeID;
    }

    public String getLotOwnershipTypeName() {
        return LotOwnershipTypeName;
    }

    public void setLotOwnershipTypeName(String lotOwnershipTypeName) {
        LotOwnershipTypeName = lotOwnershipTypeName;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getValueOfRepair() {
        return ValueOfRepair;
    }

    public void setValueOfRepair(String valueOfRepair) {
        ValueOfRepair = valueOfRepair;
    }
}

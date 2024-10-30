package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BuildingInventoryYearDataModel
{
    @SerializedName("BuildingInventoryYearID")
    @Expose
    private String BuildingInventoryYearID;

    @SerializedName("BuildingInfoID")
    @Expose
    private String BuildingInfoID;

    @SerializedName("AssetID")
    @Expose
    private String AssetID;

    @SerializedName("BuildingName")
    @Expose
    private String BuildingName;

    @SerializedName("NoOfFloors")
    @Expose
    private String NoOfFloors;

    @SerializedName("Seismicity")
    @Expose
    private String Seismicity;

    @SerializedName("DateFinished")
    @Expose
    private String DateFinished;

    @SerializedName("Age")
    @Expose
    private String Age;

    @SerializedName("Location")
    @Expose
    private String Location;

    @SerializedName("BuildingConditionReport")
    @Expose
    private BuildingConditionReportModel BuildingConditionReport;

    @SerializedName("OwnerName")
    @Expose
    private String OwnerName;

    @SerializedName("ImageUrl")
    @Expose
    private String ImageUrl;

    public BuildingInventoryYearDataModel()
    {
        BuildingInventoryYearID = "";
        BuildingInfoID          = "";
        AssetID                 = "";
        BuildingName            = "";
        NoOfFloors              = "";
        Seismicity              = "";
        DateFinished            = "";
        Age                     = "";
        Location                = "";
        BuildingConditionReport = null;
        OwnerName               = "";
        ImageUrl                = "";
    }

    public String getBuildingInventoryYearID()
    {
        return BuildingInventoryYearID;
    }

    public void setBuildingInventoryYearID(String buildingInventoryYearID)
    {
        BuildingInventoryYearID = buildingInventoryYearID;
    }

    public String getBuildingInfoID()
    {
        return BuildingInfoID;
    }

    public void setBuildingInfoID(String buildingInfoID)
    {
        BuildingInfoID = buildingInfoID;
    }

    public String getAssetID()
    {
        return AssetID;
    }

    public void setAssetID(String assetID)
    {
        AssetID = assetID;
    }

    public String getBuildingName()
    {
        return BuildingName;
    }

    public void setBuildingName(String buildingName)
    {
        BuildingName = buildingName;
    }

    public String getNoOfFloors()
    {
        return NoOfFloors;
    }

    public void setNoOfFloors(String noOfFloors)
    {
        NoOfFloors = noOfFloors;
    }

    public String getSeismicity()
    {
        return Seismicity;
    }

    public void setSeismicity(String seismicity)
    {
        Seismicity = seismicity;
    }

    public String getDateFinished()
    {
        return DateFinished;
    }

    public void setDateFinished(String dateFinished)
    {
        DateFinished = dateFinished;
    }

    public String getAge()
    {
        return Age;
    }

    public void setAge(String age)
    {
        Age = age;
    }

    public String getLocation()
    {
        return Location;
    }

    public void setLocation(String location)
    {
        Location = location;
    }

    public BuildingConditionReportModel getBuildingConditionReport()
    {
        return BuildingConditionReport;
    }

    public void setBuildingConditionReport(BuildingConditionReportModel buildingConditionReport)
    {
        BuildingConditionReport = buildingConditionReport;
    }

    public String getOwnerName() {
        return OwnerName;
    }

    public void setOwnerName(String ownerName) {
        OwnerName = ownerName;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}

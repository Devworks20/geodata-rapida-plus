package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EarthquakeRVSReportModel
{
    private String ID;
    private String UserAccountID;

    @SerializedName("EarthquakeRVSReportID")
    @Expose
    private String EarthquakeRVSReportID;

    @SerializedName("BuildingName")
    @Expose
    private String BuildingName;

    @SerializedName("ConcreteFinalScore")
    @Expose
    private String ConcreteFinalScore;

    @SerializedName("EarthquakeRVSReportPdfPath")
    @Expose
    private String EarthquakeRVSReportPdfPath;

    @SerializedName("FaultDistance")
    @Expose
    private String FaultDistance;

    @SerializedName("FinalScore")
    @Expose
    private String FinalScore;

    @SerializedName("NearestFault")
    @Expose
    private String NearestFault;

    @SerializedName("ScreeningDate")
    @Expose
    private String ScreeningDate;

    @SerializedName("Seismicity")
    @Expose
    private String Seismicity;

    @SerializedName("SteelFinalScore")
    @Expose
    private String SteelFinalScore;

    public EarthquakeRVSReportModel()
    {
        ID                         = "";
        UserAccountID              = "";
        EarthquakeRVSReportID      = "";
        BuildingName               = "";
        ConcreteFinalScore         = "";
        EarthquakeRVSReportPdfPath = "";
        FaultDistance              = "";
        FinalScore                 = "";
        NearestFault               = "";
        ScreeningDate              = "";
        Seismicity                 = "";
        SteelFinalScore            = "";
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUserAccountID() {
        return UserAccountID;
    }

    public void setUserAccountID(String userAccountID) {
        UserAccountID = userAccountID;
    }

    public String getEarthquakeRVSReportID() {
        return EarthquakeRVSReportID;
    }

    public void setEarthquakeRVSReportID(String earthquakeRVSReportID) {
        EarthquakeRVSReportID = earthquakeRVSReportID;
    }

    public String getBuildingName() {
        return BuildingName;
    }

    public void setBuildingName(String buildingName) {
        BuildingName = buildingName;
    }

    public String getConcreteFinalScore() {
        return ConcreteFinalScore;
    }

    public void setConcreteFinalScore(String concreteFinalScore) {
        ConcreteFinalScore = concreteFinalScore;
    }

    public String getEarthquakeRVSReportPdfPath() {
        return EarthquakeRVSReportPdfPath;
    }

    public void setEarthquakeRVSReportPdfPath(String earthquakeRVSReportPdfPath) {
        EarthquakeRVSReportPdfPath = earthquakeRVSReportPdfPath;
    }

    public String getFaultDistance() {
        return FaultDistance;
    }

    public void setFaultDistance(String faultDistance) {
        FaultDistance = faultDistance;
    }

    public String getFinalScore() {
        return FinalScore;
    }

    public void setFinalScore(String finalScore) {
        FinalScore = finalScore;
    }

    public String getNearestFault() {
        return NearestFault;
    }

    public void setNearestFault(String nearestFault) {
        NearestFault = nearestFault;
    }

    public String getScreeningDate() {
        return ScreeningDate;
    }

    public void setScreeningDate(String screeningDate) {
        ScreeningDate = screeningDate;
    }

    public String getSeismicity() {
        return Seismicity;
    }

    public void setSeismicity(String seismicity) {
        Seismicity = seismicity;
    }

    public String getSteelFinalScore() {
        return SteelFinalScore;
    }

    public void setSteelFinalScore(String steelFinalScore) {
        SteelFinalScore = steelFinalScore;
    }
}

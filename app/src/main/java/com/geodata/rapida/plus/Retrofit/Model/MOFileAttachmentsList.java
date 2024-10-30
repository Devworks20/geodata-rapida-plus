package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MOFileAttachmentsList
{
    private String ID;
    private String ScreenerID;
    private String MissionOrderID;
    private String FileName;

    @SerializedName("MOAttachmentFilePath")
    @Expose
    private String MOAttachmentFilePath;

    @SerializedName("isPreviousReport")
    @Expose
    private Boolean isPreviousReport;

    public MOFileAttachmentsList()
    {
        ID                   = "";
        ScreenerID           = "";
        MissionOrderID       = "";
        MOAttachmentFilePath = "";
        isPreviousReport     = false;
        FileName             = "";
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

    public String getMissionOrderID() {
        return MissionOrderID;
    }

    public void setMissionOrderID(String missionOrderID) {
        MissionOrderID = missionOrderID;
    }

    public String getMOAttachmentFilePath() {
        return MOAttachmentFilePath;
    }

    public void setMOAttachmentFilePath(String MOAttachmentFilePath) {
        this.MOAttachmentFilePath = MOAttachmentFilePath;
    }

    public Boolean getPreviousReport()
    {
        return isPreviousReport;
    }

    public void setPreviousReport(Boolean previousReport)
    {
        isPreviousReport = previousReport;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }
}

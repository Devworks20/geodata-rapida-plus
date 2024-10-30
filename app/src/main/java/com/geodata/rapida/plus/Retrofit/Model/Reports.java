package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Reports
{
    @SerializedName("FilePath")
    @Expose
    private String FilePath;

    @SerializedName("FinalScoreList")
    @Expose
    private List<FinalScoreModel> FinalScoreList;

    @SerializedName("MissionOrderID")
    @Expose
    private String MissionOrderID;

    public Reports()
    {
        FilePath       = "";
        FinalScoreList = null;
        MissionOrderID = "";
    }

    public String getFilePath()
    {
        return FilePath;
    }

    public void setFilePath(String filePath)
    {
        FilePath = filePath;
    }

    public List<FinalScoreModel> getFinalScoreList()
    {
        return FinalScoreList;
    }

    public void setFinalScoreList(List<FinalScoreModel> finalScoreList)
    {
        FinalScoreList = finalScoreList;
    }

    public String getMissionOrderID()
    {
        return MissionOrderID;
    }

    public void setMissionOrderID(String missionOrderID)
    {
        MissionOrderID = missionOrderID;
    }
}

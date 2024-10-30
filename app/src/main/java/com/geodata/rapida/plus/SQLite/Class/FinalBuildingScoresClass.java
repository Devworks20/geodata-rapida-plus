package com.geodata.rapida.plus.SQLite.Class;

public class FinalBuildingScoresClass
{
    private String ID;
    private String Category;
    private String UserAccountID;
    private String MissionOrderID;
    private String BuildingScoreNo;
    private String BuildingType;
    private String FinalScore;
    private String isActive;

    public FinalBuildingScoresClass()
    {
       ID              = "";
       Category        = "";
       UserAccountID   = "";
       MissionOrderID  = "";
       BuildingScoreNo = "";
       BuildingType    = "";
       FinalScore      = "";
       isActive        = "0";
    }

    public String getID()
    {
        return ID;
    }

    public void setID(String ID)
    {
        this.ID = ID;
    }

    public String getCategory()
    {
        return Category;
    }

    public void setCategory(String category)
    {
        Category = category;
    }

    public String getUserAccountID()
    {
        return UserAccountID;
    }

    public void setUserAccountID(String userAccountID)
    {
        UserAccountID = userAccountID;
    }

    public String getMissionOrderID()
    {
        return MissionOrderID;
    }

    public void setMissionOrderID(String missionOrderID)
    {
        MissionOrderID = missionOrderID;
    }

    public String getBuildingScoreNo()
    {
        return BuildingScoreNo;
    }

    public void setBuildingScoreNo(String buildingScoreNo)
    {
        BuildingScoreNo = buildingScoreNo;
    }

    public String getBuildingType()
    {
        return BuildingType;
    }

    public void setBuildingType(String buildingType)
    {
        BuildingType = buildingType;
    }

    public String getFinalScore()
    {
        return FinalScore;
    }

    public void setFinalScore(String finalScore)
    {
        FinalScore = finalScore;
    }

    public String getIsActive()
    {
        return isActive;
    }

    public void setIsActive(String isActive)
    {
        this.isActive = isActive;
    }
}

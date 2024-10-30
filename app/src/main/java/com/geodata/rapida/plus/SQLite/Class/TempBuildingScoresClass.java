package com.geodata.rapida.plus.SQLite.Class;

public class TempBuildingScoresClass
{
    private String ID;
    private String UserAccountID;
    private String MissionOrderID;
    private String Category;
    private String BuildingID;
    private String BuildingType;
    private String Modifiers;
    private String Scores;
    private String isActive;

    public TempBuildingScoresClass()
    {
       ID             = "";
       UserAccountID  = "";
       MissionOrderID = "";
       Category       = "";
       BuildingID     = "";
       BuildingType   = "";
       Modifiers      = "";
       Scores         = "";
       isActive       = "0";
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

    public String getUserAccountID() {
        return UserAccountID;
    }

    public void setUserAccountID(String userAccountID) {
        UserAccountID = userAccountID;
    }

    public String getMissionOrderID() {
        return MissionOrderID;
    }

    public void setMissionOrderID(String missionOrderID) {
        MissionOrderID = missionOrderID;
    }

    public String getBuildingID() {
        return BuildingID;
    }

    public void setBuildingID(String buildingID) {
        BuildingID = buildingID;
    }

    public String getBuildingType() {
        return BuildingType;
    }

    public void setBuildingType(String buildingType) {
        BuildingType = buildingType;
    }

    public String getModifiers()
    {
        return Modifiers;
    }

    public void setModifiers(String modifiers)
    {
        Modifiers = modifiers;
    }

    public String getScores()
    {
        return Scores;
    }

    public void setScores(String scores)
    {
        Scores = scores;
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

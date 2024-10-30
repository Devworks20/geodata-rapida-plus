package com.geodata.rapida.plus.SQLite.Class;

public class SelectedFallingHazardsClass
{
    private String ID;
    private String UserAccountID;
    private String MissionOrderID;
    private String Category;
    private String FallingHazardDesc;
    private String OthersField;

    public SelectedFallingHazardsClass()
    {
        ID                = "";
        UserAccountID     = "";
        MissionOrderID    = "";
        Category          = "";
        FallingHazardDesc = "";
        OthersField       = "";
    }

    public String getID()
    {
        return ID;
    }

    public void setID(String ID)
    {
        this.ID = ID;
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

    public String getCategory()
    {
        return Category;
    }

    public void setCategory(String category)
    {
        Category = category;
    }

    public String getFallingHazardDesc()
    {
        return FallingHazardDesc;
    }

    public void setFallingHazardDesc(String fallingHazardDesc)
    {
        FallingHazardDesc = fallingHazardDesc;
    }

    public String getOthersField()
    {
        return OthersField;
    }

    public void setOthersField(String othersField)
    {
        OthersField = othersField;
    }
}

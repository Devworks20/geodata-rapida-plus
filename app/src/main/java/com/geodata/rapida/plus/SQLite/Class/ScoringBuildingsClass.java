package com.geodata.rapida.plus.SQLite.Class;

public class ScoringBuildingsClass
{
    private String Modifiers;
    private String isActive1;
    private String isActive2;

    private String BuildingID1;
    private String BuildingScore1;
    private String BuildingID2;
    private String BuildingScore2;

    public ScoringBuildingsClass()
    {
        Modifiers      = "";
        isActive1      = "0";
        isActive2      = "0";
        BuildingID1    = "";
        BuildingScore1 = "";
        BuildingID2    = "";
        BuildingScore2 = "";
    }

    public String getModifiers()
    {
        return Modifiers;
    }

    public void setModifiers(String modifiers)
    {
        Modifiers = modifiers;
    }

    public String getIsActive1()
    {
        return isActive1;
    }

    public void setIsActive1(String isActive1)
    {
        this.isActive1 = isActive1;
    }

    public String getIsActive2()
    {
        return isActive2;
    }

    public void setIsActive2(String isActive2)
    {
        this.isActive2 = isActive2;
    }

    public String getBuildingID1()
    {
        return BuildingID1;
    }

    public void setBuildingID1(String buildingID1)
    {
        BuildingID1 = buildingID1;
    }

    public String getBuildingScore1()
    {
        return BuildingScore1;
    }

    public void setBuildingScore1(String buildingScore1)
    {
        BuildingScore1 = buildingScore1;
    }

    public String getBuildingID2()
    {
        return BuildingID2;
    }

    public void setBuildingID2(String buildingID2)
    {
        BuildingID2 = buildingID2;
    }

    public String getBuildingScore2()
    {
        return BuildingScore2;
    }

    public void setBuildingScore2(String buildingScore2)
    {
        BuildingScore2 = buildingScore2;
    }
}

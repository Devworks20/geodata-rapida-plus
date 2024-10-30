package com.geodata.rapida.plus.SQLite.Class;

public class AddBuildingScoresClass
{
    private String ID;
    private String Category;
    private String BuildingType;
    private String Modifiers;
    private String Scores;
    private String isActive;

    public AddBuildingScoresClass()
    {
       ID           = "";
       Category     = "";
       BuildingType = "";
       Modifiers    = "";
       Scores       = "";
       isActive     = "0";
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

    public String getBuildingType()
    {
        return BuildingType;
    }

    public void setBuildingType(String buildingType)
    {
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

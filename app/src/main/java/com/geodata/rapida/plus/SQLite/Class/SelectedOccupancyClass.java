package com.geodata.rapida.plus.SQLite.Class;

public class SelectedOccupancyClass
{
    private String ID;
    private String UserAccountID;
    private String MissionOrderID;
    private String Category;
    private String UseOfCharacterOccupancyID;

    public SelectedOccupancyClass( )
    {
        ID                        = "";
        UserAccountID             = "";
        MissionOrderID            = "";
        Category                  = "";
        UseOfCharacterOccupancyID = "";
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

    public String getMissionOrderID() {
        return MissionOrderID;
    }

    public void setMissionOrderID(String missionOrderID) {
        MissionOrderID = missionOrderID;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getUseOfCharacterOccupancyID() {
        return UseOfCharacterOccupancyID;
    }

    public void setUseOfCharacterOccupancyID(String useOfCharacterOccupancyID) {
        UseOfCharacterOccupancyID = useOfCharacterOccupancyID;
    }
}

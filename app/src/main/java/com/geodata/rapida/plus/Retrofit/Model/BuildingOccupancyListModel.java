package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BuildingOccupancyListModel
{
    private String ID;
    private String ScreenerID;
    private String MissionOrderID;

    @SerializedName("Occupancy")
    @Expose
    private String Occupancy;

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

    public String getOccupancy() {
        return Occupancy;
    }

    public void setOccupancy(String occupancy) {
        Occupancy = occupancy;
    }
}

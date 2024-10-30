package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AssignedInspectorsListModel
{
    private String ID;
    private String ScreenerID;
    private String MissionOrderID;

    @SerializedName("Inspector")
    @Expose
    private String Inspector;

    @SerializedName("Position")
    @Expose
    private String Position;

    @SerializedName("isTL")
    @Expose
    private Boolean isTL;


    public AssignedInspectorsListModel()
    {
        ID                 = "";
        ScreenerID         = "";
        MissionOrderID     = "";
        Inspector          = "";
        Position           = "";
        isTL               = false;
    }

    public String getID()
    {
        return ID;
    }

    public void setID(String ID)
    {
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

    public String getInspector() {
        return Inspector;
    }

    public void setInspector(String inspector) {
        Inspector = inspector;
    }

    public String getPosition() {
        return Position;
    }

    public void setPosition(String position) {
        Position = position;
    }

    public Boolean getTL() {
        return isTL;
    }

    public void setTL(Boolean TL) {
        isTL = TL;
    }
}

package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BldgGroup
{
    @SerializedName("BldgGroupDesc")
    @Expose
    private String BldgGroupDesc;

    @SerializedName("BldgGroupID")
    @Expose
    private String BldgGroupID;

    public BldgGroup()
    {
        BldgGroupDesc = "";
        BldgGroupID   = "";
    }

    public String getBldgGroupDesc() {
        return BldgGroupDesc;
    }

    public void setBldgGroupDesc(String bldgGroupDesc) {
        BldgGroupDesc = bldgGroupDesc;
    }

    public String getBldgGroupID() {
        return BldgGroupID;
    }

    public void setBldgGroupID(String bldgGroupID) {
        BldgGroupID = bldgGroupID;
    }
}

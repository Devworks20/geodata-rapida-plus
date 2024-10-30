package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BuildingSoilType
{
    @SerializedName("BuildingSoilTypeCode")
    @Expose
    private String BuildingSoilTypeCode;

    @SerializedName("BuildingSoilTypeDesc")
    @Expose
    private String BuildingSoilTypeDesc;

    public BuildingSoilType()
    {
        BuildingSoilTypeCode = "";
        BuildingSoilTypeDesc =  "";
    }

    public String getBuildingSoilTypeCode() {
        return BuildingSoilTypeCode;
    }

    public void setBuildingSoilTypeCode(String buildingSoilTypeCode) {
        BuildingSoilTypeCode = buildingSoilTypeCode;
    }

    public String getBuildingSoilTypeDesc() {
        return BuildingSoilTypeDesc;
    }

    public void setBuildingSoilTypeDesc(String buildingSoilTypeDesc) {
        BuildingSoilTypeDesc = buildingSoilTypeDesc;
    }
}


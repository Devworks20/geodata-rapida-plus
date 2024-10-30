package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SoilTypesModel
{
    @SerializedName("BuildingSoilTypeID")
    @Expose
    private Integer BuildingSoilTypeID;

    @SerializedName("BuildingSoilTypeCode")
    @Expose
    private String BuildingSoilTypeCode;

    @SerializedName("BuildingSoilTypeDesc")
    @Expose
    private String BuildingSoilTypeDesc;

    public SoilTypesModel()
    {
        BuildingSoilTypeID   = 0;
        BuildingSoilTypeCode = "";
        BuildingSoilTypeDesc = "";
    }

    public Integer getBuildingSoilTypeID()
    {
        return BuildingSoilTypeID;
    }

    public void setBuildingSoilTypeID(Integer buildingSoilTypeID)
    {
        BuildingSoilTypeID = buildingSoilTypeID;
    }

    public String getBuildingSoilTypeCode()
    {
        return BuildingSoilTypeCode;
    }

    public void setBuildingSoilTypeCode(String buildingSoilTypeCode)
    {
        BuildingSoilTypeCode = buildingSoilTypeCode;
    }

    public String getBuildingSoilTypeDesc()
    {
        return BuildingSoilTypeDesc;
    }

    public void setBuildingSoilTypeDesc(String buildingSoilTypeDesc)
    {
        BuildingSoilTypeDesc = buildingSoilTypeDesc;
    }
}

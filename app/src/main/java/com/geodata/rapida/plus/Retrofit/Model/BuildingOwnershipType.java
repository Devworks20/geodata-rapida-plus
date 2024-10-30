package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BuildingOwnershipType
{
    @SerializedName("BuildingOwnershipTypeDesc")
    @Expose
    private String BuildingOwnershipTypeDesc;

    @SerializedName("BuildingOwnershipTypeID")
    @Expose
    private String BuildingOwnershipTypeID;

    public BuildingOwnershipType()
    {
        BuildingOwnershipTypeDesc = "";
        BuildingOwnershipTypeID   = "";
    }

    public String getBuildingOwnershipTypeDesc()
    {
        return BuildingOwnershipTypeDesc;
    }

    public void setBuildingOwnershipTypeDesc(String buildingOwnershipTypeDesc)
    {
        BuildingOwnershipTypeDesc = buildingOwnershipTypeDesc;
    }

    public String getBuildingOwnershipTypeID()
    {
        return BuildingOwnershipTypeID;
    }

    public void setBuildingOwnershipTypeID(String buildingOwnershipTypeID)
    {
        BuildingOwnershipTypeID = buildingOwnershipTypeID;
    }

}

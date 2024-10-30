package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BuildingTypeModel
{
    private String ID;

    @SerializedName("StructureTypeID")
    @Expose
    private String StructureTypeID;

    @SerializedName("Code")
    @Expose
    private String Code;

    @SerializedName("BuildingTypeName")
    @Expose
    private String BuildingTypeName;

    @SerializedName("PostBenchmark")
    @Expose
    private String PostBenchmark;

    @SerializedName("PreCode")
    @Expose
    private String PreCode;

    public BuildingTypeModel()
    {
        ID               = "";
        StructureTypeID  = "";
        Code             = "";
        BuildingTypeName = "";
        PostBenchmark    = "";
        PreCode          = "";
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getStructureTypeID() {
        return StructureTypeID;
    }

    public void setStructureTypeID(String structureTypeID) {
        StructureTypeID = structureTypeID;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getBuildingTypeName() {
        return BuildingTypeName;
    }

    public void setBuildingTypeName(String buildingTypeName) {
        BuildingTypeName = buildingTypeName;
    }

    public String getPostBenchmark() {
        return PostBenchmark;
    }

    public void setPostBenchmark(String postBenchmark) {
        PostBenchmark = postBenchmark;
    }

    public String getPreCode() {
        return PreCode;
    }

    public void setPreCode(String preCode) {
        PreCode = preCode;
    }
}

package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StructureType
{
    @SerializedName("StructureType")
    @Expose
    private String StructureType;

    @SerializedName("Code")
    @Expose
    private String Code;

    @SerializedName("Description")
    @Expose
    private String Description;

    @SerializedName("StructureTypeID")
    @Expose
    private String StructureTypeID;


    public StructureType()
    {
        StructureType   = "";
        Code            = "";
        Description     = "";
        StructureTypeID = "";
    }

    public String getStructureType() {
        return StructureType;
    }

    public void setStructureType(String structureType) {
        StructureType = structureType;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getStructureTypeID() {
        return StructureTypeID;
    }

    public void setStructureTypeID(String structureTypeID) {
        StructureTypeID = structureTypeID;
    }
}

package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LotOwnershipType
{
    @SerializedName("LotOwnershipTypeDesc")
    @Expose
    private String LotOwnershipTypeDesc;

    @SerializedName("LotOwnershipTypeID")
    @Expose
    private String LotOwnershipTypeID;

    public LotOwnershipType()
    {
        LotOwnershipTypeDesc = "";
        LotOwnershipTypeID   = "";
    }

    public String getLotOwnershipTypeDesc() {
        return LotOwnershipTypeDesc;
    }

    public void setLotOwnershipTypeDesc(String lotOwnershipTypeDesc) {
        LotOwnershipTypeDesc = lotOwnershipTypeDesc;
    }

    public String getLotOwnershipTypeID() {
        return LotOwnershipTypeID;
    }

    public void setLotOwnershipTypeID(String lotOwnershipTypeID) {
        LotOwnershipTypeID = lotOwnershipTypeID;
    }
}

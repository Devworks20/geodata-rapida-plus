package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FallingHazardsModel
{
    @SerializedName("FallingHazardID")
    @Expose
    private Integer FallingHazardID;

    @SerializedName("FallingHazardDesc")
    @Expose
    private String FallingHazardDesc;

    private String isActive;
    private String OthersField;

    public FallingHazardsModel()
    {
        FallingHazardID   = 0;
        FallingHazardDesc = "";
        isActive          = "0";
        OthersField       = "";
    }

    public Integer getFallingHazardID() {
        return FallingHazardID;
    }

    public void setFallingHazardID(Integer fallingHazardID) {
        FallingHazardID = fallingHazardID;
    }

    public String getFallingHazardDesc() {
        return FallingHazardDesc;
    }

    public void setFallingHazardDesc(String fallingHazardDesc) {
        FallingHazardDesc = fallingHazardDesc;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getOthersField() {
        return OthersField;
    }

    public void setOthersField(String othersField) {
        OthersField = othersField;
    }
}

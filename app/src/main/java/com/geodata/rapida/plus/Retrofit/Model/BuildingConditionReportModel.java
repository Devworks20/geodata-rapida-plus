package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BuildingConditionReportModel
{
    @SerializedName("InventoryYear")
    @Expose
    private String InventoryYear;

    @SerializedName("BldgConditionName")
    @Expose
    private String BldgConditionName;


    public BuildingConditionReportModel()
    {
        InventoryYear     = "";
        BldgConditionName = "";
    }

    public String getInventoryYear() {
        return InventoryYear;
    }

    public void setInventoryYear(String inventoryYear) {
        InventoryYear = inventoryYear;
    }

    public String getBldgConditionName() {
        return BldgConditionName;
    }

    public void setBldgConditionName(String bldgConditionName) {
        BldgConditionName = bldgConditionName;
    }
}

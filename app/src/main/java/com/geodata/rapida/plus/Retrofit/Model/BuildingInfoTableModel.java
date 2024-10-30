package com.geodata.rapida.plus.Retrofit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BuildingInfoTableModel
{
    @SerializedName("buildingInventoryYearList")
    @Expose
    private List<BuildingInventoryYearDataModel> BuildingInventoryYearDM;

    @SerializedName("pager")
    @Expose
    private PagerModel pager;

    public BuildingInfoTableModel()
    {
        BuildingInventoryYearDM = null;
        pager                   = null;
    }

    public List<BuildingInventoryYearDataModel> getBuildingInventoryYearDM()
    {
        return BuildingInventoryYearDM;
    }

    public void setBuildingInventoryYearDM(List<BuildingInventoryYearDataModel> buildingInventoryYearDM)
    {
        BuildingInventoryYearDM = buildingInventoryYearDM;
    }

    public PagerModel getPager()
    {
        return pager;
    }

    public void setPager(PagerModel pager)
    {
        this.pager = pager;
    }
}
